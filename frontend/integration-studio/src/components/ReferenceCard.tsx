'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { Trash2 } from 'lucide-react'
import { Dialog } from 'primereact/dialog'
import { Button } from 'primereact/button'
import type { IReference } from '@/shared/types/reference.interface'
import { deleteReference } from '@/api/references.api'
import { useQueryClient } from '@tanstack/react-query'

interface ReferenceCardProps {
  reference: IReference
}

const ReferenceCard = ({ reference }: ReferenceCardProps) => {
  const router = useRouter()
  const queryClient = useQueryClient()

  const [deleteDialogVisible, setDeleteDialogVisible] = useState(false)
  const [isDeleting, setIsDeleting] = useState(false)

  const handleClick = () => {
    router.push(`/polynom/reference/${reference.typeId}-${reference.objectId}`)
  }

  const handleDeleteClick = (e: React.MouseEvent) => {
    e.stopPropagation()
    setDeleteDialogVisible(true)
  }

  const handleDeleteConfirm = async () => {
    setIsDeleting(true)
    try {
      await deleteReference(reference.typeId, reference.objectId)
      await queryClient.invalidateQueries({ queryKey: ['references'] })
      setDeleteDialogVisible(false)
    } catch (error) {
      console.error('Failed to delete reference:', error)
    } finally {
      setIsDeleting(false)
    }
  }

  return (
    <>
      <div
        className="group p-6 rounded-xl border-2 shadow-md hover:shadow-xl transition-all duration-300 hover:scale-105 cursor-pointer relative"
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

        <button
          onClick={handleDeleteClick}
          className="absolute top-2 right-2 p-2 rounded-full opacity-0 group-hover:opacity-100 transition-opacity duration-200 hover:bg-red-100 hover:text-red-600"
          title="Удалить справочник"
        >
          <Trash2 size={18} />
        </button>
      </div>

      <Dialog
        header="Подтверждение удаления"
        visible={deleteDialogVisible}
        style={{ width: '400px' }}
        onHide={() => {
          if (!isDeleting) {
            setDeleteDialogVisible(false)
          }
        }}
        footer={
          <>
            <Button
              label="Отмена"
              icon="pi pi-times"
              outlined
              disabled={isDeleting}
              onClick={() => setDeleteDialogVisible(false)}
            />
            <Button
              label="Удалить"
              icon="pi pi-trash"
              severity="danger"
              loading={isDeleting}
              onClick={handleDeleteConfirm}
            />
          </>
        }
      >
        <p className="text-stone-700">
          Вы уверены, что хотите удалить справочник &quot;{reference.name}&quot;?
        </p>
        <p className="text-stone-500 text-sm mt-2">
          Это действие нельзя отменить.
        </p>
      </Dialog>
    </>
  )
}

export default ReferenceCard
