import { apiClient } from './api-client'
import type { IElementGroup } from '@/shared/types/elementGroup.interface'
import type { IGroupContent } from '@/shared/types/groupContent.interface'

export function fetchElementGroups(
  catalogTypeId: number,
  catalogObjectId: number,
): Promise<IElementGroup[]> {
  return apiClient<IElementGroup[]>(
    `/groups?catalogTypeId=${catalogTypeId}&catalogObjectId=${catalogObjectId}`,
  )
}

export function fetchGroupContent(
  groupTypeId: number,
  groupObjectId: number,
): Promise<IGroupContent> {
  return apiClient<IGroupContent>(
    `/groups?groupTypeId=${groupTypeId}&groupObjectId=${groupObjectId}`,
  )
}
