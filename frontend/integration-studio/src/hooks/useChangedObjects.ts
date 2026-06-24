import { useQuery } from '@tanstack/react-query'
import { fetchChangedObjects } from '@/api/search.api'

export function useChangedObjects(
  from: Date | null,
  to: Date | null,
  enabled: boolean,
) {
  return useQuery({
    queryKey: ['changedObjects', from?.getTime(), to?.getTime()],
    queryFn: () => fetchChangedObjects(from as Date, to as Date),
    enabled: enabled && !!from && !!to,
  })
}
