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

- **JaCoCo**: ≥95% LINE en paquetes `*.domain.*` y `*.application.*` (verificado en `./mvnw.cmd verify`)
- JUnit 5 + Mockito + WebTestClient
- `@DisplayName` en español
- Sin `.block()` en tests reactivos
- Smoke test (`@SpringBootTest`) excluye autoconfiguraciones MongoDB para no requerir conexión real

---

## Seguridad

- AES-256-GCM: IV aleatorio 12 bytes por operación, tag 128 bits, key desde env
- X-Correlation-ID: sanitizado `[a-zA-Z0-9\-]{8,64}`, generado si no viene en el request
- CSP + X-Frame-Options DENY + HSTS + nosniff en todos los responses
- CORS: origins configurados en `babylon.cors.allowed-origins`
- Nunca loggear `holderName`, `holderEmail` ni datos sensibles
- Nunca exponer stack traces al cliente (`server.error.include-stacktrace=never`)

---

## CI/CD

- GitHub Actions → Workload Identity Federation (keyless, sin JSON keys)
- Push a `main` → lint → build imágenes Docker → push Artifact Registry → deploy Cloud Run
- Frontend: Cloud Run `--allow-unauthenticated`, 512Mi, min=0 max=5
- Backend: Cloud Run `--no-allow-unauthenticated`, 1Gi, min=0 max=5
- Secrets en GCP Secret Manager: `MONGODB_URI`, `BABYLON_ENCRYPTION_KEY`
- GitHub Secrets: `GCP_PROJECT_ID`, `WIF_PROVIDER`, `WIF_SERVICE_ACCOUNT`

---

## Convenciones

- Eventos de log: `snake_case` (`quote_issued`, `product_fetched`, `validation_failed`)
- IDs de módulo: `death` | `disability` | `accidents`
- IDs de tier: `t1` | `t2` | `t3` | `t4`
- IDs de asistencia: `medico_virtual` | `asistencia_hogar` | `juridico` | `psicologico` | `nutricion` | `orientacion_fin`
- Parentescos válidos: `Cónyuge` | `Hijo/a` | `Padre/Madre` | `Hermano/a` | `Otro`
- Frecuencia de pago: `mensual` | `anual`
- Status de póliza: `QUOTED` | `ISSUED` | `CANCELLED`
