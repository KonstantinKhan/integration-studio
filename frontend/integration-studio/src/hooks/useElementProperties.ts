import { useQuery } from '@tanstack/react-query'
import { fetchProperties } from '@/api/properties.api'

export function useElementProperties(typeId: number, objectId: number) {
  return useQuery({
    queryKey: ['elementProperties', typeId, objectId],
    queryFn: () => fetchProperties(typeId, objectId),
  })
}
