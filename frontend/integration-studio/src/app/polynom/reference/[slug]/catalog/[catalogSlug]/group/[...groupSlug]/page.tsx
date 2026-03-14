'use client'

import { use } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from 'primereact/button'
import { Card } from 'primereact/card'
import { useGroupContent } from '@/hooks/useGroupContent'
import { useElementGroups } from '@/hooks/useElementGroups'
import { useElementProperties } from '@/hooks/useElementProperties'
import { flattenProperties } from '@/utils/flattenProperties'

// ─── Element view ──────────────────────────────────────────────────────────────

function ElementView({
  slug,
  catalogSlug,
  groupSlug,
}: {
  slug: string
  catalogSlug: string
  groupSlug: string[]
}) {
  const router = useRouter()

  // Last segment: "el-typeId-objectId", everything before = actual group path
  const lastSegment = groupSlug[groupSlug.length - 1]
  const [, elementTypeId, elementObjectId] = lastSegment.split('-').map(Number)
  const actualGroupSlug = groupSlug.slice(0, -1)

  const currentGroupSegment = actualGroupSlug[actualGroupSlug.length - 1]
  const [parentGroupTypeId, parentGroupObjectId] = currentGroupSegment
    .split('-')
    .map(Number)
  const { data: groupContent } = useGroupContent(parentGroupTypeId, parentGroupObjectId)
  const elementName =
    groupContent?.elements.find(
      (e) => e.typeId === elementTypeId && e.objectId === elementObjectId,
    )?.name ?? `Элемент ${elementTypeId}-${elementObjectId}`

  const { data, isLoading, error } = useElementProperties(elementTypeId, elementObjectId)

  const handleBack = () => {
    router.push(
      `/polynom/reference/${slug}/catalog/${catalogSlug}/group/${actualGroupSlug.join('/')}`,
    )
  }

  if (isLoading) {
    return (
      <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
        <div className="max-w-4xl mx-auto">
          <div
            className="p-8 rounded-xl border-2 shadow-md animate-pulse"
            style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
          >
            <div className="h-8 bg-stone-300 rounded w-1/2 mb-6"></div>
            <div className="h-5 bg-stone-300 rounded w-full mb-3"></div>
            <div className="h-5 bg-stone-300 rounded w-full mb-3"></div>
            <div className="h-5 bg-stone-300 rounded w-3/4"></div>
          </div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
        <div className="max-w-md mx-auto p-6 bg-red-100 border-2 border-red-400 text-red-700 rounded-xl text-center">
          <i className="pi pi-exclamation-triangle text-3xl mb-3"></i>
          <p className="text-lg">Не удалось загрузить свойства элемента</p>
          <Button
            label="Назад"
            icon="pi pi-arrow-left"
            className="mt-4"
            severity="secondary"
            onClick={handleBack}
          />
        </div>
      </div>
    )
  }

  const rows = data ? flattenProperties(data) : []

  return (
    <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
      <div className="max-w-4xl mx-auto">
        <Button
          label="Назад"
          icon="pi pi-arrow-left"
          className="mb-6"
          severity="secondary"
          text
          onClick={handleBack}
        />

        <Card
          className="shadow-md rounded-xl border-2 mb-8"
          style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
        >
          <div className="flex items-center gap-3">
            <i className="pi pi-file text-3xl" style={{ color: '#8b4513' }}></i>
            <h1 className="text-3xl font-bold text-stone-800">{elementName}</h1>
          </div>
        </Card>

        <h2 className="text-2xl font-bold text-stone-800 mb-4">Свойства</h2>

        {rows.length === 0 ? (
          <div
            className="p-6 rounded-xl border-2 text-center"
            style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
          >
            <p className="text-stone-600">Нет свойств</p>
          </div>
        ) : (
          <div
            className="rounded-xl border-2 overflow-hidden shadow-md"
            style={{ borderColor: '#d2b48c' }}
          >
            <table className="w-full border-collapse">
              <thead>
                <tr style={{ backgroundColor: '#d2b48c' }}>
                  <th className="text-left p-3 font-semibold text-stone-800 w-1/2">
                    Название
                  </th>
                  <th className="text-left p-3 font-semibold text-stone-800 w-1/2">
                    Значение
                  </th>
                </tr>
              </thead>
              <tbody>
                {rows.map((row, i) => (
                  <tr
                    key={row.id}
                    style={{
                      backgroundColor: i % 2 === 0 ? '#f4f1ea' : '#ede9df',
                      borderBottom: '1px solid #d2b48c',
                    }}
                  >
                    <td className="p-3 text-stone-700 font-medium">{row.name}</td>
                    <td className="p-3 text-stone-800">{row.value}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

// ─── Group view ────────────────────────────────────────────────────────────────

function GroupView({
  slug,
  catalogSlug,
  groupSlug,
}: {
  slug: string
  catalogSlug: string
  groupSlug: string[]
}) {
  const router = useRouter()

  const currentSegment = groupSlug[groupSlug.length - 1]
  const [typeId, objectId] = currentSegment.split('-').map(Number)

  const { data, isLoading, error } = useGroupContent(typeId, objectId)

  const [catalogTypeId, catalogObjectId] = catalogSlug.split('-').map(Number)
  const { data: catalogGroups = [] } = useElementGroups(catalogTypeId, catalogObjectId)

  const hasParent = groupSlug.length > 1
  const [parentTypeId, parentObjectId] = hasParent
    ? groupSlug[groupSlug.length - 2].split('-').map(Number)
    : [0, 0]
  const { data: parentContent } = useGroupContent(parentTypeId, parentObjectId, hasParent)

  const groupName = !hasParent
    ? catalogGroups.find((g) => g.typeId === typeId && g.objectId === objectId)?.name
    : parentContent?.groups.find((g) => g.typeId === typeId && g.objectId === objectId)?.name

  const basePath = `/polynom/reference/${slug}/catalog/${catalogSlug}`

  const handleBack = () => {
    if (groupSlug.length === 1) {
      router.push(basePath)
    } else {
      router.push(`${basePath}/group/${groupSlug.slice(0, -1).join('/')}`)
    }
  }

  const handleElementClick = (elementTypeId: number, elementObjectId: number) => {
    router.push(
      `${basePath}/group/${groupSlug.join('/')}/el-${elementTypeId}-${elementObjectId}`,
    )
  }

  const handleGroupClick = (groupTypeId: number, groupObjectId: number) => {
    router.push(`${basePath}/group/${groupSlug.join('/')}/${groupTypeId}-${groupObjectId}`)
  }

  if (isLoading) {
    return (
      <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
        <div className="max-w-2xl mx-auto">
          <div
            className="p-8 rounded-xl border-2 shadow-md animate-pulse"
            style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
          >
            <div className="h-8 bg-stone-300 rounded w-1/2 mb-6"></div>
            <div className="h-5 bg-stone-300 rounded w-1/3 mb-3"></div>
            <div className="h-5 bg-stone-300 rounded w-1/4"></div>
          </div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
        <div className="max-w-md mx-auto p-6 bg-red-100 border-2 border-red-400 text-red-700 rounded-xl text-center">
          <i className="pi pi-exclamation-triangle text-3xl mb-3"></i>
          <p className="text-lg">Не удалось загрузить данные группы</p>
          <Button
            label="Назад"
            icon="pi pi-arrow-left"
            className="mt-4"
            severity="secondary"
            onClick={handleBack}
          />
        </div>
      </div>
    )
  }

  const groups = data?.groups ?? []
  const elements = data?.elements ?? []

  return (
    <div className="min-h-screen bg-linear-to-br from-stone-100 via-amber-50 to-yellow-50 p-8">
      <div className="max-w-2xl mx-auto">
        <Button
          label="Назад"
          icon="pi pi-arrow-left"
          className="mb-6"
          severity="secondary"
          text
          onClick={handleBack}
        />

        <Card
          className="shadow-md rounded-xl border-2"
          style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
        >
          <div className="flex items-center gap-3 mb-6">
            <i className="pi pi-th-large text-3xl" style={{ color: '#8b4513' }}></i>
            <h1 className="text-3xl font-bold text-stone-800">
              {groupName ?? 'Группа'}
            </h1>
          </div>
        </Card>

        <h2 className="text-2xl font-bold text-stone-800 mt-8 mb-4">Вложенные группы</h2>

        {groups.length === 0 && (
          <div
            className="p-6 rounded-xl border-2 text-center"
            style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
          >
            <p className="text-stone-600">Нет вложенных групп</p>
          </div>
        )}

        {groups.length > 0 && (
          <div className="space-y-3">
            {groups.map((group) => (
              <div
                key={group.id}
                className="p-4 rounded-xl border-2 shadow-sm hover:shadow-xl transition-all duration-300 hover:scale-105 cursor-pointer"
                style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
                onClick={() => handleGroupClick(group.typeId, group.objectId)}
              >
                <div className="flex items-center gap-3">
                  <i className="pi pi-th-large text-xl" style={{ color: '#8b4513' }}></i>
                  <span className="font-semibold text-stone-800">{group.name}</span>
                </div>
                <div className="mt-2 text-sm text-stone-600 flex gap-4">
                  <span>Type ID: {group.typeId}</span>
                  <span>Object ID: {group.objectId}</span>
                </div>
              </div>
            ))}
          </div>
        )}

        <h2 className="text-2xl font-bold text-stone-800 mt-8 mb-4">Элементы</h2>

        {elements.length === 0 && (
          <div
            className="p-6 rounded-xl border-2 text-center"
            style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
          >
            <p className="text-stone-600">Нет элементов</p>
          </div>
        )}

        {elements.length > 0 && (
          <div className="space-y-3">
            {elements.map((element) => (
              <div
                key={element.id}
                className="p-4 rounded-xl border-2 shadow-sm hover:shadow-xl transition-all duration-300 hover:scale-105 cursor-pointer"
                style={{ backgroundColor: '#f4f1ea', borderColor: '#d2b48c' }}
                onClick={() => handleElementClick(element.typeId, element.objectId)}
              >
                <div className="flex items-center gap-3">
                  <i className="pi pi-file text-xl" style={{ color: '#8b4513' }}></i>
                  <span className="font-semibold text-stone-800">{element.name}</span>
                </div>
                <div className="mt-2 text-sm text-stone-600 flex gap-4">
                  <span>Type ID: {element.typeId}</span>
                  <span>Object ID: {element.objectId}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

// ─── Page dispatcher ───────────────────────────────────────────────────────────

export default function GroupPage({
  params,
}: {
  params: Promise<{ slug: string; catalogSlug: string; groupSlug: string[] }>
}) {
  const { slug, catalogSlug, groupSlug } = use(params)

  const lastSegment = groupSlug[groupSlug.length - 1]

  if (lastSegment.startsWith('el-')) {
    return <ElementView slug={slug} catalogSlug={catalogSlug} groupSlug={groupSlug} />
  }

  return <GroupView slug={slug} catalogSlug={catalogSlug} groupSlug={groupSlug} />
}
