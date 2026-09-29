#!/usr/bin/env python3
"""Ensure / append plain-text session audit logs under .audit/.

Examples:
  python scripts/append-session-audit.py ensure
  python scripts/append-session-audit.py ensure --session-id <id>
  python scripts/append-session-audit.py ensure --new
  python scripts/append-session-audit.py path
  python scripts/append-session-audit.py append --user "..." --thought "..." --decision "..."
"""

from __future__ import annotations

import argparse
import os
import re
import sys
from datetime import date, datetime
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
AUDIT_DIR = REPO_ROOT / ".audit"
CURRENT_POINTER = AUDIT_DIR / "current"
BINDINGS_DIR = AUDIT_DIR / "bindings"
ENV_SESSION = "SPRING_EXAM_SESSION_AUDIT"
_SESSION_DATE_RE = re.compile(r"^session-(\d{8})-")


def local_now() -> datetime:
    return datetime.now().astimezone()


def format_turn_stamp(when: datetime | None = None) -> str:
    return (when or local_now()).strftime("%Y-%m-%d %H:%M")


def format_session_filename(when: datetime | None = None) -> str:
    return (when or local_now()).strftime("session-%Y%m%d-%H%M%S.log")


def sanitize_session_id(session_id: str) -> str:
    cleaned = re.sub(r"[^A-Za-z0-9._-]+", "_", session_id.strip())[:128]
    return cleaned or "unknown"


def session_file_date(path: Path) -> date | None:
    match = _SESSION_DATE_RE.match(path.name)
    if not match:
        return None
    return datetime.strptime(match.group(1), "%Y%m%d").date()


def read_pointer() -> Path | None:
    if not CURRENT_POINTER.is_file():
        return None
    raw = CURRENT_POINTER.read_text(encoding="utf-8").strip()
    if not raw:
        return None
    target = Path(raw)
    if not target.is_absolute():
        target = (AUDIT_DIR / target).resolve()
    return target if target.is_file() else None


def write_pointer(session_file: Path) -> None:
    AUDIT_DIR.mkdir(parents=True, exist_ok=True)
    CURRENT_POINTER.write_text(str(session_file.resolve()) + "\n", encoding="utf-8")


def read_binding(session_id: str) -> Path | None:
    path = BINDINGS_DIR / sanitize_session_id(session_id)
    if not path.is_file():
        return None
    raw = path.read_text(encoding="utf-8").strip()
    if not raw:
        return None
    target = Path(raw)
    if not target.is_absolute():
        target = (AUDIT_DIR / target).resolve()
    return target if target.is_file() else None


def write_binding(session_id: str, session_file: Path) -> None:
    BINDINGS_DIR.mkdir(parents=True, exist_ok=True)
    (BINDINGS_DIR / sanitize_session_id(session_id)).write_text(
        str(session_file.resolve()) + "\n", encoding="utf-8"
    )


def create_session_file() -> Path:
    AUDIT_DIR.mkdir(parents=True, exist_ok=True)
    path = AUDIT_DIR / format_session_filename()
    if path.exists():
        stem = path.stem
        n = 2
        while True:
            candidate = AUDIT_DIR / f"{stem}-{n}.log"
            if not candidate.exists():
                path = candidate
                break
            n += 1
    path.write_text(
        f"# session audit\n# started {format_turn_stamp()}\n\n",
        encoding="utf-8",
    )
    return path


def resolve_session(*, session_id: str | None, force_new: bool) -> Path:
    env_path = os.environ.get(ENV_SESSION, "").strip()
    if env_path and not force_new:
        p = Path(env_path)
        if p.is_file():
            write_pointer(p)
            if session_id:
                write_binding(session_id, p)
            return p

    if session_id and not force_new:
        bound = read_binding(session_id)
        if bound is not None:
            write_pointer(bound)
            return bound

    if not force_new:
        current = read_pointer()
        if current is not None:
            file_date = session_file_date(current)
            if file_date is None or file_date == local_now().date() or session_id:
                if session_id:
                    write_binding(session_id, current)
                return current

    path = create_session_file()
    write_pointer(path)
    if session_id:
        write_binding(session_id, path)
    return path


def cmd_ensure(args: argparse.Namespace) -> int:
    path = resolve_session(session_id=args.session_id, force_new=args.new)
    print(path)
    return 0


def cmd_path(_: argparse.Namespace) -> int:
    current = read_pointer()
    if current is None:
        print("no current session; run ensure first", file=sys.stderr)
        return 1
    print(current)
    return 0


def format_append_block(
    *,
    user: str | None,
    thoughts: list[str],
    decision: str | None,
    tools: list[str],
) -> str:
    lines = [f"=== TURN {format_turn_stamp()} ==="]
    if user:
        lines.append(f"USER: {user}")
    if thoughts:
        lines.append("THOUGHT:")
        for t in thoughts:
            lines.append(f"- {t}")
    if tools:
        lines.append("TOOL:")
        for raw in tools:
            # Expected: name=... || why=... || args=... || result=...
            parts = [p.strip() for p in raw.split("||")]
            fields: dict[str, str] = {}
            for part in parts:
                if "=" in part:
                    k, v = part.split("=", 1)
                    fields[k.strip().lower()] = v.strip()
            name = fields.get("name", "?")
            lines.append(f"- name: {name}")
            for key in ("why", "args", "result"):
                if key in fields:
                    lines.append(f"  {key}: {fields[key]}")
    if decision:
        lines.append("DECISION:")
        lines.append(f"- {decision}")
    lines.append("")
    return "\n".join(lines)


def cmd_append(args: argparse.Namespace) -> int:
    path = resolve_session(session_id=args.session_id, force_new=False)
    if args.stdin:
        body = sys.stdin.read()
        if not body.endswith("\n"):
            body += "\n"
        with path.open("a", encoding="utf-8") as fh:
            fh.write(body)
        print(path)
        return 0

    block = format_append_block(
        user=args.user,
        thoughts=args.thought or [],
        decision=args.decision,
        tools=args.tool or [],
    )
    with path.open("a", encoding="utf-8") as fh:
        fh.write(block)
    print(path)
    return 0


def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(description="Session audit ensure/append")
    sub = p.add_subparsers(dest="command", required=True)

    e = sub.add_parser("ensure", help="Ensure a session log file exists")
    e.add_argument("--session-id", default=None)
    e.add_argument("--new", action="store_true", help="Force a new session file")
    e.set_defaults(func=cmd_ensure)

    path_p = sub.add_parser("path", help="Print current session log path")
    path_p.set_defaults(func=cmd_path)

    a = sub.add_parser("append", help="Append a TURN block")
    a.add_argument("--session-id", default=None)
    a.add_argument("--user", default=None)
    a.add_argument("--thought", action="append", default=[])
    a.add_argument("--decision", default=None)
    a.add_argument(
        "--tool",
        action="append",
        default=[],
        help='name=... || why=... || args=... || result=...',
    )
    a.add_argument("--stdin", action="store_true", help="Append raw stdin")
    a.set_defaults(func=cmd_append)

    return p


def main(argv: list[str] | None = None) -> int:
    parser = build_parser()
    args = parser.parse_args(argv)
    return int(args.func(args))


if __name__ == "__main__":
    raise SystemExit(main())
