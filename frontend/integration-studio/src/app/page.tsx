'use client'

import Link from 'next/link'
import { useRouter } from 'next/navigation'
import { Boxes, Database, Plug, Puzzle } from 'lucide-react'
import { useConnections } from '@/hooks/useConnections'
import { useSession } from '@/hooks/useSession'
import { useLoodsmanSession } from '@/hooks/useLoodsmanSession'
import type { ConnectionStatus } from '@/types/connections'

const STATUS_DOT: Record<ConnectionStatus['status'], string> = {
  connected: 'bg-green-500',
  unreachable: 'bg-red-500',
  disabled: 'bg-stone-400',
}

const FUTURE_SERVICES = [
  {
    title: 'Новый сервис',
    description: 'Интеграция с новым внешним сервисом',
    icon: Puzzle,
  },
  {
    title: 'Новый API',
    description: 'Подключение внешнего API',
    icon: Plug,
  },
] as const

const cardStyle = { backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }
const iconBoxStyle = { backgroundColor: '#fdf6ee', border: '1px solid #d2b48c' }
const chipStyle = { backgroundColor: '#fdf6ee', borderColor: '#d2b48c' }
const buttonStyle = { backgroundColor: '#8b4513', borderColor: '#8b4513' }

const Home = () => {
  const router = useRouter()
  const {
    data: connections,
    isLoading: connectionsLoading,
    isError: connectionsError,
  } = useConnections()
  const { data: session, isLoading: sessionLoading } = useSession()
  const {
    data: loodsmanSession,
    isLoading: loodsmanSessionLoading,
  } = useLoodsmanSession()

  const polynom = connections?.find((conn) => conn.id === 'polynom')
  const polynomDown = polynom?.status === 'unreachable'

  const handlePolynom = () => {
    if (session?.authenticated) {
      router.push('/dashboard')
    } else {
      router.push('/polynom/auth')
    }
  }

  const loodsman = connections?.find((conn) => conn.id === 'loodsman')
  const loodsmanReady = !connectionsLoading && !!connections
  const loodsmanDown =
    loodsmanReady && (!loodsman || loodsman.status === 'unreachable')

  const handleLoodsman = () => {
    if (loodsmanSession?.authenticated) {
      router.push('/loodsman')
    } else {
      router.push('/loodsman/auth')
    }
  }

  return (
    <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
      <div className="max-w-5xl mx-auto">
        <header className="text-center mb-6">
          <h1 className="text-3xl font-bold text-stone-800">Integration Studio</h1>
          <p className="text-sm text-stone-600 mt-1">
            Единая точка работы с внешними сервисами и API
          </p>
        </header>

        <div className="flex flex-wrap items-center justify-center gap-2 mb-10">
          {connectionsLoading && !connections && (
            <>
              {[0, 1, 2].map((i) => (
                <span
                  key={i}
                  className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full border-2"
                  style={chipStyle}
                >
                  <span className="w-2 h-2 rounded-full bg-stone-300 animate-pulse" />
                  <span className="w-16 h-3 rounded bg-stone-200 animate-pulse" />
                </span>
              ))}
            </>
          )}

          {connections?.map((conn) => (
            <Link
              key={conn.id}
              href="/connections"
              className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full border-2 shadow-sm text-sm font-medium text-stone-700 hover:shadow-md transition"
              style={chipStyle}
              title={conn.error ?? undefined}
            >
              <span className={`w-2 h-2 rounded-full shrink-0 ${STATUS_DOT[conn.status]}`} />
              {conn.name}
            </Link>
          ))}

          {connectionsError && !connections && (
            <Link
              href="/connections"
              className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full border-2 shadow-sm text-sm font-medium text-stone-500 hover:shadow-md transition"
              style={chipStyle}
            >
              <span className="w-2 h-2 rounded-full bg-stone-400 shrink-0" />
              Сервисы
            </Link>
          )}

          {connectionsError && (
            <span className="text-xs text-stone-500">статусы недоступны</span>
          )}
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div
            className="relative text-left p-6 rounded-xl border-2 shadow-md flex flex-col"
            style={cardStyle}
          >
            <div
              className="flex items-center justify-center w-12 h-12 rounded-lg mb-4"
              style={iconBoxStyle}
            >
              <Boxes size={24} className="text-stone-700" />
            </div>
            <h2 className="text-lg font-semibold text-stone-800 mb-1">Polynom</h2>
            <p className="text-sm text-stone-600 mb-4">
              Справочники, навигация и синхронизация данных Polynom
            </p>
            <div className="mt-auto">
              {polynomDown ? (
                <>
                  <button
                    type="button"
                    disabled
                    className="w-full py-2 rounded-lg text-white font-medium border-2 disabled:cursor-not-allowed disabled:opacity-60"
                    style={buttonStyle}
                  >
                    ...
                  </button>
                  <p className="text-xs text-red-700 mt-2 text-center">
                    Polynom недоступен
                  </p>
                </>
              ) : sessionLoading ? (
                <button
                  type="button"
                  disabled
                  className="w-full py-2 rounded-lg text-white font-medium border-2 disabled:cursor-not-allowed disabled:opacity-60"
                  style={buttonStyle}
                >
                  ...
                </button>
              ) : (
                <button
                  type="button"
                  onClick={handlePolynom}
                  className="w-full py-2 rounded-lg text-white font-medium border-2 hover:opacity-90 transition cursor-pointer"
                  style={buttonStyle}
                >
                  {session?.authenticated ? 'Открыть' : 'Подключить'}
                </button>
              )}
            </div>
          </div>

          <div
            className="relative text-left p-6 rounded-xl border-2 shadow-md flex flex-col"
            style={cardStyle}
          >
            <div
              className="flex items-center justify-center w-12 h-12 rounded-lg mb-4"
              style={iconBoxStyle}
            >
              <Database size={24} className="text-stone-700" />
            </div>
            <h2 className="text-lg font-semibold text-stone-800 mb-1">Loodsman</h2>
            <p className="text-sm text-stone-600 mb-4">
              Навигация по дереву данных Loodsman
            </p>
            <div className="mt-auto">
              {loodsmanDown ? (
                <>
                  <button
                    type="button"
                    disabled
                    className="w-full py-2 rounded-lg text-white font-medium border-2 disabled:cursor-not-allowed disabled:opacity-60"
                    style={buttonStyle}
                  >
                    ...
                  </button>
                  <p className="text-xs text-red-700 mt-2 text-center">
                    Loodsman недоступен
                  </p>
                </>
              ) : loodsmanSessionLoading ? (
                <button
                  type="button"
                  disabled
                  className="w-full py-2 rounded-lg text-white font-medium border-2 disabled:cursor-not-allowed disabled:opacity-60"
                  style={buttonStyle}
                >
                  ...
                </button>
              ) : (
                <button
                  type="button"
                  onClick={handleLoodsman}
                  className="w-full py-2 rounded-lg text-white font-medium border-2 hover:opacity-90 transition cursor-pointer"
                  style={buttonStyle}
                >
                  {loodsmanSession?.authenticated ? 'Открыть' : 'Подключить'}
                </button>
              )}
            </div>
          </div>

          {FUTURE_SERVICES.map(({ title, description, icon: Icon }) => (
            <div
              key={title}
              className="relative text-left p-6 rounded-xl border-2 shadow-md opacity-70 cursor-default"
              style={cardStyle}
            >
              <span className="absolute top-3 right-3 text-xs font-medium text-stone-500 bg-stone-200/70 px-2 py-0.5 rounded">
                скоро
              </span>
              <div
                className="flex items-center justify-center w-12 h-12 rounded-lg mb-4"
                style={iconBoxStyle}
              >
                <Icon size={24} className="text-stone-700" />
              </div>
              <h2 className="text-lg font-semibold text-stone-800 mb-1">{title}</h2>
              <p className="text-sm text-stone-600">{description}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default Home
