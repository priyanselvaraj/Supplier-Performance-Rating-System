package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.ImprovementActionRequest;
import com.supplier.sprsystem.dto.response.ImprovementActionResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.SupplierImprovementActionRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.service.impl.SupplierImprovementActionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplierImprovementActionServiceTest {

    @Mock
    private SupplierImprovementActionRepository actionRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SupplierImprovementActionServiceImpl actionService;

    private Supplier mockSupplier;
    private User mockUser;
    private SupplierImprovementAction mockAction;

    @BeforeEach
    void setUp() {
        mockSupplier = Supplier.builder()
                .id(1L)
                .name("Global Micro")
                .supplierCode("SUP-00001")
                .build();

        mockUser = User.builder()
                .id(1L)
                .username("admin")
                .fullName("Admin User")
                .build();

        mockAction = SupplierImprovementAction.builder()
                .id(10L)
                .supplier(mockSupplier)
                .title("Conduct Quality Review")
                .description("Review defect rates with QA team")
                .priority(ImprovementActionPriority.HIGH)
                .status(ImprovementActionStatus.OPEN)
                .createdByUser(mockUser)
                .assignedUser(mockUser)
                .dueDate(LocalDate.now().plusDays(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("ACTION-01: Create improvement action saves record and notifies assignee")
    void testCreateAction() {
        ImprovementActionRequest req = new ImprovementActionRequest(
                1L, "Conduct Quality Review", "Review defect rates",
                ImprovementActionPriority.HIGH, ImprovementActionStatus.OPEN, 1L, LocalDate.now().plusDays(10), null
        );

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(mockSupplier));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(actionRepository.save(any(SupplierImprovementAction.class))).thenReturn(mockAction);

        ImprovementActionResponse res = actionService.createAction(req, 1L);

        assertThat(res).isNotNull();
        assertThat(res.getTitle()).isEqualTo("Conduct Quality Review");
        assertThat(res.getPriority()).isEqualTo(ImprovementActionPriority.HIGH);
        verify(actionRepository, times(1)).save(any(SupplierImprovementAction.class));
        verify(notificationService, times(1)).createNotification(eq(1L), anyString(), anyString(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("ACTION-02: Update action status to COMPLETED records completed timestamp")
    void testUpdateActionStatus() {
        when(actionRepository.findById(10L)).thenReturn(Optional.of(mockAction));
        when(actionRepository.save(any(SupplierImprovementAction.class))).thenReturn(mockAction);

        ImprovementActionResponse res = actionService.updateActionStatus(10L, ImprovementActionStatus.COMPLETED, "Resolved successfully", 1L);

        assertThat(res).isNotNull();
        assertThat(mockAction.getStatus()).isEqualTo(ImprovementActionStatus.COMPLETED);
        assertThat(mockAction.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("ACTION-03: Retrieve paginated improvement actions")
    void testGetActions() {
        Page<SupplierImprovementAction> page = new PageImpl<>(List.of(mockAction));
        when(actionRepository.filterActions(eq(null), eq(null), eq(null), eq(null), any(Pageable.class))).thenReturn(page);

        PaginatedResponse<ImprovementActionResponse> res = actionService.getActions(0, 10, null, null, null, null);

        assertThat(res).isNotNull();
        assertThat(res.getContent()).hasSize(1);
        assertThat(res.getTotalElements()).isEqualTo(1);
    }
}
