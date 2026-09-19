export interface ConnectionStatus {
  id: string
  name: string
  host: string
  port: number
  status: 'connected' | 'unreachable' | 'disabled' | 'connecting'
  latencyMs: number | null
  error: string | null
}

export interface ConnectionSettings {
  database: {
    url: string
    user: string
    password: string
    poolSize: number
  }
  rabbitmq: {
    host: string
    port: number
    vhost: string
    user: string
    password: string
    exchange: string
    routingKey: string
  }
  email: {
    enabled: boolean
    smtpHost: string
    smtpPort: number
    smtpTls: boolean
    from: string
    password: string
    to: string
  }
  scheduler: {
    enabled: boolean
    intervalMinutes: number
    scopeTypeId: number
    scopeObjectId: number
    serviceUser: string
    servicePassword: string
    serviceStorageId: string
    externalApiTimezoneOffsetMinutes: number
  }
  polynom: {
    baseUrl: string
  }
  loodsman: {
    baseUrl: string
  }
}
