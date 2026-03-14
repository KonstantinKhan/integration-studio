'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { InputText } from 'primereact/inputtext'
import { Password } from 'primereact/password'
import { Button } from 'primereact/button'
import { Card } from 'primereact/card'
import { Dropdown } from 'primereact/dropdown'
import { useStorages } from '@/hooks/useStorages'
import { useAuthorize } from '@/hooks/useAuthorize'
import { useAuthStore } from '@/store/auth.store'

interface FormData {
  username: string
  password: string
}

const LoginForm = () => {
  const [formData, setFormData] = useState<FormData>({
    username: '',
    password: '',
  })

  const [errors, setErrors] = useState<Partial<FormData>>({})
  const [submitted, setSubmitted] = useState(false)
  const [selectedStorage, setSelectedStorage] = useState<string>('')
  const [authError, setAuthError] = useState<string>('')
  const router = useRouter()

  const { data: storages = [], isLoading: storagesLoading } = useStorages()
  const authMutation = useAuthorize()
  const setSelectedStorageId = useAuthStore((s) => s.setSelectedStorageId)

  const validateField = (name: keyof FormData, value: string) => {
    let error = ''

    switch (name) {
      case 'username':
        if (!value.trim()) {
          error = 'Username is required'
        } else if (value.length < 3) {
          error = 'Username must be at least 3 characters'
        }
        break
      case 'password':
        if (!value) {
          error = 'Password is required'
        } else if (value.length < 6) {
          error = 'Password must be at least 6 characters'
        }
        break
      default:
        break
    }

    return error
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData({
      ...formData,
      [name]: value,
    })

    const error = validateField(name as keyof FormData, value)
    setErrors((prev) => ({
      ...prev,
      [name]: error || undefined,
    }))
  }

  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    const error = validateField(name as keyof FormData, value)
    setErrors((prev) => ({
      ...prev,
      [name]: error || undefined,
    }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    setSubmitted(true)
    setAuthError('')

    const newErrors: Partial<FormData> = {}

    Object.entries(formData).forEach(([key, value]) => {
      const error = validateField(key as keyof FormData, value as string)
      if (error) {
        newErrors[key as keyof FormData] = error
      }
    })

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors)
      return
    }

    if (!selectedStorage) {
      setAuthError('Please select a storage')
      return
    }

    const storageObj = storages.find((s) => s.displayName === selectedStorage)
    if (!storageObj) return

    authMutation.mutate(
      {
        login: formData.username,
        password: formData.password,
        storageId: storageObj.storageId,
      },
      {
        onSuccess: () => {
          setSelectedStorageId(storageObj.storageId)
          router.push('/polynom')
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
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-stone-100 via-amber-50 to-yellow-50">
      <Card
        className="w-full max-w-md p-6 shadow-lg rounded-xl border-2 border-stone-300"
        style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
      >
        <h2 className="text-2xl font-bold text-center mb-6 text-stone-800">
          Login to Account
        </h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label
              htmlFor="username"
              className="block text-sm font-medium text-stone-700 mb-1"
            >
              Username
            </label>
            <InputText
              id="username"
              name="username"
              value={formData.username}
              onChange={handleChange}
              onBlur={handleBlur}
              className={`w-full ${errors.username ? 'p-invalid' : ''}`}
              placeholder="Enter your username"
              style={{
                borderColor: '#d2b48c',
                backgroundColor: '#fdf6ee',
                boxSizing: 'border-box',
              }}
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
              Password
            </label>

            <Password
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              onBlur={handleBlur}
              inputClassName="w-full"
              className={`w-full ${errors.password ? 'p-invalid' : ''}`}
              inputStyle={{
                borderColor: '#d2b48c',
                backgroundColor: '#fdf6ee',
              }}
              placeholder="Enter your password"
              toggleMask
              feedback={false}
            />
          </div>
          {errors.password && (
            <small className="p-error block mt-1">{errors.password}</small>
          )}

          <div>
            <Dropdown
              className="w-full"
              value={selectedStorage}
              onChange={(e) => setSelectedStorage(e.value)}
              options={storages.map((s) => s.displayName)}
              optionLabel="storage"
              emptyMessage="Нет доступных хранилищ ПОЛИНОМ"
              disabled={storagesLoading}
              loading={storagesLoading}
            />
            {storagesLoading && (
              <small className="p-error block mt-1">
                {'Загрузка хранилищ ПОЛИНОМ'}
              </small>
            )}
          </div>

          {authError && (
            <div className="p-3 mb-3 bg-red-100 border border-red-400 text-red-700 rounded">
              {authError}
            </div>
          )}

          <Button
            type="submit"
            label="Login"
            loading={authMutation.isPending}
            disabled={authMutation.isPending}
            className="w-full mt-4"
            style={{
              backgroundColor: '#8b4513',
              borderColor: '#8b4513',
              color: 'white',
            }}
          />
        </form>
      </Card>
    </div>
  )
}

export default LoginForm
