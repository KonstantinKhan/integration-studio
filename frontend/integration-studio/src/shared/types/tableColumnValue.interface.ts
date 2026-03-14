import type { Identifier } from "./identifier.interface"

export interface TableColumnValue {
	objectId: number
	typeId: number
	tablePropertyDefinitionColumn: Identifier
	width: number
}