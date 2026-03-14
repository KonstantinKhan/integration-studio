import { apiClient } from './api-client'
import type { PropertyOwnerResponse } from '@/shared/types/propertyOwnerResponse.interface'

export function fetchProperties(
  typeId: number,
  objectId: number,
): Promise<PropertyOwnerResponse> {
  return apiClient<PropertyOwnerResponse>('/properties', {
    method: 'POST',
    body: JSON.stringify({ typeId, objectId }),
  })
}
