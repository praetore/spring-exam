---
name: elo-questions
description: >-
  Genereer of merge Spring Exam ELO-meerkeuzevragen in questions.json.
  Gebruik bij verzoek om vragen, ELO-bank, examvalkuilen, of schema-validatie
  van de vragenbank. Topics: ioc, aop, transactions, mvc, testing, security, boot, data.
---

# ELO-vragen genereren

## Bron van waarheid

1. Lees **eerst** `frontend/public/schema/exam-question.schema.json` (item) en eventueel `exam-question-bank.schema.json` (array).
2. Lees `EXAMEN.md` voor syllabus-context van het gevraagde topic.
3. Doelbestand: `frontend/src/data/questions.json` (gitignored).
4. Seed (wél in git): `frontend/src/data/questions.example.json`.

Als `questions.json` ontbreekt: `node frontend/scripts/ensure-questions.mjs` of start Vite/`npm run predev`.

## Ad-hoc helpers (Pattern 5)

Eenmalige merge-/generate-scripts → **`.temp/`** met naam `YYYY-MM-DD-slug.ext` en een korte header (wat + hoe draaien). Geen aparte inventaris.

## Regels

1. Elk object valid t.o.v. item-schema (`additionalProperties: false`, required, topic-enum).
2. Focus op **examvalkuilen** (veelgemaakte fouten); Nederlands.
3. Unieke kebab-case `id`. Merge op id — bestaande ids niet stilzwijgend overschrijven tenzij gevraagd.
4. `answer` ∈ `choices[].id` (of array bij `multi`).
5. `type: "code"` → vul `code`. Altijd zinvolle `trap`.
6. Topic `data` = JDBC + Spring Data JPA; Actuator onder `boot`.
7. Start-elo vaak ≈ `700 + difficulty * 100`.

## Na genereren

- Schrijf naar `questions.json`, niet naar `.example.json` (tenzij seed-update gevraagd).
- Claim geen “schema OK” zonder check; optioneel: Ajv-validate via korte node one-liner of handmatige checklist.
- Commit **nooit** `questions.json`.
