export interface ILoodsmanObjectProperties {
  type?: string | null
  product?: string | null
  version?: string | null
  state?: string | null
}

export interface ILoodsmanObjectVersion {
  idVersion: number
  version?: string | null
  state?: string | null
  dateOfCreate?: string | null
}

export interface ILoodsmanAttribute {
  name?: string | null
  value?: string | null
}

export interface ILoodsmanLinkQuantity {
  value?: number | null
  min?: number | null
  max?: number | null
}

export interface ILoodsmanLink {
  linkId: number
  product?: string | null
  version?: string | null
  type?: string | null
  quantity?: ILoodsmanLinkQuantity
  attributes?: ILoodsmanAttribute[]
}

export interface ILoodsmanObjectInfo {
  idVersion: number
  properties: ILoodsmanObjectProperties
  versions?: ILoodsmanObjectVersion[]
  attributes?: ILoodsmanAttribute[]
  links?: ILoodsmanLink[]
}

