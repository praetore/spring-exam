# Spring Professional Develop — examenonderdelen

Bron: [VMware Spring Professional Develop Exam Guide](https://docs.broadcom.com/doc/vmw-spring-professional-develop-exam-guide) (2V0-72.22 → Spring Certified Professional 2024).

## Exam-meta

| | |
|---|---|
| Code | 2V0-72.22 |
| Vragen | 60 |
| Tijd | 130 minuten |
| Slaagscore | 300 (scaled) |
| Baseline docs | Spring Framework **5.3**, Spring Boot **2.5**, Spring Data 2021.0 |

Dit repo draait Boot **3.5** / Spring **6** — handig om te oefenen, maar noteer API-verschillen t.o.v. exammateriaal (Jakarta, Security DSL, enz.).

**Niet op de syllabus:** volledige JTA/XA-setup. Transacties = Spring `@Transactional` (propagatie, rollback, tests).

---

## Vereiste Java-kennis (niet-Spring) — opfrissen

Het examen test Spring, maar verwacht dat onderstaande **standaard Java / Jakarta EE-bouwstenen** bekend zijn. Zonder die basis voelen scopes, MVC en Security abstract.

### Servlet API & HTTP (vaak het zwakste punt na jaren “alleen Boot”)

Spring MVC/Security bouwen hier bovenop. Even terug in je hoofd:

| Concept | Wat je moet kunnen uitleggen | Waar Spring het gebruikt |
|---------|------------------------------|---------------------------|
| **HTTP request/response** | method, path, headers, status, body | `@RequestMapping`, `RestTemplate` |
| **`HttpServletRequest` / `Response`** | parameters, attributes, input/output | filters, argument resolvers |
| **Cookie** | client bewaart naam/waarde; meegestuurd per request | o.a. `JSESSIONID`, Security |
| **HTTP Session** | server-side map per bezoeker, id in cookie | `session`-scope, login-state |
| **`ServletContext`** | één per webapp; app-brede attributes | `application`-scope |
| **Filter** | runt vóór/na servlet; ketting (`FilterChain`) | Spring Security filter chain |
| **DispatcherServlet** | front controller: één servlet, delegeert naar controllers | hart van Spring MVC |
| **Servlet lifecycle** | init → service → destroy; context init/destroy | Boot start/embedded Tomcat |

**Opfris-oefening in dit repo:** `backend/http/scope-diff.http` — singleton vs application via `ServletContext`; shop-demo voor session/request.

Korte mentale modellen:

```
Browser  --HTTP-->  Filter(s)  -->  DispatcherServlet  -->  @Controller
                \-> kan Session / ServletContext lezen/schrijven
```

- **request** = één HTTP-call  
- **session** = reeks calls met dezelfde `JSESSIONID`  
- **application / ServletContext** = hele webapp (war/context)  
- **singleton (Spring)** = hele Spring `ApplicationContext` (niet hetzelfde object-archief als ServletContext)

### Java-taal (voor Core, AOP, TX)

| Onderwerp | Waarom op het examen |
|-----------|----------------------|
| **Annotations** | meta-data op class/method/field; retention; hoe Spring ze scant |
| **Interfaces vs classes** | JDK-proxy (interface) vs CGLIB (concrete class) |
| **Checked vs unchecked exceptions** | default `@Transactional` rollback alleen bij runtime |
| **`try-with-resources` / AutoCloseable** | JDBC-resources; waarom templates helpen |
| **Generics** | repositories, `RestTemplate`/`JdbcTemplate` callbacks |
| **Concurrency** | singleton = shared; mutable state moet thread-safe |
| **Classpath / packages** | component-scan startpunt; “bean niet gevonden” |
| **Equals/hashCode** (basis) | soms bij entities / collections; niet overdrijven |

### JDBC (voor section Data — Spring JDBC)

Niet diep DBA, wel het model:

- `DataSource` → `Connection` → `Statement`/`PreparedStatement` → `ResultSet`
- SQLExceptions zijn checked → Spring vertaalt naar unchecked data-access hiërarchie
- Waarom `JdbcTemplate`: minder boilerplate, consistente exception-vertaling

### JPA / persistence (voor Spring Data JPA)

- **Entity** = class ↔ tabel (`@Entity`, id)
- **`EntityManager` / persistence context** = managed instances in een unit of work
- **Repository** = abstractie boven queries (Spring Data vult implementatie in)
- Relatie met **transacties**: flush/commit aan TX-grens (vandaar `@Transactional` op services)

### Testing (JUnit 5)

- `@Test`, assertions, lifecycle (`@BeforeEach`)
- Verschil unit test (geen Spring) vs test mét Spring-context
- Mocking-idee: vervang dependency; ken het verschil later met `@Mock` vs `@MockBean`

### Security-begrippen (vóór Spring Security) — wél examenrelevant

Dit stond al in dit lijstje; zonder deze termen voelt Section 5 abstract:

| Term | Betekenis |
|------|-----------|
| **Authentication** | wie ben je? (login / credentials) |
| **Authorization** | mag je dit? (rollen / rechten) |
| **Principal** | de huidige gebruiker |
| **Role / authority** | label voor authorization (bv. `ROLE_ADMIN`) |
| **Session + cookie** | server onthoudt login; browser stuurt session-id mee |
| **CSRF** | kwaadwillende site triggert jouw browser + cookie → state-changing request; verdediging = CSRF-token |
| **HttpOnly / Secure cookies** | JS kan cookie niet lezen / alleen over HTTPS |

Ook uit het **Servlet**-blok hierboven (zelfde lijst): **Filter**, **HTTP session**, **cookie** — Spring Security is vooral een **filter chain** vóór je controllers.

**Niet** voor het examen (ondanks ons side-track): JWT-HMAC tot in detail, Vault, secret-rotatie, Next.js session libraries.

### Mini-checklist Security (niet-Spring)

- [ ] Authn vs authz in één zin
- [ ] Waarom CSRF relevant is bij cookie-sessies + form/POST
- [ ] Wat een Filter doet in de request-pipeline
- [ ] Session-cookie = sleutel, data op de server

---

- Volledige Servlet-spec uit je hoofd
- Ruwe JSP/JSF
- JTA/XA two-phase commit
- Advanced JVM tuning / classloader-trucs
- Hibernate internals tot in de queryplaner

### Mini-checklist “ken ik Servlet nog?”

- [ ] Wat is het verschil request attribute vs session attribute vs ServletContext attribute?
- [ ] Waar komt `JSESSIONID` vandaan en wanneer is er een nieuwe session?
- [ ] Wat doet een `Filter` t.o.v. een `Servlet`?
- [ ] Waarom heeft Spring MVC één `DispatcherServlet`?
- [ ] Waarom is `application`-scope ≠ Spring-`singleton` (hint: `ServletContext`)?

---

## Section 1 — Spring Core

### 1.1 Introduction to Spring Framework
- Wat Spring oplost (IoC, DI, cross-cutting)

### 1.2 Java Configuration
- Beans via Java-config (`@Configuration`, `@Bean`)
- Beans uit de `ApplicationContext` halen
- Meerdere config-classes
- Dependencies tussen beans
- Bean scopes (`singleton`, `prototype`, …)

### 1.3 Properties and Profiles
- Externalized properties
- Profiles
- SpEL

### 1.4 Annotation-Based Configuration & Component Scanning
- Annotation-config
- Best practices (constructor injectie, enz.)
- `@PostConstruct` / `@PreDestroy`
- Stereotypes (`@Component`, `@Service`, `@Repository`, `@Controller`, …)

### 1.5 Spring Bean Lifecycle
- Lifecycle-stappen
- `BeanFactoryPostProcessor` / `BeanPostProcessor`
- Proxies (runtime gedrag: TX, AOP, …)
- Bean creation order
- Injectie op type (`@Primary`, `@Qualifier`, ambiguïteit)

### 1.6 Aspect Oriented Programming
- Waarom AOP
- Advices implementeren (Spring AOP)
- Pointcut expressions
- Advice-types (`Before`, `After`, `AfterReturning`, `AfterThrowing`, `Around`)

**ELO-topics:** `ioc`, `aop`

---

## Section 2 — Data Management

### 2.1 Spring JDBC
- `JdbcTemplate` configureren/gebruiken
- Queries + callbacks (result sets)
- Data-access exceptions (Spring hiërarchie)

### 2.2 Transaction Management
- Spring TX-management (`@Transactional`)
- Propagation
- Rollback rules (default: unchecked; `rollbackFor`, …)
- Transacties in tests

### 2.3 Spring Boot + Spring Data
- JPA-app met Spring Boot
- Spring Data JPA repositories

**ELO-topics:** `transactions`, `data` (JDBC + Spring Data JPA)

---

## Section 3 — Spring MVC

### 3.1 Web Applications with Spring Boot
- MVC-app met Boot
- Request processing lifecycle (REST)
- Eenvoudige REST-controller (GET)
- Deployment-config

### 3.2 REST Applications
- Controllers voor verschillende HTTP-verbs
- `RestTemplate` om REST-services aan te roepen

**ELO-topics:** `mvc`

---

## Section 4 — Testing

### 4.1 Testing Spring Applications
- JUnit 5
- Spring integration tests
- Profiles in tests
- Database in tests

### 4.2 Advanced Testing (Boot + MockMvc)
- Spring Boot test support
- Integration testing
- MockMvc
- Slice testing (`@WebMvcTest`, `@DataJpaTest`, …)

**ELO-topics:** `testing`

---

## Section 5 — Security

### 5.1 Basic security concepts
- Zie ook hierboven: authn/authz, principal, CSRF, session/cookie (niet-Spring basis)

### 5.2 Authentication & Authorization (Spring Security config)
- Security filter chain (verzoeken passeren filters vóór MVC)
- Configureren wat publiek is vs authenticated (`permitAll`, `authenticated`, role-checks)
- User details / in-memory of custom user service (hoogover: waar komen users vandaan)
- Password encoding-idee (plaintext passwords opslaan = fout; encoder bestaat)
- Form login / HTTP Basic (klassiek exammateriaal; Boot 2-stijl vs nieuwere DSL — ken het concept)
- CSRF: default aan bij browser-flows; bij pure stateless API vaak uit — weet **wanneer**
- Exception handling hoogover: 401 vs 403

### 5.3 Method-level security
- Annotaties zoals `@PreAuthorize` / `@Secured` (afhankelijk van lesmateriaal)
- Moet **aangezet** worden (`@EnableMethodSecurity` / oudere `@EnableGlobalMethodSecurity`)
- Werkt via **proxy** → self-invocation mist security net als bij `@Transactional`

**ELO-topics:** `security`

**Exam-instinkers Security**
- CSRF niet blind “altijd uit”
- Method security vergeten te enablen
- 401 (niet ingelogd) vs 403 (wel ingelogd, geen recht)
- Actuator endpoints apart beveiligen (raakt ook section 6)

---

## Section 6 — Spring Boot

### 6.1 Feature Introduction
- Boot-features
- Dependency management (BOM / starters)

### 6.2 Properties & Autoconfiguration
- Properties laden/definiëren
- Auto-configuration
- Defaults overriden

### 6.3 Actuator
- Endpoints configureren
- Actuator HTTP endpoints beveiligen
- Custom metrics
- Custom health indicators

**ELO-topics:** `boot` (inclusief Actuator)

---

## Snelle checklist (instinkers)

| Onderdeel | Let op |
|-----------|--------|
| Core / proxies | Self-invocation omzeilt `@Transactional` / `@Async` / AOP |
| TX | Checked exception → default **geen** rollback |
| Boot properties | CLI / env winnen van `application.properties` |
| Testing | `@MockBean` ≠ `@Mock`+`@InjectMocks`; slices ≠ volle context |
| Security | CSRF-context; method security enablen; 401 vs 403; proxy/self-invocation |
| Boot | `@SpringBootApplication` scan-startpunt; `@ConditionalOnMissingBean` |

---

## Mapping naar dit project

| Repo | Syllabus |
|------|----------|
| `frontend/` ELO-quiz | Alle topics via `questions.json` |
| `backend/` sandbox | Demo-code per onderwerp (Core, TX, MVC, …) |
| `infra/postgres` | Optioneel bij data/TX-oefeningen in de sandbox |
| Schema | `frontend/public/schema/exam-question.schema.json` |
