'use client'

import { memo, useState } from 'react'
import { Calendar } from 'primereact/calendar'
import { Button } from 'primereact/button'
import { useMigrationStream } from '@/hooks/useMigrationStream'
import type { EnrichedSearchResultItem } from '@/shared/types/enrichedSearchResultItem.interface'
import type { IPropertySearchRequest } from '@/shared/types/propertySearchRequest.interface'

function pad(n: number): string {
  return n.toString().padStart(2, '0')
}

function formatDate(date: Date): string {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T00:00:00.000`
}

function buildRequest(from: Date, to: Date): IPropertySearchRequest {
  const fromStr = formatDate(from)
  const toStr = formatDate(to)
  return {
    pageNumber: 1,
    pageSize: 100,
    ownerScope: { objectId: 61, typeId: 48 },
    condition: {
      enabled: true,
      intersectionType: 0,
      complexConditions: [
        {
          enabled: false,
          intersectionType: 0,
          simpleConditions: [
            {
              enabled: true,
              searchConditionTargetQualifier: { objectId: 40, typeId: 54 },
              operation: 6,
              options: 0,
              value: { typeId: 0, objectId: 0 },
            },
            {
              enabled: true,
              searchConditionTargetQualifier: { objectId: 40, typeId: 54 },
              operation: 4,
              options: 0,
              value: { typeId: 0, objectId: 1 },
            },
          ],
        },
      ],
    },
    values: {
      dateTimeProperties: [
        {
          objectId: 0,
          typeId: 0,
          value: {
            objectId: 0,
            dataType: 6,
            value: toStr,
            valueFrom: { value: fromStr, useTime: false },
            valueTo: { value: toStr, useTime: false },
            valueSingle: { value: toStr, useTime: false },
            useTime: false,
          },
        },
        {
          objectId: 1,
          typeId: 0,
          value: {
            objectId: 1,
            dataType: 6,
            value: toStr,
            valueFrom: { value: fromStr, useTime: false },
            valueTo: { value: toStr, useTime: false },
            valueSingle: { value: toStr, useTime: false },
            useTime: false,
          },
        },
      ],
    },
  }
}

const STATUS_LABEL: Record<string, string> = {
  RUNNING: 'Выполняется',
  COMPLETED: 'Завершено',
  FAILED: 'Ошибка',
}

const PROP_CLASSIFIER_CODE = 'Код классификатора'
const PROP_DESIGNATION = 'Обозначение'

/** Извлечь строковое значение свойства по имени. */
function propValue(item: EnrichedSearchResultItem, name: string): string {
  const prop = item.properties.find((p) => p.name === name)
  if (!prop) return ''
  const v = prop.value.value
  return typeof v === 'string' ? v : String(v)
}

interface MigrationRowProps {
  index: number
  code: string
  designation: string
}

/** Одна строка миграции. memo + стабильный key => не пересчитывается при добавлении новых. */
const MigrationRow = memo(function MigrationRow({ index, code, designation }: MigrationRowProps) {
  return (
    <tr className="border-b border-stone-200 last:border-0">
      <td className="py-1.5 px-3 text-stone-500 text-sm tabular-nums">{index}</td>
      <td className="py-1.5 px-3 text-sm text-stone-700 break-all">{code || '—'}</td>
      <td className="py-1.5 px-3 text-sm text-stone-800 break-all">{designation || '—'}</td>
    </tr>
  )
})

const ChangesPage = () => {
  const [from, setFrom] = useState<Date | null>(null)
  const [to, setTo] = useState<Date | null>(null)

  const {
    status,
    processedCount,
    items,
    errorMessage,
    connecting,
    start,
  } = useMigrationStream()

  const isRunning = status === 'RUNNING'
  const isTerminal = status === 'COMPLETED' || status === 'FAILED'

  const handleStart = () => {
    if (!from || !to) return
    void start(buildRequest(from, to))
  }

  return (
    <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
      <div className="max-w-7xl mx-auto">
        <h1 className="text-4xl font-bold text-stone-800 mb-8 text-center">
          Миграция изменённых объектов
        </h1>

        <div className="max-w-3xl mx-auto mb-6 p-6 rounded-xl border-2 shadow-md flex flex-wrap items-end gap-4"
          style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}>
          <div className="flex flex-col gap-1">
            <label className="text-sm font-medium text-stone-700">От</label>
            <Calendar
              value={from}
              onChange={(e) => setFrom(e.value as Date | null)}
              dateFormat="dd.mm.yy"
              showIcon
            />
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-sm font-medium text-stone-700">До</label>
            <Calendar
              value={to}
              onChange={(e) => setTo(e.value as Date | null)}
              dateFormat="dd.mm.yy"
              showIcon
            />
          </div>
          <Button
            label="Запустить миграцию"
            icon="pi pi-play"
            loading={connecting}
            disabled={!from || !to || connecting || isRunning}
            onClick={handleStart}
          />
        </div>

        {(status || connecting) && (
          <div className="max-w-3xl mx-auto mb-6 p-4 rounded-xl border-2 shadow-sm flex items-center gap-4"
            style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}>
            <span className="text-sm font-medium text-stone-700">Статус:</span>
            <span
              className={`text-sm font-semibold ${
                status === 'FAILED'
                  ? 'text-red-600'
                  : status === 'COMPLETED'
                    ? 'text-green-700'
                    : 'text-amber-700'
              }`}
            >
              {connecting ? 'Подключение…' : STATUS_LABEL[status ?? 'RUNNING']}
            </span>
            <span className="text-sm text-stone-600">
              Обработано: <b>{processedCount}</b>
            </span>
            {isRunning && (
              <span className="text-xs text-stone-500">
                (стрим идёт; при обновлении страницы подключение восстановится)
              </span>
            )}
          </div>
        )}

        {isTerminal && items.length === 0 && !errorMessage && (
          <div className="max-w-md mx-auto p-8 rounded-xl border-2 shadow-md text-center"
            style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}>
            <i className="pi pi-inbox text-5xl text-stone-400 mb-4"></i>
            <p className="text-xl text-stone-600">
              Миграция завершена. Объекты не стримились в текущей сессии
              (tail-only: данные, отправленные до подключения, не восстанавливаются).
            </p>
          </div>
        )}

        {errorMessage && (
          <div className="max-w-md mx-auto p-6 bg-red-100 border-2 border-red-400 text-red-700 rounded-xl text-center">
            <i className="pi pi-exclamation-triangle text-3xl mb-3"></i>
            <p className="text-lg">{errorMessage}</p>
          </div>
        )}

        {items.length > 0 && (
          <div className="max-w-5xl mx-auto rounded-xl border-2 shadow-md overflow-hidden"
            style={{ backgroundColor: '#fff', borderColor: '#d2b48c' }}>
            <table className="w-full border-collapse">
              <thead className="bg-stone-100 text-left">
                <tr>
                  <th className="py-2 px-3 text-xs font-semibold text-stone-600 uppercase tracking-wide w-16">№</th>
                  <th className="py-2 px-3 text-xs font-semibold text-stone-600 uppercase tracking-wide">Код классификатора</th>
                  <th className="py-2 px-3 text-xs font-semibold text-stone-600 uppercase tracking-wide">Обозначение</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item, i) => (
                  <MigrationRow
                    key={`${item.typeId}-${item.objectId}`}
                    index={i + 1}
                    code={propValue(item, PROP_CLASSIFIER_CODE)}
                    designation={propValue(item, PROP_DESIGNATION)}
                  />
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

export default ChangesPage
