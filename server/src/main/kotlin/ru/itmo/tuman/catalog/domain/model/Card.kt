package ru.itmo.tuman.catalog.domain.model

/** Одна карточка набора. Необязательные поля — null, если их нет в файле. */
data class Card(
    val id: String,
    val front: String,
    val back: String,
    val pronunciation: String?,
    val notes: String?,
    val example: String?,
    val exampleTranslation: String?,
    val tags: List<String>,
)
