package ru.itmo.tuman.catalog.domain.model

/** Набор карточек целиком — то, что лежит в одном файле content/<id>.json. */
data class CardSet(
    val id: String,
    val language: String,
    val title: String,
    val description: String,
    val version: Int,
    val sources: List<String>,
    val cards: List<Card>,
)
