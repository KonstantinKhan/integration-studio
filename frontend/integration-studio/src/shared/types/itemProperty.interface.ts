import type { Identifier } from "./identifier.interface"

export interface ItemProperty {
	objectId: number
	typeId: number
	dataType: number
	contract: Identifier
	contractProperty: Identifier
	definition: Identifier
	valueData?: Identifier
	evaluationMode: number
	isLinked: boolean
}