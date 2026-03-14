import type { Contract } from "./contract.interface"
import type { ItemProperty } from "./itemProperty.interface"

export interface Item {
	objectId: number
	typeId: number
	id: string
	selfContracts: Contract[]
	properties: ItemProperty[]
}