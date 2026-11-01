package ru.itmo.tuman.catalog.data

import ru.itmo.tuman.catalog.testdata.ANOTHER_SET_JSON
import ru.itmo.tuman.catalog.testdata.TEST_SET_JSON
import ru.itmo.tuman.catalog.testdata.contentDirWith
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsonFileSetRepositoryTest {

    @Test
    fun `читает набор со всеми полями и башкирскими буквами`() {
        val repository = JsonFileSetRepository(contentDirWith("test-set.json" to TEST_SET_JSON))

        val set = repository.getAll().single()

        assertEquals("test-set", set.id)
        assertEquals("Тестовый набор: әөүғҡңҙҫһ", set.title)
        assertEquals(2, set.cards.size)
        assertEquals("Һаумы!", set.cards[0].front)
        assertNull(set.cards[0].example, "необязательного поля нет в файле — значит null")
        assertEquals("город", set.cards[1].exampleTranslation)
    }

    @Test
    fun `кривой файл пропускается, остальные наборы читаются`() {
        val dir = contentDirWith(
            "test-set.json" to TEST_SET_JSON,
            "broken.json" to "{ это не JSON",
            "no-cards.json" to """{"id": "no-cards", "language": "ba"}""",
        )

        val ids = JsonFileSetRepository(dir).getAll().map { it.id }

        assertEquals(listOf("test-set"), ids)
    }

    @Test
    fun `файлы не json не читаются`() {
        val dir = contentDirWith(
            "test-set.json" to TEST_SET_JSON,
            "another-set.json" to ANOTHER_SET_JSON,
            "README.md" to "# не набор",
        )

        assertEquals(2, JsonFileSetRepository(dir).getAll().size)
    }

    @Test
    fun `пустая папка — пустой список`() {
        assertTrue(JsonFileSetRepository(contentDirWith()).getAll().isEmpty())
    }

    @Test
    fun `нет папки — понятная ошибка`() {
        val repository = JsonFileSetRepository(Path("no-such-folder"))

        assertFailsWith<IllegalStateException> { repository.getAll() }
    }
}
