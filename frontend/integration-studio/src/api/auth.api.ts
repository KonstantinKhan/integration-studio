import { apiClient } from './api-client'
import type { IAuth } from '@/shared/types/auth.interface'

export function authorize(data: IAuth): Promise<unknown> {
  return apiClient('/authorize', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}
