package com.supplier.sprsystem.config;

import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SupplierCategoryRepository categoryRepository;
    private final EvaluationCriteriaRepository criteriaRepository;
    private final SupplierRepository supplierRepository;
    private final SupplierEvaluationRepository evaluationRepository;
    private final SupplierPerformanceRatingRepository performanceRatingRepository;
    private final NotificationRepository notificationRepository;
    private final SupplierImprovementActionRepository improvementActionRepository;
    private final SupplierDocumentRepository documentRepository;
    private final SupplierProfileUpdateRequestRepository profileUpdateRequestRepository;
    private final SupplierCommunicationRepository communicationRepository;
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowInstanceRepository workflowInstanceRepository;
    private final ApprovalTaskRepository approvalTaskRepository;
    private final WorkflowEscalationRepository workflowEscalationRepository;
    private final WorkflowAuditLogRepository workflowAuditLogRepository;
    private final KpiDefinitionRepository kpiDefinitionRepository;
    private final SavedReportRepository savedReportRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final WebhookSubscriptionRepository webhookSubscriptionRepository;
    private final IntegrationSyncHistoryRepository syncHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.seed.enabled:true}")
    private boolean seedEnabled = true;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           SupplierCategoryRepository categoryRepository,
                           EvaluationCriteriaRepository criteriaRepository,
                           SupplierRepository supplierRepository,
                           SupplierEvaluationRepository evaluationRepository,
                           SupplierPerformanceRatingRepository performanceRatingRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) NotificationRepository notificationRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) SupplierImprovementActionRepository improvementActionRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) SupplierDocumentRepository documentRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) SupplierProfileUpdateRequestRepository profileUpdateRequestRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) SupplierCommunicationRepository communicationRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) WorkflowDefinitionRepository workflowDefinitionRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) WorkflowInstanceRepository workflowInstanceRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) ApprovalTaskRepository approvalTaskRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) WorkflowEscalationRepository workflowEscalationRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) WorkflowAuditLogRepository workflowAuditLogRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) KpiDefinitionRepository kpiDefinitionRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) SavedReportRepository savedReportRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) ApiKeyRepository apiKeyRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) WebhookSubscriptionRepository webhookSubscriptionRepository,
                           @org.springframework.beans.factory.annotation.Autowired(required = false) IntegrationSyncHistoryRepository syncHistoryRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.criteriaRepository = criteriaRepository;
        this.supplierRepository = supplierRepository;
        this.evaluationRepository = evaluationRepository;
        this.performanceRatingRepository = performanceRatingRepository;
        this.notificationRepository = notificationRepository;
        this.improvementActionRepository = improvementActionRepository;
        this.documentRepository = documentRepository;
        this.profileUpdateRequestRepository = profileUpdateRequestRepository;
        this.communicationRepository = communicationRepository;
        this.workflowDefinitionRepository = workflowDefinitionRepository;
        this.workflowInstanceRepository = workflowInstanceRepository;
        this.approvalTaskRepository = approvalTaskRepository;
        this.workflowEscalationRepository = workflowEscalationRepository;
        this.workflowAuditLogRepository = workflowAuditLogRepository;
        this.kpiDefinitionRepository = kpiDefinitionRepository;
        this.savedReportRepository = savedReportRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.webhookSubscriptionRepository = webhookSubscriptionRepository;
        this.syncHistoryRepository = syncHistoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initRoles();
        initDefaultUsers();
        initDefaultCategories();
        initDefaultCriteria();
        initDefaultWorkflowDefinitions();
        initDefaultKpiDefinitions();
        if (seedEnabled) {
            initSampleSuppliersAndEvaluations();
            initSupplierPortalData();
            initSampleWorkflowInstances();
            initSampleSavedReports();
            initSampleIntegrations();
        }
    }

    private void initRoles() {
        for (ERole eRole : ERole.values()) {
            if (!roleRepository.existsByName(eRole)) {
                Role role = Role.builder().name(eRole).build();
                roleRepository.save(role);
                logger.info("Initialized role: {}", eRole.name());
            }
        }
    }

    private void initDefaultUsers() {
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElse(null);
        Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER).orElse(null);

        if (!userRepository.existsByUsername("admin")) {
            Set<Role> adminRoles = new HashSet<>();
            if (adminRole != null) adminRoles.add(adminRole);
            if (managerRole != null) adminRoles.add(managerRole);

            User admin = User.builder()
                    .username("admin")
                    .email("admin@sprsystem.com")
                    .password(passwordEncoder.encode("Admin@12345"))
                    .fullName("System Administrator")
                    .phone("+1 555-0100")
                    .department("IT Administration")
                    .active(true)
                    .roles(adminRoles)
                    .build();

            userRepository.save(admin);
            logger.info("Initialized default admin user: admin / Admin@12345");
        }

        if (!userRepository.existsByUsername("manager")) {
            Set<Role> mgrRoles = new HashSet<>();
            if (managerRole != null) mgrRoles.add(managerRole);

            User manager = User.builder()
                    .username("manager")
                    .email("manager@sprsystem.com")
                    .password(passwordEncoder.encode("Manager@12345"))
                    .fullName("Procurement Manager")
                    .phone("+1 555-0101")
                    .department("Procurement & Supply Chain")
                    .active(true)
                    .roles(mgrRoles)
                    .build();

            userRepository.save(manager);
            logger.info("Initialized default manager user: manager / Manager@12345");
        }
    }

    private void initDefaultCategories() {
        if (categoryRepository.count() == 0) {
            List<SupplierCategory> categories = Arrays.asList(
                    SupplierCategory.builder()
                            .name("Electronics & Hardware")
                            .code("CAT-ELEC")
                            .description("Suppliers providing computer chips, circuit boards, and electronic equipment.")
                            .build(),
                    SupplierCategory.builder()
                            .name("Raw Materials")
                            .code("CAT-RAW")
                            .description("Suppliers supplying metals, plastics, chemical precursors, and textiles.")
                            .build(),
                    SupplierCategory.builder()
                            .name("Logistics & Transportation")
                            .code("CAT-LOG")
                            .description("Freight carriers, warehousing services, and 3PL distribution providers.")
                            .build(),
                    SupplierCategory.builder()
                            .name("IT & Cloud Services")
                            .code("CAT-IT")
                            .description("Software licensing, cloud infrastructure, and consulting vendors.")
                            .build(),
                    SupplierCategory.builder()
                            .name("Packaging & Distribution")
                            .code("CAT-PACK")
                            .description("Cartons, containers, pallets, and shipping packaging materials.")
                            .build()
            );

            categoryRepository.saveAll(categories);
            logger.info("Initialized {} default supplier categories.", categories.size());
        }
    }

    private void initDefaultCriteria() {
        if (criteriaRepository.count() == 0) {
            List<EvaluationCriteria> criteriaList = Arrays.asList(
                    EvaluationCriteria.builder()
                            .name("Product Quality")
                            .code("CRIT-QUAL")
                            .description("Conformance to specifications, defect rate, and standard compliance.")
                            .weight(30.0)
                            .maxScore(100.0)
                            .displayOrder(1)
                            .active(true)
                            .build(),
                    EvaluationCriteria.builder()
                            .name("Delivery Performance")
                            .code("CRIT-DELV")
                            .description("On-time delivery rate, lead time adherence, and order accuracy.")
                            .weight(25.0)
                            .maxScore(100.0)
                            .displayOrder(2)
                            .active(true)
                            .build(),
                    EvaluationCriteria.builder()
                            .name("Price Competitiveness")
                            .code("CRIT-PRIC")
                            .description("Competitive pricing, payment terms flexibility, and cost transparency.")
                            .weight(20.0)
                            .maxScore(100.0)
                            .displayOrder(3)
                            .active(true)
                            .build(),
                    EvaluationCriteria.builder()
                            .name("Communication & Support")
                            .code("CRIT-COMM")
                            .description("Responsiveness to inquiries, issue resolution speed, and customer service.")
                            .weight(15.0)
                            .maxScore(100.0)
                            .displayOrder(4)
                            .active(true)
                            .build(),
                    EvaluationCriteria.builder()
                            .name("Reliability & Risk Compliance")
                            .code("CRIT-RELI")
                            .description("Financial stability, business continuity, regulatory compliance, and risk controls.")
                            .weight(10.0)
                            .maxScore(100.0)
                            .displayOrder(5)
                            .active(true)
                            .build()
            );

            criteriaRepository.saveAll(criteriaList);
            logger.info("Initialized {} evaluation criteria with total weight 100%.", criteriaList.size());
        }
    }

    private void initSampleSuppliersAndEvaluations() {
        if (supplierRepository.count() == 0) {
            List<SupplierCategory> categories = categoryRepository.findAll();
            if (categories.isEmpty()) return;

            SupplierCategory catElec = categories.stream().filter(c -> "CAT-ELEC".equals(c.getCode())).findFirst().orElse(categories.get(0));
            SupplierCategory catLog = categories.stream().filter(c -> "CAT-LOG".equals(c.getCode())).findFirst().orElse(categories.get(0));
            SupplierCategory catRaw = categories.stream().filter(c -> "CAT-RAW".equals(c.getCode())).findFirst().orElse(categories.get(0));
            SupplierCategory catIt = categories.stream().filter(c -> "CAT-IT".equals(c.getCode())).findFirst().orElse(categories.get(0));
            SupplierCategory catPack = categories.stream().filter(c -> "CAT-PACK".equals(c.getCode())).findFirst().orElse(categories.get(0));

            User manager = userRepository.findByUsername("manager").orElse(null);
            User admin = userRepository.findByUsername("admin").orElse(null);
            List<EvaluationCriteria> criteriaList = criteriaRepository.findAllByOrderByDisplayOrderAsc();

            // 1. Apex Microelectronics Inc. (EXCELLENT - Sustained Improving)
            Supplier sup1 = Supplier.builder()
                    .supplierCode("SUP-10001")
                    .name("Apex Microelectronics Inc.")
                    .contactPerson("Sarah Jenkins")
                    .email("orders@apexmicro.com")
                    .phone("+1 408-555-0144")
                    .address("100 Silicon Way, Suite 400")
                    .city("San Jose")
                    .country("United States")
                    .category(catElec)
                    .status(SupplierStatus.ACTIVE)
                    .overallRating(92.0)
                    .ratingCategory(RatingCategory.EXCELLENT)
                    .totalEvaluations(4)
                    .build();
            sup1 = supplierRepository.save(sup1);

            // 2. Vanguard Global Freight (GOOD - Stable)
            Supplier sup2 = Supplier.builder()
                    .supplierCode("SUP-10002")
                    .name("Vanguard Global Freight")
                    .contactPerson("David Miller")
                    .email("dispatch@vanguardfreight.com")
                    .phone("+1 312-555-0188")
                    .address("500 Logistics Blvd")
                    .city("Chicago")
                    .country("United States")
                    .category(catLog)
                    .status(SupplierStatus.ACTIVE)
                    .overallRating(78.0)
                    .ratingCategory(RatingCategory.GOOD)
                    .totalEvaluations(3)
                    .build();
            sup2 = supplierRepository.save(sup2);

            // 3. Prime Industrial Polymers (POOR - Sharp Decline / Critical Risk)
            Supplier sup3 = Supplier.builder()
                    .supplierCode("SUP-10003")
                    .name("Prime Industrial Polymers")
                    .contactPerson("Elena Rostova")
                    .email("sales@primepolymers.com")
                    .phone("+49 30 5550199")
                    .address("Industriestrasse 42")
                    .city("Berlin")
                    .country("Germany")
                    .category(catRaw)
                    .status(SupplierStatus.ACTIVE)
                    .overallRating(55.8)
                    .ratingCategory(RatingCategory.AVERAGE)
                    .totalEvaluations(3)
                    .build();
            sup3 = supplierRepository.save(sup3);

            // 4. CloudScale Infrastructure Inc. (EXCELLENT - High Performer)
            Supplier sup4 = Supplier.builder()
                    .supplierCode("SUP-10004")
                    .name("CloudScale Infrastructure Inc.")
                    .contactPerson("Liam Thorne")
                    .email("support@cloudscale.io")
                    .phone("+1 206-555-0177")
                    .address("701 Pike St")
                    .city("Seattle")
                    .country("United States")
                    .category(catIt)
                    .status(SupplierStatus.ACTIVE)
                    .overallRating(94.0)
                    .ratingCategory(RatingCategory.EXCELLENT)
                    .totalEvaluations(3)
                    .build();
            sup4 = supplierRepository.save(sup4);

            // 5. EcoBox Logistics Packaging (AVERAGE - Moderate Decline)
            Supplier sup5 = Supplier.builder()
                    .supplierCode("SUP-10005")
                    .name("EcoBox Logistics Packaging")
                    .contactPerson("Hannah Abbott")
                    .email("contact@ecoboxpack.com")
                    .phone("+44 20 7946 0912")
                    .address("15 Canal Reach")
                    .city("London")
                    .country("United Kingdom")
                    .category(catPack)
                    .status(SupplierStatus.ACTIVE)
                    .overallRating(68.8)
                    .ratingCategory(RatingCategory.AVERAGE)
                    .totalEvaluations(3)
                    .build();
            sup5 = supplierRepository.save(sup5);

            // 6. Titan Steel & Metallurgy Corp. (HIGH RISK - Rapid Decline)
            Supplier sup6 = Supplier.builder()
                    .supplierCode("SUP-10006")
                    .name("Titan Steel & Metallurgy Corp.")
                    .contactPerson("Marcus Vance")
                    .email("inquiries@titansteel.com")
                    .phone("+1 412-555-0155")
                    .address("888 Foundry Lane")
                    .city("Pittsburgh")
                    .country("United States")
                    .category(catRaw)
                    .status(SupplierStatus.ACTIVE)
                    .overallRating(60.8)
                    .ratingCategory(RatingCategory.AVERAGE)
                    .totalEvaluations(3)
                    .build();
            sup6 = supplierRepository.save(sup6);

            // 7. Vertex Precision Instruments (VERY GOOD - Improving)
            Supplier sup7 = Supplier.builder()
                    .supplierCode("SUP-10007")
                    .name("Vertex Precision Instruments")
                    .contactPerson("Clara Dupont")
                    .email("sales@vertexprecision.fr")
                    .phone("+33 1 40 55 01 22")
                    .address("24 Rue de la Paix")
                    .city("Paris")
                    .country("France")
                    .category(catElec)
                    .status(SupplierStatus.ACTIVE)
                    .overallRating(84.3)
                    .ratingCategory(RatingCategory.GOOD)
                    .totalEvaluations(3)
                    .build();
            sup7 = supplierRepository.save(sup7);

            // 8. Nexus Facility Services (GOOD - Early History)
            Supplier sup8 = Supplier.builder()
                    .supplierCode("SUP-10008")
                    .name("Nexus Facility Services")
                    .contactPerson("Robert Chen")
                    .email("support@nexusfacilities.com")
                    .phone("+1 617-555-0199")
                    .address("120 Beacon St")
                    .city("Boston")
                    .country("United States")
                    .category(catPack)
                    .status(SupplierStatus.ACTIVE)
                    .overallRating(74.0)
                    .ratingCategory(RatingCategory.GOOD)
                    .totalEvaluations(2)
                    .build();
            sup8 = supplierRepository.save(sup8);

            // Multi-quarter Evaluations Seeding (Q2 2025 through Q1 2026)
            if (manager != null && criteriaList.size() >= 5) {
                // Supplier 1: Apex Microelectronics (4 quarters: 88.5 -> 91.0 -> 93.5 -> 95.0)
                createEvaluationRecord(sup1, manager, "EV-202506-1001", "Q2 2025", LocalDate.of(2025, 6, 15),
                        90, 88, 85, 92, 90,
                        "Initial qualification audit. Solid technical foundation with minor yield fluctuations.",
                        "Clean room standards exceed ISO-14644 Class 5 requirements.",
                        "Initial batch lead times slightly longer than estimated.",
                        "Approve vendor for standard production ramp.", criteriaList);

                createEvaluationRecord(sup1, manager, "EV-202509-1002", "Q3 2025", LocalDate.of(2025, 9, 20),
                        92, 90, 88, 94, 92,
                        "Q3 volume production review. Strong delivery performance and zero defect lots.",
                        "Automated optical inspection (AOI) implemented across all wafer lines.",
                        "Expedite air freight tracking during international customs handoffs.",
                        "Expand volume allocation by 15%.", criteriaList);

                createEvaluationRecord(sup1, manager, "EV-202512-1003", "Q4 2025", LocalDate.of(2025, 12, 18),
                        95, 93, 90, 96, 95,
                        "Year-end operational review. Outstanding quality consistency and responsive engineering.",
                        "Defect PPM dropped below 5 PPM across 100k units delivered.",
                        "Explore domestic backup inventory warehousing.",
                        "Award preferred Tier 1 status.", criteriaList);

                createEvaluationRecord(sup1, manager, "EV-202603-1004", "Q1 2026", LocalDate.of(2026, 3, 10),
                        96, 95, 92, 98, 96,
                        "Q1 2026 benchmark assessment. Highest performing electronics vendor in portfolio.",
                        "Six-Sigma quality metrics maintained throughout peak demand cycle.",
                        "None. Excellent cross-functional collaboration.",
                        "Execute multi-year strategic supply agreement.", criteriaList);

                // Supplier 2: Vanguard Global Freight (3 quarters: 76.5 -> 79.0 -> 78.5)
                createEvaluationRecord(sup2, manager, "EV-202506-2001", "Q2 2025", LocalDate.of(2025, 6, 20),
                        78, 80, 75, 72, 75,
                        "Quarterly freight carrier audit. Dependable regional distribution.",
                        "92% on-time delivery across standard domestic routes.",
                        "EDI status update latency occasionally exceeds 4 hours.",
                        "Implement automated GPS webhook integrations.", criteriaList);

                createEvaluationRecord(sup2, manager, "EV-202509-2002", "Q3 2025", LocalDate.of(2025, 9, 25),
                        80, 82, 78, 75, 78,
                        "Q3 transit performance review. API integration completed successfully.",
                        "Real-time shipment visibility significantly improved dispatch scheduling.",
                        "Detention charge disputes require faster reconciliation.",
                        "Maintain current freight lane allocations.", criteriaList);

                createEvaluationRecord(sup2, manager, "EV-202512-2003", "Q4 2025", LocalDate.of(2025, 12, 22),
                        80, 80, 78, 76, 78,
                        "Holiday peak season review. Capacity held steady despite bad weather.",
                        "High driver availability and dedicated account manager support.",
                        "Fuel surcharge adjustment calculations need clearer documentation.",
                        "Renew annual carrier master service agreement.", criteriaList);

                // Supplier 3: Prime Industrial Polymers (3 quarters: 68.0 -> 55.0 -> 44.5 - Sharp Decline)
                createEvaluationRecord(sup3, manager, "EV-202506-3001", "Q2 2025", LocalDate.of(2025, 6, 28),
                        70, 72, 70, 65, 60,
                        "Initial polymer supply review. Acceptable baseline performance.",
                        "Competitive unit pricing on bulk resin orders.",
                        "Packaging moisture barriers need reinforcement.",
                        "Continue bi-monthly quality sampling.", criteriaList);

                createEvaluationRecord(sup3, manager, "EV-202509-3002", "Q3 2025", LocalDate.of(2025, 9, 28),
                        55, 60, 62, 50, 45,
                        "Q3 audit triggered by incoming material non-conformance reports.",
                        "Immediate response to emergency inquiry.",
                        "12% batch rejection rate due to chemical viscosity out of spec.",
                        "Issue Level 1 Corrective Action Plan (CAP).", criteriaList);

                createEvaluationRecord(sup3, manager, "EV-202512-3003", "Q4 2025", LocalDate.of(2025, 12, 29),
                        40, 48, 52, 45, 42,
                        "Critical failure audit. Repeated non-compliance with REACH environmental standards.",
                        "None demonstrated during this period.",
                        "Unacceptable defect rates and unannounced manufacturing line relocation.",
                        "Freeze all purchase orders. Initiate formal supplier offboarding review.", criteriaList);

                // Supplier 4: CloudScale Infrastructure Inc. (3 quarters: 91.5 -> 94.0 -> 96.5)
                createEvaluationRecord(sup4, manager, "EV-202506-4001", "Q2 2025", LocalDate.of(2025, 6, 12),
                        92, 94, 88, 92, 92,
                        "Q2 cloud SLA audit. High availability and excellent compute throughput.",
                        "99.99% uptime achieved across all provisioned clusters.",
                        "Billing portal granularity could be enhanced for sub-department tagging.",
                        "Maintain enterprise support tier.", criteriaList);

                createEvaluationRecord(sup4, manager, "EV-202509-4002", "Q3 2025", LocalDate.of(2025, 9, 15),
                        95, 96, 90, 95, 95,
                        "SOC-2 Type II audit verification and disaster recovery drill.",
                        "Zero RPO/RTO downtime recorded during simulated failover test.",
                        "None.",
                        "Authorize deployment of mission-critical databases to CloudScale VPC.", criteriaList);

                createEvaluationRecord(sup4, manager, "EV-202512-4003", "Q4 2025", LocalDate.of(2025, 12, 12),
                        98, 98, 92, 98, 98,
                        "Annual cloud infrastructure review. Flawless security and scalability.",
                        "Proactive DDoS mitigation prevented multiple service interruptions.",
                        "None.",
                        "Upgrade contract to Tier 1 Enterprise Partner.", criteriaList);

                // Supplier 5: EcoBox Logistics Packaging (3 quarters: 72.0 -> 68.5 -> 66.0)
                createEvaluationRecord(sup5, manager, "EV-202506-5001", "Q2 2025", LocalDate.of(2025, 6, 18),
                        75, 72, 74, 68, 70,
                        "Cardboard packaging quality inspection. Standard commercial grade.",
                        "100% recycled fiber certification verified.",
                        "Box edge compression strength inconsistent under humid storage.",
                        "Require moisture-resistant liner upgrade.", criteriaList);

                createEvaluationRecord(sup5, manager, "EV-202509-5002", "Q3 2025", LocalDate.of(2025, 9, 22),
                        70, 68, 72, 65, 66,
                        "Q3 warehouse pallet audit. Some crushed boxes observed during stacking.",
                        "Eco-friendly inks and biodegradable tape compliance.",
                        "Packaging deformation caused minor product damage in 3 shipments.",
                        "Issue requirement for ECT-32 test certification before next batch.", criteriaList);

                createEvaluationRecord(sup5, manager, "EV-202512-5003", "Q4 2025", LocalDate.of(2025, 12, 19),
                        68, 65, 70, 62, 64,
                        "Year-end packaging review. Continued degradation in box durability.",
                        "Flexible order lot sizes.",
                        "Failure to meet minimum bursting test threshold.",
                        "Place vendor on 60-day performance probation.", criteriaList);

                // Supplier 6: Titan Steel & Metallurgy (3 quarters: 74.0 -> 60.0 -> 48.5)
                createEvaluationRecord(sup6, manager, "EV-202506-6001", "Q2 2025", LocalDate.of(2025, 6, 24),
                        76, 75, 78, 70, 70,
                        "Initial foundry audit for structural steel billets.",
                        "High tensile strength and mill test reports provided on time.",
                        "Surface oxidation present on outdoor storage billets.",
                        "Require covered tarping for all rail shipments.", criteriaList);

                createEvaluationRecord(sup6, manager, "EV-202509-6002", "Q3 2025", LocalDate.of(2025, 9, 26),
                        62, 60, 65, 55, 55,
                        "Q3 metallurgical test review. Significant drop in alloy consistency.",
                        "Low bulk spot pricing.",
                        "Carbon content variance caused 4 machine tool breakages.",
                        "Issue mandatory root cause analysis and supplier improvement action.", criteriaList);

                createEvaluationRecord(sup6, manager, "EV-202512-6003", "Q4 2025", LocalDate.of(2025, 12, 28),
                        50, 48, 55, 45, 42,
                        "Emergency re-evaluation. Delayed deliveries and unresolved QA defects.",
                        "None.",
                        "Failure to provide Spectrometer test certificates with raw shipments.",
                        "Restrict vendor to non-critical auxiliary fabrications only.", criteriaList);

                // Supplier 7: Vertex Precision Instruments (3 quarters: 81.0 -> 84.5 -> 87.5)
                createEvaluationRecord(sup7, manager, "EV-202506-7001", "Q2 2025", LocalDate.of(2025, 6, 10),
                        82, 80, 80, 82, 82,
                        "Initial precision tooling and calibration gauge audit.",
                        "High dimensional accuracy (+/- 2 microns).",
                        "Calibration certificate documentation format needs alignment with internal ERP.",
                        "Approve vendor with standard 12-month re-certification cycle.", criteriaList);

                createEvaluationRecord(sup7, manager, "EV-202509-7002", "Q3 2025", LocalDate.of(2025, 9, 14),
                        86, 84, 82, 85, 86,
                        "Q3 tooling performance review. Micro-measurement accuracy confirmed.",
                        "Digital calibration certificate barcode integration completed.",
                        "Minor lead time extension on custom CNC tooling inserts.",
                        "Increase order volume for high-precision manufacturing lines.", criteriaList);

                createEvaluationRecord(sup7, manager, "EV-202512-7003", "Q4 2025", LocalDate.of(2025, 12, 15),
                        89, 88, 84, 88, 89,
                        "Q4 operational review. Superb instrument durability and zero field failures.",
                        "Extended warranty and rapid calibration recalibration service turnaround.",
                        "None.",
                        "Award preferred precision tooling supplier status.", criteriaList);

                // Supplier 8: Nexus Facility Services (2 quarters: 73.0 -> 75.0)
                createEvaluationRecord(sup8, manager, "EV-202509-8001", "Q3 2025", LocalDate.of(2025, 9, 10),
                        74, 72, 75, 70, 75,
                        "Q3 facility management and janitorial contractor evaluation.",
                        "Strict adherence to OSHA chemical handling protocols.",
                        "Weekend emergency response times need improvement.",
                        "Maintain contract; review on-call SLA staffing.", criteriaList);

                createEvaluationRecord(sup8, manager, "EV-202512-8002", "Q4 2025", LocalDate.of(2025, 12, 10),
                        76, 75, 76, 72, 77,
                        "Year-end facility contract review. Improved weekend coverage.",
                        "Zero safety incidents recorded across 365 operating days.",
                        "Supply room inventory restocking delays.",
                        "Renew annual facility services agreement.", criteriaList);
            }

            logger.info("Initialized 8 sample suppliers with 22 detailed multi-quarter evaluations and rating histories.");

            // Seed Notifications
            if (notificationRepository != null && notificationRepository.count() == 0) {
                if (admin != null) {
                    Notification n1 = Notification.builder()
                            .user(admin)
                            .title("Early Warning: High Risk Vendor Detected")
                            .message("Prime Industrial Polymers (SUP-10003) score fell below 50% threshold. Immediate remediation required.")
                            .notificationType(NotificationType.ALERT)
                            .priority(NotificationPriority.CRITICAL)
                            .relatedResourceType("SUPPLIER")
                            .relatedResourceId(sup3.getId())
                            .isRead(false)
                            .build();

                    Notification n2 = Notification.builder()
                            .user(admin)
                            .title("Quarterly Evaluation Completed")
                            .message("Evaluation EV-202603-1004 completed for Apex Microelectronics Inc. (Score: 95.0%).")
                            .notificationType(NotificationType.EVALUATION)
                            .priority(NotificationPriority.MEDIUM)
                            .relatedResourceType("EVALUATION")
                            .relatedResourceId(1L)
                            .isRead(false)
                            .build();

                    Notification n3 = Notification.builder()
                            .user(admin)
                            .title("Sharp Performance Drop Detected")
                            .message("Titan Steel & Metallurgy Corp. (SUP-10006) dropped by 11.5 points in Q4 2025 review.")
                            .notificationType(NotificationType.ALERT)
                            .priority(NotificationPriority.HIGH)
                            .relatedResourceType("SUPPLIER")
                            .relatedResourceId(sup6.getId())
                            .isRead(false)
                            .build();

                    notificationRepository.saveAll(List.of(n1, n2, n3));
                }

                if (manager != null) {
                    Notification n4 = Notification.builder()
                            .user(manager)
                            .title("Action Item Assigned: Quality Audit")
                            .message("You have been assigned to conduct an On-Site Quality Audit for Prime Industrial Polymers.")
                            .notificationType(NotificationType.IMPROVEMENT_ACTION)
                            .priority(NotificationPriority.HIGH)
                            .relatedResourceType("SUPPLIER")
                            .relatedResourceId(sup3.getId())
                            .isRead(false)
                            .build();

                    Notification n5 = Notification.builder()
                            .user(manager)
                            .title("AI Intelligence Velocity Report")
                            .message("Apex Microelectronics trajectory velocity forecasted at +2.1 pts/cycle with HIGH confidence.")
                            .notificationType(NotificationType.AI_INSIGHT)
                            .priority(NotificationPriority.LOW)
                            .relatedResourceType("SUPPLIER")
                            .relatedResourceId(sup1.getId())
                            .isRead(true)
                            .build();

                    notificationRepository.saveAll(List.of(n4, n5));
                }
                logger.info("Initialized sample notifications for Phase 12.");
            }

            // Seed Supplier Improvement Actions (CAP)
            if (improvementActionRepository != null && improvementActionRepository.count() == 0) {
                if (admin != null) {
                    SupplierImprovementAction action1 = SupplierImprovementAction.builder()
                            .supplier(sup3)
                            .title("Mandatory Polymer Viscosity Audit & Root Cause Analysis")
                            .description("Conduct on-site chemical engineering audit and require ISO-9001 corrective action plan following Q4 resin impurity failure.")
                            .priority(ImprovementActionPriority.CRITICAL)
                            .status(ImprovementActionStatus.IN_PROGRESS)
                            .assignedUser(manager != null ? manager : admin)
                            .createdByUser(admin)
                            .dueDate(LocalDate.now().plusWeeks(2))
                            .resolutionNotes("Audit scheduled for next Monday with vendor QA director.")
                            .build();

                    SupplierImprovementAction action2 = SupplierImprovementAction.builder()
                            .supplier(sup6)
                            .title("Raw Steel Metallurgical Purity Verification")
                            .description("Implement mandatory Spectrometer chemical composition batch certification prior to rail dispatch.")
                            .priority(ImprovementActionPriority.HIGH)
                            .status(ImprovementActionStatus.OPEN)
                            .assignedUser(admin)
                            .createdByUser(admin)
                            .dueDate(LocalDate.now().plusWeeks(3))
                            .build();

                    SupplierImprovementAction action3 = SupplierImprovementAction.builder()
                            .supplier(sup5)
                            .title("Cardboard Box Edge Crush Test (ECT) Certification")
                            .description("Upgrade packaging material liner moisture barrier and verify ECT-32 compression strength compliance.")
                            .priority(ImprovementActionPriority.MEDIUM)
                            .status(ImprovementActionStatus.COMPLETED)
                            .assignedUser(manager != null ? manager : admin)
                            .createdByUser(admin)
                            .dueDate(LocalDate.now().minusDays(5))
                            .completedAt(LocalDate.now().minusDays(2).atTime(14, 30))
                            .resolutionNotes("Passed laboratory compression stress test with 38 lbs/in rating.")
                            .build();

                    SupplierImprovementAction action4 = SupplierImprovementAction.builder()
                            .supplier(sup2)
                            .title("Logistics Lead-Time EDI SLA Review")
                            .description("Review transit milestone latency and establish automatic webhook status updates for air freight.")
                            .priority(ImprovementActionPriority.MEDIUM)
                            .status(ImprovementActionStatus.OPEN)
                            .assignedUser(manager != null ? manager : admin)
                            .createdByUser(admin)
                            .dueDate(LocalDate.now().plusDays(18))
                            .build();

                    improvementActionRepository.saveAll(List.of(action1, action2, action3, action4));
                    logger.info("Initialized sample supplier improvement actions for Phase 12.");
                }
            }
        }
    }

    private SupplierEvaluation createEvaluationRecord(
            Supplier supplier,
            User evaluator,
            String evalCode,
            String period,
            LocalDate date,
            double qualScore,
            double delvScore,
            double pricScore,
            double commScore,
            double compScore,
            String comments,
            String strengths,
            String areasForImprovement,
            String recommendation,
            List<EvaluationCriteria> criteriaList) {

        double totalWeighted = (qualScore * 0.30) + (delvScore * 0.25) + (pricScore * 0.20) + (commScore * 0.15) + (compScore * 0.10);
        java.math.BigDecimal bd = java.math.BigDecimal.valueOf(totalWeighted).setScale(1, java.math.RoundingMode.HALF_UP);
        double finalScore = bd.doubleValue();

        RatingCategory category;
        SupplierRating sRating;
        PerformanceStatus pStatus;

        if (finalScore >= 90.0) {
            category = RatingCategory.EXCELLENT;
            sRating = SupplierRating.EXCELLENT;
            pStatus = PerformanceStatus.HIGH_PERFORMING;
        } else if (finalScore >= 80.0) {
            category = RatingCategory.GOOD;
            sRating = SupplierRating.VERY_GOOD;
            pStatus = PerformanceStatus.HIGH_PERFORMING;
        } else if (finalScore >= 70.0) {
            category = RatingCategory.GOOD;
            sRating = SupplierRating.GOOD;
            pStatus = PerformanceStatus.SATISFACTORY;
        } else if (finalScore >= 50.0) {
            category = RatingCategory.AVERAGE;
            sRating = SupplierRating.AVERAGE;
            pStatus = PerformanceStatus.NEEDS_IMPROVEMENT;
        } else {
            category = RatingCategory.POOR;
            sRating = SupplierRating.POOR;
            pStatus = PerformanceStatus.LOW_PERFORMING;
        }

        SupplierEvaluation eval = SupplierEvaluation.builder()
                .evaluationCode(evalCode)
                .supplier(supplier)
                .evaluator(evaluator)
                .evaluationDate(date)
                .evaluationPeriod(period)
                .totalWeightedScore(finalScore)
                .ratingCategory(category)
                .status(EvaluationStatus.COMPLETED)
                .generalComments(comments)
                .strengths(strengths)
                .areasForImprovement(areasForImprovement)
                .recommendation(recommendation)
                .scores(new ArrayList<>())
                .build();

        double[] rawScores = {qualScore, delvScore, pricScore, commScore, compScore};
        for (int i = 0; i < Math.min(5, criteriaList.size()); i++) {
            EvaluationCriteria c = criteriaList.get(i);
            double obtained = rawScores[i];
            double weighted = (obtained / c.getMaxScore()) * c.getWeight();
            EvaluationScore es = EvaluationScore.builder()
                    .evaluation(eval)
                    .criteria(c)
                    .scoreObtained(obtained)
                    .maxScore(c.getMaxScore())
                    .weight(c.getWeight())
                    .weightedScore(java.math.BigDecimal.valueOf(weighted).setScale(2, java.math.RoundingMode.HALF_UP).doubleValue())
                    .remarks("Verified against " + period + " audit logs.")
                    .build();
            eval.addScore(es);
        }

        SupplierEvaluation saved = evaluationRepository.save(eval);

        if (performanceRatingRepository != null) {
            SupplierPerformanceRating spr = SupplierPerformanceRating.builder()
                    .supplier(supplier)
                    .evaluation(saved)
                    .score(finalScore)
                    .rating(sRating)
                    .performanceStatus(pStatus)
                    .ratingDate(date)
                    .build();
            performanceRatingRepository.save(spr);
        }

        return saved;
    }

    private void initSupplierPortalData() {
        Role supplierRole = roleRepository.findByName(ERole.ROLE_SUPPLIER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(ERole.ROLE_SUPPLIER).build()));

        List<Supplier> suppliers = supplierRepository.findAll();
        if (suppliers.isEmpty()) return;

        Supplier apexSupplier = suppliers.stream().filter(s -> s.getSupplierCode().equals("SUP-10001")).findFirst().orElse(suppliers.get(0));
        Supplier nexusSupplier = suppliers.stream().filter(s -> s.getSupplierCode().equals("SUP-10002") || s.getSupplierCode().equals("SUP-10004")).findFirst().orElse(suppliers.size() > 1 ? suppliers.get(1) : suppliers.get(0));

        // 1. Create Demo Supplier User: supplier_apex (Apex Microelectronics)
        User userApex = null;
        if (!userRepository.existsByUsername("supplier_apex")) {
            userApex = User.builder()
                    .username("supplier_apex")
                    .email("orders@apexmicro.com")
                    .password(passwordEncoder.encode("Supplier@12345"))
                    .fullName("Sarah Jenkins (Apex Portal)")
                    .phone("+1 408-555-0144")
                    .department("Supplier Account Operations")
                    .active(true)
                    .roles(Set.of(supplierRole))
                    .supplier(apexSupplier)
                    .build();
            userApex = userRepository.save(userApex);
            logger.info("Initialized demo supplier user: supplier_apex / Supplier@12345 (Apex Microelectronics)");
        } else {
            userApex = userRepository.findByUsername("supplier_apex").orElse(null);
        }

        // 2. Create Demo Supplier User: supplier_nexus (Nexus Technologies)
        if (!userRepository.existsByUsername("supplier_nexus")) {
            User userNexus = User.builder()
                    .username("supplier_nexus")
                    .email("contact@nexusglobal.com")
                    .password(passwordEncoder.encode("Supplier@12345"))
                    .fullName("David Miller (Nexus Portal)")
                    .phone("+1 312-555-0188")
                    .department("Supplier Account Operations")
                    .active(true)
                    .roles(Set.of(supplierRole))
                    .supplier(nexusSupplier)
                    .build();
            userRepository.save(userNexus);
            logger.info("Initialized demo supplier user: supplier_nexus / Supplier@12345 (Nexus Global)");
        }

        // 3. Seed Sample Documents
        if (documentRepository != null && documentRepository.count() == 0 && userApex != null) {
            SupplierDocument doc1 = SupplierDocument.builder()
                    .supplier(apexSupplier)
                    .documentName("ISO-9001-2015-Certificate-Apex.pdf")
                    .documentType("Quality Certification")
                    .storedFileName("iso9001_apex_cert_2026.pdf")
                    .contentType("application/pdf")
                    .fileSize(2458000L)
                    .uploadedBy(userApex)
                    .status(DocumentStatus.ACTIVE)
                    .notes("Annual ISO 9001 Quality Management System renewal certificate.")
                    .build();

            SupplierDocument doc2 = SupplierDocument.builder()
                    .supplier(apexSupplier)
                    .documentName("RoHS-REACH-Compliance-2026.pdf")
                    .documentType("Environmental & RoHS")
                    .storedFileName("rohs_reach_apex_compliance.pdf")
                    .contentType("application/pdf")
                    .fileSize(1850000L)
                    .uploadedBy(userApex)
                    .status(DocumentStatus.ACTIVE)
                    .notes("Semiconductor lead-free and hazardous substances conformity report.")
                    .build();

            SupplierDocument doc3 = SupplierDocument.builder()
                    .supplier(nexusSupplier)
                    .documentName("Commercial-General-Liability-Insurance.pdf")
                    .documentType("Insurance Policy")
                    .storedFileName("insurance_liability_nexus_2026.pdf")
                    .contentType("application/pdf")
                    .fileSize(3120000L)
                    .uploadedBy(userApex)
                    .status(DocumentStatus.ACTIVE)
                    .notes("Comprehensive $10M liability coverage policy.")
                    .build();

            documentRepository.saveAll(List.of(doc1, doc2, doc3));
            logger.info("Initialized sample supplier documents for Phase 13.");
        }

        // 4. Seed Sample Profile Update Request
        if (profileUpdateRequestRepository != null && profileUpdateRequestRepository.count() == 0 && userApex != null) {
            SupplierProfileUpdateRequest req1 = SupplierProfileUpdateRequest.builder()
                    .supplier(apexSupplier)
                    .requestedBy(userApex)
                    .contactPerson("Sarah Jenkins-Ramirez")
                    .phone("+1 408-555-0199")
                    .email("compliance@apexmicro.com")
                    .address("100 Silicon Way, Building B, 4th Floor")
                    .website("https://apexmicro.example.com")
                    .city("San Jose")
                    .state("California")
                    .country("United States")
                    .status(UpdateRequestStatus.PENDING)
                    .build();

            profileUpdateRequestRepository.save(req1);
            logger.info("Initialized sample supplier profile update request for Phase 13.");
        }

        // 5. Seed Sample Communications
        if (communicationRepository != null && communicationRepository.count() == 0 && userApex != null) {
            User manager = userRepository.findByUsername("manager").orElse(userApex);

            SupplierCommunication comm1 = SupplierCommunication.builder()
                    .supplier(apexSupplier)
                    .sender(userApex)
                    .subject("Q1 Production Capacity Expansion Notice")
                    .message("We have commissioned our second cleanroom line, expanding quarterly wafer throughput by 35%. Let us know if your procurement forecast requires higher allocation.")
                    .relatedResourceType("EVALUATION")
                    .relatedResourceId(1L)
                    .isFromSupplier(true)
                    .build();

            SupplierCommunication comm2 = SupplierCommunication.builder()
                    .supplier(apexSupplier)
                    .sender(manager)
                    .subject("RE: Q1 Production Capacity Expansion Notice")
                    .message("Excellent news Sarah. We have noted this in your vendor scorecard and will coordinate with supply planning for Q2 allocations.")
                    .relatedResourceType("EVALUATION")
                    .relatedResourceId(1L)
                    .isFromSupplier(false)
                    .build();

            communicationRepository.saveAll(List.of(comm1, comm2));
            logger.info("Initialized sample supplier communication thread for Phase 13.");
        }
    }

    private void initDefaultWorkflowDefinitions() {
        if (workflowDefinitionRepository == null || workflowDefinitionRepository.count() > 0) {
            return;
        }

        List<WorkflowDefinition> definitions = new ArrayList<>();

        // 1. Supplier Profile Update Workflow (2 Steps)
        WorkflowDefinition defProfile = WorkflowDefinition.builder()
                .name("Supplier Profile Update Review")
                .workflowType(WorkflowType.SUPPLIER_PROFILE_UPDATE)
                .description("Multi-tier review process for vendor profile changes, contact info modifications, and tax registrations.")
                .active(true)
                .build();
        defProfile.addStep(WorkflowStep.builder()
                .stepOrder(1)
                .stepName("Procurement Manager Review")
                .requiredRole(ERole.ROLE_MANAGER)
                .slaHours(24)
                .escalationRole(ERole.ROLE_ADMIN)
                .autoEscalate(true)
                .instructions("Verify submitted company credentials, tax numbers, and contact details.")
                .build());
        defProfile.addStep(WorkflowStep.builder()
                .stepOrder(2)
                .stepName("Administrator Final Approval")
                .requiredRole(ERole.ROLE_ADMIN)
                .slaHours(48)
                .escalationRole(ERole.ROLE_ADMIN)
                .autoEscalate(false)
                .instructions("Authorize official updates to ERP vendor master catalog.")
                .build());
        definitions.add(defProfile);

        // 2. Supplier Document Review Workflow (1 Step)
        WorkflowDefinition defDoc = WorkflowDefinition.builder()
                .name("Compliance Document Verification")
                .workflowType(WorkflowType.SUPPLIER_DOCUMENT_REVIEW)
                .description("Review and validation of uploaded vendor compliance certificates, ISO audits, and insurance policies.")
                .active(true)
                .build();
        defDoc.addStep(WorkflowStep.builder()
                .stepOrder(1)
                .stepName("Compliance Specialist Review")
                .requiredRole(ERole.ROLE_MANAGER)
                .slaHours(48)
                .escalationRole(ERole.ROLE_ADMIN)
                .autoEscalate(true)
                .instructions("Inspect certificate expiration date, insurance liability limits, and issuing accreditation bodies.")
                .build());
        definitions.add(defDoc);

        // 3. Evaluation Approval Workflow (2 Steps)
        WorkflowDefinition defEval = WorkflowDefinition.builder()
                .name("Supplier Evaluation Sign-Off")
                .workflowType(WorkflowType.EVALUATION_APPROVAL)
                .description("Formal sign-off hierarchy for completed supplier performance evaluation scorecards.")
                .active(true)
                .build();
        defEval.addStep(WorkflowStep.builder()
                .stepOrder(1)
                .stepName("Category Lead Assessment")
                .requiredRole(ERole.ROLE_MANAGER)
                .slaHours(24)
                .escalationRole(ERole.ROLE_ADMIN)
                .autoEscalate(true)
                .instructions("Review criterion scoring weights and qualitative evaluator notes.")
                .build());
        defEval.addStep(WorkflowStep.builder()
                .stepOrder(2)
                .stepName("Procurement Director Endorsement")
                .requiredRole(ERole.ROLE_ADMIN)
                .slaHours(24)
                .escalationRole(ERole.ROLE_ADMIN)
                .autoEscalate(true)
                .instructions("Authorize publication of vendor rating scorecard and tier classification.")
                .build());
        definitions.add(defEval);

        // 4. Improvement Action Closure Workflow (2 Steps)
        WorkflowDefinition defCap = WorkflowDefinition.builder()
                .name("Corrective Action Plan Closure")
                .workflowType(WorkflowType.IMPROVEMENT_ACTION_CLOSURE)
                .description("Verification and closure sign-off for supplier corrective action plans (CAP).")
                .active(true)
                .build();
        defCap.addStep(WorkflowStep.builder()
                .stepOrder(1)
                .stepName("QA Evidence Verification")
                .requiredRole(ERole.ROLE_MANAGER)
                .slaHours(48)
                .escalationRole(ERole.ROLE_ADMIN)
                .autoEscalate(true)
                .instructions("Validate audit resolution evidence, test batch records, and root-cause analysis.")
                .build());
        defCap.addStep(WorkflowStep.builder()
                .stepOrder(2)
                .stepName("Supply Chain Operations Sign-Off")
                .requiredRole(ERole.ROLE_ADMIN)
                .slaHours(24)
                .escalationRole(ERole.ROLE_ADMIN)
                .autoEscalate(false)
                .instructions("Approve formal CAP closure and lift any provisional procurement constraints.")
                .build());
        definitions.add(defCap);

        // 5. Supplier Status Change Workflow (2 Steps)
        WorkflowDefinition defStatus = WorkflowDefinition.builder()
                .name("Supplier Status Transition Approval")
                .workflowType(WorkflowType.SUPPLIER_STATUS_CHANGE)
                .description("Governance workflow for onboarding, suspending, or blacklisting suppliers.")
                .active(true)
                .build();
        defStatus.addStep(WorkflowStep.builder()
                .stepOrder(1)
                .stepName("Sourcing Manager Review")
                .requiredRole(ERole.ROLE_MANAGER)
                .slaHours(24)
                .escalationRole(ERole.ROLE_ADMIN)
                .autoEscalate(true)
                .instructions("Evaluate contract impact, open purchase orders, and replacement supplier readiness.")
                .build());
        defStatus.addStep(WorkflowStep.builder()
                .stepOrder(2)
                .stepName("Executive Committee Sign-Off")
                .requiredRole(ERole.ROLE_ADMIN)
                .slaHours(48)
                .escalationRole(ERole.ROLE_ADMIN)
                .autoEscalate(false)
                .instructions("Authorize formal supplier status modification in all downstream enterprise systems.")
                .build());
        definitions.add(defStatus);

        workflowDefinitionRepository.saveAll(definitions);
        logger.info("Initialized {} default workflow definitions with multi-level approval steps.", definitions.size());
    }

    private void initSampleWorkflowInstances() {
        if (workflowInstanceRepository == null || workflowInstanceRepository.count() > 0) {
            return;
        }

        User manager = userRepository.findByUsername("manager").orElse(null);
        User admin = userRepository.findByUsername("admin").orElse(null);
        User userApex = userRepository.findByUsername("supplier_apex").orElse(admin != null ? admin : manager);

        if (manager == null || admin == null || userApex == null) {
            return;
        }

        WorkflowDefinition profileDef = workflowDefinitionRepository.findByWorkflowTypeAndActiveTrue(WorkflowType.SUPPLIER_PROFILE_UPDATE).orElse(null);
        WorkflowDefinition docDef = workflowDefinitionRepository.findByWorkflowTypeAndActiveTrue(WorkflowType.SUPPLIER_DOCUMENT_REVIEW).orElse(null);
        WorkflowDefinition capDef = workflowDefinitionRepository.findByWorkflowTypeAndActiveTrue(WorkflowType.IMPROVEMENT_ACTION_CLOSURE).orElse(null);

        if (profileDef == null || docDef == null || capDef == null) {
            return;
        }

        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // 1. In-Progress Profile Update Workflow (Apex Microelectronics)
        WorkflowInstance inst1 = WorkflowInstance.builder()
                .workflowDefinition(profileDef)
                .workflowType(WorkflowType.SUPPLIER_PROFILE_UPDATE)
                .title("Profile Update: Apex Microelectronics Inc.")
                .relatedResourceType("PROFILE_UPDATE_REQUEST")
                .relatedResourceId(1L)
                .status(WorkflowStatus.IN_PROGRESS)
                .currentStepOrder(1)
                .totalSteps(2)
                .initiatedBy(userApex)
                .startedAt(now.minusHours(6))
                .metadata("{\"supplierCode\":\"SUP-001\",\"supplierName\":\"Apex Microelectronics Inc.\"}")
                .build();
        WorkflowInstance savedInst1 = workflowInstanceRepository.save(inst1);

        WorkflowStep step1_1 = profileDef.getSteps().stream().filter(s -> s.getStepOrder() == 1).findFirst().orElse(null);
        if (step1_1 != null) {
            ApprovalTask task1 = ApprovalTask.builder()
                    .workflowInstance(savedInst1)
                    .workflowStep(step1_1)
                    .stepOrder(1)
                    .stepName(step1_1.getStepName())
                    .requiredRole(ERole.ROLE_MANAGER)
                    .status(TaskStatus.PENDING)
                    .assignedAt(now.minusHours(6))
                    .dueAt(now.plusHours(18))
                    .build();
            approvalTaskRepository.save(task1);
            savedInst1.addTask(task1);

            WorkflowAuditLog log1_1 = WorkflowAuditLog.builder()
                    .workflowInstance(savedInst1)
                    .eventType("WORKFLOW_CREATED")
                    .performedBy(userApex)
                    .description("Workflow initiated by " + userApex.getFullName() + " (Supplier Profile Update)")
                    .timestamp(now.minusHours(6))
                    .build();
            WorkflowAuditLog log1_2 = WorkflowAuditLog.builder()
                    .workflowInstance(savedInst1)
                    .eventType("STEP_STARTED")
                    .performedBy(userApex)
                    .description("Step 1 started: " + step1_1.getStepName() + " assigned to ROLE_MANAGER")
                    .timestamp(now.minusHours(6))
                    .build();
            workflowAuditLogRepository.saveAll(List.of(log1_1, log1_2));
        }

        // 2. Approved Document Review Workflow (ISO 9001:2025)
        WorkflowInstance inst2 = WorkflowInstance.builder()
                .workflowDefinition(docDef)
                .workflowType(WorkflowType.SUPPLIER_DOCUMENT_REVIEW)
                .title("Document Review: ISO-9001-2025-Certificate.pdf (Apex Microelectronics Inc.)")
                .relatedResourceType("DOCUMENT")
                .relatedResourceId(1L)
                .status(WorkflowStatus.APPROVED)
                .currentStepOrder(1)
                .totalSteps(1)
                .initiatedBy(userApex)
                .startedAt(now.minusDays(2))
                .completedAt(now.minusDays(1))
                .metadata("{\"supplierCode\":\"SUP-001\",\"documentType\":\"ISO 9001 Quality Certificate\"}")
                .build();
        WorkflowInstance savedInst2 = workflowInstanceRepository.save(inst2);

        WorkflowStep step2_1 = docDef.getSteps().stream().filter(s -> s.getStepOrder() == 1).findFirst().orElse(null);
        if (step2_1 != null) {
            ApprovalTask task2 = ApprovalTask.builder()
                    .workflowInstance(savedInst2)
                    .workflowStep(step2_1)
                    .stepOrder(1)
                    .stepName(step2_1.getStepName())
                    .requiredRole(ERole.ROLE_MANAGER)
                    .actionedBy(manager)
                    .status(TaskStatus.APPROVED)
                    .comments("Accreditation verified with international registry. Valid through 2028.")
                    .assignedAt(now.minusDays(2))
                    .actionedAt(now.minusDays(1))
                    .dueAt(now.minusDays(1).plusHours(24))
                    .build();
            approvalTaskRepository.save(task2);
            savedInst2.addTask(task2);

            WorkflowAuditLog log2_1 = WorkflowAuditLog.builder()
                    .workflowInstance(savedInst2)
                    .eventType("WORKFLOW_CREATED")
                    .performedBy(userApex)
                    .description("Workflow initiated for compliance certificate review")
                    .timestamp(now.minusDays(2))
                    .build();
            WorkflowAuditLog log2_2 = WorkflowAuditLog.builder()
                    .workflowInstance(savedInst2)
                    .eventType("TASK_APPROVED")
                    .performedBy(manager)
                    .description("Step 1 approved by " + manager.getFullName() + ": Accreditation verified.")
                    .timestamp(now.minusDays(1))
                    .build();
            WorkflowAuditLog log2_3 = WorkflowAuditLog.builder()
                    .workflowInstance(savedInst2)
                    .eventType("WORKFLOW_COMPLETED")
                    .performedBy(manager)
                    .description("Workflow successfully approved and completed")
                    .timestamp(now.minusDays(1))
                    .build();
            workflowAuditLogRepository.saveAll(List.of(log2_1, log2_2, log2_3));
        }

        // 3. Escalated CAP Closure Workflow (CAP-2026-001)
        WorkflowInstance inst3 = WorkflowInstance.builder()
                .workflowDefinition(capDef)
                .workflowType(WorkflowType.IMPROVEMENT_ACTION_CLOSURE)
                .title("CAP Closure: CAP-2026-001 SMT Solder Void Reduction")
                .relatedResourceType("IMPROVEMENT_ACTION")
                .relatedResourceId(1L)
                .status(WorkflowStatus.ESCALATED)
                .currentStepOrder(1)
                .totalSteps(2)
                .initiatedBy(manager)
                .startedAt(now.minusHours(60))
                .metadata("{\"actionCode\":\"CAP-2026-001\",\"priority\":\"HIGH\"}")
                .build();
        WorkflowInstance savedInst3 = workflowInstanceRepository.save(inst3);

        WorkflowStep step3_1 = capDef.getSteps().stream().filter(s -> s.getStepOrder() == 1).findFirst().orElse(null);
        if (step3_1 != null) {
            ApprovalTask task3 = ApprovalTask.builder()
                    .workflowInstance(savedInst3)
                    .workflowStep(step3_1)
                    .stepOrder(1)
                    .stepName(step3_1.getStepName())
                    .requiredRole(ERole.ROLE_ADMIN) // Escalated to Admin
                    .status(TaskStatus.ESCALATED)
                    .assignedAt(now.minusHours(60))
                    .dueAt(now.minusHours(12)) // SLA breached 12h ago
                    .build();
            approvalTaskRepository.save(task3);
            savedInst3.addTask(task3);

            WorkflowEscalation esc3 = WorkflowEscalation.builder()
                    .workflowInstance(savedInst3)
                    .approvalTask(task3)
                    .escalatedFromRole(ERole.ROLE_MANAGER)
                    .escalatedToRole(ERole.ROLE_ADMIN)
                    .reason("SLA deadline of 48h exceeded by 12 hours without resolution.")
                    .escalationLevel(1)
                    .resolved(false)
                    .escalatedAt(now.minusHours(12))
                    .build();
            workflowEscalationRepository.save(esc3);

            WorkflowAuditLog log3_1 = WorkflowAuditLog.builder()
                    .workflowInstance(savedInst3)
                    .eventType("WORKFLOW_CREATED")
                    .performedBy(manager)
                    .description("Workflow initiated by " + manager.getFullName())
                    .timestamp(now.minusHours(60))
                    .build();
            WorkflowAuditLog log3_2 = WorkflowAuditLog.builder()
                    .workflowInstance(savedInst3)
                    .eventType("TASK_ESCALATED")
                    .performedBy(admin)
                    .description("Task escalated to ROLE_ADMIN due to SLA breach (12h overdue)")
                    .timestamp(now.minusHours(12))
                    .build();
            workflowAuditLogRepository.saveAll(List.of(log3_1, log3_2));
        }

        logger.info("Initialized 3 sample workflow instances across in-progress, approved, and escalated states.");
    }

    private void initDefaultKpiDefinitions() {
        if (kpiDefinitionRepository.count() > 0) {
            logger.info("KPI definitions already initialized. Skipping.");
            return;
        }

        List<KpiDefinition> kpis = List.of(
                KpiDefinition.builder()
                        .kpiCode("AVG_SUPPLIER_SCORE")
                        .name("Average Supplier Score")
                        .description("Mean overall evaluation score across all active suppliers")
                        .category(KpiCategory.PERFORMANCE)
                        .calculationType(KpiCalculationType.AVERAGE_SCORE)
                        .unit("POINTS")
                        .targetValue(85.0)
                        .warningThreshold(75.0)
                        .criticalThreshold(60.0)
                        .higherIsBetter(true)
                        .active(true)
                        .build(),

                KpiDefinition.builder()
                        .kpiCode("QUALITY_COMPLIANCE_RATE")
                        .name("Quality Compliance Rate")
                        .description("Average quality and defect compliance rating percentage")
                        .category(KpiCategory.QUALITY)
                        .calculationType(KpiCalculationType.PERCENTAGE)
                        .unit("%")
                        .targetValue(92.0)
                        .warningThreshold(80.0)
                        .criticalThreshold(70.0)
                        .higherIsBetter(true)
                        .active(true)
                        .build(),

                KpiDefinition.builder()
                        .kpiCode("ON_TIME_DELIVERY_RATE")
                        .name("On-Time Delivery Rate")
                        .description("Average delivery timeliness score percentage")
                        .category(KpiCategory.DELIVERY)
                        .calculationType(KpiCalculationType.PERCENTAGE)
                        .unit("%")
                        .targetValue(90.0)
                        .warningThreshold(80.0)
                        .criticalThreshold(65.0)
                        .higherIsBetter(true)
                        .active(true)
                        .build(),

                KpiDefinition.builder()
                        .kpiCode("CAP_COMPLETION_RATE")
                        .name("CAP Closure Rate")
                        .description("Percentage of corrective action plans successfully resolved")
                        .category(KpiCategory.OPERATIONS)
                        .calculationType(KpiCalculationType.PERCENTAGE)
                        .unit("%")
                        .targetValue(85.0)
                        .warningThreshold(70.0)
                        .criticalThreshold(50.0)
                        .higherIsBetter(true)
                        .active(true)
                        .build(),

                KpiDefinition.builder()
                        .kpiCode("HIGH_RISK_SUPPLIER_RATIO")
                        .name("High Risk Supplier Ratio")
                        .description("Proportion of active suppliers categorized with high or critical supply risk")
                        .category(KpiCategory.RISK)
                        .calculationType(KpiCalculationType.RATIO)
                        .unit("%")
                        .targetValue(5.0)
                        .warningThreshold(15.0)
                        .criticalThreshold(25.0)
                        .higherIsBetter(false)
                        .active(true)
                        .build(),

                KpiDefinition.builder()
                        .kpiCode("SLA_COMPLIANCE_RATE")
                        .name("Workflow SLA Compliance Rate")
                        .description("Percentage of workflow approval tasks completed within SLA deadlines")
                        .category(KpiCategory.GOVERNANCE)
                        .calculationType(KpiCalculationType.PERCENTAGE)
                        .unit("%")
                        .targetValue(95.0)
                        .warningThreshold(85.0)
                        .criticalThreshold(75.0)
                        .higherIsBetter(true)
                        .active(true)
                        .build(),

                KpiDefinition.builder()
                        .kpiCode("EVALUATION_COVERAGE_RATE")
                        .name("Supplier Evaluation Coverage")
                        .description("Percentage of active suppliers that have received at least one formal evaluation")
                        .category(KpiCategory.PERFORMANCE)
                        .calculationType(KpiCalculationType.PERCENTAGE)
                        .unit("%")
                        .targetValue(90.0)
                        .warningThreshold(75.0)
                        .criticalThreshold(50.0)
                        .higherIsBetter(true)
                        .active(true)
                        .build(),

                KpiDefinition.builder()
                        .kpiCode("TOP_TIER_SUPPLIER_RATIO")
                        .name("Top Tier Supplier Proportion")
                        .description("Percentage of suppliers holding EXCELLENT or GOOD performance status")
                        .category(KpiCategory.PERFORMANCE)
                        .calculationType(KpiCalculationType.RATIO)
                        .unit("%")
                        .targetValue(60.0)
                        .warningThreshold(40.0)
                        .criticalThreshold(20.0)
                        .higherIsBetter(true)
                        .active(true)
                        .build()
        );

        kpiDefinitionRepository.saveAll(kpis);
        logger.info("Initialized {} default enterprise KPI definitions.", kpis.size());
    }

    private void initSampleSavedReports() {
        if (savedReportRepository.count() > 0) {
            logger.info("Saved reports already initialized. Skipping.");
            return;
        }

        User admin = userRepository.findByUsername("admin").orElse(null);
        if (admin == null) return;

        List<SavedReport> sampleReports = List.of(
                SavedReport.builder()
                        .name("Executive Supplier Performance Matrix")
                        .description("Multi-attribute quarterly review of overall supplier ratings and quality conformance")
                        .reportType("SUPPLIER_PERFORMANCE")
                        .scope("ALL_SUPPLIERS")
                        .filters("{\"status\":\"ACTIVE\",\"ratingCategory\":\"ALL\"}")
                        .selectedMetrics("[\"AVG_SUPPLIER_SCORE\",\"QUALITY_COMPLIANCE_RATE\",\"ON_TIME_DELIVERY_RATE\"]")
                        .visualization("BAR_CHART")
                        .isPublic(true)
                        .createdBy(admin)
                        .build(),

                SavedReport.builder()
                        .name("Supply Chain Risk & CAP Governance Report")
                        .description("High-risk vendor identification and open corrective action plan closure velocity")
                        .reportType("RISK_INTELLIGENCE")
                        .scope("HIGH_RISK")
                        .filters("{\"riskLevel\":\"HIGH\",\"includeCap\":true}")
                        .selectedMetrics("[\"HIGH_RISK_SUPPLIER_RATIO\",\"CAP_COMPLETION_RATE\"]")
                        .visualization("TABLE")
                        .isPublic(true)
                        .createdBy(admin)
                        .build(),

                SavedReport.builder()
                        .name("Procurement Workflow SLA Governance")
                        .description("Audit of approval turnaround times and managerial SLA compliance rates")
                        .reportType("WORKFLOW_GOVERNANCE")
                        .scope("ALL_SUPPLIERS")
                        .filters("{\"workflowStatus\":\"ALL\"}")
                        .selectedMetrics("[\"SLA_COMPLIANCE_RATE\"]")
                        .visualization("PIE_CHART")
                        .isPublic(false)
                        .createdBy(admin)
                        .build()
        );

        savedReportRepository.saveAll(sampleReports);
        logger.info("Initialized {} sample saved report configurations.", sampleReports.size());
    }

    private void initSampleIntegrations() {
        if (apiKeyRepository == null || webhookSubscriptionRepository == null || syncHistoryRepository == null) {
            return;
        }

        if (apiKeyRepository.count() == 0) {
            // Seed a sample ERP Production connector API Key
            // Raw test key: sprs_live_demo1234567890abcdef12345678
            String sampleKeyHash = com.supplier.sprsystem.security.ApiKeyAuthenticationFilter.hashKey("sprs_live_demo1234567890abcdef12345678");
            ApiKey apiKey = new ApiKey(
                    "Enterprise ERP Production Connector",
                    "sprs_live_demo",
                    sampleKeyHash,
                    Set.of(ApiKeyScope.SUPPLIER_READ, ApiKeyScope.SUPPLIER_WRITE, ApiKeyScope.PERFORMANCE_READ, ApiKeyScope.EVALUATION_READ, ApiKeyScope.REPORT_READ, ApiKeyScope.SYNC_MANAGE),
                    java.time.LocalDateTime.now().plusYears(1),
                    "admin"
            );
            apiKey.setLastUsedAt(java.time.LocalDateTime.now().minusHours(2));
            apiKeyRepository.save(apiKey);
            logger.info("Initialized sample API Key (prefix: sprs_live_demo).");
        }

        if (webhookSubscriptionRepository.count() == 0) {
            WebhookSubscription webhook = new WebhookSubscription(
                    "SAP Procurement Real-time Webhook",
                    "https://webhook.site/sprs-demo-receiver",
                    "whsec_demo_9876543210fedcba98765432",
                    Set.of(WebhookEventType.SUPPLIER_CREATED, WebhookEventType.SUPPLIER_UPDATED, WebhookEventType.SUPPLIER_RATING_UPDATED, WebhookEventType.EVALUATION_COMPLETED)
            );
            webhook.recordSuccess();
            webhookSubscriptionRepository.save(webhook);
            logger.info("Initialized sample Webhook subscription.");
        }

        if (syncHistoryRepository.count() == 0) {
            IntegrationSyncHistory history = new IntegrationSyncHistory(
                    "SAP ERP Purchasing",
                    IntegrationType.SUPPLIER_CATALOG,
                    SyncMode.UPSERT,
                    "Automated Sync Job"
            );
            history.setTotalRecords(25);
            history.setCreatedCount(15);
            history.setUpdatedCount(10);
            history.setSkippedCount(0);
            history.setFailedCount(0);
            history.markCompleted(SyncStatus.SUCCESS, null, null);
            syncHistoryRepository.save(history);
            logger.info("Initialized sample Integration Sync History.");
        }
    }
}

