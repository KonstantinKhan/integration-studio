import { useSyncExternalStore } from 'react'
import { mountedStore, nodeStore } from '../stores/selected-node.store'

export function useSelectedNode() {
  const mounted = useSyncExternalStore(
    mountedStore.subscribe,
    mountedStore.getSnapshot,
    mountedStore.getServerSnapshot,
  )

  const storedNode = useSyncExternalStore(
    nodeStore.subscribe,
    nodeStore.getSnapshot,
    () => null,
  )

  return { mounted, storedNode }
}
