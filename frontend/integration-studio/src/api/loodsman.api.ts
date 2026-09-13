import { apiClient } from './api-client'
import type { ILoodsmanDatabase } from '@/shared/types/loodsmanDatabase.interface'
import type { ILoodsmanAuth } from '@/shared/types/loodsmanAuth.interface'
import type { ILoodsmanTreeNode } from '@/shared/types/loodsmanTreeNode.interface'

export function fetchLoodsmanDatabases(): Promise<ILoodsmanDatabase[]> {
  return apiClient<ILoodsmanDatabase[]>('/loodsman/databases', {
    signal: AbortSignal.timeout(10_000),
  })
}

export function loodsmanAuthorize(data: ILoodsmanAuth): Promise<unknown> {
  return apiClient('/loodsman/authorize', {
    method: 'POST',
    body: JSON.stringify(data),
    signal: AbortSignal.timeout(10_000),
  })
}

export function loodsmanLogout(): Promise<unknown> {
  return apiClient('/loodsman/logout', {
    method: 'POST',
    signal: AbortSignal.timeout(10_000),
  })
}

export function fetchLoodsmanTreeRoot(): Promise<ILoodsmanTreeNode[]> {
  return apiClient<ILoodsmanTreeNode[]>('/loodsman/tree/root', {
    signal: AbortSignal.timeout(10_000),
  })
}
