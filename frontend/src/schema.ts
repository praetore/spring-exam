import Ajv2020 from 'ajv/dist/2020.js'
import type { QuestionView } from './types'
import { fetchSchema } from './api'

const ajv = new Ajv2020({ allErrors: true, strict: false })

/** Validates the public question view shape (answer fields are server-only). */
const viewSchema = {
  type: 'object',
  required: ['id', 'topic', 'difficulty', 'elo', 'type', 'prompt', 'choices'],
  properties: {
    id: { type: 'string' },
    topic: { type: 'string' },
    difficulty: { type: 'integer' },
    elo: { type: 'integer' },
    type: { type: 'string', enum: ['single', 'multi', 'code'] },
    prompt: { type: 'string' },
    code: { type: ['string', 'null'] },
    choices: {
      type: 'array',
      minItems: 2,
      items: {
        type: 'object',
        required: ['id', 'text'],
        properties: {
          id: { type: 'string' },
          text: { type: 'string' },
        },
      },
    },
  },
  additionalProperties: true,
} as const

const validateView = ajv.compile(viewSchema)

let fullSchemaLoaded = false

export async function ensureServerSchema(): Promise<void> {
  if (fullSchemaLoaded) return
  const schema = await fetchSchema()
  // Touch the full exam schema so the client proves /api/schema works.
  ajv.compile(schema)
  fullSchemaLoaded = true
}

export function assertQuestionView(question: QuestionView): void {
  if (!validateView(question)) {
    const details = validateView.errors?.map((e) => `${e.instancePath} ${e.message}`).join('; ')
    throw new Error(`Question failed client schema check: ${details}`)
  }
}
