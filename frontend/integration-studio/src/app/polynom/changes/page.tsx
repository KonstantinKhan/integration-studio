'use client'

import { useState } from 'react'
import { Calendar } from 'primereact/calendar'
import { Button } from 'primereact/button'
import { useChangedObjects } from '@/hooks/useChangedObjects'
import ChangedObjectCard from '@/components/ChangedObjectCard'

const ChangesPage = () => {
  const [from, setFrom] = useState<Date | null>(null)
  const [to, setTo] = useState<Date | null>(null)
  const [enabled, setEnabled] = useState(false)

  const { data: items = [], isLoading, error } = useChangedObjects(from, to, enabled)

  const handleSearch = () => {
    if (!from || !to) return
    setEnabled(true)
  }

  return (
    <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
      <div className="max-w-7xl mx-auto">
        <h1 className="text-4xl font-bold text-stone-800 mb-8 text-center">
          Изменённые объекты
        </h1>

        <div
          className="max-w-3xl mx-auto mb-8 p-6 rounded-xl border-2 shadow-md flex flex-wrap items-end gap-4"
          style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
        >
          <div className="flex flex-col gap-1">
            <label className="text-sm font-medium text-stone-700">От</label>
            <Calendar
              value={from}
              onChange={(e) => { setFrom(e.value as Date | null); setEnabled(false) }}
              dateFormat="dd.mm.yy"
              showIcon
            />
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-sm font-medium text-stone-700">До</label>
            <Calendar
              value={to}
              onChange={(e) => { setTo(e.value as Date | null); setEnabled(false) }}
              dateFormat="dd.mm.yy"
              showIcon
            />
          </div>
          <Button
            label="Найти"
            icon="pi pi-search"
            loading={isLoading}
            disabled={!from || !to || isLoading}
            onClick={handleSearch}
          />
        </div>

        {enabled && isLoading && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[...Array(6)].map((_, i) => (
              <div
                key={i}
                className="p-5 rounded-xl border-2 shadow-md animate-pulse h-40"
                style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
              />
            ))}
          </div>
        )}

        {enabled && error && (
          <div className="max-w-md mx-auto p-6 bg-red-100 border-2 border-red-400 text-red-700 rounded-xl text-center">
            <i className="pi pi-exclamation-triangle text-3xl mb-3"></i>
            <p className="text-lg">Не удалось загрузить данные</p>
          </div>
        )}

        {enabled && !isLoading && !error && items.length === 0 && (
          <div
            className="max-w-md mx-auto p-8 rounded-xl border-2 shadow-md text-center"
            style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
          >
            <i className="pi pi-inbox text-5xl text-stone-400 mb-4"></i>
            <p className="text-xl text-stone-600">Нет изменённых объектов</p>
          </div>
        )}

        {enabled && !isLoading && !error && items.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {items.map((item) => (
              <ChangedObjectCard
                key={`${item.typeId}-${item.objectId}`}
                item={item}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default ChangesPage
