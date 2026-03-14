import type { EnumIntItem } from "./enumIntItem.interface"

export interface EnumIntProperty {
  objectId: number
  typeId: number
  id: string
  name: string
  dataType: number
  description?: string
  code: string
  isSystemObject: boolean
  absoluteCode: string
  items: EnumIntItem[]
}
