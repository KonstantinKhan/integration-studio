import { useQuery } from '@tanstack/react-query'
import { fetchLoodsmanTreeRoot } from '@/api/loodsman.api'

export function useLoodsmanTreeRoot() {
  return useQuery({
    queryKey: ['loodsman', 'tree', 'root'],
    queryFn: fetchLoodsmanTreeRoot,
  })
}
