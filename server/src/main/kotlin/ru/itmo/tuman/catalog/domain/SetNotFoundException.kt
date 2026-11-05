package ru.itmo.tuman.catalog.domain

/**
 * Набора с таким id нет. Бросает core, а слой api превращает её
 * в 404 и {"error": "set_not_found"} (docs/architecture.md, раздел 5).
 */
class SetNotFoundException(val id: String) : RuntimeException("Набор не найден: $id")
