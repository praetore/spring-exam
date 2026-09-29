# Cursor project hooks (safety)

Geschaald van `../rws/.cursor/hooks` — solo examenrepo, zonder sync/VPN/GitLab-hooks.

Config: `.cursor/hooks.json` · scripts: `.cursor/hooks/*.py`

| Event | Script | Gedrag |
|-------|--------|--------|
| `sessionStart` | `session_start.py` | `ensure --session-id` + env `SPRING_EXAM_SESSION_AUDIT` |
| `beforeShellExecution` | `before_shell_guard.py` | Deny: destructive git, secret paths, `questions.json` commit |
| `beforeReadFile` | `before_read_secret.py` | Deny: `.env` / credentials / keys |
| `postToolUseFailure` | `post_tool_failure.py` | Append `.audit/hook-denials.log` |

Alle hooks zijn **fail-open** (crash → `allow`). Python 3 op PATH vereist.

```powershell
echo '{"command":"git push --force origin HEAD"}' | python .cursor/hooks/before_shell_guard.py
echo '{"path":".env"}' | python .cursor/hooks/before_read_secret.py
echo '{}' | python .cursor/hooks/session_start.py
```
