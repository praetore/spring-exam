#!/usr/bin/env python3
"""sessionStart: bind one audit log file to this Cursor chat session."""

from __future__ import annotations

import subprocess
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from _lib import emit, read_input, repo_root  # noqa: E402


def main() -> int:
    data = read_input()
    root = repo_root()
    script = root / "scripts" / "append-session-audit.py"
    session_id = data.get("session_id") or data.get("conversation_id")
    if not isinstance(session_id, str) or not session_id.strip():
        session_id = None
    else:
        session_id = session_id.strip()

    extra: dict = {}
    if script.is_file():
        try:
            cmd = [sys.executable, str(script), "ensure"]
            if session_id:
                cmd.extend(["--session-id", session_id])
            else:
                cmd.append("--new")
            completed = subprocess.run(
                cmd,
                cwd=str(root),
                check=False,
                capture_output=True,
                text=True,
                encoding="utf-8",
                errors="replace",
                timeout=15,
            )
            path_line = (completed.stdout or "").strip().splitlines()
            path_hint = path_line[-1] if path_line else "(pad onbekend)"
            extra["additional_context"] = (
                f"Session audit voor deze chat: {path_hint}. "
                "Eén logfile per Cursor-chat; volg session-audit.mdc "
                "(USER/THOUGHT/TOOL/DECISION)."
            )
            if path_hint and path_hint != "(pad onbekend)":
                extra["env"] = {"SPRING_EXAM_SESSION_AUDIT": path_hint}
        except (OSError, subprocess.TimeoutExpired):
            extra["additional_context"] = (
                "Kon append-session-audit.py ensure niet draaien; "
                "maak/hergebruik handmatig .audit/session-*.log."
            )
    else:
        extra["additional_context"] = (
            "scripts/append-session-audit.py ontbreekt; sessielog handmatig aanmaken."
        )
    emit(extra if extra else {"ok": True})
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
