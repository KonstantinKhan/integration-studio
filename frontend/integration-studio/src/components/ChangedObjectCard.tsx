import type { EnrichedSearchResultItem } from '@/shared/types/enrichedSearchResultItem.interface'
import type { PropertyValue } from '@/shared/types/propertyResult.interface'

function renderValue(v: PropertyValue): { text: string; muted?: boolean } {
  switch (v.type) {
    case 'boolean':
      return { text: v.value ? 'Да' : 'Нет' }
    case 'unknown':
      return { text: 'нет значения', muted: true }
    default:
      return { text: v.value }
  }
}

const ChangedObjectCard = ({ item }: { item: EnrichedSearchResultItem }) => {
  return (
    <div
      className="p-5 rounded-xl border-2 shadow-md"
      style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
    >
      <h3 className="text-lg font-bold text-stone-800 mb-3">
        {item.name ?? `Объект ${item.objectId}`}
      </h3>
      {item.properties.length === 0 ? (
        <p className="text-sm text-stone-500">Нет свойств</p>
      ) : (
        <ul className="space-y-1">
          {item.properties.map((prop) => {
            const { text, muted } = renderValue(prop.value)
            return (
              <li
                key={`${prop.typeId}-${prop.objectId}`}
                className={`text-sm text-stone-700${muted ? ' italic text-stone-400' : ''}`}
              >
                <span className="font-medium">{prop.name}:</span>{' '}
                <span>{text}</span>
              </li>
            )
          })}
        </ul>
      )}
    </div>
  )
}

export default ChangedObjectCard
