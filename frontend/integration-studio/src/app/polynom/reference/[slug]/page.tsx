'use client'

import { use } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from 'primereact/button'
import { Card } from 'primereact/card'
import { useReferenceDetails } from '@/hooks/useReferenceDetails'
import { useCatalogs } from '@/hooks/useCatalogs'

export default function ReferencePage({
  params,
}: {
  params: Promise<{ slug: string }>
}) {
  const { slug } = use(params)
  const [typeId, objectId] = slug.split('-').map(Number)
  const router = useRouter()

  const {
    data: reference,
    isLoading,
    error,
  } = useReferenceDetails(typeId, objectId)
  const {
    data: catalogs = [],
    isLoading: catalogsLoading,
    error: catalogsError,
  } = useCatalogs(typeId, objectId)

  if (isLoading) {
    return (
      <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
        <div className="max-w-2xl mx-auto">
          <div
            className="p-8 rounded-xl border-2 shadow-md animate-pulse"
            style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
          >
            <div className="h-8 bg-stone-300 rounded w-1/2 mb-6"></div>
            <div className="h-5 bg-stone-300 rounded w-1/3 mb-3"></div>
            <div className="h-5 bg-stone-300 rounded w-1/4"></div>
          </div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
        <div className="max-w-md mx-auto p-6 bg-red-100 border-2 border-red-400 text-red-700 rounded-xl text-center">
          <i className="pi pi-exclamation-triangle text-3xl mb-3"></i>
          <p className="text-lg">Не удалось загрузить данные справочника</p>
          <Button
            label="Назад"
            icon="pi pi-arrow-left"
            className="mt-4"
            severity="secondary"
            onClick={() => router.push('/polynom')}
          />
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
      <div className="max-w-2xl mx-auto">
        <Button
          label="Назад"
          icon="pi pi-arrow-left"
          className="mb-6"
          severity="secondary"
          text
          onClick={() => router.push('/polynom')}
        />

        <Card
          className="shadow-md rounded-xl border-2"
          style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
        >
          <div className="flex items-center gap-3 mb-6">
            <i className="pi pi-book text-3xl" style={{ color: '#8b4513' }}></i>
            <h1 className="text-3xl font-bold text-stone-800">
              {reference?.name}
            </h1>
          </div>

          <div className="space-y-3 text-stone-700">
            <p>
              <span className="font-medium">Type ID:</span> {reference?.typeId}
            </p>
            <p>
              <span className="font-medium">Object ID:</span>{' '}
              {reference?.objectId}
            </p>
          </div>
        </Card>

        <h2 className="text-2xl font-bold text-stone-800 mt-8 mb-4">
          Каталоги
        </h2>

        {catalogsLoading && (
          <div className="space-y-3">
            {[...Array(3)].map((_, i) => (
              <div
                key={i}
                className="p-4 rounded-xl border-2 animate-pulse"
                style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
              >
                <div className="h-5 bg-stone-300 rounded w-1/3"></div>
              </div>
            ))}
          </div>
        )}

        {catalogsError && (
          <div className="p-4 bg-red-100 border-2 border-red-400 text-red-700 rounded-xl text-center">
            <p>Не удалось загрузить каталоги</p>
          </div>
        )}

        {!catalogsLoading && !catalogsError && catalogs.length === 0 && (
          <div
            className="p-6 rounded-xl border-2 text-center"
            style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
          >
            <p className="text-stone-600">Нет доступных каталогов</p>
          </div>
        )}

        {!catalogsLoading && !catalogsError && catalogs.length > 0 && (
          <div className="space-y-3">
            {catalogs.map((catalog) => (
              <div
                key={catalog.id}
                className="p-4 rounded-xl border-2 shadow-sm hover:shadow-xl transition-all duration-300 hover:scale-105 cursor-pointer"
                style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
                onClick={() => router.push(`/polynom/reference/${slug}/catalog/${catalog.typeId}-${catalog.objectId}`)}
              >
                <div className="flex items-center gap-3">
                  <i
                    className="pi pi-folder text-xl"
                    style={{ color: '#8b4513' }}
                  ></i>
                  <span className="font-semibold text-stone-800">
                    {catalog.name}
                  </span>
                </div>
                <div className="mt-2 text-sm text-stone-600 flex gap-4">
                  <span>Type ID: {catalog.typeId}</span>
                  <span>Object ID: {catalog.objectId}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
