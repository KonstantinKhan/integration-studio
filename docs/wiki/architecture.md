# Архитектура

> См. также: [Конфигурация](configuration.md) · [Фронтенд](frontend.md) · [Auth flow](auth-flow.md)

## Общая схема

```
Браузер (UI, Next.js :3000)
   │  HTTP + cookie USER_SESSION
   ▼
Kotlin BFF (Ktor, :8080)  ── сессии в памяти, маршруты, маппинг
   │
   ├──▶ Polynom API (:5100/api/v1)   — HTTP, токены, refresh
   ├──▶ Loodsman API (:8076/api/v4)  — HTTP, session-заголовки (без refresh)
   ├──▶ PostgreSQL (:5432)            — Exposed, миграции
   ├──▶ RabbitMQ (:5672)              — события миграции
   └──▶ SMTP                          — уведомления (выключено по умолчанию)
```

Принцип: UI общается **только** с BFF. Прямых вызовов Polynom из браузера нет.

## Backend: `backend/integration-studio` (Gradle, Kotlin JVM 21)

| Модуль | Роль |
|---|---|
| `ktor-server-app` | BFF: маршруты, сессии, конфиг, планировщик, стримы. Точка сборки всех модулей |
| `polynom-client` | HTTP-клиент Polynom: `PolynomClient` + `LoginApi`/`ConceptApi`/`CatalogApi`/`GroupApi`, auth (`SessionStoreAuthProvider`, `TokenManager`), `config/AuthConfig` |
| `polynom-dto-kmp` | Транспортные DTO Polynom (KMP commonMain): `IStorageDefinition`, `LoginRequest/Response`, `IIdentifiableObject`… |
| `loodsman-client` | HTTP-клиент Loodsman: `LoodsmanClient` + `AuthApi`/`PdmApi`; токенов нет — заголовки `web-loodsman-session`/`x-loodsman-db-name` |
| `loodsman-dto-kmp` | Транспортные DTO Loodsman: `DatabaseOutputDto`, `LoginInputDto`, `SessionOutputDto`, `PdmNodeDto` |
| `loodsman-session-store` | Отдельное хранилище loodsman-сессий: `LoodsmanSession`, `LoodsmanSessionStore`, `InMemoryLoodsmanSessionStore` |
| `domain` | Доменные модели (`StorageDefinition`, auth-модели) + интерфейс `SessionStore` |
| `logics` | `PolynomApplicationService` + сервисы (Reference/Catalog/Concept/Group). Фасад над polynom-client |
| `mapping` | Мапперы transport→domain (`PolynomDto2Domain.kt`: например `IStorageDefinition.toDomain()`) |
| `bff-dto` | DTO BFF-контрактов (`PolynomElementFromPeriodRequestBffDto`) |
| `etl-mapper` | Мапперы Excel/ETL (часть легаси) |
| `excel-service` | Чтение/обработка Excel |

Поток типичного запроса: route (ktor-server-app) → `PolynomApplicationService` (logics) → `PolynomClient`/sub-API (polynom-client) → Polynom; ответ маппится в domain (mapping) и сериализуется в BFF-ответ. Для Loodsman проще: route → `LoodsmanClient` (loodsman-client) напрямую — свой ApplicationService не заводили, API тонкий ([Loodsman](loodsman.md)).

Правило именования клиента: класс `PolynomClient` (бывш. `PolynomApi`), переменные — `polynomClient`.

## Frontend: `frontend/integration-studio`

Next.js 16 (App Router, Turbopack), TypeScript strict, PrimeReact, Tailwind v4, zustand, @tanstack/react-query, lucide-react. Детали — [Фронтенд](frontend.md).

## Сессии

`SessionStore` (domain, интерфейс с `store/retrieve/updateCredentials/remove/cleanup`) → реализация `InMemorySessionStore` (ConcurrentHashMap + StateFlow). Cookie `USER_SESSION`: httpOnly, SameSite=lax, 7 дней. Фоновая чистка сессий старше 7 дней — каждые 60с (Application.kt).

Токены Polynom хранятся внутри `UserCredentials` в сессии; refresh — см. [Конфигурация](configuration.md). Loodsman-сессия (sessionId+dbName) — в отдельном `LoodsmanSessionStore` (модуль loodsman-session-store, НЕ domain), ключ тот же bff-uuid; cookie `USER_SESSION` общая — один `/logout` закрывает обе сессии ([Loodsman](loodsman.md)).

## Известные решения

- BFF отдаёт домену/фронту доменные модели, транспорт Polynom наружу не протекает (пример: `/storage-definitions` возвращает domain `StorageDefinition`, а не `IStorageDefinition`)
- Адреса всех внешних систем — единственная точка правды `application.conf` ([Конфигурация](configuration.md))
- Открытый вопрос: `GET /connections` публичен и отдаёт host:port инфраструктуры — приемлемо для dev, при хостинге стоит отдавать анонимам только id/name/status ([Мониторинг](connections-monitoring.md))
