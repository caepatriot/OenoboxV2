# OenoboxV2

Monorepo with:
- `Backend/Oenobox` (Spring Boot + PostgreSQL + Flyway)
- `Frontend/Frontend` (Vue 3 + Vite)

## 1. Prerequisites

- Java 17
- Maven Wrapper (already included)
- Node.js 18+
- PostgreSQL 14+

## 2. Backend setup

Path: `Backend/Oenobox`

1. Copy environment template:
   - `.env.example` -> `.env` (optional reference file)
2. Set system environment variables (or rely on local defaults):
   - `SPRING_PROFILES_ACTIVE` (`local`, `test`, `prod`)
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `SERVER_PORT` (prod profile)

Default local values are already configured:
- `jdbc:postgresql://localhost:5432/oenobox`
- user `oenobox`
- password `oenobox`

### Run backend (Windows)

```powershell
cd Backend/Oenobox
.\mvnw spring-boot:run
```

On startup, Flyway executes SQL migrations from:
- `src/main/resources/db/migration`

Current initial migration:
- `V1__init_schema.sql`

## 3. Frontend setup

Path: `Frontend/Frontend`

1. Install dependencies:

```powershell
cd Frontend/Frontend
npm install
```

2. Environment files:
- `.env.development`
- `.env.test`
- `.env.production`
- `.env.example`

Main variable:
- `VITE_API_BASE_URL` (used by `src/services/api.js`)

3. Run frontend:

```powershell
npm run dev
```

## 4. Profiles

Backend profile files:
- `application.properties` (shared defaults)
- `application-local.properties`
- `application-test.properties`
- `application-prod.properties`

Flyway is enabled for all profiles and Hibernate DDL auto-generation is disabled (`ddl-auto=validate`) so schema is managed by migrations.