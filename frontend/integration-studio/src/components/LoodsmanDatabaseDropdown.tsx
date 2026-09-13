'use client'

import { Dropdown } from 'primereact/dropdown'
import { useLoodsmanDatabases } from '@/hooks/useLoodsmanDatabases'
import type { ILoodsmanDatabase } from '@/shared/types/loodsmanDatabase.interface'

interface LoodsmanDatabaseDropdownProps {
  id?: string
  value: string | null
  onChange: (dbName: string) => void
  className?: string
}

const LoodsmanDatabaseDropdown = ({
  id,
  value,
  onChange,
  className,
}: LoodsmanDatabaseDropdownProps) => {
  const { data: databases = [], isLoading } = useLoodsmanDatabases()

  // Пустые имена БД не показываем
  const options = databases.filter(
    (db): db is ILoodsmanDatabase & { name: string } =>
      db.name !== null && db.name.trim() !== '',
  )

  return (
    <Dropdown
      inputId={id}
      className={className}
      value={value}
      onChange={(e) => onChange(e.value)}
      options={options}
      optionLabel="name"
      optionValue="name"
      placeholder="Выберите базу данных Loodsman"
      emptyMessage="Нет доступных баз данных Loodsman"
      disabled={isLoading}
      loading={isLoading}
    />
  )
}

export default LoodsmanDatabaseDropdown
