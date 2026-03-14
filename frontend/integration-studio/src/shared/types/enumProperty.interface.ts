import type { EnumItem } from "./enumItem.interface";

export interface EnumProperty {
		objectId: number,
		typeId: number,
		id: string,
		name: string,
		dataType: number,
		description?: string,
		code: string,
		isSystemObject: boolean,
		absoluteCode: string,
		items: EnumItem[]
}