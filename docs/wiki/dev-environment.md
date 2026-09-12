# Окружение и сборка

> См. также: [Процесс работы](workflows-and-insights.md)

## Среда

- Проект в `/mnt/c/MyProjects/integration-studio` — Windows FS через WSL (DrvFs): медленнее нативного Linux, чувствителен к параллельным локам
- Бэкенд собирается из WSL (Gradle 9.1), фронтенд — из **Windows** (npm/node.exe)

## Бэкенд (Gradle)

```bash
cd backend/integration-studio
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew build -x test --console=plain
```

- Toolchain **Kotlin JVM 21**. В WSL был только JRE — установлен `openjdk-21-jdk-headless` (`apt install`). Без JDK (нет `javac`) toolchain не резолвится: «Cannot find a Java installation… Toolchain download repositories have not been configured»
- `JAVA_HOME` указывать явно — авто-детект Gradle его не нашёл

### Типовой флейк #1: локи Gradle

Симптомы: «Could not create service of type FileHasher…», падение без изменений кода.

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew --stop
# и повторить сборку
```

**Не запускать gradle и next build параллельно** — гонка файл-локов WSL↔Windows. Падеж на ровном месте → сначала простой перезапуск, и только потом поиск ошибки в коде (реальные ошибки компиляции: `grep "e: "` на выводе `:модуль:compileKotlin`).

## Фронтенд (Next.js)

```bash
# из WSL — через Windows node (node_modules содержит Windows-нативные бинарники, напр. lightningcss)
cmd.exe /c "cd /d C:\MyProjects\integration-studio\frontend\integration-studio && npm run build"
```

Прямой `npm run build` из WSL падает на загрузке нативных биндингов lightningcss — это не ошибка кода.

### Типовой флейк #2: стейл-кеш `.next`

После **удаления** route-файлов next build может падать на `.next/dev/types/validator.ts` («Cannot find module …route.js») — генерированные типы ссылаются на старый маршрут. Лечение:

```bash
rm -rf frontend/integration-studio/.next
```

## Валидация изменений

Воркфлоу сессии: правки → ревью сабагентом → фиксы → **обе** сборки зелёные → готово. Тесты целенаправленно не запускаются (договорённость: валидация = корректность сборок).

## Полезное

- Git: rename через WSL может падать с Permission denied, пока Windows держит дескриптор — тогда `cp -a` + `rm -rf`; при коммите rename определится сам
- Переменные окружения фронта: `NEXT_PUBLIC_API_URL` (бэк, default `http://localhost:8080`); бэка — see `application.conf` (env-override у каждого ключа, напр. `POLYNOM_BASE_URL`, `DB_*`, `RABBITMQ_*`)
