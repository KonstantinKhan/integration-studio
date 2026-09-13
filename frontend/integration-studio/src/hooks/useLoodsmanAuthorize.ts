import { useMutation, useQueryClient } from '@tanstack/react-query'
import { loodsmanAuthorize } from '@/api/loodsman.api'
import { useAuthStore } from '@/store/auth.store'
import type { ILoodsmanAuth } from '@/shared/types/loodsmanAuth.interface'

export function useLoodsmanAuthorize() {
  const setAuthenticated = useAuthStore((s) => s.setAuthenticated)
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: ILoodsmanAuth) => loodsmanAuthorize(data),
    onSuccess: () => {
      setAuthenticated(true)
      // removeQueries, а не invalidate — invalidate успевает отдать 401 guard'у (гонка)
      queryClient.removeQueries({ queryKey: ['loodsman-session'] })
    },
  })
}
