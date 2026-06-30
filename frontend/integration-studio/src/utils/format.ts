function pad(n: number): string {
  return n.toString().padStart(2, '0')
}

export function formatDate(date: Date): string {
  const iso = date.toISOString()
  return iso.slice(0, 23)
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
