package ru.itmo.tuman.catalog.api

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ru.itmo.tuman.catalog.module
import ru.itmo.tuman.catalog.testdata.ANOTHER_SET_JSON
import ru.itmo.tuman.catalog.testdata.TEST_SET_JSON
import ru.itmo.tuman.catalog.testdata.contentDirWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SetsRoutesTest {

    /** Запускает сервер в памяти с папкой наборов [contentDir]. */
    private fun testServer(contentDir: String, block: suspend ApplicationTestBuilder.() -> Unit) =
        testApplication {
            environment {
                config = MapApplicationConfig("catalog.contentDir" to contentDir)
            }
            application { module() }
            block()
        }

    @Test
    fun `GET sets отдаёт список наборов в UTF-8`() {
        val dir = contentDirWith("test-set.json" to TEST_SET_JSON)

        testServer(dir.toString()) {
            val response = client.get("/sets")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("application/json; charset=UTF-8", response.headers[HttpHeaders.ContentType])

            val set = Json.parseToJsonElement(response.bodyAsText()).jsonArray.single().jsonObject
            assertEquals("test-set", set["id"]!!.jsonPrimitive.content)
            assertEquals("Тестовый набор: әөүғҡңҙҫһ", set["title"]!!.jsonPrimitive.content)
            assertEquals(2, set["version"]!!.jsonPrimitive.int)
            assertEquals(2, set["card_count"]!!.jsonPrimitive.int)
            assertFalse("cards" in set, "в списке наборов карточки не отдаются")
        }
    }

    @Test
    fun `приложение с Accept application json тоже получает ответ`() {
        val dir = contentDirWith("test-set.json" to TEST_SET_JSON)

        testServer(dir.toString()) {
            val response = client.get("/sets") { header(HttpHeaders.Accept, "application/json") }

            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun `наборы отсортированы по id`() {
        val dir = contentDirWith("test-set.json" to TEST_SET_JSON, "another-set.json" to ANOTHER_SET_JSON)

        testServer(dir.toString()) {
            val ids = Json.parseToJsonElement(client.get("/sets").bodyAsText())
                .jsonArray.map { it.jsonObject["id"]!!.jsonPrimitive.content }

            assertEquals(listOf("another-set", "test-set"), ids)
        }
    }

    @Test
    fun `кривой файл не роняет каталог`() {
        val dir = contentDirWith("test-set.json" to TEST_SET_JSON, "broken.json" to "{ сломано")

        testServer(dir.toString()) {
            val response = client.get("/sets")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(1, Json.parseToJsonElement(response.bodyAsText()).jsonArray.size)
        }
    }

    @Test
    fun `нет папки с наборами — 500 и ошибка в JSON без подробностей`() {
        testServer("no-such-folder") {
            val response = client.get("/sets")

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals("""{"error":"internal_error"}""", response.bodyAsText())
        }
    }
}
