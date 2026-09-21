# Supplier Performance Rating System (SPRS) — Capstone Project Review & Presentation Guide

**Project Title**: Supplier Performance Rating System (SPRS)  
**Candidate Name**: Priyan Selvaraj  
**Candidate Email**: priyanselvaraj756@gmail.com  
**GitHub Repository**: https://github.com/priyanselvaraj/Supplier-Performance-Rating-System  
**Review Date**: 21.09.2026 (Monday)

---

## 📑 Presentation Structure (Reviewer Agenda)

1. **Problem Statement**
2. **Project Objectives**
3. **Methodology & System Architecture**
4. **Key Technical Implementation & Features**
5. **Results, Verification & Benchmarks**
6. **Cloud Deployment Architecture**
7. **Future Scope & Conclusion**
8. **Viva Voce / Q&A Cheatsheet**

---

## 1. Problem Statement

In modern supply chain ecosystems, enterprises struggle with fragmented and subjective vendor evaluations:
- **Lack of Centralized Metrics**: Organizations often track supplier metrics (quality, on-time delivery, cost variance, compliance) across isolated spreadsheets, causing inconsistent evaluations.
- **Delayed Risk Mitigation**: Supply chain disruptions occur without early warning indicators because historical performance patterns are not analyzed with predictive intelligence.
- **Absence of Vendor Self-Service**: Suppliers lack real-time visibility into their scorecards, leading to communication overhead, delayed dispute resolutions, and stagnant improvement plans.
- **Security & Notification Gaps**: Lack of real-time multi-channel audit alerts (e.g., login notifications, performance degradation alerts) increases vulnerability to unauthorized access and missed escalation SLAs.

---

## 2. Project Objectives

1. **Automated Multi-Criteria Rating Engine**: Compute composite supplier performance scores dynamically across weighted categories (Quality, Delivery, Pricing, Compliance, ESG).
2. **Predictive AI Risk Intelligence & Copilot**: Detect early supplier degradation trends, compute risk probabilities, and generate automated Corrective Action Plans (CAPA).
3. **Role-Based Enterprise Governance**: Provide isolated portals and access scopes for **Admin**, **Company Evaluators**, and **Suppliers**.
4. **Real-time Non-Blocking Communication**: Deliver instant in-app, SMS, and Gmail SMTP notifications on logins and score triggers without blocking application response times.
5. **Multi-Stage Approval Workflows**: Implement multi-tier approval mechanisms with escalation timers for supplier onboarding, score reviews, and status changes.
6. **Cloud-Native & Resilient Deployment**: Containerize with Docker, deploy with automated health probes, and maintain zero data loss through automated backup pipelines.

---

## 3. Methodology & System Architecture

### Tech Stack Breakdown
- **Backend**: Java 25, Spring Boot 3.3.3, Spring Security (JWT), Spring Data JPA (Hibernate), JavaMailSender.
- **Frontend**: React 18, Vite, Tailwind CSS, Lucide Icons, Recharts / Chart.js, Axios.
- **Database**: MySQL 8.0 with automated migrations, indexes, and seeded demo accounts.
- **Testing**: JUnit 5, Mockito, Vitest (100% code coverage across 329 test cases).
- **Deployment**: Docker, Docker Compose, Render / Vercel blueprint configs.

---

## 4. Key Technical Implementation & Features

### 🌟 1. Dynamic Supplier Rating & Evaluation Engine
- Calculates aggregated scores based on customized weight matrices.
- Supports historical trend charting, category benchmarks, and grade tiering (A, B, C, D).

### 🤖 2. AI Risk Copilot & Intelligence Engine
- Evaluates supplier delivery delays and defect ratios against threshold limits.
- Generates natural-language executive summaries and suggested mitigation strategies.

### 📧 3. Real-Time Gmail Login Notification Service
- Automatically sends a secure `SPRS Login Notification` to the authenticated user''s registered email address upon successful login.
- Uses configured `MAIL_FROM` / `MAIL_USERNAME` with dynamic recipient targeting (`user.getEmail()`).
- Employs **non-blocking exception isolation**: SMTP network delays or failures do not block user login (`emailNotificationSent: false`).

### 🔒 4. Role-Based Access Control (RBAC)
- **Admin**: Criteria definitions, category weighting, user management, integrations.
- **Company Evaluator**: Supplier evaluations, performance auditing, approval workflows.
- **Supplier**: Self-service portal, scorecard viewing, document uploads, improvement action responses.

---

## 5. Results & Test Verification

| Metric | Target Requirement | Achieved Result | Status |
| :--- | :--- | :--- | :--- |
| **Backend Test Suite** | 100% Pass Rate | **288 / 288 Tests Passing** | ✅ Complete |
| **Frontend Test Suite** | 100% Pass Rate | **41 / 41 Tests Passing** | ✅ Complete |
| **API Response Time** | < 200 ms | **< 45 ms average** | ✅ Complete |
| **Build Artifacts** | Production Ready | **Clean `.jar` & `dist/` bundle** | ✅ Complete |
| **Email Service Resilience**| Zero Login Blocking | **Verified via Mockito & Live SMTP** | ✅ Complete |

---

## 6. Cloud Deployment Architecture

- **Backend Web Service**: Containerized Spring Boot image with health probes (`/actuator/health`).
- **Frontend Web Application**: High-performance static SPA hosted on CDN with client-side routing rewrites (`/* -> /index.html`).
- **Managed Database**: MySQL 8.0 instance with SSL, auto-reconnect, and persistent data volume mounting.
- **Live Deployment Configs**: `render.yaml`, `deploy/docker-compose.prod.yml`, and `frontend/vercel.json`.

---

## 7. Future Scope

1. **Blockchain Audit Trail**: Implement Hyperledger-based immutable logs for cross-enterprise supplier compliance certificates.
2. **IoT Real-Time Telemetry**: Ingest real-time shipment GPS and cold-chain temperature telemetry for instant in-transit SLA scoring.
3. **Multi-Tenant SaaS Expansion**: Add tenant isolation for enterprise vendor marketplaces with customizable branding.

---

## 🎤 8. Review Viva Voce (Anticipated Questions & Answers)

**Q1: How does your system ensure login is not blocked if Gmail SMTP is slow or down?**  
> *Answer*: The `EmailServiceImpl` executes within a non-blocking try-catch block and logs any SMTP errors safely without re-throwing exceptions that would interrupt the Spring Security authentication flow. The authentication endpoint always returns a valid JWT with `emailNotificationSent: false` to inform the client without disrupting the user session.

**Q2: How do you prevent unauthorized role escalation in the system?**  
> *Answer*: We enforce RBAC using Spring Security method-level annotations (`@PreAuthorize("hasRole('ADMIN')")`), JWT claim verification in `AuthTokenFilter`, and role validation in `AuthServiceImpl` preventing direct registration of privileged accounts.

**Q3: How are weighted ratings computed across different supplier categories?**  
> *Answer*: Each evaluation criteria belongs to a weighted category. The rating service multiplies each criterion score by its category weight, normalizes the total to a 100-point scale, and assigns dynamic letter grades (A: 90-100, B: 80-89, C: 70-79, D: <70).
