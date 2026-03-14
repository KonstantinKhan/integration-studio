import type { Contract } from "./contract.interface";
import type { Definitions } from "./definitions.interface";
import type { Item } from "./item.interface";
import type { MeasureEntities } from "./measureEntities.interface";
import type { MeasureUnits } from "./measureUnits.interface";
import type { PropertyOwner } from "./propertyOwner.interface";
import type { Values } from "./values.interface";

export interface PropertyOwnerResponse {
	propertyOwner: PropertyOwner,
	allContracts: Contract[],
	definitions: Definitions,
	measureEntities: MeasureEntities,
	measureUnits: MeasureUnits,
	values: Values,
	items: Item[]
}