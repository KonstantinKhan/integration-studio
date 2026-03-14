import type { TableColumnValue } from "./tableColumnValue.interface"
import type { TableRowValue } from "./tableRowValue.interface"

export interface TablePropertyValues {
	objectId: number
	typeId: number
	columns: TableColumnValue[]
	rows: TableRowValue[]
}