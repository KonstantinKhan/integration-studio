'use client'

import { useEffect, useState } from 'react'
import type { SyncSummaryResponse } from '@/shared/types/sync-summary.interface'
import { formatDisplayDateTime } from '@/utils/format'

interface Props {
  summary: SyncSummaryResponse | undefined
  isLoading: boolean
}

export function SyncInfoPanel({ summary, isLoading }: Props) {
  const lastAutoSync = summary?.lastAutoSync ?? null
  const lastManualSync = summary?.lastManualSync ?? null
  const errorsBetween = summary?.errorsBetween ?? 0

  const autoTime = lastAutoSync ? new Date(lastAutoSync.startedAt).getTime() : null
  const manualTime = lastManualSync ? new Date(lastManualSync.startedAt).getTime() : null
  const autoIsNewer = autoTime !== null && manualTime !== null && autoTime > manualTime

  const [countdown, setCountdown] = useState<string | null>(null)
  useEffect(() => {
    if (!lastAutoSync || !summary?.schedulerIntervalMinutes) {
      setCountdown(null)
      return
    }
    const nextSyncAt = new Date(lastAutoSync.startedAt).getTime() + summary.schedulerIntervalMinutes * 60_000
    const update = () => {
      const diff = nextSyncAt - Date.now()
      if (diff <= 0) { setCountdown('скоро'); return }
      const totalSec = Math.floor(diff / 1000)
      const m = Math.floor(totalSec / 60)
      const s = totalSec % 60
      setCountdown(`${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`)
    }
    update()
    const timer = setInterval(update, 1000)
    return () => clearInterval(timer)
  }, [lastAutoSync?.startedAt, summary?.schedulerIntervalMinutes])

  if (isLoading) {
    return (
      <div
        className="max-w-3xl mx-auto mb-6 p-4 rounded-xl border-2 animate-pulse"
        style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c', height: '80px' }}
      />
    )
  }

  if (!summary) return null

  return (
    <div
      className="max-w-3xl mx-auto mb-6 p-4 rounded-xl border-2 shadow-sm text-sm text-stone-700"
      style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
    >
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
        <div className="flex flex-col gap-0.5">
          <span className="font-medium text-stone-500 text-xs uppercase tracking-wide">
            Последняя авто-синхронизация
          </span>
          {lastAutoSync ? (
            <>
              <span className="font-semibold text-stone-800">
                {formatDisplayDateTime(lastAutoSync.startedAt)}
              </span>
              {lastAutoSync.fromDate && lastAutoSync.toDate && (
                <span className="text-xs text-stone-500">
                  Период: {formatDisplayDateTime(lastAutoSync.fromDate)} — {formatDisplayDateTime(lastAutoSync.toDate)}
                </span>
              )}
              {lastAutoSync.processedCount !== null && (
                <span className="text-xs text-stone-500">
                  Обработано: {lastAutoSync.processedCount}
                </span>
              )}
              {countdown && (
                <span className="text-xs text-stone-500">
                  Следующая через: <span className="font-mono font-semibold">{countdown}</span>
                </span>
              )}
            </>
          ) : (
            <span className="text-stone-400 italic">не было</span>
          )}
        </div>

        <div className="flex flex-col gap-0.5">
          <span className="font-medium text-stone-500 text-xs uppercase tracking-wide">
            Последняя ручная синхронизация
          </span>
          {lastManualSync ? (
            <>
              <span className="font-semibold text-stone-800">
                {formatDisplayDateTime(lastManualSync.startedAt)}
              </span>
              {lastManualSync.fromDate && lastManualSync.toDate && (
                <span className="text-xs text-stone-500">
                  Период: {formatDisplayDateTime(lastManualSync.fromDate)} — {formatDisplayDateTime(lastManualSync.toDate)}
                </span>
              )}
              {lastManualSync.processedCount !== null && (
                <span className="text-xs text-stone-500">
                  Обработано: {lastManualSync.processedCount}
                </span>
              )}
            </>
          ) : (
            <span className="text-stone-400 italic">не было</span>
          )}
        </div>
      </div>

      {(lastAutoSync && lastManualSync) && (
        <div className="mt-3 pt-3 border-t border-stone-300">
          {autoIsNewer ? (
            <span className="font-semibold text-green-700">
              Авто-синхронизация актуальна
            </span>
          ) : (
            <span className="text-stone-600">
              Ошибочных синхронизаций за период между ними:{' '}
              <span className={errorsBetween > 0 ? 'font-semibold text-red-600' : 'font-semibold text-green-700'}>
                {errorsBetween}
              </span>
            </span>
          )}
        </div>
      )}
    </div>
  )
}
