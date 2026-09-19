'use client'

import { useCallback, useEffect, useState } from 'react'
import { Save, AlertTriangle, Check, Lock } from 'lucide-react'
import { ApiError } from '@/api/api-client'
import { connectionsApi } from '@/api/connections.api'
import type { ConnectionSettings } from '@/types/connections'

interface InputFieldProps {
  label: string
  value: string | number
  onChange: (value: string) => void
  type?: 'text' | 'password' | 'number'
  placeholder?: string
}

const InputField = ({ label, value, onChange, type = 'text', placeholder }: InputFieldProps) => (
  <label className="flex flex-col gap-1">
    <span className="text-xs font-medium text-stone-600">{label}</span>
    <input
      type={type}
      value={value}
      placeholder={placeholder}
      onChange={(e) => onChange(e.target.value)}
      className="px-3 py-1.5 rounded-lg border-2 text-sm text-stone-800 outline-none focus:ring-2 focus:ring-amber-300"
      style={{ backgroundColor: '#fdf6ee', borderColor: '#d2b48c' }}
    />
  </label>
)

const CheckField = ({ label, checked, onChange }: { label: string; checked: boolean; onChange: (v: boolean) => void }) => (
  <label className="flex items-center gap-2 cursor-pointer select-none">
    <input type="checkbox" checked={checked} onChange={(e) => onChange(e.target.checked)} className="accent-amber-600" />
    <span className="text-xs font-medium text-stone-600">{label}</span>
  </label>
)

const Section = ({ title, children }: { title: string; children: React.ReactNode }) => (
  <div className="p-4 rounded-xl border-2" style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}>
    <div className="text-sm font-semibold text-stone-800 mb-3">{title}</div>
    {children}
  </div>
)

const PASSWORD_HINT = 'Пусто — не менять'

const ConnectionSettingsPanel = ({ onSaved }: { onSaved: () => void }) => {
  const [settings, setSettings] = useState<ConnectionSettings | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [saved, setSaved] = useState(false)
  const [needsAuth, setNeedsAuth] = useState(false)
  const [adminPassword, setAdminPassword] = useState('')
  const [authError, setAuthError] = useState<string | null>(null)
  const [isAuthenticating, setIsAuthenticating] = useState(false)

  const loadSettings = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const s = await connectionsApi.getSettings()
      setSettings(s)
      setNeedsAuth(false)
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        setNeedsAuth(true)
      } else {
        setError(err instanceof Error ? err.message : 'Не удалось загрузить настройки')
      }
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    loadSettings()
  }, [loadSettings])

  const handleAuth = useCallback(async () => {
    setIsAuthenticating(true)
    setAuthError(null)
    try {
      await connectionsApi.auth(adminPassword)
      await loadSettings()
    } catch (err) {
      setAuthError(err instanceof Error ? err.message : 'Ошибка входа')
    } finally {
      setIsAuthenticating(false)
    }
  }, [adminPassword, loadSettings])

  const patch = useCallback((mutator: (draft: ConnectionSettings) => void) => {
    setSettings((prev) => {
      if (!prev) return prev
      const draft = structuredClone(prev)
      mutator(draft)
      return draft
    })
    setSaved(false)
  }, [])

  const handleSave = useCallback(async () => {
    if (!settings) return
    setIsSaving(true)
    setError(null)
    try {
      const next = await connectionsApi.updateSettings(settings)
      setSettings(next)
      setSaved(true)
      onSaved()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Не удалось сохранить настройки')
    } finally {
      setIsSaving(false)
    }
  }, [settings, onSaved])

  if (needsAuth) {
    return (
      <div className="p-6 rounded-xl border-2" style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}>
        <div className="flex items-center gap-2 mb-4">
          <Lock size={18} className="text-stone-700" />
          <div className="text-sm font-semibold text-stone-800">Вход в настройки</div>
        </div>
        <p className="text-xs text-stone-600 mb-4">
          Для изменения подключений введите админ-пароль (env APP_ADMIN_PASSWORD).
        </p>
        <form
          className="flex flex-col sm:flex-row gap-2"
          onSubmit={(e) => {
            e.preventDefault()
            handleAuth()
          }}
        >
          <input
            type="password"
            value={adminPassword}
            onChange={(e) => setAdminPassword(e.target.value)}
            placeholder="Админ-пароль"
            className="flex-1 px-3 py-2 rounded-lg border-2 text-sm text-stone-800 outline-none focus:ring-2 focus:ring-amber-300"
            style={{ backgroundColor: '#fdf6ee', borderColor: '#d2b48c' }}
          />
          <button
            type="submit"
            disabled={isAuthenticating || !adminPassword}
            className="px-4 py-2 rounded-lg border-2 text-sm text-stone-800 shadow-sm transition hover:shadow-md disabled:opacity-60 disabled:cursor-not-allowed"
            style={{ backgroundColor: '#fdf6ee', borderColor: '#d2b48c' }}
          >
            {isAuthenticating ? 'Вход...' : 'Войти'}
          </button>
        </form>
        {authError && (
          <div className="flex items-center gap-2 mt-3 text-red-700 text-sm">
            <AlertTriangle size={14} />
            {authError}
          </div>
        )}
      </div>
    )
  }

  if (!settings && isLoading) {
    return (
      <div className="p-6 rounded-xl border-2 text-center text-stone-500" style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}>
        Загрузка настроек...
      </div>
    )
  }

  if (!settings) {
    return (
      <div className="flex items-center gap-2 p-4 rounded-xl border-2 text-red-800" style={{ backgroundColor: '#fdf6ee', borderColor: '#d2b48c' }}>
        <AlertTriangle size={18} className="text-red-600 shrink-0" />
        <span className="text-sm">{error ?? 'Настройки недоступны'}</span>
      </div>
    )
  }

  return (
    <div className="space-y-3">
      <Section title="PostgreSQL">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <InputField label="JDBC URL" value={settings.database.url} onChange={(v) => patch((d) => { d.database.url = v })} placeholder="jdbc:postgresql://host:5432/db" />
          <InputField label="Пользователь" value={settings.database.user} onChange={(v) => patch((d) => { d.database.user = v })} />
          <InputField label="Пароль" type="password" value={settings.database.password} onChange={(v) => patch((d) => { d.database.password = v })} placeholder={PASSWORD_HINT} />
          <InputField label="Размер пула" type="number" value={settings.database.poolSize} onChange={(v) => patch((d) => { d.database.poolSize = Number(v) || 0 })} />
        </div>
      </Section>

      <Section title="RabbitMQ">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <InputField label="Хост" value={settings.rabbitmq.host} onChange={(v) => patch((d) => { d.rabbitmq.host = v })} />
          <InputField label="Порт" type="number" value={settings.rabbitmq.port} onChange={(v) => patch((d) => { d.rabbitmq.port = Number(v) || 0 })} />
          <InputField label="Virtual host" value={settings.rabbitmq.vhost} onChange={(v) => patch((d) => { d.rabbitmq.vhost = v })} />
          <InputField label="Пользователь" value={settings.rabbitmq.user} onChange={(v) => patch((d) => { d.rabbitmq.user = v })} />
          <InputField label="Пароль" type="password" value={settings.rabbitmq.password} onChange={(v) => patch((d) => { d.rabbitmq.password = v })} placeholder={PASSWORD_HINT} />
          <InputField label="Exchange" value={settings.rabbitmq.exchange} onChange={(v) => patch((d) => { d.rabbitmq.exchange = v })} />
          <InputField label="Routing key" value={settings.rabbitmq.routingKey} onChange={(v) => patch((d) => { d.rabbitmq.routingKey = v })} />
        </div>
      </Section>

      <Section title="Внешние API">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <InputField label="Polynom base URL" value={settings.polynom.baseUrl} onChange={(v) => patch((d) => { d.polynom.baseUrl = v })} />
          <InputField label="Loodsman base URL" value={settings.loodsman.baseUrl} onChange={(v) => patch((d) => { d.loodsman.baseUrl = v })} />
        </div>
      </Section>

      <Section title="SMTP">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <CheckField label="Включён" checked={settings.email.enabled} onChange={(v) => patch((d) => { d.email.enabled = v })} />
          <CheckField label="TLS (465)" checked={settings.email.smtpTls} onChange={(v) => patch((d) => { d.email.smtpTls = v })} />
          <InputField label="SMTP хост" value={settings.email.smtpHost} onChange={(v) => patch((d) => { d.email.smtpHost = v })} />
          <InputField label="SMTP порт" type="number" value={settings.email.smtpPort} onChange={(v) => patch((d) => { d.email.smtpPort = Number(v) || 0 })} />
          <InputField label="От кого" value={settings.email.from} onChange={(v) => patch((d) => { d.email.from = v })} />
          <InputField label="Пароль" type="password" value={settings.email.password} onChange={(v) => patch((d) => { d.email.password = v })} placeholder={PASSWORD_HINT} />
          <InputField label="Кому" value={settings.email.to} onChange={(v) => patch((d) => { d.email.to = v })} />
        </div>
      </Section>

      <Section title="Планировщик синхронизации">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <CheckField label="Включён" checked={settings.scheduler.enabled} onChange={(v) => patch((d) => { d.scheduler.enabled = v })} />
          <InputField label="Интервал (мин)" type="number" value={settings.scheduler.intervalMinutes} onChange={(v) => patch((d) => { d.scheduler.intervalMinutes = Number(v) || 0 })} />
          <InputField label="Сервисный пользователь" value={settings.scheduler.serviceUser} onChange={(v) => patch((d) => { d.scheduler.serviceUser = v })} />
          <InputField label="Пароль сервисного пользователя" type="password" value={settings.scheduler.servicePassword} onChange={(v) => patch((d) => { d.scheduler.servicePassword = v })} placeholder={PASSWORD_HINT} />
          <InputField label="Storage ID" value={settings.scheduler.serviceStorageId} onChange={(v) => patch((d) => { d.scheduler.serviceStorageId = v })} />
        </div>
      </Section>

      {error && (
        <div className="flex items-center gap-2 p-4 rounded-xl border-2 text-red-800" style={{ backgroundColor: '#fdf6ee', borderColor: '#d2b48c' }}>
          <AlertTriangle size={18} className="text-red-600 shrink-0" />
          <span className="text-sm">{error}</span>
        </div>
      )}

      {saved && (
        <div className="flex items-center gap-2 p-4 rounded-xl border-2 text-green-800" style={{ backgroundColor: '#fdf6ee', borderColor: '#d2b48c' }}>
          <Check size={18} className="text-green-600 shrink-0" />
          <span className="text-sm">Настройки сохранены, переподключение запущено</span>
        </div>
      )}

      <div className="flex justify-end">
        <button
          type="button"
          onClick={handleSave}
          disabled={isSaving}
          className="flex items-center gap-2 px-4 py-2 rounded-lg border-2 text-stone-800 shadow-sm transition hover:shadow-md disabled:opacity-60 disabled:cursor-not-allowed"
          style={{ backgroundColor: '#fdf6ee', borderColor: '#d2b48c' }}
        >
          <Save size={16} />
          {isSaving ? 'Сохранение...' : 'Сохранить и переподключить'}
        </button>
      </div>
    </div>
  )
}

export { ConnectionSettingsPanel }
