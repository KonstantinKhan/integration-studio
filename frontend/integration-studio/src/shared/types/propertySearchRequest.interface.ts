export interface IIdentifiableObject {
  objectId: number
  typeId: number
}

export interface ISimpleConditionRequest {
  enabled: boolean
  searchConditionTargetQualifier: IIdentifiableObject
  operation: number
  options: number
  value: IIdentifiableObject
}

export interface IComplexConditionRequest {
  enabled: boolean
  intersectionType: number
  simpleConditions?: ISimpleConditionRequest[]
  complexConditions?: IComplexConditionRequest[]
}

export interface IDateTimeRangeValue {
  value: string
  useTime: boolean
}

export interface IDateTimeValue {
  objectId: number
  dataType: number
  value: string
  valueFrom: IDateTimeRangeValue
  valueTo: IDateTimeRangeValue
  valueSingle: IDateTimeRangeValue
  useTime: boolean
}

export interface IDateTimePropertyValueRequest {
  objectId: number
  typeId: number
  value: IDateTimeValue
}

export interface IAblePropertyValuesRequest {
  dateTimeProperties?: IDateTimePropertyValueRequest[]
}

export interface IPropertySearchRequest {
  pageNumber: number
  pageSize: number
  ownerScope: IIdentifiableObject
  condition: IComplexConditionRequest
  values: IAblePropertyValuesRequest
}
