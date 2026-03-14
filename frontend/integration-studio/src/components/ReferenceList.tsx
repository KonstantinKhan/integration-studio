'use client'

import { useState } from 'react'
import { Button } from 'primereact/button'
import { InputText } from 'primereact/inputtext'
import { useReferences } from '@/hooks/useReferences'
import { createReference } from '@/api/references.api'
import { useQueryClient } from '@tanstack/react-query'
import ReferenceCard from './ReferenceCard'

const ReferenceList = () => {
  const queryClient = useQueryClient()
  const { data: references = [], isLoading, error } = useReferences()

  const [newReferenceName, setNewReferenceName] = useState('')
  const [isCreating, setIsCreating] = useState(false)
  const [createError, setCreateError] = useState<string | null>(null)
  const [createSuccess, setCreateSuccess] = useState(false)

  const handleCreateReference = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!newReferenceName.trim()) return

    setIsCreating(true)
    setCreateError(null)
    setCreateSuccess(false)

    try {
      await createReference(newReferenceName.trim())
      setCreateSuccess(true)
      setNewReferenceName('')
      
      // Инвалидируем кэш справочников — React Query автоматически обновит данные
      await queryClient.invalidateQueries({ queryKey: ['references'] })
      
      setTimeout(() => {
        setCreateSuccess(false)
      }, 3000)
    } catch (err) {
      setCreateError('Не удалось создать справочник. Попробуйте еще раз.')
    } finally {
      setIsCreating(false)
    }
  }

  return (
    <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
      <div className="max-w-7xl mx-auto">
        <h1 className="text-4xl font-bold text-stone-800 mb-8 text-center">
          Справочники
        </h1>

        <div className="max-w-2xl mx-auto mb-8">
          <div
            className="p-6 rounded-xl border-2 shadow-md"
            style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
          >
            <div className="flex items-center gap-3 mb-4">
              <i
                className="pi pi-plus-circle text-2xl"
                style={{ color: '#8b4513' }}
              ></i>
              <h2 className="text-xl font-bold text-stone-800">
                Создать справочник
              </h2>
            </div>

            <form onSubmit={handleCreateReference} className="flex gap-3">
              <InputText
                value={newReferenceName}
                onChange={(e) => setNewReferenceName(e.target.value)}
                placeholder="Название справочника"
                className="flex-1"
                disabled={isCreating}
              />
              <Button
                type="submit"
                label="Создать"
                icon="pi pi-plus"
                loading={isCreating}
                disabled={!newReferenceName.trim() || isCreating}
              />
            </form>

            {createSuccess && (
              <div className="mt-3 p-3 bg-green-100 border border-green-400 text-green-700 rounded-lg flex items-center gap-2">
                <i className="pi pi-check-circle"></i>
                <span>Справочник успешно создан!</span>
              </div>
            )}

            {createError && (
              <div className="mt-3 p-3 bg-red-100 border border-red-400 text-red-700 rounded-lg flex items-center gap-2">
                <i className="pi pi-exclamation-circle"></i>
                <span>{createError}</span>
              </div>
            )}
          </div>
        </div>

        {/* Loading State */}
        {isLoading && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {[...Array(8)].map((_, index) => (
              <div
                key={index}
                className="p-6 rounded-xl border-2 shadow-md animate-pulse"
                style={{
                  backgroundColor: '#f4f1ea',
                  borderColor: '#d2b48c',
                }}
              >
                <div className="h-6 bg-stone-300 rounded w-3/4"></div>
              </div>
            ))}
          </div>
        )}

        {!isLoading && error && (
          <div className="max-w-md mx-auto p-6 bg-red-100 border-2 border-red-400 text-red-700 rounded-xl text-center">
            <i className="pi pi-exclamation-triangle text-3xl mb-3"></i>
            <p className="text-lg">Не удалось загрузить список справочников</p>
          </div>
        )}

        {!isLoading && !error && references.length === 0 && (
          <div
            className="max-w-md mx-auto p-8 rounded-xl border-2 shadow-md text-center"
            style={{
              backgroundColor: '#f4f1ea',
              borderColor: '#d2b48c',
            }}
          >
            <i className="pi pi-inbox text-5xl text-stone-400 mb-4"></i>
            <p className="text-xl text-stone-600">
              Нет доступных справочников
            </p>
          </div>
        )}

        {!isLoading && !error && references.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {references.map((reference, index) => (
              <ReferenceCard key={index} reference={reference} />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default ReferenceList
