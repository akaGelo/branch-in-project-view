# Branch in Project View

Простое расширение для IntelliJ IDEA: показывает текущую git-ветку рядом с корневыми узлами
проекта и каждого git-репозитория в Project View. Ветки кроме `main`/`master`/`develop`
подсвечиваются **жирным оранжевым**, чтобы сразу видеть, что ты не на основной ветке.

## Установка

### Вариант 1 — кастомный репозиторий плагинов (с авто-обновлением)

1. **Settings → Plugins → ⚙ → Manage Plugin Repositories…**
2. Добавь URL:
   ```
   https://raw.githubusercontent.com/akaGelo/branch-in-project-view/main/updatePlugins.xml
   ```
3. Найди **Branch in Project View** в Marketplace-поиске и установи. Обновления прилетают сами.

### Вариант 2 — вручную

Скачай `*.zip` из [Releases](https://github.com/akaGelo/branch-in-project-view/releases) →
**Settings → Plugins → ⚙ → Install Plugin from Disk…**

## Как устроено

- `BranchNodeDecorator` — `ProjectViewNodeDecorator`, дописывает `[branch]` к узлу, чья папка
  является корнем git-репозитория (корень проекта или контент-рут модуля). Ветка читается из
  кэша git4idea (`GitRepositoryManager`), без вызова git-процесса.
- `BranchRepoChangeListener` — слушает `GitRepositoryChangeListener`, при `checkout`
  обновляет дерево Project View.

## Разработка

Сборка таргетит версионный дистрибутив (`intellijIdeaCommunity`) — воспроизводимо в CI и на
любой машине. Локально для скорости можно собирать против уже установленной IDEA: добавь в
`~/.gradle/gradle.properties` строку

```
localIdePath=/путь/к/установленной/idea
```

— тогда платформа и JBR берутся с диска, ничего не качается.

```bash
./gradlew buildPlugin   # zip в build/distributions/
./gradlew runIde        # песочница с плагином
```

## Релиз

1. Обнови `pluginVersion` в `gradle.properties` (или сразу тегни — версия берётся из тега).
2. Поставь тег и запушь:
   ```bash
   git tag v0.1.1 && git push origin main --tags
   ```
3. Workflow `release.yml` соберёт zip, создаст GitHub Release, обновит `updatePlugins.xml` и
   `pluginVersion` в `main`. У пользователей с подключённым кастомным репозиторием появится апдейт.

## Требования

- JDK 21, Gradle 9 (через wrapper)
- IntelliJ Platform Gradle Plugin 2.16.0
- Совместимость: IDE начиная с build 243 (2024.3), без верхней границы
