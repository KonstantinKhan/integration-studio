import { apiClient } from './api-client'
import type { INode } from '@/shared/types/node.interface'

export function fetchTreeRoot(): Promise<INode> {
  return apiClient<INode>('/tree/root')
}

export function fetchNodes(typeId: number, objectId: number): Promise<INode[]> {
  return apiClient<INode[]>(`/tree/nodes?typeId=${typeId}&objectId=${objectId}`)
}
