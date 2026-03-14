import type { ContractProperty } from "./contractProperty.interface";

export interface Contract {
	ojectId: number,
	typeId: number,
	name: string,
	description?: string,
	properties: ContractProperty[]
}