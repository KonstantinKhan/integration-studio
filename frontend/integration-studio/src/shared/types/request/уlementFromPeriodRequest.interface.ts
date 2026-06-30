import type { Identifier } from "../identifier.interface"

export interface ElementFromPeriodRequest {
	scope: Identifier,
	from: string
	to: string
}