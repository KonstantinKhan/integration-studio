import type { Identifier } from "./identifier.interface"

export interface MeasureUnits {
	objectId: number
	typeId: number
	name: string
	uid: string
	code: string
	basicUnit: Identifier
}