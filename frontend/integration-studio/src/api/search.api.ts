import { apiClient } from './api-client'
import type { EnrichedSearchResultItem } from '@/shared/types/enrichedSearchResultItem.interface'

function pad(n: number): string {
  return n.toString().padStart(2, '0')
}

function formatDate(date: Date): string {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T00:00:00.000`
}

function buildRequest(from: Date, to: Date) {
  const fromStr = formatDate(from)
  const toStr = formatDate(to)
  return {
    pageNumber: 1,
    pageSize: 100,
    ownerScope: { objectId: 61, typeId: 48 },
    condition: {
      enabled: true,
      intersectionType: 0,
      complexConditions: [
        {
          enabled: false,
          intersectionType: 0,
          simpleConditions: [
            {
              enabled: true,
              searchConditionTargetQualifier: { objectId: 40, typeId: 54 },
              operation: 6,
              options: 0,
              value: { typeId: 0, objectId: 0 },
            },
            {
              enabled: true,
              searchConditionTargetQualifier: { objectId: 40, typeId: 54 },
              operation: 4,
              options: 0,
              value: { typeId: 0, objectId: 1 },
            },
          ],
        },
      ],
    },
    values: {
      dateTimeProperties: [
        {
          objectId: 0,
          typeId: 0,
          value: {
            objectId: 0,
            dataType: 6,
            value: toStr,
            valueFrom: { value: fromStr, useTime: false },
            valueTo: { value: toStr, useTime: false },
            valueSingle: { value: toStr, useTime: false },
            useTime: false,
          },
        },
        {
          objectId: 1,
          typeId: 0,
          value: {
            objectId: 1,
            dataType: 6,
            value: toStr,
            valueFrom: { value: fromStr, useTime: false },
            valueTo: { value: toStr, useTime: false },
            valueSingle: { value: toStr, useTime: false },
            useTime: false,
          },
        },
      ],
    },
  }
}

export function fetchChangedObjects(
  from: Date,
  to: Date,
): Promise<EnrichedSearchResultItem[]> {
  return apiClient<EnrichedSearchResultItem[]>('/search/changed-objects', {
    method: 'POST',
    body: JSON.stringify(buildRequest(from, to)),
  })
}
