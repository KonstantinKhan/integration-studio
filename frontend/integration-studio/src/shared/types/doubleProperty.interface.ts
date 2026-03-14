import type { Identifier } from "./identifier.interface";

export interface DoubleProperty {
	objectId: number,
	typeId: number,
	id: string,
	name: string,
	dataType: number,
	description?: string,
	code: string,
	isSystemObject: boolean,
	absoluteCode: string,
	measureEntity: Identifier
}