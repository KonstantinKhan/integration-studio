import { useQuery } from '@tanstack/react-query'
import { fetchNodes } from '@/api/tree.api'

export function useNodes(typeId?: number, objectId?: number, enabled = true) {
  return useQuery({
    queryKey: ['tree', 'nodes', typeId, objectId],
    queryFn: () => fetchNodes(typeId!, objectId!),
    enabled: enabled && typeId != null && objectId != null,
  })
}
