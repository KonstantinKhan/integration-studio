import { useMutation, useQueryClient } from '@tanstack/react-query'
import { authorize } from '@/api/auth.api'
import { useAuthStore } from '@/store/auth.store'
import type { IAuth } from '@/shared/types/auth.interface'

export function useAuthorize() {
  const setAuthenticated = useAuthStore((s) => s.setAuthenticated)
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: IAuth) => authorize(data),
    onSuccess: () => {
      setAuthenticated(true)
      queryClient.removeQueries({ queryKey: ['session'] })
    },
  })
}
