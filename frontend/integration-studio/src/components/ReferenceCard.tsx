'use client'

import { useRouter } from 'next/navigation'
import type { IReference } from '@/shared/types/reference.interface'

interface ReferenceCardProps {
  reference: IReference
}

const ReferenceCard = ({ reference }: ReferenceCardProps) => {
  const router = useRouter()

  const handleClick = () => {
    router.push(`/polynom/reference/${reference.typeId}-${reference.objectId}`)
  }

  return (
    <div
      className="p-6 rounded-xl border-2 shadow-md hover:shadow-xl transition-all duration-300 hover:scale-105 cursor-pointer"
      style={{
        backgroundColor: '#f4f1ea',
        borderColor: '#d2b48c',
      }}
      onClick={handleClick}
    >
      <div className="flex items-center gap-3">
        <i className="pi pi-book text-2xl" style={{ color: '#8b4513' }}></i>
        <h3 className="text-lg font-semibold text-stone-800 truncate">
          {reference.name}
        </h3>
      </div>
    </div>
  )
}

export default ReferenceCard
