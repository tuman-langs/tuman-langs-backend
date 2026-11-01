package ru.itmo.tuman.catalog.domain.model

/** Краткие сведения о наборе для списка: без карточек, только их число. */
data class SetSummary(
    val id: String,
    val language: String,
    val title: String,
    val description: String,
    val version: Int,
    val cardCount: Int,
)
