import { createStorageStore } from "@/shared/lib/storage";

const STORAGE_KEY = 'polynom.changes.selectedNode'

export type StoredNode = { name: string; typeId: number; objectId: number } | null

export const nodeStore = createStorageStore<StoredNode>(STORAGE_KEY, null)

export const mountedStore = {
	subscribe: () => () => {},
	getSnapshot: () => true,
	getServerSnapshot: () => false,
}
