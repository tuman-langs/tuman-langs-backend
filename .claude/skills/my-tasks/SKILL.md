---
name: my-tasks
description: Показать участнику его задачи и PR в проекте «Туман» и подсказать, чем заняться дальше. Вызывается командой /my-tasks.
disable-model-invocation: true
allowed-tools: Bash(gh api user *) Bash(gh issue list *) Bash(gh pr list *) Bash(git branch *) Bash(git status *)
---

Помоги участнику понять, что у него сейчас на руках. Ничего не меняй — только читай и подсказывай.

1. Узнай логин: `gh api user --jq .login`. Если `gh` не авторизован — подскажи `gh auth login` и остановись.
2. Открытые задачи в обоих репозиториях:
   - `gh issue list --repo tuman-langs/tuman-langs-mobile --assignee @me --state open --json number,title,labels,url`
   - `gh issue list --repo tuman-langs/tuman-langs-backend --assignee @me --state open --json number,title,labels,url`
3. PR участника и PR, где ждут его ревью, в обоих репозиториях:
   - `gh pr list --repo <репозиторий> --author @me --json number,title,reviewDecision,url`
   - `gh pr list --repo <репозиторий> --search "review-requested:@me" --json number,title,url`
4. Локальное состояние: `git branch --show-current` и `git status --short`.
5. Покажи короткую сводку таблицей: задача → репозиторий → есть ли ветка (`git branch -a --list "*/<номер>-*"`) → есть ли PR → что сделать дальше.
6. Подскажи один следующий шаг, по приоритету:
   - есть PR с замечаниями (`CHANGES_REQUESTED`) → `/fix-review`;
   - кто-то ждёт его ревью → `/review-pr <номер>`;
   - есть незаконченная ветка с изменениями → продолжить или `/finish-task`;
   - есть задача без ветки → `/start-task <номер>`;
   - задач нет → посмотреть колонку «На неделю» на доске https://github.com/orgs/tuman-langs/projects/1 и написать руководителю.

Если задача в другом репозитории, чем текущая папка, скажи, в какой папке запустить Claude Code.
