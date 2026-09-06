# Supplier Performance Rating System (SPRS) — Final Architecture Specification

## 1. System Overview & Roadmap Completion
The **Supplier Performance Rating System (SPRS)** is an enterprise-grade platform designed for end-to-end supplier lifecycle management, multi-criteria evaluations, automated score calculation, AI-assisted performance analytics, workflow approvals, real-time monitoring, enterprise integrations, and cloud disaster recovery.

The completion of **Phase 17** marks the final milestone of the 17-phase system development roadmap.

---

## 2. Multi-Tier System Architecture Diagram

```
+-----------------------------------------------------------------------------------+
|                                PRESENTATION TIER                                  |
|                                                                                   |
|  React 18 + TypeScript + Vite + TailwindCSS SPA                                   |
|  - Role-Based Dynamic Dashboards (Admin, Manager, Evaluator, Supplier)            |
|  - Lucide Icons + Recharts Analytics + Export Engines (PDF/Excel/CSV)             |
+-----------------------------------------+-----------------------------------------+
                                          | HTTP / HTTPS (REST API)
                                          v
+-----------------------------------------------------------------------------------+
|                             GATEWAY & REVERSE PROXY                               |
|                                                                                   |
|  Nginx 1.27 Reverse Proxy & Static Server                                         |
|  - TLS Termination & Security Headers (CSP, HSTS, X-Frame-Options, XSS Protection) |
|  - Gzip Compression & Static Asset Caching (Cache-Control: 1y)                    |
|  - Reverse Proxy routing (`/api/*` and `/actuator/*` to Backend Cluster)          |
+-----------------------------------------+-----------------------------------------+
                                          |
                                          v
+-----------------------------------------------------------------------------------+
|                               APPLICATION TIER                                    |
|                                                                                   |
|  Spring Boot 3.3.3 API Service (Java 17 / Eclipse Temurin)                        |
|                                                                                   |
|  +---------------------------+  +---------------------------+                     |
|  | Authentication & Security |  | Core Business Services    |                     |
|  | - Stateless JWT Filter    |  | - Supplier Management     |                     |
|  | - RBAC Authority Matrix   |  | - Evaluation & Scoring    |                     |
|  | - API Key External Filter |  | - Rating Engine           |                     |
|  | - Rate Limiter (Token Bkt)|  | - Improvement Actions     |                     |
|  +---------------------------+  +---------------------------+                     |
|  +---------------------------+  +---------------------------+                     |
|  | Intelligence & Analytics  |  | Integrations & Workflows  |                     |
|  | - AI Insights & Anomalies |  | - Webhook Dispatcher     |                     |
|  | - BI Report Builder       |  | - External REST API       |                     |
|  | - Dynamic KPI Calculator  |  | - Workflow Approvals/Esc. |                     |
|  +---------------------------+  +---------------------------+                     |
|  +---------------------------+  +---------------------------+                     |
|  | Operational Health        |  | Production Validation     |                     |
|  | - Spring Boot Actuator    |  | - Fail-Fast Config Check  |                     |
|  | - Deep DB Health Indicator|  | - HikariCP Pool Manager   |                     |
|  +---------------------------+  +---------------------------+                     |
+-----------------------------------------+-----------------------------------------+
                                          | JDBC (HikariCP Connection Pool)
                                          v
+-----------------------------------------------------------------------------------+
|                               PERSISTENCE TIER                                    |
|                                                                                   |
|  MySQL 8.0 Relational Database Engine                                             |
|  - Spring Data JPA / Hibernate ORM with optimistic locking                        |
|  - Indexed Schemas for high-throughput evaluation and rating queries              |
|  - Automated single-transaction snapshots & binlog replication                     |
+-----------------------------------------------------------------------------------+
```

---

## 3. Comprehensive Phase Summary (Phases 1–17)

| Phase | Core Capability | Architectural Highlights |
| :--- | :--- | :--- |
| **Phase 1** | Project Setup & Architecture | Monorepo layout, Maven Java 17 backend, React 18 TypeScript frontend. |
| **Phase 2** | Database & Core Models | MySQL schema, JPA entities, audited timestamps, base repositories. |
| **Phase 3** | Authentication & Security | JWT token generation/validation, password encryption (BCrypt), Auth Controller. |
| **Phase 4** | Role-Based Access Control | 4 primary roles (`ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_EVALUATOR`, `ROLE_SUPPLIER`). |
| **Phase 5** | Supplier Management | Supplier CRUD, category mapping, search, filtering, status transitions. |
| **Phase 6** | Evaluation Criteria | Weighted criteria matrices, score validation, scoring rubrics. |
| **Phase 7** | Evaluation Submissions | Multi-criteria evaluation entry, draft saving, workflow submissions. |
| **Phase 8** | Rating Engine | Automated score aggregation, letter grade assignment, weighted calculation. |
| **Phase 9** | Dashboard & Analytics | Executive dashboards, KPI scorecards, interactive charts (Recharts). |
| **Phase 10**| Reporting & Export | Standardized PDF, Excel, and CSV export generators. |
| **Phase 11**| AI Intelligence | Anomaly detection, benchmark comparisons, risk prediction engine. |
| **Phase 12**| Notifications & Monitoring | In-app alerts, email dispatch templates, real-time event logs. |
| **Phase 13**| Supplier Portal | Dedicated vendor portal, self-service dispute filing, scorecard review. |
| **Phase 14**| Workflow & Approvals | Multi-level approval chains, SLA tracking, automated escalation triggers. |
| **Phase 15**| Business Intelligence & KPI Builder | Custom KPI formula builder, multi-dimensional BI reports, executive dashboards. |
| **Phase 16**| Enterprise Integrations & APIs | Secure external REST APIs, API key authentication, HMAC signed webhooks, rate limiting. |
| **Phase 17**| Cloud Deployment & DR | Multi-stage Dockerfiles, production Compose stack, backup/restore scripts, CI/CD, DR plan. |

---

## 4. Security Architecture & Threat Mitigation

1. **Stateless JWT Authentication**: Access tokens signed using HMAC-SHA256 with 24-hour expiration; verified by `AuthTokenFilter`.
2. **API Key Integration Security**: External B2B endpoints (`/api/v1/external/**`) require `X-API-KEY` with SHA-256 validation via `ApiKeyAuthenticationFilter`.
3. **Webhook Integrity**: Webhook payloads signed with HMAC-SHA256 headers (`X-SPRS-Signature`) for tamper-proof consumer verification.
4. **Rate Limiting**: Integration endpoints protected by in-memory token bucket rate limiters preventing DoS attacks.
5. **Least-Privilege Non-Root Execution**: Backend runs under dedicated Alpine Linux user `spruser` (`UID 10001`).
6. **Production Configuration Validation**: `ProductionEnvironmentValidator` prevents application startup if insecure default keys or misconfigured datasources are detected in the `prod` profile.

---

## 5. High Availability, Scalability & Disaster Recovery

* **Horizontal Scalability**: Fully stateless API layer supports horizontal auto-scaling (AWS ECS Fargate, Kubernetes HPA, GCP Cloud Run) without sticky sessions.
* **Database Connection Pooling**: Tuned HikariCP pool (`minimum-idle: 5`, `maximum-pool-size: 20`, `connection-timeout: 30000ms`).
* **Automated DR & PITR**: Non-blocking `mysqldump` script with gzip compression and automated 30-day retention pruning.
* **Target RTO / RPO**: RTO < 30 Minutes, RPO < 15 Minutes (with continuous binlog replication).
