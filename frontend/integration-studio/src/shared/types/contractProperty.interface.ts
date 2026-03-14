import type { Identifier } from "./identifier.interface"

export interface ContractProperty {
	objectId: number,
	typeId: number,
	dataType: number,
	definition: Identifier,
	defaultValue?: Identifier,
	absoluteCode: string,
	isMandatory: boolean,
	isHidden: boolean,
	isSpecial: boolean,
	isUnique: boolean,
	isIndexable: boolean,
	isDynamic: boolean,
	isDisplayedForSelection: boolean,
	isSetBeforeApplying: boolean,
	isReadOnly: boolean,
	isDefaultIfEmpty: boolean,
	isNameManuallySet: boolean,
	position: number
}