# Phase 13: Advanced Supplier Self-Service Portal & Collaboration

The **Supplier Self-Service Portal** provides a dedicated, multi-tenant workspace for external vendors and suppliers. Vendors can securely view their real-time performance ratings, inspect detailed audit evaluation scorecards, submit corrective action plan (CAP) responses, upload compliance certificates, and communicate directly with corporate buyers.

---

## 1. Core Architecture & Security Controls

### Multi-Tenant Vendor Isolation
1. **Role-Based Isolation**: Each supplier user has `ROLE_SUPPLIER` associated with their specific `Supplier` entity in the database (`User.supplier`).
2. **Context Resolution**: The backend service extracts the authenticated user's associated supplier entity automatically (`user.getSupplier()`).
3. **Scorecard Sanitization**: Internal evaluator IDs and internal remarks are excluded from the supplier-facing scorecard DTOs (`SupplierPortalEvaluationResponse`), ensuring vendors only see relevant criteria scores and qualitative feedback.
4. **Document Access Security**: All file downloads and uploads enforce path traversal checks and verify that documents belong strictly to the authenticated vendor.

---

## 2. Supplier Portal REST Endpoints

All supplier-facing endpoints are rooted under `/api/v1/supplier-portal` and protected with `@PreAuthorize("hasAnyRole('SUPPLIER', 'ADMIN', 'MANAGER')")`.

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/supplier-portal/dashboard` | Vendor dashboard KPIs, rating status, AI insights, recent scorecards |
| `GET` | `/api/v1/supplier-portal/profile` | Registered company details & change request history |
| `POST` | `/api/v1/supplier-portal/profile/request-update` | Submit profile change request for buyer review |
| `GET` | `/api/v1/supplier-portal/performance` | Current rating grade, score delta, and rating engine audit history |
| `GET` | `/api/v1/supplier-portal/evaluations` | List of completed evaluation scorecards |
| `GET` | `/api/v1/supplier-portal/evaluations/{id}` | Detailed scorecard with criteria breakdown and strengths/recommendations |
| `GET` | `/api/v1/supplier-portal/improvement-actions` | Corrective Action Plans (CAP) assigned to the vendor |
| `POST` | `/api/v1/supplier-portal/improvement-actions/{id}/respond` | Submit progress countermeasures and notes |
| `GET` | `/api/v1/supplier-portal/documents` | Uploaded compliance certificates & status |
| `POST` | `/api/v1/supplier-portal/documents` | Upload new document (multipart/form-data) |
| `GET` | `/api/v1/supplier-portal/documents/{id}/download` | Securely download document binary |
| `DELETE` | `/api/v1/supplier-portal/documents/{id}` | Delete uploaded document |
| `GET` | `/api/v1/supplier-portal/communications` | Two-way message thread with corporate buyer |
| `POST` | `/api/v1/supplier-portal/communications` | Send message/inquiry to buyer |
| `GET` | `/api/v1/supplier-portal/notifications` | Vendor alerts & reminders |

### Admin & Manager Operations

Rooted under `/api/v1/supplier-portal/admin` (`ROLE_ADMIN`, `ROLE_MANAGER`):

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/supplier-portal/admin/users` | Provision new vendor login account |
| `GET` | `/api/v1/supplier-portal/admin/profile-requests` | List pending supplier profile update requests |
| `PUT` | `/api/v1/supplier-portal/admin/profile-requests/{id}` | Approve or reject profile change request |
| `PUT` | `/api/v1/supplier-portal/admin/documents/{id}/review` | Review compliance document (Active / Rejected) |

---

## 3. Demo Credentials

| Role | Username | Password | Supplier Company |
|---|---|---|---|
| **Supplier User** | `supplier_apex` | `Supplier@12345` | Apex Microelectronics Inc. (`SUP-10001`) |
| **Supplier User** | `supplier_nexus` | `Supplier@12345` | Nexus Technologies / Vanguard |
| **Procurement Admin** | `admin` | `Admin@12345` | Corporate Administration |
| **Procurement Manager** | `manager` | `Manager@12345` | Procurement Management |

---

## 4. Frontend Modules

- **`SupplierLayout.jsx`**: Responsive layout with vendor header, verification status badge, and navigation sidebar.
- **`SupplierDashboard.jsx`**: High-level KPIs, overall rating category, active CAP actions count, and AI predictive intelligence.
- **`SupplierProfile.jsx`**: Company details, verified contacts, and change request workflow modal.
- **`SupplierPerformance.jsx`**: Score progression, rating engine audits, and SWOT analysis.
- **`SupplierEvaluations.jsx` & `SupplierEvaluationDetails.jsx`**: Sanitized scorecards, criteria weights, and recommendations.
- **`SupplierImprovementActions.jsx`**: CAP corrective actions management and progress submission.
- **`SupplierDocuments.jsx`**: Compliance file repository with upload, download, and delete.
- **`SupplierCommunications.jsx`**: Interactive messaging thread with procurement auditors.
- **`SupplierPortalAdmin.jsx`**: Backoffice review panel for managing profile changes and provisioning accounts.
