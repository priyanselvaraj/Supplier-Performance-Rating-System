# Supplier Performance Rating System (SPRS) — Production Deployment Guide

This guide provides end-to-end instructions for running, testing, containerizing, backing up, and deploying the **Supplier Performance Rating System**.

---

## 🏗 System Architecture

```
                                  +-----------------------------+
                                  |    Web Browser / Client     |
                                  +-----------------------------+
                                                 |
                                         HTTP / HTTPS (:80 / :443)
                                                 v
                       +----------------------------------------------------+
                       |           Frontend (Nginx / React 18 SPA)          |
                       |                   Container: sprs-frontend         |
                       +----------------------------------------------------+
                                                 |
                                         REST API Proxy (:8080)
                                                 v
                       +----------------------------------------------------+
                       |         Backend API (Spring Boot 3.3.3)            |
                       |                   Container: sprs-backend          |
                       +----------------------------------------------------+
                                                 |
                                          JDBC Connection (:3306)
                                                 v
                       +----------------------------------------------------+
                       |              Database (MySQL 8.0)                  |
                       |                   Container: sprs-mysql            |
                       +----------------------------------------------------+
                                                 |
                                   Automated Daily Snapshots & PITR
                                                 v
                       +----------------------------------------------------+
                       |          Backup Archive Storage (GZIP)             |
                       +----------------------------------------------------+
```

---

## 💻 1. Local Development Setup (Without Docker)

### Prerequisites
- **Java**: OpenJDK 17 or higher
- **Maven**: 3.9+
- **Node.js**: 18.x or 20.x
- **npm**: 9.x+

### Backend Setup
1. Open a terminal in `./backend`:
   ```bash
   cd backend
   mvn clean compile
   ```
2. Run backend in development mode (uses H2 in-memory DB by default):
   ```bash
   mvn spring-boot:run
   ```
   *Backend API: `http://localhost:8080`*  
   *Swagger Docs: `http://localhost:8080/swagger-ui.html`*  
   *Actuator Health: `http://localhost:8080/actuator/health`*

### Frontend Setup
1. Open a second terminal in `./frontend`:
   ```bash
   cd frontend
   npm install
   ```
2. Start Vite development server:
   ```bash
   npm run dev
   ```
   *Frontend Application: `http://localhost:5173`*

### Default Test Credentials
- **Admin**: `username: admin` | `password: Admin@12345`
- **Manager**: `username: manager` | `password: Manager@12345`
- **Supplier**: `username: supplier_user` | `password: Supplier@12345`

---

## 🐳 2. Containerized Deployment (Docker Compose)

Docker Compose orchestrates the full multi-tier stack (MySQL 8.0 + Spring Boot 3.3.3 + Nginx React SPA) in isolated bridge networking.

### Prerequisites
- Docker Engine 24.0+
- Docker Compose v2.20+

### Step 1: Configure Environment Variables
Copy `deploy/.env.example` to `deploy/.env`:
```bash
cp deploy/.env.example deploy/.env
```
Ensure production passwords and a strong 256-bit JWT secret are set in `deploy/.env`:
```env
DB_NAME=spr_system_db
DB_USERNAME=spruser
DB_PASSWORD=SecurePassword@2026
DB_ROOT_PASSWORD=RootSecret@2026
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```

### Step 2: Build and Start Production Containers
```bash
docker compose -f deploy/docker-compose.prod.yml --env-file deploy/.env up --build -d
```

### Step 3: Verify Container Health
```bash
docker compose -f deploy/docker-compose.prod.yml ps
```
All 3 containers should display `(healthy)` or `running`:
- `sprs-mysql-prod` (MySQL 8.0 Database, port 3306)
- `sprs-backend-prod` (Spring Boot API, port 8080)
- `sprs-frontend-prod` (Nginx + React SPA, port 80)

### Step 4: Stop Containers
```bash
docker compose -f deploy/docker-compose.prod.yml down
```
*To wipe persistent database storage:*
```bash
docker compose -f deploy/docker-compose.prod.yml down -v
```

---

## 💾 3. Database Backup & Disaster Recovery Automation

### Automated Backup Script
Run automated single-transaction compressed database backups with 30-day retention pruning:
```bash
./deploy/scripts/backup.sh
```

### Disaster Recovery Restore Script
To restore the database from a compressed snapshot:
```bash
./deploy/scripts/restore.sh /path/to/backup.sql.gz
```
*Detailed disaster recovery plans are documented in [docs/DISASTER_RECOVERY_PLAN.md](docs/DISASTER_RECOVERY_PLAN.md) and [docs/DATABASE_BACKUP_AND_RECOVERY.md](docs/DATABASE_BACKUP_AND_RECOVERY.md).*

---

## ⚙️ 4. Environment Variables Reference

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `prod` (or `dev`) |
| `BACKEND_PORT` | Backend application port | `8080` |
| `DB_HOST` | Database hostname | `mysql` (Docker) or `localhost` (Local) |
| `DB_PORT` | MySQL connection port | `3306` |
| `DB_NAME` | Database schema name | `spr_system_db` |
| `DB_USERNAME` | Database user | `spruser` |
| `DB_PASSWORD` | Database user password | `sprpassword` |
| `DB_ROOT_PASSWORD` | MySQL root administrative password | `rootpassword` |
| `JWT_SECRET` | 256-bit HMAC secret key for signing tokens | `404E635266556A586E3272...` |
| `JWT_EXPIRATION_MS` | Token lifespan in milliseconds | `86400000` (24 hours) |
| `ALLOWED_ORIGINS` | Comma-separated CORS allowed origins | `http://localhost:5173,http://localhost:80,http://localhost` |
| `FRONTEND_PORT` | Port mapped to frontend container | `80` (or `5173`) |

---

## 🧪 5. Testing & Verification Commands

### Backend Automated Test Suite (259 Tests)
```bash
cd backend
mvn clean test
```
- Includes 198 Unit Tests (Services, Scoring, DTOs, Security, Utilities)
- Includes 60 API MockMvc Integration Tests (`AuthController`, `SupplierController`, `EvaluationController`, `ReportController`, `DashboardController`, `AiController`, `WorkflowController`, `ExternalApiController`)
- Context Integration Test (`SprSystemApplicationTests`)

### Frontend Component Tests (Vitest — 38 Tests)
```bash
cd frontend
npm test -- --run
```

### Frontend Production Build Test
```bash
cd frontend
npm run build
```

---

## 🔒 6. Production Security Checklist

- [x] **BCrypt Password Hashing**: Passwords stored with BCrypt (strength 10).
- [x] **Stateless JWT Tokens**: Tokens signed with HMAC-SHA256, expiration enforced.
- [x] **Role-Based Access Control**: `@PreAuthorize` guards all sensitive mutations and executive reports.
- [x] **Actuator Endpoint Exposure**: Only `/actuator/health` and `/actuator/info` exposed; sensitive metrics sealed.
- [x] **Multi-Stage Docker Builds**: Non-root runtime users used (`spruser:sprgroup`); no compilers or build tools in production images.
- [x] **Input Validation**: Jakarta Validation (`@Valid`, `@NotBlank`, `@Size`, `@Min`, `@Max`) active on all controller endpoints.
- [x] **Zero Secrets in VCS**: `.gitignore` configured to exclude `.env`, `.env.*`, and credentials.
- [x] **Production Validator**: `ProductionEnvironmentValidator` fails fast if weak or default keys are used in production.
