package ru.itmo.tuman.catalog.testdata

import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText

// Тестовые наборы. Переводы условные: это проверка сервера, а не языковой контент.

/** Набор с башкирскими буквами из всех девяти «особых»: ә ө ү ғ ҡ ң ҙ ҫ һ. */
val TEST_SET_JSON = """
{
  "id": "test-set",
  "language": "ba",
  "title": "Тестовый набор: әөүғҡңҙҫһ",
  "description": "Только для тестов сервера",
  "version": 2,
  "sources": ["тест"],
  "cards": [
    {"id": "test-set-0001", "front": "Һаумы!", "back": "Здравствуй!", "tags": ["тест"]},
    {"id": "test-set-0002", "front": "Ҡ ҡ", "back": "буква", "example": "ҡала",
     "example_translation": "город", "tags": ["тест"]}
  ]
}
""".trimIndent()

/** Второй набор: проверяем, что список отсортирован по id. */
val ANOTHER_SET_JSON = """
{
  "id": "another-set", "language": "ba", "title": "Другой", "description": "Тест",
  "version": 1, "sources": [], "cards": []
}
""".trimIndent()

/** Создаёт временную папку с файлами: имя файла → содержимое. */
fun contentDirWith(vararg files: Pair<String, String>): Path {
    val dir = createTempDirectory("content-test")
    files.forEach { (name, text) -> dir.resolve(name).writeText(text, Charsets.UTF_8) }
    return dir
}
