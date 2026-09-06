import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { WorkflowDashboard } from '../pages/workflows/WorkflowDashboard';
import { MyApprovals } from '../pages/workflows/MyApprovals';
import { WorkflowConfigAdmin } from '../pages/workflows/WorkflowConfigAdmin';
import { workflowService } from '../services/workflow.service';

vi.mock('../services/workflow.service', () => {
  const service = {
    getWorkflows: vi.fn(),
    getWorkflowSummary: vi.fn(),
    getWorkflowById: vi.fn(),
    getMyPendingApprovals: vi.fn(),
    getMyApprovalHistory: vi.fn(),
    approveTask: vi.fn(),
    rejectTask: vi.fn(),
    escalateTask: vi.fn(),
    checkOverdueTasks: vi.fn(),
    getWorkflowDefinitions: vi.fn(),
    getWorkflowDefinitionById: vi.fn(),
    createWorkflowDefinition: vi.fn(),
    updateWorkflowDefinition: vi.fn(),
    toggleWorkflowDefinition: vi.fn(),
    deleteWorkflowDefinition: vi.fn(),
  };
  return {
    default: service,
    workflowService: service,
  };
});

// Mock AuthContext
vi.mock('../context/AuthContext', () => ({
  useAuth: () => ({
    user: { id: 1, username: 'admin', roles: ['ROLE_ADMIN'] },
    isAdmin: () => true,
    isManager: () => true,
    isSupplier: () => false,
  })
}));

describe('Workflow Management Component Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders WorkflowDashboard with metrics and workflow instance table', async () => {
    workflowService.getWorkflowSummary.mockResolvedValue({
      success: true,
      data: {
        totalWorkflows: 15,
        inProgressWorkflows: 6,
        approvedWorkflows: 7,
        rejectedWorkflows: 1,
        escalatedWorkflows: 1,
        slaComplianceRate: 93.3,
        avgApprovalTimeHours: 4.2
      }
    });

    workflowService.getWorkflows.mockResolvedValue({
      success: true,
      data: {
        content: [
          {
            id: 101,
            workflowType: 'SUPPLIER_PROFILE_UPDATE',
            workflowName: 'Supplier Profile Update',
            title: 'Profile Update - Apex Micro',
            status: 'IN_PROGRESS',
            currentStepOrder: 1,
            totalSteps: 2,
            currentStepName: 'Manager Review',
            initiatorUsername: 'apex_supplier',
            supplierName: 'Apex Microelectronics',
            createdAt: '2026-09-06T09:00:00',
            slaHoursRemaining: 18.5,
            slaBreached: false
          }
        ],
        totalPages: 1,
        totalElements: 1,
        number: 0,
        size: 10
      }
    });

    render(
      <BrowserRouter>
        <WorkflowDashboard />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Workflow & Multi-Level Approvals')).toBeInTheDocument();
      expect(screen.getByText('15')).toBeInTheDocument();
      expect(screen.getByText('Profile Update - Apex Micro')).toBeInTheDocument();
      expect(screen.getByText('Manager Review')).toBeInTheDocument();
    });
  });

  it('renders MyApprovals with pending tasks and action buttons', async () => {
    workflowService.getMyPendingApprovals.mockResolvedValue({
      success: true,
      data: {
        content: [
          {
            id: 201,
            stepName: 'Finance Verification',
            workflowType: 'SUPPLIER_DOCUMENT_REVIEW',
            workflowTitle: 'ISO 9001 Certificate Review',
            supplierName: 'Apex Microelectronics',
            initiatorUsername: 'sarah_admin',
            requiredRole: 'ROLE_ADMIN',
            status: 'PENDING',
            assignedAt: '2026-09-06T08:00:00',
            dueAt: '2026-09-07T08:00:00',
            slaHoursRemaining: 22.0,
            slaBreached: false,
            workflowInstanceId: 101
          }
        ],
        totalPages: 1,
        totalElements: 1,
        number: 0,
        size: 10
      }
    });

    render(
      <BrowserRouter>
        <MyApprovals />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('My Approvals & Delegated Tasks')).toBeInTheDocument();
      expect(screen.getByText('ISO 9001 Certificate Review')).toBeInTheDocument();
      expect(screen.getByText(/Finance Verification/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /Approve/i })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /Reject/i })).toBeInTheDocument();
    });
  });

  it('renders WorkflowConfigAdmin with workflow templates and steps', async () => {
    workflowService.getWorkflowDefinitions.mockResolvedValue({
      success: true,
      data: [
        {
          id: 1,
          name: 'Supplier Profile Update Workflow',
          workflowType: 'SUPPLIER_PROFILE_UPDATE',
          description: 'Standard 2-tier approval for supplier profile updates',
          active: true,
          slaHours: 24,
          stepsCount: 2,
          steps: [
            { id: 11, stepOrder: 1, stepName: 'Manager Review', requiredRole: 'ROLE_MANAGER', slaHours: 12 },
            { id: 12, stepOrder: 2, stepName: 'Admin Approval', requiredRole: 'ROLE_ADMIN', slaHours: 12 }
          ]
        }
      ]
    });

    render(
      <BrowserRouter>
        <WorkflowConfigAdmin />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Workflow Definitions & SLA Rules')).toBeInTheDocument();
      expect(screen.getByText('Supplier Profile Update Workflow')).toBeInTheDocument();
      expect(screen.getByText('Standard 2-tier approval for supplier profile updates')).toBeInTheDocument();
    });
  });
});
