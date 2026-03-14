import { useQuery } from '@tanstack/react-query'
import { fetchReferenceDetails } from '@/api/references.api'

export function useReferenceDetails(typeId: number, objectId: number) {
  return useQuery({
    queryKey: ['referenceDetails', typeId, objectId],
    queryFn: () => fetchReferenceDetails(typeId, objectId),
  })
}
