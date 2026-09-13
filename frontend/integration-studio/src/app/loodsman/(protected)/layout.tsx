import type { ReactNode } from 'react'
import RequireLoodsmanSession from '@/components/RequireLoodsmanSession'

interface ProtectedLoodsmanLayoutProps {
  children: ReactNode
}

const ProtectedLoodsmanLayout = ({
  children,
}: ProtectedLoodsmanLayoutProps) => (
  <RequireLoodsmanSession>{children}</RequireLoodsmanSession>
)

export default ProtectedLoodsmanLayout
