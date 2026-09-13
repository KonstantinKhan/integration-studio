export interface ILoodsmanTreeNode {
  id: number
  idType: number
  hasLink: boolean
  product?: string | null
  version?: string | null
  idState: number
  idLock: number
  accessLevel: number
  label: number
  labelName?: string | null
  idLink?: number | null
  idLinkType?: number | null
  minQuantity?: number | null
  maxQuantity?: number | null
}
