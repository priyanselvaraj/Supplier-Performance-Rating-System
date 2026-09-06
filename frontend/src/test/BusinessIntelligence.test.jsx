import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { BiDashboard } from '../pages/bi/BiDashboard';
import { SupplierComparison } from '../pages/bi/SupplierComparison';
import { PerformanceBenchmarking } from '../pages/bi/PerformanceBenchmarking';
import { ReportBuilder } from '../pages/bi/ReportBuilder';
import { SavedReports } from '../pages/bi/SavedReports';
import { ExecutiveDashboard } from '../pages/bi/ExecutiveDashboard';
import { KpiAdmin } from '../pages/bi/KpiAdmin';
import { biService } from '../services/bi.service';

// Mock biService
vi.mock('../services/bi.service', () => {
  const service = {
    getBiDashboardSummary: vi.fn(),
    calculateAllActiveKpis: vi.fn(),
    calculateKpi: vi.fn(),
    calculateKpiById: vi.fn(),
    getPerformanceTrends: vi.fn(),
    compareSuppliers: vi.fn(),
    getSupplierBenchmark: vi.fn(),
    generateReportPreview: vi.fn(),
    getSavedReports: vi.fn(),
    getSavedReportById: vi.fn(),
    saveReport: vi.fn(),
    updateSavedReport: vi.fn(),
    deleteSavedReport: vi.fn(),
    getExecutiveDashboard: vi.fn(),
    getAllKpiDefinitions: vi.fn(),
    getKpiDefinitionById: vi.fn(),
    createKpiDefinition: vi.fn(),
    updateKpiDefinition: vi.fn(),
    toggleKpiDefinition: vi.fn(),
    deleteKpiDefinition: vi.fn(),
  };
  return {
    default: service,
    biService: service,
  };
});

// Mock supplierService
vi.mock('../services/supplier.service', () => {
  const service = {
    getAllSuppliers: vi.fn().mockResolvedValue({
      data: [
        { id: 1, name: 'Global Microchips Corp', supplierCode: 'SUP-001', category: 'Hardware' },
        { id: 2, name: 'Acme Hardware Ltd', supplierCode: 'SUP-002', category: 'Hardware' }
      ]
    }),
    getSupplierById: vi.fn(),
  };
  return {
    default: service,
    supplierService: service,
  };
});

// Mock categoryService
vi.mock('../services/category.service', () => {
  const service = {
    getAllCategories: vi.fn().mockResolvedValue({
      data: [
        { id: 1, name: 'Electronics' },
        { id: 2, name: 'Hardware' }
      ]
    }),
  };
  return {
    default: service,
    categoryService: service,
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

// Mock react-chartjs-2
vi.mock('react-chartjs-2', () => ({
  Doughnut: () => <div data-testid="mock-doughnut-chart">Doughnut Chart</div>,
  Bar: () => <div data-testid="mock-bar-chart">Bar Chart</div>,
  Line: () => <div data-testid="mock-line-chart">Line Chart</div>,
  Radar: () => <div data-testid="mock-radar-chart">Radar Chart</div>,
}));

describe('Business Intelligence & Executive Analytics Component Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders BiDashboard with KPI cards and performance distributions', async () => {
    biService.getBiDashboardSummary.mockResolvedValue({
      totalSuppliers: 42,
      activeSuppliers: 38,
      averageSupplierScore: 88.5,
      totalEvaluations: 120,
      highRiskSuppliersCount: 2,
      openImprovementActionsCount: 8,
      slaComplianceRate: 95.0,
      pendingApprovalsCount: 3,
      topKpis: [
        {
          kpiCode: 'OTD_RATE',
          name: 'On-Time Delivery Rate',
          category: 'DELIVERY',
          value: 94.2,
          targetValue: 90.0,
          unit: '%',
          status: 'GOOD',
          sampleCount: 40,
          statusReason: 'Achieved SLA targets consistently.'
        },
        {
          kpiCode: 'HIGH_RISK_COUNT',
          name: 'High Risk Suppliers',
          category: 'RISK',
          value: 2.0,
          targetValue: 0.0,
          unit: 'vendors',
          status: 'CRITICAL',
          sampleCount: 42,
          statusReason: '2 suppliers flagged high risk.'
        }
      ],
      ratingDistribution: [
        { ratingCategory: 'EXCELLENT', supplierCount: 15, percentage: 50.0 }
      ],
      categoryBreakdown: [
        { categoryName: 'Hardware', averageRating: 88.5 }
      ],
      topPerformingSuppliers: [
        { id: 1, name: 'Global Microchips Corp', overallRating: 96.4, supplierCode: 'SUP-001', categoryName: 'Hardware' }
      ],
      lowPerformingSuppliers: [
        { id: 2, name: 'Apex Logistics Inc', overallRating: 54.0, supplierCode: 'SUP-002', categoryName: 'Logistics' }
      ]
    });

    render(
      <BrowserRouter>
        <BiDashboard />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Business Intelligence & KPI Hub')).toBeInTheDocument();
      expect(screen.getByText('On-Time Delivery Rate')).toBeInTheDocument();
      expect(screen.getByText('High Risk Suppliers')).toBeInTheDocument();
      expect(screen.getByText('Global Microchips Corp')).toBeInTheDocument();
    });
  });

  it('renders SupplierComparison with comparative matrices and metrics', async () => {
    biService.compareSuppliers.mockResolvedValue({
      suppliers: [
        {
          supplierId: 1,
          supplierName: 'Global Microchips Corp',
          supplierCode: 'SUP-001',
          category: 'Hardware',
          overallScore: 94.5,
          ratingGrade: 'A',
          riskLevel: 'LOW',
          onTimeDeliveryRate: 98.0,
          qualityScore: 95.0,
          capResolutionRate: 100.0,
          slaComplianceRate: 96.0,
          criteriaScores: { Quality: 95.0, Delivery: 98.0 }
        },
        {
          supplierId: 2,
          supplierName: 'Acme Hardware Ltd',
          supplierCode: 'SUP-002',
          category: 'Hardware',
          overallScore: 78.2,
          ratingGrade: 'C',
          riskLevel: 'MEDIUM',
          onTimeDeliveryRate: 82.0,
          qualityScore: 80.0,
          capResolutionRate: 75.0,
          slaComplianceRate: 85.0,
          criteriaScores: { Quality: 80.0, Delivery: 82.0 }
        }
      ],
      criteriaNames: ['Quality', 'Delivery'],
      criteriaBreakdown: [
        { criteriaName: 'Quality', category: 'General', scores: { 1: 95.0, 2: 80.0 } },
        { criteriaName: 'Delivery', category: 'General', scores: { 1: 98.0, 2: 82.0 } }
      ],
      highestRatedSupplier: 'Global Microchips Corp',
      lowestRiskSupplier: 'Global Microchips Corp',
      comparisonSummary: 'Global Microchips Corp leads in all operational dimensions.'
    });

    render(
      <BrowserRouter>
        <SupplierComparison />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Supplier Comparison Matrix')).toBeInTheDocument();
      expect(screen.getByText('Side-by-Side Analytics')).toBeInTheDocument();
    });
  });

  it('renders PerformanceBenchmarking with percentile metrics and gap analysis', async () => {
    biService.getSupplierBenchmark.mockResolvedValue({
      supplierId: 1,
      supplierName: 'Global Microchips Corp',
      categoryName: 'Electronics',
      supplierScore: 92.5,
      categoryAverageScore: 81.0,
      overallAverageScore: 76.5,
      categoryDelta: 11.5,
      overallDelta: 16.0,
      categoryPercentile: 94.0,
      enterprisePercentile: 96.5,
      performanceTier: 'TOP_DECILE',
      criteriaBenchmarks: [
        {
          criteriaName: 'Quality Assurance',
          supplierScore: 95.0,
          categoryAverage: 82.0,
          overallAverage: 78.0,
          deltaVsCategory: 13.0,
          deltaVsOverall: 17.0
        }
      ],
      strengths: ['Superior Quality Assurance'],
      gaps: ['Maintain standard SLA turnaround']
    });

    render(
      <BrowserRouter>
        <PerformanceBenchmarking />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Performance Benchmarking')).toBeInTheDocument();
      expect(screen.getByText(/Industry & Category Benchmarking/i)).toBeInTheDocument();
    });
  });

  it('renders ReportBuilder with configuration parameters and preview canvas', async () => {
    biService.generateReportPreview.mockResolvedValue({
      reportTitle: 'Executive Performance Summary',
      generatedAt: '2026-09-06T10:00:00',
      totalRecords: 2,
      columns: ['Supplier Name', 'Score', 'Rating Grade'],
      chartType: 'BAR',
      chartData: {
        labels: ['Apex Micro', 'Beta Tech'],
        series: [{ name: 'Score', data: [92.0, 78.5] }]
      },
      rows: [
        { 'Supplier Name': 'Apex Micro', Score: 92.0, 'Rating Grade': 'A' },
        { 'Supplier Name': 'Beta Tech', Score: 78.5, 'Rating Grade': 'C' }
      ]
    });

    render(
      <BrowserRouter>
        <ReportBuilder />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Dynamic Report Builder')).toBeInTheDocument();
      expect(screen.getByText('Query & Filter Blueprint')).toBeInTheDocument();
    });
  });

  it('renders SavedReports gallery with 1-click execution', async () => {
    biService.getSavedReports.mockResolvedValue([
      {
        id: 1,
        name: 'Quarterly Vendor Risk Analysis',
        description: 'Automated executive scorecard of high risk suppliers',
        reportType: 'RISK_ANALYSIS',
        scope: 'ALL_SUPPLIERS',
        visualization: 'BAR_CHART',
        isPublic: true,
        createdByUsername: 'admin',
        createdAt: '2026-09-01T12:00:00'
      }
    ]);

    render(
      <BrowserRouter>
        <SavedReports />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Saved Intelligence Reports')).toBeInTheDocument();
      expect(screen.getByText('Quarterly Vendor Risk Analysis')).toBeInTheDocument();
      expect(screen.getByText('Run Report')).toBeInTheDocument();
    });
  });

  it('renders ExecutiveDashboard with C-suite overview and strategic insights', async () => {
    biService.getExecutiveDashboard.mockResolvedValue({
      supplierOverview: {
        totalSuppliers: 42,
        activeSuppliers: 38,
        newSuppliersLast30Days: 4,
        overallAverageRating: 84.6,
        topCategory: 'Electronics',
        categoryHealthScore: 89.2
      },
      riskOverview: {
        highRiskSuppliers: 2,
        mediumRiskSuppliers: 6,
        lowRiskSuppliers: 34,
        averageRiskScore: 18.4,
        criticalAlertsCount: 1,
        highRiskVendors: ['Vendor Alpha', 'Vendor Delta']
      },
      performanceOverview: {
        improvingSuppliersCount: 20,
        stableSuppliersCount: 18,
        decliningSuppliersCount: 4,
        averageScoreQuarterChange: 3.2,
        ratingDistribution: [{ ratingLevel: 'EXCELLENT', count: 15 }],
        topSuppliers: [{ supplierName: 'Elite Semiconductor', score: 98.2, ratingGrade: 'A' }],
        bottomSuppliers: [{ supplierName: 'Slow Logistics Co', score: 48.0, ratingGrade: 'D' }]
      },
      operationalOverview: {
        openImprovementActions: 8,
        overdueImprovementActions: 1,
        capClosureRate: 92.4,
        pendingWorkflowApprovals: 3,
        escalatedWorkflows: 0,
        workflowSlaComplianceRate: 97.5
      },
      strategicInsights: [
        {
          type: 'RISK',
          title: 'Critical Supply Redundancy Risk',
          description: 'Single-source dependency detected for micro-controllers.',
          actionRecommendation: 'Initiate vendor qualification workflow for backup suppliers.'
        }
      ]
    });

    render(
      <BrowserRouter>
        <ExecutiveDashboard />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('C-Suite Executive Overview')).toBeInTheDocument();
      expect(screen.getByText('42')).toBeInTheDocument();
      expect(screen.getByText('Critical Supply Redundancy Risk')).toBeInTheDocument();
      expect(screen.getByText('Elite Semiconductor')).toBeInTheDocument();
    });
  });

  it('renders KpiAdmin with KPI definition table and threshold manager', async () => {
    biService.getAllKpiDefinitions.mockResolvedValue([
      {
        id: 1,
        kpiCode: 'OTD_RATE',
        name: 'On-Time Delivery Rate',
        category: 'DELIVERY',
        calculationType: 'ON_TIME_DELIVERY_RATE',
        unit: '%',
        targetValue: 95.0,
        warningThreshold: 88.0,
        criticalThreshold: 80.0,
        higherIsBetter: true,
        active: true
      }
    ]);

    render(
      <BrowserRouter>
        <KpiAdmin />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('KPI Definition & Threshold Manager')).toBeInTheDocument();
      expect(screen.getByText('On-Time Delivery Rate')).toBeInTheDocument();
      expect(screen.getByText('OTD_RATE')).toBeInTheDocument();
      expect(screen.getByText('95 %')).toBeInTheDocument();
    });
  });
});
