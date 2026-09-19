'use client'

import { useCallback, useEffect, useRef, useState } from 'react'
import { RefreshCw, Plug, AlertTriangle, Settings } from 'lucide-react'
import { apiClient, ApiError } from '@/api/api-client'
import { ConnectionSettingsPanel } from '@/components/ConnectionSettingsPanel'
import type { ConnectionStatus } from '@/types/connections'

const STATUS_META: Record<
  ConnectionStatus['status'],
  { label: string; badge: string; dot: string }
> = {
  connected: {
    label: 'Подключено',
    badge: 'bg-green-100 text-green-800',
    dot: 'bg-green-500',
  },
  unreachable: {
    label: 'Не подключено',
    badge: 'bg-red-100 text-red-800',
    dot: 'bg-red-500',
  },
  disabled: {
    label: 'Выключено',
    badge: 'bg-stone-200 text-stone-600',
    dot: 'bg-stone-400',
  },
  connecting: {
    label: 'Подключение...',
    badge: 'bg-blue-100 text-blue-800',
    dot: 'bg-blue-500 animate-pulse',
  },
}

const ConnectionsPage = () => {
  const [data, setData] = useState<ConnectionStatus[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [showSettings, setShowSettings] = useState(false)

  const controllerRef = useRef<AbortController | null>(null)

  const fetchStatuses = useCallback(async () => {
    controllerRef.current?.abort()
    const controller = new AbortController()
    controllerRef.current = controller
    const { signal } = controller

    if (signal.aborted) return

    setIsLoading(true)
    setError(null)

    try {
      const result = await apiClient<ConnectionStatus[]>('/connections', { signal })
      setData(result)
    } catch (err) {
      if (signal.aborted || (err instanceof DOMException && err.name === 'AbortError')) {
        return
      }
      if (err instanceof ApiError) {
        setError(err.message)
        return
      }
      setError(err instanceof Error ? err.message : 'Неизвестная ошибка')
    } finally {
      if (!signal.aborted) {
        setIsLoading(false)
      }
    }
  }, [])

  useEffect(() => {
    fetchStatuses()

    const interval = setInterval(() => fetchStatuses(), 30_000)

    return () => {
      clearInterval(interval)
      controllerRef.current?.abort()
    }
  }, [fetchStatuses])

  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
      <div className="w-full max-w-3xl">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-3xl font-bold text-stone-800">Подключения</h1>
            <p className="text-sm text-stone-600 mt-1">
              Статус подключения к внешним сервисам
            </p>
          </div>
          <button
            type="button"
            onClick={() => setShowSettings((v) => !v)}
            className="flex items-center gap-2 px-4 py-2 rounded-lg border-2 text-stone-800 shadow-sm transition hover:shadow-md"
            style={{
              backgroundColor: '#fdf6ee',
              borderColor: '#d2b48c',
            }}
          >
            <Settings size={16} />
            Настройки
          </button>
          <button
            type="button"
            onClick={() => fetchStatuses()}
            disabled={isLoading}
            className="flex items-center gap-2 px-4 py-2 rounded-lg border-2 text-stone-800 shadow-sm transition hover:shadow-md disabled:opacity-60 disabled:cursor-not-allowed"
            style={{
              backgroundColor: '#fdf6ee',
              borderColor: '#d2b48c',
            }}
          >
            <RefreshCw size={16} className={isLoading ? 'animate-spin' : ''} />
            Обновить
          </button>
        </div>

        {error && (
          <div
            className="flex items-center gap-2 p-4 rounded-xl border-2 mb-4 text-red-800"
            style={{ backgroundColor: '#fdf6ee', borderColor: '#d2b48c' }}
          >
            <AlertTriangle size={18} className="text-red-600 shrink-0" />
            <span className="text-sm">
              Не удалось получить статусы подключений: {error}
            </span>
          </div>
        )}

        {showSettings && (
          <div className="mb-6">
            <ConnectionSettingsPanel onSaved={fetchStatuses} />
          </div>
        )}

        <div className="space-y-3">
          {isLoading && data.length === 0 && (
            <div
              className="p-6 rounded-xl border-2 text-center text-stone-500"
              style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
            >
              Проверка подключений...
            </div>
          )}

          {!isLoading && !error && data.length === 0 && (
            <div
              className="p-6 rounded-xl border-2 text-center text-stone-500"
              style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
            >
              Нет данных о подключениях
            </div>
          )}

          {data.map((conn) => {
            const status = STATUS_META[conn.status] ?? STATUS_META.unreachable

            return (
              <div
                key={conn.id}
                className="flex items-center justify-between gap-4 p-4 rounded-xl border-2 shadow-sm"
                style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
              >
                <div className="flex items-center gap-3 min-w-0">
                  <div
                    className="flex items-center justify-center w-10 h-10 rounded-lg shrink-0"
                    style={{
                      backgroundColor: '#fdf6ee',
                      border: '1px solid #d2b48c',
                    }}
                  >
                    <Plug size={20} className="text-stone-700" />
                  </div>
                  <div className="min-w-0">
                    <div className="text-base font-semibold text-stone-800">
                      {conn.name}
                    </div>
                    <div className="text-sm text-stone-600 truncate">
                      {conn.host}:{conn.port}
                    </div>
                    {conn.error && (
                      <div className="text-xs text-red-600 mt-0.5 truncate">
                        {conn.error}
                      </div>
                    )}
                  </div>
                </div>

                <div className="flex items-center gap-3 shrink-0">
                  {conn.latencyMs !== null && (
                    <span className="text-sm text-stone-600">
                      {conn.latencyMs} мс
                    </span>
                  )}
                  <span
                    className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium ${status.badge}`}
                  >
                    <span className={`w-2 h-2 rounded-full ${status.dot}`} />
                    {status.label}
                  </span>
                </div>
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}

export default ConnectionsPage
