import { useQuery } from '@tanstack/react-query'
import { fetchReferences } from '@/api/references.api'

export function useReferences() {
  return useQuery({
    queryKey: ['references'],
    queryFn: fetchReferences,
  })
}
