import type { TableColumn } from "./tableColumn.interface"

export interface TableProperty {
  objectId: number
  typeId: number
  id: string
  name: string
  dataType: number
  description?: string
  code: string
  isSystemObject: boolean
  absoluteCode: string
	items: TableColumn[]
}
