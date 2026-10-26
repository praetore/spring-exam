---
name: sandbox-demo
description: >-
  Spring-exam backend sandbox: demo-code onder io.praetore.springexam,
  IntelliJ HTTP Client-bestanden in backend/.temp/http/, filesystem-XML in
  backend/.temp/xml/, en root-.temp study-gidsen.
  Gebruik bij nieuwe sandbox-slice, TX/JDBC/JPA/MVC/scopes/actuator-oefenen,
  endpoints, .http request-files, of sandbox study-docs.
---

# Sandbox-demo’s

## Scope

- Code: `backend/src/main/java/io/praetore/springexam/…` (gitignored behalve `SpringExamApplication`)
- HTTP: `backend/.temp/http/*.http` (gitignored; `backend/.temp/.gitkeep` wél in git)
- Filesystem-XML e.d.: `backend/.temp/xml/` (gitignored)
- Config: profile `local` / `application-local.properties` (resources, gitignored behalve minimale props)
- Study-gids: repo-root `.temp/YYYY-MM-DD-<slice>-study.md` (gitignored behalve root-`.temp` README)
- Tests: **niet** in `SpringExamApplicationTests` — aparte (gitignored) testclasses
- Zie ook rule `sandbox-backend.mdc`

## Nieuwe sandbox aanmaken — workflow (HARD)

Als de gebruiker vraagt om een **nieuwe** sandbox / demo-slice / “vervang de oude”:

1. **Inventariseer** kort wat er nu staat (packages onder `io.praetore.springexam`, `backend/.temp/http/*.http`, relevante root-`.temp/*-study.md`).
2. **Vraag eerst** of de bestaande sandbox **weg moet** (code + http + eventueel oude study-gids), of **ernaast** mag blijven.  
   - **Niet** verwijderen tot de gebruiker dat bevestigt (tenzij ze in dezelfde zin al expliciet “verwijder de oude” zeiden).
3. Bouw daarna de nieuwe slice: praktisch domein, HTTP met leercomments, `application-local` indien nodig.
4. **Altijd** een study-gids meeleveren (sjabloon hieronder) — standaard, niet alleen op verzoek.
5. Local verify: kleinste relevante check (`mvn -q -DskipTests compile` en/of smoke-test).

Uitbreiden van de *huidige* slice (extra endpoint, fix) → geen “mag de oude weg?”-vraag; wel study-gids bijwerken als de architectuur/flow materieel verandert.

## HTTP-bestanden — leercommentaar (HARD)

Bij **aanmaken of uitbreiden** van `backend/.temp/http/*.http`:

1. Elke request (of logische stap) heeft `#`-comments die uitleggen wat je ziet en waarom (vaak 2–5 regels).
2. **Natuurlijke taal**, alsof je het aan jezelf uitlegt. Geen labels of steekwoorden als `Opsteken:`, `Leer dit:`, `Exam:`, `Let op:`, `Tip:`.
3. Wel concreet: verwachte velden/waarden, valkuil, link naar annotatie of class als dat helpt.
4. **Brede regels** (~100–120 tekens): niet vroeg afbreken; schermruimte mag.

```http
### Korte titel van de request
# Uitgebreidere uitleg in lopende zinnen op brede regels: wat de response toont, welke Spring-keuze daaronder zit, en waar je op moet letten.
METHOD http://localhost:8080/...
```

5. File-header mag startinstructie houden (`Sandbox`, profile `local`); dat vervangt **geen** leercomment per request.
6. Optioneel `@name` voor chaining.

**Niet:** kale URL-omschrijving; telegramstijl met alleen annotatie-namen; meta-labels vóór de zin.  
**Wel:** leesbare mini-uitleg naast de request.

## Demo-stijl (code)

- Klein en examgericht; één les per endpoint waar het kan.
- HTTP-comments + study-gids vullen elkaar aan.

### Domein: serieus / praktisch (HARD)

Sandbox-slices zijn **mini-apps** waarin examstof uit de flow komt — referentie: ticketdesk-checkout (`orders/`).

**Wel:** herkenbaar scenario (checkout, betalen, mail, audit, rapportage, bookkeeping, …) met echte-achtige namen en responses die je zou verwachten van een kleine API.

**Niet (te schools):**
- lesjes-API’s (`/api/core/di`, “PetOwner”, cat/dog/hamster, `lesson`-JSON)
- aparte endpoints per annotatie alleen om te demonstreren
- speelgoeddomeinen die alleen bestaan om `@Primary` te labelen

Examhoek mag in Javadoc/HTTP-comments/study-doc; de **API zelf** voelt als oefensoftware, niet als PowerPoint.

### Package-mapjes ↔ architectuur (HARD)

Geen platte dump van alle classes in één package. **Subpackages volgen de lagen** uit het study-doc-diagram (§2), bijvoorbeeld:

| Laag in diagram | Typische map |
|-----------------|--------------|
| Controllers / HTTP | `…/<slice>/web` |
| Use-case / service | `…/<slice>/checkout` (of `service`) |
| Gateways / adapters | `…/<slice>/payment`, `mail`, `audit`, … |
| Persistatie | `…/<slice>/repository` |
| Lazy / reporting | `…/<slice>/report` |
| `@Configuration` extras | `…/<slice>/config` |

Study-doc §2 toont dezelfde package-namen in het ASCII-diagram (of een korte map↔laag-tabel). Bij herstructureren: diagram + bronpaden meenemen.

### Javadoc (HARD — ook bij volgende sandbox)

Op **controllers, services, config-classes, belangrijke beans/interfaces** (natuurlijke taal, Nederlands of kort gemengd):

1. **Class-Javadoc** — wat dit type in de mini-app doet / welke examhoek het raakt.
2. **Publieke methodes** (zeker alle `@GetMapping`/`@PostMapping` en use-case-methodes) — wat de call doet en wat je in de response of het gedrag terugziet.
3. Geen meta-labels (`Opsteken:`, `Leer dit:`); wél concreet (bean-namen, profiles, lazy).
4. Triviale getters op records mag je overslaan als de class-Javadoc genoeg is.

## Sandbox study-docs (`.temp/`) — HARD, standaard bij elke (nieuwe) slice

Pad: `.temp/YYYY-MM-DD-<slice>-study.md`. Referentie: `2026-10-26-core-checkout-study.md`.

**Altijd dezelfde structuur:**

1. **Header** — HTML-comment (`.temp` ad-hoc + niet committen) + titel + datum + korte slice-omschrijving  
2. **Bronnentabel** — code, HTTP, `EXAMEN.md`, eventuele cheatsheet + startregel (Sandbox / profile)  
3. **§1 Wat is dit?** — 2–4 bullets: wat de mini-app doet / waar examstof zit  
4. **§2 Architectuur (lagen)** — ASCII-diagram + optionele stereotype-/rol-tabel  
5. **§3 Kernconcepten → waar in de code** — per examonderwerp een tabel (feit ↔ class/endpoint/response)  
6. **§4 Request-flow** — ASCII van de hoofduse-case  
7. **§5 Snelle cheat** — compacte Q→A  

**Niet** in dit sjabloon: lange “hoe te bestuderen”-rondes, “wat dit expres niet is”, of vaste syllabus (dat blijft `EXAMEN.md`).
