#!/usr/bin/env python3
"""postToolUseFailure: append denial/tool-failure breadcrumb (no agent narration)."""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from _lib import emit, log_denial, read_input  # noqa: E402


def main() -> int:
    try:
        data = read_input()
        tool = data.get("tool_name") or data.get("tool") or "unknown"
        err = data.get("error") or data.get("message") or ""
        log_denial("tool_failure", f"tool={tool} err={str(err)[:300]}")
    except Exception:  # noqa: BLE001
        pass
    emit({"ok": True})
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
