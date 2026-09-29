import questionsJson from './data/questions.json'
import { assertQuestions } from './schema'
import type { AnswerResult, EloStatus, Question, QuestionView } from './types'

const STARTING_ELO = 1000
const K_FACTOR = 32
const STORAGE_KEY = 'spring-exam-elo'
/** Don't repeat a question within this many draws (capped by bank size). */
const RECENT_LIMIT = 8
/** Pick randomly among the N closest ELO matches. */
const CANDIDATE_WINDOW = 5

type StoredState = {
  playerElo: number
  answered: number
  correct: number
  questionElo: Record<string, number>
  /** Recently shown/answered question ids (oldest → newest). */
  recentIds: string[]
}

let bank: Question[] | null = null
let bankLoading: Promise<Question[]> | null = null

function asAnswers(answer: string | string[]): string[] {
  return Array.isArray(answer) ? answer : [answer]
}

function normalize(ids: string[]): Set<string> {
  return new Set(ids.map((id) => id.toLowerCase()))
}

function loadState(questions: Question[]): StoredState {
  const defaults: StoredState = {
    playerElo: STARTING_ELO,
    answered: 0,
    correct: 0,
    questionElo: Object.fromEntries(questions.map((q) => [q.id, q.elo])),
    recentIds: [],
  }
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return defaults
    const parsed = JSON.parse(raw) as Partial<StoredState>
    return {
      ...defaults,
      ...parsed,
      questionElo: { ...defaults.questionElo, ...parsed.questionElo },
      recentIds: Array.isArray(parsed.recentIds) ? parsed.recentIds : [],
    }
  } catch {
    return defaults
  }
}

function pushRecent(state: StoredState, questionId: string, poolSize: number): void {
  const limit = Math.max(1, Math.min(RECENT_LIMIT, poolSize - 1))
  state.recentIds = [...state.recentIds.filter((id) => id !== questionId), questionId].slice(
    -limit,
  )
}

function saveState(state: StoredState): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
}

function eloDelta(player: number, question: number, won: boolean): number {
  const expected = 1 / (1 + 10 ** ((question - player) / 400))
  const score = won ? 1 : 0
  return Math.round(K_FACTOR * (score - expected))
}

function toView(question: Question): QuestionView {
  const { answer: _a, explanation: _e, trap: _t, ...view } = question
  return view
}

export async function initBank(): Promise<Question[]> {
  if (bank) return bank
  if (bankLoading) return bankLoading
  bankLoading = (async () => {
    const questions = questionsJson as Question[]
    await assertQuestions(questions)
    bank = questions
    return bank
  })()
  try {
    return await bankLoading
  } catch (error) {
    bankLoading = null
    throw error
  }
}

export function getStatus(): EloStatus {
  const questions = bank ?? (questionsJson as Question[])
  const state = loadState(questions)
  return {
    elo: state.playerElo,
    answered: state.answered,
    correct: state.correct,
  }
}

export function getTopics(): string[] {
  const questions = bank ?? (questionsJson as Question[])
  return [...new Set(questions.map((q) => q.topic))].sort()
}

export function resetElo(): EloStatus {
  const questions = bank ?? (questionsJson as Question[])
  const state: StoredState = {
    playerElo: STARTING_ELO,
    answered: 0,
    correct: 0,
    questionElo: Object.fromEntries(questions.map((q) => [q.id, q.elo])),
    recentIds: [],
  }
  saveState(state)
  return getStatus()
}

export function nextQuestion(topic?: string): QuestionView {
  const questions = bank ?? (questionsJson as Question[])
  const state = loadState(questions)
  const filtered = questions.filter(
    (q) => !topic || q.topic.toLowerCase() === topic.toLowerCase(),
  )
  if (filtered.length === 0) {
    throw new Error('Geen vraag voor dit topic')
  }

  const recent = new Set(state.recentIds)
  let pool = filtered.filter((q) => !recent.has(q.id))
  if (pool.length === 0) pool = filtered

  // When browsing all topics, avoid topics seen in the last few draws.
  if (!topic && state.recentIds.length > 0) {
    const recentTopics = new Set(
      state.recentIds
        .slice(-3)
        .map((id) => questions.find((q) => q.id === id)?.topic)
        .filter((t): t is string => Boolean(t)),
    )
    const diversified = pool.filter((q) => !recentTopics.has(q.topic))
    if (diversified.length > 0) {
      pool = diversified
    } else {
      const lastTopic = questions.find((q) => q.id === state.recentIds.at(-1))?.topic
      if (lastTopic) {
        const differently = pool.filter((q) => q.topic !== lastTopic)
        if (differently.length > 0) pool = differently
      }
    }
  }

  const scored = pool
    .map((q) => ({
      q,
      dist: Math.abs((state.questionElo[q.id] ?? q.elo) - state.playerElo),
    }))
    .sort((a, b) => a.dist - b.dist)

  const window = scored.slice(0, Math.min(CANDIDATE_WINDOW, scored.length))
  const pick = window[Math.floor(Math.random() * window.length)].q

  pushRecent(state, pick.id, filtered.length)
  saveState(state)
  return toView(pick)
}

export function submitAnswer(questionId: string, choiceIds: string[]): AnswerResult {
  const questions = bank ?? (questionsJson as Question[])
  const question = questions.find((q) => q.id === questionId)
  if (!question) {
    throw new Error('Onbekende vraag')
  }
  const state = loadState(questions)
  const expected = asAnswers(question.answer)
  const correct = setsEqual(normalize(choiceIds), normalize(expected))
  const qElo = state.questionElo[question.id] ?? question.elo
  const delta = eloDelta(state.playerElo, qElo, correct)
  state.playerElo += delta
  state.questionElo[question.id] = Math.max(0, qElo - delta)
  state.answered += 1
  if (correct) state.correct += 1
  saveState(state)

  return {
    correct,
    expected,
    explanation: question.explanation,
    trap: question.trap,
    playerElo: state.playerElo,
    questionElo: state.questionElo[question.id],
    eloDelta: delta,
  }
}

function setsEqual(a: Set<string>, b: Set<string>): boolean {
  if (a.size !== b.size) return false
  for (const value of a) {
    if (!b.has(value)) return false
  }
  return true
}
