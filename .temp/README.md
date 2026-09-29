# `.temp/` — disposable scaffolding

GUARDRAILS **Pattern 5** + SIGN #4.

| Map | Wat |
|-----|-----|
| **`.temp/`** | Eenmalige / experimentele agent-helpers (niet committen behalve `.gitkeep`) |
| **`scripts/`** | Vaste tooling (`append-session-audit.py`) |
| **`frontend/scripts/`** | Vaste FE-tooling (`ensure-questions.mjs` voor bootstrap) |

## Nu hier

| Bestand | Rol |
|---------|-----|
| `weak-area-questions.batch.json` | Batch Data/JPA/Actuator-vragen (29-09-2026) |
| `merge-weak-area-questions.mjs` | Merge batch → `frontend/src/data/questions.json` |

```powershell
node .temp/merge-weak-area-questions.mjs
```

Nieuwe ad-hoc scripts: hier toevoegen (liefst met datum in de naam). ≥3 throwaways voor hetzelfde doel → stop en stel vaste `scripts/`-tooling voor.
