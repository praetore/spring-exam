import type { AnswerResult, EloStatus, QuestionView } from './types'

async function parseJson<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const text = await response.text()
    throw new Error(text || `HTTP ${response.status}`)
  }
  return response.json() as Promise<T>
}

export function fetchElo(): Promise<EloStatus> {
  return fetch('/api/elo').then((r) => parseJson<EloStatus>(r))
}

export function resetElo(): Promise<EloStatus> {
  return fetch('/api/elo/reset', { method: 'POST' }).then((r) => parseJson<EloStatus>(r))
}

export function fetchTopics(): Promise<string[]> {
  return fetch('/api/topics').then((r) => parseJson<string[]>(r))
}

export function fetchNextQuestion(topic?: string): Promise<QuestionView> {
  const query = topic ? `?topic=${encodeURIComponent(topic)}` : ''
  return fetch(`/api/questions/next${query}`).then((r) => parseJson<QuestionView>(r))
}

export function submitAnswer(questionId: string, choiceIds: string[]): Promise<AnswerResult> {
  return fetch('/api/answers', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ questionId, choiceIds }),
  }).then((r) => parseJson<AnswerResult>(r))
}

export function fetchSchema(): Promise<object> {
  return fetch('/api/schema').then((r) => parseJson<object>(r))
}
