import type { Identifier } from "./identifier.interface"
import type { ComplexConditionRequest } from "./request/complexConditionRequest.interface"

export interface SearchRequest {
	pageNumber: number,
	pageSize: number,
	ownerScope: Identifier,
	condition: ComplexConditionRequest
}