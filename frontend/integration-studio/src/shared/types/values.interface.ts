import type { BinaryPropertyValues } from "./binaryPropertyValues.interface"
import type { BooleanPropertyValues } from "./booleanProperyValue.interface"
import type { ColorPropertyValues } from "./colorPropertyValues.interface"
import type { DateTimePropertyValues } from "./dataTimePropertyValues.interface"
import type { DoublePropertyValues } from "./doublePropertyValue.interface"
import type { EnumBoolPropertyValues } from "./enumBoolPropertyValues.interface"
import type { EnumDoublePropertyValues } from "./enumDoublePropertyValues.interface"
import type { EnumIntPropertyValues } from "./enumIntPropertyValues.interface"
import type { EnumPropertyValues } from "./enumPropertyValues.interface"
import type { EnumStringPropertyValues } from "./enumStringPropertyValues.interface"
import type { GuidPropertyValues } from "./guidPropertyValues.interface"
import type { ImagePropertyValues } from "./imagePropertyValues.interface"
import type { IntegerPropertyValues } from "./integerPropertyValues.interface"
import type { OpticPropertyValues } from "./opticPropertyValues.interface"
import type { RtfPropertyValues } from "./rtfProprtyValues.interface"
import type { SetPropertyValues } from "./setPropertyValues.interface"
import type { StringPropertyValues } from "./stringPropertyValues.interface"
import type { TablePropertyValues } from "./tablePropertyValues.interface"

export interface Values {
  doubleProperties: DoublePropertyValues[]
  stringProperties: StringPropertyValues[]
  booleanProperties: BooleanPropertyValues[]
  colorProperties: ColorPropertyValues[]
  opticProperties: OpticPropertyValues[]
  dateTimeProperties: DateTimePropertyValues[]
  imageProperties: ImagePropertyValues[]
  rtfProperties: RtfPropertyValues[]
	enumProperties: EnumPropertyValues[]
	setProperties: SetPropertyValues[]
	integerProperties: IntegerPropertyValues[]
	binaryProperties: BinaryPropertyValues[]
	guidProperties: GuidPropertyValues[]
	enumBoolProperties: EnumBoolPropertyValues[]
	enumDoubleProperties: EnumDoublePropertyValues[]
	enumIntProperties: EnumIntPropertyValues[]
	enumStringProperties: EnumStringPropertyValues[]
	tableProperties: TablePropertyValues[]
}