# Phase 14: Workflow, Approval and Escalation Management

The **Workflow, Approval, and Escalation Management Module** introduces enterprise-grade governance, sequential multi-level approval hierarchies, automated Service Level Agreement (SLA) monitoring, and threshold-based escalation dispatching into the Supplier Performance Rating System (SPRS).

---

## 1. Core Architecture & Workflow Lifecycle

### Workflow Lifecycle State Machine
Workflows move through deterministic states:
- `IN_PROGRESS`: Currently awaiting approval on one of its sequential steps.
- `APPROVED`: All required sequential steps have been approved by authorized approvers.
- `REJECTED`: Any approver has rejected the request, terminating the workflow and logging the rationale.
- `ESCALATED`: A task has breached its SLA threshold and has been escalated to higher managerial oversight (`ROLE_ADMIN`).
- `CANCELLED`: Withdrawn or superseded.

### Automated SLA & Escalation Engine
- **SLA Calculation**: Each step defines `slaHours` (e.g., 24h, 48h).
- **Task Due Date**: Calculated at step activation: `dueAt = now() + slaHours`.
- **Scheduled Background Scanner**: A Spring `@Scheduled(cron = "0 */15 * * * *")` scanner queries for tasks where `status = PENDING` and `dueAt < now()`.
- **Escalation Dispatcher**:
  - Sets task status to `ESCALATED`.
  - Reassigns target authority to `ROLE_ADMIN`.
  - Creates a permanent immutable record in `WorkflowEscalation`.
  - Broadcasts immediate high-priority notifications with `NotificationType.ESCALATION` to all corporate administrators.

---

## 2. Supported Workflow Types & Automated Hooks

| Workflow Type | Description | Trigger Event | Post-Approval Hook Execution |
|---|---|---|---|
| `SUPPLIER_PROFILE_UPDATE` | Multi-tier approval for vendor company profile changes | Vendor submits profile update request via Supplier Portal | Automatically updates `Supplier` entity fields (`name`, `contactPerson`, `email`, `phone`) and marks update request `APPROVED` |
| `SUPPLIER_DOCUMENT_REVIEW` | Review & compliance check for supplier uploaded documents | Vendor uploads a compliance certificate / document | Sets `SupplierDocument` status to `ACTIVE` |
| `EVALUATION_APPROVAL` | Managerial and administrative sign-off on supplier evaluations | Evaluator submits completed evaluation | Changes evaluation status to `APPROVED` and publishes scores to supplier portal |
| `IMPROVEMENT_ACTION_CLOSURE` | Verification and sign-off on vendor CAP corrective actions | Vendor submits countermeasure response | Changes action item status to `CLOSED` and notifies vendor |
| `SUPPLIER_STATUS_CHANGE` | Approval for supplier onboarding, suspension, or blacklisting | Buyer requests supplier status change | Applies new status to `Supplier` entity |

---

## 3. Workflow REST API Reference

### Workflow Instance Endpoints (`/api/v1/workflows`)

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/v1/workflows` | `ADMIN`, `MANAGER` | Search, filter, and paginate workflow instances with SLA metrics |
| `GET` | `/api/v1/workflows/summary` | `ADMIN`, `MANAGER` | KPI summary statistics (counts, compliance rate, avg approval time) |
| `GET` | `/api/v1/workflows/{id}` | `ADMIN`, `MANAGER`, `SUPPLIER` (own) | Get workflow instance details, step timeline, and audit history |
| `GET` | `/api/v1/workflows/resource/{resourceType}/{resourceId}` | Authenticated | Retrieve workflow instance attached to a specific resource |

### Approvals & Task Inbox (`/api/v1/approvals`)

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/v1/approvals/my` | `ADMIN`, `MANAGER` | Get pending approval tasks assigned to the user's role or user ID |
| `GET` | `/api/v1/approvals/history` | `ADMIN`, `MANAGER` | Paginated history of approved/rejected tasks by current user |
| `GET` | `/api/v1/approvals/{id}` | `ADMIN`, `MANAGER` | Get task details by ID |
| `POST` | `/api/v1/approvals/{id}/approve` | `ADMIN`, `MANAGER` | Approve pending task, advances to next step or completes workflow |
| `POST` | `/api/v1/approvals/{id}/reject` | `ADMIN`, `MANAGER` | Reject pending task with required reason, terminates workflow |

### Escalations Management (`/api/v1/escalations`)

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/v1/escalations` | `ADMIN` | List all historical and active workflow escalations |
| `POST` | `/api/v1/escalations/check-overdue` | `ADMIN` | Trigger on-demand SLA scan and escalate overdue tasks |
| `POST` | `/api/v1/escalations/{taskId}` | `ADMIN`, `MANAGER` | Manually escalate a task to administrator role |

### Workflow Configuration & Admin (`/api/v1/admin/workflows`)

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/v1/admin/workflows/definitions` | `ADMIN` | List all configured workflow definition templates and step hierarchies |
| `GET` | `/api/v1/admin/workflows/definitions/{id}` | `ADMIN` | Get specific workflow definition with steps |
| `POST` | `/api/v1/admin/workflows/definitions` | `ADMIN` | Create a new workflow definition with sequential steps |
| `PUT` | `/api/v1/admin/workflows/definitions/{id}` | `ADMIN` | Update an existing workflow definition and steps |
| `PATCH` | `/api/v1/admin/workflows/definitions/{id}/toggle` | `ADMIN` | Enable or disable workflow definition |
| `DELETE` | `/api/v1/admin/workflows/definitions/{id}` | `ADMIN` | Delete workflow definition |

---

## 4. Frontend User Interfaces

1. **Workflow Dashboard (`/workflows`)**:
   - High-level KPIs: Total Active Workflows, In Progress, Approved, Rejected, Escalations, SLA Compliance Rate, Average Approval Time.
   - Comprehensive filter bar: Status, Workflow Type, Keyword search, Date range, Sorting.
   - Live workflow table with SLA remaining badges, step progress indicators, and direct navigation.
   - Manual "Scan SLA Overdue" button with instant feedback toast.

2. **My Approvals Inbox (`/my-approvals`)**:
   - Tabbed view: **Pending Approvals** & **Approval History**.
   - Pending task cards highlighting step name, required role, SLA countdown timer, and workflow context.
   - Direct modal dialogs for **Approve** (with optional comments) and **Reject** (with mandatory reason).

3. **Workflow Details & Audit Timeline (`/workflows/:id`)**:
   - Visual step timeline displaying previous, current active, and future steps with user, timestamp, and status.
   - Progress bar with percentage completion.
   - Related business resource snapshot (Supplier, Document, Evaluation).
   - Immutable audit trail listing all status changes, approvals, rejections, and escalations.

4. **Workflow Configuration Blueprint (`/admin/workflows`)**:
   - Administrative blueprint designer for managing sequential multi-step approval workflows.
   - Step builder allowing dynamic addition, reordering, role assignment (`ROLE_MANAGER`, `ROLE_ADMIN`), SLA hours, and specific reviewer instructions.
   - Active/Inactive toggle switches and deletion safeguards.

---

## 5. Security & RBAC Matrix

| Role | View Workflows | Approve/Reject Assigned Tasks | Reconfigure Workflow Blueprints | Trigger Manual Escalation |
|---|---|---|---|---|
| `ROLE_ADMIN` | All | Yes (all admin tasks & escalated tasks) | Yes | Yes |
| `ROLE_MANAGER` | All | Yes (manager tasks) | No | Yes |
| `ROLE_SUPPLIER` | Own requests only | No | No | No |
