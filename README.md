# Hotel Reservations

Sistema de reservas hoteleras **full-stack** (Java + Angular).

- **Backend:** Java 21 · Spring Boot **3.5.x** · Web · Security · Data JPA · Flyway · PostgreSQL
- **Frontend:** Angular **20** · Angular Material (M3 Azure/Blue) · Reactive Forms
- **Infra (dev):** Docker Compose (PostgreSQL 16, Redis)
- **Herramientas:** IntelliJ IDEA CE · VS Code · SonarLint · Prettier/ESLint · Postman · DBeaver
- **SO:** Windows 10 Pro (WSL2 + Docker Desktop)

---

## Estructura

```
hotel-reservas/
├─ backend/
│  ├─ src/main/java/com/juanda/backend/
│  │  ├─ config/           # SecurityConfig, OpenApiConfig, etc.
│  │  └─ web/              # Controllers (HealthController, SearchController)
│  └─ src/main/resources/
│     └─ db/migration/     # Migraciones Flyway (V1__init.sql, ...)
├─ frontend/
│  └─ src/app/
│     ├─ core/             # ApiService
│     └─ features/search/  # Pantalla de búsqueda
├─ docker-compose.yml      # Postgres + Redis (dev)
├─ .editorconfig           # Estilo (LF, etc.)
├─ .gitattributes          # EOL (LF/CRLF)
└─ .gitignore
```

---

## Requisitos

- **Java 21**
- **Docker Desktop** (WSL2)
- **Node 22** / **npm 11**
- **Angular CLI 20** (`npm i -g @angular/cli@20`)
- (Opcional) **DBeaver**, **Postman**

---

## Cómo ejecutar

### 1) Infra (BD/Cache)

```powershell
# desde la raíz del repo
docker compose up -d postgres redis
docker ps   # verifica postgres:16 en 5432
```

**Credenciales (dev):**

- Host `localhost` · Port `5432`
- DB `hotel`
- User `dev` · Pass `dev`

### 2) Backend (Spring Boot)

```powershell
cd backend
.\mvnw.cmd spring-boot:run
# Swagger UI:  http://localhost:8080/swagger-ui.html
# OpenAPI:     http://localhost:8080/v3/api-docs
# Health:      http://localhost:8080/api/health
# Search mock: http://localhost:8080/api/search?checkIn=2025-09-15&checkOut=2025-09-18&guests=2
```

> **Migraciones:** Flyway aplica automáticamente los scripts en `backend/src/main/resources/db/migration/`.

### 3) Frontend (Angular)

```powershell
cd frontend
npm i
npm start
# http://localhost:4200  (proxy /api -> :8080)
```

- Proxy dev: `frontend/proxy.conf.json` enruta `/api/*` al backend (evita CORS).
- Tema **Material M3** (Azure/Blue) + **Roboto** + **Material Symbols** listos.

---

## Endpoints

- `GET /api/health` → `{"status":"ok"}`
- `GET /api/search?checkIn=YYYY-MM-DD&checkOut=YYYY-MM-DD&guests=N`  
  **200**:
  ```json
  {
    "checkIn": "2025-09-15",
    "checkOut": "2025-09-18",
    "guests": 2,
    "results": [
      { "roomId": 101, "type": "double", "capacity": 2, "price": 120.0 },
      { "roomId": 203, "type": "suite", "capacity": 3, "price": 240.0 }
    ]
  }
  ```
  **400** (manejador global):
  ```json
  {
    "timestamp": "2025-09-10T05:10:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "checkOut debe ser posterior a checkIn y guests >= 1",
    "path": "/api/search"
  }
  ```

---

## Base de datos

- Migración inicial: `V1__init.sql` crea tablas:
  `app_user`, `room`, `rate_plan`, `room_inventory`, `booking`, `booking_item`.
- Tabla de control: `flyway_schema_history`.

**DBeaver (conexión):**

- Driver PostgreSQL · Host `localhost` · Port `5432`
- DB `hotel` · User `dev` · Pass `dev` → _Test Connection_ → _Finish_.

---

## Flujo Git (modo empresa)

- Ramas por cambio: `feat/*`, `fix/*`, `chore/*`, `docs/*`  
  Ej.: `feat/backend-search-mock`, `feat/frontend-search-form`
- Commits: **Conventional Commits**  
  Ej.: `feat(frontend): search page wired to /api/search`
- PRs pequeños → **Squash & merge** → **Delete branch**.
- `main` protegido (PR obligatorio).

---

## Scripts útiles

**Backend**

```powershell
cd backend
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

**Frontend**

```powershell
cd frontend
npm run build         # producción
npm test              # unit tests (Karma)
npm start             # dev con proxy
```

**Docker**

```powershell
docker compose up -d postgres redis
docker compose logs -f postgres
docker compose down -v   # ⚠️ borra volúmenes (resetea la BD)
```

---

## Localización (opcional)

Para español (fechas/moneda):

```ts
// src/main.ts
import { registerLocaleData } from "@angular/common";
import es from "@angular/common/locales/es";
registerLocaleData(es);

// src/app/app.config.ts
import { LOCALE_ID } from "@angular/core";
import { MAT_DATE_LOCALE } from "@angular/material/core";

export const appConfig = {
  providers: [
    { provide: LOCALE_ID, useValue: "es" },
    { provide: MAT_DATE_LOCALE, useValue: "es-ES" },
  ],
};
```

---

## Roadmap

- Entidades JPA + repos + MapStruct
- Servicio de búsqueda real (inventario/precios)
- Seeds (`V2__seed.sql`)
- Tests (JUnit + Testcontainers)
- Cache (Redis) en `/api/search`
- Autenticación JWT (roles: admin/recepción/cliente)
- CI (GitHub Actions) y Sonar

---

## Licencia

MIT — libre uso con atribución.
