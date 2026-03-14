import { useQuery } from '@tanstack/react-query'
import { fetchStorages } from '@/api/storage.api'

export function useStorages() {
  return useQuery({
    queryKey: ['storages'],
    queryFn: fetchStorages,
  })
}
