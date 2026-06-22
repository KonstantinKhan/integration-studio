'use client'

import { useState } from 'react'
import { Button } from 'primereact/button'
import { apiClient } from '@/api/api-client'

const Header = () => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleTest = async (concurrency: number, count = 100) => {
    setLoading(true)
    setError(null)

    try {
      await apiClient<string>(`/test?groupTypeId=39&groupObjectId=23&count=${count}&concurrency=${concurrency}`)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Request failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <header
      className="flex items-center justify-between px-8 py-4 border-b-2 shadow-sm"
      style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
    >
      <h1 className="text-xl font-bold text-stone-800">Integration Studio</h1>

      <div className="flex items-center gap-4">
        {error && (
          <span className="text-sm text-red-600">{error}</span>
        )}
        {[1, 3, 4, 5, 6].map((c) => (
          <Button
            key={c}
            label={`C=${c}`}
            icon="pi pi-bolt"
            size="small"
            loading={loading}
            onClick={() => handleTest(c)}
          />
        ))}
      </div>
    </header>
  )
}

export default Header
