package ru.itmo.tuman.catalog

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import ru.itmo.tuman.catalog.api.setsRoutes
import ru.itmo.tuman.catalog.core.GetSetsUseCase
import ru.itmo.tuman.catalog.data.JsonFileSetRepository
import kotlin.io.path.Path

// Порт и путь к наборам берутся из application.conf.
fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    install(CallLogging)

    // Собираем слои: data → core → api.
    val contentDir = Path(environment.config.property("catalog.contentDir").getString())
    val repository = JsonFileSetRepository(contentDir)
    val getSets = GetSetsUseCase(repository)

    routing {
        setsRoutes(getSets)
    }
}
