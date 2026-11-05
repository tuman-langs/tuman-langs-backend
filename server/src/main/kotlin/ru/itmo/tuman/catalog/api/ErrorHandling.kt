package ru.itmo.tuman.catalog.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import ru.itmo.tuman.catalog.api.dto.ErrorDto
import ru.itmo.tuman.catalog.domain.SetNotFoundException

/**
 * Ошибки → JSON с правильным кодом (docs/architecture.md, раздел 5).
 *
 * Набора нет → 404 и {"error": "set_not_found"}: это обычная ситуация, не сбой сервера.
 *
 * Любая неожиданная ошибка превращается в 500 и {"error": "internal_error"}.
 * Подробности (текст ошибки, пути к файлам) пишутся только в лог сервера,
 * наружу они не уходят.
 */
fun Application.configureErrorHandling() {
    install(StatusPages) {
        // StatusPages выбирает самый точный тип исключения, поэтому этот обработчик
        // сработает раньше общего Throwable ниже.
        exception<SetNotFoundException> { call, _ ->
            call.respond(HttpStatusCode.NotFound, ErrorDto("set_not_found"))
        }
        exception<Throwable> { call, cause ->
            call.application.log.error("Ошибка при обработке ${call.request.local.uri}", cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorDto("internal_error"))
        }
    }
}
