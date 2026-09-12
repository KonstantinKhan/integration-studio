package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.domain.polynom.models.ClassifierTreeNode
import com.khan366kos.etl.excel.service.ExcelService
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.logics.ClassifierTreeBuilder
import com.khan366kos.integration.studio.logics.PolynomApplicationService
import com.khan366kos.integration.studio.mapping.toBffDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

private const val MAX_CONCURRENT_CONCEPT_ADDS = 16
private const val MAX_CONCURRENT_GROUP_CREATES = 32

private suspend fun createGroupBranch(
    service: PolynomApplicationService,
    sessionId: String,
    groupLimit: Semaphore,
    parentTypeId: Int,
    parentObjectId: Int,
    groupNode: ClassifierTreeNode.Group,
) {
    val created = groupLimit.withPermit {
        service.groupService.create(sessionId, parentTypeId, parentObjectId, groupNode.name.asString())
    }
    if (groupNode.groups.isEmpty()) return
    coroutineScope {
        groupNode.groups.map { child ->
            async {
                createGroupBranch(
                    service, sessionId, groupLimit,
                    created.typeId.asInt(), created.objectId.asInt(), child
                )
            }
        }.awaitAll()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun Route.migration(service: PolynomApplicationService, path: String): Route = route("migration") {
    post {
        val sessionId = call.userSession.id
        val excelService = ExcelService()
        val treeBuilder = ClassifierTreeBuilder()
        val data = excelService.classifierGroups(path)

        val tree = treeBuilder.build(data)

        val concepts = service.concepts(
            sessionId,
            listOf("ClassifData", "2328d6b0-9ece-4e87-9f63-09454f20211f") // todo вынести в настройку
        )

        withContext(Dispatchers.IO) {
            when (tree) {
                is ClassifierTreeNode.Reference -> {
                    val reference = service.referenceService.referenceCreate(
                        sessionId,
                        tree.name.asString()
                    )

                    // Phase 1: catalogs (parallel)
                    val responseCatalogs = tree.catalogs.map { catalog ->
                        async {
                            service.catalogService.create(
                                sessionId,
                                reference.typeId.asInt(),
                                reference.objectId.asInt(),
                                catalog.name.asString()
                            )
                        }
                    }.awaitAll()

                    // Phase 2: concept-adds across all catalogs (isolated from groups, cap 4)
                    responseCatalogs
                        .flatMap { rc -> concepts.map { rc to it } }
                        .asFlow()
                        .flatMapMerge(concurrency = MAX_CONCURRENT_CONCEPT_ADDS) { (rc, concept) ->
                            flow {
                                service.conceptService.addConceptToCatalog(
                                    sessionId,
                                    rc.typeId.asInt(),
                                    rc.objectId.asInt(),
                                    concept.typeId.asInt(),
                                    concept.objectId.asInt()
                                )
                                emit(Unit)
                            }
                        }.toList()

                    // Phase 3: groups (recursive — groups may contain nested groups, cap 3)
                    val groupLimit = Semaphore(MAX_CONCURRENT_GROUP_CREATES)
                    coroutineScope {
                        responseCatalogs
                            .zip(tree.catalogs.map { it.groups })
                            .flatMap { (rc, groups) -> groups.map { rc to it } }
                            .map { (rc, group) ->
                                async {
                                    createGroupBranch(
                                        service, sessionId, groupLimit,
                                        rc.typeId.asInt(), rc.objectId.asInt(), group
                                    )
                                }
                            }.awaitAll()
                    }
                }

                else -> return@withContext
            }

//            val catalogs = tree.children.map { child ->
//                async {
//                    service.catalogService.create(
//                        sessionId,
//                        reference.typeId.asInt(),
//                        reference.objectId.asInt(),
//                        child.group.name
//                    )
//                }
//            }.awaitAll()

//            catalogs.forEach { catalog ->
//                concepts
//                    .map { concept ->
//                        async {
//                            service.conceptService.addConceptToCatalog(
//                                sessionId,
//                                catalog.typeId.asInt(),
//                                catalog.objectId.asInt(),
//                                concept.typeId.asInt(),
//                                concept.objectId.asInt()
//                            )
//                        }
//                    }
//                    .awaitAll()
//            }
        }

        call.respond(HttpStatusCode.Created, (tree as ClassifierTreeNode.Reference).toBffDto())
    }
}