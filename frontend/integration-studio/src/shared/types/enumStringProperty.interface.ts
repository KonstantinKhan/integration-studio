import type { EnumStringItem } from "./enumStringItem.interface"

export interface EnumStringProperty {
  objectId: number
  typeId: number
  id: string
  name: string
  dataType: number
  description?: string
  code: string
  isSystemObject: boolean
  absoluteCode: string
	items: EnumStringItem[]
}
