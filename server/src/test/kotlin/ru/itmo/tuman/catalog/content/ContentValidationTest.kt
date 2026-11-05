package ru.itmo.tuman.catalog.content

import ru.itmo.tuman.catalog.data.JsonFileSetRepository
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Проверяет настоящие наборы из content/, а не тестовые (формат — content/README.md).
 *
 * Сервер молча пропускает кривой файл, чтобы не упал весь каталог, — значит,
 * ошибку в наборе ловим здесь: PR с кривым набором не пройдёт `./gradlew test`.
 * Тесты запускаются из папки server/, поэтому путь — ../content.
 */
class ContentValidationTest {

    private val contentDir = Path("../content")

    private val fileIds = contentDir.listDirectoryEntries()
        .filter { it.extension == "json" }
        .map { it.nameWithoutExtension }
        .sorted()

    private val sets = JsonFileSetRepository(contentDir).getAll()

    @Test
    fun `каждый файл читается и id набора совпадает с именем файла`() {
        // Репозиторий читает файлы в порядке имён, поэтому списки должны совпасть целиком.
        // Не прочитался файл или id не тот — в сообщении будет видно, какой именно.
        assertEquals(fileIds, sets.map { it.id }, "файлы content/*.json и id прочитанных наборов")
    }

    @Test
    fun `id карточек внутри набора уникальны`() {
        for (set in sets) {
            val repeated = set.cards.groupBy { it.id }.filterValues { it.size > 1 }.keys
            assertTrue(repeated.isEmpty(), "в наборе ${set.id} повторяются id карточек: $repeated")
        }
    }

    @Test
    fun `у карточек не пустые front и back`() {
        for (set in sets) {
            val empty = set.cards.filter { it.front.isBlank() || it.back.isBlank() }.map { it.id }
            assertTrue(empty.isEmpty(), "в наборе ${set.id} пустые front или back у карточек: $empty")
        }
    }

    @Test
    fun `id набора уникален во всём каталоге`() {
        // Один и тот же id в двух файлах: GET /sets/{id} отдал бы только первый.
        val repeated = sets.groupBy { it.id }.filterValues { it.size > 1 }.keys
        assertTrue(repeated.isEmpty(), "повторяются id наборов: $repeated")
    }

    @Test
    fun `example_translation указан только вместе с example`() {
        for (set in sets) {
            val alone = set.cards.filter { it.exampleTranslation != null && it.example == null }.map { it.id }
            assertTrue(alone.isEmpty(), "в наборе ${set.id} есть example_translation без example: $alone")
        }
    }
}
