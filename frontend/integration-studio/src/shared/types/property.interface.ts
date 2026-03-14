import type { EvaluationPropertyInfo } from "./evaluationPropertyInfo.interface"
import type { Identifier } from "./identifier.interface"
import type { LinkedPropertyInfo } from "./linkedPropertyInfo.interface"

export interface Property {
	type: string
	contract: Identifier
	definition: Identifier
	linkedPropertyInfo: LinkedPropertyInfo
	value?: Identifier
	isOwn: boolean
	isLinked: boolean
	contractPropertySource: Identifier
	evaluationPropertyInfo: EvaluationPropertyInfo
	writeAccess: boolean
	name: string
	objectId: number
	typeId: number
}