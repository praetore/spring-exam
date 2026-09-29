export type Choice = {
  id: string
  text: string
}

export type Question = {
  id: string
  topic: string
  difficulty: number
  elo: number
  type: 'single' | 'multi' | 'code'
  prompt: string
  code?: string
  choices: Choice[]
  answer: string | string[]
  explanation: string
  trap?: string
}

export type QuestionView = Omit<Question, 'answer' | 'explanation' | 'trap'>

export type EloStatus = {
  elo: number
  answered: number
  correct: number
}

export type AnswerResult = {
  correct: boolean
  expected: string[]
  explanation: string
  trap?: string
  playerElo: number
  questionElo: number
  eloDelta: number
}
