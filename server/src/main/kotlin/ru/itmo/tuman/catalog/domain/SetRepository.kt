package ru.itmo.tuman.catalog.domain

import ru.itmo.tuman.catalog.domain.model.CardSet

/**
 * Откуда берутся наборы. Слой core знает только этот интерфейс,
 * а то, что наборы лежат в JSON-файлах, знает лишь реализация в data.
 */
interface SetRepository {
    /** Все наборы, которые удалось прочитать. Кривые файлы пропускаются. */
    fun getAll(): List<CardSet>

    /** Набор с таким [id] или null, если его нет (или его файл не удалось прочитать). */
    fun getById(id: String): CardSet?
}
