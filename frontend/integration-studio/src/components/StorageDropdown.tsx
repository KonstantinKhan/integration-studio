'use client'

import { Dropdown } from 'primereact/dropdown'
import { useStorages } from '@/hooks/useStorages'

interface StorageDropdownProps {
  id?: string
  value: string | null
  onChange: (storageId: string) => void
  className?: string
}

const StorageDropdown = ({
  id,
  value,
  onChange,
  className,
}: StorageDropdownProps) => {
  const { data: storages = [], isLoading } = useStorages()

  return (
    <Dropdown
      inputId={id}
      className={className}
      value={value}
      onChange={(e) => onChange(e.value)}
      options={storages}
      optionLabel="displayName"
      optionValue="storageId"
      placeholder="Выберите хранилище ПОЛИНОМ"
      emptyMessage="Нет доступных хранилищ ПОЛИНОМ"
      disabled={isLoading}
      loading={isLoading}
    />
  )
}

export default StorageDropdown
