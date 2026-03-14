import type { Identifier } from "./identifier.interface"

export interface EvaluationPropertyInfo {
	evaluationMode: number
	formula: Identifier
	appointedFormula: Identifier
	usePropertyValue: boolean
	writeAccess: boolean
	objectId: number
	typeId: number
}