# Agentic patterns — status in spring-exam

Inventaris van [awesome-agentic-patterns](https://www.agentic-patterns.com) t.o.v. de AI-agent tooling in deze repo.

Geïnspireerd op `../rws/AGENTIC-PATTERNS.md`, geschaald voor een **persoonlijke** Spring-examenrepo (geen gitjoin-sync, geen GitLab-fleet).

Bron: [agentic-patterns.com](https://www.agentic-patterns.com) · [llms.txt](https://www.agentic-patterns.com/llms.txt)  
Laatst bijgewerkt: 29-09-2026

---

## Legenda

| Status | Betekenis |
|--------|-----------|
| **Geïmplementeerd** | Bewust aanwezig in rules, skills, scripts of GUARDRAILS |
| **Deels** | Kerngedachte zit erin; uitvoering is incompleet of ad-hoc |
| **Potentieel** | Past hier; nog niet (of nauwelijks) gebouwd |
| **Skip** | Lage ROI voor solo examenleren |

---

## Geïmplementeerd

| Pattern | Waar | Opmerking |
|---------|------|-----------|
| [Team-Shared Agent Configuration as Code](https://www.agentic-patterns.com/patterns/team-shared-agent-configuration) | `.cursor/`, `GUARDRAILS.md`, `AGENTS.md` | Config als code in de repo |
| [Versioned Constitution Governance](https://www.agentic-patterns.com/patterns/versioned-constitution-governance) | `GUARDRAILS.md` + SIGNs | MUST + pointer; how-to in skills/rules |
| [Filesystem-Based Agent State](https://www.agentic-patterns.com/patterns/filesystem-based-agent-state) | `.audit/session-*.log`, `current`, bindings | Sessie buiten contextvenster |
| [Verbose Reasoning Transparency](https://www.agentic-patterns.com/patterns/verbose-reasoning-transparency) | `session-audit.mdc` | USER/THOUGHT/TOOL/DECISION |
| [Context Window Anxiety / Context Rotation](https://www.agentic-patterns.com/patterns/context-window-anxiety-management) | GUARDRAILS Pattern 2 | Samenvatten + human check-in |
| [Privilege Boundaries](https://www.agentic-patterns.com/patterns/tool-capability-compartmentalization) (variant) | GUARDRAILS Pattern 3 + hooks | Secrets / destructieve git |
| [Agent Circuit Breaker](https://www.agentic-patterns.com/patterns/agent-circuit-breaker) (variant) | GUARDRAILS Pattern 4 + SIGN #1 | Rate limits; stop bij auth-fails |
| [Disposable Scaffolding Over Durable Features](https://www.agentic-patterns.com/patterns/disposable-scaffolding-over-durable-features) | `.temp/` vs `scripts/` + SIGN #4 | Ad-hoc ≠ vaste tooling |
| [Human-in-the-Loop Approval Framework](https://www.agentic-patterns.com/patterns/human-in-loop-approval-framework) (variant) | User rules + GUARDRAILS | Commit/push alleen op verzoek |
| [Hook-Based Safety Guard Rails](https://www.agentic-patterns.com/patterns/hook-based-safety-guard-rails) | `.cursor/hooks.json` | sessionStart, secret read, destructive git |
| [Denial Tracking](https://www.agentic-patterns.com/patterns/denial-tracking-permission-escalation) (variant) | `.audit/hook-denials.log` | Via hooks |
| [Curated Domain Context](https://www.agentic-patterns.com/patterns/layered-configuration-context) (variant) | `EXAMEN.md`, skill `elo-questions`, schema | Exam syllabus + vraaggeneratie on demand |

---

## Deels

| Pattern | Wat er is | Gat |
|---------|-----------|-----|
| [CLI-First Skill Design](https://www.agentic-patterns.com/patterns/cli-first-skill-design) | `append-session-audit.py`, `frontend/scripts/ensure-questions.mjs` | Geen Ajv-CLI validate-bank script |
| [Output Verification Loop](https://www.agentic-patterns.com/patterns/output-verification-loop) | `local-verify.mdc` | Agent draait checks niet altijd vóór “klaar” |
| [Plan-Then-Execute](https://www.agentic-patterns.com/patterns/plan-then-execute-pattern) | Cursor Plan mode | Geen projectbrede “altijd eerst plannen”-rule |
| [Skill Library Evolution](https://www.agentic-patterns.com/patterns/skill-library-evolution) | skill `elo-questions` | Geen promote-lus `.temp/` → script |

---

## Potentieel

| Prio | Pattern | Mogelijke vorm |
|------|---------|----------------|
| 1 | [Memory Synthesis from Execution Logs](https://www.agentic-patterns.com/patterns/memory-synthesis-from-execution-logs) | Periodiek SIGNs/lessen uit `.audit/` |
| 2 | [Spec-As-Test Feedback Loop](https://www.agentic-patterns.com/patterns/spec-as-test-feedback-loop) | Schema → validate-bank CLI vóór ELO-append |
| 3 | [Feature List as Immutable Contract](https://www.agentic-patterns.com/patterns/feature-list-as-immutable-contract) | Examenchecklist in `EXAMEN.md` als scope-contract |

---

## Skip

- Multi-agent mesh / RL / fleet orchestration
- Gitjoin sync host→joins (geen monorepo)
- GitLab MR/Renovate-skills (niet relevant hier)

---

## Onderhoud

Na substantiële AI-tooling-wijziging: status in deze file bijwerken.
