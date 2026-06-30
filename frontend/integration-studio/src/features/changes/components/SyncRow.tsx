import { memo } from "react"

interface SyncRowProps {
  index: number
  code: string
  designation: string
  changeDate: string
}

export const SyncRow = memo(function SyncRow({
  index,
  code,
  designation,
  changeDate,
}: SyncRowProps) {
  return (
    <tr className="border-b border-stone-200 last:border-0">
      <td className="py-1.5 px-3 text-stone-500 text-sm tabular-nums">
        {index}
      </td>
      <td className="py-1.5 px-3 text-sm text-stone-700 break-all">
        {code || '—'}
      </td>
      <td className="py-1.5 px-3 text-sm text-stone-800 break-all">
        {designation || '—'}
      </td>
      <td className="py-1.5 px-3 text-sm text-stone-800 break-all">
        {changeDate || '—'}
      </td>
    </tr>
  )
})