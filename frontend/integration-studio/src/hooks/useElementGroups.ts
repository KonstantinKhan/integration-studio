import { useQuery } from '@tanstack/react-query'
import { fetchElementGroups } from '@/api/element-groups.api'

export function useElementGroups(catalogTypeId: number, catalogObjectId: number) {
  return useQuery({
    queryKey: ['elementGroups', catalogTypeId, catalogObjectId],
    queryFn: () => fetchElementGroups(catalogTypeId, catalogObjectId),
  })
}
