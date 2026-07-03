package com.khan366kos.integration.studio.mapping

import com.khan366kos.integration.studio.bff.dto.models.IdentifierBffDto
import com.khan366kos.integration.studio.bff.dto.request.PolynomElementFromPeriodRequestBffDto
import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.models.properties.values.IDateTimeValue
import com.khan366kos.integration.studio.transport.polynom.request.IAblePropertyValuesRequest
import com.khan366kos.integration.studio.transport.polynom.request.IComplexConditionRequest
import com.khan366kos.integration.studio.transport.polynom.request.IDateTimePropertyValueRequest
import com.khan366kos.integration.studio.transport.polynom.request.search.IPropertySearchRequest
import com.khan366kos.integration.studio.transport.polynom.request.ISimpleConditionRequest

fun PolynomElementFromPeriodRequestBffDto.toPolynomDto() = IPropertySearchRequest(
    ownerScope = scope.toPolynomDto(),
    condition = IComplexConditionRequest(
        enabled = true,
        intersectionType = 0,
        complexConditions = listOf(
            IComplexConditionRequest(
                enabled = false,
                intersectionType = 0,
                simpleConditions = listOf(
                    ISimpleConditionRequest(
                        enabled = true,
                        searchConditionTargetQualifier = IIdentifiableObject(40, 54),
                        operation = 6,
                        options = 0,
                        value = IIdentifiableObject(0, 0)
                    ),
                    ISimpleConditionRequest(
                        enabled = true,
                        searchConditionTargetQualifier = IIdentifiableObject(40, 54),
                        operation = 4,
                        options = 0,
                        value = IIdentifiableObject(1, 0)
                    )
                )
            )
        )
    ),
    values = IAblePropertyValuesRequest(
        dateTimeProperties = listOf(
            IDateTimePropertyValueRequest(
                objectId = 0,
                typeId = 0,
                value = IDateTimeValue(
                    value = from.toString(),
                    useTime = true,
                    objectId = 0,
                    dataType = 6,
                )
            ),
            IDateTimePropertyValueRequest(
                objectId = 1,
                typeId = 0,
                value = IDateTimeValue(
                    value = to.toString(),
                    useTime = true,
                    objectId = 1,
                    dataType = 6,
                )
            )
        )
    ),
    pageNumber = 1,
    pageSize = 100
)

fun IdentifierBffDto.toPolynomDto() = IIdentifiableObject(
    objectId = objectId,
    typeId = typeId
)