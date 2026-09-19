# Конфигурация и доступ к Polynom API

> См. также: [Архитектура](architecture.md) · [Auth flow](auth-flow.md) · [Подключения](connections-monitoring.md)

## Два слоя конфигурации

1. **`application.conf`** — дефолты + env-override (`key = value` затем `key = ${?ENV}`; env побеждает). Адреса всех внешних сервисов — только здесь.
2. **`data/connection-settings.json`** (`AppSettingsStore`) — настройки, изменённые из UI; читаются при старте поверх application.conf, пишутся атомарно (tmp+rename). Путь: `connection-settings.file` / env `CONNECTION_SETTINGS_FILE`. Файл в .gitignore (пароли). Детали и runtime-применение — [Подключения](connections-monitoring.md)

## Единственная точка правды

Все адреса внешних сервисов — в `ktor-server-app/src/main/resources/application.conf` (env-override для каждого ключа):

```hocon
polynom {
  base-url = "http://localhost:5100/api/v1"   # env POLYNOM_BASE_URL
}
loodsman {
  base-url = "http://localhost:8076/api/v4"   # env LOODSMAN_BASE_URL
}
database   { url = "jdbc:postgresql://127.0.0.1:5432/...", user, password, pool-size }
rabbitmq   { host, port, vhost, user, password, exchange, routing-key }
email      { enabled=false, smtp-host, smtp-port, smtp-tls, from, password, to }
sync-scheduler { enabled=false, interval-minutes, service-user, ... }
```

Менять адрес — только здесь. На старте было два хардкода `localhost:5100` в `Application.kt` — устранено: `defaultRequest` и refresh-эндпоинт читают один `polynom.base-url` (URI парсится: scheme→протокол, порт по умолчанию 443/80, path с завершающим `/`; fail-fast `require` на схему/хост).

## Как устроен доступ в `polynom-client`

**Два механизма, оба из одного конфига:**

1. **`defaultRequest`** (HttpClient в Application.kt) — базовый URL для всех относительных путей. Все вызовы API пишут пути относительно: `"login/sign-in"`, `"reference/get-all"`, `"concept/get-by-code"`…
2. **`baseUrl` → `SessionStoreAuthProvider.createTokenRefreshApi`** — абсолютный URL для единственного refresh-вызова: `PATCH baseUrl.trimEnd('/') + AuthConfig.LOGIN_ENDPOINT` (т.е. `.../api/v1/login/update-token`). `trimEnd('/')` защищает от двойного слеша при trailing slash в конфиге.

## Состав клиента

- `PolynomClient(httpClient, authProvider, tokenManager)` — фасад: `loginApi`, `conceptApi`, `catalogApi`, `groupApi` + собственные методы (references, groups, elements, properties, search, tree)
- `LoginApi` — все `login/*` эндпоинты: `storageDefinitions()` (`List<IStorageDefinition>`), `signIn(LoginRequest)`, `currentUserInfo(sessionId)`
- Собственный **единственный** `TokenManager` создаётся в `AppConfig.create` и шарится между `authProvider` и `PolynomClient` (раньше дублировался — устранено)
- Loodsman: второй `HttpClient` со своим `defaultRequest` (из `loodsman.base-url`) + `LoodsmanClient` + `InMemoryLoodsmanSessionStore`, всё собирается там же в `AppConfig.create` ([Loodsman](loodsman.md))

## Токены и refresh

- `SessionStoreAuthProvider.getAuthContext(sessionId)` достаёт `UserCredentials` из сессии
- `TokenManager.refreshAuth`/`getValidCredentials`: если токен истекает (порог `AuthConfig.REFRESH_THRESHOLD = 0.8` от срока жизни) → `PATCH /login/update-token` (Bearer access + refresh в body, text/plain) → обновление в SessionStore
- `AuthConfig`: живые константы `REFRESH_THRESHOLD`, `LOGIN_ENDPOINT`. Мёртвые `BASE_URL`/`BASE_API_PATH` удалены — не заводить обратно, адрес берётся из `application.conf`

## Маппинг storage-definitions

```
LoginApi.storageDefinitions(): List<IStorageDefinition>   (polynom-dto-kmp)
  → IStorageDefinition.toDomain()                         (mapping/PolynomDto2Domain.kt)
  → BFF /storage-definitions: List<StorageDefinition>     (domain, @Serializable)
```

## Типовой грабли

- `call.sessions.clear<T>()` — reified, вызывать `clear<UserSession>()`, не `clear(UserSession)`
- WSL-сборка требует `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` — [Окружение](dev-environment.md)
- Пароль страницы настроек: `app.admin-password` (дефолт `"admin"`, env `APP_ADMIN_PASSWORD`) — в прод перекрывать env
