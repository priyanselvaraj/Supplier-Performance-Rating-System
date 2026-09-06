# Notification System & Real-Time Monitoring Guide

## 1. Overview
The **Notification System** in the Supplier Performance Rating System (SPRS) provides persistent, multi-channel, role-aware event logging and real-time Server-Sent Events (SSE) notification delivery. It tracks operational events, early warning alerts, evaluation completions, rating shifts, and supplier corrective action assignments.

---

## 2. Notification Architecture

```
                                  +-----------------------------+
                                  |  System Event Triggers      |
                                  |  - Evaluation Completed     |
                                  |  - Low Score / Sharp Drop   |
                                  |  - AI Early Warning Alert   |
                                  |  - Action Item Assigned     |
                                  +--------------+--------------+
                                                 |
                                                 v
                                  +-----------------------------+
                                  | NotificationService         |
                                  | - User Preference Filter    |
                                  | - Duplicate Prevention (6h) |
                                  +--------------+--------------+
                                                 |
                        +------------------------+------------------------+
                        |                                                 |
                        v                                                 v
         +-----------------------------+                   +-----------------------------+
         | Database Persistence        |                   | Real-Time SSE Stream        |
         | Table: notifications        |                   | SseEmitter (HTTP/2 Event)   |
         | User Isolation & Unread Flag|                   | Active User Session Stream  |
         +-----------------------------+                   +--------------+--------------+
                        |                                                 |
                        +------------------------+------------------------+
                                                 |
                                                 v
                                  +-----------------------------+
                                  | React Client UI             |
                                  | - Navbar Notification Bell  |
                                  | - Unread Count Badge (Live) |
                                  | - Dropdown Activity Popup   |
                                  | - /notifications Center     |
                                  +-----------------------------+
```

---

## 3. Notification Types & Priority Levels

### Notification Types
| Type | Description | Trigger Example |
| :--- | :--- | :--- |
| `ALERT` | High-priority early warning | Supplier score falls below 50% or sharp decline detected |
| `EVALUATION` | Audit & evaluation lifecycle | New evaluation finalized or submitted |
| `RATING` | Rating tier changes | Vendor upgraded to `EXCELLENT` or downgraded to `POOR` |
| `IMPROVEMENT_ACTION` | Corrective action plan task | User assigned to audit or remediation plan |
| `AI_INSIGHT` | Predictive intelligence insight | Significant negative velocity trajectory forecasted |
| `SYSTEM` | General administrative message | User role updated or system maintenance notice |

### Priority Levels
| Priority | Color | Badge | Description |
| :--- | :--- | :--- | :--- |
| `CRITICAL` | Red (`#ef4444`) | `CRITICAL` | Immediate intervention required (e.g. critical safety/quality breach) |
| `HIGH` | Orange (`#f97316`) | `HIGH` | Elevated risk or urgent deadline approaching |
| `MEDIUM` | Blue (`#3b82f6`) | `MEDIUM` | Standard operational events and routine evaluations |
| `LOW` | Slate (`#10b981`) | `LOW` | Informational updates and benign activity logs |

---

## 4. Real-Time Delivery Technology: Server-Sent Events (SSE)

### Why Server-Sent Events (SSE) over WebSocket?
1. **Unidirectional Efficiency**: Notifications are server-to-client dispatches. SSE avoids bidirectional socket framing overhead.
2. **Native HTTP/2 & Proxy Compatibility**: Works effortlessly with standard HTTP load balancers and reverse proxies without complex WebSocket upgrade handshakes.
3. **Automatic Reconnection**: Built-in client retry mechanisms ensure seamless recovery during network switches.
4. **Spring Web Integration**: Supported directly via Spring MVC `SseEmitter` with thread-safe user session mapping.

### SSE Stream Endpoint
- **URL**: `GET /api/v1/notifications/stream`
- **Headers**: `Accept: text/event-stream`, `Authorization: Bearer <token>`
- **Events**:
  - `INIT`: Dispatched upon initial stream handshake.
  - `NOTIFICATION`: Live payload containing `NotificationResponse` JSON.

---

## 5. User Notification Preferences

Users can customize notification channels for each `NotificationType`:
- **In-App Enabled**: Determines whether notifications appear in the Bell dropdown and Notification Center.
- **Email Enabled**: Controls external email dispatch preferences.

### REST Endpoints
- `GET /api/v1/notifications/preferences`: Retrieve user channel settings.
- `PUT /api/v1/notifications/preferences`: Update preferences payload array.

---

## 6. Duplicate Prevention & Security

1. **Duplicate Suppression**: Before creating a notification, the service queries `existsByUserIdAndTitleAndRelatedResourceTypeAndRelatedResourceIdAndCreatedAtAfter` (past 6 hours) to prevent alert flooding.
2. **User Isolation**: All notification queries are partitioned by authenticated user ID (`user.id = :userId`). Attempting to mark or delete another user's notification returns a `400 Bad Request`.
3. **Authentication**: All notification REST and SSE endpoints require valid JWT authentication (`isAuthenticated()`).
