export interface UnknownPropertyValue {
  type: 'unknown'
  data: string
}

export interface StringPropertyValue {
  type: 'string'
  data: string
}

export interface DateTimePropertyValue {
  type: 'dateTime'
  data: string
}

export interface EnumPropertyValue {
  type: 'enum'
  data: string
}

export interface SetPropertyValue {
  type: 'setVal'
  data: string
}

export interface BooleanPropertyValue {
  type: 'boolean'
  data: boolean
}

export type PropertyValue =
  | UnknownPropertyValue
  | StringPropertyValue
  | DateTimePropertyValue
  | EnumPropertyValue
  | SetPropertyValue
  | BooleanPropertyValue

export interface PropertyResult {
  name: string
  value: PropertyValue
  typeId: number
  objectId: number
}
