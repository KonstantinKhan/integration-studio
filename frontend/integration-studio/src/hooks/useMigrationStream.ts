'use client'

import { useEffect } from 'react'
import { useMigrationStreamStore } from '@/store/migration-stream.store'

/**
 * Восстанавливает SSE-подключение после refresh вкладки по streamId из
 * sessionStorage и предоставляет управление стримом.
 */
export function useMigrationStream() {
  const resumeFromStorage = useMigrationStreamStore((s) => s.resumeFromStorage)
  const start = useMigrationStreamStore((s) => s.start)
  const streamId = useMigrationStreamStore((s) => s.streamId)
  const status = useMigrationStreamStore((s) => s.status)
  const processedCount = useMigrationStreamStore((s) => s.processedCount)
  const items = useMigrationStreamStore((s) => s.items)
  const errorMessage = useMigrationStreamStore((s) => s.errorMessage)
  const connecting = useMigrationStreamStore((s) => s.connecting)

  useEffect(() => {
    void resumeFromStorage()
  }, [resumeFromStorage])

  return {
    streamId,
    status,
    processedCount,
    items,
    errorMessage,
    connecting,
    start,
    resumeFromStorage,
  }
}
