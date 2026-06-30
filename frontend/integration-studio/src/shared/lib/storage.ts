export function createStorageStore<T>(key: string, initialValue: T) {
  let currentValue: T = initialValue
  if (typeof window !== 'undefined') {
    try {
      const raw = localStorage.getItem(key)
      if (raw) {
        currentValue = JSON.parse(raw) as T
      }
    } catch {}
  }

  const listeners = new Set<() => void>()

  const handleStorage = (event: StorageEvent) => {
    if (event.key === key) {
      try {
        const parsed = event.newValue
          ? JSON.parse(event.newValue)
          : initialValue
        if (JSON.stringify(parsed) !== JSON.stringify(currentValue)) {
          currentValue = parsed
          listeners.forEach((l) => l())
        }
      } catch {}
    }
  }

  const subscribe = (callback: () => void) => {
    listeners.add(callback)

    if (typeof window !== 'undefined') {
      window.addEventListener('storage', handleStorage)
    }

    return () => {
      listeners.delete(callback)
      if (typeof window !== 'undefined') {
        window.removeEventListener('storage', handleStorage)
      }
    }
  }

  const getSnapshot = () => currentValue

  const setValue = (value: T) => {
    const newValue = value
    if (JSON.stringify(newValue) !== JSON.stringify(currentValue)) {
      currentValue = newValue
      if (typeof window !== 'undefined') {
        localStorage.setItem(key, JSON.stringify(newValue))
      }
      listeners.forEach((l) => l())
    }
  }

  return { subscribe, getSnapshot, setValue }
}
