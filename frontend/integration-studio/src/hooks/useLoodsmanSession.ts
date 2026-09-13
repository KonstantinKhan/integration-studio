import { useQuery } from '@tanstack/react-query'
import { apiClient, ApiError } from '@/api/api-client'

export interface LoodsmanSessionCheck {
  authenticated: boolean
  sessionId?: string | null
  username?: string | null
}

export function useLoodsmanSession() {
  return useQuery({
    queryKey: ['loodsman-session'],
    queryFn: async (): Promise<LoodsmanSessionCheck> => {
      try {
        return await apiClient<LoodsmanSessionCheck>('/loodsman/check-session', {
          signal: AbortSignal.timeout(10_000),
        })
      } catch (err) {
        // 401 — сессии нет, это не ошибка
        if (err instanceof ApiError && err.status === 401) {
          return { authenticated: false }
        }
        throw err
      }
    },
    retry: false,
    staleTime: 60_000,
  })
}
