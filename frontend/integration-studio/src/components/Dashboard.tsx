'use client'

import { useRouter } from 'next/navigation'
import { Map, ArrowLeftRight, RefreshCw } from 'lucide-react'

interface NavItem {
  title: string
  description: string
  icon: typeof Map
  href?: string
  soon?: boolean
}

const NAV_ITEMS: NavItem[] = [
  {
    title: 'Навигация',
    description: 'Просмотр и управление справочниками',
    icon: Map,
    href: '/polynom',
  },
  {
    title: 'Миграция',
    description: 'Миграция изменённых объектов',
    icon: ArrowLeftRight,
    soon: true,
  },
  {
    title: 'Синхронизация',
    description: 'Синхронизация данных между системами',
    icon: RefreshCw,
    href: '/polynom/changes',
  },
]

const Dashboard = () => {
  const router = useRouter()

  return (
    <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
      <div className="max-w-5xl mx-auto">
        <h2 className="text-2xl font-bold text-center mb-8 text-stone-800">
          Панель управления
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {NAV_ITEMS.map((item) => {
            const Icon = item.icon
            const clickable = Boolean(item.href)

            const handleNavigate = () => {
              if (item.href) router.push(item.href)
            }

            return (
              <button
                key={item.title}
                type="button"
                onClick={handleNavigate}
                disabled={!clickable}
                className={`relative text-left p-6 rounded-xl border-2 shadow-md transition ${
                  clickable
                    ? 'hover:shadow-lg hover:-translate-y-0.5 cursor-pointer'
                    : 'cursor-default opacity-70'
                }`}
                style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
              >
                {item.soon && (
                  <span className="absolute top-3 right-3 text-xs font-medium text-stone-500 bg-stone-200/70 px-2 py-0.5 rounded">
                    скоро
                  </span>
                )}
                <div
                  className="flex items-center justify-center w-12 h-12 rounded-lg mb-4"
                  style={{
                    backgroundColor: '#fdf6ee',
                    border: '1px solid #d2b48c',
                  }}
                >
                  <Icon size={24} className="text-stone-700" />
                </div>
                <h3 className="text-lg font-semibold text-stone-800 mb-1">
                  {item.title}
                </h3>
                <p className="text-sm text-stone-600">{item.description}</p>
              </button>
            )
          })}
        </div>
      </div>
    </div>
  )
}

export default Dashboard
