# Supplier Performance Rating System (SPRS) — API Overview

This document provides a summary of all REST API endpoints implemented in the **Supplier Performance Rating System**.

---

## 🔐 1. Authentication & User Management APIs

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | **Public** | Authenticates user with username/email & password. Returns JWT token. |
| `POST` | `/api/v1/auth/register` | **Public** | Registers a new user with full validation. |
| `GET` | `/api/v1/auth/me` | **Authenticated** | Returns current user profile details. |
| `PUT` | `/api/v1/auth/profile` | **Authenticated** | Updates current user full name, phone number, and department. |
| `PUT` | `/api/v1/auth/change-password`| **Authenticated** | Changes user password with old password verification. |
| `GET` | `/api/v1/users` | **ROLE_ADMIN** | Paginated and filtered search of users. |
| `GET` | `/api/v1/users/{id}` | **ROLE_ADMIN** | Retrieves user details by ID. |
| `PUT` | `/api/v1/users/{id}` | **ROLE_ADMIN** | Updates user details, assigned roles, and active status. |
| `PATCH`| `/api/v1/users/{id}/toggle-status`| **ROLE_ADMIN**| Activates or deactivates a user account. |

---

## 🏢 2. Supplier & Category Management APIs

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/suppliers` | **ADMIN, MANAGER** | Paginated suppliers with search, category, status, and sorting filters. |
| `GET` | `/api/v1/suppliers/{id}` | **ADMIN, MANAGER** | Retrieves supplier details by ID with current performance metrics. |
| `POST` | `/api/v1/suppliers` | **ROLE_ADMIN** | Creates a new supplier with auto-generated code `SUP-00001`. |
| `PUT` | `/api/v1/suppliers/{id}` | **ROLE_ADMIN** | Updates existing supplier details. |
| `PATCH`| `/api/v1/suppliers/{id}/toggle-status`| **ROLE_ADMIN**| Activates or deactivates a supplier. |
| `DELETE`| `/api/v1/suppliers/{id}` | **ROLE_ADMIN** | Safely deletes a supplier if no evaluations exist. |
| `GET` | `/api/v1/categories` | **ADMIN, MANAGER** | Retrieves all supplier categories. |
| `POST` | `/api/v1/categories` | **ROLE_ADMIN** | Creates a new supplier category. |
| `PUT` | `/api/v1/categories/{id}` | **ROLE_ADMIN** | Updates supplier category name and description. |
| `DELETE`| `/api/v1/categories/{id}` | **ROLE_ADMIN** | Safely deletes category if no suppliers are linked. |

---

## 📋 3. Evaluation Criteria APIs

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/evaluation-criteria` | **ADMIN, MANAGER** | Retrieves all criteria ordered by display order. |
| `GET` | `/api/v1/evaluation-criteria/active` | **ADMIN, MANAGER** | Retrieves active criteria configured for evaluations. |
| `POST` | `/api/v1/evaluation-criteria` | **ROLE_ADMIN** | Creates a new evaluation criterion with weight and max score. |
| `PUT` | `/api/v1/evaluation-criteria/{id}` | **ROLE_ADMIN** | Updates evaluation criterion details. |
| `PATCH`| `/api/v1/evaluation-criteria/{id}/toggle-active` | **ROLE_ADMIN** | Activates/deactivates criterion with weight warning checks. |
| `DELETE`| `/api/v1/evaluation-criteria/{id}` | **ROLE_ADMIN** | Deletes criterion if no historical evaluation scores reference it. |

---

## ⭐ 4. Supplier Evaluation & Rating Engine APIs

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/evaluations` | **ADMIN, MANAGER** | Paginated evaluations with multi-attribute filtering. |
| `GET` | `/api/v1/evaluations/my-evaluations` | **ADMIN, MANAGER** | Retrieves evaluations authored by the authenticated user. |
| `GET` | `/api/v1/evaluations/{id}` | **ADMIN, MANAGER** | Retrieves evaluation scorecard and detailed criteria scores. |
| `POST` | `/api/v1/evaluations` | **ADMIN, MANAGER** | Submits a completed evaluation (validates 100% active weights). |
| `POST` | `/api/v1/evaluations/draft` | **ADMIN, MANAGER** | Saves an in-progress evaluation as a draft. |
| `PUT` | `/api/v1/evaluations/{id}` | **ADMIN, MANAGER** | Updates draft evaluation before final submission. |
| `GET` | `/api/v1/ratings` | **ADMIN, MANAGER** | Paginated ratings ledger across all suppliers. |
| `GET` | `/api/v1/ratings/supplier/{supplierId}` | **ADMIN, MANAGER** | Retrieves full historical rating timeline for a supplier. |
| `GET` | `/api/v1/ratings/supplier/{supplierId}/latest` | **ADMIN, MANAGER** | Retrieves latest performance rating and trend. |

---

## 📊 5. Executive Dashboard & Visual Analytics APIs

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/dashboard/summary` | **ROLE_ADMIN** | Executive KPI summary (Suppliers, Active, Scores, Status counts). |
| `GET` | `/api/v1/dashboard/top-suppliers` | **ADMIN, MANAGER** | Retrieves top-performing suppliers ranked by overall score. |
| `GET` | `/api/v1/dashboard/low-suppliers` | **ADMIN, MANAGER** | Retrieves suppliers needing performance improvement. |
| `GET` | `/api/v1/dashboard/recent-evaluations`| **ADMIN, MANAGER** | Retrieves recent evaluation audit entries. |
| `GET` | `/api/v1/dashboard/ratings-distribution`| **ADMIN, MANAGER** | Returns supplier rating tier percentages. |
| `GET` | `/api/v1/dashboard/trends` | **ADMIN, MANAGER** | Performance score trends aggregated by `MONTH`, `QUARTER`, or `YEAR`. |

---

## 📑 6. Reports & Multi-Format Export APIs

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/reports/supplier/{id}` | **ADMIN, MANAGER** | Comprehensive performance report JSON for a supplier. |
| `GET` | `/api/v1/reports/supplier/{id}/export/pdf` | **ADMIN, MANAGER** | Exports supplier report as a formatted **PDF document**. |
| `GET` | `/api/v1/reports/supplier/{id}/export/excel` | **ADMIN, MANAGER** | Exports supplier report as a styled **Excel spreadsheet (.xlsx)**. |
| `GET` | `/api/v1/reports/supplier/{id}/export/csv` | **ADMIN, MANAGER** | Exports supplier report as a **CSV file**. |
| `GET` | `/api/v1/reports/evaluation/{id}/export/pdf`| **ADMIN, MANAGER** | Exports official evaluation scorecard as PDF. |
| `GET` | `/api/v1/reports/overall` | **ROLE_ADMIN** | Complete organizational executive performance summary. |
| `GET` | `/api/v1/reports/overall/export/pdf` | **ROLE_ADMIN** | Exports executive organization summary as PDF. |
| `GET` | `/api/v1/reports/overall/export/excel` | **ROLE_ADMIN** | Exports executive organization summary as Excel. |
| `GET` | `/api/v1/reports/overall/export/csv` | **ROLE_ADMIN** | Exports executive organization summary as CSV. |

---

## 🩺 7. System Health & Monitoring APIs

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/health` | **Public** | Operational status, application version, timestamp. |
| `GET` | `/actuator/health` | **Public** | Spring Boot Actuator health status (`UP`). |
| `GET` | `/actuator/info` | **Public** | Safe application build and environment metadata. |
| `GET` | `/v3/api-docs` | **Public** | OpenAPI 3 JSON schema specification. |
| `GET` | `/swagger-ui.html` | **Public** | Interactive Swagger UI API playground. |

---

## 🤖 8. AI Intelligence & Predictive Analytics APIs (Phase 11)

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/ai/dashboard` | **ADMIN, MANAGER** | Executive AI summary with risk distribution, critical alerts, and declining velocity. |
| `GET` | `/api/v1/ai/suppliers/{id}/prediction` | **ADMIN, MANAGER** | Generates statistical score forecast, trajectory velocity, and confidence score. |
| `GET` | `/api/v1/ai/suppliers/{id}/risk` | **ADMIN, MANAGER** | Computes composite risk score (0-100), risk level, and explainable risk factors. |
| `GET` | `/api/v1/ai/suppliers/{id}/trend` | **ADMIN, MANAGER** | Calculates historical score delta, percentage change, and velocity direction. |
| `GET` | `/api/v1/ai/suppliers/{id}/recommendations` | **ADMIN, MANAGER** | Prescribes prioritized criteria remediations and actionable steps. |
| `GET` | `/api/v1/ai/suppliers/{id}/alerts` | **ADMIN, MANAGER** | Retrieves early warning triggers and active alerts for supplier. |
| `GET` | `/api/v1/ai/alerts` | **ADMIN, MANAGER** | Retrieves all system-wide active AI early warning alerts. |
| `GET` | `/api/v1/ai/suppliers/{id}/insights` | **ADMIN, MANAGER** | Unified intelligence package (Prediction + Risk + Trend + Recommendations + Alerts). |

---

## 🔔 9. Notification System APIs (Phase 12)

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/notifications` | **Authenticated** | Paginated user notifications with optional unread and type filters. |
| `GET` | `/api/v1/notifications/recent` | **Authenticated** | 10 most recent notifications for header bell dropdown. |
| `GET` | `/api/v1/notifications/unread` | **Authenticated** | All unread notifications for current user. |
| `GET` | `/api/v1/notifications/count` | **Authenticated** | Live count of unread notifications for navbar badge. |
| `PUT` | `/api/v1/notifications/{id}/read` | **Authenticated** | Marks a single notification as read. |
| `PUT` | `/api/v1/notifications/read-all` | **Authenticated** | Marks all notifications as read for current user. |
| `DELETE` | `/api/v1/notifications/{id}` | **Authenticated** | Deletes a notification belonging to current user. |
| `GET` | `/api/v1/notifications/preferences` | **Authenticated** | Retrieves user channel preferences (In-App, Email). |
| `PUT` | `/api/v1/notifications/preferences` | **Authenticated** | Updates user channel preferences. |
| `GET` | `/api/v1/notifications/stream` | **Authenticated** | Subscribes to live Server-Sent Events (SSE) notification stream. |

---

## 🛠️ 10. Supplier Improvement Action APIs (Phase 12)

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/improvement-actions` | **ADMIN, MANAGER** | Paginated list of improvement actions with filtering. |
| `GET` | `/api/v1/improvement-actions/{id}` | **ADMIN, MANAGER** | Retrieves improvement action details by ID. |
| `GET` | `/api/v1/improvement-actions/supplier/{id}` | **ADMIN, MANAGER** | Retrieves all improvement actions for a specific supplier. |
| `POST` | `/api/v1/improvement-actions` | **ADMIN, MANAGER** | Creates a new corrective action plan and notifies assignee. |
| `PUT` | `/api/v1/improvement-actions/{id}` | **ADMIN, MANAGER** | Updates improvement action details and due dates. |
| `PATCH` | `/api/v1/improvement-actions/{id}/status` | **ADMIN, MANAGER** | Updates action status (`OPEN`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`). |
| `DELETE` | `/api/v1/improvement-actions/{id}` | **ADMIN** | Deletes an improvement action plan. |

---

## 📈 11. Real-Time Operational Monitoring APIs (Phase 12)

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/monitoring/summary` | **ADMIN, MANAGER** | Aggregates active suppliers, high-risk vendors, critical alerts, and live operational health. |

---

## 🌐 12. Advanced Supplier Portal APIs (Phase 13)

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/supplier-portal/dashboard` | **SUPPLIER, ADMIN, MANAGER** | Vendor dashboard KPIs, rating status, recent scorecards, and open CAP actions. |
| `GET` | `/api/v1/supplier-portal/profile` | **SUPPLIER, ADMIN, MANAGER** | Registered vendor company details & change request history. |
| `POST` | `/api/v1/supplier-portal/profile/request-update` | **SUPPLIER** | Submit vendor company profile change request (triggers workflow). |
| `GET` | `/api/v1/supplier-portal/performance` | **SUPPLIER, ADMIN, MANAGER** | Performance rating ledger, score differences, and rating audits. |
| `GET` | `/api/v1/supplier-portal/evaluations` | **SUPPLIER, ADMIN, MANAGER** | Sanitized evaluation scorecard list for the authenticated supplier. |
| `GET` | `/api/v1/supplier-portal/evaluations/{id}` | **SUPPLIER, ADMIN, MANAGER** | Detailed scorecard breakdown with criteria weights, scores, and recommendations. |
| `GET` | `/api/v1/supplier-portal/improvement-actions` | **SUPPLIER, ADMIN, MANAGER** | Corrective Action Plans (CAP) assigned to the vendor. |
| `POST` | `/api/v1/supplier-portal/improvement-actions/{id}/respond` | **SUPPLIER** | Submit vendor progress countermeasures and completion notes. |
| `GET` | `/api/v1/supplier-portal/documents` | **SUPPLIER, ADMIN, MANAGER** | Compliance certificates and document list. |
| `POST` | `/api/v1/supplier-portal/documents` | **SUPPLIER, ADMIN, MANAGER** | Upload compliance document (multipart/form-data, triggers review workflow). |
| `GET` | `/api/v1/supplier-portal/documents/{id}/download` | **SUPPLIER, ADMIN, MANAGER** | Download document binary securely with tenant validation. |
| `DELETE` | `/api/v1/supplier-portal/documents/{id}` | **SUPPLIER, ADMIN** | Delete uploaded document. |
| `GET` | `/api/v1/supplier-portal/communications` | **SUPPLIER, ADMIN, MANAGER** | Two-way message thread between buyer and supplier. |
| `POST` | `/api/v1/supplier-portal/communications` | **SUPPLIER, ADMIN, MANAGER** | Send direct communication message. |
| `GET` | `/api/v1/supplier-portal/notifications` | **SUPPLIER** | Supplier alerts, reminders, and evaluation publication events. |
| `POST` | `/api/v1/supplier-portal/admin/users` | **ROLE_ADMIN** | Provision new supplier login credentials and link to supplier entity. |
| `GET` | `/api/v1/supplier-portal/admin/profile-requests` | **ADMIN, MANAGER** | List pending supplier profile update requests. |
| `PUT` | `/api/v1/supplier-portal/admin/profile-requests/{id}` | **ADMIN, MANAGER** | Approve or reject profile change request. |
| `PUT` | `/api/v1/supplier-portal/admin/documents/{id}/review` | **ADMIN, MANAGER** | Review compliance document (Active / Rejected). |

---

## ⚡ 13. Workflow, Approval & Escalation Management APIs (Phase 14)

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/workflows` | **ADMIN, MANAGER** | Paginated workflow instances with status, type, initiator, and SLA metrics. |
| `GET` | `/api/v1/workflows/summary` | **ADMIN, MANAGER** | Executive KPI summary (Total, In Progress, Approved, Rejected, Escalated, SLA Compliance Rate). |
| `GET` | `/api/v1/workflows/{id}` | **ADMIN, MANAGER, SUPPLIER** | Get workflow details, multi-step progression timeline, and audit logs. |
| `GET` | `/api/v1/workflows/resource/{resourceType}/{resourceId}` | **Authenticated** | Retrieve workflow instance attached to a specific resource. |
| `GET` | `/api/v1/approvals/my` | **ADMIN, MANAGER** | Get pending approval tasks assigned to the user's role or user ID with SLA countdowns. |
| `GET` | `/api/v1/approvals/history` | **ADMIN, MANAGER** | Paginated history of approved or rejected tasks actioned by the user. |
| `GET` | `/api/v1/approvals/{id}` | **ADMIN, MANAGER** | Get approval task details by ID. |
| `POST` | `/api/v1/approvals/{id}/approve` | **ADMIN, MANAGER** | Approve pending task, advances to next step or executes post-approval completion hook. |
| `POST` | `/api/v1/approvals/{id}/reject` | **ADMIN, MANAGER** | Reject pending task with required reason, terminates workflow. |
| `GET` | `/api/v1/escalations` | **ROLE_ADMIN** | List all historical and active workflow escalations. |
| `POST` | `/api/v1/escalations/check-overdue` | **ROLE_ADMIN** | Trigger automated background SLA scan and escalate overdue tasks. |
| `POST` | `/api/v1/escalations/{taskId}` | **ADMIN, MANAGER** | Manually escalate a task to administrator role with reason. |
| `GET` | `/api/v1/admin/workflows/definitions` | **ROLE_ADMIN** | List all workflow definition templates and configured sequential steps. |
| `GET` | `/api/v1/admin/workflows/definitions/{id}` | **ROLE_ADMIN** | Retrieve specific workflow definition blueprint and steps. |
| `POST` | `/api/v1/admin/workflows/definitions` | **ROLE_ADMIN** | Create a new workflow definition blueprint with sequential steps. |
| `PUT` | `/api/v1/admin/workflows/definitions/{id}` | **ROLE_ADMIN** | Update workflow definition blueprint metadata and step hierarchy. |
| `PATCH` | `/api/v1/admin/workflows/definitions/{id}/toggle` | **ROLE_ADMIN** | Activate or deactivate a workflow definition blueprint. |
| `DELETE` | `/api/v1/admin/workflows/definitions/{id}` | **ROLE_ADMIN** | Delete a workflow definition template. |

---

## 📊 14. Business Intelligence & KPI Builder APIs (Phase 15)

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/bi/dashboard` | **ADMIN, MANAGER** | Executive BI Dashboard summary with top KPIs, risk distribution, trends. |
| `GET` | `/api/v1/bi/kpis` | **ADMIN, MANAGER** | Calculate and fetch all active KPI scores against configured targets. |
| `GET` | `/api/v1/bi/kpis/{id}/calculate` | **ADMIN, MANAGER** | Calculate single KPI score and status. |
| `GET` | `/api/v1/bi/trends` | **ADMIN, MANAGER** | Time-series performance and quality trends across intervals. |
| `GET` | `/api/v1/bi/compare` | **ADMIN, MANAGER** | Side-by-side radar and matrix comparison between multiple suppliers. |
| `GET` | `/api/v1/bi/benchmarks/{supplierId}` | **ADMIN, MANAGER** | Supplier benchmarking against category peer averages and top quartiles. |
| `POST` | `/api/v1/bi/reports/preview` | **ADMIN, MANAGER** | Live dynamic query aggregation preview for Custom Report Builder. |
| `GET` | `/api/v1/bi/reports` | **ADMIN, MANAGER** | List saved custom reports (public + user-owned). |
| `POST` | `/api/v1/bi/reports` | **ADMIN, MANAGER** | Save custom report configuration. |
| `GET` | `/api/v1/bi/reports/{id}` | **ADMIN, MANAGER** | Get saved custom report configuration by ID. |
| `PUT` | `/api/v1/bi/reports/{id}` | **ADMIN, MANAGER** | Update saved custom report configuration. |
| `DELETE` | `/api/v1/bi/reports/{id}` | **ADMIN, MANAGER** | Delete saved custom report configuration. |
| `GET` | `/api/v1/bi/executive` | **ADMIN, MANAGER** | Comprehensive C-suite executive dashboard analytics. |
| `GET` | `/api/v1/bi/admin/kpis` | **ROLE_ADMIN** | List all KPI definitions and thresholds. |
| `POST` | `/api/v1/bi/admin/kpis` | **ROLE_ADMIN** | Create new KPI definition with target thresholds and weights. |
| `GET` | `/api/v1/bi/admin/kpis/{id}` | **ROLE_ADMIN** | Get KPI definition details by ID. |
| `PUT` | `/api/v1/bi/admin/kpis/{id}` | **ROLE_ADMIN** | Update KPI definition, formula type, and target thresholds. |
| `PATCH` | `/api/v1/bi/admin/kpis/{id}/toggle` | **ROLE_ADMIN** | Activate or deactivate KPI definition. |
| `DELETE` | `/api/v1/bi/admin/kpis/{id}` | **ROLE_ADMIN** | Delete KPI definition. |

---

## 🌐 15. Enterprise Integrations & External APIs (Phase 16)

### External Consumer REST APIs (`/api/v1/external/**`)
| Method | Endpoint | Required Scope / Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/external/suppliers` | `SCOPE_SUPPLIER_READ` / Admin / Manager | Query supplier catalog with pagination and search. |
| `GET` | `/api/v1/external/suppliers/{id}` | `SCOPE_SUPPLIER_READ` / Admin / Manager | Fetch single supplier profile. |
| `POST` | `/api/v1/external/suppliers` | `SCOPE_SUPPLIER_WRITE` / Admin | Create and register a new supplier. |
| `GET` | `/api/v1/external/suppliers/{id}/performance` | `SCOPE_PERFORMANCE_READ` / Admin / Manager | Fetch performance ratings, risk metrics, and evaluation history. |
| `GET` | `/api/v1/external/evaluations/supplier/{supplierId}` | `SCOPE_EVALUATION_READ` / Admin / Manager | Fetch evaluation scorecards for a supplier. |
| `GET` | `/api/v1/external/reports/summary` | `SCOPE_REPORT_READ` / Admin / Manager | Retrieve executive BI summary report. |

### Integration Management Admin APIs (`/api/v1/admin/integrations/**`)
| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/admin/integrations/health` | **ROLE_ADMIN** | Overview health metrics, delivery rates, and sync stats. |
| `GET` | `/api/v1/admin/integrations/api-keys` | **ROLE_ADMIN** | List all API keys with metadata and scopes. |
| `POST` | `/api/v1/admin/integrations/api-keys` | **ROLE_ADMIN** | Generate a new API key (reveals raw plaintext secret once). |
| `GET` | `/api/v1/admin/integrations/api-keys/{id}` | **ROLE_ADMIN** | Get API key metadata. |
| `PATCH` | `/api/v1/admin/integrations/api-keys/{id}/status` | **ROLE_ADMIN** | Revoke or activate an API key. |
| `DELETE` | `/api/v1/admin/integrations/api-keys/{id}` | **ROLE_ADMIN** | Delete an API key permanently. |
| `GET` | `/api/v1/admin/integrations/webhooks` | **ROLE_ADMIN** | List all webhook subscriptions. |
| `POST` | `/api/v1/admin/integrations/webhooks` | **ROLE_ADMIN** | Create a new webhook subscription. |
| `GET` | `/api/v1/admin/integrations/webhooks/{id}` | **ROLE_ADMIN** | Get webhook subscription details. |
| `PUT` | `/api/v1/admin/integrations/webhooks/{id}` | **ROLE_ADMIN** | Update webhook subscription. |
| `PATCH` | `/api/v1/admin/integrations/webhooks/{id}/status` | **ROLE_ADMIN** | Pause or resume webhook subscription. |
| `DELETE` | `/api/v1/admin/integrations/webhooks/{id}` | **ROLE_ADMIN** | Delete webhook subscription. |
| `POST` | `/api/v1/admin/integrations/webhooks/{id}/test` | **ROLE_ADMIN** | Send immediate test ping to target URL. |
| `GET` | `/api/v1/admin/integrations/delivery-logs` | **ROLE_ADMIN** | List recent webhook delivery logs with payloads and latency. |
| `GET` | `/api/v1/admin/integrations/delivery-logs/subscription/{subId}` | **ROLE_ADMIN** | List delivery logs for a specific subscription. |
| `POST` | `/api/v1/admin/integrations/sync/suppliers` | **ROLE_ADMIN** | Execute batch supplier synchronization (UPSERT, CREATE_ONLY, UPDATE_EXISTING). |
| `GET` | `/api/v1/admin/integrations/sync/history` | **ROLE_ADMIN** | List supplier synchronization run histories. |
| `GET` | `/api/v1/admin/integrations/sync/history/{id}` | **ROLE_ADMIN** | Get detailed record of a specific synchronization run. |

---

## 🤖 16. AI Copilot, Prescriptive Intelligence & Decision Support APIs (Phase 18)

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/ai/copilot/query` | **Authenticated** | Conversational natural language queries with role-based data isolation, metric citations, and suggested actions. |
| `GET` | `/api/v1/ai/copilot/history` | **Authenticated** | Returns recent copilot query history and response citations for the authenticated user. |
| `POST` | `/api/v1/ai/copilot/feedback/{historyId}` | **Authenticated** | Logs user rating (thumbs up/down) and qualitative feedback on an AI response. |
| `POST` | `/api/v1/ai/suppliers/compare` | **ADMIN, MANAGER** | Generates side-by-side multi-supplier comparative AI analysis, strengths, weaknesses, and recommendation. |
| `GET` | `/api/v1/ai/insights/executive` | **ADMIN, MANAGER** | Generates synthesized C-suite executive briefing with portfolio KPIs, risk distributions, and strategic recommendations. |
| `GET` | `/api/v1/ai/workflow/recommendations` | **ADMIN, MANAGER** | Evaluates active approval tasks and escalations to identify operational bottlenecks and actionable suggestions. |
| `POST` | `/api/v1/ai/recommendations/decision` | **ADMIN, MANAGER** | Human-in-the-Loop decision capture (`ACCEPTED`, `DISMISSED`, `ACTION_CREATED`) with optional automated CAP creation. |
| `GET` | `/api/v1/ai/suppliers/{supplierId}/decisions` | **Authenticated** | Retrieves historical audit trail of human decisions on AI recommendations for a specific supplier. |


