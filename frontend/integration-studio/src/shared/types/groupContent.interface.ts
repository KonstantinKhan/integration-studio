import type { IElement } from "./element.interface";
import type { IElementGroup } from "./elementGroup.interface";

export interface IGroupContent {
	groups: IElementGroup[],
	elements: IElement[]
}