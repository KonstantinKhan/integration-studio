import type { SyncStatus } from '@/features/changes/constants'
import type { EnrichedSearchResultItem } from './enrichedSearchResultItem.interface'
import type { IPropertySearchRequest } from './propertySearchRequest.interface'

export interface StreamStatusResponse {
  streamId: string
  status: SyncStatus
  processedCount: number
  errorMessage: string | null
}

export interface StartStreamResponse {
  streamId: string
  status?: SyncStatus
  message?: string
}

export type StreamEvent =
  | { type: 'started'; streamId: string }
  | { type: 'item'; item: EnrichedSearchResultItem }
  | { type: 'progress'; processed: number }
  | { type: 'error'; message: string }
  | { type: 'done'; processed: number }

export type { IPropertySearchRequest as PropertySearchRequest }

