import type { EnrichedSearchResultItem } from '@/shared/types/enrichedSearchResultItem.interface'
import { formatDate } from '@/utils/format'

export function buildSyncRequest(
  from: Date,
  to: Date,
  typeId: number,
  objectId: number,
) {
  const fromStr = formatDate(from)
  const toStr = formatDate(to)
  const scope = { objectId: objectId, typeId: typeId }
  const request = { scope, from: fromStr, to: toStr }
  return request
}
