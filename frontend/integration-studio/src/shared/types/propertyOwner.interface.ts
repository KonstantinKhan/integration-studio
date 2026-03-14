import type { Property } from "./property.interface"

export interface PropertyOwner {
	id: string
	properties: Property[]
	writeAccess: boolean
	objectId: number
	typeId: number
}