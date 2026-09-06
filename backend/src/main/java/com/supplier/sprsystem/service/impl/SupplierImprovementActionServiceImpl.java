package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.request.ImprovementActionRequest;
import com.supplier.sprsystem.dto.response.ImprovementActionResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.SupplierImprovementActionRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.service.NotificationService;
import com.supplier.sprsystem.service.SupplierImprovementActionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplierImprovementActionServiceImpl implements SupplierImprovementActionService {

    private final SupplierImprovementActionRepository actionRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public SupplierImprovementActionServiceImpl(SupplierImprovementActionRepository actionRepository,
                                               SupplierRepository supplierRepository,
                                               UserRepository userRepository,
                                               NotificationService notificationService) {
        this.actionRepository = actionRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ImprovementActionResponse createAction(ImprovementActionRequest request, Long currentUserId) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));

        User createdBy = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        User assignedUser = null;
        if (request.getAssignedUserId() != null) {
            assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedUserId()));
        }

        SupplierImprovementAction action = SupplierImprovementAction.builder()
                .supplier(supplier)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : ImprovementActionPriority.MEDIUM)
                .status(request.getStatus() != null ? request.getStatus() : ImprovementActionStatus.OPEN)
                .assignedUser(assignedUser)
                .createdByUser(createdBy)
                .dueDate(request.getDueDate())
                .resolutionNotes(request.getResolutionNotes())
                .build();

        SupplierImprovementAction saved = actionRepository.save(action);

        // Notify assigned user if specified
        if (assignedUser != null) {
            NotificationPriority notifPriority = mapPriority(saved.getPriority());
            notificationService.createNotification(
                    assignedUser.getId(),
                    "New Improvement Action Assigned",
                    String.format("You have been assigned action '%s' for supplier %s (Due: %s).",
                            saved.getTitle(), supplier.getName(), saved.getDueDate() != null ? saved.getDueDate() : "Not specified"),
                    NotificationType.IMPROVEMENT_ACTION,
                    notifPriority,
                    "IMPROVEMENT_ACTION",
                    saved.getId()
            );
        }

        return mapToResponse(saved);
    }

    @Override
    public ImprovementActionResponse updateAction(Long id, ImprovementActionRequest request, Long currentUserId) {
        SupplierImprovementAction action = actionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierImprovementAction", "id", id));

        if (request.getSupplierId() != null && !action.getSupplier().getId().equals(request.getSupplierId())) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));
            action.setSupplier(supplier);
        }

        action.setTitle(request.getTitle());
        action.setDescription(request.getDescription());
        if (request.getPriority() != null) action.setPriority(request.getPriority());
        if (request.getStatus() != null) {
            action.setStatus(request.getStatus());
            if (request.getStatus() == ImprovementActionStatus.COMPLETED && action.getCompletedAt() == null) {
                action.setCompletedAt(LocalDateTime.now());
            }
        }
        if (request.getDueDate() != null) action.setDueDate(request.getDueDate());
        if (request.getResolutionNotes() != null) action.setResolutionNotes(request.getResolutionNotes());

        if (request.getAssignedUserId() != null) {
            User assigned = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedUserId()));
            action.setAssignedUser(assigned);
        }

        SupplierImprovementAction updated = actionRepository.save(action);
        return mapToResponse(updated);
    }

    @Override
    public ImprovementActionResponse updateActionStatus(Long id, ImprovementActionStatus status, String resolutionNotes, Long currentUserId) {
        SupplierImprovementAction action = actionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierImprovementAction", "id", id));

        action.setStatus(status);
        if (resolutionNotes != null) {
            action.setResolutionNotes(resolutionNotes);
        }
        if (status == ImprovementActionStatus.COMPLETED) {
            action.setCompletedAt(LocalDateTime.now());
        }

        SupplierImprovementAction updated = actionRepository.save(action);

        // Notify action creator if status is completed
        if (action.getCreatedByUser() != null && !action.getCreatedByUser().getId().equals(currentUserId)) {
            notificationService.createNotification(
                    action.getCreatedByUser().getId(),
                    "Improvement Action " + status.getDisplayName(),
                    String.format("Action '%s' for supplier %s has been updated to %s.",
                            action.getTitle(), action.getSupplier().getName(), status.getDisplayName()),
                    NotificationType.IMPROVEMENT_ACTION,
                    NotificationPriority.MEDIUM,
                    "IMPROVEMENT_ACTION",
                    action.getId()
            );
        }

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ImprovementActionResponse getActionById(Long id) {
        SupplierImprovementAction action = actionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierImprovementAction", "id", id));
        return mapToResponse(action);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ImprovementActionResponse> getActions(int page, int size, Long supplierId, ImprovementActionStatus status, ImprovementActionPriority priority, Long assignedUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SupplierImprovementAction> actionPage = actionRepository.filterActions(supplierId, status, priority, assignedUserId, pageable);

        List<ImprovementActionResponse> content = actionPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                content,
                actionPage.getNumber(),
                actionPage.getSize(),
                actionPage.getTotalElements(),
                actionPage.getTotalPages(),
                actionPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImprovementActionResponse> getActionsBySupplierId(Long supplierId) {
        return actionRepository.findBySupplierIdOrderByCreatedAtDesc(supplierId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAction(Long id, Long currentUserId) {
        SupplierImprovementAction action = actionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierImprovementAction", "id", id));
        actionRepository.delete(action);
    }

    private NotificationPriority mapPriority(ImprovementActionPriority p) {
        if (p == null) return NotificationPriority.MEDIUM;
        switch (p) {
            case CRITICAL: return NotificationPriority.CRITICAL;
            case HIGH: return NotificationPriority.HIGH;
            case LOW: return NotificationPriority.LOW;
            default: return NotificationPriority.MEDIUM;
        }
    }

    private ImprovementActionResponse mapToResponse(SupplierImprovementAction a) {
        return ImprovementActionResponse.builder()
                .id(a.getId())
                .supplierId(a.getSupplier().getId())
                .supplierName(a.getSupplier().getName())
                .supplierCode(a.getSupplier().getSupplierCode())
                .title(a.getTitle())
                .description(a.getDescription())
                .priority(a.getPriority())
                .status(a.getStatus())
                .assignedUserId(a.getAssignedUser() != null ? a.getAssignedUser().getId() : null)
                .assignedUserName(a.getAssignedUser() != null ? a.getAssignedUser().getFullName() : null)
                .createdByUserId(a.getCreatedByUser() != null ? a.getCreatedByUser().getId() : null)
                .createdByUserName(a.getCreatedByUser() != null ? a.getCreatedByUser().getFullName() : null)
                .dueDate(a.getDueDate())
                .completedAt(a.getCompletedAt())
                .resolutionNotes(a.getResolutionNotes())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
