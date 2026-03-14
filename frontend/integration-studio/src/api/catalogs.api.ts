import { apiClient } from './api-client'
import type { ICatalog } from '@/shared/types/catalog.interface'

export function fetchCatalogs(
  referenceTypeId: number,
  referenceObjectId: number,
): Promise<ICatalog[]> {
  return apiClient<ICatalog[]>(
    `/catalogs?referenceTypeId=${referenceTypeId}&referenceObjectId=${referenceObjectId}`,
  )
}

export function fetchCatalogDetails(
  typeId: number,
  objectId: number,
): Promise<ICatalog> {
  return apiClient<ICatalog>(
    `/catalogs?typeId=${typeId}&objectId=${objectId}`,
  )
}
