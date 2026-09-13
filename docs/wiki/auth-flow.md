# Пайплайн подключения к Polynom и сессии

> См. также: [Фронтенд](frontend.md) · [Мониторинг](connections-monitoring.md) · [Конфигурация](configuration.md)

## Флоу

```
/ (стартовая, публичная)
 ├─ карточка Polynom:
 │    сессия жива → «Открыть» → /dashboard
 │    нет сессии  → «Подключить» → /polynom/auth
 │    Polynom unreachable → кнопка disabled («Polynom недоступен»)
 │
 └─ /polynom/auth (степпер, публичная)
      Шаг 1: выбор хранилища (GET /storage-definitions) → «Далее»
      Шаг 2: логин/пароль → POST /authorize → /dashboard

/dashboard «Работа с Polynom»: Навигация | Миграция (скоро) | Синхронизация | Выйти
```

Loodsman-ветка (зеркало того же паттерна):

```
карточка Loodsman на / (статус из /connections)
 └─ /loodsman/auth: шаг 1 — выбор БД (GET /loodsman/databases) → шаг 2 — логин/пароль → POST /loodsman/authorize → /loodsman
/loodsman «Работа с Loodsman»: корень навигации (Pdm/get-tree) | Выйти
```

Принципы: стартовая доступна при любом состоянии сети/Polynom (все запросы фоновые, через react-query); авторизация закреплена за сервисом — паттерн `/polynom/auth`, следующий сервис получит свой `/<service>/auth`.

## Маршруты и guard

- **Route group** `app/polynom/(protected)/` — охраняет `/polynom`, `/polynom/changes`, `/polynom/reference/**`; URL группу не содержит
- `app/dashboard/layout.tsx` и `app/polynom/(protected)/layout.tsx` — тонкие обёртки над `components/RequireSession.tsx`
- `/polynom/auth` — **вне** guard (точка входа), там же ссылка «← На главную»

## RequireSession (guard)

| Состояние useSession | Поведение |
|---|---|
| `isLoading` | полноэкранный спиннер (`isLoading`), children не рендерятся (нет мигания контента) |
| `isError` | экран «Бэкенд недоступен» + «Повторить» (`refetch()`); редиректа нет — избегаем циклов |
| `authenticated: false` | `router.replace('/polynom/auth')` (только когда запрос завершился: `!isFetching`) |
| `authenticated: true` | children |

`RequireLoodsmanSession` — точная копия для Loodsman: `useLoodsmanSession` (`GET /loodsman/check-session`, ключ `['loodsman-session']`), редирект `/loodsman/auth`; layout `app/loodsman/(protected)/layout.tsx` ([Loodsman](loodsman.md)).

## useSession

- `GET /check-session`; 401 → `{authenticated: false}` (не ошибка); прочие сетевые сбои → isError
- **Обязателен таймаут**: `AbortSignal.timeout(10_000)` — иначе при умершей сети guard висит вечно
- `retry: false`, `staleTime: 60_000`
- `['session']` удаляется из кэша в `onSuccess` мутации авторизации (`useAuthorize`, `removeQueries`) — инвалидации мало: guard маунтится с протухшим `{authenticated:false}` и уходит в редирект до завершения refetch

## Сессии на бэке

- Cookie `USER_SESSION` (httpOnly, lax, 7 дней); состояние — `InMemorySessionStore` (чистка старше 7 дней каждые 60с)
- `POST /authorize` (Routing.kt): создаёт sessionId (UUID), `signIn` в Polynom, credentials (access/refresh/expiry) → `sessionStore.store`, cookie set
- `GET /check-session`: `SessionCheckResponse{authenticated, sessionId, username}`

## Logout

- Бэк: `POST /logout` — `sessionStore.remove(id)` + `call.sessions.clear<UserSession>()` (reified! не `clear(UserSession)`), идемпотентен (200 без сессии)
- **Общая cookie `USER_SESSION` между сервисами**: `POST /logout` теперь чистит и `loodsmanSessionStore` (+ remote loodsman-logout в try/catch). `POST /loodsman/logout` — идемпотентен, зеркальная логика ([Loodsman](loodsman.md))
- Фронт (`Dashboard.handleLogout`): `await logout()` (ошибки гасим — разлогин идемпотентен), затем `window.location.assign('/')` — полная перезагрузка детерминированно сбрасывает react-query кэш, zustand и убирает гонку guard-редиректа (старый вариант с `router.push` + `queryClient.clear()` страдал гонкой: живой guard успевал отрефетчить `/check-session`, получить 401 и перебить навигацию)

## Whitelist (кто не требует сессии)

- Вне `route("/")` (плагин SessionInterceptor вообще не применяется): `/storage-definitions`, `/authorize`, `/check-session`, `/logout`
- Внутри плагина есть bypass по точному совпадению сегмента: `"connections"` (`trim('/')` — покрывает и trailing slash; подпути/чужие пути не матчится)
- Loodsman: весь префикс `loodsman` — bypass плагина (публичные маршруты + `tree/root`, который проверяет loodsman-сессию сам: плагин смотрит только polynom-креды)
