import { useQuery } from '@tanstack/react-query'
import { fetchLoodsmanDatabases } from '@/api/loodsman.api'

export function useLoodsmanDatabases() {
  return useQuery({
    queryKey: ['loodsman', 'databases'],
    queryFn: fetchLoodsmanDatabases,
  })
}
