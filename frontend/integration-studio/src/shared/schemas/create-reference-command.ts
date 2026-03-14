import * as z from 'zod'

const CreateReferenceResponseSchema = z.object({
  id: z.string().nullable(),
  name: z.string().nullable(),
  typeId: z.number().int(),
  objectId: z.number().int(),
})

export type CreateReferenceResponse = z.infer<
  typeof CreateReferenceResponseSchema
>
