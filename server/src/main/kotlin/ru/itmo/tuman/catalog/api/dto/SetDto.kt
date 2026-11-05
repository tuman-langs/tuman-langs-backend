package ru.itmo.tuman.catalog.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.itmo.tuman.catalog.domain.model.Card
import ru.itmo.tuman.catalog.domain.model.CardSet

/** Ответ GET /sets/{id} — набор целиком, поля как в content/README.md и docs/api.md. */
@Serializable
data class SetDto(
    val id: String,
    val language: String,
    val title: String,
    val description: String,
    val version: Int,
    val sources: List<String>,
    val cards: List<CardDto>,
)

/**
 * Одна карточка. Необязательные поля равны null, если их нет в наборе,
 * и тогда не попадают в JSON (explicitNulls = false в Application.kt) —
 * так же, как в файле набора поле «просто не пишется».
 */
@Serializable
data class CardDto(
    val id: String,
    val front: String,
    val back: String,
    val pronunciation: String? = null,
    val notes: String? = null,
    val example: String? = null,
    @SerialName("example_translation")
    val exampleTranslation: String? = null,
    val tags: List<String>,
)

fun CardSet.toDto() = SetDto(
    id = id,
    language = language,
    title = title,
    description = description,
    version = version,
    sources = sources,
    cards = cards.map { it.toDto() },
)

fun Card.toDto() = CardDto(
    id = id,
    front = front,
    back = back,
    pronunciation = pronunciation,
    notes = notes,
    example = example,
    exampleTranslation = exampleTranslation,
    tags = tags,
)
