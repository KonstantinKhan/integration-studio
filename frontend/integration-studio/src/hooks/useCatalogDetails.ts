import { useQuery } from '@tanstack/react-query'
import { fetchCatalogDetails } from '@/api/catalogs.api'

export function useCatalogDetails(typeId: number, objectId: number) {
  return useQuery({
    queryKey: ['catalogDetails', typeId, objectId],
    queryFn: () => fetchCatalogDetails(typeId, objectId),
  })
}
