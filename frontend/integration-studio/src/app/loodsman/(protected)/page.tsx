'use client'

import { useRouter } from 'next/navigation'
import { useEffect, useState } from 'react'
import { ProgressSpinner } from 'primereact/progressspinner'
import { InputText } from 'primereact/inputtext'
import { Button } from 'primereact/button'
import { LogOut, Map, Search } from 'lucide-react'
import { useQueryClient } from '@tanstack/react-query'
import { ApiError } from '@/api/api-client'
import { loodsmanLogout } from '@/api/loodsman.api'
import { useLoodsmanTreeRoot } from '@/hooks/useLoodsmanTreeRoot'
import { useLoodsmanObjectInfo } from '@/hooks/useLoodsmanObjectInfo'
import LoodsmanObjectCard from '@/components/LoodsmanObjectCard'
import type { ILoodsmanTreeNode } from '@/shared/types/loodsmanTreeNode.interface'

const cardStyle = { backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }
const iconBoxStyle = { backgroundColor: '#fdf6ee', border: '1px solid #d2b48c' }
const inputStyle = {
  borderColor: '#d2b48c',
  backgroundColor: '#fdf6ee',
  boxSizing: 'border-box',
} as const
const primaryButtonStyle = {
  backgroundColor: '#8b4513',
  borderColor: '#8b4513',
  color: 'white',
} as const

// product → labelName → #id: у корня дерева детей нет, потому плоский список
const getNodeLabel = (node: ILoodsmanTreeNode): string =>
  node.product ?? node.labelName ?? `#${node.id}`

const LoodsmanPage = () => {
  const router = useRouter()
  const queryClient = useQueryClient()
  const [searchValue, setSearchValue] = useState('')
  const [selectedIdVersion, setSelectedIdVersion] = useState<number | null>(null)
  const {
    data: nodes,
    isLoading,
    isError,
    error,
    refetch,
  } = useLoodsmanTreeRoot()
  const {
    data: objectInfo,
    isLoading: isObjectInfoLoading,
    isPlaceholderData: isObjectInfoPlaceholder,
    isError: isObjectInfoError,
    error: objectInfoError,
    refetch: refetchObjectInfo,
  } = useLoodsmanObjectInfo(selectedIdVersion)
  const isCardRefreshing = isObjectInfoPlaceholder
  const isCurrentInfo =
    objectInfo !== undefined && objectInfo.idVersion === selectedIdVersion
  const errorStatus = error instanceof ApiError ? error.status : null
  const objectInfoErrorStatus =
    objectInfoError instanceof ApiError ? objectInfoError.status : null
  const objectInfoErrorMessage =
    objectInfoError instanceof ApiError
      ? objectInfoError.message
      : 'Не удалось загрузить объект'

  useEffect(() => {
    if (errorStatus === 401 || objectInfoErrorStatus === 401) {
      queryClient.removeQueries({ queryKey: ['loodsman-session'] })
      router.replace('/loodsman/auth')
    }
  }, [errorStatus, objectInfoErrorStatus, queryClient, router])

  const handleSearch = (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault()
    const parsed = Number.parseInt(searchValue.trim(), 10)
    if (Number.isInteger(parsed)) {
      setSelectedIdVersion(parsed)
    }
  }

  const handleLogout = async () => {
    try {
      await loodsmanLogout()
    } catch {}
    window.location.assign('/')
  }

  const objectInfoCard =
    objectInfo && (isCurrentInfo || isObjectInfoPlaceholder) ? (
      <div
        className="transition-opacity duration-150"
        style={
          isCardRefreshing
            ? { opacity: 0.6, pointerEvents: 'none' }
            : undefined
        }
      >
        <LoodsmanObjectCard
          info={objectInfo}
          onSelectVersion={setSelectedIdVersion}
        />
      </div>
    ) : null

  return (
    <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
      <div className="max-w-5xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <h2 className="text-2xl font-bold text-stone-800">
            Работа с Loodsman
          </h2>
          <button
            type="button"
            onClick={handleLogout}
            className="flex items-center gap-2 px-4 py-2 rounded-lg border-2 text-sm font-medium text-stone-700 transition hover:shadow-md"
            style={{
              backgroundColor: '#fdf6ee',
              borderColor: '#d2b48c',
            }}
          >
            <LogOut size={18} />
            Выйти
          </button>
        </div>

        <div
          className="p-6 rounded-xl border-2 shadow-md mb-6"
          style={cardStyle}
        >
          <div className="flex items-center gap-3 mb-4">
            <div
              className="flex items-center justify-center w-10 h-10 rounded-lg"
              style={iconBoxStyle}
            >
              <Search size={20} className="text-stone-700" />
            </div>
            <div>
              <h3 className="text-lg font-semibold text-stone-800">
                Поиск объекта
              </h3>
              <p className="text-sm text-stone-600">
                Атрибутивная информация по идентификатору версии
              </p>
            </div>
            {objectInfo && isCardRefreshing ? (
              <ProgressSpinner
                style={{ width: '24px', height: '24px' }}
                aria-label="Обновление информации об объекте"
                className="ml-auto"
              />
            ) : null}
          </div>

          <form onSubmit={handleSearch} className="flex gap-2 mb-4">
            <InputText
              id="object-id-version"
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
              type="text"
              inputMode="numeric"
              placeholder="Идентификатор версии объекта"
              className="w-full"
              style={inputStyle}
            />
            <button
              type="submit"
              className="flex items-center gap-2 px-4 rounded-lg text-sm font-medium shrink-0 cursor-pointer transition hover:shadow-md"
              style={primaryButtonStyle}
            >
              <Search size={16} className="shrink-0" />
              Найти
            </button>
          </form>

          {selectedIdVersion === null ? (
            <p className="text-sm text-stone-500 py-2">
              Введите идентификатор объекта и нажмите «Найти»
            </p>
          ) : !objectInfo && isObjectInfoLoading ? (
            <div
              className="flex justify-center py-10"
              role="status"
              aria-label="Загрузка информации об объекте"
            >
              <ProgressSpinner style={{ width: '48px', height: '48px' }} />
            </div>
          ) : isObjectInfoError ? (
            <div className="space-y-4">
              <div className="flex items-center justify-between gap-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
                <span>
                  {objectInfo
                    ? `Не удалось загрузить объект ${selectedIdVersion}: ${objectInfoErrorMessage}`
                    : objectInfoErrorMessage}
                </span>
                <Button
                  label="Повторить"
                  icon="pi pi-refresh"
                  outlined
                  onClick={() => refetchObjectInfo()}
                  style={{ borderColor: '#8b4513', color: '#8b4513' }}
                />
              </div>
              {objectInfoCard}
            </div>
          ) : (
            objectInfoCard
          )}
        </div>

        <div
          className="p-6 rounded-xl border-2 shadow-md"
          style={cardStyle}
        >
          <div className="flex items-center gap-3 mb-4">
            <div
              className="flex items-center justify-center w-10 h-10 rounded-lg"
              style={iconBoxStyle}
            >
              <Map size={20} className="text-stone-700" />
            </div>
            <div>
              <h3 className="text-lg font-semibold text-stone-800">
                Навигация
              </h3>
              <p className="text-sm text-stone-600">
                Корень дерева данных Loodsman
              </p>
            </div>
          </div>

          {isLoading ? (
            <div
              className="flex justify-center py-10"
              role="status"
              aria-label="Загрузка дерева"
            >
              <ProgressSpinner style={{ width: '48px', height: '48px' }} />
            </div>
          ) : isError ? (
            <div className="flex items-center justify-between gap-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
              <span>Не удалось загрузить дерево Loodsman.</span>
              <Button
                label="Повторить"
                icon="pi pi-refresh"
                outlined
                onClick={() => refetch()}
                style={{ borderColor: '#8b4513', color: '#8b4513' }}
              />
            </div>
          ) : !nodes || nodes.length === 0 ? (
            <p className="text-sm text-stone-500 py-4">Нет доступных узлов</p>
          ) : (
            <ul className="divide-y divide-stone-200">
              {nodes.map((node, index) => (
                <li
                  key={`${node.id}-${index}`}
                  className="py-2.5 text-sm text-stone-800"
                >
                  {getNodeLabel(node)}
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  )
}

export default LoodsmanPage
