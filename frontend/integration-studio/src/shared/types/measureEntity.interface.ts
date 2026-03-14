import type { Identifier } from "./identifier.interface"

export interface MeasureEntity {
  objectId: number
  typeId: number
  name: string
  uid: string
  code: string
	basicUnit: Identifier
}
