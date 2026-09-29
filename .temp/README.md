# `.temp/` — disposable scaffolding

GUARDRAILS **Pattern 5** + SIGN #4.

| Map | Wat |
|-----|-----|
| **`.temp/`** | Eenmalige / experimentele agent-helpers — **niet committen** (behalve dit README + `.gitkeep`) |
| **`scripts/`** | Vaste tooling (`append-session-audit.py`) |
| **`frontend/scripts/`** | Vaste FE-tooling (`ensure-questions.mjs`) |

## Conventie (geen aparte inventaris)

1. **Bestandsnaam:** `YYYY-MM-DD-korte-slug.ext` (bijv. `2026-09-29-merge-weak-area.mjs`).
2. **Header** in het script/batch-README (eerste regels): wat het doet + hoe te draaien.
3. **Ontdekken:** `Get-ChildItem .temp` — geen handmatige `LOCAL.md` (die loopt achter).
4. Multi-file batch → map `.temp/YYYY-MM-DD-slug/` met één entrypoint + korte `README.md` *in die map* (ook gitignored).

≥3 helpers voor hetzelfde doel in één sessie → stop; stel vaste tooling onder `scripts/` voor.
