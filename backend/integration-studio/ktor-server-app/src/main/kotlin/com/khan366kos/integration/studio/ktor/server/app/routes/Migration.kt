package com.khan366kos.integration.studio.ktor.server.app.routes

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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

fun Route.migration(service: PolynomApplicationService): Route = route("migration") {
    post {
        val sessionId = call.userSession.id
        val excelService = ExcelService()
        val treeBuilder = ClassifierTreeBuilder()
        val data = excelService.classifierGroups("C:\\Users\\han\\Desktop\\Структура Классификатора.xlsx")

        val tree = treeBuilder.build(data)

        val concepts = service.concepts(
            sessionId,
            listOf("ClassifData", "2328d6b0-9ece-4e87-9f63-09454f20211f") // todo вынести в настройку
        )

        val reference = service.referenceCreate(sessionId, tree.root().group.name)

        withContext(Dispatchers.IO) {
            val catalogs = tree.children.map { child ->
                async {
                    service.catalogService.create(
                        sessionId,
                        reference.typeId.asInt(),
                        reference.objectId.asInt(),
                        child.group.name
                    )
                }
            }.awaitAll()

            val groups = tree.children.first().children
            val firstCatalog = tree.children.first()

            catalogs.forEach { catalog ->
                concepts
                    .map { concept ->
                        async {
                            service.conceptService.addConceptToCatalog(
                                sessionId,
                                catalog.typeId.asInt(),
                                catalog.objectId.asInt(),
                                concept.typeId.asInt(),
                                concept.objectId.asInt()
                            )
                        }
                    }
                    .awaitAll()
            }
        }

        call.respond(HttpStatusCode.Created, reference.toBffDto())
    }
}