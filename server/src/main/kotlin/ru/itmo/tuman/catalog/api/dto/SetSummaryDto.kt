package ru.itmo.tuman.catalog.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.itmo.tuman.catalog.domain.model.SetSummary

/** Элемент ответа GET /sets — поля как в docs/api.md. */
@Serializable
data class SetSummaryDto(
    val id: String,
    val language: String,
    val title: String,
    val description: String,
    val version: Int,
    @SerialName("card_count")
    val cardCount: Int,
)

fun SetSummary.toDto() = SetSummaryDto(
    id = id,
    language = language,
    title = title,
    description = description,
    version = version,
    cardCount = cardCount,
)
