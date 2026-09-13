'use client'

import { useRouter } from 'next/navigation'
import { useEffect } from 'react'
import { ProgressSpinner } from 'primereact/progressspinner'
import { Button } from 'primereact/button'
import { LogOut, Map } from 'lucide-react'
import { useQueryClient } from '@tanstack/react-query'
import { ApiError } from '@/api/api-client'
import { loodsmanLogout } from '@/api/loodsman.api'
import { useLoodsmanTreeRoot } from '@/hooks/useLoodsmanTreeRoot'
import type { ILoodsmanTreeNode } from '@/shared/types/loodsmanTreeNode.interface'

const cardStyle = { backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }
const iconBoxStyle = { backgroundColor: '#fdf6ee', border: '1px solid #d2b48c' }

// product → labelName → #id: у корня дерева детей нет, потому плоский список
const getNodeLabel = (node: ILoodsmanTreeNode): string =>
  node.product ?? node.labelName ?? `#${node.id}`

const LoodsmanPage = () => {
  const router = useRouter()
  const queryClient = useQueryClient()
  const {
    data: nodes,
    isLoading,
    isError,
    error,
    refetch,
  } = useLoodsmanTreeRoot()
  const errorStatus = error instanceof ApiError ? error.status : null

  useEffect(() => {
    if (errorStatus === 401) {
      queryClient.removeQueries({ queryKey: ['loodsman-session'] })
      router.replace('/loodsman/auth')
    }
  }, [errorStatus, queryClient, router])

  const handleLogout = async () => {
    try {
      await loodsmanLogout()
    } catch {}
    window.location.assign('/')
  }

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
