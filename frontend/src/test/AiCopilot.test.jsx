import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { AiCopilotPage } from '../pages/ai/AiCopilotPage';
import { ExecutiveInsightsPage } from '../pages/ai/ExecutiveInsightsPage';
import { aiService } from '../services/ai.service';

vi.mock('../services/ai.service', () => ({
  aiService: {
    getCopilotHistory: vi.fn().mockResolvedValue({ success: true, data: [] }),
    queryCopilot: vi.fn().mockResolvedValue({
      success: true,
      data: {
        id: 1,
        question: 'Which suppliers are performing poorly?',
        answer: '### Underperforming Suppliers\n- Apex Logistics (68.5%)',
        queryIntent: 'Identify Underperforming Suppliers',
        intentCategory: 'PERFORMANCE_ANALYSIS',
        confidence: 'HIGH',
        dataCitations: ['Evaluated across 5 suppliers'],
        suggestedActions: ['Initiate CAP review'],
        followUpQuestions: ['Why is Apex high risk?'],
        createdAt: new Date().toISOString()
      }
    }),
    getExecutiveAiInsights: vi.fn().mockResolvedValue({
      success: true,
      data: {
        generatedAt: new Date().toISOString(),
        executiveSummary: 'SPRS portfolio average is 82.5%.',
        totalSuppliersEvaluated: 12,
        portfolioAverageScore: 82.5,
        highRiskSuppliersCount: 2,
        criticalAlertsCount: 3,
        overdueWorkflowsCount: 1,
        openImprovementActionsCount: 4,
        keyRisks: ['2 suppliers breached risk threshold'],
        performanceHighlights: ['Logistics lead times improved'],
        areasRequiringAttention: ['Invoice turnaround delays'],
        strategicRecommendations: [
          {
            priority: 'HIGH',
            title: 'SLA Enforcement',
            rationale: 'Elevated variance',
            suggestedAction: 'Mandate monthly audits'
          }
        ]
      }
    })
  }
}));

describe('AI Copilot & Decision Support Frontend Tests', () => {
  it('renders AI Copilot header and quick prompt chips', async () => {
    render(
      <BrowserRouter>
        <AiCopilotPage />
      </BrowserRouter>
    );

    expect(screen.getByText('Supplier Management Copilot')).toBeInTheDocument();
    const queryElements = screen.getAllByText('Which suppliers are performing poorly?');
    expect(queryElements.length).toBeGreaterThanOrEqual(1);
    expect(screen.getByPlaceholderText(/Ask any supplier performance/i)).toBeInTheDocument();
  });

  it('renders welcome message in Copilot chat container', async () => {
    render(
      <BrowserRouter>
        <AiCopilotPage />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Welcome to SPRS Supplier Management AI Copilot/i)).toBeInTheDocument();
    });
  });

  it('renders Executive AI Insights Dashboard correctly', async () => {
    render(
      <BrowserRouter>
        <ExecutiveInsightsPage />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Executive AI Decision Support')).toBeInTheDocument();
      expect(screen.getByText(/SPRS portfolio average is 82.5%./i)).toBeInTheDocument();
      expect(screen.getByText('Portfolio Health')).toBeInTheDocument();
      expect(screen.getByText('High Risk Vendors')).toBeInTheDocument();
    });
  });
});
