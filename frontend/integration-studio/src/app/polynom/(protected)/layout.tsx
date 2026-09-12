import type { ReactNode } from 'react'
import RequireSession from '@/components/RequireSession'

interface ProtectedPolynomLayoutProps {
  children: ReactNode
}

const ProtectedPolynomLayout = ({ children }: ProtectedPolynomLayoutProps) => (
  <RequireSession>{children}</RequireSession>
)

export default ProtectedPolynomLayout
