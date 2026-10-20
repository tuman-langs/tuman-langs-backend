# Архитектура сервера каталога

Этот документ отвечает на вопрос «куда положить файл». Его читают участники и Claude Code перед каждой задачей. За основу взят CalorAI Backend: слои `api` → `core` → `domain` ← `data` и запуск в отдельной точке входа. Там каждый слой — отдельный Gradle-модуль на Spring Boot. Нашему серверу нужно всего два запроса, поэтому слои сделаны пакетами внутри одного модуля, а вместо Spring взят более лёгкий Ktor.

Если нужного места нет или правило мешает, не изобретайте своё. Напишите комментарий в задаче, руководитель решит и поправит этот документ.

## 1. Что делает сервер

Приложение спрашивает список наборов и скачивает выбранный набор. Наборы — JSON-файлы в папке `content/`, база данных не нужна: при запуске сервер читает файлы и отдаёт их по API (`docs/api.md`).

```
Android-приложение ──GET /sets──────▶ сервер ──читает──▶ content/*.json
                   ◀──список наборов──
                   ──GET /sets/{id}──▶
                   ◀──набор целиком───
```

## 2. Структура репозитория

```
tuman-langs-backend/
├── content/                 наборы карточек (JSON) и правила их оформления   — Богданов Е.
│   ├── README.md            формат набора
│   ├── SOURCES.md           словари и учебники для сверки
│   └── ba-basic-words.json  один файл = один набор
├── server/                  код сервера (модуль Gradle)                       — Василенко А.
├── docs/
│   ├── api.md               описание запросов и ответов
│   └── architecture.md      этот файл
├── settings.gradle.kts      include(":server")
├── build.gradle.kts
├── gradle/libs.versions.toml  версии библиотек, в одном месте
├── .claude/                 общие настройки и команды Claude Code
└── .github/                 шаблоны задач и PR
```

## 3. Пакеты внутри `server`

Стек — **Kotlin + Ktor** на JVM 17: язык тот же, что в приложении, сборка через Gradle, открывается в Android Studio или IntelliJ IDEA. Если backend-разработчик хочет другой стек, это решается с руководителем в задаче про выбор стека. Слои и правила ниже остаются теми же.

Корневой пакет — `ru.itmo.tuman.catalog`.

```
server/src/main/kotlin/ru/itmo/tuman/catalog/
├── Application.kt           точка входа: порт, плагины Ktor, подключение маршрутов    ≈ bootstrap в CalorAI
│
├── api/                     HTTP: что приходит и что уходит                             ≈ api
│   ├── SetsRoutes.kt        GET /sets, GET /sets/{id}
│   ├── ErrorHandling.kt     ошибки → JSON {"error": "..."} с правильным кодом
│   └── dto/                 SetSummaryDto, SetDto, CardDto, ErrorDto
│
├── core/                    сценарии: что сервер умеет                                  ≈ core/useCase
│   ├── GetSetsUseCase.kt
│   └── GetSetUseCase.kt
│
├── domain/                  модели и интерфейсы, без Ktor и файлов                      ≈ domain
│   ├── model/               CardSet, Card, SetSummary
│   ├── SetRepository.kt     интерфейс: «дай все наборы», «дай набор по id»
│   └── SetNotFoundException.kt
│
└── data/                    откуда берутся данные                                       ≈ backend/jpa
    └── JsonFileSetRepository.kt   читает content/*.json, реализует SetRepository

server/src/main/resources/
└── application.conf         порт и путь к папке content (можно переопределить переменной CONTENT_DIR)

server/src/test/kotlin/ru/itmo/tuman/catalog/
├── api/SetsRoutesTest.kt         запросы к серверу через testApplication
├── data/JsonFileSetRepositoryTest.kt
└── content/ContentValidationTest.kt  все файлы в content/ соответствуют формату
```

**Поток данных:** `SetsRoutes` принимает запрос → вызывает `GetSetUseCase` → тот просит `SetRepository` → `JsonFileSetRepository` читает файл → модель `domain` превращается в `Dto` и уходит в ответ.

```
GET /sets/{id} ──▶ SetsRoutes ──▶ GetSetUseCase ──▶ SetRepository ◀── JsonFileSetRepository ──▶ content/{id}.json
       ▲                │
       └── SetDto (JSON) ◀┘
```

## 4. Слои простыми словами

| Слой | Что можно | Чего нельзя |
|---|---|---|
| `api` | маршруты Ktor, DTO, коды ответа, превращение ошибок в JSON | читать файлы, считать что-то сложное |
| `core` | сценарии: собрать список, найти набор, посчитать `card_count` | знать про HTTP и Ktor |
| `domain` | data class моделей, интерфейс репозитория, свои исключения | импортировать `io.ktor.*`, `java.io.File` |
| `data` | чтение и разбор JSON-файлов, кэш в памяти | знать про HTTP |

Правила зависимостей:
1. `api` → `core` → `domain`. `data` зависит только от `domain`.
2. DTO живут только в `api`, наружу из сервера уходят только они. Если поле в ответе меняется, правится `docs/api.md` и сообщается Android-разработчикам.
3. Сервер не падает на кривом файле в `content/`: такой набор пропускается, причина пишется в лог, тест `ContentValidationTest` ловит это ещё до PR.

## 5. Ответы и ошибки

| Ситуация | Код | Тело |
|---|---|---|
| Всё хорошо | `200` | JSON по `docs/api.md`, кодировка UTF-8 |
| Набора с таким id нет | `404` | `{"error": "set_not_found"}` |
| Неожиданная ошибка | `500` | `{"error": "internal_error"}` без деталей и путей к файлам |

Башкирские буквы (ә, ө, ү, ғ, ҡ, ң, ҙ, ҫ, һ) в ответе должны прийти без искажений: заголовок `Content-Type: application/json; charset=UTF-8`, файлы читаются как UTF-8.

## 6. Библиотеки

| Что | Библиотека |
|---|---|
| Сервер | Ktor Server (Netty), плагины ContentNegotiation, StatusPages, CallLogging |
| JSON | Kotlinx Serialization |
| Тесты | Ktor `testApplication`, JUnit 5 (`kotlin-test`) |

Версии — только в `gradle/libs.versions.toml`.

## 7. Именование

| Что | Как | Пример |
|---|---|---|
| Маршруты | `<Ресурс>Routes.kt`, функция `Route.<ресурс>Routes()` | `SetsRoutes.kt` |
| Сценарий | `<Глагол><Что>UseCase` | `GetSetUseCase` |
| Модель | существительное | `CardSet`, `Card` |
| Ответ | `<Что>Dto` | `SetSummaryDto` |
| Файл набора | `<язык>-<тема>.json`, имя = `id` | `ba-basic-words.json` |

Код и имена — на английском, комментарии, коммиты и описания — на русском.

## 8. Запуск и проверка

| Что | Команда |
|---|---|
| Запустить сервер | `./gradlew :server:run` → http://localhost:8080/sets |
| Все тесты (код и наборы) | `./gradlew test` |

На Windows: `.\gradlew.bat` вместо `./gradlew`. На GitHub автоматические проверки отключены, поэтому тесты каждый запускает у себя перед PR (`/finish-task` делает это сам).

Где разместить сервер на время проверки и защиты, решает backend-разработчик с руководителем. Это отдельная задача.
