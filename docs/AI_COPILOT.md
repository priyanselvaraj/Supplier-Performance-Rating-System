# Phase 18 — Intelligent Automation, AI Copilot and Advanced Decision Support

## 1. Executive Summary

Phase 18 introduces an enterprise-grade **AI Copilot** and **Intelligent Decision Support Architecture** to the Supplier Performance Rating System (SPRS). Moving beyond historical retrospective reporting, the platform now provides proactive, conversational decision support grounded exclusively in real, authorized system records.

---

## 2. Core Architectural Principles

```
+-------------------------------------------------------------------------------+
|                               User Interaction                                |
|   (Natural Language Question / Prompt Chips / Decision Trigger)               |
+-------------------------------------------------------------------------------+
                                        |
                                        v
+-------------------------------------------------------------------------------+
|                       Role-Based Authorization Layer                          |
|   (Validates JWT, enforces Supplier Isolation & Manager/Admin Permissions)    |
+-------------------------------------------------------------------------------+
                                        |
                                        v
+-------------------------------------------------------------------------------+
|                       AI Copilot Intent Classifier                            |
|   - Performance Analysis        - Risk Intelligence                           |
|   - Multi-Cycle Trends          - Comparative Insights                        |
|   - Remediation Actions (CAP)   - Governance Workflows                        |
|   - Executive C-Suite Summary   - Anomaly Explanations                        |
+-------------------------------------------------------------------------------+
                                        |
                                        v
+-------------------------------------------------------------------------------+
|                       Controlled Domain Data Services                         |
|   (SupplierRepository, EvaluationRepository, ImprovementActionRepo, etc.)    |
+-------------------------------------------------------------------------------+
                                        |
                                        v
+-------------------------------------------------------------------------------+
|                         Analytical Decision Engine                            |
|   - Weighted Linear Regressions  - Composite Risk Indices                     |
|   - Defect Anomaly Tracking      - Velocity Rates & Projections               |
+-------------------------------------------------------------------------------+
                                        |
                                        v
+-------------------------------------------------------------------------------+
|                         Explainable AI Formulator                             |
|   - Structured Markdown Synthesis     - Metric Citations & Audits             |
|   - Recommended Human Next Steps      - Follow-up Prompt Exploration          |
+-------------------------------------------------------------------------------+
                                        |
                                        v
+-------------------------------------------------------------------------------+
|                    Human-in-the-Loop Decision Governance                      |
|   (Accept / Dismiss / Convert Recommendation into Official CAP Action)        |
+-------------------------------------------------------------------------------+
```

---

## 3. Human-in-the-Loop Governance & Safety Rules

1. **Advisory Role Only**: The AI Copilot generates explanations, warnings, and remediation proposals. It **never** autonomously mutates business records, alters supplier ratings, or approves/rejects vendor applications.
2. **Action Initiation Protocol**: When a manager approves an AI recommendation, they can trigger an official **Corrective Action Plan (CAP)**, recording their human identity, timestamp, and target completion timeline.
3. **Audit Trail & Decision Persistence**: All human decisions on AI recommendations are preserved in `ai_recommendation_decisions` for regulatory and compliance audits.

---

## 4. Role-Based Data Isolation & Security

| Role | Permitted AI Scope | Restrictions |
| :--- | :--- | :--- |
| `ROLE_ADMIN` | Full Portfolio Access, Executive AI Insights, System-wide Analytics | Unrestricted authorized database analytics |
| `ROLE_MANAGER` | All Supplier Scorecards, Workflow Escalations, Action Plan Initiations | Unrestricted authorized database analytics |
| `ROLE_SUPPLIER` | Strictly isolated to the vendor's own scorecard and risk indicators | Blocked from viewing competitors or portfolio-wide comparisons |

---

## 5. API Reference

### 5.1 AI Copilot Query
- **Endpoint**: `POST /api/v1/ai/copilot/query`
- **Access**: `ADMIN`, `MANAGER`, `SUPPLIER`
- **Request Body**:
```json
{
  "question": "Which suppliers are performing poorly?",
  "supplierId": null,
  "context": null
}
```
- **Response**:
```json
{
  "success": true,
  "data": {
    "id": 101,
    "question": "Which suppliers are performing poorly?",
    "answer": "### Underperforming Suppliers Identified\n- **Apex Logistics** (Score: 68.5%, Tier: POOR)...",
    "queryIntent": "Identify Underperforming Suppliers",
    "intentCategory": "PERFORMANCE_ANALYSIS",
    "confidence": "HIGH",
    "dataCitations": ["Apex Logistics rating: 68.5% from latest audit"],
    "suggestedActions": ["Initiate Corrective Action Plan (CAP)"],
    "followUpQuestions": ["Why is Apex Logistics considered high risk?"]
  }
}
```

### 5.2 Executive AI Insights
- **Endpoint**: `GET /api/v1/ai/executive-insights`
- **Access**: `ADMIN`, `MANAGER`
- **Description**: Synthesizes portfolio average scores, high-risk suppliers, open improvement plans, and prioritized strategic recommendations.

### 5.3 Supplier AI Comparison
- **Endpoint**: `POST /api/v1/ai/suppliers/compare`
- **Access**: `ADMIN`, `MANAGER`
- **Request Body**: `{"supplierIds": [1, 2]}`

### 5.4 Human Recommendation Decision
- **Endpoint**: `POST /api/v1/ai/recommendations/{id}/decision`
- **Access**: `ADMIN`, `MANAGER`
- **Request Body**:
```json
{
  "recommendationRef": "UUID-REC-001",
  "status": "ACTION_CREATED",
  "decisionNotes": "Approved CAP for logistics delay",
  "actionTitle": "Remediate Delivery SLA",
  "targetCompletionDate": "2026-10-15"
}
```

---

## 6. Frontend Components & Routes

- `/ai-copilot` — Interactive conversational analytics assistant with quick prompt chips, history drawer, and feedback rating.
- `/executive-insights` — C-Suite decision support briefing with KPI health cards, risk lists, and strategic action recommendations.
- `/suppliers/:id` — Embedded AI Prescriptive Recommendations panel equipped with **Accept**, **Dismiss**, and **Create CAP Action** buttons.
