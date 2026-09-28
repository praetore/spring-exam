# AGENTS.md — Spring Exam ELO

## Doel van dit project

Dit project is een **fullstack ELO** voor het Spring-examen: oefenomgeving om examengerichte kennis op te bouwen (geen productie-app).

Volledig overzicht van examenonderdelen: [`EXAMEN.md`](EXAMEN.md).

## Architectuur

| Laag | Pad | Tech | Rol |
|------|-----|------|-----|
| Backend | `backend/` | Spring Boot 3.5 / Java 21 / JPA | API, schema-validatie, scoring, ELO in Postgres |
| Frontend | `frontend/` | Vite + React + TypeScript | Quiz-UI, Ajv view-validatie |
| Infra | `infra/` | Docker Compose | Postgres (lokaal) |
| Data | `backend/src/main/resources/questions/` + `schema/` | JSON + JSON Schema | Vragenbron |

JWT/auth is **nog niet** nodig voor de lokale single-player ELO; daarom geen JWT-service in Compose.

### API (kort)

- `GET /api/elo` — spelerstatus (Postgres)
- `POST /api/elo/reset` — reset rating
- `GET /api/topics` — topics
- `GET /api/questions/next?topic=` — volgende vraag (zonder antwoord)
- `POST /api/answers` — `{ questionId, choiceIds }` → uitslag + ELO-delta
- `GET /api/schema` — exam-question JSON Schema

### Lokaal draaien

IntelliJ run configs (`.run/`): **Docker Compose**, **Backend**, **Frontend**, of **Full stack**.

```bash
# infra (optioneel handmatig; backend start Compose ook via spring-boot-docker-compose)
cd infra && docker compose up -d

# backend
cd backend && mvn spring-boot:run

# frontend
cd frontend && npm install && npm run dev
```

Frontend: http://localhost:5173 (proxy `/api` → `:8080`).

## Hoe agents dit project moeten behandelen

- Behandel code, uitleg en oefeningen als **examvoorbereiding**.
- Focus: IoC, bean lifecycle, AOP, transactions, MVC/REST, testing, Security, Spring Boot.
- Benadruk **instinkers** (proxies/self-invocation, rollback-defaults, property precedence, slices, enz.).
- Nieuwe vragen: voeg toe aan `backend/src/main/resources/questions/questions.json`, volg het schema.
- Houd voorbeelden klein; geen enterprise-boilerplate zonder leerrendement.
- Vermeld Boot 3 / Spring 6 vs ouder exammateriaal (5.3 / Boot 2.x) als antwoorden kunnen verschillen.

## Werkafspraken

- Spring-code hoort in `backend/`; infra in `infra/`; UI in `frontend/`.
- Wijzigingen dienen het leerdoel of de ELO-flow.
- Geen productieconcerns tenzij het examonderwerp is.
