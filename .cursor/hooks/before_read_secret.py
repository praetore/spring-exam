#!/usr/bin/env python3
"""beforeReadFile: block reading real secret env/credential files."""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from _lib import allow, deny, file_path, is_secret_path, log_denial, read_input  # noqa: E402


def main() -> int:
    try:
        data = read_input()
        path = file_path(data)
        if path and is_secret_path(path):
            log_denial("read_secret", path)
            deny(
                f"Lezen van secret-bestand geblokkeerd: {path}",
                "Secrets horen lokaal en niet in agent-context.",
            )
            return 0
        allow()
        return 0
    except Exception as exc:  # noqa: BLE001 — fail-open
        try:
            log_denial("read_secret_hook_error", str(exc)[:300])
        except Exception:  # noqa: BLE001
            pass
        allow()
        return 0


if __name__ == "__main__":
    raise SystemExit(main())
