package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.external.*;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.EvaluationScoreRepository;
import com.supplier.sprsystem.repository.SupplierCategoryRepository;
import com.supplier.sprsystem.repository.SupplierEvaluationRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.service.WebhookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/external")
public class ExternalApiController {

    private final SupplierRepository supplierRepository;
    private final SupplierCategoryRepository categoryRepository;
    private final SupplierEvaluationRepository evaluationRepository;
    private final EvaluationScoreRepository evaluationScoreRepository;
    private final WebhookService webhookService;

    public ExternalApiController(SupplierRepository supplierRepository,
                                 SupplierCategoryRepository categoryRepository,
                                 SupplierEvaluationRepository evaluationRepository,
                                 EvaluationScoreRepository evaluationScoreRepository,
                                 WebhookService webhookService) {
        this.supplierRepository = supplierRepository;
        this.categoryRepository = categoryRepository;
        this.evaluationRepository = evaluationRepository;
        this.evaluationScoreRepository = evaluationScoreRepository;
        this.webhookService = webhookService;
    }

    @GetMapping("/suppliers")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'SCOPE_SUPPLIER_READ')")
    public ResponseEntity<Page<ExternalSupplierDto>> getSuppliers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Supplier> supplierPage;
        if (StringUtils.hasText(search)) {
            supplierPage = supplierRepository.searchSuppliers(search.trim(), pageable);
        } else {
            supplierPage = supplierRepository.findAll(pageable);
        }

        Page<ExternalSupplierDto> dtoPage = supplierPage.map(this::mapToExternalSupplierDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/suppliers/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'SCOPE_SUPPLIER_READ')")
    public ResponseEntity<ExternalSupplierDto> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));
        return ResponseEntity.ok(mapToExternalSupplierDto(supplier));
    }

    @PostMapping("/suppliers")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'SCOPE_SUPPLIER_WRITE')")
    public ResponseEntity<ExternalSupplierDto> createSupplier(@Valid @RequestBody ExternalSupplierCreateRequest request) {
        if (supplierRepository.existsBySupplierCode(request.getSupplierCode())) {
            throw new IllegalArgumentException("Supplier code '" + request.getSupplierCode() + "' already exists");
        }
        if (supplierRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Supplier email '" + request.getEmail() + "' already exists");
        }

        SupplierCategory category = resolveCategory(request.getCategoryName());

        Supplier supplier = new Supplier();
        supplier.setSupplierCode(request.getSupplierCode().trim());
        supplier.setName(request.getName().trim());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setEmail(request.getEmail().trim().toLowerCase());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setWebsite(request.getWebsite());
        supplier.setCategory(category);

        SupplierStatus status = SupplierStatus.ACTIVE;
        if (StringUtils.hasText(request.getStatus())) {
            try {
                status = SupplierStatus.valueOf(request.getStatus().trim().toUpperCase());
            } catch (Exception ignored) {}
        }
        supplier.setStatus(status);

        Supplier saved = supplierRepository.save(supplier);

        try {
            webhookService.dispatchEvent(WebhookEventType.SUPPLIER_CREATED, saved.getSupplierCode());
        } catch (Exception ignored) {}

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToExternalSupplierDto(saved));
    }

    @GetMapping("/suppliers/{id}/performance")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'SCOPE_PERFORMANCE_READ')")
    public ResponseEntity<ExternalSupplierPerformanceDto> getSupplierPerformance(@PathVariable Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        List<SupplierEvaluation> evaluations = evaluationRepository.findBySupplierIdOrderByEvaluationDateDesc(id);

        double qualitySum = 0;
        double deliverySum = 0;
        double costSum = 0;
        double serviceSum = 0;
        int evaluatedCount = 0;

        for (SupplierEvaluation eval : evaluations) {
            if (eval.getStatus() == EvaluationStatus.COMPLETED || eval.getStatus() == EvaluationStatus.SUBMITTED) {
                evaluatedCount++;
                if (eval.getScores() != null) {
                    for (EvaluationScore score : eval.getScores()) {
                        String criteriaName = score.getCriteria() != null ? score.getCriteria().getName().toLowerCase() : "";
                        if (criteriaName.contains("quality")) {
                            qualitySum += score.getScore();
                        } else if (criteriaName.contains("delivery")) {
                            deliverySum += score.getScore();
                        } else if (criteriaName.contains("cost") || criteriaName.contains("price")) {
                            costSum += score.getScore();
                        } else if (criteriaName.contains("service") || criteriaName.contains("support")) {
                            serviceSum += score.getScore();
                        }
                    }
                }
            }
        }

        Double avgQuality = evaluatedCount > 0 ? Math.round((qualitySum / evaluatedCount) * 10.0) / 10.0 : null;
        Double avgDelivery = evaluatedCount > 0 ? Math.round((deliverySum / evaluatedCount) * 10.0) / 10.0 : null;
        Double avgCost = evaluatedCount > 0 ? Math.round((costSum / evaluatedCount) * 10.0) / 10.0 : null;
        Double avgService = evaluatedCount > 0 ? Math.round((serviceSum / evaluatedCount) * 10.0) / 10.0 : null;

        LocalDateTime lastEvaluatedAt = !evaluations.isEmpty() && evaluations.get(0).getEvaluationDate() != null
                ? evaluations.get(0).getEvaluationDate().atStartOfDay()
                : null;

        double riskScore = calculateRiskScore(supplier);
        String riskLevel = calculateRiskLevel(supplier);

        ExternalSupplierPerformanceDto performance = new ExternalSupplierPerformanceDto(
                supplier.getId(),
                supplier.getSupplierCode(),
                supplier.getName(),
                supplier.getOverallRating(),
                supplier.getRatingCategory() != null ? supplier.getRatingCategory().name() : null,
                riskScore,
                riskLevel,
                supplier.getTotalEvaluations(),
                avgQuality,
                avgDelivery,
                avgCost,
                avgService,
                lastEvaluatedAt,
                supplier.getUpdatedAt()
        );

        return ResponseEntity.ok(performance);
    }

    @GetMapping("/evaluations/supplier/{supplierId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'SCOPE_EVALUATION_READ')")
    public ResponseEntity<List<ExternalEvaluationDto>> getEvaluationsForSupplier(@PathVariable Long supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found with ID: " + supplierId);
        }

        List<SupplierEvaluation> evaluations = evaluationRepository.findBySupplierIdOrderByEvaluationDateDesc(supplierId);

        List<ExternalEvaluationDto> dtos = evaluations.stream()
                .map(this::mapToExternalEvaluationDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/reports/summary")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'SCOPE_REPORT_READ')")
    public ResponseEntity<ExternalReportSummaryDto> getReportSummary() {
        long totalSuppliers = supplierRepository.count();
        long activeSuppliers = supplierRepository.countByActive(true);
        Double avgScore = supplierRepository.calculateAverageSupplierRating();
        if (avgScore == null) avgScore = 0.0;
        avgScore = Math.round(avgScore * 10.0) / 10.0;

        long excellent = supplierRepository.countByRatingCategory(RatingCategory.EXCELLENT);
        long good = supplierRepository.countByRatingCategory(RatingCategory.GOOD);
        long average = supplierRepository.countByRatingCategory(RatingCategory.AVERAGE);
        long poor = supplierRepository.countByRatingCategory(RatingCategory.POOR);

        long highRisk = supplierRepository.findAll().stream()
                .filter(s -> "HIGH".equals(calculateRiskLevel(s)))
                .count();

        long completedEvals = evaluationRepository.countByStatus(EvaluationStatus.COMPLETED);

        ExternalReportSummaryDto summary = new ExternalReportSummaryDto(
                LocalDateTime.now(),
                totalSuppliers,
                activeSuppliers,
                avgScore,
                excellent,
                good,
                average,
                poor,
                highRisk,
                completedEvals
        );

        return ResponseEntity.ok(summary);
    }

    private SupplierCategory resolveCategory(String categoryName) {
        if (StringUtils.hasText(categoryName)) {
            Optional<SupplierCategory> catOpt = categoryRepository.findByNameIgnoreCase(categoryName.trim());
            if (catOpt.isPresent()) {
                return catOpt.get();
            }
            String catCode = categoryName.trim().toUpperCase().replaceAll("[^A-Z0-9]", "_");
            if (catCode.length() > 20) catCode = catCode.substring(0, 20);
            SupplierCategory newCat = SupplierCategory.builder()
                    .code(catCode + "_" + System.currentTimeMillis() % 1000)
                    .name(categoryName.trim())
                    .description("Auto-created from External API")
                    .active(true)
                    .build();
            return categoryRepository.save(newCat);
        }

        List<SupplierCategory> allCats = categoryRepository.findAll();
        if (!allCats.isEmpty()) {
            return allCats.get(0);
        }

        SupplierCategory general = SupplierCategory.builder()
                .code("CAT_GEN")
                .name("General Supplies")
                .description("Default general category")
                .active(true)
                .build();
        return categoryRepository.save(general);
    }

    private double calculateRiskScore(Supplier s) {
        double rating = s.getOverallRating() != null ? s.getOverallRating() : 0.0;
        return Math.round(Math.max(0.0, 100.0 - rating) * 10.0) / 10.0;
    }

    private String calculateRiskLevel(Supplier s) {
        if (s.getRatingCategory() == RatingCategory.POOR || (s.getOverallRating() != null && s.getOverallRating() < 50.0)) {
            return "HIGH";
        } else if (s.getRatingCategory() == RatingCategory.AVERAGE || (s.getOverallRating() != null && s.getOverallRating() < 75.0)) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private ExternalSupplierDto mapToExternalSupplierDto(Supplier s) {
        return new ExternalSupplierDto(
                s.getId(),
                s.getSupplierCode(),
                s.getName(),
                s.getContactPerson(),
                s.getEmail(),
                s.getPhone(),
                s.getAddress(),
                s.getCategory() != null ? s.getCategory().getName() : null,
                s.getStatus() != null ? s.getStatus().name() : null,
                s.getOverallRating(),
                s.getRatingCategory() != null ? s.getRatingCategory().name() : null,
                calculateRiskScore(s),
                calculateRiskLevel(s),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }

    private ExternalEvaluationDto mapToExternalEvaluationDto(SupplierEvaluation eval) {
        return new ExternalEvaluationDto(
                eval.getId(),
                eval.getSupplier() != null ? eval.getSupplier().getId() : null,
                eval.getSupplier() != null ? eval.getSupplier().getSupplierCode() : null,
                eval.getSupplier() != null ? eval.getSupplier().getName() : null,
                eval.getEvaluationPeriod(),
                eval.getEvaluationDate() != null ? eval.getEvaluationDate().atStartOfDay() : null,
                eval.getTotalWeightedScore(),
                eval.getRatingCategory() != null ? eval.getRatingCategory().name() : null,
                eval.getEvaluator() != null ? eval.getEvaluator().getFullName() : null,
                eval.getStatus() != null ? eval.getStatus().name() : null,
                eval.getComments()
        );
    }
}
