import { useQuery } from '@tanstack/react-query'
import { fetchTreeRoot } from '@/api/tree.api'

export function useTreeRoot() {
  return useQuery({
    queryKey: ['tree', 'root'],
    queryFn: fetchTreeRoot,
  })
}
