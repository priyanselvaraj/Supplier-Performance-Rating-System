-- =====================================================
-- Supplier Performance Rating System (SPRS)
-- Complete DML Seed Data Script for MySQL 8+
-- =====================================================

USE spr_database;

-- 1. Insert Initial System Roles
INSERT IGNORE INTO roles (id, name) VALUES 
(1, 'ROLE_ADMIN'),
(2, 'ROLE_MANAGER');

-- 2. Insert Default Administrative & Manager Users
-- Note: Passwords encoded with BCrypt (Strength 10)
-- 'Admin@12345' -> $2a$10$eD76l.uF45/W4D1gO09Jz.sVzV5mHwPjVqjGfVwz6bK1hW7p8hQXe
-- 'Manager@12345' -> $2a$10$k8lE15nC1x/U2g9lZ5jKGeXw0t4kPz7bQ8mD9qK4hW1t8lQ4hP1Xm
INSERT IGNORE INTO users (id, username, email, password, full_name, phone, department, active) VALUES
(1, 'admin', 'admin@sprsystem.com', '$2a$10$eD76l.uF45/W4D1gO09Jz.sVzV5mHwPjVqjGfVwz6bK1hW7p8hQXe', 'System Administrator', '+1 555-0100', 'IT & Governance', TRUE),
(2, 'manager', 'manager@sprsystem.com', '$2a$10$k8lE15nC1x/U2g9lZ5jKGeXw0t4kPz7bQ8mD9qK4hW1t8lQ4hP1Xm', 'Senior Procurement Manager', '+1 555-0101', 'Global Procurement', TRUE);

-- 3. Assign Roles to Users
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin has ROLE_ADMIN
(1, 2), -- admin has ROLE_MANAGER
(2, 2); -- manager has ROLE_MANAGER

-- 4. Insert Default Supplier Categories
INSERT IGNORE INTO supplier_categories (id, name, code, description) VALUES
(1, 'Electronics & Hardware', 'CAT-ELEC', 'Semiconductor chips, printed circuit boards, sensors, microcontrollers, and electronic assemblies.'),
(2, 'Raw Materials & Metallurgy', 'CAT-RAW', 'Metals, polymers, industrial chemicals, alloys, and raw physical commodities.'),
(3, 'Logistics & Freight Services', 'CAT-LOG', 'Global air, sea, rail freight, warehousing, customs brokerage, and domestic last-mile shipping.'),
(4, 'Software & IT Services', 'CAT-IT', 'Cloud infrastructure hosting, custom software engineering, cybersecurity services, and SaaS tools.'),
(5, 'Industrial Packaging & Containers', 'CAT-PACK', 'Corrugated packaging, foam inserts, sustainable mailers, and palletization materials.');

-- 5. Insert Default Evaluation Criteria (Weights Total 100%)
INSERT IGNORE INTO evaluation_criteria (id, name, code, description, weight, max_score, display_order, active) VALUES
(1, 'Quality of Goods / Services', 'CRIT-QUAL', 'Evaluation of defect rates, compliance with technical specifications, material purity, and durability.', 30.0, 100.0, 1, TRUE),
(2, 'Delivery Punctuality & Lead Time', 'CRIT-DELV', 'On-time shipment rate, lead time accuracy, packaging integrity, and emergency order handling.', 25.0, 100.0, 2, TRUE),
(3, 'Pricing & Commercial Competitiveness', 'CRIT-PRIC', 'Cost transparency, market competitiveness, volume discounts, payment term flexibility, and invoice accuracy.', 20.0, 100.0, 3, TRUE),
(4, 'Customer Support & Responsiveness', 'CRIT-COMM', 'Speed of inquiry resolution, technical support availability, proactive communications, and dispute settlement.', 15.0, 100.0, 4, TRUE),
(5, 'Regulatory Compliance & ESG', 'CRIT-COMP', 'Compliance with environmental standards (ISO 14001, RoHS, REACH), labor policies, and workplace safety.', 10.0, 100.0, 5, TRUE);

-- 6. Insert Sample Suppliers
INSERT IGNORE INTO suppliers (id, supplier_code, name, contact_person, email, phone, address, city, country, category_id, status, overall_rating, rating_category, total_evaluations) VALUES
(1, 'SUP-10001', 'Apex Microelectronics Inc.', 'Sarah Jenkins', 'orders@apexmicro.com', '+1 408-555-0144', '100 Silicon Way, Suite 400', 'San Jose', 'United States', 1, 'ACTIVE', 92.0, 'EXCELLENT', 4),
(2, 'SUP-10002', 'Vanguard Global Freight', 'David Miller', 'dispatch@vanguardfreight.com', '+1 312-555-0188', '500 Logistics Blvd', 'Chicago', 'United States', 3, 'ACTIVE', 78.0, 'GOOD', 3),
(3, 'SUP-10003', 'Prime Industrial Polymers', 'Elena Rostova', 'sales@primepolymers.com', '+49 30 5550199', 'Industriestrasse 42', 'Berlin', 'Germany', 2, 'ACTIVE', 55.8, 'AVERAGE', 3),
(4, 'SUP-10004', 'CloudScale Infrastructure Inc.', 'Liam Thorne', 'support@cloudscale.io', '+1 206-555-0177', '701 Pike St', 'Seattle', 'United States', 4, 'ACTIVE', 94.0, 'EXCELLENT', 3),
(5, 'SUP-10005', 'EcoBox Logistics Packaging', 'Hannah Abbott', 'contact@ecoboxpack.com', '+44 20 7946 0912', '15 Canal Reach', 'London', 'United Kingdom', 5, 'ACTIVE', 68.8, 'AVERAGE', 3),
(6, 'SUP-10006', 'Titan Steel & Metallurgy Corp.', 'Marcus Vance', 'inquiries@titansteel.com', '+1 412-555-0155', '888 Foundry Lane', 'Pittsburgh', 'United States', 2, 'ACTIVE', 60.8, 'AVERAGE', 3),
(7, 'SUP-10007', 'Vertex Precision Instruments', 'Clara Dupont', 'sales@vertexprecision.fr', '+33 1 40 55 01 22', '24 Rue de la Paix', 'Paris', 'France', 1, 'ACTIVE', 84.3, 'VERY_GOOD', 3),
(8, 'SUP-10008', 'Nexus Facility Services', 'Robert Chen', 'support@nexusfacilities.com', '+1 617-555-0199', '120 Beacon St', 'Boston', 'United States', 5, 'ACTIVE', 74.0, 'GOOD', 2);

-- 7. Insert Sample Evaluations (Multi-Quarter: Q2 2025 - Q1 2026)
INSERT IGNORE INTO supplier_evaluations (id, evaluation_code, supplier_id, evaluator_id, evaluation_date, evaluation_period, total_weighted_score, rating_category, status, general_comments, strengths, areas_for_improvement, recommendation) VALUES
(1, 'EV-202506-1001', 1, 2, '2025-06-15', 'Q2 2025', 88.5, 'VERY_GOOD', 'COMPLETED', 'Initial qualification audit. Solid technical foundation.', 'Clean room standards exceed ISO-14644.', 'Initial batch lead times slightly longer than estimated.', 'Approve vendor for standard production ramp.'),
(2, 'EV-202509-1002', 1, 2, '2025-09-20', 'Q3 2025', 91.0, 'EXCELLENT', 'COMPLETED', 'Q3 volume production review. Strong delivery performance.', 'AOI automated optical inspection across all wafer lines.', 'Expedite air freight tracking during international customs handoffs.', 'Expand volume allocation by 15%.'),
(3, 'EV-202512-1003', 1, 2, '2025-12-18', 'Q4 2025', 93.5, 'EXCELLENT', 'COMPLETED', 'Year-end operational review. Outstanding quality consistency.', 'Defect PPM dropped below 5 PPM across 100k units delivered.', 'Explore domestic backup inventory warehousing.', 'Award preferred Tier 1 status.'),
(4, 'EV-202603-1004', 1, 2, '2026-03-10', 'Q1 2026', 95.0, 'EXCELLENT', 'COMPLETED', 'Q1 2026 benchmark assessment. Highest performing electronics vendor.', 'Six-Sigma quality metrics maintained throughout peak demand cycle.', 'None.', 'Execute multi-year strategic supply agreement.'),

(5, 'EV-202506-2001', 2, 2, '2025-06-20', 'Q2 2025', 76.5, 'GOOD', 'COMPLETED', 'Quarterly freight carrier audit. Dependable regional distribution.', '92% on-time delivery across standard domestic routes.', 'EDI status update latency occasionally exceeds 4 hours.', 'Implement automated GPS webhook integrations.'),
(6, 'EV-202509-2002', 2, 2, '2025-09-25', 'Q3 2025', 79.0, 'GOOD', 'COMPLETED', 'Q3 transit performance review. API integration completed successfully.', 'Real-time shipment visibility significantly improved dispatch scheduling.', 'Detention charge disputes require faster reconciliation.', 'Maintain current freight lane allocations.'),
(7, 'EV-202512-2003', 2, 2, '2025-12-22', 'Q4 2025', 78.5, 'GOOD', 'COMPLETED', 'Holiday peak season review. Capacity held steady despite bad weather.', 'High driver availability and dedicated account manager support.', 'Fuel surcharge adjustment calculations need clearer documentation.', 'Renew annual carrier master service agreement.'),

(8, 'EV-202506-3001', 3, 2, '2025-06-28', 'Q2 2025', 68.0, 'AVERAGE', 'COMPLETED', 'Initial polymer supply review. Acceptable baseline performance.', 'Competitive unit pricing on bulk resin orders.', 'Packaging moisture barriers need reinforcement.', 'Continue bi-monthly quality sampling.'),
(9, 'EV-202509-3002', 3, 2, '2025-09-28', 'Q3 2025', 55.0, 'AVERAGE', 'COMPLETED', 'Q3 audit triggered by incoming material non-conformance reports.', 'Immediate response to emergency inquiry.', '12% batch rejection rate due to chemical viscosity out of spec.', 'Issue Level 1 Corrective Action Plan (CAP).'),
(10, 'EV-202512-3003', 3, 2, '2025-12-29', 'Q4 2025', 44.5, 'POOR', 'COMPLETED', 'Critical failure audit. Repeated non-compliance with REACH standards.', 'None demonstrated during this period.', 'Unacceptable defect rates and unannounced manufacturing line relocation.', 'Freeze all purchase orders. Initiate formal supplier offboarding review.'),

(11, 'EV-202506-4001', 4, 2, '2025-06-12', 'Q2 2025', 91.5, 'EXCELLENT', 'COMPLETED', 'Q2 cloud SLA audit. High availability and compute throughput.', '99.99% uptime achieved across all provisioned clusters.', 'Billing portal granularity could be enhanced.', 'Maintain enterprise support tier.'),
(12, 'EV-202509-4002', 4, 2, '2025-09-15', 'Q3 2025', 94.0, 'EXCELLENT', 'COMPLETED', 'SOC-2 Type II audit verification and disaster recovery drill.', 'Zero RPO/RTO downtime recorded during simulated failover test.', 'None.', 'Authorize deployment of mission-critical databases to CloudScale VPC.'),
(13, 'EV-202512-4003', 4, 2, '2025-12-12', 'Q4 2025', 96.5, 'EXCELLENT', 'COMPLETED', 'Annual cloud infrastructure review. Flawless security and scalability.', 'Proactive DDoS mitigation prevented multiple service interruptions.', 'None.', 'Upgrade contract to Tier 1 Enterprise Partner.'),

(14, 'EV-202506-5001', 5, 2, '2025-06-18', 'Q2 2025', 72.0, 'GOOD', 'COMPLETED', 'Cardboard packaging quality inspection. Standard commercial grade.', '100% recycled fiber certification verified.', 'Box edge compression strength inconsistent under humid storage.', 'Require moisture-resistant liner upgrade.'),
(15, 'EV-202509-5002', 5, 2, '2025-09-22', 'Q3 2025', 68.5, 'AVERAGE', 'COMPLETED', 'Q3 warehouse pallet audit. Some crushed boxes observed during stacking.', 'Eco-friendly inks and biodegradable tape compliance.', 'Packaging deformation caused minor product damage in 3 shipments.', 'Issue requirement for ECT-32 test certification before next batch.'),
(16, 'EV-202512-5003', 5, 2, '2025-12-19', 'Q4 2025', 66.0, 'AVERAGE', 'COMPLETED', 'Year-end packaging review. Continued degradation in box durability.', 'Flexible order lot sizes.', 'Failure to meet minimum bursting test threshold.', 'Place vendor on 60-day performance probation.'),

(17, 'EV-202506-6001', 6, 2, '2025-06-24', 'Q2 2025', 74.0, 'GOOD', 'COMPLETED', 'Initial foundry audit for structural steel billets.', 'High tensile strength and mill test reports provided on time.', 'Surface oxidation present on outdoor storage billets.', 'Require covered tarping for all rail shipments.'),
(18, 'EV-202509-6002', 6, 2, '2025-09-26', 'Q3 2025', 60.0, 'AVERAGE', 'COMPLETED', 'Q3 metallurgical test review. Significant drop in alloy consistency.', 'Low bulk spot pricing.', 'Carbon content variance caused 4 machine tool breakages.', 'Issue mandatory root cause analysis and supplier improvement action.'),
(19, 'EV-202512-6003', 6, 2, '2025-12-28', 'Q4 2025', 48.5, 'POOR', 'COMPLETED', 'Emergency re-evaluation. Delayed deliveries and unresolved QA defects.', 'None.', 'Failure to provide Spectrometer test certificates with raw shipments.', 'Restrict vendor to non-critical auxiliary fabrications only.'),

(20, 'EV-202506-7001', 7, 2, '2025-06-10', 'Q2 2025', 81.0, 'VERY_GOOD', 'COMPLETED', 'Initial precision tooling and calibration gauge audit.', 'High dimensional accuracy (+/- 2 microns).', 'Calibration certificate format alignment needed.', 'Approve vendor with standard 12-month re-certification cycle.'),
(21, 'EV-202509-7002', 7, 2, '2025-09-14', 'Q3 2025', 84.5, 'VERY_GOOD', 'COMPLETED', 'Q3 tooling performance review. Micro-measurement accuracy confirmed.', 'Digital calibration certificate barcode integration completed.', 'Minor lead time extension on custom CNC tooling inserts.', 'Increase order volume for high-precision manufacturing lines.'),
(22, 'EV-202512-7003', 7, 2, '2025-12-15', 'Q4 2025', 87.5, 'VERY_GOOD', 'COMPLETED', 'Q4 operational review. Superb instrument durability and zero field failures.', 'Extended warranty and rapid recalibration turnaround.', 'None.', 'Award preferred precision tooling supplier status.');

-- 8. Insert Evaluation Scores Breakdown
INSERT IGNORE INTO evaluation_scores (id, evaluation_id, criteria_id, score_obtained, max_score, weight, weighted_score, remarks) VALUES
-- Eval 1: Apex Q2 2025 (88.5)
(1, 1, 1, 90.0, 100.0, 30.0, 27.0, 'Verified against Q2 2025 audit logs.'),
(2, 1, 2, 88.0, 100.0, 25.0, 22.0, 'Verified against Q2 2025 audit logs.'),
(3, 1, 3, 85.0, 100.0, 20.0, 17.0, 'Verified against Q2 2025 audit logs.'),
(4, 1, 4, 92.0, 100.0, 15.0, 13.8, 'Verified against Q2 2025 audit logs.'),
(5, 1, 5, 90.0, 100.0, 10.0, 9.0, 'Verified against Q2 2025 audit logs.'),

-- Eval 2: Apex Q3 2025 (91.0)
(6, 2, 1, 92.0, 100.0, 30.0, 27.6, 'Verified against Q3 2025 audit logs.'),
(7, 2, 2, 90.0, 100.0, 25.0, 22.5, 'Verified against Q3 2025 audit logs.'),
(8, 2, 3, 88.0, 100.0, 20.0, 17.6, 'Verified against Q3 2025 audit logs.'),
(9, 2, 4, 94.0, 100.0, 15.0, 14.1, 'Verified against Q3 2025 audit logs.'),
(10, 2, 5, 92.0, 100.0, 10.0, 9.2, 'Verified against Q3 2025 audit logs.'),

-- Eval 3: Apex Q4 2025 (93.5)
(11, 3, 1, 95.0, 100.0, 30.0, 28.5, 'Verified against Q4 2025 audit logs.'),
(12, 3, 2, 93.0, 100.0, 25.0, 23.25, 'Verified against Q4 2025 audit logs.'),
(13, 3, 3, 90.0, 100.0, 20.0, 18.0, 'Verified against Q4 2025 audit logs.'),
(14, 3, 4, 96.0, 100.0, 15.0, 14.4, 'Verified against Q4 2025 audit logs.'),
(15, 3, 5, 95.0, 100.0, 10.0, 9.5, 'Verified against Q4 2025 audit logs.'),

-- Eval 4: Apex Q1 2026 (95.0)
(16, 4, 1, 96.0, 100.0, 30.0, 28.8, 'Verified against Q1 2026 audit logs.'),
(17, 4, 2, 95.0, 100.0, 25.0, 23.75, 'Verified against Q1 2026 audit logs.'),
(18, 4, 3, 92.0, 100.0, 20.0, 18.4, 'Verified against Q1 2026 audit logs.'),
(19, 4, 4, 98.0, 100.0, 15.0, 14.7, 'Verified against Q1 2026 audit logs.'),
(20, 4, 5, 96.0, 100.0, 10.0, 9.6, 'Verified against Q1 2026 audit logs.');

-- 9. Insert Sample Performance Rating History
INSERT IGNORE INTO supplier_performance_ratings (id, supplier_id, evaluation_id, score, rating, performance_status, rating_date) VALUES
(1, 1, 1, 88.5, 'VERY_GOOD', 'HIGH_PERFORMING', '2025-06-15'),
(2, 1, 2, 91.0, 'EXCELLENT', 'HIGH_PERFORMING', '2025-09-20'),
(3, 1, 3, 93.5, 'EXCELLENT', 'HIGH_PERFORMING', '2025-12-18'),
(4, 1, 4, 95.0, 'EXCELLENT', 'HIGH_PERFORMING', '2026-03-10'),

(5, 2, 5, 76.5, 'GOOD', 'SATISFACTORY', '2025-06-20'),
(6, 2, 6, 79.0, 'GOOD', 'SATISFACTORY', '2025-09-25'),
(7, 2, 7, 78.5, 'GOOD', 'SATISFACTORY', '2025-12-22'),

(8, 3, 8, 68.0, 'AVERAGE', 'NEEDS_IMPROVEMENT', '2025-06-28'),
(9, 3, 9, 55.0, 'AVERAGE', 'NEEDS_IMPROVEMENT', '2025-09-28'),
(10, 3, 10, 44.5, 'POOR', 'LOW_PERFORMING', '2025-12-29'),

(11, 4, 11, 91.5, 'EXCELLENT', 'HIGH_PERFORMING', '2025-06-12'),
(12, 4, 12, 94.0, 'EXCELLENT', 'HIGH_PERFORMING', '2025-09-15'),
(13, 4, 13, 96.5, 'EXCELLENT', 'HIGH_PERFORMING', '2025-12-12'),

(14, 5, 14, 72.0, 'GOOD', 'SATISFACTORY', '2025-06-18'),
(15, 5, 15, 68.5, 'AVERAGE', 'NEEDS_IMPROVEMENT', '2025-09-22'),
(16, 5, 16, 66.0, 'AVERAGE', 'NEEDS_IMPROVEMENT', '2025-12-19'),

(17, 6, 17, 74.0, 'GOOD', 'SATISFACTORY', '2025-06-24'),
(18, 6, 18, 60.0, 'AVERAGE', 'NEEDS_IMPROVEMENT', '2025-09-26'),
(19, 6, 19, 48.5, 'POOR', 'LOW_PERFORMING', '2025-12-28'),

(20, 7, 20, 81.0, 'VERY_GOOD', 'HIGH_PERFORMING', '2025-06-10'),
(21, 7, 21, 84.5, 'VERY_GOOD', 'HIGH_PERFORMING', '2025-09-14'),
(22, 7, 22, 87.5, 'VERY_GOOD', 'HIGH_PERFORMING', '2025-12-15');

-- 10. Insert Sample Improvement Actions (CAP)
INSERT IGNORE INTO supplier_improvement_actions (id, supplier_id, title, description, priority, status, assigned_user_id, created_by_user_id, due_date, completed_at, resolution_notes) VALUES
(1, 3, 'Mandatory Polymer Viscosity Audit & Root Cause Analysis', 'Conduct on-site chemical engineering audit and require ISO-9001 corrective action plan following Q4 resin impurity failure.', 'CRITICAL', 'IN_PROGRESS', 2, 1, '2026-09-15', NULL, 'Audit scheduled for next Monday with vendor QA director.'),
(2, 6, 'Raw Steel Metallurgical Purity Verification', 'Implement mandatory Spectrometer chemical composition batch certification prior to rail dispatch.', 'HIGH', 'OPEN', 1, 1, '2026-09-22', NULL, NULL),
(3, 5, 'Cardboard Box Edge Crush Test (ECT) Certification', 'Upgrade packaging material liner moisture barrier and verify ECT-32 compression strength compliance.', 'MEDIUM', 'COMPLETED', 2, 1, '2026-08-25', '2026-08-28 14:30:00', 'Passed laboratory compression stress test with 38 lbs/in rating.'),
(4, 2, 'Logistics Lead-Time EDI SLA Review', 'Review transit milestone latency and establish automatic webhook status updates for air freight.', 'MEDIUM', 'OPEN', 2, 1, '2026-09-19', NULL, NULL);

-- 11. Insert Sample System Notifications
INSERT IGNORE INTO notifications (id, user_id, title, message, notification_type, priority, related_resource_type, related_resource_id, is_read, created_at) VALUES
(1, 1, 'Early Warning: High Risk Vendor Detected', 'Prime Industrial Polymers (SUP-10003) score fell below 50% threshold. Immediate remediation required.', 'ALERT', 'CRITICAL', 'SUPPLIER', 3, FALSE, NOW() - INTERVAL 2 HOUR),
(2, 1, 'Quarterly Evaluation Completed', 'Evaluation EV-202603-1004 completed for Apex Microelectronics Inc. (Score: 95.0%).', 'EVALUATION', 'MEDIUM', 'EVALUATION', 4, FALSE, NOW() - INTERVAL 5 HOUR),
(3, 1, 'Sharp Performance Drop Detected', 'Titan Steel & Metallurgy Corp. (SUP-10006) dropped by 11.5 points in Q4 2025 review.', 'ALERT', 'HIGH', 'SUPPLIER', 6, FALSE, NOW() - INTERVAL 1 DAY),
(4, 2, 'Action Item Assigned: Quality Audit', 'You have been assigned to conduct an On-Site Quality Audit for Prime Industrial Polymers.', 'IMPROVEMENT_ACTION', 'HIGH', 'SUPPLIER', 3, FALSE, NOW() - INTERVAL 4 HOUR),
(5, 2, 'AI Intelligence Velocity Report', 'Apex Microelectronics trajectory velocity forecasted at +2.1 pts/cycle with HIGH confidence.', 'AI_INSIGHT', 'LOW', 'SUPPLIER', 1, TRUE, NOW() - INTERVAL 3 DAY);
