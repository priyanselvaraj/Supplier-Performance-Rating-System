# Production Deployment & Operations Guide

## 1. Prerequisites
Before deploying the Supplier Performance Rating System in a production environment, ensure the host machine meets the following minimum requirements:

* **Docker Engine**: Version 24.0+
* **Docker Compose**: Version 2.20+ (Plugin or Standalone)
* **Compute / Memory**: 2+ vCPU, 4GB+ RAM, 20GB+ SSD Storage
* **Network Ports**: Inbound access on ports `80` (HTTP), `443` (HTTPS), and optionally `8080` (API direct).

---

## 2. Quick-Start Production Deployment

### Step 1: Clone Repository and Navigate to Deploy Directory
```bash
git clone https://github.com/priyanselvaraj/Supplier-Performance-Rating-System.git
cd Supplier-Performance-Rating-System
```

### Step 2: Configure Production Environment Variables
Copy the template and configure your production secrets:
```bash
cp deploy/.env.example deploy/.env
```
Edit `deploy/.env`:
* Generate a secure 256-bit random JWT Secret:
  ```bash
  openssl rand -hex 32
  ```
* Set `DB_PASSWORD` and `DB_ROOT_PASSWORD` to strong unique strings.
* Set `ALLOWED_ORIGINS` to your production domain name(s) (e.g., `https://sprs.yourdomain.com`).

### Step 3: Launch Production Container Stack
```bash
docker compose -f deploy/docker-compose.prod.yml --env-file deploy/.env up -d --build
```

### Step 4: Verify Deployment Health
Check that all containers are healthy and running:
```bash
docker compose -f deploy/docker-compose.prod.yml ps
```
Verify the Actuator health probe:
```bash
curl -f http://localhost:8080/actuator/health
# Expected Output: {"status":"UP","components":{"db":{"status":"UP"...}}}
```

---

## 3. Zero-Downtime Rolling Update Procedure

To update the application to a new version without service interruption:
```bash
# 1. Pull latest Git changes
git pull origin main

# 2. Build new images in the background
docker compose -f deploy/docker-compose.prod.yml build

# 3. Perform zero-downtime container replacement
docker compose -f deploy/docker-compose.prod.yml up -d --no-deps --build backend frontend

# 4. Confirm system status
docker compose -f deploy/docker-compose.prod.yml ps
```

---

## 4. Production Logs & Monitoring

### Viewing Live Container Logs
```bash
# Tail all container logs
docker compose -f deploy/docker-compose.prod.yml logs -f

# Tail backend API logs specifically
docker compose -f deploy/docker-compose.prod.yml logs -f backend

# Tail frontend Nginx access/error logs
docker compose -f deploy/docker-compose.prod.yml logs -f frontend
```

### Application Metrics & Observability
* **Liveness & Readiness Probes**: `http://<host>:8080/actuator/health`
* **Application Info**: `http://<host>:8080/actuator/info`
* **Nginx Reverse Proxy API**: `http://<host>/api/`
