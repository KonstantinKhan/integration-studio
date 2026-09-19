import { apiClient } from '@/api/api-client'
import type { ConnectionSettings, ConnectionStatus } from '@/types/connections'

export const connectionsApi = {
  getStatuses: (signal?: AbortSignal) =>
    apiClient<ConnectionStatus[]>('/connections', { signal }),

  auth: (password: string) =>
    apiClient<{ username: string }>('/connections/auth', {
      method: 'POST',
      body: JSON.stringify({ password }),
    }),

  getSettings: (signal?: AbortSignal) =>
    apiClient<ConnectionSettings>('/connections/settings', { signal }),

  updateSettings: (settings: ConnectionSettings) =>
    apiClient<ConnectionSettings>('/connections/settings', {
      method: 'PUT',
      body: JSON.stringify(settings),
    }),
}
