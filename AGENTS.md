# AGENTS.md - Oenobox Development Guide

This guide documents essential patterns and workflows for AI coding agents working on Oenobox, a wine tasting inventory management system.

## Quick Start

### Prerequisites
- Java 17, Maven Wrapper, Node.js 18+, PostgreSQL 14+

### Local Development
```powershell
# Backend (Spring Boot on 8080)
cd Backend/Oenobox
.\mvnw spring-boot:run

# Frontend (Vite on 5173, in another terminal)
cd Frontend/Frontend
npm install
npm run dev

# API available at: http://localhost:8080/api
# Swagger UI at: http://localhost:8080/swagger-ui.html
# Frontend at: http://localhost:5173
```

### Environment Setup
- Backend reads `application-{profile}.properties` where `SPRING_PROFILES_ACTIVE` (local|test|prod)
- Local defaults: PostgreSQL `oenobox:oenobox@localhost:5432/oenobox`
- Frontend: Set `VITE_API_BASE_URL` in `.env.development` (defaults to `http://localhost:8080/api`)
- Migrations auto-run on startup via Flyway from `src/main/resources/db/migration`

---

## Architecture Overview

### Monorepo Structure
```
OenoboxV2/
├── Backend/Oenobox/          # Spring Boot 3.4.0 + PostgreSQL
│   ├── src/main/java/lu/caepatriot/oenobox/
│   │   ├── cellar/
│   │   ├── inventory/
│   │   ├── tasting/
│   │   ├── winecatalog/
│   │   └── common/            # Shared: controllers, exceptions, utils
│   └── src/main/resources/db/migration/  # Flyway migrations (V1__*)
├── Frontend/Frontend/         # Vue ^3.5.11 + Vite
│   └── src/
│       ├── modules/
│       ├── admin/
│       ├── assistant/
│       ├── cellar/
│       ├── dashboard/
│       ├── imports/
│       ├── inventory/
│       ├── tasting/
│       ├── winecatalog/
│       ├── core/              # Shared (router, plugins, stores, utils)
│       ├── services/          # API clients (api.js)
│       └── views/             # Page components
└── docs/architecture/         # Domain documentation
```

### Domain Organization
Each domain (`cellar`, `inventory`, `tasting`, `winecatalog`) follows identical structure:
- **controller/**: REST endpoints `@RequestMapping("/api/{domain}")`
- **service/**: Business logic, transaction boundaries, error handling
- **repository/**: Spring Data JPA interfaces extending `JpaRepository<Entity, Long>`
- **entity/**: JPA `@Entity` classes mapped to PostgreSQL tables
- **dto/**: Data transfer objects for API responses

---

## Backend Patterns

### API Endpoints
All endpoints follow RESTful conventions under `/api/{domain}`:
```
GET    /api/wines               # List all
GET    /api/wines/{id}          # Fetch one
POST   /api/wines               # Create
PUT    /api/wines/{id}          # Update
DELETE /api/wines/{id}          # Delete
```

Controllers use:
- `@RestController` + `@RequestMapping` + `@CrossOrigin("*")`
- `@Tag` for Swagger documentation
- `ResponseEntity<T>` for type-safe responses
- Example: `winecatalog/controller/WineController.java`

### Service Layer
Services are `@Service` with injected repositories:
```java
@Service
public class WineService {
  private final WineRepository repo;
  
  public WineService(WineRepository repo) { this.repo = repo; }
  
  @Transactional(readOnly = true)  // Use for read operations
  public List<WineDto> getAll() { /* ... */ }
}
```

**Key patterns:**
- Constructor injection (no `@Autowired`)
- `@Transactional` on methods requiring database transactions
- Stream/collect for filtering: `.stream().filter(...).collect(Collectors.toList())`
- DTOs for responses, entities for persistence

### DTOs (Data Transfer Objects)
DTOs shield API from entity structure and enable nested responses:
```java
public class WineDto {
  private Long id;
  private String name;
  private WineTypeDto wineType;  // Nested DTO
  private List<CepageDto> cepages;
  // Getters/setters...
}
```

Mapping: Services convert entities → DTOs before returning to controllers.

### JPA Repository Conventions
Repositories extend `JpaRepository<Entity, Long>`:
```java
@Repository
public interface WineRepository extends JpaRepository<Wine, Long> {
  List<Wine> findByNameContainingIgnoreCaseOrRegionContainingIgnoreCaseOrderByNameAsc(
    String name, String region);
  
  @Query("SELECT w FROM Wine w WHERE w.wineType.id = :typeId")
  List<Wine> findByWineTypeId(@Param("typeId") Long typeId);
}
```

**Method naming:** Spring parses `findBy` + property names + conditions (Containing, IgnoreCase, OrderBy)
**Custom queries:** Use `@Query` with JPQL for complex logic

### Entity Mappings
Entities use `@Table`, `@Column`, `@ManyToOne`, `@OneToMany`:
- Timestamps: `@Column(name = "created_at")` mapped to `LocalDateTime`
- Enums: Stored as VARCHAR or INTEGER
- Collections: `@ElementCollection` for simple types, `@OneToMany` for entity relationships
- Foreign keys: `@ManyToOne` on child, `@OneToMany(mappedBy="...")` on parent

### Exception Handling
Common exceptions in `common/exception/`:
```java
throw new ResourceNotFoundException("Wine not found with id: " + id);
```

---

## Frontend Patterns

### Component Structure
- **Vue 3 SFC** (Single File Components) with `<script setup>`, `<template>`, `<style scoped>`
- Vuetify components: `<v-card>`, `<v-btn>`, `<v-text-field>`, etc.
- Composables in `shared/composables/` for reusable logic
- Module-specific components in `modules/{domain}/components/`

### Routing
`core/router/index.js` defines routes:
```javascript
{
  path: '/tasting',
  name: 'tasting',
  component: () => import('@/views/TastingView.vue')
}
```

Child routes supported:
```javascript
{
  path: '/inventory',
  component: () => import('@/views/InventoryView.vue'),
  children: [
    { path: 'overview', component: (...) },
    { path: 'intake', component: (...) }
  ]
}
```

### State Management (Pinia)
Stores in `stores/` + `modules/{domain}/store/`:
```javascript
// src/stores/cave.js
import { defineStore } from 'pinia'
export const useCaveStore = defineStore('cave', () => {
  const caves = ref([])
  const fetchCaves = async () => {
    caves.value = await caveApi.getAll()
  }
  return { caves, fetchCaves }
})
```

Usage in components:
```javascript
const caveStore = useCaveStore()
onMounted(() => caveStore.fetchCaves())
```

### API Client (services/api.js)
Axios client with interceptors, organized by domain:
```javascript
export const wineApi = {
  getAll: (params) => api.get('/wines', { params }),
  getById: (id) => api.get(`/wines/${id}`),
  create: (data) => api.post('/wines', data)
}

export const caveApi = {
  getWorkspace: (id) => api.get(`/caves/${id}/workspace`),
  bulkUpdateSpaces: (payload) => api.post('/caves/spaces/bulk', payload)
}
```

Interceptors handle:
- Request logging: `console.log('API Request:', method, url)`
- Response success/error callbacks via `serverStatus` module

---

## Database & Migrations

### Flyway Migration Management
Migrations in `src/main/resources/db/migration/` named `V{number}__{description}.sql`:
- `V9__modular_domain_separation.sql` — modular domain separation
- `V10__enhance_wine_catalog_details.sql` — enhance wine catalog details
- `V11__add_space_group.sql` — add space group
- `V12__enhance_cellar_workspace.sql` — enhance cellar workspace
- `V13__inventory_intake_foundation.sql` — inventory intake foundation — Inventory system tables

**Key settings** (`application.properties`):
```ini
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.jpa.hibernate.ddl-auto=validate  # Never auto-generate; migrations only
```

**To add a migration:**
1. Create `V{N+1}__{description}.sql` in migration folder
2. Next run will auto-execute it (baseline tracking prevents re-runs)
3. Ensure idempotent scripts or use `IF NOT EXISTS`

### Schema Patterns
- Timestamps: `created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP`, `updated_at TIMESTAMP`
- Foreign keys: `FOREIGN KEY (wine_type_id) REFERENCES wine_type(id) ON DELETE CASCADE`
- Unique constraints: `UNIQUE(name, wine_type_id)` for composite uniqueness
- JSONB for flexible fields: `wine_type_restriction JSONB` (used in tasting_field)

---

## Development Workflows

### Adding a New Feature

**1. Backend entity & migration:**
- Create table in new migration `V{N}__feature_name.sql`
- Create `src/main/java/lu/caepatriot/oenobox/{domain}/entity/NewEntity.java`
- Create `{domain}/repository/NewEntityRepository.java` (extends JpaRepository)

**2. Backend service & DTO:**
- Create `{domain}/dto/NewEntityDto.java` (immutable response shape)
- Create `{domain}/service/NewEntityService.java` (injectable, transactional)
- Implement CRUD logic, return DTOs

**3. Backend controller:**
- Create `{domain}/controller/NewEntityController.java`
- Add `@RestController`, `@RequestMapping("/api/{lowercase}")`, `@Tag`
- Wire service via constructor injection
- Return `ResponseEntity<T>`

**4. Frontend API:**
- Add methods to `src/services/api.js` under new export (e.g., `export const newEntityApi = {...}`)
- One method per endpoint: `getAll()`, `getById(id)`, `create(data)`, etc.

**5. Frontend pages & components:**
- Add route in `core/router/index.js`
- Create page component `modules/{domain}/pages/{Feature}Page.vue`
- Create reusable components in `modules/{domain}/components/`
- Use `api.newEntityApi` to fetch data
- Manage state with Pinia store if needed

### Testing Locally
- Backend: Tests in `src/test/java/lu/caepatriot/oenobox/`
- Frontend: Manual testing via `npm run dev` + browser (no Jest configured)
- Both: Use local profile (`SPRING_PROFILES_ACTIVE=local`)

### Build & Deployment
- Backend: `mvn clean package` → JAR in `target/`
- Frontend: `npm run build` → Dist folder for static serving
- Profiles: Set `SPRING_PROFILES_ACTIVE=prod` + environment variables for cloud

---

## Common Gotchas & Tips

1. **DTO vs Entity:** Always return DTOs from controllers, never expose full entities (lazy loading, security)
2. **Transaction boundaries:** Read operations use `@Transactional(readOnly = true)`, write operations use `@Transactional`
3. **Frontend API calls:** All calls go through `api.js` interceptors—don't bypass with raw axios
4. **Timestamp zones:** PostgreSQL stores UTC; Java `LocalDateTime` handles conversion
5. **Flyway safety:** Test migrations locally first; production rollbacks are complex
6. **Module boundaries:** Keep domain services isolated; share DTO/entity references, not logic

---

## Key Files to Know

| File | Purpose |
|------|---------|
| `Backend/Oenobox/pom.xml` | Maven dependencies & plugins |
| `Backend/Oenobox/src/main/resources/application.properties` | Spring config (database, JWT, OAuth2, OCR) |
| `Frontend/Frontend/package.json` | Node dependencies & scripts |
| `Frontend/Frontend/vite.config.js` | Vite build config (@ alias) |
| `Frontend/Frontend/src/services/api.js` | All API client methods |
| `Frontend/Frontend/src/core/router/index.js` | Route definitions |
| `docs/architecture/domain-overview.md` | Architecture overview |

---

## External Integrations

- **JWT Authentication:** Configured in `application.properties` (`JWT_SECRET`, `JWT_ISSUER`)
- **OAuth2 (Google):** Configured via Spring Security + `GOOGLE_OAUTH_CLIENT_ID` env var
- **OCR (ocrspace):** Label photo scanning (`OCRSPACE_API_URL`, `OCRSPACE_API_KEY`)
- **Swagger/SpringDoc:** Auto-documented on `/swagger-ui.html`

---

## Maintaining This Document

### Automated Updates (Recommended)
Run this script after adding new domains, modules, or migrations:
```powershell
cd Backend/Oenobox
node ../../scripts/update-agents-md.js
```

This script auto-discovers and updates:
- Backend domains from `src/main/java/lu/caepatriot/oenobox/`
- Frontend modules from `src/modules/`
- Flyway migrations from `db/migration/`
- Spring Boot & Vue versions

### Setup Automated Sync (Optional)
Add to `.git/hooks/pre-commit` to auto-update before commits:
```bash
#!/bin/bash
cd "$(git rev-parse --show-toplevel)"
node scripts/update-agents-md.js
git add AGENTS.md
```

### Manual Sync Triggers
Update AGENTS.md when:
1. **New domain added**: `Backend/Oenobox/src/main/java/lu/caepatriot/oenobox/{newdomain}/`
2. **New frontend module**: `Frontend/Frontend/src/modules/{newmodule}/`
3. **API patterns change**: New controller conventions, endpoint structure
4. **Major dependency updates**: Spring Boot, Vue, Vuetify versions
5. **Build/deployment changes**: New profiles, environment setup
6. **New integrations added**: Auth, OCR, external APIs

---

**Last Updated:** May 1, 2026 | **Stack:** Spring Boot 3.4, Java 17, Vue 3, PostgreSQL
