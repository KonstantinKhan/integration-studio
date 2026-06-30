export const SYNC_STATUS = {
  RUNNING: 'RUNNING',
  COMPLETED: 'COMPLETED',
  FAILED: 'FAILED',
} as const

export type SyncStatus = typeof SYNC_STATUS[keyof typeof SYNC_STATUS]

export const SYNC_STATUS_LABEL: Record<SyncStatus, string> = {
  [SYNC_STATUS.RUNNING]: 'Выполняется',
  [SYNC_STATUS.COMPLETED]: 'Завершено',
  [SYNC_STATUS.FAILED]:	'Ошибка'
}
