import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { SupplierDashboard } from '../pages/supplier-portal/SupplierDashboard';
import { SupplierProfile } from '../pages/supplier-portal/SupplierProfile';
import { SupplierEvaluations } from '../pages/supplier-portal/SupplierEvaluations';
import { SupplierDocuments } from '../pages/supplier-portal/SupplierDocuments';
import { SupplierImprovementActions } from '../pages/supplier-portal/SupplierImprovementActions';
import supplierPortalService from '../services/supplierPortal.service';

vi.mock('../services/supplierPortal.service', () => ({
  default: {
    getDashboard: vi.fn(),
    getProfile: vi.fn(),
    getPerformance: vi.fn(),
    getEvaluations: vi.fn(),
    getEvaluationDetails: vi.fn(),
    getImprovementActions: vi.fn(),
    getDocuments: vi.fn(),
    getCommunications: vi.fn(),
    getNotifications: vi.fn(),
  },
  supplierPortalService: {
    getDashboard: vi.fn(),
    getProfile: vi.fn(),
    getPerformance: vi.fn(),
    getEvaluations: vi.fn(),
    getEvaluationDetails: vi.fn(),
    getImprovementActions: vi.fn(),
    getDocuments: vi.fn(),
    getCommunications: vi.fn(),
    getNotifications: vi.fn(),
  }
}));

describe('Supplier Portal Component Tests', () => {
  it('renders SupplierDashboard with KPI metrics', async () => {
    supplierPortalService.getDashboard.mockResolvedValue({
      success: true,
      data: {
        supplierId: 100,
        supplierCode: 'SUP-10001',
        supplierName: 'Apex Microelectronics Inc.',
        categoryName: 'Electronics',
        overallRating: 92.5,
        ratingCategory: 'EXCELLENT',
        performanceStatus: 'HIGH_PERFORMING',
        totalEvaluations: 4,
        scoreDifference: 2.5,
        performanceTrend: 'IMPROVING',
        openActionsCount: 1,
        completedActionsCount: 2,
        totalDocumentsCount: 3,
        unreadNotificationsCount: 0,
        recentEvaluations: [
          {
            id: 1,
            evaluationCode: 'EV-10001',
            evaluationPeriod: '2026-Q1',
            evaluationDate: '2026-03-31',
            totalWeightedScore: 92.5,
            ratingCategory: 'EXCELLENT',
          },
        ],
        urgentActions: [],
      },
    });

    render(
      <BrowserRouter>
        <SupplierDashboard />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Apex Microelectronics Inc.')).toBeInTheDocument();
      expect(screen.getAllByText('92.5').length).toBeGreaterThan(0);
      expect(screen.getByText('EV-10001')).toBeInTheDocument();
    });
  });

  it('renders SupplierProfile with verified company details', async () => {
    supplierPortalService.getProfile.mockResolvedValue({
      success: true,
      data: {
        id: 100,
        supplierCode: 'SUP-10001',
        name: 'Apex Microelectronics Inc.',
        contactPerson: 'Sarah Jenkins',
        email: 'orders@apexmicro.com',
        phone: '+1 408-555-0144',
        categoryName: 'Electronics',
        overallRating: 92.5,
        ratingCategory: 'EXCELLENT',
        totalEvaluations: 4,
        hasPendingUpdateRequest: false,
        updateRequests: [],
      },
    });

    render(
      <BrowserRouter>
        <SupplierProfile />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Sarah Jenkins')).toBeInTheDocument();
      expect(screen.getByText('orders@apexmicro.com')).toBeInTheDocument();
      expect(screen.getByText(/Request Profile Update/i)).toBeInTheDocument();
    });
  });

  it('renders SupplierEvaluations scorecard list', async () => {
    supplierPortalService.getEvaluations.mockResolvedValue({
      success: true,
      data: [
        {
          id: 1,
          evaluationCode: 'EV-10001',
          evaluationPeriod: '2026-Q1',
          evaluationDate: '2026-03-31',
          totalWeightedScore: 92.5,
          ratingCategory: 'EXCELLENT',
          strengths: 'Excellent delivery and high quality.',
        },
      ],
    });

    render(
      <BrowserRouter>
        <SupplierEvaluations />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('EV-10001')).toBeInTheDocument();
      expect(screen.getByText(/Inspect Scorecard/i)).toBeInTheDocument();
    });
  });

  it('renders SupplierDocuments list and upload button', async () => {
    supplierPortalService.getDocuments.mockResolvedValue({
      success: true,
      data: [
        {
          id: 10,
          documentName: 'ISO-9001-Cert.pdf',
          documentType: 'Quality Certification',
          fileSize: 2048000,
          status: 'ACTIVE',
          createdAt: '2026-01-15T10:00:00',
        },
      ],
    });

    render(
      <BrowserRouter>
        <SupplierDocuments />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('ISO-9001-Cert.pdf')).toBeInTheDocument();
      expect(screen.getByText('Upload Document')).toBeInTheDocument();
    });
  });

  it('renders SupplierImprovementActions with response capability', async () => {
    supplierPortalService.getImprovementActions.mockResolvedValue({
      success: true,
      data: [
        {
          id: 5,
          title: 'Batch Spectrometer Testing',
          description: 'Ensure lab purity certification on dispatch.',
          priority: 'HIGH',
          status: 'OPEN',
          dueDate: '2026-04-15',
        },
      ],
    });

    render(
      <BrowserRouter>
        <SupplierImprovementActions />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Batch Spectrometer Testing')).toBeInTheDocument();
      expect(screen.getByText('Submit Response')).toBeInTheDocument();
    });
  });
});
