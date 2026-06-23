import { apiClient } from './api-client'
import type { IConcept } from '@/shared/types/appointedConcept.interface'

export function fetchConceptsByGroup(
	groupTypeId: number,
	groupObjectId: number,
): Promise<IConcept[]> {
	return apiClient<IConcept[]>(
		'/concept/get-by-concept-appointer',
		{
			method: 'POST',
			body: JSON.stringify({
				group: { typeId: groupTypeId, objectId: groupObjectId },
			}),
		},
	)
}
