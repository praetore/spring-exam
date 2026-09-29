import Ajv2020 from 'ajv/dist/2020.js'
import type { ErrorObject, ValidateFunction } from 'ajv'
import type { Question } from './types'

const SCHEMA_ID = 'https://praetore.io/schemas/exam-question.json'
const ajv = new Ajv2020({ allErrors: true, strict: false })

let validateQuestion: ValidateFunction | null = null
let loading: Promise<ValidateFunction> | null = null

export async function loadQuestionValidator(): Promise<ValidateFunction> {
  if (validateQuestion) return validateQuestion
  if (loading) return loading

  loading = (async () => {
    const existing = ajv.getSchema(SCHEMA_ID)
    if (existing) {
      validateQuestion = existing as ValidateFunction
      return validateQuestion
    }

    const response = await fetch('/schema/exam-question.schema.json')
    if (!response.ok) {
      throw new Error(`Schema laden mislukt (${response.status})`)
    }
    const schema = await response.json()

    const raced = ajv.getSchema(SCHEMA_ID)
    if (raced) {
      validateQuestion = raced as ValidateFunction
      return validateQuestion
    }

    validateQuestion = ajv.compile(schema)
    return validateQuestion
  })()

  try {
    return await loading
  } catch (error) {
    loading = null
    throw error
  }
}

export async function assertQuestions(questions: Question[]): Promise<void> {
  const validate = await loadQuestionValidator()
  for (const question of questions) {
    const questionId = question.id
    const ok = validate(question as unknown)
    if (!ok) {
      const details = (validate.errors as ErrorObject[] | null | undefined)
        ?.map((e) => `${e.instancePath} ${e.message}`)
        .join('; ')
      throw new Error(`Vraag ${questionId} faalt schema: ${details}`)
    }
  }
}
