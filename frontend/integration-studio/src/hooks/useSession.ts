import { useQuery } from '@tanstack/react-query'
import { apiClient, ApiError } from '@/api/api-client'

export interface SessionCheck {
  authenticated: boolean
  sessionId?: string | null
  username?: string | null
}

export function useSession() {
  return useQuery({
    queryKey: ['session'],
    queryFn: async (): Promise<SessionCheck> => {
      try {
        return await apiClient<SessionCheck>('/check-session', {
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
