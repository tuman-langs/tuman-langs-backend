package ru.itmo.tuman.catalog.api

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import ru.itmo.tuman.catalog.api.dto.toDto
import ru.itmo.tuman.catalog.core.GetSetUseCase
import ru.itmo.tuman.catalog.core.GetSetsUseCase

fun Route.setsRoutes(getSets: GetSetsUseCase, getSet: GetSetUseCase) {
    get("/sets") {
        call.respond(getSets().map { it.toDto() })
    }

    // Если набора нет, GetSetUseCase бросит SetNotFoundException,
    // а ErrorHandling.kt превратит её в 404 и {"error": "set_not_found"}.
    get("/sets/{id}") {
        val id = call.parameters["id"]!! // в маршруте "/sets/{id}" параметр есть всегда
        call.respond(getSet(id).toDto())
    }
}
