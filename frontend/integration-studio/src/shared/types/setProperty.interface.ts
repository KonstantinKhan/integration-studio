import type { SetItem } from "./setItem.interface"

export interface SetProperty {
  objectId: number
  typeId: number
  id: string
  name: string
  dataType: number
  description?: string
  code: string
  isSystemObject: boolean
  absoluteCode: string
	items: SetItem[]
}
