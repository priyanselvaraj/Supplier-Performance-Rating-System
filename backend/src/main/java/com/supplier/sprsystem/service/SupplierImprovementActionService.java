package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.ImprovementActionRequest;
import com.supplier.sprsystem.dto.response.ImprovementActionResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.model.entity.ImprovementActionPriority;
import com.supplier.sprsystem.model.entity.ImprovementActionStatus;

import java.util.List;

public interface SupplierImprovementActionService {

    ImprovementActionResponse createAction(ImprovementActionRequest request, Long currentUserId);

    ImprovementActionResponse updateAction(Long id, ImprovementActionRequest request, Long currentUserId);

    ImprovementActionResponse updateActionStatus(Long id, ImprovementActionStatus status, String resolutionNotes, Long currentUserId);

    ImprovementActionResponse getActionById(Long id);

    PaginatedResponse<ImprovementActionResponse> getActions(int page, int size, Long supplierId, ImprovementActionStatus status, ImprovementActionPriority priority, Long assignedUserId);

    List<ImprovementActionResponse> getActionsBySupplierId(Long supplierId);

    void deleteAction(Long id, Long currentUserId);
}
