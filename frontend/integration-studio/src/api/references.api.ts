import type { CreateReferenceResponse } from '@/shared/schemas/create-reference-command'
import { apiClient, ApiError } from './api-client'
import type { IReference } from '@/shared/types/reference.interface'

export function fetchReferences(): Promise<IReference[]> {
  return apiClient<IReference[]>('/references')
}

export function fetchReferenceDetails(
  typeId: number,
  objectId: number
): Promise<IReference> {
  return apiClient(`/references?typeId=${typeId}&objectId=${objectId}`)
}

export function createReference(name: string): Promise<CreateReferenceResponse> {
  return apiClient<CreateReferenceResponse>('/references/create', {
    method: 'POST',
    body: JSON.stringify({ name }),
  })
}

export function deleteReference(typeId: number, objectId: number): Promise<void> {
  return apiClient<void>('/references/delete', {
    method: 'POST',
    body: JSON.stringify({ typeId, objectId }),
  })
}
