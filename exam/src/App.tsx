import { useCallback, useEffect, useState } from 'react'
import {
  getStatus,
  getTopics,
  initBank,
  nextQuestion,
  resetElo,
  submitAnswer,
} from './elo'
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

  const loadQuestion = useCallback((selectedTopic: string) => {
    setLoading(true)
    setError(null)
    setResult(null)
    setSelected([])
    try {
      setQuestion(nextQuestion(selectedTopic || undefined))
      setStatus(getStatus())
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
        await initBank()
        setTopics(getTopics())
        setStatus(getStatus())
        loadQuestion('')
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Vragenbank laden mislukt')
        setLoading(false)
      }
    })()
  }, [loadQuestion])

  function toggleChoice(id: string) {
    if (result || !question) return
    if (question.type === 'multi') {
      setSelected((prev) =>
        prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id],
      )
    } else {
      setSelected([id])
    }
  }

  function onSubmit() {
    if (!question || selected.length === 0) return
    setError(null)
    try {
      const answer = submitAnswer(question.id, selected)
      setResult(answer)
      setStatus(getStatus())
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Antwoord mislukt')
    }
  }

  function onReset() {
    setStatus(resetElo())
    loadQuestion(topic)
  }

  return (
    <div className="app">
      <header className="hero">
        <p className="brand">Spring Exam ELO</p>
        <h1>Oefen voor Spring Professional</h1>
        <p className="lede">
          Examengerichte meerkeuzevragen met ELO-scoring. Voortgang blijft lokaal in de browser;
          Spring-concepten oefen je in <code>backend/</code>.
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
        <button type="button" className="ghost" onClick={onReset} disabled={loading}>
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
              loadQuestion(value)
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
        <button type="button" className="ghost" onClick={() => loadQuestion(topic)} disabled={loading}>
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
              const isExpected = result?.expected
                .map((id) => id.toLowerCase())
                .includes(choice.id.toLowerCase())
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
              onClick={onSubmit}
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
                  <strong>Valkuil:</strong> {result.trap}
                </p>
              )}
              <button type="button" className="primary" onClick={() => loadQuestion(topic)}>
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
