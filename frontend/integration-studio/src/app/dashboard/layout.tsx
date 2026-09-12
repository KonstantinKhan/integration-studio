import type { ReactNode } from 'react'
import RequireSession from '@/components/RequireSession'

interface DashboardLayoutProps {
  children: ReactNode
}

const DashboardLayout = ({ children }: DashboardLayoutProps) => (
  <RequireSession>{children}</RequireSession>
)

export default DashboardLayout
