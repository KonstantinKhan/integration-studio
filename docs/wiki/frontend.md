# Фронтенд

> См. также: [Auth flow](auth-flow.md) · [Мониторинг](connections-monitoring.md) · [Архитектура](architecture.md)

## Стек

Next.js 16 (App Router, Turbopack), TypeScript strict, PrimeReact 10, Tailwind v4, zustand 5, @tanstack/react-query 5, lucide-react. Расположение: `frontend/integration-studio`.

## Структура `src/`

```
app/
  page.tsx                    # стартовая: статус-чипы + карточки разделов (публичная)
  connections/page.tsx        # полный статус подключений (публичная)
  dashboard/
    layout.tsx                # RequireSession guard
    page.tsx                  # «Работа с Polynom» + кнопка Выйти
  polynom/
    auth/page.tsx             # степпер: хранилище → авторизация (публичная)
    (protected)/              # guard; группу в URL не видно
      page.tsx                # /polynom — навигация по справочникам
      changes/                # /polynom/changes — синхронизация
      reference/[slug]/...    # справочники/каталоги/группы
api/
  api-client.ts               # единственная точка HTTP
  auth.api.ts, storage.api.ts, search-stream.api.ts, ...
components/                   # StorageDropdown, Dashboard, RequireSession, ...
hooks/                        # useSession, useConnections, useStorages, useAuthorize, ...
store/auth.store.ts           # zustand: selectedStorageId, isAuthenticated, logout()
types/                        # общий контракт connections.ts и доменные типы
config/api.ts                 # API_BASE_URL
```

## api-client (`src/api/api-client.ts`)

- `API_BASE_URL = NEXT_PUBLIC_API_URL ?? 'http://localhost:8080'`
- `credentials: 'include'` всегда (cookie-сессия)
- `ApiError(status, message)`: текст ошибки — `errorData.message ?? errorData.error ?? 'Request failed'` (бэк шлёт ошибки и ключом `message`, и ключом `error` — читаем оба)
- Умеет в пустые тела (204/пустой 200 → `undefined as T`)
- Таймауты — забота вызывающего: `apiClient(url, { signal })` (см. `AbortSignal.timeout` в useSession)

## Состояние

- **Серверные данные** — только react-query (queryKey уникальны: `session`, `connections`, `storages`, …)
- **zustand** (`auth.store.ts`) — только выбор пользователя: `selectedStorageId` (живёт между шагами auth и визитами), `isAuthenticated`, `logout()`. Persist нет — после F5 источник правды `/check-session`. `isAuthenticated`/`logout()` guard'ы не читают — сессионный verdict только у react-query
- Выход — полная перезагрузка: `window.location.assign('/')` после `await logout()` (см. [Auth flow](auth-flow.md)). Одним движением сбрасывает react-query кэш, zustand и гонки навигации; вариант `queryClient.clear()` после `router.push` страдал гонкой с живым guard — не использовать

## Стили

Палитра проекта: карточки `#f4f1ea`, границы `#d2b48c`, акцент `#fdf6ee`, кнопки `#8b4513`, фон страниц `bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50` (Tailwind v4 — `bg-linear-*`, не `bg-gradient-*`). Иконки — lucide-react. Каменно-янтарная тема выдерживается во всех новых экранах.

## Конвенции

- Типы контрактов с бэком — в `src/types/`, один файл на контракт, дубликаты не разводить
- Все импорты алиасные `@/...` — перемещение файлов внутри `src/app` безопасно
- Guard'ы — через layout (route groups), не через обёртку каждой страницы
- Бэкенд-контракт начинается с `/` (например `apiClient('/connections')`), Next route handlers для проксирования бэка **не используются** (удалены)
