import type { PropertyResult } from './propertyResult.interface'

export interface EnrichedSearchResultItem {
  designation: string
  classifierCode: string
  changeDate: Date
  objectId: number
  typeId: number
  iconCode: number
  properties: PropertyResult[]
}
