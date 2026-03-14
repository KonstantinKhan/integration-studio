import { apiClient } from './api-client'
import type { IStorage } from '@/shared/types/storage.interface'

export function fetchStorages(): Promise<IStorage[]> {
  return apiClient<IStorage[]>('/storage-definitions')
}
