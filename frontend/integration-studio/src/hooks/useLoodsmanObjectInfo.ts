import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { fetchLoodsmanObjectInfo } from '@/api/loodsman.api'

export function useLoodsmanObjectInfo(idVersion: number | null) {
  return useQuery({
    queryKey: ['loodsman', 'object-info', idVersion],
    queryFn: () => fetchLoodsmanObjectInfo(idVersion as number),
    enabled: idVersion !== null,
    placeholderData: keepPreviousData,
    staleTime: 60_000,
    refetchOnWindowFocus: false,
  })
}
