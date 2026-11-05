package ru.itmo.tuman.catalog.core

import ru.itmo.tuman.catalog.domain.SetNotFoundException
import ru.itmo.tuman.catalog.domain.SetRepository
import ru.itmo.tuman.catalog.domain.model.CardSet

/** Сценарий «скачать набор»: набор целиком со всеми карточками. */
class GetSetUseCase(private val repository: SetRepository) {

    /** @throws SetNotFoundException если набора с таким [id] нет. */
    operator fun invoke(id: String): CardSet =
        repository.getById(id) ?: throw SetNotFoundException(id)
}
