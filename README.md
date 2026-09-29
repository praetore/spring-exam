# Spring Exam

Persoonlijke voorbereiding op het **Spring Professional**-certificaat (o.a. 2V0-72.22 / Spring Certified Professional).

Twee lagen:

| Laag | Map | Doel |
|------|-----|------|
| **Oefenexamen (ELO)** | [`frontend/`](frontend/) | Meerkeuzevragen in de browser, lokaale ELO-scoring |
| **Sandbox** | [`backend/`](backend/) | Kleine Spring Boot-demo’s om concepten te oefenen |
| **Infra** | [`infra/`](infra/) | Optioneel Postgres voor data/TX-oefeningen |

Syllabus & Java/Servlet-basiskennis: [`EXAMEN.md`](EXAMEN.md).

## Snel starten

### Oefenexamen (frontend)

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173.

Vragen staan lokaal in `frontend/src/data/questions.json` (gitignored). Bij ontbreken kopieert `npm run predev` / de Vite-plugin vanuit `questions.example.json`.

### Sandbox (backend)

```bash
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

- Java 21, Spring Boot 3.5
- Minimale `application.properties` in git; sandbox-config in **`application-local.properties`** (gitignored, profile `local`)
- Oefencode onder `io.praetore.springexam` is grotendeels gitignored (behalve `SpringExamApplication` + smoke-test)

IntelliJ: run configs **Oefenexamen**, **Sandbox**, **Docker Compose**, **Full stack** (`.run/`).

## Repo-indeling

```
frontend/     Vite + React + TypeScript + Ajv (ELO-quiz)
backend/      Spring Boot sandbox
infra/        Docker Compose (Postgres)
EXAMEN.md     Examengids / checklist
AGENTS.md     Instructies voor AI-agents
LICENSE       MIT
```

## Licentie

[MIT](LICENSE) © 2026 Darryl Amatsetam
