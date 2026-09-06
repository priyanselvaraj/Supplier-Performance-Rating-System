# API Contract Specification

Base URL: `/api/v1`  
Authentication: `Authorization: Bearer <JWT_TOKEN>`  
Swagger UI Interactive Documentation: `http://localhost:8080/swagger-ui/index.html`  
OpenAPI JSON Spec: `http://localhost:8080/v3/api-docs`

---

## 1. Authentication Endpoints

### 1.1 Authenticate User (Login)
- **Method / URL**: `POST /api/v1/auth/login`
- **Access**: Public
- **Request Body**:
```json
{
  "username": "admin",
  "password": "Admin@12345"
}
```
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "admin",
    "email": "admin@sprsystem.com",
    "fullName": "System Administrator",
    "role": "ADMIN",
    "roles": ["ROLE_ADMIN"]
  }
}
```

### 1.2 Register New User
- **Method / URL**: `POST /api/v1/auth/register`
- **Access**: Public
- **Request Body**:
```json
{
  "username": "jdoe",
  "email": "jdoe@company.com",
  "password": "Password@12345",
  "confirmPassword": "Password@12345",
  "fullName": "John Doe",
  "phone": "+1 555-0100",
  "department": "Procurement",
  "role": "MANAGER"
}
```

### 1.3 Get Current User Profile
- **Method / URL**: `GET /api/v1/auth/me`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`

---

## 2. Supplier Category Endpoints

- `GET /api/v1/categories`: Get all categories (`ROLE_ADMIN`, `ROLE_MANAGER`)
- `GET /api/v1/categories/{id}`: Get category by ID (`ROLE_ADMIN`, `ROLE_MANAGER`)
- `POST /api/v1/categories`: Create category (`ROLE_ADMIN` only)
- `PUT /api/v1/categories/{id}`: Update category (`ROLE_ADMIN` only)
- `DELETE /api/v1/categories/{id}`: Safe delete category (`ROLE_ADMIN` only)
- `PATCH /api/v1/categories/{id}/activate`: Activate category (`ROLE_ADMIN` only)
- `PATCH /api/v1/categories/{id}/deactivate`: Deactivate category (`ROLE_ADMIN` only)

---

## 3. Supplier Management Endpoints

- `GET /api/v1/suppliers?page=0&size=10&sortBy=supplierName&direction=asc`: Paginated & sorted suppliers (`ROLE_ADMIN`, `ROLE_MANAGER`)
- `GET /api/v1/suppliers/search?keyword=electronics`: Search across name, code, email, contact person (`ROLE_ADMIN`, `ROLE_MANAGER`)
- `GET /api/v1/suppliers/filter?categoryId=1&active=true&city=San%20Jose`: Filter suppliers (`ROLE_ADMIN`, `ROLE_MANAGER`)
- `GET /api/v1/suppliers/{id}`: Get by ID (`ROLE_ADMIN`, `ROLE_MANAGER`)
- `GET /api/v1/suppliers/code/{code}`: Get by code (`ROLE_ADMIN`, `ROLE_MANAGER`)
- `POST /api/v1/suppliers`: Create supplier (`ROLE_ADMIN` only)
- `PUT /api/v1/suppliers/{id}`: Update supplier (`ROLE_ADMIN` only)
- `PATCH /api/v1/suppliers/{id}/activate`: Activate supplier (`ROLE_ADMIN` only)
- `PATCH /api/v1/suppliers/{id}/deactivate`: Deactivate supplier (`ROLE_ADMIN` only)
- `PATCH /api/v1/suppliers/{id}/status?status=ACTIVE`: Update supplier status (`ROLE_ADMIN` only)
- `DELETE /api/v1/suppliers/{id}`: Delete supplier (`ROLE_ADMIN` only)

---

## 4. Evaluation Criteria Endpoints

Base URL: `/api/v1/evaluation-criteria` (also mapped at `/api/v1/criteria`)

### 4.1 Get All Criteria
- **Method / URL**: `GET /api/v1/evaluation-criteria`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`

### 4.2 Get Active Criteria
- **Method / URL**: `GET /api/v1/evaluation-criteria/active`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`

### 4.3 Get Criteria By ID
- **Method / URL**: `GET /api/v1/evaluation-criteria/{id}`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`

### 4.4 Get Total Active Weights
- **Method / URL**: `GET /api/v1/evaluation-criteria/total-weight`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`

### 4.5 Create Criteria
- **Method / URL**: `POST /api/v1/evaluation-criteria`
- **Access**: `ROLE_ADMIN` only
- **Request Body**:
```json
{
  "name": "Sustainability & ESG",
  "code": "CRIT-ESG",
  "description": "Environmental impact, green certifications, ethical labor",
  "weight": 10.0,
  "maxScore": 100.0,
  "displayOrder": 6,
  "active": true
}
```

### 4.6 Update Criteria
- **Method / URL**: `PUT /api/v1/evaluation-criteria/{id}`
- **Access**: `ROLE_ADMIN` only

### 4.7 Activate / Deactivate Criteria
- **Method / URL**: `PATCH /api/v1/evaluation-criteria/{id}/activate` & `PATCH /api/v1/evaluation-criteria/{id}/deactivate`
- **Access**: `ROLE_ADMIN` only

### 4.8 Safe Delete Criteria
- **Method / URL**: `DELETE /api/v1/evaluation-criteria/{id}`
- **Access**: `ROLE_ADMIN` only (Rejects if historical scores exist)

---

## 5. Supplier Evaluation Endpoints

Base URL: `/api/v1/evaluations`

### 5.1 Create & Submit Evaluation
- **Method / URL**: `POST /api/v1/evaluations`
- **Access**: `ROLE_MANAGER`, `ROLE_ADMIN`
- **Request Body**:
```json
{
  "supplierId": 1,
  "evaluationDate": "2026-08-31",
  "evaluationPeriod": "Q3 2026",
  "generalComments": "Excellent chip quality and delivery accuracy.",
  "strengths": "Defect rate below 0.05%, automated invoice reconciliation.",
  "areasForImprovement": "Slight lag in customer support escalation.",
  "recommendation": "Maintain tier-1 supplier status with volume expansion.",
  "scores": [
    { "criteriaId": 1, "scoreObtained": 95.0, "remarks": "99.8% conformance" },
    { "criteriaId": 2, "scoreObtained": 90.0, "remarks": "Delivered 29 of 30 on time" },
    { "criteriaId": 3, "scoreObtained": 85.0, "remarks": "Competitive volume tiers" },
    { "criteriaId": 4, "scoreObtained": 90.0, "remarks": "Prompt account manager" },
    { "criteriaId": 5, "scoreObtained": 95.0, "remarks": "ISO 9001 and RoHS compliant" }
  ]
}
```
- **Response (201 Created)**:
```json
{
  "success": true,
  "message": "Evaluation submitted successfully",
  "data": {
    "id": 1,
    "evaluationCode": "EV-202608-4821",
    "supplierId": 1,
    "supplierCode": "SUP-00001",
    "supplierName": "Apex Chipsets Ltd",
    "supplierCategoryName": "Electronics & Hardware",
    "evaluatorId": 2,
    "evaluatorName": "Procurement Manager",
    "evaluationDate": "2026-08-31",
    "evaluationPeriod": "Q3 2026",
    "status": "COMPLETED",
    "totalWeightedScore": 91.25,
    "ratingCategory": "EXCELLENT",
    "scores": [
      {
        "id": 1,
        "criteriaId": 1,
        "criteriaName": "Product Quality",
        "criteriaCode": "CRIT-QUAL",
        "scoreObtained": 95.0,
        "maxScore": 100.0,
        "weight": 30.0,
        "weightedScore": 28.5,
        "remarks": "99.8% conformance"
      }
    ]
  }
}
```

### 5.2 Save Draft Evaluation
- **Method / URL**: `POST /api/v1/evaluations/draft`
- **Access**: `ROLE_MANAGER`, `ROLE_ADMIN`

### 5.3 Get Evaluations (Paginated & Filterable)
- **Method / URL**: `GET /api/v1/evaluations?supplierId=1&status=COMPLETED&page=0&size=10&sortBy=evaluationDate&direction=desc`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`

### 5.4 Get My Evaluations
- **Method / URL**: `GET /api/v1/evaluations/my-evaluations?page=0&size=10`
- **Access**: `ROLE_MANAGER`, `ROLE_ADMIN`

### 5.5 Get Evaluation By ID / Code / Supplier
- `GET /api/v1/evaluations/{id}`
- `GET /api/v1/evaluations/code/{code}`
- `GET /api/v1/evaluations/supplier/{supplierId}`

### 5.6 Update Draft Evaluation
- **Method / URL**: `PUT /api/v1/evaluations/{id}`
- **Access**: Evaluator owner or `ROLE_ADMIN` (Only when in `DRAFT` status)

### 5.7 Submit Draft Evaluation
- **Method / URL**: `PATCH /api/v1/evaluations/{id}/submit`
- **Access**: Evaluator owner or `ROLE_ADMIN` (Validates active criteria sum equals 100%)

### 5.8 Complete Evaluation
- **Method / URL**: `PATCH /api/v1/evaluations/{id}/complete`
- **Access**: Evaluator owner or `ROLE_ADMIN`

### 5.9 Cancel Evaluation
- **Method / URL**: `PATCH /api/v1/evaluations/{id}/cancel`
- **Access**: Evaluator owner or `ROLE_ADMIN`

### 5.10 Delete Evaluation
- **Method / URL**: `DELETE /api/v1/evaluations/{id}`
- **Access**: `ROLE_ADMIN` only

---

## 6. Supplier Performance Ratings & Classification Endpoints (Phase 5)

Base URL: `/api/v1/ratings`

### 6.1 Get All Ratings (Paginated & Filtered)
- **Method / URL**: `GET /api/v1/ratings?supplierId=1&rating=EXCELLENT&performanceStatus=HIGH_PERFORMING&page=0&size=10&sortBy=ratingDate&direction=desc`
- **Access**: `ROLE_ADMIN` only
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Supplier ratings fetched successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "supplierId": 1,
        "supplierCode": "SUP-00001",
        "supplierName": "Apex Chipsets Ltd",
        "evaluationId": 1,
        "evaluationCode": "EV-202608-4821",
        "score": 92.5,
        "rating": "EXCELLENT",
        "ratingDisplayName": "Excellent",
        "performanceStatus": "HIGH_PERFORMING",
        "performanceStatusDisplayName": "High Performing",
        "ratingDate": "2026-08-31",
        "createdAt": "2026-08-31T19:40:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1,
    "last": true
  }
}
```

### 6.2 Get Rating By ID
- **Method / URL**: `GET /api/v1/ratings/{id}`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`

### 6.3 Get Latest Supplier Rating
- **Method / URL**: `GET /api/v1/ratings/supplier/{supplierId}/latest`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`

### 6.4 Get Supplier Rating History
- **Method / URL**: `GET /api/v1/ratings/supplier/{supplierId}/history`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Supplier rating history fetched successfully",
  "data": {
    "supplierId": 1,
    "supplierCode": "SUP-00001",
    "supplierName": "Apex Chipsets Ltd",
    "ratings": [
      {
        "id": 1,
        "score": 92.5,
        "rating": "EXCELLENT",
        "performanceStatus": "HIGH_PERFORMING",
        "ratingDate": "2026-08-31",
        "evaluationId": 1
      },
      {
        "id": 2,
        "score": 86.5,
        "rating": "VERY_GOOD",
        "performanceStatus": "HIGH_PERFORMING",
        "ratingDate": "2026-05-15",
        "evaluationId": 2
      }
    ]
  }
}
```

### 6.5 Get Supplier Performance Summary & Trend
- **Method / URL**: `GET /api/v1/ratings/supplier/{supplierId}/summary`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Supplier performance summary fetched successfully",
  "data": {
    "supplierId": 1,
    "supplierCode": "SUP-00001",
    "supplierName": "Apex Chipsets Ltd",
    "latestScore": 92.5,
    "latestRating": "EXCELLENT",
    "latestRatingDisplayName": "Excellent",
    "latestPerformanceStatus": "HIGH_PERFORMING",
    "latestPerformanceStatusDisplayName": "High Performing",
    "previousScore": 86.5,
    "previousRating": "VERY_GOOD",
    "previousRatingDisplayName": "Very Good",
    "scoreDifference": 6.0,
    "performanceTrend": "IMPROVING",
    "performanceTrendDisplayName": "Improving",
    "ratingDate": "2026-08-31",
    "totalEvaluations": 2
  }
}
```

### 6.6 Get High Performing Suppliers
- **Method / URL**: `GET /api/v1/ratings/high-performing`
- **Access**: `ROLE_ADMIN` only

### 6.7 Get Suppliers Needing Improvement
- **Method / URL**: `GET /api/v1/ratings/needs-improvement`
- **Access**: `ROLE_ADMIN` only

---

## 7. Dashboard & Analytics Endpoints (Phase 6)

### 7.1 Get Dashboard Summary
- **Method / URL**: `GET /api/v1/dashboard/summary`
- **Access**: `ROLE_ADMIN` only
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Dashboard summary fetched successfully",
  "data": {
    "totalSuppliers": 6,
    "activeSuppliers": 5,
    "inactiveSuppliers": 1,
    "totalEvaluations": 3,
    "draftEvaluations": 0,
    "submittedEvaluations": 0,
    "completedEvaluations": 3,
    "cancelledEvaluations": 0,
    "averagePerformanceScore": 81.33,
    "highPerformingSuppliers": 2,
    "satisfactorySuppliers": 0,
    "needsImprovementSuppliers": 1,
    "lowPerformingSuppliers": 0
  }
}
```

### 7.2 Get Supplier Statistics
- **Method / URL**: `GET /api/v1/dashboard/supplier-statistics`
- **Access**: `ROLE_ADMIN` only
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Supplier statistics fetched successfully",
  "data": {
    "totalSuppliers": 6,
    "activeSuppliers": 5,
    "inactiveSuppliers": 1,
    "suppliersByCategory": [
      {
        "categoryId": 1,
        "categoryName": "Electronics & Hardware",
        "supplierCount": 2
      },
      {
        "categoryId": 2,
        "categoryName": "Logistics & Transport",
        "supplierCount": 2
      }
    ]
  }
}
```

### 7.3 Get Evaluation Statistics
- **Method / URL**: `GET /api/v1/dashboard/evaluation-statistics`
- **Access**: `ROLE_ADMIN` only
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Evaluation statistics fetched successfully",
  "data": {
    "total": 3,
    "draft": 0,
    "submitted": 0,
    "completed": 3,
    "cancelled": 0
  }
}
```

### 7.4 Get Performance Analytics
- **Method / URL**: `GET /api/v1/dashboard/performance?startDate=2026-01-01&endDate=2026-12-31`
- **Access**: `ROLE_ADMIN` only
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Performance analytics fetched successfully",
  "data": {
    "averageScore": 81.33,
    "highestScore": 92.5,
    "lowestScore": 65.0,
    "totalRatedSuppliers": 3
  }
}
```

### 7.5 Get Rating Distribution
- **Method / URL**: `GET /api/v1/dashboard/rating-distribution`
- **Access**: `ROLE_ADMIN` only
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Rating distribution fetched successfully",
  "data": [
    {
      "rating": "EXCELLENT",
      "ratingDisplayName": "Excellent",
      "count": 1,
      "percentage": 33.33
    },
    {
      "rating": "VERY_GOOD",
      "ratingDisplayName": "Very Good",
      "count": 1,
      "percentage": 33.33
    },
    {
      "rating": "GOOD",
      "ratingDisplayName": "Good",
      "count": 0,
      "percentage": 0.0
    },
    {
      "rating": "AVERAGE",
      "ratingDisplayName": "Average",
      "count": 1,
      "percentage": 33.33
    },
    {
      "rating": "POOR",
      "ratingDisplayName": "Poor",
      "count": 0,
      "percentage": 0.0
    }
  ]
}
```

### 7.6 Get Performance Status Distribution
- **Method / URL**: `GET /api/v1/dashboard/performance-status-distribution`
- **Access**: `ROLE_ADMIN` only
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Performance status distribution fetched successfully",
  "data": [
    {
      "performanceStatus": "HIGH_PERFORMING",
      "performanceStatusDisplayName": "High Performing",
      "count": 2,
      "percentage": 66.67
    },
    {
      "performanceStatus": "SATISFACTORY",
      "performanceStatusDisplayName": "Satisfactory",
      "count": 0,
      "percentage": 0.0
    },
    {
      "performanceStatus": "NEEDS_IMPROVEMENT",
      "performanceStatusDisplayName": "Needs Improvement",
      "count": 1,
      "percentage": 33.33
    },
    {
      "performanceStatus": "LOW_PERFORMING",
      "performanceStatusDisplayName": "Low Performing",
      "count": 0,
      "percentage": 0.0
    }
  ]
}
```

### 7.7 Get Top Performing Suppliers
- **Method / URL**: `GET /api/v1/dashboard/top-suppliers?limit=5`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Top performing suppliers fetched successfully",
  "data": [
    {
      "supplierId": 1,
      "supplierCode": "SUP-00001",
      "supplierName": "Apex Chipsets Ltd",
      "categoryName": "Electronics & Hardware",
      "latestScore": 92.5,
      "rating": "EXCELLENT",
      "ratingDisplayName": "Excellent",
      "performanceStatus": "HIGH_PERFORMING",
      "performanceStatusDisplayName": "High Performing",
      "ratingDate": "2026-08-31"
    }
  ]
}
```

### 7.8 Get Low Performing Suppliers
- **Method / URL**: `GET /api/v1/dashboard/low-performing-suppliers?limit=5`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Low performing suppliers fetched successfully",
  "data": [
    {
      "supplierId": 2,
      "supplierCode": "SUP-00002",
      "supplierName": "Beacon Logistics",
      "categoryName": "Logistics & Transport",
      "latestScore": 65.0,
      "rating": "AVERAGE",
      "ratingDisplayName": "Average",
      "performanceStatus": "NEEDS_IMPROVEMENT",
      "performanceStatusDisplayName": "Needs Improvement",
      "ratingDate": "2026-08-31"
    }
  ]
}
```

### 7.9 Get Recent Evaluations
- **Method / URL**: `GET /api/v1/dashboard/recent-evaluations?limit=10`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Recent evaluations fetched successfully",
  "data": [
    {
      "evaluationId": 1,
      "evaluationCode": "EV-202608-0001",
      "supplierId": 1,
      "supplierName": "Apex Chipsets Ltd",
      "evaluatorName": "Manager User",
      "evaluationDate": "2026-08-30",
      "status": "COMPLETED",
      "totalScore": 92.5
    }
  ]
}
```

### 7.10 Get Supplier Performance Trends
- **Method / URL**: `GET /api/v1/dashboard/performance-trends?supplierId=1`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Supplier performance trends fetched successfully",
  "data": [
    {
      "date": "2026-08-30",
      "score": 92.5,
      "rating": "EXCELLENT",
      "ratingDisplayName": "Excellent",
      "performanceStatus": "HIGH_PERFORMING",
      "performanceStatusDisplayName": "High Performing"
    }
  ]
}
```

### 7.11 Get Overall Performance Trend
- **Method / URL**: `GET /api/v1/dashboard/overall-performance-trend?groupBy=MONTH`
- **Access**: `ROLE_ADMIN` only
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Overall performance trend fetched successfully",
  "data": [
    {
      "period": "2026-08",
      "averageScore": 81.33,
      "evaluationCount": 3
    }
  ]
}
```

### 7.12 Get Suppliers by Category
- **Method / URL**: `GET /api/v1/dashboard/suppliers-by-category`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Suppliers by category fetched successfully",
  "data": [
    {
      "categoryId": 1,
      "categoryName": "Electronics & Hardware",
      "supplierCount": 2
    },
    {
      "categoryId": 2,
      "categoryName": "Logistics & Transport",
      "supplierCount": 2
    }
  ]
}
```

---

## 8. Reports & Export Endpoints (Phase 7)

### 8.1 Get Supplier Performance Report
- **Method / URL**: `GET /api/v1/reports/supplier/{supplierId}?startDate=2026-01-01&endDate=2026-12-31`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Supplier performance report generated successfully",
  "data": {
    "supplierId": 1,
    "supplierCode": "SUP-00001",
    "supplierName": "Apex Chipsets Ltd",
    "categoryName": "Electronics & Hardware",
    "active": true,
    "latestScore": 92.5,
    "latestRating": "EXCELLENT",
    "latestRatingDisplayName": "Excellent",
    "performanceStatus": "HIGH_PERFORMING",
    "performanceStatusDisplayName": "High Performing",
    "previousScore": 86.5,
    "scoreDifference": 6.0,
    "performanceTrend": "IMPROVING",
    "performanceTrendDisplayName": "Improving",
    "latestRatingDate": "2026-08-20",
    "ratingHistory": [
      {
        "id": 1,
        "supplierId": 1,
        "score": 92.5,
        "rating": "EXCELLENT",
        "ratingDisplayName": "Excellent",
        "performanceStatus": "HIGH_PERFORMING",
        "performanceStatusDisplayName": "High Performing",
        "ratingDate": "2026-08-20",
        "evaluationCode": "EV-2026-001"
      }
    ]
  }
}
```

### 8.2 Get Supplier Evaluation Scorecard Report
- **Method / URL**: `GET /api/v1/reports/evaluation/{evaluationId}`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Supplier evaluation report generated successfully",
  "data": {
    "evaluationId": 1,
    "evaluationCode": "EV-2026-001",
    "supplierId": 1,
    "supplierCode": "SUP-00001",
    "supplierName": "Apex Chipsets Ltd",
    "categoryName": "Electronics & Hardware",
    "evaluatorName": "Manager User",
    "evaluationDate": "2026-08-20",
    "evaluationPeriod": "Q3 2026",
    "status": "COMPLETED",
    "totalScore": 92.5,
    "generalComments": "Excellent supplier engagement and quality compliance.",
    "strengths": "On-time delivery, defect rate < 0.1%",
    "areasForImprovement": "None",
    "recommendation": "Renew preferred supplier partnership",
    "criteriaScores": [
      {
        "criteriaId": 1,
        "criteriaName": "Product Quality",
        "criteriaCode": "CRIT-QUAL",
        "weight": 35.0,
        "rawScore": 95.0,
        "maxScore": 100.0,
        "weightedScore": 33.25,
        "comments": "Defect-free batch delivery"
      }
    ]
  }
}
```

### 8.3 Get Supplier Rating History Report
- **Method / URL**: `GET /api/v1/reports/supplier/{supplierId}/rating-history?startDate=2026-01-01&endDate=2026-12-31`
- **Access**: `ROLE_ADMIN`, `ROLE_MANAGER`

### 8.4 Get Overall Supplier Performance Summary Report
- **Method / URL**: `GET /api/v1/reports/overall?startDate=2026-01-01&endDate=2026-12-31`
- **Access**: `ROLE_ADMIN` only
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Overall performance report generated successfully",
  "data": {
    "generatedAt": "2026-09-01",
    "totalSuppliers": 6,
    "activeSuppliers": 5,
    "inactiveSuppliers": 1,
    "totalRatedSuppliers": 3,
    "averagePerformanceScore": 81.33,
    "highestPerformanceScore": 92.5,
    "lowestPerformanceScore": 65.0,
    "ratingDistribution": [],
    "performanceStatusDistribution": [],
    "topSuppliers": [],
    "lowPerformingSuppliers": []
  }
}
```

### 8.5 Get Evaluation Summary Report
- **Method / URL**: `GET /api/v1/reports/evaluations/summary?startDate=2026-01-01&endDate=2026-12-31`
- **Access**: `ROLE_ADMIN` only

### 8.6 Report Export Endpoints
- **PDF Export**:
  - `GET /api/v1/reports/supplier/{supplierId}/export/pdf` (`ROLE_ADMIN`, `ROLE_MANAGER`)
  - `GET /api/v1/reports/overall/export/pdf` (`ROLE_ADMIN`)
  - `GET /api/v1/reports/evaluation/{evaluationId}/export/pdf` (`ROLE_ADMIN`, `ROLE_MANAGER`)
  - *Response*: `application/pdf` with `Content-Disposition: attachment; filename="..."`
- **Excel Export**:
  - `GET /api/v1/reports/supplier/{supplierId}/export/excel` (`ROLE_ADMIN`, `ROLE_MANAGER`)
  - `GET /api/v1/reports/overall/export/excel` (`ROLE_ADMIN`)
  - `GET /api/v1/reports/evaluation/{evaluationId}/export/excel` (`ROLE_ADMIN`, `ROLE_MANAGER`)
  - *Response*: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` with `Content-Disposition: attachment; filename="..."`
- **CSV Export**:
  - `GET /api/v1/reports/supplier/{supplierId}/export/csv` (`ROLE_ADMIN`, `ROLE_MANAGER`)
  - `GET /api/v1/reports/overall/export/csv` (`ROLE_ADMIN`)
  - `GET /api/v1/reports/evaluation/{evaluationId}/export/csv` (`ROLE_ADMIN`, `ROLE_MANAGER`)
  - *Response*: `text/csv` with UTF-8 encoding and `Content-Disposition: attachment; filename="..."`

