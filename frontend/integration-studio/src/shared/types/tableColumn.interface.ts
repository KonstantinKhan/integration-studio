import type { Identifier } from './identifier.interface'

export interface TableColumn {
  objectId: number
  typeId: number
  owner: Identifier
  dataType: number
  definition: Identifier
  defaultValue: Identifier
  absoluteCode: string
	isMandatory: boolean
	isHidden: boolean
	isSpecial: boolean
	isUnique: boolean
	isIndexable: boolean
	isDynamic: boolean
	isDisplayedForSelection: boolean
	isSetBeforeApplying: boolean
	isReadOnly: boolean
	isDefaultIfEmpty: boolean
	isNameManuallySet: boolean
	position: number
	isUniqueInTable: boolean
}
