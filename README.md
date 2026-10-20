<div align="center">

# 🌫️ Туман — сервер каталога

**Каталог готовых наборов карточек по башкирскому языку для Android-приложения [«Туман — Языки России»](https://github.com/tuman-langs/tuman-langs-mobile).**

[![Kotlin](https://img.shields.io/badge/Kotlin-JVM%2017-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Ktor](https://img.shields.io/badge/Ktor-REST%20API-087CFA?logo=ktor&logoColor=white)](https://ktor.io/)
[![JSON](https://img.shields.io/badge/наборы-JSON%20UTF--8-000000?logo=json&logoColor=white)](content/README.md)
[![Наборы](https://img.shields.io/badge/цель-3%20набора%20·%20100%2B%20карточек-F57C00)](content/README.md)
[![ITMO](https://img.shields.io/badge/ИТМО-курсовой%20проект%202026-1946BA)](https://github.com/tuman-langs/tuman-langs-mobile/blob/main/docs/tz.md)

[📋 Доска задач](https://github.com/orgs/tuman-langs/projects/1) · [🔌 API](docs/api.md) · [🏗 Архитектура](docs/architecture.md) · [🗂 Формат наборов](content/README.md) · [📱 Приложение](https://github.com/tuman-langs/tuman-langs-mobile)

</div>

---

## 📦 Что это

Приложение «Туман» почти всё делает без интернета. Сеть нужна в одном месте: чтобы посмотреть каталог готовых наборов и скачать набор на телефон. Этот репозиторий отвечает за обе части:

- **`content/`** — сами наборы карточек в JSON, сверенные со словарями;
- **`server/`** — маленький сервер, который отдаёт эти наборы по API.

```
📱 приложение ──GET /sets──────▶ 🖥 сервер ──читает──▶ 🗂 content/*.json
              ◀──список наборов──
              ──GET /sets/{id}──▶
              ◀──набор целиком───
```

### ✨ Что должно быть к защите

| | Требование ТЗ | Где |
|---|---|---|
| 🗂 | не менее **3 наборов**, всего **100+ карточек**, в том числе по аффиксам | `content/` |
| 📚 | каждый перевод сверен минимум с **двумя источниками** | `content/SOURCES.md` |
| 🔌 | список наборов и набор целиком в **JSON** | `GET /sets`, `GET /sets/{id}` |
| 🌍 | новый язык добавляется **без изменения кода** — только новыми файлами | формат в `content/README.md` |
| 🛡 | понятные ответы об ошибках, сервер не падает на кривом файле | `api/ErrorHandling.kt` |
| ☁️ | сервер доступен на время проверки и защиты | хостинг — отдельная задача |

---

## 🔌 API

| Запрос | Ответ |
|---|---|
| `GET /sets` | список наборов без карточек: `id`, `language`, `title`, `description`, `version`, `card_count` |
| `GET /sets/{id}` | набор целиком в формате `content/README.md`; нет такого — `404 {"error": "set_not_found"}` |

Примеры ответов и открытые вопросы — [docs/api.md](docs/api.md).

---

## 👥 Кто здесь работает

| Участник | Роль | Зона |
|---|---|---|
| Василенко Арина | backend-разработчик | `server/`, `docs/api.md`, размещение сервера |
| Богданов Егор | контент-лид, лингвист | `content/`: наборы, формат, источники |
| Бондаренко Егор, Карпенко Анастасия | Android | потребители API: сохранение и экран каталога |
| Туяков Мирхат | руководитель | ревью, согласование стека и формата |

---

## 🛠 Технологический стек

| Слой | Технологии |
|---|---|
| Язык | **Kotlin** (JVM 17) — тот же, что в приложении |
| Сервер | **Ktor** (Netty), ContentNegotiation, StatusPages, CallLogging |
| JSON | **Kotlinx Serialization** |
| Хранилище | файлы `content/*.json`, база данных не нужна |
| Тесты | Ktor `testApplication`, `kotlin-test` (JUnit 5) |
| Сборка | Gradle, версии в `gradle/libs.versions.toml` |

Стек окончательно утверждается в задаче [«Выбрать стек и запустить первый сервер»](https://github.com/tuman-langs/tuman-langs-backend/issues/1).

---

## 🏗 Архитектура

```
tuman-langs-backend/
├── content/                     наборы карточек: *.json, README.md (формат), SOURCES.md
├── server/src/main/kotlin/ru/itmo/tuman/catalog/
│   ├── Application.kt           точка входа: порт, плагины, маршруты
│   ├── api/                     маршруты, DTO, ошибки → JSON
│   ├── core/                    сценарии: GetSetsUseCase, GetSetUseCase
│   ├── domain/                  модели и интерфейс SetRepository
│   └── data/                    JsonFileSetRepository — читает content/
├── server/src/test/             тесты API, чтения файлов и проверки всех наборов
└── docs/                        api.md, architecture.md
```

```
SetsRoutes (api) ──▶ GetSetUseCase (core) ──▶ SetRepository (domain) ◀── JsonFileSetRepository (data) ──▶ content/
```

Слои взяты из CalorAI Backend и упрощены под маленький сервер. Что можно в каждом слое, как называть файлы и как отвечать на ошибки — [docs/architecture.md](docs/architecture.md).

---

## 🚀 Как запустить

> Раздел станет рабочим после первой задачи по серверу. Команды ниже — целевые, их подтверждает backend-разработчик.

**Нужно:** JDK 17 (подойдёт встроенный в Android Studio) или IntelliJ IDEA.

```bash
git clone https://github.com/tuman-langs/tuman-langs-backend.git
cd tuman-langs-backend
./gradlew :server:run            # Windows: .\gradlew.bat :server:run
```

Проверка: откройте http://localhost:8080/sets — должен прийти JSON со списком наборов, башкирские буквы без «?» и квадратов.

```bash
./gradlew test                   # тесты сервера и проверка всех файлов в content/
```

### Добавить набор без кода

Контент-лиду не нужен ни сервер, ни Android Studio: **Add file → Create new file** в папке `content/` прямо на GitHub, при сохранении выбрать **«Create a new branch for this commit and start a pull request»**. Правила оформления — [content/README.md](content/README.md).

---

## 🔄 Как мы работаем

```
Issue на доске («На неделю»)
   │  /start-task N
   ▼
ветка feature/N-… или content/N-… («В работе») ── коммиты «#N: что сделано»
   │  /finish-task
   ▼
PR в main («На ревью») ◀── /fix-review ◀── замечания ревьюера
   │  одобрено → Squash and merge
   ▼
issue закрыт, карточка «Готово»
```

- Одна задача — одна ветка — один PR, в `main` только через PR с одобрением.
- В каждом PR — раздел **«Использование ИИ»** (требование ИТМО).
- **Проверки запускаются локально:** GitHub Actions в репозитории выключены, поэтому `./gradlew test` перед PR обязателен.
- Переводы проверяет только контент-лид по словарям: ИИ помогает с форматом JSON, но не с переводами.

Первый день в проекте — [onboarding в mobile](https://github.com/tuman-langs/tuman-langs-mobile/blob/main/docs/onboarding.md).

---

## 🤖 Работа с Claude Code

Claude Code читает [CLAUDE.md](CLAUDE.md) и `docs/architecture.md` и сразу знает правила. Команды те же, что в mobile:

| Команда | Что делает |
|---|---|
| `/my-tasks` | мои задачи, PR и запросы на ревью в обоих репозиториях |
| `/start-task 1` | читает issue, создаёт ветку, составляет план по архитектуре |
| `/finish-task` | свежий main, тесты и проверка наборов, коммит, PR по шаблону |
| `/review-pr 4` | сверяет чужой PR с задачей и архитектурой, готовит замечания |
| `/fix-review` | разбирает замечания и правит в той же ветке |
| `/explain-my-work` | объясняет сделанное и задаёт контрольные вопросы к защите |

---

## 📚 Документация

| Документ | О чём |
|---|---|
| [CLAUDE.md](CLAUDE.md) | контекст и правила для Claude Code |
| [docs/architecture.md](docs/architecture.md) | слои, пакеты, куда класть файлы, ошибки |
| [docs/api.md](docs/api.md) | запросы и ответы |
| [content/README.md](content/README.md) | формат набора и правила оформления |
| [ТЗ проекта](https://github.com/tuman-langs/tuman-langs-mobile/blob/main/docs/tz.md) | требования (в репозитории приложения) |

---

<div align="center">

**Курсовой проект ИТМО · осень 2026**

Сделано с заботой о языках России 🌲

</div>
