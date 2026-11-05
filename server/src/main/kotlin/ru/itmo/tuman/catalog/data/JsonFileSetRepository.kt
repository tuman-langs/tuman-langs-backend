package ru.itmo.tuman.catalog.data

import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import ru.itmo.tuman.catalog.domain.SetRepository
import ru.itmo.tuman.catalog.domain.model.Card
import ru.itmo.tuman.catalog.domain.model.CardSet
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText

/**
 * Читает наборы из JSON-файлов в папке [contentDir].
 *
 * Файлы читаются заново при каждом запросе: их немного и они маленькие,
 * зато исправленный набор появляется в каталоге без перезапуска сервера.
 */
class JsonFileSetRepository(private val contentDir: Path) : SetRepository {

    private val log = LoggerFactory.getLogger(JsonFileSetRepository::class.java)

    // Незнакомые поля не ломают чтение: так контент-лид может добавить поле
    // раньше, чем сервер научится его отдавать.
    private val json = Json { ignoreUnknownKeys = true }

    override fun getAll(): List<CardSet> {
        if (!contentDir.isDirectory()) {
            throw IllegalStateException("Папка с наборами не найдена: ${contentDir.toAbsolutePath()}")
        }
        return contentDir.listDirectoryEntries()
            .filter { it.extension == "json" }
            .sortedBy { it.fileName.toString() }
            .mapNotNull { readSet(it) }
    }

    /**
     * Ищем среди уже прочитанных наборов, а не открываем файл content/<id>.json напрямую:
     * id приходит из адреса запроса, и строка вроде "../secret" не должна
     * превращаться в путь к файлу за пределами папки с наборами.
     */
    override fun getById(id: String): CardSet? = getAll().find { it.id == id }

    /** Возвращает null, если файл не удалось прочитать: один кривой набор не роняет весь каталог. */
    private fun readSet(file: Path): CardSet? =
        try {
            json.decodeFromString<SetFile>(file.readText(Charsets.UTF_8)).toDomain()
        } catch (e: Exception) {
            log.warn("Набор {} пропущен: {}", file.fileName, e.message)
            null
        }
}

private fun SetFile.toDomain() = CardSet(
    id = id,
    language = language,
    title = title,
    description = description,
    version = version,
    sources = sources,
    cards = cards.map { it.toDomain() },
)

private fun CardFile.toDomain() = Card(
    id = id,
    front = front,
    back = back,
    pronunciation = pronunciation,
    notes = notes,
    example = example,
    exampleTranslation = exampleTranslation,
    tags = tags,
)
