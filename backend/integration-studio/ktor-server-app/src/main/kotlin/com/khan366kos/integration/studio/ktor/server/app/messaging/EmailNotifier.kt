package com.khan366kos.integration.studio.ktor.server.app.messaging

import com.khan366kos.integration.studio.ktor.server.app.errors.SyncError
import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import java.util.Properties
import java.util.UUID

class EmailNotifier(private val config: EmailConfig) {
    private val log = LoggerFactory.getLogger(EmailNotifier::class.java)

    fun sendSyncError(runId: UUID, runType: String, error: SyncError) {
        if (!config.enabled) return
        try {
            val props = Properties().apply {
                put("mail.smtp.host", config.smtpHost)
                put("mail.smtp.port", config.smtpPort.toString())
                put("mail.smtp.auth", "true")
                if (config.smtpTls) {
                    // SSL/TLS (implicit) — порт 465
                    put("mail.smtp.ssl.enable", "true")
                } else {
                    // STARTTLS — порт 587
                    put("mail.smtp.starttls.enable", "true")
                }
            }
            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication() =
                    PasswordAuthentication(config.from, config.password)
            })
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(config.from))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.to))
                subject = "[Integration Studio] Ошибка синхронизации ($runType)"
                setText(buildBody(runId, runType, error), "UTF-8")
            }
            Transport.send(message)
            log.info("Error notification sent for run {} ({})", runId, runType)
        } catch (e: Exception) {
            log.error("Failed to send error email for run {}: {}", runId, e.message)
        }
    }

    private fun buildBody(runId: UUID, runType: String, error: SyncError): String = """
        Синхронизация завершилась с ошибкой.

        Run ID: $runId
        Тип синхронизации: $runType
        Категория ошибки: ${error::class.simpleName}
        Сообщение: ${error.message}
        Причина: ${error.cause?.message ?: "неизвестно"}

        Для диагностики проверьте логи сервера и запись в таблице migration_runs с данным Run ID.
    """.trimIndent()
}
