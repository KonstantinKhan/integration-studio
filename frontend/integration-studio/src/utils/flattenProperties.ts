import type { PropertyOwnerResponse } from '@/shared/types/propertyOwnerResponse.interface'
import type { Values } from '@/shared/types/values.interface'
import type { MeasureEntities } from '@/shared/types/measureEntities.interface'
import type { Identifier } from '@/shared/types/identifier.interface'

export interface PropertyRow {
  id: string
  name: string
  value: string
}

function m(v: { objectId: number; typeId: number }, id: Identifier): boolean {
  return v.objectId === id.objectId && v.typeId === id.typeId
}

// Safely converts any runtime value to string, even if interfaces are wrong.
// If an object is received, extracts its `value` field recursively.
function safeStr(val: unknown): string {
  if (val == null) return '—'
  if (typeof val === 'object') {
    const obj = val as Record<string, unknown>
    if ('value' in obj) return safeStr(obj.value)
    return JSON.stringify(val)
  }
  return String(val)
}

function formatValue(
  values: Values,
  measureEntities: MeasureEntities,
  valueId: Identifier,
): string {
  const str = values.stringProperties?.find((v) => m(v, valueId))
  if (str) return safeStr(str.value)

  const int = values.integerProperties?.find((v) => m(v, valueId))
  if (int !== undefined) return safeStr(int.value)

  const dbl = values.doubleProperties?.find((v) => m(v, valueId))
  if (dbl) {
    const unit = dbl.measureUnit
      ? measureEntities.entities.find((e) => m(e, dbl.measureUnit))
      : undefined
    const numStr = safeStr(dbl.value)
    return unit ? `${numStr} ${unit.name}` : numStr
  }

  const bool = values.booleanProperties?.find((v) => m(v, valueId))
  if (bool) return safeStr(bool.value)

  const color = values.colorProperties?.find((v) => m(v, valueId))
  if (color) return `RGB(${color.r}, ${color.g}, ${color.b})`

  const guid = values.guidProperties?.find((v) => m(v, valueId))
  if (guid) return safeStr(guid.value)

  const dt = values.dateTimeProperties?.find((v) => m(v, valueId))
  if (dt) {
    const raw = dt.value as unknown
    if (typeof raw !== 'string') return safeStr(raw)
    return dt.useTime ? raw : raw.split('T')[0]
  }

  const enm = values.enumProperties?.find((v) => m(v, valueId))
  if (enm) return safeStr(enm.value)

  const enmStr = values.enumStringProperties?.find((v) => m(v, valueId))
  if (enmStr) return enmStr.value?.description ?? '—'

  const enmInt = values.enumIntProperties?.find((v) => m(v, valueId))
  if (enmInt) return enmInt.value?.description ?? '—'

  const enmDbl = values.enumDoubleProperties?.find((v) => m(v, valueId))
  if (enmDbl) return enmDbl.value?.description ?? '—'

  const enmBool = values.enumBoolProperties?.find((v) => m(v, valueId))
  if (enmBool) return enmBool.value?.description ?? '—'

  const tbl = values.tableProperties?.find((v) => m(v, valueId))
  if (tbl) return `[Таблица: ${tbl.rows.length} стр.]`

  const img = values.imageProperties?.find((v) => m(v, valueId))
  if (img) return img.isEmpty ? '—' : '[Изображение]'

  const rtf = values.rtfProperties?.find((v) => m(v, valueId))
  if (rtf) return rtf.value ? '[RTF]' : '—'

  const bin = values.binaryProperties?.find((v) => m(v, valueId))
  if (bin) return bin.isEmpty ? '—' : '[Файл]'

  const opt = values.opticProperties?.find((v) => m(v, valueId))
  if (opt) return '[Оптика]'

  const set = values.setProperties?.find((v) => m(v, valueId))
  if (set) return set.values.join(', ')

  return '—'
}

export function flattenProperties(owner: PropertyOwnerResponse): PropertyRow[] {
  const rows: PropertyRow[] = []
  for (const prop of owner.propertyOwner.properties ?? []) {
    const value = prop.value
      ? formatValue(owner.values, owner.measureEntities, prop.value)
      : '—'
    rows.push({ id: `${prop.typeId}-${prop.objectId}`, name: prop.name, value })
  }
  return rows
}
