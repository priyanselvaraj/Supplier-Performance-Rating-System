package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.request.*;
import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.DuplicateResourceException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.exception.UnauthorizedException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import com.supplier.sprsystem.security.services.UserDetailsImpl;
import com.supplier.sprsystem.service.AiIntelligenceService;
import com.supplier.sprsystem.service.NotificationService;
import com.supplier.sprsystem.service.SupplierPortalService;
import com.supplier.sprsystem.service.SupplierRatingService;
import com.supplier.sprsystem.service.WorkflowEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplierPortalServiceImpl implements SupplierPortalService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierPortalServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SupplierRepository supplierRepository;
    private final SupplierEvaluationRepository evaluationRepository;
    private final SupplierImprovementActionRepository improvementActionRepository;
    private final SupplierDocumentRepository documentRepository;
    private final SupplierProfileUpdateRequestRepository profileUpdateRequestRepository;
    private final SupplierCommunicationRepository communicationRepository;
    private final SupplierRatingService ratingService;
    private final AiIntelligenceService aiIntelligenceService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final WorkflowEngineService workflowEngineService;

    @Value("${app.upload.dir:uploads/documents}")
    private String uploadBaseDir;

    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024; // 15 MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "png", "jpg", "jpeg", "docx", "xlsx", "csv", "txt", "zip"
    );

    public SupplierPortalServiceImpl(UserRepository userRepository,
                                    RoleRepository roleRepository,
                                    SupplierRepository supplierRepository,
                                    SupplierEvaluationRepository evaluationRepository,
                                    SupplierImprovementActionRepository improvementActionRepository,
                                    SupplierDocumentRepository documentRepository,
                                    SupplierProfileUpdateRequestRepository profileUpdateRequestRepository,
                                    SupplierCommunicationRepository communicationRepository,
                                    SupplierRatingService ratingService,
                                    AiIntelligenceService aiIntelligenceService,
                                    NotificationService notificationService,
                                    PasswordEncoder passwordEncoder,
                                    @Autowired(required = false) @Lazy WorkflowEngineService workflowEngineService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.supplierRepository = supplierRepository;
        this.evaluationRepository = evaluationRepository;
        this.improvementActionRepository = improvementActionRepository;
        this.documentRepository = documentRepository;
        this.profileUpdateRequestRepository = profileUpdateRequestRepository;
        this.communicationRepository = communicationRepository;
        this.ratingService = ratingService;
        this.aiIntelligenceService = aiIntelligenceService;
        this.notificationService = notificationService;
        this.passwordEncoder = passwordEncoder;
        this.workflowEngineService = workflowEngineService;
    }

    // =========================================================================
    // HELPER METHODS FOR AUTHENTICATION & DATA ISOLATION
    // =========================================================================

    private User getAuthenticatedUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }

        if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userRepository.findById(userDetails.getId())
                    .orElseGet(() -> userRepository.findByUsername(userDetails.getUsername())
                            .orElseThrow(() -> new ResourceNotFoundException("User", "username", userDetails.getUsername())));
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));
    }

    private Supplier resolveAuthenticatedSupplier(User user) {
        boolean isSupplierRole = user.getRoles().stream()
                .anyMatch(r -> r.getName() == ERole.ROLE_SUPPLIER);

        if (isSupplierRole) {
            if (user.getSupplier() == null) {
                throw new UnauthorizedException("Your account is not linked to any registered supplier. Please contact procurement support.");
            }
            return user.getSupplier();
        }

        // For Admin / Manager testing
        if (user.getSupplier() != null) {
            return user.getSupplier();
        }

        return supplierRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "system", "no suppliers exist"));
    }

    // =========================================================================
    // 1. DASHBOARD & PROFILE
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public SupplierPortalDashboardResponse getSupplierDashboard() {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        SupplierPerformanceSummaryResponse summary = ratingService.getSupplierPerformanceSummary(supplier.getId());

        List<SupplierImprovementAction> allActions = improvementActionRepository.findBySupplierIdOrderByCreatedAtDesc(supplier.getId());
        long openCount = allActions.stream().filter(a -> a.getStatus() == ImprovementActionStatus.OPEN).count();
        long inProgressCount = allActions.stream().filter(a -> a.getStatus() == ImprovementActionStatus.IN_PROGRESS).count();
        long completedCount = allActions.stream().filter(a -> a.getStatus() == ImprovementActionStatus.COMPLETED).count();

        long docCount = documentRepository.countBySupplierId(supplier.getId());
        long unreadNotifs = notificationService.getUnreadCount(user.getId());

        List<SupplierEvaluation> evals = evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateDesc(
                supplier.getId(), EvaluationStatus.COMPLETED);

        List<SupplierPortalEvaluationResponse> recentEvals = evals.stream()
                .limit(5)
                .map(this::mapToPortalEvaluationResponse)
                .collect(Collectors.toList());

        List<ImprovementActionResponse> urgentActions = allActions.stream()
                .filter(a -> a.getStatus() == ImprovementActionStatus.OPEN || a.getStatus() == ImprovementActionStatus.IN_PROGRESS)
                .limit(5)
                .map(this::mapToActionResponse)
                .collect(Collectors.toList());

        SupplierAiInsightsResponse aiInsights = null;
        try {
            aiInsights = aiIntelligenceService.getSupplierInsights(supplier.getId());
        } catch (Exception ignored) {}

        return SupplierPortalDashboardResponse.builder()
                .supplierId(supplier.getId())
                .supplierCode(supplier.getSupplierCode())
                .supplierName(supplier.getName())
                .categoryName(supplier.getCategory() != null ? supplier.getCategory().getName() : "General")
                .status(supplier.getStatus())
                .contactPerson(supplier.getContactPerson())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .overallRating(supplier.getOverallRating())
                .ratingCategory(supplier.getRatingCategory())
                .performanceStatus(summary.getLatestPerformanceStatus())
                .totalEvaluations(supplier.getTotalEvaluations())
                .lastEvaluationDate(summary.getRatingDate())
                .scoreDifference(summary.getScoreDifference())
                .performanceTrend(summary.getPerformanceTrend())
                .openActionsCount(openCount)
                .inProgressActionsCount(inProgressCount)
                .completedActionsCount(completedCount)
                .totalDocumentsCount(docCount)
                .unreadNotificationsCount(unreadNotifs)
                .recentEvaluations(recentEvals)
                .urgentActions(urgentActions)
                .aiInsights(aiInsights)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierPortalProfileResponse getSupplierProfile() {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        List<SupplierProfileUpdateRequest> requests = profileUpdateRequestRepository.findBySupplierIdOrderByCreatedAtDesc(supplier.getId());
        boolean hasPending = requests.stream().anyMatch(r -> r.getStatus() == UpdateRequestStatus.PENDING);

        List<SupplierProfileUpdateRequestDto> requestDtos = requests.stream()
                .map(this::mapToUpdateRequestDto)
                .collect(Collectors.toList());

        return SupplierPortalProfileResponse.builder()
                .id(supplier.getId())
                .supplierCode(supplier.getSupplierCode())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .website(supplier.getWebsite())
                .city(supplier.getCity())
                .state(supplier.getState())
                .country(supplier.getCountry())
                .categoryId(supplier.getCategory() != null ? supplier.getCategory().getId() : null)
                .categoryName(supplier.getCategory() != null ? supplier.getCategory().getName() : "General")
                .status(supplier.getStatus())
                .overallRating(supplier.getOverallRating())
                .ratingCategory(supplier.getRatingCategory())
                .totalEvaluations(supplier.getTotalEvaluations())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .hasPendingUpdateRequest(hasPending)
                .updateRequests(requestDtos)
                .build();
    }

    @Override
    @Transactional
    public SupplierProfileUpdateRequestDto submitProfileUpdateRequest(SupplierProfileUpdateSubmitRequest request) {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        if (profileUpdateRequestRepository.existsBySupplierIdAndStatus(supplier.getId(), UpdateRequestStatus.PENDING)) {
            throw new BadRequestException("A profile update request is already pending review for your company.");
        }

        SupplierProfileUpdateRequest updateReq = SupplierProfileUpdateRequest.builder()
                .supplier(supplier)
                .requestedBy(user)
                .contactPerson(request.getContactPerson() != null ? request.getContactPerson().trim() : supplier.getContactPerson())
                .phone(request.getPhone() != null ? request.getPhone().trim() : supplier.getPhone())
                .email(request.getEmail() != null ? request.getEmail().trim().toLowerCase() : supplier.getEmail())
                .address(request.getAddress() != null ? request.getAddress().trim() : supplier.getAddress())
                .website(request.getWebsite() != null ? request.getWebsite().trim() : supplier.getWebsite())
                .city(request.getCity() != null ? request.getCity().trim() : supplier.getCity())
                .state(request.getState() != null ? request.getState().trim() : supplier.getState())
                .country(request.getCountry() != null ? request.getCountry().trim() : supplier.getCountry())
                .status(UpdateRequestStatus.PENDING)
                .build();

        SupplierProfileUpdateRequest saved = profileUpdateRequestRepository.save(updateReq);

        // Initiate Phase 14 Multi-Level Workflow if workflow engine is active
        if (workflowEngineService != null) {
            try {
                workflowEngineService.startWorkflow(
                        WorkflowType.SUPPLIER_PROFILE_UPDATE,
                        "Profile Update: " + supplier.getName(),
                        "PROFILE_UPDATE_REQUEST",
                        saved.getId(),
                        user,
                        "{\"supplierCode\":\"" + supplier.getSupplierCode() + "\",\"supplierName\":\"" + supplier.getName() + "\"}"
                );
            } catch (Exception e) {
                logger.warn("Could not start workflow for profile update request {}: {}", saved.getId(), e.getMessage());
            }
        }

        // Notify procurement managers
        notificationService.broadcastToRole(
                "ROLE_MANAGER",
                "Supplier Profile Update Request",
                "Supplier '" + supplier.getName() + "' submitted a profile update request for review.",
                NotificationType.SYSTEM,
                NotificationPriority.MEDIUM,
                "PROFILE_UPDATE_REQUEST",
                saved.getId()
        );

        return mapToUpdateRequestDto(saved);
    }

    // =========================================================================
    // 2. PERFORMANCE & EVALUATIONS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public SupplierPortalPerformanceResponse getSupplierPerformance() {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        SupplierPerformanceSummaryResponse summary = ratingService.getSupplierPerformanceSummary(supplier.getId());
        RatingHistoryResponse history = ratingService.getSupplierRatingHistory(supplier.getId());

        List<PerformanceTrendResponse> trendData = Collections.emptyList();
        try {
            trendData = history.getRatings().stream()
                    .map(r -> PerformanceTrendResponse.builder()
                            .date(r.getRatingDate())
                            .score(r.getScore())
                            .rating(r.getRating())
                            .performanceStatus(r.getPerformanceStatus())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception ignored) {}

        SupplierAiInsightsResponse aiInsights = null;
        try {
            aiInsights = aiIntelligenceService.getSupplierInsights(supplier.getId());
        } catch (Exception ignored) {}

        return SupplierPortalPerformanceResponse.builder()
                .supplierId(supplier.getId())
                .supplierCode(supplier.getSupplierCode())
                .supplierName(supplier.getName())
                .currentScore(supplier.getOverallRating())
                .currentRating(summary.getLatestRating())
                .ratingCategory(supplier.getRatingCategory())
                .performanceStatus(summary.getLatestPerformanceStatus())
                .performanceTrend(summary.getPerformanceTrend())
                .scoreDifference(summary.getScoreDifference())
                .totalEvaluations(supplier.getTotalEvaluations())
                .ratingHistory(history.getRatings())
                .trendData(trendData)
                .aiInsights(aiInsights)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierPortalEvaluationResponse> getSupplierEvaluations() {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        return evaluationRepository.findBySupplierIdAndStatusOrderByEvaluationDateDesc(supplier.getId(), EvaluationStatus.COMPLETED)
                .stream()
                .map(this::mapToPortalEvaluationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierPortalEvaluationResponse getSupplierEvaluationDetails(Long evaluationId) {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        SupplierEvaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation", "id", evaluationId));

        // Enforce supplier data isolation
        if (!evaluation.getSupplier().getId().equals(supplier.getId())) {
            throw new UnauthorizedException("You are not authorized to view evaluation records for another supplier.");
        }

        return mapToPortalEvaluationResponse(evaluation);
    }

    // =========================================================================
    // 3. IMPROVEMENT ACTIONS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<ImprovementActionResponse> getSupplierImprovementActions() {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        return improvementActionRepository.findBySupplierIdOrderByCreatedAtDesc(supplier.getId())
                .stream()
                .map(this::mapToActionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ImprovementActionResponse respondToImprovementAction(Long actionId, SupplierActionResponseRequest request) {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        SupplierImprovementAction action = improvementActionRepository.findById(actionId)
                .orElseThrow(() -> new ResourceNotFoundException("ImprovementAction", "id", actionId));

        // Enforce supplier data isolation
        if (!action.getSupplier().getId().equals(supplier.getId())) {
            throw new UnauthorizedException("You are not authorized to respond to improvement actions belonging to another supplier.");
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String existingNotes = action.getResolutionNotes() != null ? action.getResolutionNotes() : "";
        String appendedNotes = (existingNotes.isEmpty() ? "" : existingNotes + "\n\n")
                + "[" + timestamp + " Supplier Update by " + user.getFullName() + "]: " + request.getResponseNotes().trim();

        action.setResolutionNotes(appendedNotes);
        SupplierImprovementAction updated = improvementActionRepository.save(action);

        // Notify assigned manager or procurement team
        if (action.getAssignedUser() != null) {
            notificationService.createNotification(
                    action.getAssignedUser().getId(),
                    "Supplier Action Response",
                    "Supplier '" + supplier.getName() + "' submitted an update on action: " + action.getTitle(),
                    NotificationType.IMPROVEMENT_ACTION,
                    NotificationPriority.HIGH,
                    "IMPROVEMENT_ACTION",
                    action.getId()
            );
        }

        return mapToActionResponse(updated);
    }

    // =========================================================================
    // 4. DOCUMENTS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDocumentResponse> getSupplierDocuments() {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        return documentRepository.findBySupplierIdOrderByCreatedAtDesc(supplier.getId())
                .stream()
                .map(this::mapToDocumentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SupplierDocumentResponse uploadDocument(MultipartFile file, String documentType, String notes) {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded document file must not be empty.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum allowable limit of 15MB.");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (originalFilename.contains("..")) {
            throw new BadRequestException("Filename contains invalid path traversal characters.");
        }

        String extension = "";
        int extIdx = originalFilename.lastIndexOf('.');
        if (extIdx > 0) {
            extension = originalFilename.substring(extIdx + 1).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Unsupported file format ." + extension + ". Allowed formats: " + ALLOWED_EXTENSIONS);
        }

        String storedFileName = UUID.randomUUID() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        try {
            Path targetDir = Paths.get(uploadBaseDir, "supplier_" + supplier.getId()).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            Path targetPath = targetDir.resolve(storedFileName).normalize();
            if (!targetPath.startsWith(targetDir)) {
                throw new BadRequestException("Path traversal validation failed.");
            }

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BadRequestException("Could not store file on server: " + ex.getMessage());
        }

        SupplierDocument document = SupplierDocument.builder()
                .supplier(supplier)
                .documentName(originalFilename)
                .documentType(documentType != null && !documentType.trim().isEmpty() ? documentType.trim() : "General Document")
                .storedFileName(storedFileName)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedBy(user)
                .status(DocumentStatus.ACTIVE)
                .notes(notes)
                .build();

        SupplierDocument saved = documentRepository.save(document);

        // Initiate Phase 14 Multi-Level Workflow if workflow engine is active
        if (workflowEngineService != null) {
            try {
                workflowEngineService.startWorkflow(
                        WorkflowType.SUPPLIER_DOCUMENT_REVIEW,
                        "Document Review: " + saved.getDocumentName() + " (" + supplier.getName() + ")",
                        "DOCUMENT",
                        saved.getId(),
                        user,
                        "{\"supplierCode\":\"" + supplier.getSupplierCode() + "\",\"documentType\":\"" + saved.getDocumentType() + "\"}"
                );
            } catch (Exception e) {
                logger.warn("Could not start workflow for uploaded document {}: {}", saved.getId(), e.getMessage());
            }
        }

        // Notify procurement managers of uploaded document
        notificationService.broadcastToRole(
                "ROLE_MANAGER",
                "New Supplier Document Uploaded",
                "Supplier '" + supplier.getName() + "' uploaded: " + originalFilename + " (" + document.getDocumentType() + ")",
                NotificationType.SYSTEM,
                NotificationPriority.LOW,
                "SUPPLIER_DOCUMENT",
                saved.getId()
        );

        return mapToDocumentResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadDocument(Long documentId) {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        SupplierDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        boolean isManagerOrAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName() == ERole.ROLE_ADMIN || r.getName() == ERole.ROLE_MANAGER);

        if (!isManagerOrAdmin && !doc.getSupplier().getId().equals(supplier.getId())) {
            throw new UnauthorizedException("You are not authorized to download documents belonging to another supplier.");
        }

        try {
            Path filePath = Paths.get(uploadBaseDir, "supplier_" + doc.getSupplier().getId(), doc.getStoredFileName()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Document file not found on disk: " + doc.getDocumentName());
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("Document file path error: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteDocument(Long documentId) {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        SupplierDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        boolean isManagerOrAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName() == ERole.ROLE_ADMIN || r.getName() == ERole.ROLE_MANAGER);

        if (!isManagerOrAdmin && !doc.getSupplier().getId().equals(supplier.getId())) {
            throw new UnauthorizedException("You are not authorized to delete documents belonging to another supplier.");
        }

        try {
            Path filePath = Paths.get(uploadBaseDir, "supplier_" + doc.getSupplier().getId(), doc.getStoredFileName()).toAbsolutePath().normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {}

        documentRepository.delete(doc);
    }

    // =========================================================================
    // 5. COMMUNICATIONS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<SupplierCommunicationResponse> getSupplierCommunications() {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        return communicationRepository.findBySupplierIdOrderByCreatedAtDesc(supplier.getId())
                .stream()
                .map(this::mapToCommunicationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SupplierCommunicationResponse sendCommunication(SupplierCommunicationCreateRequest request) {
        User user = getAuthenticatedUserEntity();
        Supplier supplier = resolveAuthenticatedSupplier(user);

        boolean isFromSupplier = user.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_SUPPLIER);

        SupplierCommunication comm = SupplierCommunication.builder()
                .supplier(supplier)
                .sender(user)
                .subject(request.getSubject().trim())
                .message(request.getMessage().trim())
                .relatedResourceType(request.getRelatedResourceType())
                .relatedResourceId(request.getRelatedResourceId())
                .isFromSupplier(isFromSupplier)
                .build();

        SupplierCommunication saved = communicationRepository.save(comm);

        if (isFromSupplier) {
            notificationService.broadcastToRole(
                    "ROLE_MANAGER",
                    "New Supplier Message",
                    "Supplier '" + supplier.getName() + "': " + request.getSubject(),
                    NotificationType.SYSTEM,
                    NotificationPriority.MEDIUM,
                    "SUPPLIER_COMMUNICATION",
                    saved.getId()
            );
        }

        return mapToCommunicationResponse(saved);
    }

    // =========================================================================
    // 6. NOTIFICATIONS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getSupplierNotifications() {
        User user = getAuthenticatedUserEntity();
        return notificationService.getRecentNotifications(user.getId());
    }

    // =========================================================================
    // 7. ADMIN / MANAGER OPERATIONS
    // =========================================================================

    @Override
    @Transactional
    public UserResponse createSupplierUser(SupplierAccountCreateRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));

        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken.");
        }

        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already in use.");
        }

        Role supplierRole = roleRepository.findByName(ERole.ROLE_SUPPLIER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(ERole.ROLE_SUPPLIER).build()));

        User user = User.builder()
                .username(request.getUsername().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phone(request.getPhone() != null ? request.getPhone().trim() : supplier.getPhone())
                .department("Supplier Portal")
                .active(true)
                .roles(Set.of(supplierRole))
                .supplier(supplier)
                .build();

        User saved = userRepository.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .fullName(saved.getFullName())
                .phone(saved.getPhone())
                .department(saved.getDepartment())
                .active(saved.isActive())
                .roles(Set.of("ROLE_SUPPLIER"))
                .supplierId(supplier.getId())
                .supplierName(supplier.getName())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierProfileUpdateRequestDto> getPendingProfileUpdateRequests() {
        return profileUpdateRequestRepository.findByStatusOrderByCreatedAtDesc(UpdateRequestStatus.PENDING)
                .stream()
                .map(this::mapToUpdateRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SupplierProfileUpdateRequestDto reviewProfileUpdateRequest(Long requestId, ProfileUpdateRequestReviewDto reviewDto) {
        User reviewer = getAuthenticatedUserEntity();

        SupplierProfileUpdateRequest req = profileUpdateRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("ProfileUpdateRequest", "id", requestId));

        if (req.getStatus() != UpdateRequestStatus.PENDING) {
            throw new BadRequestException("This profile update request has already been processed with status: " + req.getStatus());
        }

        req.setReviewedBy(reviewer);
        req.setReviewedAt(LocalDateTime.now());
        req.setReviewerNotes(reviewDto.getReviewerNotes());

        if (Boolean.TRUE.equals(reviewDto.getApproved())) {
            req.setStatus(UpdateRequestStatus.APPROVED);

            Supplier supplier = req.getSupplier();
            if (req.getContactPerson() != null) supplier.setContactPerson(req.getContactPerson());
            if (req.getPhone() != null) supplier.setPhone(req.getPhone());
            if (req.getEmail() != null) supplier.setEmail(req.getEmail());
            if (req.getAddress() != null) supplier.setAddress(req.getAddress());
            if (req.getWebsite() != null) supplier.setWebsite(req.getWebsite());
            if (req.getCity() != null) supplier.setCity(req.getCity());
            if (req.getState() != null) supplier.setState(req.getState());
            if (req.getCountry() != null) supplier.setCountry(req.getCountry());
            supplierRepository.save(supplier);

            // Notify supplier user
            notificationService.createNotification(
                    req.getRequestedBy().getId(),
                    "Profile Update Approved",
                    "Your profile update request for '" + supplier.getName() + "' has been approved by the procurement team.",
                    NotificationType.SYSTEM,
                    NotificationPriority.MEDIUM,
                    "PROFILE_UPDATE_REQUEST",
                    req.getId()
            );
        } else {
            req.setStatus(UpdateRequestStatus.REJECTED);

            // Notify supplier user
            notificationService.createNotification(
                    req.getRequestedBy().getId(),
                    "Profile Update Rejected",
                    "Your profile update request was not approved: " + (reviewDto.getReviewerNotes() != null ? reviewDto.getReviewerNotes() : "No reason provided."),
                    NotificationType.SYSTEM,
                    NotificationPriority.HIGH,
                    "PROFILE_UPDATE_REQUEST",
                    req.getId()
            );
        }

        SupplierProfileUpdateRequest saved = profileUpdateRequestRepository.save(req);
        return mapToUpdateRequestDto(saved);
    }

    @Override
    @Transactional
    public SupplierDocumentResponse reviewDocument(Long documentId, DocumentReviewDto reviewDto) {
        SupplierDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        doc.setStatus(reviewDto.getStatus());
        if (reviewDto.getNotes() != null) {
            doc.setNotes(reviewDto.getNotes());
        }

        SupplierDocument saved = documentRepository.save(doc);
        return mapToDocumentResponse(saved);
    }

    // =========================================================================
    // DTO MAPPERS
    // =========================================================================

    private SupplierPortalEvaluationResponse mapToPortalEvaluationResponse(SupplierEvaluation ev) {
        List<SupplierPortalCriteriaScoreResponse> scoreDtos = (ev.getScores() != null ? ev.getScores() : Collections.<EvaluationScore>emptyList())
                .stream()
                .map(s -> SupplierPortalCriteriaScoreResponse.builder()
                        .criteriaId(s.getCriteria() != null ? s.getCriteria().getId() : null)
                        .criteriaName(s.getCriteria() != null ? s.getCriteria().getName() : "General Criteria")
                        .description(s.getCriteria() != null ? s.getCriteria().getDescription() : "")
                        .weight(s.getWeight())
                        .maxScore(s.getMaxScore())
                        .scoreObtained(s.getScoreObtained())
                        .weightedScore(s.getWeightedScore())
                        .remarks(s.getRemarks())
                        .build())
                .collect(Collectors.toList());

        return SupplierPortalEvaluationResponse.builder()
                .id(ev.getId())
                .evaluationCode(ev.getEvaluationCode())
                .supplierId(ev.getSupplier().getId())
                .supplierName(ev.getSupplier().getName())
                .evaluationDate(ev.getEvaluationDate())
                .evaluationPeriod(ev.getEvaluationPeriod())
                .status(ev.getStatus())
                .totalWeightedScore(ev.getTotalWeightedScore())
                .ratingCategory(ev.getRatingCategory())
                .strengths(ev.getStrengths())
                .areasForImprovement(ev.getAreasForImprovement())
                .recommendation(ev.getRecommendation())
                .criteriaScores(scoreDtos)
                .build();
    }

    private ImprovementActionResponse mapToActionResponse(SupplierImprovementAction a) {
        return ImprovementActionResponse.builder()
                .id(a.getId())
                .supplierId(a.getSupplier().getId())
                .supplierName(a.getSupplier().getName())
                .title(a.getTitle())
                .description(a.getDescription())
                .priority(a.getPriority())
                .status(a.getStatus())
                .dueDate(a.getDueDate())
                .assignedUserId(a.getAssignedUser() != null ? a.getAssignedUser().getId() : null)
                .assignedUserName(a.getAssignedUser() != null ? a.getAssignedUser().getFullName() : "Unassigned")
                .createdByUserId(a.getCreatedByUser() != null ? a.getCreatedByUser().getId() : null)
                .createdByUserName(a.getCreatedByUser() != null ? a.getCreatedByUser().getFullName() : "System")
                .resolutionNotes(a.getResolutionNotes())
                .createdAt(a.getCreatedAt())
                .completedAt(a.getCompletedAt())
                .build();
    }

    private SupplierProfileUpdateRequestDto mapToUpdateRequestDto(SupplierProfileUpdateRequest r) {
        return SupplierProfileUpdateRequestDto.builder()
                .id(r.getId())
                .supplierId(r.getSupplier().getId())
                .supplierName(r.getSupplier().getName())
                .requestedByUserId(r.getRequestedBy().getId())
                .requestedByUsername(r.getRequestedBy().getUsername())
                .contactPerson(r.getContactPerson())
                .phone(r.getPhone())
                .email(r.getEmail())
                .address(r.getAddress())
                .website(r.getWebsite())
                .city(r.getCity())
                .state(r.getState())
                .country(r.getCountry())
                .status(r.getStatus())
                .reviewerNotes(r.getReviewerNotes())
                .reviewedByUsername(r.getReviewedBy() != null ? r.getReviewedBy().getUsername() : null)
                .createdAt(r.getCreatedAt())
                .reviewedAt(r.getReviewedAt())
                .build();
    }

    private SupplierDocumentResponse mapToDocumentResponse(SupplierDocument doc) {
        return SupplierDocumentResponse.builder()
                .id(doc.getId())
                .supplierId(doc.getSupplier().getId())
                .supplierName(doc.getSupplier().getName())
                .documentName(doc.getDocumentName())
                .documentType(doc.getDocumentType())
                .contentType(doc.getContentType())
                .fileSize(doc.getFileSize())
                .uploadedByUsername(doc.getUploadedBy() != null ? doc.getUploadedBy().getUsername() : "System")
                .status(doc.getStatus())
                .notes(doc.getNotes())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    private SupplierCommunicationResponse mapToCommunicationResponse(SupplierCommunication c) {
        return SupplierCommunicationResponse.builder()
                .id(c.getId())
                .supplierId(c.getSupplier().getId())
                .supplierName(c.getSupplier().getName())
                .senderId(c.getSender().getId())
                .senderName(c.getSender().getFullName())
                .subject(c.getSubject())
                .message(c.getMessage())
                .relatedResourceType(c.getRelatedResourceType())
                .relatedResourceId(c.getRelatedResourceId())
                .isFromSupplier(c.isFromSupplier())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
