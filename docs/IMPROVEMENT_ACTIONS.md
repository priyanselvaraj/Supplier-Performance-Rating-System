# Supplier Improvement Actions & Corrective Action Plans (CAP)

## 1. Overview
The **Supplier Improvement Actions** module allows procurement managers and administrators to establish, assign, and track structured Corrective Action Plans (CAP) when suppliers exhibit performance degradation or criteria failures.

---

## 2. Improvement Action Lifecycle

```
                     +----------------------------------+
                     |  Trigger: Low Performance /      |
                     |  AI Recommendation / Quality CAP |
                     +-----------------+----------------+
                                       |
                                       v
                     +----------------------------------+
                     |  Status: OPEN                    |
                     |  - Target Supplier Assigned      |
                     |  - Due Date & Priority Set       |
                     |  - Assignee Notified via Event   |
                     +-----------------+----------------+
                                       |
                                       v
                     +----------------------------------+
                     |  Status: IN_PROGRESS             |
                     |  - On-site audit / root cause    |
                     |  - Remediation milestones logged |
                     +-----------------+----------------+
                                       |
                     +-----------------+-----------------+
                     |                                   |
                     v                                   v
+----------------------------------+   +----------------------------------+
|  Status: COMPLETED               |   |  Status: CANCELLED               |
|  - Resolution notes recorded     |   |  - Deprecated or duplicate plan  |
|  - Timestamp stamped             |   |  - Audit reason archived         |
|  - Creator notified              |   +----------------------------------+
+----------------------------------+
```

---

## 3. Data Structure

| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | `Long` | Primary Key |
| `supplier` | `Supplier` | Target vendor requiring remediation |
| `title` | `String` | Brief action title (e.g., "Quality Audit & ISO-9001 Review") |
| `description` | `String` | Root causes, requirements, and acceptance criteria |
| `priority` | `Enum` | `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` |
| `status` | `Enum` | `OPEN`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED` |
| `assignedUser` | `User` | Manager responsible for remediation |
| `createdByUser` | `User` | User who initiated the action plan |
| `dueDate` | `LocalDate` | Mandatory deadline for completion |
| `completedAt` | `LocalDateTime` | Timestamp when status transitioned to `COMPLETED` |
| `resolutionNotes` | `String` | Findings, audit results, or justification |

---

## 4. REST API Reference

All endpoints are secured for `ROLE_ADMIN` and `ROLE_MANAGER`:

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/improvement-actions` | Paginated action list with filters (`supplierId`, `status`, `priority`, `assignedUserId`). |
| `GET` | `/api/v1/improvement-actions/{id}` | Retrieve action details by ID. |
| `GET` | `/api/v1/improvement-actions/supplier/{supplierId}` | Get all improvement actions for a specific supplier. |
| `POST` | `/api/v1/improvement-actions` | Create new improvement action and notify assignee. |
| `PUT` | `/api/v1/improvement-actions/{id}` | Update action details and due dates. |
| `PATCH` | `/api/v1/improvement-actions/{id}/status` | Update lifecycle status (`OPEN` $\to$ `COMPLETED`) and resolution notes. |
| `DELETE` | `/api/v1/improvement-actions/{id}` | Delete an action (Admin only). |

---

## 5. AI Recommendations Integration

When reviewing a supplier in the **AI Intelligence Dashboard** or **Supplier Profile**, prescriptive recommendations identify deficient criteria ($< 75\%$). Authorized users can click to generate an Improvement Action directly pre-populated with:
- Suggested action title
- Remediation steps and root-cause analysis
- Recommended priority tier
