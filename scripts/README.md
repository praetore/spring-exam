# Scripts (vaste agent-tooling)

Ad-hoc helpers horen in **`.temp/`** (zie `.temp/README.md`), niet hier (GUARDRAILS Pattern 5).

| Script | Rol |
|--------|-----|
| `append-session-audit.py` | Ensure/append plain-text sessielogs onder `.audit/` |
| `../frontend/scripts/ensure-questions.mjs` | Kopieert `questions.example.json` → `questions.json` indien ontbreekt |

Vraag-batches / eenmalige merges → `.temp/` (bijv. `merge-weak-area-questions.mjs`).

## Session audit

```powershell
python scripts/append-session-audit.py ensure
python scripts/append-session-audit.py ensure --session-id <cursor-session-id>
python scripts/append-session-audit.py path

python scripts/append-session-audit.py append `
  --user "Parafrase" `
  --thought "Afwegin" `
  --decision "Conclusie" `
  --tool "name=Shell || why=... || args=... || result=..."
```

Pointer: `.audit/current`. Binding: `.audit/bindings/<session_id>`. Env (via hook): `SPRING_EXAM_SESSION_AUDIT`.

Geen extra pip-deps (stdlib `argparse` only).
