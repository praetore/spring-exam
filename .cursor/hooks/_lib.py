"""Shared helpers for Cursor project hooks (stdin JSON → stdout JSON)."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from typing import Any


def read_input() -> dict[str, Any]:
    raw = sys.stdin.read()
    if not raw.strip():
        return {}
    try:
        data = json.loads(raw)
    except json.JSONDecodeError:
        return {"_raw": raw}
    return data if isinstance(data, dict) else {"_value": data}


def emit(payload: dict[str, Any]) -> None:
    sys.stdout.write(json.dumps(payload, ensure_ascii=False))
    sys.stdout.flush()


def allow(**extra: Any) -> None:
    emit({"permission": "allow", **extra})


def deny(user_message: str, agent_message: str | None = None, **extra: Any) -> None:
    emit(
        {
            "permission": "deny",
            "user_message": user_message,
            "agent_message": agent_message or user_message,
            **extra,
        }
    )


def shell_command(data: dict[str, Any]) -> str:
    for key in ("command", "cmd", "shell_command"):
        val = data.get(key)
        if isinstance(val, str) and val.strip():
            return val
    tool_input = data.get("tool_input")
    if isinstance(tool_input, dict):
        for key in ("command", "cmd"):
            val = tool_input.get(key)
            if isinstance(val, str) and val.strip():
                return val
    return ""


def file_path(data: dict[str, Any]) -> str:
    for key in ("path", "file_path", "filePath", "uri"):
        val = data.get(key)
        if isinstance(val, str) and val.strip():
            return val
    tool_input = data.get("tool_input")
    if isinstance(tool_input, dict):
        for key in ("path", "file_path"):
            val = tool_input.get(key)
            if isinstance(val, str) and val.strip():
                return val
    return ""


def repo_root() -> Path:
    return Path.cwd().resolve()


def normalize_path(path: str) -> str:
    return path.replace("\\", "/").lower()


SECRET_PATH_RE = re.compile(
    r"(?:^|/|\\)"
    r"(?:\.env(?:$|\.local$)|"
    r"credentials\.json|secrets\.json|"
    r".*\.pem$|.*\.key$|.*\.p12$|.*\.jks$|"
    r"id_rsa|id_ed25519)",
    re.IGNORECASE,
)

DESTRUCTIVE_GIT_RE = re.compile(
    r"\bgit\s+(?:push\s+.*--force(?:-with-lease)?|"
    r"reset\s+--hard|"
    r"clean\s+-[a-z]*f|"
    r"checkout\s+--\s+\.|"
    r"branch\s+-D\b)",
    re.IGNORECASE,
)

QUESTIONS_JSON_RE = re.compile(
    r"(^|[/\\])frontend[/\\]src[/\\]data[/\\]questions\.json$",
    re.IGNORECASE,
)


def is_secret_path(path: str) -> bool:
    return bool(SECRET_PATH_RE.search(normalize_path(path)))


def is_questions_json(path: str) -> bool:
    return bool(QUESTIONS_JSON_RE.search(normalize_path(path)))


def denial_log_path() -> Path:
    p = repo_root() / ".audit" / "hook-denials.log"
    p.parent.mkdir(parents=True, exist_ok=True)
    return p


def log_denial(kind: str, detail: str) -> None:
    from datetime import datetime, timezone

    line = f"{datetime.now(timezone.utc).isoformat()} kind={kind} detail={detail[:500]}\n"
    try:
        with denial_log_path().open("a", encoding="utf-8") as fh:
            fh.write(line)
    except OSError:
        pass
