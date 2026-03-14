'use client'

import { useStorages } from '@/hooks/useStorages'

const Storage = () => {
  const { data: storages = [] } = useStorages()

  return (
    <div>
      {storages.map((el) => (
        <div key={el.storageId}>
          <p>{el.displayName}</p>
          <span>{el.storageId}</span>
        </div>
      ))}
    </div>
  )
}

export default Storage
