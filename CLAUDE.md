# CLAUDE.md — Branch in Project View

Плагин IntelliJ IDEA: показывает текущую git-ветку у корневых узлов проекта/репозиториев в
Project View; не-`main`/`master`/`develop` подсвечивает жирным оранжевым.

**Это публичный репозиторий (github.com/akaGelo).** Никаких личных/корп данных в коммиты:
ни абсолютных путей `/home/...`, ни личной почты, ни корп-хостов. Перед пушем — аудит
`git grep --cached` по этим паттернам.

## Архитектура (вся логика — два класса)

- `BranchNodeDecorator` (`ProjectViewNodeDecorator`) — дописывает `[branch]` к узлу, чья папка
  есть корень git-репо (корень проекта через `guessProjectDir()` или `PsiDirectoryNode`).
  Помечаем только сам VCS-рут (`repo.root == dir`). Ветка — из кэша git4idea
  (`GitRepositoryManager.getRepositoryForRootQuick`), **без вызова git-процесса** (decorate
  зовётся часто и в фоне — не блокировать). Detached HEAD → короткий хэш, оранжевым.
- `BranchRepoChangeListener` (`GitRepositoryChangeListener`, через `<projectListeners>`) —
  при checkout делает `ProjectView.refresh()` на EDT (`invokeLater`, событие не на EDT).

## Сборка — важные решения

- **Таргет двойной** (`build.gradle.kts`): по умолчанию `intellijIdeaCommunity("2024.3")`
  (воспроизводимо в CI). Локально, если в `~/.gradle/gradle.properties` задан `localIdePath`,
  берётся установленная IDEA + её JBR — **ничего не качается**. Путь живёт только в глобальном
  gradle.properties, в репо его нет.
- **`instrumentCode = false`** и **`jetbrainsRuntimeLocal`** (в local-ветке) — намеренно:
  `www.jetbrains.com` (intellij-repository) отдаёт `java-compiler-ant-tasks` и JBR
  чудовищно медленно (read timeout). Так мы убираем обращения к этому хосту. Не включать
  инструментацию без необходимости.
- Стек: JDK 21, Gradle 9, IntelliJ Platform Gradle Plugin 2.16, Kotlin 2.3.10 (= бандл
  платформы 261), `since-build 243` без верхней границы.
- Версия — `pluginVersion` в `gradle.properties`; CI релиза переопределяет её из тега
  (`-PpluginVersion`). `version = providers.gradleProperty("pluginVersion").get()`.

## Распространение (намеренно НЕ Marketplace и НЕ GitHub Pages)

Кастомный репозиторий плагинов через `updatePlugins.xml` по **raw-ссылке**:
`https://raw.githubusercontent.com/akaGelo/branch-in-project-view/main/updatePlugins.xml`.
Pages не используем сознательно (под akaGelo висит основной сайт, не трогаем субдомен).

**Релиз:** `git tag vX.Y.Z && git push --tags` → workflow `release.yml` собирает zip,
создаёт GitHub Release с ним, регенерит `updatePlugins.xml` + `pluginVersion` и коммитит в
`main` (`[skip ci]`). raw-ссылка начинает отдавать новую версию → у пользователей авто-апдейт.
Без релиза (только тег) ссылка на zip 404 — релиз обязателен.

## Не коммитить

`build/`, `.gradle/`, `.intellijPlatform/` (генерится сборкой, ссылается на локальную IDE),
`.idea/`, `.kotlin/`. Всё в `.gitignore`.

## Команды

```bash
./gradlew buildPlugin   # zip в build/distributions/
./gradlew runIde        # песочница (локально поднимет IDE из localIdePath)
```
