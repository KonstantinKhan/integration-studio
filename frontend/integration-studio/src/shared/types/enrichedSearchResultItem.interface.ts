import type { PropertyResult } from './propertyResult.interface'

export interface EnrichedSearchResultItem {
  name: string | null
  objectId: number
  typeId: number
  iconCode: number
  properties: PropertyResult[]
}
