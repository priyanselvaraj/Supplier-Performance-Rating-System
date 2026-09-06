import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { IntegrationsDashboard } from '../pages/integrations/IntegrationsDashboard';
import { ApiKeyManagerModal } from '../pages/integrations/ApiKeyManagerModal';
import { WebhookModal } from '../pages/integrations/WebhookModal';
import { SupplierSyncModal } from '../pages/integrations/SupplierSyncModal';
import integrationService from '../services/integration.service';

vi.mock('../services/integration.service', () => {
  const service = {
    getHealthSummary: vi.fn(),
    getApiKeys: vi.fn(),
    createApiKey: vi.fn(),
    toggleApiKeyStatus: vi.fn(),
    deleteApiKey: vi.fn(),
    getWebhooks: vi.fn(),
    createWebhook: vi.fn(),
    updateWebhook: vi.fn(),
    toggleWebhookStatus: vi.fn(),
    deleteWebhook: vi.fn(),
    testWebhook: vi.fn(),
    getDeliveryLogs: vi.fn(),
    getDeliveryLogsBySubscription: vi.fn(),
    syncSuppliers: vi.fn(),
    getSyncHistory: vi.fn(),
    getSyncHistoryById: vi.fn()
  };
  return {
    default: service
  };
});

const mockHealthSummary = {
  activeApiKeysCount: 3,
  totalApiKeysCount: 4,
  activeWebhooksCount: 2,
  totalWebhooksCount: 2,
  totalDeliveriesCount: 45,
  successfulDeliveriesCount: 43,
  failedDeliveriesCount: 2,
  deliverySuccessRate: 95.6,
  totalSyncRunsCount: 12,
  successfulSyncRunsCount: 12,
  syncSuccessRate: 100.0,
  lastSyncTime: '2026-09-06T10:30:00',
  lastWebhookDeliveryTime: '2026-09-06T11:00:00'
};

const mockApiKeys = [
  {
    id: 1,
    name: 'ERP Production Connector',
    keyPrefix: 'sprs_live_ab12',
    scopes: ['SUPPLIER_READ', 'SUPPLIER_WRITE', 'PERFORMANCE_READ'],
    active: true,
    rateLimitPerMinute: 60,
    createdByName: 'admin',
    lastUsedAt: '2026-09-06T09:00:00',
    createdAt: '2026-09-01T00:00:00'
  },
  {
    id: 2,
    name: 'BI Reporting Connector',
    keyPrefix: 'sprs_live_cd34',
    scopes: ['REPORT_READ', 'PERFORMANCE_READ'],
    active: false,
    rateLimitPerMinute: 120,
    createdByName: 'admin',
    lastUsedAt: null,
    createdAt: '2026-09-02T00:00:00'
  }
];

const mockWebhooks = [
  {
    id: 1,
    name: 'SAP Real-Time Receiver',
    targetUrl: 'https://api.sap-procure.com/webhook',
    secretToken: 'whsec_test123',
    eventTypes: ['SUPPLIER_CREATED', 'SUPPLIER_UPDATED'],
    active: true,
    failureCount: 0,
    lastSuccessAt: '2026-09-06T11:00:00',
    lastFailureAt: null
  }
];

const mockSyncHistory = [
  {
    id: 101,
    sourceSystem: 'SAP ERP Purchasing',
    syncMode: 'UPSERT',
    totalRecords: 15,
    createdCount: 10,
    updatedCount: 5,
    skippedCount: 0,
    failedCount: 0,
    status: 'SUCCESS',
    durationMs: 340,
    startedAt: '2026-09-06T10:30:00'
  }
];

const mockDeliveryLogs = [
  {
    id: 501,
    subscriptionName: 'SAP Real-Time Receiver',
    eventType: 'SUPPLIER_CREATED',
    targetUrl: 'https://api.sap-procure.com/webhook',
    responseStatus: 200,
    durationMs: 125,
    status: 'SUCCESS',
    deliveredAt: '2026-09-06T11:00:00',
    payload: '{"eventId":"123","eventType":"SUPPLIER_CREATED"}'
  }
];

describe('Phase 16 — Enterprise Integrations Frontend Suite', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    integrationService.getHealthSummary.mockResolvedValue(mockHealthSummary);
    integrationService.getApiKeys.mockResolvedValue(mockApiKeys);
    integrationService.getWebhooks.mockResolvedValue(mockWebhooks);
    integrationService.getSyncHistory.mockResolvedValue(mockSyncHistory);
    integrationService.getDeliveryLogs.mockResolvedValue(mockDeliveryLogs);
  });

  it('INT-FE-01: Renders master integration dashboard with health cards and stats', async () => {
    render(
      <BrowserRouter>
        <IntegrationsDashboard />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Enterprise Integrations & External APIs')).toBeInTheDocument();
      expect(screen.getAllByText('Active API Keys').length).toBeGreaterThan(0);
      expect(screen.getAllByText('Active Webhooks').length).toBeGreaterThan(0);
      expect(screen.getByText('Webhook Delivery Rate')).toBeInTheDocument();
      expect(screen.getByText('Sync Success Rate')).toBeInTheDocument();
    });
  });

  it('INT-FE-02: Displays API keys tab with scopes and active toggles', async () => {
    render(
      <BrowserRouter>
        <IntegrationsDashboard />
      </BrowserRouter>
    );

    const apiKeyTab = await screen.findByRole('button', { name: /API Keys \(/i });
    fireEvent.click(apiKeyTab);

    await waitFor(() => {
      expect(screen.getByText('ERP Production Connector')).toBeInTheDocument();
      expect(screen.getByText('BI Reporting Connector')).toBeInTheDocument();
      expect(screen.getByText('sprs_live_ab12...')).toBeInTheDocument();
      expect(screen.getByText('SUPPLIER_READ')).toBeInTheDocument();
    });
  });

  it('INT-FE-03: Displays Webhooks tab and triggers test ping', async () => {
    integrationService.testWebhook.mockResolvedValue({
      id: 99,
      status: 'SUCCESS',
      responseStatus: 200,
      durationMs: 88
    });

    render(
      <BrowserRouter>
        <IntegrationsDashboard />
      </BrowserRouter>
    );

    const webhooksTab = await screen.findByRole('button', { name: /Webhooks \(/i });
    fireEvent.click(webhooksTab);

    await waitFor(() => {
      expect(screen.getByText('SAP Real-Time Receiver')).toBeInTheDocument();
      expect(screen.getByText('https://api.sap-procure.com/webhook')).toBeInTheDocument();
    });

    const pingButtons = screen.getAllByRole('button', { name: /Test Ping/i });
    fireEvent.click(pingButtons[0]);

    await waitFor(() => {
      expect(integrationService.testWebhook).toHaveBeenCalledWith(1);
    });
  });

  it('INT-FE-04: Displays Sync History tab with metrics', async () => {
    render(
      <BrowserRouter>
        <IntegrationsDashboard />
      </BrowserRouter>
    );

    const syncTab = await screen.findByRole('button', { name: /Sync History \(/i });
    fireEvent.click(syncTab);

    await waitFor(() => {
      expect(screen.getByText('SAP ERP Purchasing')).toBeInTheDocument();
      expect(screen.getByText('UPSERT')).toBeInTheDocument();
      expect(screen.getByText('15')).toBeInTheDocument();
    });
  });

  it('INT-FE-05: Displays Delivery Logs tab and inspects payload', async () => {
    render(
      <BrowserRouter>
        <IntegrationsDashboard />
      </BrowserRouter>
    );

    const logsTab = await screen.findByRole('button', { name: /Delivery Logs \(/i });
    fireEvent.click(logsTab);

    await waitFor(() => {
      expect(screen.getByText('200')).toBeInTheDocument();
      expect(screen.getByText('125ms')).toBeInTheDocument();
    });

    const inspectBtn = screen.getByRole('button', { name: /Inspect/i });
    fireEvent.click(inspectBtn);

    await waitFor(() => {
      expect(screen.getByText(/Webhook Payload Inspector/i)).toBeInTheDocument();
    });
  });

  it('INT-FE-06: ApiKeyManagerModal handles creation flow and displays one-time secret', async () => {
    integrationService.createApiKey.mockResolvedValue({
      id: 5,
      name: 'New Custom Key',
      keyPrefix: 'sprs_live_new1',
      rawApiKey: 'sprs_live_new1234567890abcdef123456',
      scopes: ['SUPPLIER_READ'],
      rateLimitPerMinute: 60,
      active: true
    });

    const handleCreated = vi.fn();
    render(
      <ApiKeyManagerModal
        isOpen={true}
        onClose={vi.fn()}
        onKeyCreated={handleCreated}
      />
    );

    expect(screen.getByText('Create Enterprise API Key')).toBeInTheDocument();
    const nameInput = screen.getByPlaceholderText(/e.g. SAP Production ERP/i);
    fireEvent.change(nameInput, { target: { value: 'New Custom Key' } });

    const submitBtn = screen.getByRole('button', { name: /Generate API Key/i });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(integrationService.createApiKey).toHaveBeenCalled();
      expect(screen.getByText('API Key Generated')).toBeInTheDocument();
      expect(screen.getByText('sprs_live_new1234567890abcdef123456')).toBeInTheDocument();
      expect(screen.getByText(/Save Your API Key Now/i)).toBeInTheDocument();
    });
  });

  it('INT-FE-07: SupplierSyncModal loads template and submits sync payload', async () => {
    integrationService.syncSuppliers.mockResolvedValue({
      syncHistoryId: 77,
      sourceSystem: 'SAP ERP Procurement',
      syncMode: 'UPSERT',
      status: 'SUCCESS',
      totalRecords: 2,
      createdCount: 2,
      updatedCount: 0,
      skippedCount: 0,
      failedCount: 0,
      durationMs: 250,
      message: '2 created'
    });

    const handleFinished = vi.fn();
    render(
      <SupplierSyncModal
        isOpen={true}
        onClose={vi.fn()}
        onSyncFinished={handleFinished}
      />
    );

    expect(screen.getByText('Execute Supplier Synchronization')).toBeInTheDocument();
    const runBtn = screen.getByRole('button', { name: /Run Synchronization/i });
    fireEvent.click(runBtn);

    await waitFor(() => {
      expect(integrationService.syncSuppliers).toHaveBeenCalled();
      expect(screen.getByText('Synchronization Complete')).toBeInTheDocument();
    });
  });
});
