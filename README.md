# Babylon Custom Insurance

Plataforma de seguros de vida modulares B2B2C.

## Stack

- **Frontend**: Vite + React 18
- **Backend**: Java 23 + Spring Boot 4.0.3 + WebFlux + MongoDB Reactive
- **Base de datos**: MongoDB Atlas M0
- **Deploy**: GCP Cloud Run + GitHub Actions

## Estructura

```
babylon-insurance/
├── frontend/     → React SPA (Vite)
├── backend/      → Spring Boot WebFlux (Arquitectura Hexagonal)
├── tests/        → Playwright E2E (36 tests contra producción)
└── .github/      → CI/CD Pipeline
```

## Levantar localmente

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Variables de entorno requeridas

### Backend (`backend/.env`)

```
MONGODB_URI=mongodb://localhost:27017/babylon
BABYLON_ENCRYPTION_KEY=<base64 de 32 bytes — generar con: node scripts/generate-key.js>
ALLOWED_ORIGINS=http://localhost:3000
```

### Frontend (`frontend/.env`)

```
VITE_API_URL=http://localhost:8080
```

## Tests E2E (Playwright)

Los tests corren contra el entorno de producción en Cloud Run.

### Instalación (una vez)

```bash
cd tests
npm install
npx playwright install chromium
```

### Ejecutar

```bash
cd tests
npx playwright test              # headless — todos los tests
npx playwright test --headed     # con browser visible
npx playwright test --ui         # UI interactiva con timeline y screenshots
npx playwright test 03-beneficiaries  # suite específica
npx playwright show-report       # ver reporte HTML del último run
```

### Suites disponibles

| Suite | Descripción | Tests |
|-------|-------------|-------|
| `01-smoke` | Carga de app, título, catálogo completo | 3 |
| `02-modules` | Toggle módulos, selección de tier | 6 |
| `03-beneficiaries` | Agregar/quitar beneficiarios, validaciones, suma 100% | 8 |
| `04-assistances` | Límite por módulos activos, toggle | 4 |
| `05-holder-form` | Validaciones nombre, email, teléfono, edad | 7 |
| `06-cart-pricing` | Precios en tiempo real, descuento anual | 4 |
| `07-happy-path` | Flujo completo de cotización y pantalla de éxito | 4 |

> **Nota:** Cloud Run usa `min=0` — el primer test puede tardar ~25 s mientras el backend hace cold start.

## Arquitectura de Infraestructura (GCP)

```mermaid
graph TB
    DEV(["👨‍💻 Developer\nGitHub push → main"])

    subgraph GHA["⚙️ GitHub Actions"]
        WF["CI/CD Workflow"]
        WIF["Workload Identity\nFederation (OIDC)\nkeyless — sin JSON keys"]
    end

    subgraph GCP["☁️ Google Cloud Platform — us-central1"]
        SA["🔑 Service Account\nbabylon-cicd-sa"]

        subgraph BUILD["Cloud Build"]
            CB["Docker build\nfrontend + backend"]
        end

        subgraph AR["Artifact Registry"]
            IMG_FE["babylon-frontend:latest"]
            IMG_BE["babylon-backend:latest"]
        end

        subgraph SM["Secret Manager"]
            S1["MONGODB_URI"]
            S2["BABYLON_ENCRYPTION_KEY"]
            S3["ALLOWED_ORIGINS"]
        end

        subgraph CR["Cloud Run"]
            FE["🌐 Frontend\n512 Mi · min=0 · max=5\npúblico"]
            BE["⚙️ Backend\n1 Gi · min=0 · max=5\nprivado (no-auth)"]
        end
    end

    subgraph EXT["🌍 Externo"]
        ATLAS[("MongoDB Atlas\nM0 Free · GCP us-central1\nDB: babylon")]
        USER(["👤 Usuario\nBrowser"])
    end

    DEV -->|"git push main"| WF
    WF -->|"OIDC token"| WIF
    WIF -->|"impersonate"| SA
    SA -->|"trigger build"| CB
    CB -->|"push image"| IMG_FE
    CB -->|"push image"| IMG_BE
    IMG_FE -->|"deploy"| FE
    IMG_BE -->|"deploy"| BE
    BE -.->|"lee secretos\nen arranque"| SM
    BE -->|"TLS · Atlas URI"| ATLAS
    USER -->|"HTTPS"| FE
    FE -->|"REST /api/**\nHTTPS"| BE
```

---

## Gitflow

```
main
 └── feature/nombre-del-cambio   ← branch de trabajo
      └── PR → revisión → merge a main
```

Nunca commitear directamente a `main`.
