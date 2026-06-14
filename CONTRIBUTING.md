# Contributing

Спасибо за интерес! Плагин намеренно крошечный — вся логика в двух Kotlin-классах.

## Окружение

- JDK 21
- Сборка через Gradle wrapper (`./gradlew`), отдельный Gradle не нужен.

По умолчанию сборка тянет версионный дистрибутив IDE (воспроизводимо). Если у тебя уже
установлена IntelliJ IDEA и хочется собирать быстрее (без скачивания платформы), добавь в
`~/.gradle/gradle.properties`:

```
localIdePath=/path/to/your/idea
```

## Сборка и запуск

```bash
./gradlew buildPlugin   # собрать zip → build/distributions/
./gradlew runIde        # запустить песочницу IDE с плагином
./gradlew verifyPlugin  # (опц.) IntelliJ Plugin Verifier
```

## Структура

- `src/main/kotlin/dev/gelo/branchview/BranchNodeDecorator.kt` — отрисовка ветки в Project View.
- `src/main/kotlin/dev/gelo/branchview/BranchRepoChangeListener.kt` — обновление при checkout.
- `src/main/resources/META-INF/plugin.xml` — регистрация декоратора и слушателя.

## Стиль

- Kotlin, как в существующем коде: компактно, комментарии — только по делу (почему, не что).
- Не делай `decorate()` тяжёлым: он вызывается часто и в фоне. Только чтение кэша git4idea,
  никаких вызовов git-процесса или блокирующих операций.

## Pull requests

1. Форкни, заведи ветку от `main`.
2. Убедись, что `./gradlew buildPlugin` проходит.
3. Опиши, что и зачем меняешь. Скриншот для UI-изменений приветствуется.

## Релизы (для мейнтейнера)

Тег `vX.Y.Z` в `main` → CI собирает, публикует GitHub Release с zip и обновляет
`updatePlugins.xml`. Подробности — в `CLAUDE.md`.
