import { useQuery } from '@tanstack/react-query'
import { fetchConceptsByGroup } from '@/api/concept.api'

export function useGroupConcepts(
	groupTypeId: number,
	groupObjectId: number,
	enabled: boolean = true,
) {
	return useQuery({
		queryKey: ['groupConcepts', groupTypeId, groupObjectId],
		queryFn: () => fetchConceptsByGroup(groupTypeId, groupObjectId),
		enabled,
	})
}
