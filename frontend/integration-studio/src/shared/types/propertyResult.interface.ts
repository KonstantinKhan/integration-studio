export interface UnknownPropertyValue {
  type: 'unknown'
  value: string
  typeId: number
  objectId: number
}

export interface StringPropertyValue {
  type: 'string'
  value: string
  typeId: number
  objectId: number
}

export interface DateTimePropertyValue {
  type: 'dateTime'
  value: string
  typeId: number
  objectId: number
}

export interface EnumPropertyValue {
  type: 'enum'
  value: string
  typeId: number
  objectId: number
}

export interface SetPropertyValue {
  type: 'setVal'
  value: string
  typeId: number
  objectId: number
}

export interface BooleanPropertyValue {
  type: 'boolean'
  value: boolean
  typeId: number
  objectId: number
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
