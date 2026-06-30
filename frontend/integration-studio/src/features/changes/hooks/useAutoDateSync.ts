import type { SyncSummaryResponse } from '@/shared/types/sync-summary.interface'
import { useEffect, useRef } from 'react'

export function useAutoDateSync(
  syncSummary: SyncSummaryResponse | undefined,
  isLoading: boolean,
  setFrom: (date: Date | null) => void,
  setTo: (date: Date | null) => void,
) {
  const lastApplied = useRef<string | null>(null)

  useEffect(() => {
    if (isLoading) return
    const startedAt = syncSummary?.lastAutoSync?.startedAt ?? null
    if (startedAt === lastApplied.current) return
    lastApplied.current = startedAt

    const timeoutId = setTimeout(() => {
      if (syncSummary?.lastAutoSync?.fromDate) {
        setFrom(new Date(syncSummary.lastAutoSync.fromDate))
      }
      setTo(new Date())
    }, 0)

    return () => clearTimeout(timeoutId)
  }, [syncSummary, isLoading])
}
