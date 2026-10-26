# AGENTS.md — Spring Exam ELO

## Agent config lagen

| Laag | Bestand | Rol |
|------|---------|-----|
| Wet | [`GUARDRAILS.md`](GUARDRAILS.md) | Stopregels / blast radius (MUST + SIGN pointers) |
| Onboarding | deze `AGENTS.md` | Projectdoel, layout, workflows (**Nederlands**) |
| Patterns | [`AGENTIC-PATTERNS.md`](AGENTIC-PATTERNS.md) | Wat we al doen vs potentieel (inspiratie: rws) |
| Cursor | `.cursor/rules/*.mdc`, `.cursor/skills/`, hooks | Auto-apply + how-to |

**Taal:** AI-toolingdocs = **Nederlands**. Git-commitmessages = **Engels** (`commit-messages.mdc`).  
Datums in docs: **dd-mm-jjjj**; TURN-headers in audit = ISO `YYYY-MM-DD`.

Harde MUST’s staan in `GUARDRAILS.md`. How-to → skills / rules / `scripts/README.md` — niet alles in GUARDRAILS verdubbelen.

---

## Doel van dit project

Examenvoorbereiding in twee lagen:

| Laag | Pad | Rol |
|------|-----|-----|
| **ELO-quiz** | `frontend/` | Alle examenvragen, scoring, ELO (lokaal in de browser) |
| **Spring-sandbox** | `backend/` | Code om Spring-concepten te oefenen (geen quiz-API) |
| **Infra** | `infra/` | Optioneel Postgres wanneer je data/TX in de sandbox oefent |

Volledig overzicht examenonderdelen (+ Java/Servlet-basiskennis): [`EXAMEN.md`](EXAMEN.md) — **blijft in repo-root** (syllabus).

## Workflow voor agents

1. **ELO genereren:** op verzoek → skill **`elo-questions`** / schema’s onder `frontend/public/schema/`. Schrijf naar `frontend/src/data/questions.json` (**gitignored** — niet committen).
2. **Spring oefenen:** op verzoek → skill **`sandbox-demo`**: demo-code onder `io.praetore.springexam`, `backend/http/` met leercomments, én standaard een `.temp/…-study.md` (architectuur-sjabloon in de skill). Domein: **serieus/praktisch** (zoals ticketdesk-checkout), geen schoolse lesjes-API. Bij een **nieuwe** slice: eerst vragen of de oude sandbox weg moet (tenzij al expliciet gezegd). **Niet** `SpringExamApplicationTests` uitbreiden — nieuwe tests in aparte (gitignored) testclasses.
3. **Niet** de quiz via de backend serveren — die blijft frontend-only.
4. **Sessie-audit:** zie GUARDRAILS Pattern 1 + `session-audit.mdc` (`.audit/`).
5. **Ad-hoc (Pattern 5):** scripts, gap-cheatsheets na oefenexamens, parse-helpers → **`.temp/YYYY-MM-DD-slug`** (zie `.temp/README.md`). **Niet** in repo-root als vaste docs. Vaste tooling → `scripts/` of `frontend/scripts/`.

## Frontend (ELO)

- Vite + React + TypeScript + Ajv
- Vragen (lokaal): `frontend/src/data/questions.json` (gitignore)
- Seed: `frontend/src/data/questions.example.json` → `npm run predev` / Vite-plugin kopieert indien ontbreekt
- Schema: `frontend/public/schema/exam-question.schema.json` (+ bank-schema)
- ELO-state: `localStorage` (`spring-exam-elo`)
- Start: `cd frontend && npm run dev` → http://localhost:5173

## Backend (sandbox)

- Java 21 / Spring Boot 3.5 / Maven
- **In git:** `SpringExamApplication.java`, `SpringExamApplicationTests.java` (**alleen** `contextLoads` — niet verder vullen), `resources/.gitkeep`, minimale `application.properties` (naam + poort)
- **Lokaal (gitignored):** overige classes, **overige tests**, `backend/http/`, `application-local.properties`, `schema.sql`/`data.sql`, …
- **Conventie:** sandbox-config (JDBC/JPA/Actuator) → `application-local.properties` + Spring profile **`local`** (run config **Sandbox** zet `-Dspring-boot.run.profiles=local`)
- HTTP Client: `backend/http/` (lokaal; environment **local**)
- Actieve sandbox-slice: **Core / ticketdesk** (`orders/` — DI, profiles, lazy; zie `.temp/…-core-checkout-study.md`)
- Start: IntelliJ **Sandbox** of `cd backend && mvn spring-boot:run "-Dspring-boot.run.profiles=local"`
- Let op Boot 3 / Spring 6 vs exammateriaal (5.3 / Boot 2.x)

## IntelliJ run configs (`.run/`)

- **Oefenexamen** — ELO-quiz (`frontend/`)
- **Sandbox** — Maven `spring-boot:run` (`backend/`)
- **Docker Compose** — Postgres indien nodig voor sandbox-oefeningen
- **Full stack** — alle drie

## Layout (AI-tooling)

- `GUARDRAILS.md`, `AGENTIC-PATTERNS.md`, `AGENTS.md` — constitutie / inventaris / onboarding
- `.cursor/rules/`, `.cursor/skills/`, `.cursor/hooks/` — Cursor enforcement + how-to
- `scripts/append-session-audit.py` — sessielog ensure/append
- `.audit/` — sessielogs (niet committen behalve `.gitkeep`)
- `.temp/` — ad-hoc scripts (niet committen behalve `.gitkeep` / conventie-README); naam `YYYY-MM-DD-slug` + header

## Werkafspraken

- Wijzigingen dienen examenleren: ELO-vraag of sandbox-demo.
- Geen productieconcerns tenzij het examonderwerp is.
- Geen JTA/XA in Docker alleen om TX te oefenen — `@Transactional` + eventueel Postgres volstaat.
- Commit/push alleen op verzoek van de gebruiker.
