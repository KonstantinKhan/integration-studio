import { useQuery } from '@tanstack/react-query'
import { fetchCatalogs } from '@/api/catalogs.api'

export function useCatalogs(referenceTypeId: number, referenceObjectId: number) {
  return useQuery({
    queryKey: ['catalogs', referenceTypeId, referenceObjectId],
    queryFn: () => fetchCatalogs(referenceTypeId, referenceObjectId),
  })
}
