# GUARDRAILS.md
# Persistent safety constraints
# Protocol: https://guardrails.md/

## Meta
Created: 29-09-2026
Universal patterns: 5
Learned signs: 5
Adapted from: `../rws/GUARDRAILS.md` (persoonlijke examenrepo — geen monorepo-sync)

**Lagen:** dit bestand = constitutie (stopregels / blast radius). Multi-stap how-to → skills
(`.cursor/skills/…`) of rules (`.cursor/rules/*.mdc`). Scriptcatalogus → `scripts/README.md`.
SIGNs hier zijn MUST + pointer; procedure niet verdubbelen.

---

## Universal patterns

### Pattern 1 — Artifact verification (sessie-auditlogs)

**Trigger:** Elke agent-turn (niet alleen bij codewijzigingen).
**Instruction:**
1. Maak bij sessiestart één logfile: `.audit/session-YYYYMMDD-HHMMSS.log` in de **repo-root** — **één file per Cursor-chat** (`sessionStart` bindt via `session_id` / `SPRING_EXAM_SESSION_AUDIT`). Hergebruik die file de hele chat. TURN-datum = vandaag lokaal. Schrijven mag zonder user-OK (geen commit). Prefer Write/append; Shell + `python scripts/append-session-audit.py` als fallback. `.audit/` hoort in `.cursorindexingignore` + `.gitignore`, **niet** in `.cursorignore`.
2. **Volledig spoor** (zie `session-audit.mdc`): per turn `USER` / `THOUGHT` / elke `TOOL` (why/args/result) / `DECISION`. Plain text — geen JSON/YAML.
3. Voor destructieve acties: noteer plan in sessielog, vraag human approval, wacht.
4. Geen secrets in logs. Commit nooit `.audit/*.log` (wel `.audit/.gitkeep`).
**Reason:** Audit moet bruikbaar zijn om agent-redenering te reviewen.
**Provenance:** Adapted from rws, 29-09-2026

### Pattern 2 — Context rotation

**Trigger:** Context >80% vol OF 10+ opeenvolgende fouten.
**Instruction:**
1. Schrijf korte samenvatting + open punten naar de huidige sessielog in `.audit/`.
2. Stop de foutlus; herlees `GUARDRAILS.md` + doelstelling.
3. Vraag de gebruiker om een schone doorstart als de context vervuild raakt.
**Reason:** Voorkomt recursive retry-loops.
**Provenance:** Universal pattern uit https://guardrails.md/

### Pattern 3 — Privilege boundaries

**Allowed (zonder extra goedkeuring):**
- Read: projectbronnen, tests, docs, `.cursor/`, `scripts/`, schemas
- Write: frontend/backend binnen de gevraagde scope; `.audit/session-*.log`; `.temp/`; lokale `frontend/src/data/questions.json` (gitignored); lokale sandbox-classes/tests onder `io.praetore.springexam` (gitignored)

**Forbidden / approval required:**
- Secrets committen (`.env`, tokens, credentials)
- Destructieve git (`push --force`, hard reset) tenzij de gebruiker dat expliciet vraagt
- `questions.json` committen (hoort gitignored)
- `SpringExamApplicationTests` uitbreiden met extra tests (smoke-only in git)
- `node_modules/`, `.git/` handmatig patchen

**Enforcement:** `.cursor/hooks.json` + `.cursor/hooks/` (secrets read/commit, destructive git); fail-open.
**Reason:** Beperkt blast radius.
**Provenance:** Adapted from rws, 29-09-2026

### Pattern 4 — Rate limiting

**Limits:**
- Max 10 tool calls in een enge retry-lus op dezelfde fout
- Na 3 identieke auth-/API-fouten: stop en vraag de gebruiker (SIGN #1)
- Bij limiet: forceer Pattern 2
**Reason:** Voorkomt runaway loops.
**Provenance:** Universal pattern uit https://guardrails.md/

### Pattern 5 — Ad-hoc scripts in `.temp/`

**Trigger:** De agent maakt een helper-/eenmalig script dat **geen** officiële project-tooling is.
**Instruction:**
1. Plaats onder **`.temp/`**.
2. **Niet** in `scripts/` — die map is voor vaste tooling (`append-session-audit.py`, frontend `ensure-questions.mjs`).
3. Commit geen inhoud van `.temp/` behalve `.temp/.gitkeep`. Geen inventaris-README in `.temp/`: throwaways hebben datum in de naam + header — conventie in skill **`temp-scaffold`**.
4. Verplaats naar `scripts/` alleen op verzoek **óf** na SIGN #4.
**Reason:** Voorkomt vervuiling van vaste tooling.
**Provenance:** Adapted from rws, 29-09-2026

---

## Learned signs

## SIGN #1
**Trigger:** Authenticated externe toegang faalt (401/403, lege token, credential-fout).
**Instruction:**
1. **Stop** — geen auth-omwegen; geen tokens hardcoden.
2. Secrets alleen lokaal (bijv. `.env`) — nooit in chat, commits of logs.
3. Nooit tokenwaarden printen. Noteer auth-fout (zonder secrets) in `.audit/`.
**Reason:** Auth-omwegen verspillen tijd en lekken secrets.
**Provenance:** Adapted from rws SIGN #1, 29-09-2026

---

## SIGN #2
**Trigger:** Gebruiker plakt credentials/tokens in chat of bestanden.
**Instruction:**
1. Herhaal de waarden niet in antwoorden, logs of commits.
2. Waarschuw kort dat rotatie nodig is; secrets lokaal houden.
**Reason:** Tokens in chat zijn gelekt.
**Provenance:** Adapted from rws SIGN #2, 29-09-2026

---

## SIGN #3
**Trigger:** MCP-tool in deze sessie niet aanroepbaar.
**Instruction:**
1. **Breek af** — geen fake broninhoud alsof MCP werkte.
2. Zeg welke tool ontbreekt; wacht op user-alternatief.
**Reason:** Fake bronclaims houden claims integer.
**Provenance:** Adapted from rws SIGN #4, 29-09-2026

---

## SIGN #4
**Trigger:** ≥3 nieuwe `.temp/`-helpers in één sessie, of herhaalde varianten voor hetzelfde doel.
**Instruction:**
1. **Stop** met nóg een throwaway.
2. Stel uitbreiding van `scripts/` / skill voor; noteer gap in `.audit/`.
**Reason:** Herhaalde temp-helpers verspillen tijd.
**Provenance:** Adapted from rws SIGN #6, 29-09-2026

---

## SIGN #5
**Trigger:** App-code wijzigen en daarna “klaar” melden; of herformatteren buiten scope.
**Instruction:**
1. **MUST:** project-linters respecteren; geen unrelated churn; geen verzonnen “lokaal OK”.
2. Procedure → always-rule **`local-verify.mdc`**.
**Reason:** Portable gedrag; FE/BE hebben eigen checks.
**Provenance:** Adapted from rws SIGN #9, 29-09-2026
