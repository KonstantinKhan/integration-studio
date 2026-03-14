import { create } from 'zustand'

interface AuthState {
  isAuthenticated: boolean
  selectedStorageId: string | null
  setAuthenticated: (value: boolean) => void
  setSelectedStorageId: (id: string | null) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  isAuthenticated: false,
  selectedStorageId: null,
  setAuthenticated: (value) => set({ isAuthenticated: value }),
  setSelectedStorageId: (id) => set({ selectedStorageId: id }),
  logout: () => set({ isAuthenticated: false, selectedStorageId: null }),
}))
