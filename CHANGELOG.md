# Changelog

All notable changes to the **Supplier Performance Rating System (SPRS)** project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.8.0] - 2026-09-06

### Added (Phase 18 — Intelligent Automation, AI Copilot and Advanced Decision Support)
- **Supplier Management AI Copilot (`/ai-copilot`)**:
  - Conversational natural language query engine with role-based data isolation (Suppliers restricted to their own scorecard; Admins/Managers access full portfolio).
  - Grounded responses with dynamic metric citations (`evaluations_count`, `current_score`, `risk_score`, `trend`) and recommended action links.
  - Interaction history auditing and user feedback rating (`AiInteractionHistory`).
- **Multi-Supplier Comparative AI Analysis**:
  - Side-by-side performance matrix synthesis comparing scorecards, risk tiers, and category standing (`POST /api/v1/ai/suppliers/compare`).
- **Executive AI Insights Briefing (`/executive-insights`)**:
  - C-suite synthesis summarizing portfolio health, risk distributions, critical supplier alerts, and strategic procurement recommendations.
- **Smart Workflow & Bottleneck Assistance**:
  - Proactive evaluation of pending approvals and overdue escalations with actionable remediation suggestions.
- **Human-in-the-Loop Recommendation Governance**:
  - Embedded prescriptive recommendations on `SupplierDetails.jsx` enabling managers to **Accept**, **Dismiss**, or **Create CAP Action**.
  - Persists human audit trail via `AiRecommendationDecision` and optionally creates actionable `SupplierImprovementAction` records.
- **Verification**:
  - 269 backend unit and integration tests passing (100% pass rate).
  - 41 frontend vitest component tests passing (100% pass rate).
  - Frontend production build generated with 0 errors.

---

## [1.7.0] - 2026-09-06

### Added (Phase 17 — Cloud Deployment, Scalability and Disaster Recovery)
- **Production Configuration & Security Hardening**:
  - `application-prod.yml`: Externalized environment mappings, HikariCP connection pooling, Actuator liveness/readiness probes, and GZIP compression.
  - `ProductionEnvironmentValidator`: Fail-fast startup listener validating 256-bit `JWT_SECRET`, detecting default dev keys, and checking datasource configuration.
  - `DatabaseHealthIndicator`: Deep Actuator health probe for database connectivity.
  - `logback-spring.xml`: Structured production logging with trace/span ID support and profile-specific log levels.
  - `frontend/nginx.conf`: Production Nginx reverse proxy with security headers (`X-Frame-Options`, `X-Content-Type-Options`, `X-XSS-Protection`, `Referrer-Policy`, `Permissions-Policy`), GZIP compression, and SPA routing fallback.
- **Production Containerization & Orchestration**:
  - `backend/Dockerfile`: Multi-stage Eclipse Temurin 17 JRE Alpine container running as non-root user `spruser` (`UID 10001`).
  - `frontend/Dockerfile`: Multi-stage Node.js 20 + Nginx 1.27 Alpine container with static asset optimization.
  - `deploy/docker-compose.prod.yml`: Production Compose stack with isolated networks, healthchecks, resource limits, and volume persistence.
  - `deploy/.env.example`: Comprehensive environment template for production secrets.
- **Automated Backup & Disaster Recovery Automation**:
  - `deploy/scripts/backup.sh`: Non-blocking `mysqldump` with gzip compression, checksum validation, and automated 30-day retention pruning.
  - `deploy/scripts/restore.sh`: Atomic gzip decompress and restore script with database connectivity and table count verification.
- **Enterprise CI/CD Workflow**:
  - `.github/workflows/ci-cd.yml`: Unified GitHub Actions pipeline validating backend Maven build/tests (259 tests), frontend Vitest/build (38 tests), and multi-container Docker images.
- **Comprehensive Disaster Recovery & Cloud Architecture Documentation**:
  - `docs/DATABASE_BACKUP_AND_RECOVERY.md`: Backup frequencies, single-transaction isolation, and PITR procedures.
  - `docs/DISASTER_RECOVERY_PLAN.md`: Business continuity metrics (RTO < 30m, RPO < 15m), scenario mitigation matrix, and failover runbook.
  - `docs/CLOUD_DEPLOYMENT.md`: Reference architectures for AWS (ECS Fargate + Aurora MySQL), GCP (Cloud Run + Cloud SQL), and Azure.
  - `docs/DEPLOYMENT_GUIDE.md`: Production operations manual for Docker Compose deployment and zero-downtime rolling updates.
  - `docs/FINAL_ARCHITECTURE.md`: Complete 17-phase system architecture specification.
- **Verification**:
  - 259 backend unit and integration tests passing (100% pass rate).
  - 38 frontend vitest component tests passing (100% pass rate).
  - Frontend production build generated with 0 errors.

---

## [1.6.0] - 2026-09-06

### Added (Phase 16 — Enterprise Integrations and External APIs)
- **External REST API Suite (`/api/v1/external/**`)**:
  - `ExternalApiController`: Ingest external evaluations, export supplier rating summaries, and trigger manual syncs.
  - `ApiKeyAuthenticationFilter`: High-performance API key authentication with SHA-256 key hashing and rate limiting.
  - `IntegrationAdminController`: Manage API keys, webhook subscriptions, and integration sync histories.
- **HMAC Webhook Event Notification Engine**:
  - `WebhookEventDispatcher`: Asynchronous webhook delivery with HMAC-SHA256 signature headers (`X-SPRS-Signature`) and exponential backoff retry.
- **Frontend Integration Management Dashboard**:
  - `IntegrationsDashboard.jsx`: API Key generation modal, Webhook subscription manager, manual sync triggers, and sync audit logs.

---

## [1.5.0] - 2026-09-05

### Added (Phase 15 — Advanced Reporting, Business Intelligence and KPI Builder)
- **Business Intelligence & KPI Calculation Engine**:
  - `BusinessIntelligenceService`: Dynamic multi-dimensional aggregation, cohort analysis, and trend forecasting.
  - `KpiAdminController`: Dynamic KPI formula builder supporting custom supplier performance metrics.
- **Interactive BI & Report Builder UI**:
  - `ReportBuilder.jsx`: Drag-and-drop metric selector, custom filters, and multi-format preview.
  - `BiDashboard.jsx`: High-density executive BI charts with cohort breakdown and rating correlation heatmaps.
  - `SavedReports.jsx`: Saved report template manager with scheduled export capabilities.

---

## [1.4.0] - 2026-09-04

### Added (Phase 14 — Enterprise Workflow Management and Multi-Level Approvals)
- **Configurable Multi-Step Approval Engine**:
  - `WorkflowEngineService`: Multi-step sequential and parallel approval definitions with strict SLA enforcement.
  - `ApprovalController`: Approve, reject, or request changes on pending evaluation scorecards and improvement actions.
  - `WorkflowEscalationController`: Automated escalation triggers when approval SLAs are breached.
- **Frontend Approval Center & Workflow Configurator**:
  - `MyApprovals.jsx`: Evaluator and Manager approval task queue with bulk actions.
  - `WorkflowDashboard.jsx`: Live workflow instance tracker with visual step progress bars.
  - `WorkflowConfigAdmin.jsx`: Admin workflow definition builder.

---

## [1.3.0] - 2026-09-03

### Added (Phase 13 — Dedicated Supplier Portal and Dispute Resolution)
- **Supplier Portal & Self-Service Portal**:
  - `SupplierPortalController`: Supplier scorecard review, dispute filing, and improvement action collaboration.
  - `SupplierPortalAdminController`: Admin dispute adjudication and vendor profile verification.
- **Frontend Vendor Portal Views**:
  - `SupplierDashboard.jsx`, `SupplierPerformance.jsx`, `SupplierEvaluations.jsx`, `SupplierCommunications.jsx`, `SupplierAccount.jsx`.

---

## [1.2.0] - 2026-09-01

### Added (Phase 12 — Advanced Notifications, Supplier Collaboration and Real-Time Monitoring)
- **Persistent Multi-Channel Notification Engine**:
  - `Notification` & `UserNotificationPreference` entities with user isolation, unread tracking, and duplicate suppression.
  - 10 new REST & Server-Sent Events (SSE) stream endpoints under `/api/v1/notifications/`.
- **Corrective Action Plan (CAP) Tracking**:
  - `SupplierImprovementAction` lifecycle engine (`OPEN` -> `IN_PROGRESS` -> `COMPLETED` / `CANCELLED`).
  - `/improvement-actions` portal and embedded Supplier Details CAP section.
- **Real-Time Operational Monitoring Dashboard**:
  - Live pulse dashboard at `/monitoring` with health status indicator (`HEALTHY`, `WARNING`, `ATTENTION_REQUIRED`).
- **Frontend Components**:
  - Navbar `NotificationBell` with animated live unread badge and interactive quick-action dropdown.
  - `/notifications` Notification Center with tab filters, preference modal, and bulk "Mark all read".

---

## [1.1.0] - 2026-09-01

### Added (Phase 11 — AI-Powered Supplier Intelligence and Predictive Analytics)
- **Time-Weighted Linear Trajectory Regression Engine**:
  - Predicts next-cycle performance score with trajectory velocity rate ($m$) and statistical confidence grading.
- **Multi-Factor Supplier Risk Scoring & Early Warning Engine**:
  - Calculates composite Risk Score (0–100) and classifies vendors into `LOW`, `MEDIUM`, `HIGH`, and `CRITICAL` risk tiers.
- **Prescriptive Criteria Remediation Engine**:
  - Analyzes criteria deficiencies ($< 75\%$) and generates prioritized, contextual corrective action plans.
- **AI Intelligence REST API Suite & Executive Dashboard**:
  - 8 new REST endpoints under `/api/v1/ai/` and interactive React view (`/ai-intelligence`).

---

## [1.0.0] - 2026-08-30

### Initial Full-Stack Platform Release (Phases 1–10)
- Core Java 17 + Spring Boot 3.3.3 API backend and React 18 + Vite frontend.
- MySQL 8.0 schema, Spring Security 6 with stateless JWT authentication, and RBAC matrix.
- Supplier and Category directory with multi-keyword search, filtering, and pagination.
- Multi-criteria weighted evaluation scoring engine with BigDecimal financial precision.
- Automated rating tier classification (`EXCELLENT`, `GOOD`, `AVERAGE`, `POOR`).
- Multi-format export engine (PDF, Excel, CSV) via OpenPDF and Apache POI.
- Multi-tier Docker Compose containerization and GitHub Actions CI pipelines.
