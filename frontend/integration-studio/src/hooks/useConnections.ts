import { useQuery } from '@tanstack/react-query'
import { apiClient } from '@/api/api-client'
import type { ConnectionStatus } from '@/types/connections'

export function useConnections() {
  return useQuery({
    queryKey: ['connections'],
    queryFn: () => apiClient<ConnectionStatus[]>('/connections'),
    refetchInterval: 30_000,
    retry: 1,
  })
}
