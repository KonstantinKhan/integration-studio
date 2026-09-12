export interface ConnectionStatus {
  id: string
  name: string
  host: string
  port: number
  status: 'connected' | 'unreachable' | 'disabled'
  latencyMs: number | null
  error: string | null
}
