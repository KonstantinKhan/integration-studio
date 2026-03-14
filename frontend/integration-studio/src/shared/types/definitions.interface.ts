import type { BinaryProperty } from "./binaryProperty.interface";
import type { BooleanProperty } from "./booleanProperty.interface";
import type { ColorProperty } from "./colorProperty.interface";
import type { DateTimeProperty } from "./dateTimeProperty.interface";
import type { DoubleProperty } from "./doubleProperty.interface";
import type { EnumBoolProperty } from "./enumBoolProperty.interface";
import type { EnumDoubleProperty } from "./enumDoubleProperty.interface";
import type { EnumIntProperty } from "./enumIntProperty.interface";
import type { EnumProperty } from "./enumProperty.interface";
import type { EnumStringProperty } from "./enumStringProperty.interface";
import type { GuidProperty } from "./guidProperty.interface";
import type { ImageProperty } from "./imageProperty.interface";
import type { IntegerProperty } from "./integerProperty.interface";
import type { OpticProperty } from "./opticProperty.interface";
import type { RtfProperty } from "./rtfProperty.interface";
import type { SetProperty } from "./setProperty.interface";
import type { StringProperty } from "./stringProperty.interface";
import type { TableProperty } from "./tableProperty.interface";

export interface Definitions {
	doubleProperties: DoubleProperty[],
	stringProperties: StringProperty[],
	booleanProperties?: BooleanProperty[],
	colorProperties?: ColorProperty[],
	opticProperties?: OpticProperty[],
	dateTimeProperties: DateTimeProperty[],
	imageProperties: ImageProperty[],
	rtfProperties: RtfProperty[],
	enumProperties: EnumProperty[],
	setProperties?: SetProperty[],
	integerProperties?: IntegerProperty[],
	binaryProperties?: BinaryProperty[],
	guidProperties?: GuidProperty[],
	enumBoolProperties?: EnumBoolProperty[],
	enumDoubleProperties?: EnumDoubleProperty[],
	enumIntProperties?: EnumIntProperty[],
	enumStringProperties?: EnumStringProperty[],
	tableProperties?: TableProperty[]
}