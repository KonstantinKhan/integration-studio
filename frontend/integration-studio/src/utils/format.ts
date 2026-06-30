function pad(n: number): string {
  return n.toString().padStart(2, '0')
}

export function formatDate(date: Date): string {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}` +
    `T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}.000`
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
