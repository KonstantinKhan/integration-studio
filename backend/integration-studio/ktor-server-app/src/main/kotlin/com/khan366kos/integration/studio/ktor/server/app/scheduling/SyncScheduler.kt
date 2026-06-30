package com.khan366kos.integration.studio.ktor.server.app.scheduling

import com.khan366kos.domain.models.auth.UserCredentials
import com.khan366kos.domain.models.auth.simple.AccessToken
import com.khan366kos.domain.models.auth.simple.Login
import com.khan366kos.domain.models.auth.simple.RefreshToken
import com.khan366kos.domain.models.auth.simple.StorageId
import com.khan366kos.integration.studio.application.polynom.PolynomApplicationService
import com.khan366kos.integration.studio.bff.transport.models.IdentifierBffDto
import com.khan366kos.integration.studio.bff.transport.request.PolynomElementFromPeriodRequestBffDto
import com.khan366kos.integration.studio.ktor.server.app.db.MigrationRepository
import com.khan366kos.integration.studio.ktor.server.app.db.RunType
import com.khan366kos.integration.studio.ktor.server.app.session.SessionStore
import com.khan366kos.integration.studio.ktor.server.app.streaming.SyncStreamRegistry
import com.khan366kos.integration.studio.transport.polynom.models.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalDateTime
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.minutes

class SyncScheduler(
    private val scope: CoroutineScope,
    private val config: SyncSchedulerConfig,
    private val sessionStore: SessionStore,
    private val polynomApplicationService: PolynomApplicationService,
    private val registry: SyncStreamRegistry,
    private val repository: MigrationRepository,
) {
    private val log = LoggerFactory.getLogger(SyncScheduler::class.java)

    fun start() {
        if (!config.enabled) {
            log.info("Auto-sync scheduler disabled")
            return
        }
        log.info(
            "Auto-sync scheduler enabled: interval={}min, scope={}:{}, user={}",
            config.intervalMinutes, config.scopeTypeId, config.scopeObjectId, config.serviceUser
        )
        scope.launch {
            try {
                authenticate()
            } catch (e: Exception) {
                log.error("Auto-sync: service account authentication failed: {}", e.message, e)
                return@launch
            }
            while (isActive) {
                runSync()
                delay(config.intervalMinutes.minutes)
            }
        }
    }

    private suspend fun runSync() {
        try {
            val lastAuto = repository.getLastSuccessfulRun(RunType.AUTO)
            val from = lastAuto?.startedAt ?: OffsetDateTime.now().minusMinutes(config.intervalMinutes)
            val to = OffsetDateTime.now()

            log.info("Auto-sync: starting run, from={}, to={}", from, to)

            val apiOffset = ZoneOffset.ofTotalSeconds(config.externalApiTimezoneOffsetMinutes * 60)
            val request = PolynomElementFromPeriodRequestBffDto(
                scope = IdentifierBffDto(typeId = config.scopeTypeId, objectId = config.scopeObjectId),
                from = from.withOffsetSameInstant(apiOffset).toLocalDateTime().toKotlinLocalDateTime(),
                to = to.withOffsetSameInstant(apiOffset).toLocalDateTime().toKotlinLocalDateTime(),
                timezoneOffsetMinutes = config.externalApiTimezoneOffsetMinutes,
            )
            registry.start(
                sessionId = SERVICE_SESSION_ID,
                service = polynomApplicationService,
                request = request,
                type = RunType.AUTO,
                initiatedBy = config.serviceUser,
            )
        } catch (e: Exception) {
            log.error("Auto-sync: failed to start run: {}", e.message, e)
        }
    }

    private suspend fun authenticate() {
        val response = polynomApplicationService.signIn(
            LoginRequest(
                storageId = config.serviceStorageId,
                password = config.servicePassword,
                login = config.serviceUser,
            )
        )
        val now = System.currentTimeMillis()
        val credentials = UserCredentials(
            login = Login(config.serviceUser),
            storageId = StorageId(config.serviceStorageId),
            accessToken = AccessToken(response.accessToken ?: ""),
            refreshToken = RefreshToken(response.refreshToken ?: ""),
            issuedAt = now,
            expiresAt = now + (response.expiresIn * 1000L),
        )
        sessionStore.store(SERVICE_SESSION_ID, credentials)
        log.info("Auto-sync: service account authenticated as '{}'", config.serviceUser)
    }

    companion object {
        const val SERVICE_SESSION_ID = "auto-sync-service"
    }
}
