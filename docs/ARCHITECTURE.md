# Supplier Performance Rating System (SPRS) — Architecture Documentation

This document describes the architectural design, layered patterns, security model, and data flow of the **Supplier Performance Rating System (SPRS)**.

---

## 1. High-Level System Architecture

The application adopts a **Modern 3-Tier Client-Server Architecture** designed for high modularity, horizontal scalability, and strict separation of concerns.

```mermaid
graph TD
    Client["Client Browser (React 18 SPA)"]
    Nginx["Nginx Web Server / Reverse Proxy (:80)"]
    SpringBoot["Spring Boot 3.3.3 REST API (:8080)"]
    MySQL[("MySQL 8.0 Database (:3306)")]
    Actuator["Spring Boot Actuator & Health (:8080/actuator)"]

    Client -->|HTTP / SPA Routing| Nginx
    Nginx -->|Reverse Proxy /api/| SpringBoot
    Nginx -->|Proxy /actuator/| Actuator
    SpringBoot -->|HikariCP Connection Pool / JPA| MySQL
```

---

## 2. Frontend Layered Architecture (React 18 + Vite)

The frontend is constructed as a responsive Single Page Application (SPA) structured around modular domain components:

```mermaid
graph TD
    subgraph UI_Layer ["Presentation & UI Layer"]
        Pages["Pages (Dashboard, Suppliers, Evaluations, Ratings, Reports, Users)"]
        Components["Shared Components (Table, Modal, Badge, Button, Input, Card, Toast)"]
        Layout["Layout (Navbar, Sidebar, DashboardLayout)"]
    end

    subgraph State_Layer ["State & Authentication Layer"]
        AuthContext["AuthContext (JWT Token, User Session, Roles)"]
        ProtectedRoute["ProtectedRoute Guard (RBAC Router Guard)"]
    end

    subgraph Service_Layer ["API Service Client Layer"]
        AxiosInstance["Centralized Axios Client (Base URL, JWT Interceptor, 401 Handler)"]
        Services["Domain Services (auth, supplier, evaluation, rating, report, dashboard)"]
    end

    Pages --> Components
    Pages --> Layout
    Pages --> AuthContext
    ProtectedRoute --> AuthContext
    Pages --> Services
    Services --> AxiosInstance
```

---

## 3. Backend Layered Architecture (Spring Boot 3.3.3)

The backend strictly enforces the **Controller-Service-Repository Pattern** with stateless JWT authentication:

```mermaid
graph LR
    subgraph Security_Filter ["Security Filter Chain"]
        ReqFilter["RequestCorrelationFilter (X-Request-ID)"]
        CorsFilter["CorsFilter"]
        JwtFilter["AuthTokenFilter (Bearer JWT Validation)"]
    end

    subgraph Controller_Layer ["REST Controllers"]
        Controllers["Controllers (@RestController, @RequestMapping, @Valid)"]
    end

    subgraph Service_Layer ["Business Services"]
        Services["Service Interfaces & Implementations (@Service, @Transactional)"]
        ScoringEngine["Performance Rating Engine"]
        ExportEngine["OpenPDF / Apache POI Export Engine"]
    end

    subgraph Data_Layer ["Persistence Layer"]
        Repos["Spring Data JPA Repositories (@Repository)"]
        Entities["JPA Entities (@Entity, @Table, Indexes)"]
    end

    ReqFilter --> CorsFilter
    CorsFilter --> JwtFilter
    JwtFilter --> Controllers
    Controllers --> Services
    Services --> ScoringEngine
    Services --> ExportEngine
    Services --> Repos
    Repos --> Entities
```

---

## 4. Authentication & JWT Token Flow

```mermaid
sequenceDiagram
    autonumber
    actor User as User / Client
    participant Frontend as React Frontend
    participant Security as Security Filter Chain
    participant Controller as AuthController
    participant AuthManager as AuthenticationManager
    participant JwtUtils as JwtUtils

    User->>Frontend: Enters Username & Password
    Frontend->>Controller: POST /api/v1/auth/login
    Controller->>AuthManager: authenticate(UsernamePasswordAuthenticationToken)
    AuthManager->>AuthManager: Verify BCrypt Hashed Password
    AuthManager-->>Controller: Authentication Object (UserDetailsImpl)
    Controller->>JwtUtils: generateJwtToken(Authentication)
    JwtUtils-->>Controller: Signed HMAC-SHA256 JWT
    Controller-->>Frontend: 200 OK { token, username, roles }
    Frontend->>Frontend: Save Token in localStorage
    Frontend->>Security: GET /api/v1/suppliers (Header: Authorization: Bearer <token>)
    Security->>JwtUtils: validateJwtToken(token)
    Security->>Security: Set SecurityContextHolder Authentication
    Security-->>Frontend: 200 OK (Protected Data)
```

---

## 5. Evaluation & Performance Scoring Flow

```mermaid
sequenceDiagram
    autonumber
    actor Manager as Procurement Manager
    participant UI as EvaluationForm (React)
    participant API as EvaluationController
    participant EvalService as SupplierEvaluationService
    participant RatingEngine as PerformanceRatingEngine
    participant Database as MySQL Database

    Manager->>UI: Selects Supplier & Enters Criterion Scores
    UI->>UI: Live Preview: Weighted Score = Sum(Score / Max * Weight)
    Manager->>UI: Submits Form
    UI->>API: POST /api/v1/evaluations
    API->>EvalService: createEvaluation(EvaluationRequest)
    EvalService->>EvalService: Verify Active Criteria Sum == 100%
    EvalService->>Database: Save SupplierEvaluation & EvaluationScores
    EvalService->>RatingEngine: recalculateSupplierRating(supplierId)
    RatingEngine->>RatingEngine: Calculate Aggregate Weighted Average & Status
    RatingEngine->>Database: Insert SupplierPerformanceRating History
    RatingEngine->>Database: Update Supplier overallRating & ratingCategory
    EvalService-->>UI: 201 Created { evaluationCode, totalWeightedScore, status: COMPLETED }
    UI-->>Manager: Displays Confirmation & Audit Scorecard
```

---

## 6. Database Schema & Indexing Strategy

Indexes are applied on frequently filtered and joined columns to optimize query execution plans:

| Table | Index Name | Indexed Columns | Justification |
| :--- | :--- | :--- | :--- |
| `suppliers` | `idx_suppliers_category_id` | `category_id` | Foreign key joins during category filtering |
| `suppliers` | `idx_suppliers_status` | `status` | Filter by `ACTIVE`, `INACTIVE` |
| `suppliers` | `idx_suppliers_rating_cat` | `rating_category` | High/Low performance categorization queries |
| `suppliers` | `idx_suppliers_name` | `name` | Case-insensitive multi-attribute text search |
| `supplier_evaluations` | `idx_eval_supplier_id` | `supplier_id` | History lookup by supplier |
| `supplier_evaluations` | `idx_eval_evaluator_id` | `evaluator_id` | `getMyEvaluations()` audit lookup |
| `supplier_evaluations` | `idx_eval_date` | `evaluation_date` | Date-range filtering and analytics sorting |
| `supplier_evaluations` | `idx_eval_status` | `status` | Draft vs Completed filtering |
| `supplier_performance_ratings` | `idx_spr_supplier_id` | `supplier_id` | Rating progression timeline queries |
| `supplier_performance_ratings` | `idx_spr_rating_date` | `rating_date` | Monthly/Quarterly/Annual trend aggregation |
| `evaluation_scores` | `idx_eval_score_eval_id`| `evaluation_id` | Scorecard item retrieval |
| `users` | `idx_users_active` | `active` | User status queries |
