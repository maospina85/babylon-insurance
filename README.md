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
