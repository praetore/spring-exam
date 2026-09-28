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

**ELO-topics:** `transactions` (+ later JDBC/JPA-vragen onder data)

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
### 5.2 Authentication & Authorization (Spring Security config)
### 5.3 Method-level security

**ELO-topics:** `security`

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

**ELO-topics:** `boot`

---

## Snelle checklist (instinkers)

| Onderdeel | Let op |
|-----------|--------|
| Core / proxies | Self-invocation omzeilt `@Transactional` / `@Async` / AOP |
| TX | Checked exception → default **geen** rollback |
| Boot properties | CLI / env winnen van `application.properties` |
| Testing | `@MockBean` ≠ `@Mock`+`@InjectMocks`; slices ≠ volle context |
| Security | CSRF-context; method security moet ge-enabled |
| Boot | `@SpringBootApplication` scan-startpunt; `@ConditionalOnMissingBean` |

---

## Mapping naar dit project

| Repo | Syllabus |
|------|----------|
| `backend/` API + JPA ELO | 2.2, 2.3, 3.x, 6.x |
| `frontend/` quiz | oefenen alle topics via vragen |
| `infra/postgres` | 2.x data access (geen JTA) |
| `questions.json` | per topic ELO-vragen uitbreiden |
