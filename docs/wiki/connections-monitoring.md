# Мониторинг и управление подключениями

> См. также: [Архитектура](architecture.md) · [Конфигурация](configuration.md) · [Auth flow](auth-flow.md)

## Ключевое решение: старт никогда не блокируется сервисами

PostgreSQL и RabbitMQ подключаются **в фоне** (`ReconnectLoop`, backoff 2с→60с). Приложение стартует всегда; эндпоинты, зависящие от неготового сервиса, отдают **503** (`ServiceUnavailableException` → StatusPages). Ранее `HikariDataSource` в `DatabaseFactory.init` и `RabbitMqPublisher.init{}` роняли процесс, если БД/брокер недоступны.

## Владельцы соединений (package `connection`)

| Класс | Что делает |
|---|---|
| `DatabaseManager` | Владеет Hikari-пулом и Exposed `Database`. `connect(settings)` бросает исключение при недоступности → зовётся только из фона. После коннекта — Flyway-миграция. `current: Database?` = null, пока не подключены |
| `RabbitManager` | Владеет `Connection`. Коннект + декларация exchange `integration.events`. Включены `automaticRecoveryEnabled` + `topologyRecoveryEnabled` (обрывы в рантайме чинит сам клиент), `setConnectionTimeout(5000)` |
| `ReconnectLoop` | Фоновая петля: попытка → успех → ждать kick; провал → backoff ×2 до 60с. `requestReconnect()` — по команде (смена настроек из UI). Статусы в `StatusHolder` (`CONNECTING/CONNECTED/FAILED` + lastError + latency) |
| `RabbitMqPublisher` | Публикация. Берёт соединение у `RabbitManager` на каждый publish; нет соединения → `ServiceUnavailableException("rabbitmq")` → 503 |

Ленивые (не блокируют и не подключаются до запроса): Polynom/Loodsman HTTP-клиенты, SMTP.

## Схема БД — Flyway (не SchemaUtils)

- `V1__init.sql` (`resources/db/migration`): `migration_runs`, `migration_events`
- Зависимости: `flyway-core` + `flyway-database-postgresql` (11.7.2)
- `baselineOnMigrate(true)` + `baselineVersion("1")`: старые БД (созданные бывшим `SchemaUtils.createMissingTablesAndColumns`) получают baseline=1 и V1 пропускают, свежие — выполняют
- ⚠️ `SchemaUtils.createMissingTablesAndColumns` deprecated в Exposed — не возвращать

## Настройки подключений из UI

- `AppSettingsStore` (`config/AppSettingsStore.kt`): seed из `application.conf` → перекрытие из **`data/connection-settings.json`** (путь: конфиг `connection-settings.file` или env `CONNECTION_SETTINGS_FILE`; файл в .gitignore — внутри пароли)
- `GET /connections/settings` — текущие настройки, **секреты пустые**; `PUT` с пустым секретом = «не менять»
- `PUT /connections/settings` применяет сразу: persist → rebuild Polynom/Loodsman HTTP-цепочек (без сети) → `EmailNotifier.updateConfig` → `requestServicesReconnect()` (кик фоновых петель Postgres/Rabbit)
- ⚠️ Роуты обязаны брать `config.polynomApplicationService` / `config.loodsmanClient` **на каждый запрос** (AtomicReference внутри AppConfig). Захват экземпляра при регистрации роута = «Parent job is Completed» после первого же сохранения настроек (клиент закрывается при rebuild). Настройки планировщика применяются при `start()`

## Auth для страницы настроек

Сессия от Polynom неполучаема, когда Polynom лежит — а настройки нужны именно тогда. Поэтому:

- `POST /connections/auth` (публичный, в `publicPaths` интерцептора вместе с `connections`): пароль против `app.admin-password` (дефолт `"admin"`, env `APP_ADMIN_PASSWORD`, constant-time compare) → локальная сессия `admin` на 7 дней (обычная cookie `USER_SESSION`)
- `GET/PUT /connections/settings` — под сессией (интерцептор), как остальные бизнес-роуты

## Статусы: `GET /connections`

- Public (`publicPaths`: `connections`, `connections/auth`; префикс `loodsman`)
- Postgres/RabbitMQ — **реальное состояние менеджеров** (не TCP-проба); Polynom/Loodsman/SMTP — TCP `checkTcp` (3s timeout, `Dispatchers.IO`, параллельно; `CancellationException` пробрасывается)

```kotlin
@Serializable
data class ConnectionStatusDto(
    val id: String, val name: String, val host: String, val port: Int,
    val status: String,        // "connected" | "connecting" | "unreachable" | "disabled"
    val latencyMs: Long? = null,
    val error: String? = null,
)
```

- ⚠️ Внутренние состояния менеджера маппятся в контракт через `wireStatus()`: `FAILED` → `unreachable` (не выдумывать статус `failed` — фронтовый union его не знает)
- Красный = «включён и сломан», серый `disabled` = намеренно выключен (SMTP при `email.enabled=false`), синий пульсирующий = `connecting` (идёт фоновая попытка)

## Фронтенд

- Типы: `src/types/connections.ts` — `ConnectionStatus` (status: 4-union) + `ConnectionSettings`; API: `src/api/connections.api.ts` (statuses/auth/settings)
- Страница `/connections`: карточки + кнопка «Настройки» → `ConnectionSettingsPanel` (группы полей всех секций; при 401 — форма админ-пароля; «Сохранить и переподключить»)
- Статусы обновляются раз в 30с; каждый запрос — свой `AbortController`
- На стартовой `/`: статус-чипы (green/red/gray/blue-pulse)
