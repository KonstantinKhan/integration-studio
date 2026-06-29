export interface SyncRunInfo {
  runId: string
  startedAt: string
  fromDate: string | null
  toDate: string | null
  processedCount: number | null
}

export interface SyncSummaryResponse {
  lastAutoSync: SyncRunInfo | null
  lastManualSync: SyncRunInfo | null
  errorsBetween: number
  schedulerIntervalMinutes: number | null
}
