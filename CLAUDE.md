# CLAUDE.md

Contexto completo del proyecto para Claude Code. Lee este archivo antes de cualquier tarea.

---

## Proyecto

**Babylon Custom Insurance** — Plataforma B2B2C de seguros de vida modulares.
El usuario arma su cobertura seleccionando módulos independientes (death, disability,
accidents) y asistencias gratuitas proporcionales al número de coberturas activas.
Beneficiarios ilimitados por porcentaje (suma = 100%).

---

## Monorepo

```
babylon-insurance/
├── frontend/          React 18 + Vite — SPA modular con theming por tenant
├── backend/           Spring Boot 4.0.3 + Java 23 + WebFlux + MongoDB Reactive
├── tests/             Playwright E2E — 36 tests contra producción Cloud Run
├── docs/rag/          Base RAG para el agente de automatización — GITIGNOREADO, no se sube al repo
└── .github/workflows/ CI/CD → GCP Cloud Run via GitHub Actions + Workload Identity Federation
```

---

## Backend

### Stack
- Java 23 + Spring Boot **4.0.3**
- WebFlux (todo Mono/Flux — nunca `.block()` en producción; excepción: `DataInitializer.run()`)
- Spring Data MongoDB Reactive
- Spring Security WebFlux
- Bean Validation
- Lombok (solo en adaptadores, nunca en dominio)
- Logstash Logback Encoder (logs JSON)

### Spring Boot 4 — diferencias críticas vs SB3
- **URI de MongoDB renombrada**: `spring.mongodb.uri` (NO `spring.data.mongodb.uri`)
- `spring.data.mongodb.*` solo cubre config de capa de datos (auto-index-creation, repositories, field-naming-strategy)
- `@WebFluxTest` eliminado — usar `WebTestClient.bindToController(new Controller(...))`
- `@MockBean` → `@MockitoBean` (spring-test)
- Autoconfiguration packages: `org.springframework.boot.mongodb.autoconfigure.*` y `org.springframework.boot.data.mongodb.autoconfigure.*`
- `reactiveMongoAuditingHandler` se registra dos veces con `@EnableReactiveMongoAuditing` → fix: `spring.main.allow-bean-definition-overriding=true`
- Evitar multi-documento YAML (`---`) con `${PROP}` sin default en secciones de perfil inactivas — usar archivos separados `application-{profile}.yml`

### Arquitectura Hexagonal estricta
```
com.babylon.insurance/
├── quote/
│   ├── domain/model/        Quote, SelectedCoverage, Beneficiary, QuoteStatus (records Java)
│   ├── domain/port/in/      CreateQuoteUseCase, GetQuotesUseCase
│   ├── domain/port/out/     QuoteRepositoryPort
│   ├── application/         CreateQuoteService, GetQuotesService, PremiumCalculatorStrategy
│   └── adapter/
│       ├── in/web/          QuoteController + DTOs (CreateQuoteRequest, QuoteResponse, etc.)
│       └── out/persistence/ QuoteDocument, QuoteMongoAdapter
├── product/
│   ├── domain/model/        Product, InsuranceModule, CoverageTier, DeathCoverage (records)
│   ├── domain/port/in/      GetProductUseCase
│   ├── domain/port/out/     ProductRepositoryPort
│   ├── application/         GetProductService
│   └── adapter/
│       ├── in/web/          ProductController + DTOs
│       └── out/persistence/ ProductDocument, ProductMongoAdapter
└── shared/
    ├── config/              SecurityConfig, MongoConfig, CorsConfig, WebFluxConfig
    ├── correlation/         CorrelationFilter (X-Correlation-ID, HIGHEST_PRECEDENCE)
    ├── encryption/          EncryptionPort (interfaz dominio), AesGcmEncryptionAdapter
    ├── exception/           GlobalExceptionHandler, QuoteValidationException, ResourceNotFoundException
    ├── logging/             StructuredLogger (eventos snake_case, nunca PII en logs)
    └── init/                DataInitializer (siembra catálogo si products está vacío — idempotente)
```

### Reglas invariantes del dominio
- `domain/` NO importa nada de Spring, MongoDB ni ningún framework
- Toda la lógica de negocio vive en `domain/` y `application/`
- Controladores solo delegan al caso de uso, sin lógica propia
- Adaptadores solo mapean y persisten, sin lógica propia
- `holderName` y `holderEmail` SIEMPRE cifrados con AES-256-GCM antes de persistir
- Prima calculada con `BigDecimal` (nunca `double`/`float`)
- `policyNumber` formato: `BLF-${timestamp.toString(36).toUpperCase()}`

### Endpoints
```
POST /api/quotes           → crear póliza (201)
GET  /api/quotes           → listar pólizas (200)
GET  /api/products/catalog → catálogo activo (200, Cache-Control max-age=300)
GET  /actuator/health      → health check (público)
GET  /actuator/info        → info (público)
OPTIONS /**                → CORS preflight (público)
```

### Catálogo de productos (sembrado por DataInitializer)
- **death**: t1=10M/$12.500 · t2=25M/$28.900 · t3=50M/$52.000 · t4=100M/$95.000 | hasBeneficiaries=true
- **disability**: t1=1M/$8.200 · t2=2.5M/$18.500 · t3=5M/$34.000 · t4=10M/$62.000
- **accidents**: t1=5M/$5.900 · t2=15M/$14.800 · t3=30M/$27.500 · t4=50M/$44.000
- **Asistencias** (sin costo, máx = coberturas activas): medico_virtual · asistencia_hogar · juridico · psicologico · nutricion · orientacion_fin

### Comandos backend (desde `backend/`)
```bash
./mvnw.cmd spring-boot:run                       # levantar local (puerto 8080)
./mvnw.cmd test                                  # todos los tests
./mvnw.cmd verify                                # tests + JaCoCo coverage check
./mvnw.cmd package -DskipTests
./mvnw.cmd test -Dtest=NombreClase               # test específico
./mvnw.cmd test -Dtest=NombreClase#nombreMetodo  # método específico
```

### Configuración local — secretos
Crear `backend/config/application.yml` (gitignoreado) o variables de entorno:
```yaml
spring:
  mongodb:
    uri: mongodb+srv://user:pass@cluster.mongodb.net/babylon?appName=babylom
babylon:
  encryption:
    key: <base64 32 bytes — generar con backend/scripts/generate-key.js>
  cors:
    allowed-origins: http://localhost:3000,http://localhost:5173,http://localhost:5174,http://localhost:5175
```

---

## Frontend

### Stack
- React 18 + Vite
- Estilos inline via `useTheme()` — cero archivos CSS externos
- Sin librerías de estado externas (hooks propios)

### Arquitectura
```
frontend/src/
├── styles/theme.js              ← ÚNICO archivo que cambia por cliente/tenant
├── context/ThemeContext.jsx
├── api/
│   ├── productApi.js            GET /api/products/catalog + normalizeModule()
│   └── quoteApi.js              POST /api/quotes
├── constants/
│   ├── catalog.js               ASSISTANCES, RELATIONS, copFmt(), shortFmt()
│   └── validations.js           funciones puras de validación de formulario
├── hooks/
│   ├── useProduct.js            carga catálogo desde API al montar
│   ├── useCoverage.js           estado módulos activos y tiers seleccionados
│   ├── useBeneficiaries.js      gestión + validación beneficiarios (suma=100%)
│   ├── useAssistances.js        set limitado (máx = coberturas activas)
│   └── usePremium.js            cálculos reactivos con useMemo
└── components/
    ├── atoms/                   Toggle, TierCard, Badge, ProgressBar
    ├── molecules/               ModuleCard, BeneficiaryRow, AssistanceCard, CartItem
    └── organisms/               CoverageSection, AssistanceSection, HolderForm, CartSummary
App.jsx                          orquestador principal
```

### Reglas invariantes del frontend
- Ningún componente importa directamente desde `theme.js` — solo via `useTheme()`
- Ningún componente tiene lógica de negocio — solo presentación y eventos
- Ningún componente tiene marca hardcodeada — todo desde el tema
- `useMemo` en todos los cálculos derivados
- `useCallback` en todos los handlers
- El catálogo viene 100% de la API (no hardcodeado); `productApi.js` normaliza la respuesta

### Variables de entorno frontend
```
VITE_API_URL=http://localhost:8080
```

### Comandos frontend (desde `frontend/`)
```bash
npm install
npm run dev    # desarrollo (puerto 5173+)
npm run build
```

---

## MongoDB

- Atlas M0 free — GCP us-central1 (sur América)
- Base de datos: `babylon`
- Colecciones: `quotes` (índice único en `policyNumber`), `products`
- `BigDecimal` ↔ `Decimal128` manejado por Spring Data automáticamente
- Setup inicial: `mongosh "URI" --file backend/src/main/resources/mongo/setup.js`

---

## Tests y Cobertura

- **34 tests unitarios JUnit 5** en backend (CreateQuoteServiceTest 7, QuoteControllerTest 6, PremiumCalculatorStrategyTest 5, AesGcmEncryptionTest 7, CorrelationFilterTest 4, ProductControllerTest 3, GetProductServiceTest 2)
- **JaCoCo**: ≥95% LINE en paquetes `*.domain.*` y `*.application.*` (verificado en `./mvnw.cmd verify`)
- JUnit 5 + Mockito + WebTestClient (`bindToController`, sin `@WebFluxTest`)
- `@DisplayName` en español
- Sin `.block()` en tests reactivos
- Corren en CI (job `Test Backend`) en cada push/PR a `backend/**` — **requisito obligatorio de merge** desde 2026-07-20 (`required_status_checks` en el ruleset de `main` exige el check `Test Backend` en verde, además de la aprobación humana). Playwright/E2E NO es requisito de merge todavía — queda para cuando el agente de automatización lo integre.
- No existe actualmente un test `@SpringBootTest` tipo "smoke test" en el código

---

## Seguridad

- AES-256-GCM: IV aleatorio 12 bytes por operación, tag 128 bits, key desde env
- X-Correlation-ID: sanitizado `[a-zA-Z0-9\-]{8,64}`, generado si no viene en el request
- CSP + X-Frame-Options DENY + HSTS + nosniff en todos los responses
- CORS: origins configurados en `babylon.cors.allowed-origins`
- Nunca loggear `holderName`, `holderEmail` ni datos sensibles
- Nunca exponer stack traces al cliente (`server.error.include-stacktrace=never`)
- **Nunca poner un valor de fallback con credenciales reales en `application.yml`** (`${VAR}` sin default para cualquier secreto) — hubo un incidente real de esto en el primer commit del repo (Mongo URI y AES key reales hardcodeadas como default), ya remediado: credenciales rotadas, historial de git reescrito con `git filter-repo`, repo pasado a público solo después

---

## CI/CD

- GitHub Actions → Workload Identity Federation (keyless, sin JSON keys)
- Push a `main` → lint → build imágenes Docker → push Artifact Registry → deploy Cloud Run
- Frontend: Cloud Run `--allow-unauthenticated`, 512Mi, min=0 max=5
- Backend: Cloud Run `--allow-unauthenticated` (**desplegado público actualmente** — el workflow real usa este flag; si se desea backend privado hay que cambiar `deploy-backend.yml` explícitamente), 1Gi, min=0 max=5
- Secrets en GCP Secret Manager: `MONGODB_URI`, `BABYLON_ENCRYPTION_KEY`, `ALLOWED_ORIGINS`
- GitHub Secrets: `WIF_PROVIDER`, `WIF_SERVICE_ACCOUNT`
- Repo **público** en GitHub (`maospina85/babylon-insurance`) desde 2026-07-20
- MongoDB Atlas M0 se **auto-pausa tras ~60 días de inactividad** — si el backend falla con `Failed looking up SRV record`, entrar a cloud.mongodb.com y darle "Resume" (1-3 min, no pierde datos)

### Branch protection en `main`
- Ruleset `main-pr-approval`: requiere 1 aprobación de PR antes de merge, bloquea force-push y borrado de `main`
- Bypass configurado **solo** para la cuenta owner (`maospina85`) — cualquier otra identidad (incluyendo el agente/bot) debe pasar por PR + aprobación humana real, sin excepción

### Agente de automatización (PR review + Playwright)
- GitHub App dedicada: `babylon-insurance-agent` (App ID `4350379`), instalada solo en este repo
- Permisos: `contents:write`, `pull_requests:write`, `metadata:read` — nada de `workflows` ni `secrets`
- Private key en `backend/.secrets/` (gitignoreada), tokens de instalación expiran en 1h
- Propósito: al abrir una PR, revisa el diff y genera/actualiza tests Playwright para mantener la suite E2E sincronizada con el código; a futuro el pipeline podría exigir la suite completa en verde como requisito de merge (no implementado aún)
- Detalle completo de convenciones que el agente debe seguir: `docs/rag/agente-automatizacion.md` (local, gitignoreado)

---

## Tests E2E (Playwright)

### Estructura
```
tests/
├── playwright.config.ts         Apunta a producción Cloud Run
├── package.json                 Scripts de ejecución
└── e2e/
    ├── fixtures/test-data.ts    Datos centralizados (VALID_HOLDER, MODULES, etc.)
    ├── helpers/setup.ts         Helpers reutilizables (loadApp, activateModule, fillHolder…)
    └── specs/
        ├── 01-smoke.spec.ts     Carga, título, catálogo completo
        ├── 02-coverage.spec.ts  Toggle, selección de tier, expansión
        ├── 03-beneficiaries.spec.ts  Agregar/quitar, validaciones, % suma=100
        ├── 04-assistances.spec.ts    Límite por módulos activos, toggle
        ├── 05-holder-form.spec.ts    Validaciones nombre/email/teléfono/edad
        ├── 06-cart-pricing.spec.ts   Precios, descuento anual, suma asegurada
        └── 07-happy-path.spec.ts     Flujo completo, submit, pantalla éxito
```

### Comandos (desde `tests/`)
```bash
npx playwright test              # headless, todos los tests
npx playwright test --headed     # con browser visible
npx playwright test --ui         # UI interactiva con timeline
npx playwright test 03-beneficiaries  # suite específica
npx playwright show-report       # ver último reporte HTML
```

### Invariantes de los tests
- **36/36 tests pasan** contra producción Cloud Run
- `loadApp` timeout = 45 s (Cloud Run `min=0` puede cold-start ~25 s)
- El botón CTA tiene `aria-label='Continuar con la cotización'` — NO usar el texto `'Continuar →'` en locators
- `useBeneficiaries` inicia vacío: siempre llamar "Agregar beneficiario" antes de intentar llenar inputs
- Para alertas de suma de beneficiarios usar `p[role="alert"]`, no `getByRole('alert')` (los error spans vacíos de BeneficiaryRow también tienen ese rol)

### Gitflow
- **Siempre usar feature branches** — nunca commitear directo a `main`
- Flujo: `feature/nombre` → commits → PR con descripción → merge
- Ramas del agente de automatización: prefijo `agent/`, ej. `agent/playwright/<slug>` — si el agente reacciona a una PR de dev ya abierta, no crea rama nueva, commitea directo sobre esa rama (ver `docs/rag/agente-automatizacion.md`)

---

## Convenciones

- Eventos de log: `snake_case` (`quote_issued`, `product_fetched`, `validation_failed`)
- IDs de módulo: `death` | `disability` | `accidents`
- IDs de tier: `t1` | `t2` | `t3` | `t4`
- IDs de asistencia: `medico_virtual` | `asistencia_hogar` | `juridico` | `psicologico` | `nutricion` | `orientacion_fin`
- Parentescos válidos: `Cónyuge` | `Hijo/a` | `Padre/Madre` | `Hermano/a` | `Otro`
- Frecuencia de pago: `mensual` | `anual`
- Status de póliza: `QUOTED` | `ISSUED` | `CANCELLED`
