import { useQuery } from '@tanstack/react-query'
import { fetchGroupContent } from '@/api/element-groups.api'

export function useGroupContent(groupTypeId: number, groupObjectId: number, enabled: boolean = true) {
  return useQuery({
    queryKey: ['groupContent', groupTypeId, groupObjectId],
    queryFn: () => fetchGroupContent(groupTypeId, groupObjectId),
    enabled,
  })
}
