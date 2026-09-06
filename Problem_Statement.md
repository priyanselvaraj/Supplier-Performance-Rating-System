# Problem Statement: Supplier Performance Rating System (SPRS)

## 1. Executive Summary
Modern enterprises manage sprawling supply chains consisting of hundreds or thousands of external vendors, service providers, and raw material suppliers. However, conventional supplier management processes often rely on disparate spreadsheets, subjective feedback, or informal communication channels. This lack of centralized rating and data-driven evaluation causes significant supply chain vulnerabilities, including delayed deliveries, inconsistent product quality, cost overruns, and non-compliance with industry standards.

The **Supplier Performance Rating System (SPRS)** solves these challenges by providing a unified, web-based software platform for systematic vendor evaluation, dynamic multi-criteria weighted scoring, real-time performance tier classification, executive dashboard analytics, and role-based governance.

---

## 2. Core Industry Problems Addressed

### 2.1 Lack of Standardized Evaluation Criteria
- **Challenge**: Different departments evaluate suppliers using incompatible metrics, leading to inconsistent conclusions about vendor quality.
- **Solution**: SPRS provides configurable evaluation criteria (e.g., Quality, Delivery Punctuality, Pricing Competitiveness, Customer Support, Compliance) with customizable weight distributions totaling 100%.

### 2.2 Subjective & Opaque Rating Mechanisms
- **Challenge**: Manual calculations often produce arbitrary ratings that cannot be audited or justified during contract renewals or dispute resolutions.
- **Solution**: SPRS features an automated weighted scoring engine that computes normalized percentages and assigns categorical performance tiers (`EXCELLENT`, `GOOD`, `AVERAGE`, `POOR`) with full historical audit trails.

### 2.3 Absence of Real-Time Supply Chain Visibility
- **Challenge**: Procurement executives lack actionable dashboards to immediately identify underperforming vendors before critical disruptions occur.
- **Solution**: SPRS delivers an executive dashboard with interactive visual distributions (doughnut and bar analytics), top-performing vendor rankings, and immediate warning flags for underperforming suppliers.

### 2.4 Data Security, Access Control, and Compliance
- **Challenge**: Unrestricted access risks tampering with sensitive supplier pricing, performance scores, or contractual data.
- **Solution**: Role-Based Access Control (RBAC) enforced via Spring Security 6 and stateless JWT authentication, ensuring strict separation of duties between Administrators and Procurement Managers.

---

## 3. Project Objectives
1. **Centralize Vendor Management**: Maintain a single source of truth for vendor directories, contacts, categories, and operational statuses (`ACTIVE`, `INACTIVE`, `PENDING_REVIEW`).
2. **Automate Weighted Evaluations**: Compute multi-criteria weighted evaluations with verifiable formulas:
   $$\text{Final Score} = \left( \frac{\sum (\text{Score}_i / \text{MaxScore}_i) \times \text{Weight}_i}{\sum \text{Weight}_i} \right) \times 100$$
3. **Categorize Performance Tiers**:
   - **EXCELLENT** ($\ge 85\%$): Preferred partners for strategic contracts.
   - **GOOD** ($70\% - 84.99\%$): Reliable performers meeting standard SLAs.
   - **AVERAGE** ($50\% - 69.99\%$): Vendors requiring monitoring and SLA reviews.
   - **POOR** ($< 50\%$): High-risk vendors flagged for corrective action plans or termination.
4. **Generate Verifiable Reports**: Produce exportable and printable audit sheets, historical supplier trend reports, and aggregated category distributions.
5. **Ensure Enterprise Standards**: Deliver high test coverage ($>80\%$), OWASP-compliant password policies, automated CI/CD pipelines, and clean layered architecture (Controller -> Service -> Repository -> Entity).
