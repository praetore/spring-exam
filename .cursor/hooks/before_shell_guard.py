#!/usr/bin/env python3
"""beforeShellExecution: block secrets commits, questions.json commits, destructive git."""

from __future__ import annotations

import re
import subprocess
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from _lib import (  # noqa: E402
    DESTRUCTIVE_GIT_RE,
    allow,
    deny,
    is_questions_json,
    is_secret_path,
    log_denial,
    read_input,
    repo_root,
    shell_command,
)


def staged_paths() -> list[str]:
    try:
        r = subprocess.run(
            ["git", "diff", "--cached", "--name-only", "--diff-filter=ACMR"],
            cwd=str(repo_root()),
            check=False,
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
            timeout=5,
        )
        if r.returncode == 0 and r.stdout:
            return [ln.strip() for ln in r.stdout.splitlines() if ln.strip()]
    except (OSError, subprocess.TimeoutExpired):
        pass
    return []


def paths_in_git_command(cmd: str) -> list[str]:
    parts = re.split(r"\s+", cmd)
    out: list[str] = []
    for p in parts[1:]:
        if p.startswith("-"):
            continue
        if "/" in p or "\\" in p or p.startswith("."):
            out.append(p.strip("\"'"))
    return out


def _decide() -> None:
    data = read_input()
    cmd = shell_command(data)
    if not cmd.strip():
        allow()
        return

    lower = cmd.lower()

    if DESTRUCTIVE_GIT_RE.search(cmd):
        log_denial("destructive_git", cmd)
        deny(
            "Destructief git-commando geblokkeerd "
            "(force-push / reset --hard / clean -f / checkout -- .).",
            "GUARDRAILS Pattern 3: destructieve git alleen op expliciet user-verzoek.",
        )
        return

    if re.search(r"\bgit\s+(add|commit|rm|restore --staged)\b", lower):
        candidates = paths_in_git_command(cmd) + staged_paths()
        secrets = [p for p in candidates if is_secret_path(p)]
        questions = [p for p in candidates if is_questions_json(p)]
        if secrets:
            log_denial("secret_git", ",".join(secrets[:10]))
            deny(
                f"Commit/add van secret-achtige paden geblokkeerd: {', '.join(secrets[:5])}",
                "Commit nooit .env / credentials / keys.",
            )
            return
        if questions:
            log_denial("questions_json_git", ",".join(questions[:5]))
            deny(
                "Commit/add van frontend/src/data/questions.json geblokkeerd (gitignored bank).",
                "Vragenbank is lokaal; commit alleen schema + questions.example.json.",
            )
            return

    allow()


def main() -> int:
    try:
        _decide()
        return 0
    except Exception as exc:  # noqa: BLE001 — fail-open
        try:
            log_denial("shell_guard_hook_error", str(exc)[:300])
        except Exception:  # noqa: BLE001
            pass
        allow()
        return 0


if __name__ == "__main__":
    raise SystemExit(main())
