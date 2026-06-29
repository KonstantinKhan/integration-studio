function pad(n: number): string {
  return n.toString().padStart(2, '0')
}

export function formatDate(date: Date): string {
  const year = date.getFullYear()
  const month = pad(date.getMonth() + 1)
  const day = pad(date.getDate())
  const hours = pad(date.getHours())
  const minutes = pad(date.getMinutes())
  return `${year}-${month}-${day}T${hours}:${minutes}:00.000`
}

export function formatDisplayDateTime(isoString: string): string {
  const d = new Date(isoString)
  const day = pad(d.getDate())
  const month = pad(d.getMonth() + 1)
  const year = d.getFullYear()
  const hours = pad(d.getHours())
  const minutes = pad(d.getMinutes())
  return `${day}.${month}.${year} ${hours}:${minutes}`
}
