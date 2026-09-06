# External API Consumer Guide

Welcome to the **SPRS External REST API** developer documentation. This guide details how third-party applications (ERPs, CRMs, procurement engines, BI tools) can securely connect to SPRS.

---

## 1. Authentication

Every request to the External APIs (`/api/v1/external/**`) must include your active API Key in one of the following HTTP headers:

### Primary Header:
```http
X-API-KEY: sprs_live_3f92a18b76c8413994e1048ba81234ef
```

### Alternative Header:
```http
Authorization: ApiKey sprs_live_3f92a18b76c8413994e1048ba81234ef
```

> **Note:** API Keys are generated in the SPRS Admin Dashboard under **Integrations & APIs** &rarr; **Generate API Key**.

---

## 2. Standard Responses & Status Codes

| Status Code | Meaning |
|---|---|
| `200 OK` | The request was successful. |
| `201 Created` | A new resource was created successfully. |
| `400 Bad Request` | Request validation error (e.g. missing required field). |
| `401 Unauthorized` | Invalid, expired, or missing API Key. |
| `403 Forbidden` | API Key lacks the required permission scope. |
| `404 Not Found` | Target resource does not exist. |
| `500 Internal Error`| An unexpected server error occurred. |

---

## 3. Endpoints Reference

### A. List Suppliers
Fetch the supplier catalog with pagination.

- **Endpoint:** `GET /api/v1/external/suppliers`
- **Required Scope:** `SUPPLIER_READ`
- **Query Parameters:**
  - `search` (string, optional): Search keyword matching name or code.
  - `page` (integer, default: 0): Page index.
  - `size` (integer, default: 20): Page size.
  - `sortBy` (string, default: `name`): Field to sort by.
  - `sortDir` (string, default: `asc`): Sort direction (`asc` or `desc`).

```bash
curl -X GET "https://api.yourdomain.com/api/v1/external/suppliers?page=0&size=10" \
  -H "X-API-KEY: sprs_live_your_api_key_here" \
  -H "Accept: application/json"
```

---

### B. Fetch Supplier Performance
Retrieve current ratings, risk levels, and aggregate KPI scores.

- **Endpoint:** `GET /api/v1/external/suppliers/{id}/performance`
- **Required Scope:** `PERFORMANCE_READ`

```bash
curl -X GET "https://api.yourdomain.com/api/v1/external/suppliers/1/performance" \
  -H "X-API-KEY: sprs_live_your_api_key_here" \
  -H "Accept: application/json"
```

#### Sample Response:
```json
{
  "supplierId": 1,
  "supplierCode": "SUP-001",
  "supplierName": "Apex Microelectronics Corp",
  "overallScore": 92.4,
  "ratingCategory": "EXCELLENT",
  "riskScore": 7.6,
  "riskLevel": "LOW",
  "evaluationCount": 6,
  "averageQualityScore": 94.0,
  "averageDeliveryScore": 91.5,
  "averageCostScore": 89.0,
  "averageServiceScore": 95.0,
  "lastEvaluatedAt": "2026-08-15T00:00:00",
  "lastUpdated": "2026-09-01T10:15:00"
}
```

---

### C. Create New Supplier
Register a new supplier into the SPRS ecosystem.

- **Endpoint:** `POST /api/v1/external/suppliers`
- **Required Scope:** `SUPPLIER_WRITE`
- **Content-Type:** `application/json`

```bash
curl -X POST "https://api.yourdomain.com/api/v1/external/suppliers" \
  -H "X-API-KEY: sprs_live_your_api_key_here" \
  -H "Content-Type: application/json" \
  -d '{
    "supplierCode": "EXT-SUP-202",
    "name": "Global Polymer Solutions",
    "contactPerson": "Elena Rostova",
    "email": "elena.r@globalpolymer.com",
    "phone": "+1-555-0199",
    "address": "450 Industrial Parkway, Detroit, MI",
    "website": "https://globalpolymer.com",
    "categoryName": "Raw Materials",
    "status": "ACTIVE"
  }'
```

---

### D. Fetch Supplier Evaluations
Retrieve evaluation history and scores for a specific supplier.

- **Endpoint:** `GET /api/v1/external/evaluations/supplier/{supplierId}`
- **Required Scope:** `EVALUATION_READ`

```bash
curl -X GET "https://api.yourdomain.com/api/v1/external/evaluations/supplier/1" \
  -H "X-API-KEY: sprs_live_your_api_key_here" \
  -H "Accept: application/json"
```

---

### E. Executive Report Summary
Retrieve aggregate metrics across all active suppliers.

- **Endpoint:** `GET /api/v1/external/reports/summary`
- **Required Scope:** `REPORT_READ`

```bash
curl -X GET "https://api.yourdomain.com/api/v1/external/reports/summary" \
  -H "X-API-KEY: sprs_live_your_api_key_here" \
  -H "Accept: application/json"
```

#### Sample Response:
```json
{
  "generatedAt": "2026-09-06T11:20:00",
  "totalSuppliers": 42,
  "activeSuppliers": 39,
  "averagePerformanceScore": 84.6,
  "excellentCount": 14,
  "goodCount": 18,
  "averageCount": 7,
  "poorCount": 3,
  "highRiskCount": 3,
  "completedEvaluationsCount": 128
}
```
