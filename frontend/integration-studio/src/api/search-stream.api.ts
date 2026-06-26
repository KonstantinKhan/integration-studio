import { apiClient, ApiError } from './api-client'
import { API_BASE_URL } from '@/config/api'
import type {
  IPropertySearchRequest,
} from '@/shared/types/propertySearchRequest.interface'
import type {
  StartStreamResponse,
  StreamStatusResponse,
} from '@/shared/types/streaming.interface'

/**
 * Стартовать новую миграцию. Если уже есть RUNNING поток для сессии —
 * сервер ответит 409 + { streamId, status, message } этого потока;
 * в этом случае резолвим как обычный ответ.
 */
export async function startMigrationStream(
  request: IPropertySearchRequest,
): Promise<StartStreamResponse> {
  const response = await fetch(`${API_BASE_URL}/search/streams/start`, {
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  })

  const body = await response.json().catch(() => ({}))

  if (response.ok || response.status === 409) {
    return body as StartStreamResponse
  }

  throw new ApiError(response.status, body.message ?? 'Request failed')
}

/** Текущий статус потока (для восстановления после refresh). */
export async function getMigrationStreamStatus(
  streamId: string,
): Promise<StreamStatusResponse> {
  return apiClient<StreamStatusResponse>(
    `/search/streams/${encodeURIComponent(streamId)}/status`,
    { method: 'GET' },
  )
}

/**
 * URL для SSE-подписки. Используется с EventSource.
 * Кросс-доменный (Next :3000 -> backend :8080), нужен withCredentials.
 */
export function migrationStreamEventsUrl(streamId: string): string {
  return `${API_BASE_URL}/search/streams/${encodeURIComponent(streamId)}/events`
}

