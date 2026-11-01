package ru.itmo.tuman.catalog.api.dto

import kotlinx.serialization.Serializable

/** Тело любой ошибки: {"error": "<код>"} (docs/architecture.md, раздел 5). */
@Serializable
data class ErrorDto(val error: String)
