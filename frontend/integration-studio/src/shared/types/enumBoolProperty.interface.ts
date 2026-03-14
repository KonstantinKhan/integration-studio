import type { EnumBoolItem } from "./enumBoolItem.interface"

export interface EnumBoolProperty {
  objectId: number
  typeId: number
  id: string
  name: string
  dataType: number
  description?: string
  code: string
  isSystemObject: boolean
  absoluteCode: string
	items: EnumBoolItem[]
}
