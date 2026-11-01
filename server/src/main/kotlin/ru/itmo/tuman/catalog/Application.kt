package ru.itmo.tuman.catalog

import io.ktor.http.ContentType
import io.ktor.http.withCharset
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import ru.itmo.tuman.catalog.api.configureErrorHandling
import ru.itmo.tuman.catalog.api.setsRoutes
import ru.itmo.tuman.catalog.core.GetSetsUseCase
import ru.itmo.tuman.catalog.data.JsonFileSetRepository
import kotlin.io.path.Path

// Порт и путь к наборам берутся из application.conf.
fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        // charset=UTF-8 в заголовке ответа: так любой клиент прочитает
        // башкирские буквы правильно (docs/architecture.md, раздел 5).
        json(contentType = ContentType.Application.Json.withCharset(Charsets.UTF_8))
    }
    install(CallLogging)
    configureErrorHandling()

    // Собираем слои: data → core → api.
    val contentDir = Path(environment.config.property("catalog.contentDir").getString())
    val repository = JsonFileSetRepository(contentDir)
    val getSets = GetSetsUseCase(repository)

    routing {
        setsRoutes(getSets)
    }
}
