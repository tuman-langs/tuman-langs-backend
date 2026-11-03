package ru.itmo.tuman.catalog.api

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import ru.itmo.tuman.catalog.api.dto.toDto
import ru.itmo.tuman.catalog.core.GetSetsUseCase

fun Route.setsRoutes(getSets: GetSetsUseCase) {
    get("/sets") {
        call.respond(getSets().map { it.toDto() })
    }
}
