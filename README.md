# Туман — сервер каталога наборов

Backend проекта «Туман — Языки России»: каталог готовых наборов карточек по башкирскому языку, который Android-приложение скачивает на устройство. Приложение — в репозитории [tuman-langs-mobile](https://github.com/tuman-langs/tuman-langs-mobile), задачи — на доске «Туман — MVP» организации `tuman-langs`.

## Что здесь

| Путь | Что |
|---|---|
| `content/` | наборы карточек в JSON и правила их оформления |
| `docs/api.md` | черновик API каталога |
| `CLAUDE.md` | контекст для Claude Code |
| `.github/` | шаблоны задач и PR |

Код сервера появится, когда backend-разработчик выберет стек и согласует его с руководителем.

## Как работаем

Как в mobile: задача на доске → ветка `<тип>/<номер>-<кратко>` → PR в `main` с `Closes #N` и разделом «Использование ИИ» → ревью → Squash and merge. Подробно — [onboarding в mobile](https://github.com/tuman-langs/tuman-langs-mobile/blob/main/docs/onboarding.md).
