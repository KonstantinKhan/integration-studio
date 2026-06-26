import { create } from 'zustand'
import {
  getMigrationStreamStatus,
  migrationStreamEventsUrl,
  startMigrationStream,
} from '@/api/search-stream.api'
import type { IPropertySearchRequest } from '@/shared/types/propertySearchRequest.interface'
import type { EnrichedSearchResultItem } from '@/shared/types/enrichedSearchResultItem.interface'
import type { MigrationStatus, StreamEvent } from '@/shared/types/streaming.interface'

const STORAGE_KEY = 'integration-studio.migration.streamId'

interface MigrationStreamState {
  streamId: string | null
  status: MigrationStatus | null
  processedCount: number
  items: EnrichedSearchResultItem[]
  errorMessage: string | null
  /** true пока мы пытаемся стартовать или восстановить подключение */
  connecting: boolean
  eventSource: EventSource | null

  start: (request: IPropertySearchRequest) => Promise<void>
  /** При монтировании: проверить sessionStorage и переподключиться, если стрим жив */
  resumeFromStorage: () => Promise<void>
  reset: () => void
}

function persistStreamId(streamId: string | null) {
  try {
    if (streamId) {
      sessionStorage.setItem(STORAGE_KEY, streamId)
    } else {
      sessionStorage.removeItem(STORAGE_KEY)
    }
  } catch {
    /* sessionStorage может быть недоступен */
  }
}

function readStoredStreamId(): string | null {
  try {
    return sessionStorage.getItem(STORAGE_KEY)
  } catch {
    return null
  }
}

function parseEvent(type: string, data: string): StreamEvent | null {
  try {
    const parsed = JSON.parse(data)
    return { type, ...(parsed as object) } as StreamEvent
  } catch {
    return null
  }
}

export const useMigrationStreamStore = create<MigrationStreamState>((set, get) => {
  const closeEventSource = () => {
    const current = get().eventSource
    if (current) {
      current.close()
      set({ eventSource: null })
    }
  }

  /** Подписаться на SSE-события стрима. Только tail (новые события). */
  const connect = (streamId: string) => {
    closeEventSource()

    // rAF/setTimeout-батчинг: копим события, flush раз в 100мс одним set(),
    // иначе на каждый item весь список ре-рендерится → зависание UI.
    const MAX_ITEMS = 5000
    let pendingItems: EnrichedSearchResultItem[] = []
    let pendingProcessed = 0
    let flushScheduled = false
    const flush = () => {
      flushScheduled = false
      const batch = pendingItems
      pendingItems = []
      const processed = pendingProcessed
      pendingProcessed = 0
      if (batch.length === 0 && processed === 0) return
      set((s) => {
        const merged =
          batch.length === 0 ? s.items : s.items.concat(batch)
        const trimmed =
          merged.length > MAX_ITEMS ? merged.slice(merged.length - MAX_ITEMS) : merged
        return {
          items: trimmed,
          processedCount: processed || s.processedCount,
        }
      })
    }
    const scheduleFlush = () => {
      if (flushScheduled) return
      flushScheduled = true
      // setTimeout устойчивее rAF при Background-вкладке; 100мс = 10 fps обновления.
      setTimeout(flush, 100)
    }

    const es = new EventSource(migrationStreamEventsUrl(streamId), {
      withCredentials: true,
    })

    es.addEventListener('started', (e) => {
      const ev = parseEvent('started', (e as MessageEvent).data)
      if (ev?.type === 'started') {
        set({ streamId: ev.streamId, status: 'RUNNING' })
      }
    })

    es.addEventListener('item', (e) => {
      const ev = parseEvent('item', (e as MessageEvent).data)
      if (ev?.type === 'item') {
        pendingItems.push(ev.item)
        scheduleFlush()
      }
    })

    es.addEventListener('progress', (e) => {
      const ev = parseEvent('progress', (e as MessageEvent).data)
      if (ev?.type === 'progress') {
        pendingProcessed = ev.processed
        scheduleFlush()
      }
    })

    es.addEventListener('error', (e) => {
      // 'error' может прийти как от сервера событие ИЛИ как нативная ошибка сети.
      const data = (e as MessageEvent).data
      if (typeof data === 'string') {
        const ev = parseEvent('error', data)
        if (ev?.type === 'error') {
          flush()
          set({ status: 'FAILED', errorMessage: ev.message })
          persistStreamId(null)
          closeEventSource()
          return
        }
      }
      // Native onerror: статус уже RUNNING — оставляем подключение, EventSource
      // сам ретраит. Иначе помечаем как потерю связи.
      if (get().status !== 'RUNNING') {
        closeEventSource()
      }
    })

    es.addEventListener('done', (e) => {
      const ev = parseEvent('done', (e as MessageEvent).data)
      if (ev?.type === 'done') {
        flush()
        set({ status: 'COMPLETED', processedCount: ev.processed })
        persistStreamId(null)
        closeEventSource()
      }
    })

    set({ eventSource: es, status: 'RUNNING', errorMessage: null })
  }

  return {
    streamId: null,
    status: null,
    processedCount: 0,
    items: [],
    errorMessage: null,
    connecting: false,
    eventSource: null,

    async start(request) {
      // Если уже RUNNING локально — просто реконнект.
      const existing = get().streamId
      if (existing && get().status === 'RUNNING') {
        connect(existing)
        return
      }

      set({ connecting: true, errorMessage: null, items: [], processedCount: 0 })
      try {
        const res = await startMigrationStream(request)
        persistStreamId(res.streamId)
        set({ streamId: res.streamId, status: res.status ?? 'RUNNING' })
        connect(res.streamId)
      } catch (e) {
        set({
          connecting: false,
          status: 'FAILED',
          errorMessage: e instanceof Error ? e.message : 'Failed to start stream',
        })
        return
      }
      set({ connecting: false })
    },

    async resumeFromStorage() {
      const stored = readStoredStreamId()
      if (!stored) return
      if (get().eventSource) return // уже подключены

      set({ connecting: true })
      try {
        const status = await getMigrationStreamStatus(stored)
        if (status.status === 'RUNNING') {
          // Tail-only: при resume старые items не восстанавливаются, только count.
          set({
            streamId: status.streamId,
            status: status.status,
            processedCount: status.processedCount,
            items: [],
            errorMessage: null,
          })
          connect(status.streamId)
        } else {
          // Терминальный — показываем финал без EventSource.
          set({
            streamId: status.streamId,
            status: status.status,
            processedCount: status.processedCount,
            errorMessage: status.errorMessage,
          })
          persistStreamId(null)
        }
      } catch {
        // Стрим исчез (GC или не найден) — чистим.
        persistStreamId(null)
      } finally {
        set({ connecting: false })
      }
    },

    reset() {
      closeEventSource()
      persistStreamId(null)
      set({
        streamId: null,
        status: null,
        processedCount: 0,
        items: [],
        errorMessage: null,
        connecting: false,
      })
    },
  }
})
