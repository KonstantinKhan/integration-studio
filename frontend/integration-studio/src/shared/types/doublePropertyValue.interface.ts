import type { Identifier } from "./identifier.interface"

export interface DoublePropertyValues {
	objectId: number
	typeId: number
	mode: number
	value: number
	minValue: number
	maxValue: number
	lowerTolerance: number
	upperTolerance: number
	measureUnit: Identifier
}