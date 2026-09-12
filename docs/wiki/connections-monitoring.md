# Мониторинг подключений

> См. также: [Архитектура](architecture.md) · [Auth flow](auth-flow.md) · [Инсайты](workflows-and-insights.md)

## Ключевое решение

Доступность сервисов проверяет **Kotlin BFF** — с того же хоста, откуда идут все реальные запросы. Next.js-сервер проверки не делает: его коннективность ≠ коннективность бэка и ≠ точка зрения пользователя. Адреса берутся из `application.conf` — дублирования в env фронта нет.

## Бэкенд: `GET /connections` (ktor-server-app, `routes/ConnectionsRoute.kt`)

- Публичный (bypass в SessionInterceptorPlugin) — статусы нужны на стартовой до логина. ⚠️ Открытый трейд-офф: аноним видит host:port инфраструктуры; при хостинге — отдавать только id/name/status
- Ответ: `List<ConnectionStatusDto>`

```kotlin
@Serializable
data class ConnectionStatusDto(
    val id: String, val name: String, val host: String, val port: Int,
    val status: String,        // "connected" | "unreachable" | "disabled"
    val latencyMs: Long? = null,
    val error: String? = null,
)
```

| id | Адрес | Источник |
|---|---|---|
| `polynom` | из `polynom.base-url` (URI; порт по умолчанию 443/https, 80/http) | application.conf |
| `postgres` | парсинг `database.url` (`jdbc:postgresql://host:port/...`), `parseDbPort` fallback 5432 | application.conf |
| `rabbitmq` | `rabbitmq.host` + `rabbitmq.port` | application.conf |
| `smtp` | `email.smtp-host` + `email.smtp-port` | `enabled=false` или пустой host → `disabled` без проверки |

Проверка: `checkTcp` — `Socket().use` + `connect(addr, 3000)` на `Dispatchers.IO`; latency = время до connect. **`CancellationException` пробрасывается** до общего catch (иначе отмена запроса не работает, сокет висит до таймаута). Четыре проверки — параллельно (`coroutineScope` + `async`), ошибка одной не роняет ответ.

## Фронтенд

- `hooks/useConnections.ts` — `apiClient<ConnectionStatus[]>('/connections')`, `refetchInterval: 30_000`
- Страница `/connections`: карточки host:port + бейдж + latency + error; бейджи через `STATUS_META` (exhaustive по union) + fallback `?? STATUS_META.unreachable` (бэк отдаёт status строкой — новый статус не уронит страницу); `disabled` → серый «Выключено»
- Каждый вызов — свой `AbortController` (ref в компоненте), предыдущий абортится: защита от гонки «ручной refresh vs тик интервала» и setState после unmount
- На стартовой `/`: статус-чипы (точка green/red/gray + имя → линк на `/connections`); при недоступном бэке — нейтральный чип «Сервисы» + подпись «статусы недоступны», страница работает

## Контракт

Общий тип фронта: `src/types/connections.ts` (`ConnectionStatus`) — единственный источник, импортируют и страница, и хуки. Дублировать интерфейс вручную не нужно.
