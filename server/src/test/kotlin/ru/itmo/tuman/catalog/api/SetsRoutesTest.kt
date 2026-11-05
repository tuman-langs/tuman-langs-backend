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
import kotlin.test.assertTrue

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

    @Test
    fun `GET sets id отдаёт набор целиком в UTF-8`() {
        val dir = contentDirWith("test-set.json" to TEST_SET_JSON)

        testServer(dir.toString()) {
            val response = client.get("/sets/test-set")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("application/json; charset=UTF-8", response.headers[HttpHeaders.ContentType])

            val set = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertEquals("test-set", set["id"]!!.jsonPrimitive.content)
            assertEquals("Тестовый набор: әөүғҡңҙҫһ", set["title"]!!.jsonPrimitive.content)
            assertEquals("тест", set["sources"]!!.jsonArray.single().jsonPrimitive.content)

            val cards = set["cards"]!!.jsonArray.map { it.jsonObject }
            assertEquals(listOf("test-set-0001", "test-set-0002"), cards.map { it["id"]!!.jsonPrimitive.content })
            assertEquals("Һаумы!", cards[0]["front"]!!.jsonPrimitive.content)
            assertEquals("ҡала", cards[1]["example"]!!.jsonPrimitive.content)
            assertEquals("город", cards[1]["example_translation"]!!.jsonPrimitive.content)
        }
    }

    @Test
    fun `необязательных полей, которых нет в наборе, нет и в ответе`() {
        val dir = contentDirWith("test-set.json" to TEST_SET_JSON)

        testServer(dir.toString()) {
            val card = Json.parseToJsonElement(client.get("/sets/test-set").bodyAsText())
                .jsonObject["cards"]!!.jsonArray[0].jsonObject

            for (field in listOf("pronunciation", "notes", "example", "example_translation")) {
                assertFalse(field in card, "поле $field не должно приходить как null")
            }
            assertTrue("tags" in card)
        }
    }

    @Test
    fun `нет такого набора — 404 и set_not_found`() {
        val dir = contentDirWith("test-set.json" to TEST_SET_JSON)

        testServer(dir.toString()) {
            val response = client.get("/sets/no-such-set")

            assertEquals(HttpStatusCode.NotFound, response.status)
            assertEquals("application/json; charset=UTF-8", response.headers[HttpHeaders.ContentType])
            assertEquals("""{"error":"set_not_found"}""", response.bodyAsText())
        }
    }

    @Test
    fun `GET sets id без папки с наборами — 500 без подробностей`() {
        testServer("no-such-folder") {
            val response = client.get("/sets/test-set")

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals("""{"error":"internal_error"}""", response.bodyAsText())
        }
    }
}
