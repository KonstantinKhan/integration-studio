import { create } from 'zustand'

interface AuthState {
  isAuthenticated: boolean
  selectedStorageId: string | null
  selectedLoodsmanDb: string | null
  setAuthenticated: (value: boolean) => void
  setSelectedStorageId: (id: string | null) => void
  setSelectedLoodsmanDb: (db: string | null) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  isAuthenticated: false,
  selectedStorageId: null,
  selectedLoodsmanDb: null,
  setAuthenticated: (value) => set({ isAuthenticated: value }),
  setSelectedStorageId: (id) => set({ selectedStorageId: id }),
  setSelectedLoodsmanDb: (db) => set({ selectedLoodsmanDb: db }),
  logout: () =>
    set({
      isAuthenticated: false,
      selectedStorageId: null,
      selectedLoodsmanDb: null,
    }),
}))
