# Entity-Relationship (ER) Diagram

## 1. Database Schema Overview

The SPRS relational database schema manages entities with referential integrity, foreign key constraints, cascading rules, and optimized indexes.

```mermaid
erDiagram
    USERS ||--o{ USER_ROLES : "assigned"
    ROLES ||--o{ USER_ROLES : "contains"
    
    SUPPLIER_CATEGORIES ||--o{ SUPPLIERS : "classifies"
    
    SUPPLIERS ||--o{ SUPPLIER_EVALUATIONS : "evaluated in"
    USERS ||--o{ SUPPLIER_EVALUATIONS : "conducted by"
    
    SUPPLIER_EVALUATIONS ||--|{ EVALUATION_SCORES : "composed of"
    EVALUATION_CRITERIA ||--o{ EVALUATION_SCORES : "scored against"

    USERS {
        bigint id PK
        varchar username UK
        varchar email UK
        varchar password
        varchar full_name
        varchar phone
        varchar department
        boolean active
        datetime created_at
        datetime updated_at
    }

    ROLES {
        bigint id PK
        varchar name UK
    }

    USER_ROLES {
        bigint user_id PK,FK
        bigint role_id PK,FK
    }

    SUPPLIER_CATEGORIES {
        bigint id PK
        varchar name UK
        varchar code UK
        varchar description
        datetime created_at
        datetime updated_at
    }

    SUPPLIERS {
        bigint id PK
        varchar supplier_code UK
        varchar name
        varchar contact_person
        varchar email UK
        varchar phone
        varchar address
        varchar city
        varchar country
        bigint category_id FK
        varchar status
        double overall_rating
        varchar rating_category
        int total_evaluations
        datetime created_at
        datetime updated_at
    }

    EVALUATION_CRITERIA {
        bigint id PK
        varchar name UK
        varchar code UK
        varchar description
        double weight
        double max_score
        int display_order
        boolean active
        datetime created_at
        datetime updated_at
    }

    SUPPLIER_EVALUATIONS {
        bigint id PK
        varchar evaluation_code UK
        bigint supplier_id FK
        bigint evaluator_id FK
        date evaluation_date
        varchar evaluation_period
        double total_weighted_score
        varchar rating_category
        text general_comments
        text strengths
        text areas_for_improvement
        text recommendation
        datetime created_at
        datetime updated_at
    }

    EVALUATION_SCORES {
        bigint id PK
        bigint evaluation_id FK
        bigint criteria_id FK
        double score_obtained
        double max_score
        double weight
        double weighted_score
        varchar remarks
    }
```

---

## 2. Table Cardinalities & Business Rules
1. **User & Roles**: Many-to-Many (`users` $\leftrightarrow$ `roles` via `user_roles`).
2. **Category & Suppliers**: One-to-Many (`supplier_categories` $\to$ `suppliers`). A category can have many suppliers; deleting a category requires reassigning suppliers.
3. **Supplier & Evaluations**: One-to-Many (`suppliers` $\to$ `supplier_evaluations`). Deleting an evaluation automatically triggers a recalculation of the supplier's rolling average rating.
4. **Evaluation & Scores**: One-to-Many with Cascade Delete (`supplier_evaluations` $\to$ `evaluation_scores`). Deleting an evaluation removes its child score entries.
5. **Criteria & Scores**: One-to-Many (`evaluation_criteria` $\to$ `evaluation_scores`). Criteria definitions track historical scores.
