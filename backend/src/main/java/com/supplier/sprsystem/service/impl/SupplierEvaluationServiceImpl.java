package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.request.EvaluationRequest;
import com.supplier.sprsystem.dto.request.EvaluationScoreRequest;
import com.supplier.sprsystem.dto.response.EvaluationResponse;
import com.supplier.sprsystem.dto.response.EvaluationScoreResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.exception.UnauthorizedException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.EvaluationCriteriaRepository;
import com.supplier.sprsystem.repository.SupplierEvaluationRepository;
import com.supplier.sprsystem.repository.SupplierPerformanceRatingRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.security.services.UserDetailsImpl;
import com.supplier.sprsystem.service.SupplierEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierEvaluationServiceImpl implements SupplierEvaluationService {

    private final SupplierEvaluationRepository evaluationRepository;
    private final SupplierRepository supplierRepository;
    private final EvaluationCriteriaRepository criteriaRepository;
    private final UserRepository userRepository;
    private final SupplierPerformanceRatingRepository performanceRatingRepository;
    private final com.supplier.sprsystem.service.NotificationService notificationService;

    @Autowired
    public SupplierEvaluationServiceImpl(SupplierEvaluationRepository evaluationRepository,
                                        SupplierRepository supplierRepository,
                                        EvaluationCriteriaRepository criteriaRepository,
                                        UserRepository userRepository,
                                        @Autowired(required = false) SupplierPerformanceRatingRepository performanceRatingRepository,
                                        @Autowired(required = false) com.supplier.sprsystem.service.NotificationService notificationService) {
        this.evaluationRepository = evaluationRepository;
        this.supplierRepository = supplierRepository;
        this.criteriaRepository = criteriaRepository;
        this.userRepository = userRepository;
        this.performanceRatingRepository = performanceRatingRepository;
        this.notificationService = notificationService;
    }

    public SupplierEvaluationServiceImpl(SupplierEvaluationRepository evaluationRepository,
                                        SupplierRepository supplierRepository,
                                        EvaluationCriteriaRepository criteriaRepository,
                                        UserRepository userRepository) {
        this(evaluationRepository, supplierRepository, criteriaRepository, userRepository, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<EvaluationResponse> getEvaluations(
            Long supplierId, Long evaluatorId, EvaluationStatus status, RatingCategory ratingCategory,
            LocalDate startDate, LocalDate endDate, int page, int size, String sortBy, String direction
    ) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<SupplierEvaluation> evaluationPage = evaluationRepository.filterEvaluations(
                supplierId, evaluatorId, status, ratingCategory, startDate, endDate, pageable);
        return PaginatedResponse.fromPage(evaluationPage.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<EvaluationResponse> getMyEvaluations(int page, int size, String sortBy, String direction) {
        User currentUser = getAuthenticatedUser();
        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<SupplierEvaluation> evaluationPage = evaluationRepository.findByEvaluatorId(currentUser.getId(), pageable);
        return PaginatedResponse.fromPage(evaluationPage.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getAllEvaluations() {
        return evaluationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EvaluationResponse> searchAndFilterEvaluations(
            Long supplierId, RatingCategory ratingCategory, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<SupplierEvaluation> page = evaluationRepository.searchAndFilterEvaluations(
                supplierId, ratingCategory, startDate, endDate, pageable);
        return page.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationResponse getEvaluationById(Long id) {
        SupplierEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierEvaluation", "id", id));
        return mapToResponse(evaluation);
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationResponse getEvaluationByCode(String code) {
        SupplierEvaluation evaluation = evaluationRepository.findByEvaluationCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierEvaluation", "evaluationCode", code));
        return mapToResponse(evaluation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getEvaluationsBySupplierId(Long supplierId) {
        return evaluationRepository.findBySupplierIdOrderByEvaluationDateDesc(supplierId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EvaluationResponse createEvaluation(EvaluationRequest request) {
        if (request.isDraft()) {
            return createDraftEvaluation(request);
        }
        return submitEvaluation(request);
    }

    @Override
    @Transactional
    public EvaluationResponse createDraftEvaluation(EvaluationRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));

        User evaluator = getAuthenticatedUser();
        String code = generateEvaluationCode();

        SupplierEvaluation evaluation = SupplierEvaluation.builder()
                .evaluationCode(code)
                .supplier(supplier)
                .evaluator(evaluator)
                .evaluationDate(request.getEvaluationDate() != null ? request.getEvaluationDate() : LocalDate.now())
                .evaluationPeriod(request.getEvaluationPeriod())
                .status(EvaluationStatus.DRAFT)
                .generalComments(request.getGeneralComments())
                .strengths(request.getStrengths())
                .areasForImprovement(request.getAreasForImprovement())
                .recommendation(request.getRecommendation())
                .scores(new ArrayList<>())
                .build();

        if (request.getScores() != null && !request.getScores().isEmpty()) {
            populateAndCalculateScores(evaluation, request.getScores());
        } else {
            evaluation.setTotalWeightedScore(0.0);
            evaluation.setRatingCategory(RatingCategory.UNRATED);
        }

        SupplierEvaluation saved = evaluationRepository.save(evaluation);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EvaluationResponse submitEvaluation(EvaluationRequest request) {
        validateActiveCriteriaWeights();

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));

        User evaluator = getAuthenticatedUser();
        String code = generateEvaluationCode();

        SupplierEvaluation evaluation = SupplierEvaluation.builder()
                .evaluationCode(code)
                .supplier(supplier)
                .evaluator(evaluator)
                .evaluationDate(request.getEvaluationDate() != null ? request.getEvaluationDate() : LocalDate.now())
                .evaluationPeriod(request.getEvaluationPeriod())
                .status(EvaluationStatus.COMPLETED)
                .generalComments(request.getGeneralComments())
                .strengths(request.getStrengths())
                .areasForImprovement(request.getAreasForImprovement())
                .recommendation(request.getRecommendation())
                .scores(new ArrayList<>())
                .build();

        if (request.getScores() == null || request.getScores().isEmpty()) {
            throw new BadRequestException("Evaluation must contain at least one criterion score");
        }

        populateAndCalculateScores(evaluation, request.getScores());

        SupplierEvaluation savedEvaluation = evaluationRepository.save(evaluation);
        updateSupplierAggregateRating(supplier);
        saveOrUpdatePerformanceRating(savedEvaluation);

        if (notificationService != null) {
            try {
                notificationService.broadcastToRole("ROLE_ADMIN",
                        "Evaluation Completed: " + supplier.getName(),
                        String.format("Evaluation %s recorded for %s with score %.1f%% (%s).",
                                savedEvaluation.getEvaluationCode(), supplier.getName(), savedEvaluation.getTotalWeightedScore(), savedEvaluation.getRatingCategory()),
                        NotificationType.EVALUATION,
                        savedEvaluation.getTotalWeightedScore() < 70 ? NotificationPriority.HIGH : NotificationPriority.MEDIUM,
                        "EVALUATION",
                        savedEvaluation.getId()
                );
            } catch (Exception ignored) {}
        }

        return mapToResponse(savedEvaluation);
    }

    @Override
    @Transactional
    public EvaluationResponse updateDraftEvaluation(Long id, EvaluationRequest request) {
        SupplierEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierEvaluation", "id", id));

        verifyOwnershipOrAdmin(evaluation);

        if (evaluation.getStatus() != EvaluationStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT evaluations can be edited");
        }

        if (request.getSupplierId() != null && !request.getSupplierId().equals(evaluation.getSupplier().getId())) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));
            evaluation.setSupplier(supplier);
        }

        if (request.getEvaluationDate() != null) {
            evaluation.setEvaluationDate(request.getEvaluationDate());
        }
        if (request.getEvaluationPeriod() != null) {
            evaluation.setEvaluationPeriod(request.getEvaluationPeriod());
        }
        evaluation.setGeneralComments(request.getGeneralComments());
        evaluation.setStrengths(request.getStrengths());
        evaluation.setAreasForImprovement(request.getAreasForImprovement());
        evaluation.setRecommendation(request.getRecommendation());

        if (request.getScores() != null && !request.getScores().isEmpty()) {
            evaluation.getScores().clear();
            populateAndCalculateScores(evaluation, request.getScores());
        }

        SupplierEvaluation updated = evaluationRepository.save(evaluation);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EvaluationResponse submitEvaluation(Long id) {
        SupplierEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierEvaluation", "id", id));

        verifyOwnershipOrAdmin(evaluation);

        if (evaluation.getStatus() != EvaluationStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT evaluations can be submitted");
        }

        if (evaluation.getScores() == null || evaluation.getScores().isEmpty()) {
            throw new BadRequestException("Cannot submit an evaluation without criteria scores");
        }

        validateActiveCriteriaWeights();

        evaluation.setStatus(EvaluationStatus.SUBMITTED);
        SupplierEvaluation updated = evaluationRepository.save(evaluation);
        updateSupplierAggregateRating(evaluation.getSupplier());
        saveOrUpdatePerformanceRating(updated);

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EvaluationResponse completeEvaluation(Long id) {
        SupplierEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierEvaluation", "id", id));

        verifyOwnershipOrAdmin(evaluation);

        if (evaluation.getStatus() == EvaluationStatus.COMPLETED) {
            return mapToResponse(evaluation);
        }

        if (evaluation.getStatus() == EvaluationStatus.CANCELLED) {
            throw new BadRequestException("Cannot complete a cancelled evaluation");
        }

        evaluation.setStatus(EvaluationStatus.COMPLETED);
        SupplierEvaluation updated = evaluationRepository.save(evaluation);
        updateSupplierAggregateRating(evaluation.getSupplier());
        saveOrUpdatePerformanceRating(updated);

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EvaluationResponse cancelEvaluation(Long id) {
        SupplierEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierEvaluation", "id", id));

        verifyOwnershipOrAdmin(evaluation);

        if (evaluation.getStatus() == EvaluationStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel an evaluation that is already COMPLETED");
        }

        if (evaluation.getStatus() == EvaluationStatus.CANCELLED) {
            throw new BadRequestException("Evaluation is already CANCELLED");
        }

        evaluation.setStatus(EvaluationStatus.CANCELLED);
        SupplierEvaluation updated = evaluationRepository.save(evaluation);
        updateSupplierAggregateRating(evaluation.getSupplier());

        if (performanceRatingRepository != null) {
            performanceRatingRepository.findByEvaluationId(id).ifPresent(performanceRatingRepository::delete);
        }

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteEvaluation(Long id) {
        SupplierEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierEvaluation", "id", id));

        if (performanceRatingRepository != null) {
            performanceRatingRepository.findByEvaluationId(id).ifPresent(performanceRatingRepository::delete);
        }

        Supplier supplier = evaluation.getSupplier();
        evaluationRepository.delete(evaluation);
        updateSupplierAggregateRating(supplier);
    }

    private void saveOrUpdatePerformanceRating(SupplierEvaluation evaluation) {
        if (performanceRatingRepository == null) return;
        if (evaluation.getStatus() != EvaluationStatus.COMPLETED && evaluation.getStatus() != EvaluationStatus.SUBMITTED) {
            return;
        }

        double score = evaluation.getTotalWeightedScore();
        SupplierRating rating = SupplierRating.fromScore(score);
        PerformanceStatus performanceStatus = PerformanceStatus.fromRating(rating);
        LocalDate ratingDate = evaluation.getEvaluationDate() != null ? evaluation.getEvaluationDate() : LocalDate.now();

        Optional<SupplierPerformanceRating> existingOpt = performanceRatingRepository.findByEvaluationId(evaluation.getId());
        SupplierPerformanceRating ratingEntity;
        if (existingOpt.isPresent()) {
            ratingEntity = existingOpt.get();
            ratingEntity.setScore(score);
            ratingEntity.setRating(rating);
            ratingEntity.setPerformanceStatus(performanceStatus);
            ratingEntity.setRatingDate(ratingDate);
        } else {
            ratingEntity = SupplierPerformanceRating.builder()
                    .supplier(evaluation.getSupplier())
                    .evaluation(evaluation)
                    .score(score)
                    .rating(rating)
                    .performanceStatus(performanceStatus)
                    .ratingDate(ratingDate)
                    .build();
        }
        performanceRatingRepository.save(ratingEntity);
    }

    private void populateAndCalculateScores(SupplierEvaluation evaluation, List<EvaluationScoreRequest> scoreRequests) {
        BigDecimal totalWeightedSum = BigDecimal.ZERO;
        BigDecimal totalWeightSum = BigDecimal.ZERO;

        for (EvaluationScoreRequest scoreReq : scoreRequests) {
            EvaluationCriteria criteria = criteriaRepository.findById(scoreReq.getCriteriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("EvaluationCriteria", "id", scoreReq.getCriteriaId()));

            double rawScore = scoreReq.getScoreObtained();
            double maxScore = criteria.getMaxScore() != null ? criteria.getMaxScore() : 100.0;
            double weight = criteria.getWeight();

            if (rawScore < 0.0) {
                throw new BadRequestException(String.format("Score for '%s' cannot be negative", criteria.getName()));
            }

            if (rawScore > maxScore) {
                throw new BadRequestException(String.format("Score for '%s' (%.1f) exceeds maximum score (%.1f)",
                        criteria.getName(), rawScore, maxScore));
            }

            BigDecimal bRawScore = BigDecimal.valueOf(rawScore);
            BigDecimal bMaxScore = BigDecimal.valueOf(maxScore);
            BigDecimal bWeight = BigDecimal.valueOf(weight);

            BigDecimal bWeightedScore = bRawScore
                    .divide(bMaxScore, 6, RoundingMode.HALF_UP)
                    .multiply(bWeight)
                    .setScale(2, RoundingMode.HALF_UP);

            totalWeightedSum = totalWeightedSum.add(bWeightedScore);
            totalWeightSum = totalWeightSum.add(bWeight);

            EvaluationScore score = EvaluationScore.builder()
                    .evaluation(evaluation)
                    .criteria(criteria)
                    .scoreObtained(round(rawScore, 2))
                    .maxScore(round(maxScore, 2))
                    .weight(round(weight, 2))
                    .weightedScore(bWeightedScore.doubleValue())
                    .remarks(scoreReq.getRemarks())
                    .build();

            evaluation.addScore(score);
        }

        BigDecimal finalScore = BigDecimal.ZERO;
        if (totalWeightSum.compareTo(BigDecimal.ZERO) > 0) {
            finalScore = totalWeightedSum
                    .divide(totalWeightSum, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        double totalScore = finalScore.doubleValue();
        RatingCategory ratingCategory = determineRatingCategory(totalScore);

        evaluation.setTotalWeightedScore(totalScore);
        evaluation.setRatingCategory(ratingCategory);
    }

    private void validateActiveCriteriaWeights() {
        Double totalWeight = criteriaRepository.sumActiveWeights();
        if (totalWeight == null || Math.abs(totalWeight - 100.0) > 0.01) {
            throw new BadRequestException("Active evaluation criteria weights must total 100% before submitting an evaluation. Current total: " + (totalWeight != null ? totalWeight : 0.0) + "%");
        }
    }

    private void updateSupplierAggregateRating(Supplier supplier) {
        List<SupplierEvaluation> allEvals = evaluationRepository.findBySupplierIdOrderByEvaluationDateDesc(supplier.getId()).stream()
                .filter(e -> e.getStatus() != EvaluationStatus.CANCELLED && e.getStatus() != EvaluationStatus.DRAFT)
                .collect(Collectors.toList());

        if (allEvals.isEmpty()) {
            supplier.setOverallRating(0.0);
            supplier.setRatingCategory(RatingCategory.UNRATED);
            supplier.setTotalEvaluations(0);
        } else {
            double avgScore = allEvals.stream()
                    .mapToDouble(SupplierEvaluation::getTotalWeightedScore)
                    .average()
                    .orElse(0.0);
            avgScore = round(avgScore, 2);
            supplier.setOverallRating(avgScore);
            supplier.setRatingCategory(determineRatingCategory(avgScore));
            supplier.setTotalEvaluations(allEvals.size());
        }
        supplierRepository.save(supplier);
    }

    public static RatingCategory determineRatingCategory(double score) {
        if (score >= 85.0) {
            return RatingCategory.EXCELLENT;
        } else if (score >= 70.0) {
            return RatingCategory.GOOD;
        } else if (score >= 50.0) {
            return RatingCategory.AVERAGE;
        } else {
            return RatingCategory.POOR;
        }
    }

    private void verifyOwnershipOrAdmin(SupplierEvaluation evaluation) {
        User currentUser = getAuthenticatedUser();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));

        if (!isAdmin && !evaluation.getEvaluator().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this evaluation");
        }
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException("Authenticated user is required to evaluate supplier");
        }
        if (auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userRepository.findById(userDetails.getId())
                    .orElseGet(() -> userRepository.findByUsername(userDetails.getUsername())
                            .orElseThrow(() -> new ResourceNotFoundException("User", "username", userDetails.getUsername())));
        }
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
    }

    private String generateEvaluationCode() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        int randomNum = (int) (Math.random() * 9000) + 1000;
        return "EV-" + datePrefix + "-" + randomNum;
    }

    private Pageable createPageable(int page, int size, String sortBy, String direction) {
        String cleanSortBy = "evaluationDate";
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String prop = sortBy.trim();
            if (prop.equalsIgnoreCase("id")) {
                cleanSortBy = "id";
            } else if (prop.equalsIgnoreCase("evaluationDate") || prop.equalsIgnoreCase("date")) {
                cleanSortBy = "evaluationDate";
            } else if (prop.equalsIgnoreCase("totalWeightedScore") || prop.equalsIgnoreCase("totalScore") || prop.equalsIgnoreCase("score")) {
                cleanSortBy = "totalWeightedScore";
            } else if (prop.equalsIgnoreCase("createdAt")) {
                cleanSortBy = "createdAt";
            } else {
                cleanSortBy = prop;
            }
        }

        Sort.Direction sortDirection = Sort.Direction.DESC;
        if (direction != null && direction.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        }

        return PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(sortDirection, cleanSortBy));
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private EvaluationResponse mapToResponse(SupplierEvaluation eval) {
        List<EvaluationScoreResponse> scoreResponses = eval.getScores().stream()
                .map(s -> EvaluationScoreResponse.builder()
                        .id(s.getId())
                        .criteriaId(s.getCriteria().getId())
                        .criteriaName(s.getCriteria().getName())
                        .criteriaCode(s.getCriteria().getCode())
                        .scoreObtained(s.getScoreObtained())
                        .score(s.getScoreObtained())
                        .maxScore(s.getMaxScore())
                        .weight(s.getWeight())
                        .weightedScore(s.getWeightedScore())
                        .remarks(s.getRemarks())
                        .comments(s.getRemarks())
                        .build())
                .collect(Collectors.toList());

        return EvaluationResponse.builder()
                .id(eval.getId())
                .evaluationCode(eval.getEvaluationCode())
                .supplierId(eval.getSupplier().getId())
                .supplierCode(eval.getSupplier().getSupplierCode())
                .supplierName(eval.getSupplier().getName())
                .supplierCategoryName(eval.getSupplier().getCategory() != null ? eval.getSupplier().getCategory().getName() : "N/A")
                .evaluatorId(eval.getEvaluator().getId())
                .evaluatorName(eval.getEvaluator().getFullName())
                .evaluatorUsername(eval.getEvaluator().getUsername())
                .evaluationDate(eval.getEvaluationDate())
                .evaluationPeriod(eval.getEvaluationPeriod())
                .status(eval.getStatus())
                .totalWeightedScore(eval.getTotalWeightedScore())
                .ratingCategory(eval.getRatingCategory())
                .generalComments(eval.getGeneralComments())
                .strengths(eval.getStrengths())
                .areasForImprovement(eval.getAreasForImprovement())
                .recommendation(eval.getRecommendation())
                .scores(scoreResponses)
                .createdAt(eval.getCreatedAt())
                .updatedAt(eval.getUpdatedAt())
                .build();
    }
}
