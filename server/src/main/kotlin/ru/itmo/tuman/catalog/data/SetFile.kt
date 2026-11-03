package ru.itmo.tuman.catalog.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Классы повторяют файл набора из content/README.md поле в поле.
// Они живут только в data: если формат файла изменится, правим здесь,
// а domain и ответ API не трогаем.

@Serializable
internal data class SetFile(
    val id: String,
    val language: String,
    val title: String,
    val description: String,
    val version: Int,
    val sources: List<String>,
    val cards: List<CardFile>,
)

@Serializable
internal data class CardFile(
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
