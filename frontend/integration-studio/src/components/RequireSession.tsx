'use client'

import { useEffect, type ReactNode } from 'react'
import { useRouter } from 'next/navigation'
import { ProgressSpinner } from 'primereact/progressspinner'
import { Button } from 'primereact/button'
import { ServerOff } from 'lucide-react'
import { useSession } from '@/hooks/useSession'

const fullscreenStyle =
  'min-h-screen flex items-center justify-center bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50'

interface RequireSessionProps {
  children: ReactNode
}

/**
 * Клиентская охрана защищённых зон (dashboard, polynom).
 * Пока сессия проверяется — полноэкранный спиннер.
 * Бэкенд недоступен (сетевая ошибка) — блок с кнопкой «Повторить», без редиректа.
 * Сессии нет — редирект на /polynom/auth (на время замены — null).
 * Сессия есть — рендерим содержимое.
 */
const RequireSession = ({ children }: RequireSessionProps) => {
  const router = useRouter()
  const { data, isLoading, isFetching, isError, refetch } = useSession()
  const authenticated = data?.authenticated === true

  useEffect(() => {
    if (!isLoading && !isFetching && !isError && !authenticated) {
      router.replace('/polynom/auth')
    }
  }, [isLoading, isFetching, isError, authenticated, router])

  if (isLoading) {
    return (
      <div className={fullscreenStyle} role="status" aria-label="Проверка сессии">
        <ProgressSpinner style={{ width: '56px', height: '56px' }} />
      </div>
    )
  }

  if (isError) {
    return (
      <div className={fullscreenStyle}>
        <div className="text-center p-8 rounded-xl border-2 shadow-md max-w-sm bg-[#f4f1ea] border-[#d2b48c]">
          <ServerOff size={32} className="mx-auto mb-3 text-stone-500" />
          <h2 className="text-lg font-semibold text-stone-800 mb-1">
            Бэкенд недоступен
          </h2>
          <p className="text-sm text-stone-600 mb-5">
            Не удалось проверить сессию. Попробуйте ещё раз.
          </p>
          <Button
            label="Повторить"
            icon="pi pi-refresh"
            onClick={() => refetch()}
            style={{ backgroundColor: '#8b4513', borderColor: '#8b4513' }}
          />
        </div>
      </div>
    )
  }

  // Сессии нет: редирект уже запущен в useEffect, пока не сработал — ничего не рисуем.
  if (!authenticated) {
    return null
  }

  return <>{children}</>
}

export default RequireSession
