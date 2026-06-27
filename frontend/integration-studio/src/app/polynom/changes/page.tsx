'use client'

import {
  memo,
  useMemo,
  useState,
  useSyncExternalStore,
  useEffect,
  useRef,
  useLayoutEffect,
} from 'react'
import { Calendar } from 'primereact/calendar'
import { Button } from 'primereact/button'
import { Dropdown } from 'primereact/dropdown'
import { useMigrationStream } from '@/hooks/useMigrationStream'
import { useNodes } from '@/hooks/useNodes'
import { useTreeRoot } from '@/hooks/useTreeRoot'
import type { EnrichedSearchResultItem } from '@/shared/types/enrichedSearchResultItem.interface'
import type { IPropertySearchRequest } from '@/shared/types/propertySearchRequest.interface'
import type { INode } from '@/shared/types/node.interface'

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

function propValue(item: EnrichedSearchResultItem, name: string): string {
  const prop = item.properties.find((p) => p.name === name)

  if (!prop) return ''

  const v = prop.value.data
  return typeof v === 'string' ? v : String(v)
}

interface MigrationRowProps {
  index: number
  code: string
  designation: string
}

/** Одна строка миграции. memo + стабильный key => не пересчитывается при добавлении новых. */
const MigrationRow = memo(function MigrationRow({
  index,
  code,
  designation,
}: MigrationRowProps) {
  return (
    <tr className="border-b border-stone-200 last:border-0">
      <td className="py-1.5 px-3 text-stone-500 text-sm tabular-nums">
        {index}
      </td>
      <td className="py-1.5 px-3 text-sm text-stone-700 break-all">
        {code || '—'}
      </td>
      <td className="py-1.5 px-3 text-sm text-stone-800 break-all">
        {designation || '—'}
      </td>
    </tr>
  )
})

const STORAGE_KEY = 'polynom.changes.selectedNode'

// Создаем хранилище для синхронизации с localStorage
function createStorageStore<T>(key: string, initialValue: T) {
  let currentValue: T = initialValue

  // Пытаемся загрузить значение при инициализации
  if (typeof window !== 'undefined') {
    try {
      const raw = localStorage.getItem(key)
      if (raw) {
        currentValue = JSON.parse(raw) as T
      }
    } catch {
      // ignore
    }
  }

  const listeners = new Set<() => void>()

  const subscribe = (callback: () => void) => {
    listeners.add(callback)

    // Подписываемся на событие storage для синхронизации между вкладками
    const handleStorage = (event: StorageEvent) => {
      if (event.key === key) {
        try {
          const newValue = event.newValue
            ? JSON.parse(event.newValue)
            : initialValue
          if (JSON.stringify(newValue) !== JSON.stringify(currentValue)) {
            currentValue = newValue
            listeners.forEach((l) => l())
          }
        } catch {
          // ignore
        }
      }
    }

    if (typeof window !== 'undefined') {
      window.addEventListener('storage', handleStorage)
    }

    return () => {
      listeners.delete(callback)
      if (typeof window !== 'undefined') {
        window.removeEventListener('storage', handleStorage)
      }
    }
  }

  const getSnapshot = () => currentValue

  const setValue = (value: T) => {
    const newValue = value
    if (JSON.stringify(newValue) !== JSON.stringify(currentValue)) {
      currentValue = newValue
      if (typeof window !== 'undefined') {
        localStorage.setItem(key, JSON.stringify(newValue))
      }
      listeners.forEach((l) => l())
    }
  }

  return { subscribe, getSnapshot, setValue }
}

type StoredNode = { name: string; typeId: number; objectId: number } | null

// Создаем хранилище для mounted состояния
const mountedStore = {
  subscribe: () => () => {},
  getSnapshot: () => true,
  getServerSnapshot: () => false,
}

// Создаем хранилище для узла
const nodeStore = createStorageStore<StoredNode>(STORAGE_KEY, null)

const ChangesPage = () => {
  const [from, setFrom] = useState<Date | null>(null)
  const [to, setTo] = useState<Date | null>(null)
  const [opened, setOpened] = useState(false)

  // Ref для предотвращения cascading renders
  const validationRef = useRef<{
    isValidated: boolean
    nodeKey: string | null
  }>({
    isValidated: false,
    nodeKey: null,
  })

  const mounted = useSyncExternalStore(
    mountedStore.subscribe,
    mountedStore.getSnapshot,
    mountedStore.getServerSnapshot,
  )

  const storedNode = useSyncExternalStore(
    nodeStore.subscribe,
    nodeStore.getSnapshot,
    () => null, // серверный снапшот
  )

  const [overrideNode, setOverrideNode] = useState<StoredNode | null>(null)
  const selectedNode = overrideNode ?? storedNode ?? null

  const { data: rootNode } = useTreeRoot()
  const { data: nodes = [], isLoading: nodesLoading } = useNodes(
    rootNode?.typeId,
    rootNode?.objectId,
    mounted && (opened || selectedNode != null),
  )

  // Валидация: проверяем, существует ли выбранный узел в загруженных данных
  useEffect(() => {
    // Если данные еще не загружены или нет выбранного узла - ничего не делаем
    if (!selectedNode || nodes.length === 0) return

    const nodeKey = `${selectedNode.typeId}:${selectedNode.objectId}`

    // Проверяем, не валидировали ли мы уже этот узел
    if (
      validationRef.current.isValidated &&
      validationRef.current.nodeKey === nodeKey
    ) {
      return
    }

    // Проверяем, существует ли узел с таким typeId и objectId в списке nodes или это rootNode
    const existsInNodes = nodes.some(
      (n) =>
        n.typeId === selectedNode.typeId &&
        n.objectId === selectedNode.objectId,
    )

    const isRootNode =
      rootNode &&
      rootNode.typeId === selectedNode.typeId &&
      rootNode.objectId === selectedNode.objectId

    // Если узел не найден ни в nodes, ни в rootNode - сбрасываем выбор
    if (!existsInNodes && !isRootNode) {
      console.log('Выбранный узел не найден на сервере, сбрасываем выбор')

      // Используем setTimeout для разрыва цикла рендеринга
      const timeoutId = setTimeout(() => {
        setOverrideNode(null)
        nodeStore.setValue(null)
        validationRef.current = { isValidated: true, nodeKey: null }
      }, 0)

      return () => clearTimeout(timeoutId)
    } else {
      validationRef.current = { isValidated: true, nodeKey: nodeKey }
    }
  }, [selectedNode, nodes, rootNode])

  const options = useMemo(() => {
    const list: (INode & { depth: number; key: string })[] = []
    const seen = new Set<string>()
    const push = (n: INode, depth: number) => {
      const key = `${n.typeId}:${n.objectId}`
      if (seen.has(key)) return
      seen.add(key)
      list.push({ ...n, depth, key })
    }
    if (rootNode) push(rootNode, 0)
    nodes.forEach((n) => push(n, 1))
    if (selectedNode) push(selectedNode, 1)
    return list
  }, [rootNode, nodes, selectedNode])

  const selectedKey = selectedNode
    ? `${selectedNode.typeId}:${selectedNode.objectId}`
    : rootNode
      ? `${rootNode.typeId}:${rootNode.objectId}`
      : null

  const nodeTemplate = (opt: INode & { depth: number }) => (
    <div style={{ paddingLeft: `${opt.depth * 1.5}rem` }}>{opt.name}</div>
  )

  const { status, processedCount, items, errorMessage, connecting, start } =
    useMigrationStream()

  const isRunning = status === 'RUNNING'
  const isTerminal = status === 'COMPLETED' || status === 'FAILED'

  const handleStart = () => {
    if (!from || !to) return
    void start(buildRequest(from, to))
  }

  const handleNodeChange = (key: string) => {
    const opt = options.find((o) => o.key === key)
    if (opt) {
      const node: StoredNode = {
        name: opt.name,
        typeId: opt.typeId,
        objectId: opt.objectId,
      }
      setOverrideNode(node)
      nodeStore.setValue(node)
      // Сбрасываем флаг валидации при ручном выборе
      validationRef.current = { isValidated: false, nodeKey: null }
    }
  }

  // Фиксируем размеры календарей после рендеринга
  const calendarRefs = useRef<{
    from: HTMLDivElement | null
    to: HTMLDivElement | null
  }>({
    from: null,
    to: null,
  })

  useLayoutEffect(() => {
    // После монтирования фиксируем размеры контейнеров календарей
    const fromEl = calendarRefs.current.from
    const toEl = calendarRefs.current.to

    if (fromEl) {
      const rect = fromEl.getBoundingClientRect()
      if (rect.width > 0) {
        fromEl.style.minWidth = `${rect.width}px`
        fromEl.style.minHeight = `${rect.height}px`
      }
    }

    if (toEl) {
      const rect = toEl.getBoundingClientRect()
      if (rect.width > 0) {
        toEl.style.minWidth = `${rect.width}px`
        toEl.style.minHeight = `${rect.height}px`
      }
    }
  }, [])

  return (
    <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
      <div className="max-w-7xl mx-auto">
        <h1 className="text-4xl font-bold text-stone-800 mb-8 text-center">
          Миграция изменённых объектов
        </h1>

        <div className="mb-6">
          <span className="block text-sm font-medium text-stone-700 mb-1 ml-3">
            Область поиска
          </span>
          {mounted ? (
            <Dropdown
              className="w-80 h-12"
              value={selectedKey}
              onChange={(e) => handleNodeChange(e.value)}
              options={options}
              optionLabel="name"
              optionValue="key"
              itemTemplate={nodeTemplate}
              emptyMessage="Нет справочников"
              disabled={!rootNode}
              loading={nodesLoading && opened}
              onShow={() => setOpened(true)}
            />
          ) : (
            <div className="w-80 h-12 rounded-md border border-stone-300 bg-stone-100 animate-pulse" />
          )}
        </div>

        <div
          className="max-w-3xl mx-auto mb-6 p-6 rounded-xl border-2 shadow-md flex flex-wrap items-end gap-4"
          style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
        >
          <div
            ref={(el) => {
              calendarRefs.current.from = el
            }}
            className="flex flex-col gap-1"
            style={{ minWidth: '200px', width: 'auto' }}
          >
            <label className="text-sm font-medium text-stone-700">От</label>
            <div style={{ position: 'relative', display: 'inline-block' }}>
              <Calendar
                value={from}
                onChange={(e) => setFrom(e.value as Date | null)}
                dateFormat="dd.mm.yy"
                showIcon
              />
            </div>
          </div>
          <div
            ref={(el) => {
              calendarRefs.current.to = el
            }}
            className="flex flex-col gap-1"
            style={{ minWidth: '200px', width: 'auto' }}
          >
            <label className="text-sm font-medium text-stone-700">До</label>
            <div style={{ position: 'relative', display: 'inline-block' }}>
              <Calendar
                value={to}
                onChange={(e) => setTo(e.value as Date | null)}
                dateFormat="dd.mm.yy"
                showIcon
              />
            </div>
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
          <div
            className="max-w-3xl mx-auto mb-6 p-4 rounded-xl border-2 shadow-sm flex items-center gap-4"
            style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
          >
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
          <div
            className="max-w-md mx-auto p-8 rounded-xl border-2 shadow-md text-center"
            style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
          >
            <i className="pi pi-inbox text-5xl text-stone-400 mb-4"></i>
            <p className="text-xl text-stone-600">
              Миграция завершена. Объекты не стримились в текущей сессии
              (tail-only: данные, отправленные до подключения, не
              восстанавливаются).
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
          <div
            className="max-w-5xl mx-auto rounded-xl border-2 shadow-md overflow-hidden"
            style={{ backgroundColor: '#fff', borderColor: '#d2b48c' }}
          >
            <table className="w-full border-collapse">
              <thead className="bg-stone-100 text-left">
                <tr>
                  <th className="py-2 px-3 text-xs font-semibold text-stone-600 uppercase tracking-wide w-16">
                    №
                  </th>
                  <th className="py-2 px-3 text-xs font-semibold text-stone-600 uppercase tracking-wide">
                    Код классификатора
                  </th>
                  <th className="py-2 px-3 text-xs font-semibold text-stone-600 uppercase tracking-wide">
                    Обозначение
                  </th>
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
