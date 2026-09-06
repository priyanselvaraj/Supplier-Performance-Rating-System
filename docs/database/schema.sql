-- =====================================================
-- Supplier Performance Rating System (SPRS)
-- DDL Schema Script for MySQL 8+
-- =====================================================

CREATE DATABASE IF NOT EXISTS spr_database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE spr_database;

-- 1. Roles Table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- 2. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    department VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_username (username),
    INDEX idx_user_email (email)
) ENGINE=InnoDB;

-- 3. User Roles Join Table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Supplier Categories Table
CREATE TABLE IF NOT EXISTS supplier_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(50) UNIQUE,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_cat_name (name),
    INDEX idx_cat_active (active)
) ENGINE=InnoDB;

-- 5. Suppliers Table
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(50),
    address VARCHAR(255),
    website VARCHAR(150),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    category_id BIGINT,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    overall_rating DOUBLE NOT NULL DEFAULT 0.0,
    rating_category VARCHAR(30) NOT NULL DEFAULT 'UNRATED',
    total_evaluations INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_supplier_category FOREIGN KEY (category_id) REFERENCES supplier_categories (id) ON DELETE SET NULL,
    INDEX idx_sup_code (supplier_code),
    INDEX idx_sup_status (status),
    INDEX idx_sup_rating_cat (rating_category),
    INDEX idx_sup_city (city),
    INDEX idx_sup_country (country)
) ENGINE=InnoDB;

-- 6. Evaluation Criteria Table
CREATE TABLE IF NOT EXISTS evaluation_criteria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(50) UNIQUE,
    description TEXT,
    weight DOUBLE NOT NULL DEFAULT 0.0,
    max_score DOUBLE NOT NULL DEFAULT 100.0,
    display_order INT NOT NULL DEFAULT 1,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_crit_active (active),
    INDEX idx_crit_order (display_order)
) ENGINE=InnoDB;

-- 7. Supplier Evaluations Table
CREATE TABLE IF NOT EXISTS supplier_evaluations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evaluation_code VARCHAR(50) NOT NULL UNIQUE,
    supplier_id BIGINT NOT NULL,
    evaluator_id BIGINT NOT NULL,
    evaluation_date DATE NOT NULL,
    evaluation_period VARCHAR(50),
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    total_weighted_score DOUBLE NOT NULL,
    rating_category VARCHAR(30) NOT NULL,
    general_comments TEXT,
    strengths TEXT,
    areas_for_improvement TEXT,
    recommendation TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_eval_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id) ON DELETE CASCADE,
    CONSTRAINT fk_eval_evaluator FOREIGN KEY (evaluator_id) REFERENCES users (id) ON DELETE RESTRICT,
    INDEX idx_eval_code (evaluation_code),
    INDEX idx_eval_date (evaluation_date),
    INDEX idx_eval_status (status),
    INDEX idx_eval_rating (rating_category)
) ENGINE=InnoDB;

-- 8. Evaluation Scores Table
CREATE TABLE IF NOT EXISTS evaluation_scores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evaluation_id BIGINT NOT NULL,
    criteria_id BIGINT NOT NULL,
    score_obtained DOUBLE NOT NULL,
    max_score DOUBLE NOT NULL DEFAULT 100.0,
    weight DOUBLE NOT NULL,
    weighted_score DOUBLE NOT NULL,
    remarks VARCHAR(255),
    CONSTRAINT fk_score_evaluation FOREIGN KEY (evaluation_id) REFERENCES supplier_evaluations (id) ON DELETE CASCADE,
    CONSTRAINT fk_score_criteria FOREIGN KEY (criteria_id) REFERENCES evaluation_criteria (id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 9. Supplier Performance Ratings Table (Phase 5)
CREATE TABLE IF NOT EXISTS supplier_performance_ratings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    evaluation_id BIGINT NOT NULL UNIQUE,
    score DOUBLE NOT NULL,
    rating VARCHAR(30) NOT NULL,
    performance_status VARCHAR(30) NOT NULL,
    rating_date DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_rating_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id) ON DELETE CASCADE,
    CONSTRAINT fk_rating_evaluation FOREIGN KEY (evaluation_id) REFERENCES supplier_evaluations (id) ON DELETE CASCADE,
    INDEX idx_spr_rating_date (rating_date),
    INDEX idx_spr_rating (rating),
    INDEX idx_spr_perf_status (performance_status)
) ENGINE=InnoDB;
