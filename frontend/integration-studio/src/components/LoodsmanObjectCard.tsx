'use client'

import { Fragment, type ReactNode } from 'react'
import { ChevronRight, Layers, Link2, ListTree, Tag } from 'lucide-react'
import type {
  ILoodsmanAttribute,
  ILoodsmanLink,
  ILoodsmanObjectInfo,
} from '@/shared/types/loodsmanObjectInfo.interface'

interface LoodsmanObjectCardProps {
  info: ILoodsmanObjectInfo
  onSelectVersion: (idVersion: number) => void
}

const boxStyle = { backgroundColor: '#f4f1ea', border: '1px solid #d2b48c' }
const cardBoxStyle = { backgroundColor: '#fdf6ee', borderColor: '#d2b48c' }
const currentVersionStyle = {
  backgroundColor: '#fdf6ee',
  border: '1px solid #8b4513',
}

const formatText = (value?: string | null): string => value ?? '—'

const formatNumber = (value?: number | null): string =>
  value === null || value === undefined ? '—' : String(value)

const rowGridClass = 'grid grid-cols-[minmax(0,220px)_1fr] gap-x-3'

const SectionTitle = ({ icon, title }: { icon: ReactNode; title: string }) => (
  <div className="flex items-center gap-2 mb-2">
    <span className="text-stone-700">{icon}</span>
    <h4 className="text-sm font-semibold text-stone-800">{title}</h4>
  </div>
)

const AttributeRows = ({
  attributes,
  emptyText,
}: {
  attributes: ILoodsmanAttribute[]
  emptyText: string
}) => {
  if (attributes.length === 0) {
    return <p className="text-sm text-stone-500">{emptyText}</p>
  }

  return (
    <div className={`${rowGridClass} text-sm [&>*:nth-last-child(-n+2)]:border-b-0`}>
      {attributes.map((attribute, index) => (
        <Fragment key={`${attribute.name ?? 'name'}-${index}`}>
          <div className="py-1.5 min-w-0 border-b border-stone-200 text-stone-600 truncate">
            {formatText(attribute.name)}
          </div>
          <div className="py-1.5 min-w-0 border-b border-stone-200 text-stone-800 break-words">
            {formatText(attribute.value)}
          </div>
        </Fragment>
      ))}
    </div>
  )
}

const LinkRow = ({ label, value }: { label: string; value: string }) => (
  <Fragment>
    <div className="py-1.5 min-w-0 border-b border-stone-200 text-stone-600 truncate">
      {label}
    </div>
    <div className="py-1.5 min-w-0 border-b border-stone-200 text-stone-800 break-words">
      {value}
    </div>
  </Fragment>
)

const LinkBlock = ({ link }: { link: ILoodsmanLink }) => {
  const quantity = link.quantity
  const attributes = link.attributes ?? []

  return (
    <div className="p-3 rounded-lg" style={boxStyle}>
      <p className="text-sm font-semibold text-stone-800">{formatText(link.product)}</p>
      <div
        className={`${rowGridClass} mt-1 text-sm [&>*:nth-last-child(-n+2)]:border-b-0`}
      >
        <LinkRow label="Тип" value={formatText(link.type)} />
        <LinkRow label="Версия" value={formatText(link.version)} />
        {quantity?.value != null ? (
          <LinkRow label="Количество" value={String(quantity.value)} />
        ) : (
          <>
            <LinkRow label="Мин. количество" value={formatNumber(quantity?.min)} />
            <LinkRow label="Макс. количество" value={formatNumber(quantity?.max)} />
          </>
        )}
        {attributes.map((attribute, index) => (
          <LinkRow
            key={`${attribute.name ?? 'name'}-${index}`}
            label={formatText(attribute.name)}
            value={formatText(attribute.value)}
          />
        ))}
      </div>
      {attributes.length === 0 && (
        <p className="text-sm text-stone-500">Нет атрибутов связи</p>
      )}
    </div>
  )
}

const LoodsmanObjectCard = ({ info, onSelectVersion }: LoodsmanObjectCardProps) => {
  const propertiesRows = [
    ['Тип', info.properties.type],
    ['Обозначение', info.properties.product],
    ['Версия', info.properties.version],
    ['Состояние', info.properties.state],
  ] as const

  const versions = info.versions ?? []
  const cardAttributes = info.attributes ?? []
  const links = info.links ?? []

  return (
    <div
      className="p-4 rounded-xl border-2 shadow-sm"
      style={cardBoxStyle}
    >
      <section className="mb-4">
        <SectionTitle icon={<Tag size={16} />} title="Свойства" />
        <div className="p-3 rounded-lg" style={boxStyle}>
          <div className={`${rowGridClass} text-sm`}>
            {propertiesRows.map(([label, value]) => (
              <Fragment key={label}>
                <div className="py-1 min-w-0 truncate text-stone-600">{label}</div>
                <div className="py-1 min-w-0 break-words text-stone-800">
                  {formatText(value)}
                </div>
              </Fragment>
            ))}
          </div>
        </div>
      </section>

      <section className="mb-4">
        <SectionTitle icon={<Layers size={16} />} title="Версии" />
        {versions.length === 0 ? (
          <p className="text-sm text-stone-500">Нет версий</p>
        ) : (
          <div className="space-y-1.5">
            {versions.map((version) => {
              const isCurrent = version.idVersion === info.idVersion
              return (
                <button
                  key={version.idVersion}
                  type="button"
                  onClick={() => onSelectVersion(version.idVersion)}
                  className="w-full flex items-center justify-between gap-3 px-3 py-2 rounded-lg text-left text-sm transition hover:shadow-sm cursor-pointer"
                  style={isCurrent ? currentVersionStyle : boxStyle}
                  title={
                    isCurrent
                      ? 'Текущая версия'
                      : `Показать версию #${version.idVersion}`
                  }
                >
                  <span className="flex items-center gap-1.5 font-medium text-stone-800">
                    <ChevronRight size={14} className="shrink-0 text-stone-500" />
                    {formatText(version.version)}
                    <span className="text-xs font-normal text-stone-500">
                      #{version.idVersion}
                    </span>
                  </span>
                  <span className="flex items-center gap-3 text-xs text-stone-600 shrink-0">
                    <span>{formatText(version.state)}</span>
                    <span>{formatText(version.dateOfCreate)}</span>
                  </span>
                </button>
              )
            })}
          </div>
        )}
      </section>

      <section className="mb-4">
        <SectionTitle icon={<ListTree size={16} />} title="Атрибуты" />
        <div className="p-3 rounded-lg" style={boxStyle}>
          <AttributeRows attributes={cardAttributes} emptyText="Нет атрибутов" />
        </div>
      </section>

      <section>
        <SectionTitle icon={<Link2 size={16} />} title="Связи" />
        {links.length === 0 ? (
          <p className="text-sm text-stone-500">Нет связей</p>
        ) : (
          <div className="space-y-2">
            {links.map((link) => (
              <LinkBlock key={link.linkId} link={link} />
            ))}
          </div>
        )}
      </section>
    </div>
  )
}

export default LoodsmanObjectCard
