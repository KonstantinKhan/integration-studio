# Loodsman Web API — второй подключённый сервис

> См. также: [Архитектура](architecture.md) · [Конфигурация](configuration.md) · [Auth flow](auth-flow.md)
>
> Swagger: `raw/loodsman/swagger.json` (Loodsman Web API v4, ~5 МБ — читать выборочно: сначала список `paths`, затем точечные `components.schemas`).

## Специфика API (отличия от Polynom)

- **Нет access/refresh токенов и данных о времени жизни сессии.** Auth — заголовки на каждый запрос:
  - `web-loodsman-session: <sessionId>` — сессия из ответа login
  - `x-loodsman-db-name: <dbName>` — выбранная база
- `GET /api/v4/Auth/databases` — анонимный, список баз (`[{name}]`)
- `POST /api/v4/Auth/login` `{dbName, username, password, rememberMe}` → `SessionOutputDto{sessionId, dbName, userId, checkoutId?, isEditable}`; смена базы — сначала logout
- `POST /api/v4/Auth/logout` — закрывает текущее подключение
- `GET /api/v4/Pdm/get-tree?idVersion=&hasLink=&linkTypes=&direction=` → `[ProjectDto | ObjectDto]`; в swagger `ObjectDto` наследует `ProjectDto` — в Kotlin объединены в один `PdmNodeDto` с nullable-«хвостом» (`idLink`, `idLinkType`, `minQuantity`, `maxQuantity`). Без `idVersion` — верхнеуровневые узлы
- base-url по умолчанию `http://localhost:8076/api/v4` (env `LOODSMAN_BASE_URL`); клиент зовёт относительные пути (`Auth/login`, `Pdm/get-tree`) через свой `defaultRequest`

## Модули (зеркало polynom-структуры)

| Модуль | Состав |
|---|---|
| `:loodsman-dto-kmp` | Транспортные DTO (commonMain, kotlinx-serialization): `DatabaseOutputDto`, `LoginInputDto`, `SessionOutputDto`, `PdmNodeDto` |
| `:loodsman-session-store` | `LoodsmanSession(sessionId, dbName, userId, username)`, интерфейс `LoodsmanSessionStore` (store/retrieve/remove/cleanup), `InMemoryLoodsmanSessionStore` |
| `:loodsman-client` | `LoodsmanClient(httpClient)` → `AuthApi` + `PdmApi`; заголовки сессии на logout/getTree; query-параметры getTree добавляются только non-null |

**Ключевое решение:** хранилище loodsman-сессий — отдельный модуль, а не доменный `UserCredentials`: тот жёстко токеновый (access/refresh/expiry), гнуть его под «строку сессии» — врать о природе данных. Cookie `USER_SESSION` общая, ключ в обоих сторах — один bff-uuid; один выход завершает обе сессии.

## BFF-маршруты (ktor-server-app)

| Путь | Суть | Доступ |
|---|---|---|
| `GET /loodsman/databases` | список баз | публичный |
| `POST /loodsman/authorize` | `{dbName, username, password}` → remote login → стор + cookie | публичный |
| `GET /loodsman/check-session` | 200 `{authenticated, sessionId, username}` / 401 | публичный |
| `POST /loodsman/logout` | идемпотентен: remote logout (try/catch, ошибки глотаются) + remove + clear cookie | публичный |
| `GET /loodsman/tree/root` | Pdm/get-tree без `idVersion` — корень навигации | loodsman-сессия (проверка в хендлере) |

- `SessionInterceptorPlugin`: bypass по префиксу `loodsman` (граничное сравнение пути, `loodsmanX` не проходит). Плагин проверяет polynom-креды, поэтому `tree/root` проверяет `LoodsmanSessionStore` сам
- Remote-401/403 от Loodsman: `PdmApi` проверяет статус ответа явно (ktor-клиент сам не валидирует) и бросает `LoodsmanUnauthorizedException` → хендлер чистит стор → 401 фронту → редирект на `/loodsman/auth`
- Общий polynom `POST /logout` тоже чистит loodsmanSessionStore и делает remote loodsman-logout

## Фронтенд

| Кусок | Что |
|---|---|
| `/loodsman/auth` | степпер: шаг 1 — выбор БД (пустые имена фильтруются), шаг 2 — логин/пароль |
| `/loodsman` | (protected): корень дерева (`product ?? labelName ?? '#id'`), «Выйти» → `window.location.assign('/')` |
| guard | `RequireLoodsmanSession` + `useLoodsmanSession` (`['loodsman-session']`, 401→`{authenticated:false}`, timeout 10s, retry false) |
| хуки | `useLoodsmanDatabases` / `useLoodsmanAuthorize` (onSuccess: `removeQueries(['loodsman-session'])`, не invalidate) / `useLoodsmanTreeRoot` |
| стор | zustand `selectedLoodsmanDb` (сбрасывается при logout) |
| 401 от дерева | `ApiError.status === 401` → `removeQueries` + `router.replace('/loodsman/auth')` (useEffect по статусу); прочие ошибки — карточка «Повторить» |
| стартовая | карточка по `conn.id === 'loodsman'`; пока `/connections` грузится — «...», не «недоступен» |

## Грабли

- `kotlinx` Json с `encodeDefaults=false` вырезает nullable-поля с дефолтом (`product`, `version`, `labelName`, `idLink`, …) — рантайм получает `undefined`; TS-интерфейс объявляет такие поля опциональными (`?: string | null`)
- Умершая remote-сессия без маппинга 401 — вечное «Повторить» без пути к реавторизации; лечится только связкой «исключение в клиенте → чистка стора → 401 → редирект»
