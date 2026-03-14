import { API_BASE_URL } from '@/config/api'

export class ApiError extends Error {
  constructor(
    public status: number,
    message: string,
  ) {
    super(message)
    this.name = 'ApiError'
  }
}

interface RequestOptions extends Omit<RequestInit, 'credentials'> {}

export async function apiClient<T>(
  endpoint: string,
  options: RequestOptions = {},
): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new ApiError(response.status, errorData.message ?? 'Request failed')
  }

  // Handle empty responses (e.g., 204 No Content or empty 200 OK)
  const contentLength = response.headers.get('content-length')
  if (contentLength === '0' || !contentLength) {
    const text = await response.text()
    if (!text) {
      return undefined as T
    }
  }

  return response.json() as Promise<T>
}
