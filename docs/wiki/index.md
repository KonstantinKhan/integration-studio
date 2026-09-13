# Integration Studio — Wiki

Точка входа в базу знаний проекта. Integration Studio — интеграционная платформа: Next.js UI → Kotlin BFF (Ktor) → внешние сервисы и API (Polynom — первый из них, платформа не привязана к нему).

## Структура

| Раздел | О чём |
|---|---|
| [Архитектура](architecture.md) | Модули gradle, потоки данных, роли слоёв |
| [Конфигурация и доступ к Polynom](configuration.md) | `application.conf`, `polynom.base-url`, токены, TokenManager, AuthConfig |
| [Пайплайн подключения и сессии](auth-flow.md) | Маршруты, степпер auth, guard маршрутов, logout |
| [Мониторинг подключений](connections-monitoring.md) | `GET /connections`, TCP-проверки, контракты |
| [Фронтенд](frontend.md) | Структура app router, api-client, хуки, стор, стили |
| [Окружение и сборка](dev-environment.md) | WSL + Windows, JDK, команды сборки, типовые флейки |
| [Процесс работы и инсайты](workflows-and-insights.md) | Оркестрация агентов, паттерн «фича→ревью→фикс», решения |
| [Loodsman](loodsman.md) | Второй API: модули, session-header auth, маршруты /loodsman/* |

## Ключевые маршруты

**Фронтенд (Next.js, :3000)**

| Путь | Что | Доступ |
|---|---|---|
| `/` | Стартовая: статус-чипы + карточки разделов | публичная |
| `/connections` | Полный статус подключений | публичная |
| `/polynom/auth` | Подключение к Polynom: хранилище → авторизация | публичная |
| `/dashboard` | «Работа с Polynom»: навигация/миграция/синхронизация + Выйти | сессия |
| `/polynom`, `/polynom/changes`, `/polynom/reference/**` | Рабочие разделы Polynom | сессия |
| `/loodsman/auth` | Подключение к Loodsman: база → авторизация | публичная |
| `/loodsman` | «Работа с Loodsman»: поиск объекта по идентификатору (карточка) + корень навигации + Выйти | сессия |

**Бэкенд (Ktor BFF, :8080)**

| Путь | Что | Доступ |
|---|---|---|
| `GET /storage-definitions` | Хранилища Polynom | публичный |
| `POST /authorize` | Вход (login/password/storageId) | публичный |
| `GET /check-session` | Проверка сессии | по куке |
| `POST /logout` | Выход (идемпотентный) | по куке |
| `GET /connections` | Статусы подключений (TCP) | публичный |
| `/*` (business) | concept, references, search, tree, streams… | сессия |
| `/loodsman/*` | databases, authorize, check-session, logout, tree/root, object-info | см. [Loodsman](loodsman.md) |

## Быстрые факты

- Адреса всех внешних сервисов — только в `application.conf` бэка ([Конфигурация](configuration.md))
- Доступность сервисов проверяется с хоста Kotlin BFF, не с Next-сервера ([Инсайты](workflows-and-insights.md))
- Каждому сервису — свой маршрут авторизации (`/polynom/auth`, далее по образцу) ([Auth flow](auth-flow.md))
- Loodsman: токенов нет — заголовки `web-loodsman-session` + `x-loodsman-db-name` на каждый запрос ([Loodsman](loodsman.md))
- Валидация изменений — обе сборки: `gradlew build` + `npm run build` ([Окружение](dev-environment.md))
