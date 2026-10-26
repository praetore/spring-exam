# `.temp/` — disposable scaffolding

GUARDRAILS **Pattern 5** + SIGN #4.

| Map | Wat |
|-----|-----|
| **`.temp/`** | Eenmalige / experimentele agent-helpers **én** persoonlijke exam-gap notes — **niet committen** (behalve dit README + `.gitkeep`) |
| **`scripts/`** | Vaste tooling (`append-session-audit.py`) |
| **`frontend/scripts/`** | Vaste FE-tooling (`ensure-questions.mjs`) |
| **Repo-root** | Vaste syllabus/docs (`EXAMEN.md`, `AGENTS.md`, …) — **geen** ad-hoc cheatsheets |

## Wat hoort hier (voorbeelden)

- Ad-hoc scripts / merge-helpers
- **Gap-cheatsheets** na een oefenexamen (bijv. `2026-10-23-exam-cheatsheet.md`)
- **Sandbox study-docs** (architectuur + concept→code; sjabloon in skill `sandbox-demo`, bijv. `2026-10-26-core-checkout-study.md`)
- Tijdelijke exports / parse-output van Udemy-results

**Niet hier:** `EXAMEN.md` of andere vaste syllabus — die blijven in de repo-root (git).

## Conventie (geen aparte inventaris)

1. **Bestandsnaam:** `YYYY-MM-DD-korte-slug.ext` (bijv. `2026-09-29-merge-weak-area.mjs`).
2. **Header** in het bestand (eerste regels): wat het is + context (welk oefenexamen / hoe te gebruiken).
3. **Ontdekken:** `Get-ChildItem .temp` — geen handmatige `LOCAL.md` (die loopt achter).
4. Multi-file batch → map `.temp/YYYY-MM-DD-slug/` met één entrypoint + korte `README.md` *in die map* (ook gitignored).

≥3 helpers voor hetzelfde doel in één sessie → stop; stel vaste tooling onder `scripts/` voor.
