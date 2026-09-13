'use client'

import { useState } from 'react'
import Link from 'next/link'
import { useRouter } from 'next/navigation'
import { Check } from 'lucide-react'
import { InputText } from 'primereact/inputtext'
import { Password } from 'primereact/password'
import { Button } from 'primereact/button'
import { Card } from 'primereact/card'
import { useLoodsmanAuthorize } from '@/hooks/useLoodsmanAuthorize'
import { useAuthStore } from '@/store/auth.store'
import LoodsmanDatabaseDropdown from '@/components/LoodsmanDatabaseDropdown'

interface AuthFields {
  username: string
  password: string
}

const STEPS = [
  { id: 1, label: 'База данных' },
  { id: 2, label: 'Авторизация' },
] as const

type StepId = (typeof STEPS)[number]['id']

const primaryButtonStyle = {
  backgroundColor: '#8b4513',
  borderColor: '#8b4513',
  color: 'white',
} as const

const inputStyle = {
  borderColor: '#d2b48c',
  backgroundColor: '#fdf6ee',
  boxSizing: 'border-box',
} as const

const LoodsmanAuthPage = () => {
  const [step, setStep] = useState<StepId>(1)
  const [formData, setFormData] = useState<AuthFields>({
    username: '',
    password: '',
  })
  const [errors, setErrors] = useState<Partial<AuthFields>>({})
  const [authError, setAuthError] = useState<string>('')
  const router = useRouter()

  const selectedLoodsmanDb = useAuthStore((s) => s.selectedLoodsmanDb)
  const setSelectedLoodsmanDb = useAuthStore((s) => s.setSelectedLoodsmanDb)
  const authMutation = useLoodsmanAuthorize()

  const validateUsername = (value: string) => {
    if (!value.trim()) {
      return 'Username is required'
    }
    if (value.length < 3) {
      return 'Username must be at least 3 characters'
    }
    return ''
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))

    if (name === 'username') {
      const error = validateUsername(value)
      setErrors((prev) => ({
        ...prev,
        username: error || undefined,
      }))
    }
  }

  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const { name, value } = e.target

    if (name === 'username') {
      const error = validateUsername(value)
      setErrors((prev) => ({
        ...prev,
        username: error || undefined,
      }))
    }
  }

  const goBackToDatabase = () => {
    setAuthError('')
    setStep(1)
  }

  const handleNext = () => {
    setAuthError('')
    setStep(2)
  }

  const handleSubmit = (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault()
    setAuthError('')

    if (!selectedLoodsmanDb) {
      setAuthError('Выберите базу данных')
      setStep(1)
      return
    }

    if (authMutation.isPending) return

    const usernameError = validateUsername(formData.username)
    if (usernameError) {
      setErrors({ username: usernameError })
      return
    }

    authMutation.mutate(
      {
        dbName: selectedLoodsmanDb,
        username: formData.username,
        password: formData.password,
      },
      {
        onSuccess: () => {
          router.push('/loodsman')
        },
        onError: (error) => {
          setAuthError(
            error.message ||
              'Authorization failed. Please check your credentials.',
          )
        },
      },
    )
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50">
      <Link
        href="/"
        className="self-start w-full max-w-md mb-3 text-sm text-stone-500 hover:text-stone-700 transition-colors"
      >
        ← На главную
      </Link>
      <Card
        className="w-full max-w-md p-6 shadow-lg rounded-xl border-2 border-stone-300"
        style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
      >
        <h2 className="text-2xl font-bold text-center mb-6 text-stone-800">
          Подключение к Loodsman
        </h2>

        <div className="flex items-center justify-center mb-6">
          {STEPS.map(({ id, label }, index) => (
            <div key={id} className="flex items-center">
              <div className="flex items-center gap-2">
                <span
                  className={`inline-flex items-center justify-center w-7 h-7 rounded-full text-sm font-semibold ${
                    id === step
                      ? 'text-white'
                      : id < step
                        ? 'bg-stone-300 text-stone-600'
                        : 'bg-stone-200 text-stone-500'
                  }`}
                  style={id === step ? { backgroundColor: '#8b4513' } : undefined}
                >
                  {id < step ? <Check size={16} /> : id}
                </span>
                <span
                  className={`text-sm whitespace-nowrap ${
                    id === step
                      ? 'font-semibold text-stone-800'
                      : 'text-stone-500'
                  }`}
                >
                  {id}. {label}
                </span>
              </div>
              {index < STEPS.length - 1 && (
                <span className="w-6 h-px bg-stone-300 mx-2" />
              )}
            </div>
          ))}
        </div>

        {step === 1 && (
          <div className="space-y-4">
            <div>
              <label
                htmlFor="database"
                className="block text-sm font-medium text-stone-700 mb-1"
              >
                База данных Loodsman
              </label>
              <LoodsmanDatabaseDropdown
                id="database"
                value={selectedLoodsmanDb}
                onChange={setSelectedLoodsmanDb}
                className="w-full"
              />
            </div>

            <Button
              type="button"
              label="Далее"
              disabled={!selectedLoodsmanDb}
              onClick={handleNext}
              className="w-full mt-4"
              style={primaryButtonStyle}
            />
          </div>
        )}

        {step === 2 && (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div
              className="flex items-center justify-between gap-2 p-2.5 rounded-lg border-2"
              style={{ backgroundColor: '#fdf6ee', borderColor: '#d2b48c' }}
            >
              <span className="text-sm font-medium text-stone-700 truncate">
                {selectedLoodsmanDb}
              </span>
              <button
                type="button"
                onClick={goBackToDatabase}
                className="text-sm font-medium underline shrink-0 cursor-pointer hover:opacity-80 transition"
                style={{ color: '#8b4513' }}
              >
                Изменить
              </button>
            </div>

            <div>
              <label
                htmlFor="username"
                className="block text-sm font-medium text-stone-700 mb-1"
              >
                Логин
              </label>
              <InputText
                id="username"
                name="username"
                value={formData.username}
                onChange={handleChange}
                onBlur={handleBlur}
                className={`w-full ${errors.username ? 'p-invalid' : ''}`}
                placeholder="Enter your username"
                style={inputStyle}
              />
              {errors.username && (
                <small className="p-error block mt-1">{errors.username}</small>
              )}
            </div>

            <div className="w-full">
              <label
                htmlFor="password"
                className="block text-sm font-medium text-stone-700 mb-1"
              >
                Пароль
              </label>
              <Password
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                inputClassName="w-full"
                className="w-full"
                inputStyle={{
                  borderColor: '#d2b48c',
                  backgroundColor: '#fdf6ee',
                }}
                placeholder="Enter your password"
                toggleMask
                feedback={false}
              />
            </div>

            {authError && (
              <div className="p-3 bg-red-100 border border-red-400 text-red-700 rounded">
                {authError}
              </div>
            )}

            <div className="flex gap-2 mt-4">
              <Button
                type="button"
                label="Назад"
                outlined
                onClick={goBackToDatabase}
                className="w-full"
                style={{ borderColor: '#8b4513', color: '#8b4513' }}
              />
              <Button
                type="submit"
                label="Войти"
                loading={authMutation.isPending}
                disabled={authMutation.isPending}
                className="w-full"
                style={primaryButtonStyle}
              />
            </div>
          </form>
        )}
      </Card>
    </div>
  )
}

export default LoodsmanAuthPage
