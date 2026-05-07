# ERP Starter (React + Spring Boot)

Monorepo scaffold for an ERP system:

- `frontend/`: React (Vite) UI
- `backend/`: Java Spring Boot API

## Prerequisites

- Node.js LTS (includes `npm`)
- JDK 21 (or 17)
- Maven 3.9+

## Run (dev)

### Backend

```bash
cd backend
mvn spring-boot:run
```

Backend starts on `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend starts on `http://localhost:5173`.

## API quick check

- `GET /api/health` -> `{ "status": "ok" }`
- `GET /api/me` -> requires HTTP Basic auth

Default credentials:
- username: `admin`
- password: `admin`

