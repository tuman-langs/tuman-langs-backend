package ru.itmo.tuman.catalog.core

import ru.itmo.tuman.catalog.domain.SetRepository
import ru.itmo.tuman.catalog.domain.model.SetSummary

/** Сценарий «показать каталог»: список наборов без карточек, по алфавиту id. */
class GetSetsUseCase(private val repository: SetRepository) {

    operator fun invoke(): List<SetSummary> =
        repository.getAll()
            .map { set ->
                SetSummary(
                    id = set.id,
                    language = set.language,
                    title = set.title,
                    description = set.description,
                    version = set.version,
                    cardCount = set.cards.size,
                )
            }
            .sortedBy { it.id }
}
