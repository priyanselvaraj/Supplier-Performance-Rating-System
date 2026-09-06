# Phase 16: Enterprise Integrations & External APIs

## Overview
Phase 16 equips the **Supplier Performance Rating System (SPRS)** with high-availability enterprise integration tools, an external REST API suite, automated bi-directional synchronization, and event-driven webhook dispatch signed with HMAC-SHA256 signatures.

---

## 1. Architecture Highlights

### A. HTTP Integration Engine (`RestClient`)
- Configured in `com.supplier.sprsystem.config.IntegrationConfig`.
- Uses Spring 6 / Spring Boot 3 `RestClient` backed by `SimpleClientHttpRequestFactory`.
- Configured with a **5-second connect timeout** and **10-second read timeout**.
- Truncates oversized delivery responses and captures network/HTTP error payloads into the database for diagnostic auditing.

### B. Secure API Key Management (`ApiKeyService`)
- Keys generated via cryptographically secure random bytes: `sprs_live_<32-hex-chars>`.
- **Zero Raw Key Storage**: Only the salted SHA-256 hash (`keyHash`) and a 14-character preview prefix (`keyPrefix`, e.g. `sprs_live_a1b2`) are stored in the database.
- The raw plaintext token is revealed **exactly once** in the creation response modal.
- Granular permission scopes enforced through Spring Security authorities (`SCOPE_<NAME>`):
  - `SUPPLIER_READ`: Read supplier profiles and catalogs.
  - `SUPPLIER_WRITE`: Register and create new suppliers.
  - `PERFORMANCE_READ`: Access rating scorecards, risk levels, and KPI metrics.
  - `EVALUATION_READ`: Access historical evaluation results.
  - `REPORT_READ`: Access aggregate executive summaries and reports.
  - `SYNC_MANAGE`: Execute supplier synchronization batches.
- Request rate-limiting metadata stored per API key.

### C. Webhook Subscription & Secure Delivery (`WebhookService`)
- Outgoing webhook HTTP POST notifications triggered on key domain events:
  - `SUPPLIER_CREATED`
  - `SUPPLIER_UPDATED`
  - `SUPPLIER_RATING_UPDATED`
  - `EVALUATION_COMPLETED`
  - `HIGH_RISK_SUPPLIER_DETECTED`
  - `IMPROVEMENT_ACTION_CREATED`
  - `WORKFLOW_COMPLETED`
  - `TEST_PING`
- **HMAC-SHA256 Signature Verification**: Every dispatch contains the following headers:
  - `Content-Type: application/json`
  - `X-SPRS-Event: <EVENT_TYPE>`
  - `X-SPRS-Timestamp: <ISO-8601-STRING>`
  - `X-SPRS-Signature: sha256=<HMAC_HEX_DIGEST>`
  - `User-Agent: SPRS-Webhook-Service/1.0`
- Complete audit logging in `WebhookDeliveryLog` recording payload, HTTP status code, latency (ms), outcome (`SUCCESS`/`FAILED`), and attempt count.

### D. Supplier Synchronization Engine (`SupplierSyncService`)
- Supports automated integration with ERPs (SAP, NetSuite, Oracle) and external CRMs.
- Three synchronization strategies:
  1. `UPSERT` (Default & Recommended): Updates existing suppliers matching `supplierCode`, inserts new suppliers.
  2. `CREATE_ONLY`: Creates new suppliers only; gracefully skips existing codes.
  3. `UPDATE_EXISTING`: Updates existing suppliers; gracefully skips unmapped codes.
- Batch summaries record created, updated, skipped, failed counts, execution duration (ms), and granular error logs.
- Automatic Category resolution and unique email collision prevention.

---

## 2. API Endpoints

### External REST APIs (`/api/v1/external/**`)
| Method | Endpoint | Required Scope / Role | Description |
|---|---|---|---|
| `GET` | `/api/v1/external/suppliers` | `SCOPE_SUPPLIER_READ` / Admin / Manager | Query supplier catalog with pagination |
| `GET` | `/api/v1/external/suppliers/{id}` | `SCOPE_SUPPLIER_READ` / Admin / Manager | Fetch single supplier profile |
| `POST` | `/api/v1/external/suppliers` | `SCOPE_SUPPLIER_WRITE` / Admin | Create a new supplier |
| `GET` | `/api/v1/external/suppliers/{id}/performance` | `SCOPE_PERFORMANCE_READ` / Admin / Manager | Fetch supplier performance, ratings, and risk metrics |
| `GET` | `/api/v1/external/evaluations/supplier/{supplierId}` | `SCOPE_EVALUATION_READ` / Admin / Manager | Fetch evaluation scorecards for a supplier |
| `GET` | `/api/v1/external/reports/summary` | `SCOPE_REPORT_READ` / Admin / Manager | Retrieve executive BI summary report |

### Admin Integration Management (`/api/v1/admin/integrations/**`)
| Method | Endpoint | Required Role | Description |
|---|---|---|---|
| `GET` | `/api/v1/admin/integrations/health` | `ROLE_ADMIN` | Get overall integration metrics and health summary |
| `GET` | `/api/v1/admin/integrations/api-keys` | `ROLE_ADMIN` | List all API keys |
| `POST` | `/api/v1/admin/integrations/api-keys` | `ROLE_ADMIN` | Generate a new API key (reveals raw secret once) |
| `PATCH` | `/api/v1/admin/integrations/api-keys/{id}/status` | `ROLE_ADMIN` | Revoke or activate an API key |
| `DELETE` | `/api/v1/admin/integrations/api-keys/{id}` | `ROLE_ADMIN` | Delete an API key |
| `GET` | `/api/v1/admin/integrations/webhooks` | `ROLE_ADMIN` | List all webhook subscriptions |
| `POST` | `/api/v1/admin/integrations/webhooks` | `ROLE_ADMIN` | Create a webhook subscription |
| `PUT` | `/api/v1/admin/integrations/webhooks/{id}` | `ROLE_ADMIN` | Update target URL or events |
| `PATCH` | `/api/v1/admin/integrations/webhooks/{id}/status` | `ROLE_ADMIN` | Pause or resume webhook subscription |
| `DELETE` | `/api/v1/admin/integrations/webhooks/{id}` | `ROLE_ADMIN` | Delete webhook subscription |
| `POST` | `/api/v1/admin/integrations/webhooks/{id}/test` | `ROLE_ADMIN` | Dispatch immediate test ping |
| `GET` | `/api/v1/admin/integrations/delivery-logs` | `ROLE_ADMIN` | List recent webhook delivery logs |
| `POST` | `/api/v1/admin/integrations/sync/suppliers` | `ROLE_ADMIN` | Execute batch supplier synchronization |
| `GET` | `/api/v1/admin/integrations/sync/history` | `ROLE_ADMIN` | List historical sync runs |

---

## 3. Webhook Signature Verification (Receiver Guide)

To verify that an incoming webhook was generated by SPRS and has not been tampered with, compute the HMAC-SHA256 of the raw payload using your shared secret:

### Python Sample
```python
import hmac
import hashlib

def verify_sprs_webhook(raw_payload_bytes, signature_header, shared_secret):
    expected_sig = "sha256=" + hmac.new(
        shared_secret.encode('utf-8'),
        raw_payload_bytes,
        hashlib.sha256
    ).hexdigest()
    return hmac.compare_digest(expected_sig, signature_header)
```

### Node.js Sample
```javascript
const crypto = require('crypto');

function verifySprsWebhook(rawBodyString, signatureHeader, sharedSecret) {
  const hmac = crypto.createHmac('sha256', sharedSecret);
  const digest = 'sha256=' + hmac.update(rawBodyString).digest('hex');
  return crypto.timingSafeEqual(Buffer.from(digest), Buffer.from(signatureHeader));
}
```
