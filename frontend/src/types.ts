export type Choice = {
  id: string
  text: string
}

export type QuestionView = {
  id: string
  topic: string
  difficulty: number
  elo: number
  type: 'single' | 'multi' | 'code'
  prompt: string
  code?: string | null
  choices: Choice[]
}

export type EloStatus = {
  elo: number
  answered: number
  correct: number
}

export type AnswerResult = {
  correct: boolean
  expected: string[]
  explanation: string
  trap?: string | null
  playerElo: number
  questionElo: number
  eloDelta: number
}
