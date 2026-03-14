import type { Identifier } from "./identifier.interface";

export interface StringProperty {
		objectId: number,
		typeId: number,
		id: string,
		name: string,
		dataType: number,
		description?: string,
		code: string,
		isSystemObject: boolean,
		absoluteCode: string,
}