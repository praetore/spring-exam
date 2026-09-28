import { useCallback, useEffect, useState } from 'react'
import {
  fetchElo,
  fetchNextQuestion,
  fetchTopics,
  resetElo,
  submitAnswer,
} from './api'
import { assertQuestionView, ensureServerSchema } from './schema'
import type { AnswerResult, EloStatus, QuestionView } from './types'
import './App.css'

export default function App() {
  const [status, setStatus] = useState<EloStatus | null>(null)
  const [topics, setTopics] = useState<string[]>([])
  const [topic, setTopic] = useState('')
  const [question, setQuestion] = useState<QuestionView | null>(null)
  const [selected, setSelected] = useState<string[]>([])
  const [result, setResult] = useState<AnswerResult | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  const loadQuestion = useCallback(async (selectedTopic: string) => {
    setLoading(true)
    setError(null)
    setResult(null)
    setSelected([])
    try {
      await ensureServerSchema()
      const next = await fetchNextQuestion(selectedTopic || undefined)
      assertQuestionView(next)
      setQuestion(next)
      const elo = await fetchElo()
      setStatus(elo)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Laden mislukt')
      setQuestion(null)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void (async () => {
      try {
        const [elo, topicList] = await Promise.all([fetchElo(), fetchTopics()])
        setStatus(elo)
        setTopics(topicList)
        await loadQuestion('')
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Backend niet bereikbaar')
        setLoading(false)
      }
    })()
  }, [loadQuestion])

  function toggleChoice(id: string) {
    if (result) return
    if (!question) return
    if (question.type === 'multi') {
      setSelected((prev) =>
        prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id],
      )
    } else {
      setSelected([id])
    }
  }

  async function onSubmit() {
    if (!question || selected.length === 0) return
    setLoading(true)
    setError(null)
    try {
      const answer = await submitAnswer(question.id, selected)
      setResult(answer)
      setStatus({
        elo: answer.playerElo,
        answered: (status?.answered ?? 0) + 1,
        correct: (status?.correct ?? 0) + (answer.correct ? 1 : 0),
      })
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Antwoord mislukt')
    } finally {
      setLoading(false)
    }
  }

  async function onReset() {
    setLoading(true)
    try {
      const elo = await resetElo()
      setStatus(elo)
      await loadQuestion(topic)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Reset mislukt')
      setLoading(false)
    }
  }

  return (
    <div className="app">
      <header className="hero">
        <p className="brand">Spring Exam ELO</p>
        <h1>Train de instinkers</h1>
        <p className="lede">
          Meerkeuzevragen tegen je rating. Schema-gevalideerd, server-side gescoord.
        </p>
      </header>

      <section className="panel stats" aria-live="polite">
        <div>
          <span className="label">Jouw ELO</span>
          <strong className="mono">{status?.elo ?? '—'}</strong>
        </div>
        <div>
          <span className="label">Beantwoord</span>
          <strong className="mono">{status?.answered ?? 0}</strong>
        </div>
        <div>
          <span className="label">Goed</span>
          <strong className="mono">{status?.correct ?? 0}</strong>
        </div>
        <button type="button" className="ghost" onClick={() => void onReset()} disabled={loading}>
          Reset ELO
        </button>
      </section>

      <section className="panel controls">
        <label>
          Topic
          <select
            value={topic}
            onChange={(e) => {
              const value = e.target.value
              setTopic(value)
              void loadQuestion(value)
            }}
            disabled={loading}
          >
            <option value="">alle</option>
            {topics.map((t) => (
              <option key={t} value={t}>
                {t}
              </option>
            ))}
          </select>
        </label>
        <button type="button" className="ghost" onClick={() => void loadQuestion(topic)} disabled={loading}>
          Volgende
        </button>
      </section>

      {error && <p className="error">{error}</p>}

      {question && (
        <section className="panel question">
          <div className="meta">
            <span className="tag">{question.topic}</span>
            <span className="mono">diff {question.difficulty}</span>
            <span className="mono">q-elo {question.elo}</span>
          </div>
          <h2>{question.prompt}</h2>
          {question.code && <pre className="code">{question.code}</pre>}

          <ul className="choices">
            {question.choices.map((choice) => {
              const active = selected.includes(choice.id)
              const showResult = Boolean(result)
              const isExpected = result?.expected.includes(choice.id)
              let state = ''
              if (showResult && isExpected) state = 'correct'
              else if (showResult && active && !isExpected) state = 'wrong'
              else if (active) state = 'active'

              return (
                <li key={choice.id}>
                  <button
                    type="button"
                    className={`choice ${state}`}
                    onClick={() => toggleChoice(choice.id)}
                    disabled={Boolean(result) || loading}
                  >
                    <span className="choice-id mono">{choice.id}</span>
                    <span>{choice.text}</span>
                  </button>
                </li>
              )
            })}
          </ul>

          {!result ? (
            <button
              type="button"
              className="primary"
              onClick={() => void onSubmit()}
              disabled={loading || selected.length === 0}
            >
              Controleer
            </button>
          ) : (
            <div className={`feedback ${result.correct ? 'ok' : 'bad'}`}>
              <p className="verdict">
                {result.correct ? 'Goed' : 'Fout'}{' '}
                <span className="mono">
                  ({result.eloDelta >= 0 ? '+' : ''}
                  {result.eloDelta} ELO)
                </span>
              </p>
              <p>{result.explanation}</p>
              {result.trap && (
                <p className="trap">
                  <strong>Instinker:</strong> {result.trap}
                </p>
              )}
              <button type="button" className="primary" onClick={() => void loadQuestion(topic)}>
                Volgende vraag
              </button>
            </div>
          )}
        </section>
      )}

      {!question && !loading && !error && <p className="muted">Geen vraag beschikbaar.</p>}
    </div>
  )
}
