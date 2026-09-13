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
| `:loodsman-dto-kmp` | Транспортные DTO (commonMain, kotlinx-serialization): `DatabaseOutputDto`, `LoginInputDto`, `SessionOutputDto`, `PdmNodeDto`, `PropObjectDto`, `VersionListItemDto`, `ObjectAttributeDto`, `LinkedObjectDto`, `LinkAttributeDto` |
| `:loodsman-session-store` | `LoodsmanSession(sessionId, dbName, userId, username)`, интерфейс `LoodsmanSessionStore` (store/retrieve/remove/cleanup), `InMemoryLoodsmanSessionStore` |
| `:loodsman-client` | `LoodsmanClient(httpClient)` → `AuthApi` + `PdmApi` + `ObjectInfoApi`; заголовки сессии на все запросы; query-параметры добавляются только non-null (исключение — `inverse`, добавляется всегда) |

**Ключевое решение:** хранилище loodsman-сессий — отдельный модуль, а не доменный `UserCredentials`: тот жёстко токеновый (access/refresh/expiry), гнуть его под «строку сессии» — врать о природе данных. Cookie `USER_SESSION` общая, ключ в обоих сторах — один bff-uuid; один выход завершает обе сессии.

## BFF-маршруты (ktor-server-app)

| Путь | Суть | Доступ |
|---|---|---|
| `GET /loodsman/databases` | список баз | публичный |
| `POST /loodsman/authorize` | `{dbName, username, password}` → remote login → стор + cookie | публичный |
| `GET /loodsman/check-session` | 200 `{authenticated, sessionId, username}` / 401 | публичный |
| `POST /loodsman/logout` | идемпотентен: remote logout (try/catch, ошибки глотаются) + remove + clear cookie | публичный |
| `GET /loodsman/tree/root` | Pdm/get-tree без `idVersion` — корень навигации | loodsman-сессия (проверка в хендлере) |
| `GET /loodsman/object-info?idVersion=` | Карточка объекта: агрегация 5 вызовов `ObjectInfo/*` (см. ниже) | loodsman-сессия (проверка в хендлере) |

- `SessionInterceptorPlugin`: bypass по префиксу `loodsman` (граничное сравнение пути, `loodsmanX` не проходит). Плагин проверяет polynom-креды, поэтому `tree/root` и `object-info` проверяют `LoodsmanSessionStore` сами

### object-info: агрегация

`GET /loodsman/object-info?idVersion=<idVersion>` собирает карточку из пяти эндпоинтов `ObjectInfo/*`:

1. `get-prop-objects?objectList=<id>` → свойства (type/product/version/state); пусто → **404** «Объект не найден», остальные вызовы не делаются
2. `get-version-list?typeName=&productName=` → версии (вызов только если type или product != null — иначе вернул бы версии по всей БД)
3. `get-info-about-version-mode-3?idVersion=` → атрибуты (name/value)
4. `get-l-objs?versionId=&inverse=false` → связи; атрибуты каждой связи `get-link-attributes?linkId=` — параллельно (`coroutineScope` + `async`/`awaitAll`)
5. Тип объекта связи: `get-l-objs` даёт только `typeId`, имя добирается батчем `get-prop-objects` по distinct versionId (чанки по 100 id, параллельно, merge в map idVersion→type)

Количество связи (BFF `LoodsmanLinkQuantityBffDto`): `minQuantity != null && min == max` → `value`, иначе → `min` + `max`. Фронт рендерит соответственно «Количество» или «Мин. количество»+«Макс. количество».

Ответ: `{ idVersion, properties{type,product,version,state}, versions[], attributes[{name,value}], links[{linkId,type,product,version,quantity,attributes[]}] }`
- Remote-401/403 от Loodsman: `PdmApi` проверяет статус ответа явно (ktor-клиент сам не валидирует) и бросает `LoodsmanUnauthorizedException` → хендлер чистит стор → 401 фронту → редирект на `/loodsman/auth`
- Общий polynom `POST /logout` тоже чистит loodsmanSessionStore и делает remote loodsman-logout

## Фронтенд

| Кусок | Что |
|---|---|
| `/loodsman/auth` | степпер: шаг 1 — выбор БД (пустые имена фильтруются), шаг 2 — логин/пароль |
| `/loodsman` | (protected): карточка «Поиск объекта» (ввод idVersion + «Найти») → `LoodsmanObjectCard`: Свойства / Версии (клик — запрос по этой версии) / Атрибуты / Связи (одна таблица: Тип, Версия, Количество, атрибуты связи); ниже — корень дерева + «Выйти» → `window.location.assign('/')` |
| guard | `RequireLoodsmanSession` + `useLoodsmanSession` (`['loodsman-session']`, 401→`{authenticated:false}`, timeout 10s, retry false) |
| хуки | `useLoodsmanDatabases` / `useLoodsmanAuthorize` (onSuccess: `removeQueries(['loodsman-session'])`, не invalidate) / `useLoodsmanTreeRoot` / `useLoodsmanObjectInfo` |
| object-info | `fetchLoodsmanObjectInfo` (таймаут 30s — бэк агрегирует до N+1 запросов); хук: `['loodsman','object-info',idVersion]`, `enabled: idVersion !== null`, **`placeholderData: keepPreviousData` + `staleTime: 60s` + `refetchOnWindowFocus: false`** — против дёргания при смене версии; страница: полный спиннер только при начальной загрузке, при смене — приглушение (opacity 0.6 + pointer-events none) и мелкий спиннер в шапке; устаревшие данные не выдаются за ответ: карточка гейтится по `info.idVersion === selectedIdVersion`, баннер ошибки показывается всегда при `isError` (404 «Объект … не найден» виден) |
| карточка | `LoodsmanObjectCard`: все пары label/value — единый грид `grid-cols-[minmax(0,220px)_1fr]` со strip-правилом `nth-last-child(-n+2):border-b-0`; блок связи — одна таблица (Тип/Версия/Количество/атрибуты связи) в том же стиле; lucide-иконки в обычных `<button>` (в PrimeReact Button сжимаются до ~8×16) |
| стор | zustand `selectedLoodsmanDb` (сбрасывается при logout) |
| 401 от дерева | `ApiError.status === 401` → `removeQueries` + `router.replace('/loodsman/auth')` (useEffect по статусу); прочие ошибки — карточка «Повторить» |
| стартовая | карточка по `conn.id === 'loodsman'`; пока `/connections` грузится — «...», не «недоступен» |

## Грабли

- `kotlinx` Json с `encodeDefaults=false` вырезает nullable-поля с дефолтом (`product`, `version`, `labelName`, `idLink`, …) — рантайм получает `undefined`; TS-интерфейс объявляет такие поля опциональными (`?: string | null`)
- **BFF DTO: nullable поля и списки — БЕЗ дефолтов** (`String?`, `List<…>` без `= null`/`= emptyList()`), иначе `encodeDefaults=false` вырезает сами ключи → фронт падает на обязательных полях; транспортные DTO Loodsman наоборот — с дефолтами (входящие)
- react-query `keepPreviousData`: при ошибке нового запроса остаются данные прошлого объекта — карточку гейтить по `idVersion === selectedIdVersion` и всегда показывать баннер ошибки, иначе пользователь видит чужой объект как ответ
- Умершая remote-сессия без маппинга 401 — вечное «Повторить» без пути к реавторизации; лечится только связкой «исключение в клиенте → чистка стора → 401 → редирект»
