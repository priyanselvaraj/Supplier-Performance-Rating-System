# Supplier Performance Rating System (SPRS) — Project Demonstration & Viva Guide

This step-by-step walkthrough is designed for project evaluators, viva examiners, and recruiters reviewing the **Supplier Performance Rating System**.

---

## 🎯 Demo Overview

| Property | Details |
| :--- | :--- |
| **Project Title** | Supplier Performance Rating System (SPRS) |
| **Architecture** | 3-Tier Enterprise Stack (React 18 + Spring Boot 3.3.3 + MySQL 8.0) |
| **Security** | Spring Security 6 + Stateless JWT + Role-Based Access Control (RBAC) |
| **Demo Accounts** | **Admin**: `admin / Admin@12345` \| **Manager**: `manager / Manager@12345` |

---

## 🚀 Step-by-Step Demonstration Flow (10 Key Steps)

### Step 1: Launch Application & Examine Container Stack
1. Start all containers via Docker Compose:
   ```bash
   docker compose up --build -d
   ```
2. Verify all services are healthy:
   ```bash
   docker compose ps
   ```
3. Open the web portal in browser: `http://localhost:5173`.

---

### Step 2: Authentication & RBAC Login
1. On the Login screen, demonstrate the **Quick Demo Login** button for `Admin`.
2. Observe JWT token issuance and redirection to the **Executive Dashboard**.
3. Inspect `localStorage` in browser DevTools to show the secure token storage and role decoding.

---

### Step 3: Executive Dashboard & Live Visual Analytics
1. Review the real-time KPI metric cards:
   - **Total Suppliers**, **Active Suppliers**, **Total Evaluations**, **Average Score**.
2. Examine the dynamic Chart.js charts:
   - **Rating Tier Breakdown** (Doughnut chart: `EXCELLENT`, `GOOD`, `AVERAGE`, `POOR`).
   - **Top Performing Suppliers** (Bar chart with score ranking).
   - **Suppliers Needing Improvement** (Warning cards for suppliers $< 70\%$).
   - **Recent Evaluations Ledger** (Audit table with live status badges).

---

### Step 4: Category & Evaluation Criteria Configuration (Admin Only)
1. Navigate to **Criteria Management** (`/criteria`):
   - Review active criteria: Quality (30%), Delivery (25%), Pricing (25%), Service (20%) = **100% Total**.
   - Show the total weight status banner (green when 100%, warning when $< 100\%$).
   - Demonstrate the safe deletion safeguard: Attempting to delete criteria with historical scores is prevented.

---

### Step 5: Supplier Management & Attribute Search
1. Navigate to **Suppliers** (`/suppliers`):
   - Demonstrate multi-attribute search across Name, Code, Email, or Contact Person.
   - Filter suppliers by **Category** (e.g. `Electronics`) and **Status** (`ACTIVE`).
2. Click **Add Supplier**:
   - Fill in supplier details $\to$ Submit $\to$ Observe auto-generated code `SUP-0000X`.

---

### Step 6: Conduct a Live Supplier Evaluation (Scoring Engine)
1. Navigate to **Evaluations** $\to$ Click **New Evaluation** (`/evaluations/new`):
   - Select a supplier from dropdown.
   - Enter scores for each active criterion (e.g., Quality: 95/100, Delivery: 90/100, Pricing: 85/100, Service: 92/100).
   - Observe the **Live Score Calculation Preview** calculating real-time weighted percentage.
   - Enter Qualitative Audit Strengths and Areas for Improvement.
2. Click **Submit Evaluation**:
   - Backend calculates final score, assigns rating tier (`EXCELLENT`), updates supplier historical rating, and marks evaluation as `COMPLETED`.

---

### Step 7: Rating Progression Ledger & Timeline Trends
1. Navigate to **Ratings** (`/ratings`):
   - View supplier rating cards with status badges (`HIGH_PERFORMING`) and score trend indicators (`IMPROVING`).
2. Click **View Timeline** for a supplier:
   - Examine the historical score progression line chart showing performance evolution across evaluation quarters.

---

### Step 8: Multi-Format Reports & Binary File Exports
1. Navigate to **Reports** (`/reports`):
   - Switch between **Supplier Performance**, **Evaluation Scorecard**, and **Organization Summary** tabs.
2. Demonstrate **1-Click Binary Downloads**:
   - **PDF Export**: Generates styled PDF document with tables, headers, and audit stamps.
   - **Excel (.xlsx) Export**: Generates formatted workbook using Apache POI.
   - **CSV Export**: Generates raw tabular CSV for spreadsheet analysis.

---

### Step 9: User Administration & Profile Management
1. Navigate to **User Management** (`/users` as Admin):
   - Search users, toggle account activation status, assign roles (`ROLE_ADMIN`, `ROLE_MANAGER`).
2. Navigate to **Profile** (`/profile`):
   - Update contact details and demonstrate live password complexity checker.

---

### Step 10: OpenAPI / Swagger & Actuator Health Monitoring
1. Open Swagger UI at `http://localhost:8080/swagger-ui.html`:
   - Execute an authorized API call using the `Authorize` button.
2. Open Actuator Health at `http://localhost:8080/actuator/health`:
   - Show `{"status":"UP"}` confirming database connectivity and operational health.
