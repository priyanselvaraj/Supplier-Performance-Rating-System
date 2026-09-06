import React, { useState, useEffect } from 'react';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import {
  Key,
  Webhook,
  RefreshCw,
  Activity,
  CheckCircle2,
  AlertTriangle,
  Send,
  Plus,
  Trash2,
  Edit2,
  Copy,
  Check,
  Shield,
  Clock,
  Database,
  ArrowUpRight,
  ExternalLink,
  Code
} from 'lucide-react';
import integrationService from '../../services/integration.service';
import ApiKeyManagerModal from './ApiKeyManagerModal';
import WebhookModal from './WebhookModal';
import SupplierSyncModal from './SupplierSyncModal';

export const IntegrationsDashboard = () => {
  const [activeTab, setActiveTab] = useState('overview'); // overview, api-keys, webhooks, sync, logs
  const [healthSummary, setHealthSummary] = useState(null);
  const [apiKeys, setApiKeys] = useState([]);
  const [webhooks, setWebhooks] = useState([]);
  const [syncHistory, setSyncHistory] = useState([]);
  const [deliveryLogs, setDeliveryLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Modals state
  const [isApiKeyModalOpen, setIsApiKeyModalOpen] = useState(false);
  const [isWebhookModalOpen, setIsWebhookModalOpen] = useState(false);
  const [editingWebhook, setEditingWebhook] = useState(null);
  const [isSyncModalOpen, setIsSyncModalOpen] = useState(false);

  // Quick feedback / notification state
  const [feedbackMessage, setFeedbackMessage] = useState('');
  const [copiedPrefixId, setCopiedPrefixId] = useState(null);
  const [testingWebhookId, setTestingWebhookId] = useState(null);
  const [selectedPayloadLog, setSelectedPayloadLog] = useState(null);

  const fetchAllData = async () => {
    try {
      setLoading(true);
      setError('');

      const [healthRes, keysRes, webhooksRes, syncRes, logsRes] = await Promise.all([
        integrationService.getHealthSummary().catch(() => null),
        integrationService.getApiKeys().catch(() => []),
        integrationService.getWebhooks().catch(() => []),
        integrationService.getSyncHistory().catch(() => []),
        integrationService.getDeliveryLogs().catch(() => [])
      ]);

      setHealthSummary(healthRes);
      setApiKeys(keysRes || []);
      setWebhooks(webhooksRes || []);
      setSyncHistory(syncRes || []);
      setDeliveryLogs(logsRes || []);
    } catch (err) {
      setError('Failed to load integration data: ' + (err.message || 'Unknown error'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAllData();
  }, []);

  const showNotification = (msg) => {
    setFeedbackMessage(msg);
    setTimeout(() => setFeedbackMessage(''), 4000);
  };

  // API Key handlers
  const handleToggleKeyStatus = async (id, currentStatus) => {
    try {
      await integrationService.toggleApiKeyStatus(id, !currentStatus);
      showNotification(`API Key status successfully updated.`);
      fetchAllData();
    } catch (err) {
      showNotification(`Failed to update key status: ${err.message}`);
    }
  };

  const handleDeleteKey = async (id, name) => {
    if (window.confirm(`Are you sure you want to permanently delete API Key "${name}"?`)) {
      try {
        await integrationService.deleteApiKey(id);
        showNotification(`API Key deleted.`);
        fetchAllData();
      } catch (err) {
        showNotification(`Failed to delete key: ${err.message}`);
      }
    }
  };

  // Webhook handlers
  const handleToggleWebhookStatus = async (id, currentStatus) => {
    try {
      await integrationService.toggleWebhookStatus(id, !currentStatus);
      showNotification(`Webhook status updated.`);
      fetchAllData();
    } catch (err) {
      showNotification(`Failed to update webhook: ${err.message}`);
    }
  };

  const handleDeleteWebhook = async (id, name) => {
    if (window.confirm(`Are you sure you want to delete Webhook "${name}"?`)) {
      try {
        await integrationService.deleteWebhook(id);
        showNotification(`Webhook deleted.`);
        fetchAllData();
      } catch (err) {
        showNotification(`Failed to delete webhook: ${err.message}`);
      }
    }
  };

  const handleTestPingWebhook = async (id) => {
    try {
      setTestingWebhookId(id);
      const res = await integrationService.testWebhook(id);
      if (res.status === 'SUCCESS') {
        showNotification(`Test Ping successfully delivered (HTTP ${res.responseStatus}) in ${res.durationMs}ms`);
      } else {
        showNotification(`Test Ping failed: ${res.errorMessage || 'HTTP Error ' + res.responseStatus}`);
      }
      fetchAllData();
    } catch (err) {
      showNotification(`Test Ping failed: ${err.message}`);
    } finally {
      setTestingWebhookId(null);
    }
  };

  return (
    <div className="space-y-6 pb-12">
      {/* Top Banner / Feedback Alert */}
      {feedbackMessage && (
        <div className="p-3 bg-blue-50 border border-blue-200 text-blue-800 text-xs rounded-xl flex items-center justify-between shadow-sm animate-fade-in">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-blue-600 shrink-0" />
            <span>{feedbackMessage}</span>
          </div>
          <button onClick={() => setFeedbackMessage('')} className="text-blue-500 hover:text-blue-700 text-xs font-semibold">
            Dismiss
          </button>
        </div>
      )}

      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
        <div>
          <div className="flex items-center gap-3">
            <div className="p-2.5 bg-blue-50 text-blue-600 rounded-xl border border-blue-100">
              <Database className="w-6 h-6" />
            </div>
            <div>
              <h1 className="text-xl font-bold text-slate-900">Enterprise Integrations & External APIs</h1>
              <p className="text-xs text-slate-500 mt-0.5">
                Manage secure API keys, event-driven webhooks, and automated supplier synchronization
              </p>
            </div>
          </div>
        </div>

        <div className="flex items-center flex-wrap gap-2.5">
          <Button
            variant="secondary"
            onClick={fetchAllData}
            disabled={loading}
            className="flex items-center gap-1.5 text-xs py-2"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} /> Refresh
          </Button>
          <Button
            variant="secondary"
            onClick={() => setIsSyncModalOpen(true)}
            className="flex items-center gap-1.5 text-xs py-2 text-indigo-700 bg-indigo-50 border-indigo-200 hover:bg-indigo-100"
          >
            <RefreshCw className="w-3.5 h-3.5" /> Run Supplier Sync
          </Button>
          <Button
            variant="secondary"
            onClick={() => {
              setEditingWebhook(null);
              setIsWebhookModalOpen(true);
            }}
            className="flex items-center gap-1.5 text-xs py-2"
          >
            <Plus className="w-3.5 h-3.5" /> Add Webhook
          </Button>
          <Button
            variant="primary"
            onClick={() => setIsApiKeyModalOpen(true)}
            className="flex items-center gap-1.5 text-xs py-2"
          >
            <Plus className="w-3.5 h-3.5" /> Generate API Key
          </Button>
        </div>
      </div>

      {/* Health Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card className="p-5 border-slate-200 bg-white">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Active API Keys</span>
            <div className="p-2 bg-emerald-50 text-emerald-600 rounded-lg">
              <Key className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl font-bold text-slate-900">
              {healthSummary ? healthSummary.activeApiKeysCount : apiKeys.filter((k) => k.active).length}
            </span>
            <span className="text-xs text-slate-500">
              of {healthSummary ? healthSummary.totalApiKeysCount : apiKeys.length} total
            </span>
          </div>
          <p className="text-[11px] text-slate-400 mt-1 flex items-center gap-1">
            <Shield className="w-3 h-3 text-emerald-500" /> SHA-256 Authenticated
          </p>
        </Card>

        <Card className="p-5 border-slate-200 bg-white">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Active Webhooks</span>
            <div className="p-2 bg-blue-50 text-blue-600 rounded-lg">
              <Webhook className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl font-bold text-slate-900">
              {healthSummary ? healthSummary.activeWebhooksCount : webhooks.filter((w) => w.active).length}
            </span>
            <span className="text-xs text-slate-500">
              of {healthSummary ? healthSummary.totalWebhooksCount : webhooks.length} total
            </span>
          </div>
          <p className="text-[11px] text-slate-400 mt-1 flex items-center gap-1">
            <Activity className="w-3 h-3 text-blue-500" /> HMAC-SHA256 Signed
          </p>
        </Card>

        <Card className="p-5 border-slate-200 bg-white">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Webhook Delivery Rate</span>
            <div className="p-2 bg-indigo-50 text-indigo-600 rounded-lg">
              <Send className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl font-bold text-slate-900">
              {healthSummary ? healthSummary.deliverySuccessRate : 100}%
            </span>
            <span className="text-xs text-slate-500">
              ({healthSummary ? healthSummary.successfulDeliveriesCount : 0} delivered)
            </span>
          </div>
          <p className="text-[11px] text-slate-400 mt-1 flex items-center gap-1">
            <CheckCircle2 className="w-3 h-3 text-indigo-500" /> Real-time Dispatch
          </p>
        </Card>

        <Card className="p-5 border-slate-200 bg-white">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Sync Success Rate</span>
            <div className="p-2 bg-purple-50 text-purple-600 rounded-lg">
              <RefreshCw className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl font-bold text-slate-900">
              {healthSummary ? healthSummary.syncSuccessRate : 100}%
            </span>
            <span className="text-xs text-slate-500">
              ({healthSummary ? healthSummary.totalSyncRunsCount : syncHistory.length} sync runs)
            </span>
          </div>
          <p className="text-[11px] text-slate-400 mt-1 flex items-center gap-1">
            <Database className="w-3 h-3 text-purple-500" /> Upsert & Batch Safe
          </p>
        </Card>
      </div>

      {/* Tabs Navigation */}
      <div className="border-b border-slate-200 flex items-center gap-2 overflow-x-auto">
        {[
          { id: 'overview', label: 'Overview', icon: Activity },
          { id: 'api-keys', label: `API Keys (${apiKeys.length})`, icon: Key },
          { id: 'webhooks', label: `Webhooks (${webhooks.length})`, icon: Webhook },
          { id: 'sync', label: `Sync History (${syncHistory.length})`, icon: Database },
          { id: 'logs', label: `Delivery Logs (${deliveryLogs.length})`, icon: Send }
        ].map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center gap-2 px-4 py-3 text-xs font-semibold border-b-2 transition-all whitespace-nowrap ${
                isActive
                  ? 'border-blue-600 text-blue-600 bg-blue-50/40'
                  : 'border-transparent text-slate-600 hover:text-slate-900 hover:border-slate-300'
              }`}
            >
              <Icon className="w-4 h-4" />
              {tab.label}
            </button>
          );
        })}
      </div>

      {/* TAB CONTENT: Overview */}
      {activeTab === 'overview' && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Quick API Keys Panel */}
            <Card className="p-6 border-slate-200 bg-white">
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-2">
                  <Key className="w-5 h-5 text-blue-600" />
                  <h3 className="text-sm font-bold text-slate-900">Active API Keys</h3>
                </div>
                <button
                  onClick={() => setActiveTab('api-keys')}
                  className="text-xs text-blue-600 hover:underline flex items-center gap-1 font-medium"
                >
                  View All <ArrowUpRight className="w-3.5 h-3.5" />
                </button>
              </div>

              {apiKeys.length === 0 ? (
                <div className="text-center py-8 text-slate-400 text-xs">
                  No API Keys configured. Click "+ Generate API Key" to create one.
                </div>
              ) : (
                <div className="space-y-3">
                  {apiKeys.slice(0, 4).map((k) => (
                    <div key={k.id} className="p-3 bg-slate-50 rounded-xl border border-slate-200/80 flex items-center justify-between">
                      <div>
                        <div className="flex items-center gap-2">
                          <span className="font-semibold text-xs text-slate-800">{k.name}</span>
                          <span className="font-mono text-[10px] bg-slate-200 px-1.5 py-0.5 rounded text-slate-700">
                            {k.keyPrefix}...
                          </span>
                        </div>
                        <div className="flex flex-wrap gap-1 mt-1.5">
                          {k.scopes?.map((s) => (
                            <span key={s} className="text-[10px] bg-blue-100 text-blue-800 px-1.5 py-0.2 rounded font-medium">
                              {s}
                            </span>
                          ))}
                        </div>
                      </div>
                      <span className={`text-[10px] px-2 py-0.5 rounded-full font-semibold ${
                        k.active ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-200 text-slate-600'
                      }`}>
                        {k.active ? 'ACTIVE' : 'INACTIVE'}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </Card>

            {/* Quick Webhooks Panel */}
            <Card className="p-6 border-slate-200 bg-white">
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-2">
                  <Webhook className="w-5 h-5 text-indigo-600" />
                  <h3 className="text-sm font-bold text-slate-900">Webhook Subscriptions</h3>
                </div>
                <button
                  onClick={() => setActiveTab('webhooks')}
                  className="text-xs text-blue-600 hover:underline flex items-center gap-1 font-medium"
                >
                  View All <ArrowUpRight className="w-3.5 h-3.5" />
                </button>
              </div>

              {webhooks.length === 0 ? (
                <div className="text-center py-8 text-slate-400 text-xs">
                  No Webhook subscriptions configured. Click "+ Add Webhook" to register an endpoint.
                </div>
              ) : (
                <div className="space-y-3">
                  {webhooks.slice(0, 4).map((w) => (
                    <div key={w.id} className="p-3 bg-slate-50 rounded-xl border border-slate-200/80 flex items-center justify-between">
                      <div className="max-w-[70%]">
                        <span className="font-semibold text-xs text-slate-800 block truncate">{w.name}</span>
                        <span className="text-[11px] text-slate-500 font-mono block truncate mt-0.5">{w.targetUrl}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <Button
                          variant="secondary"
                          size="sm"
                          disabled={testingWebhookId === w.id}
                          onClick={() => handleTestPingWebhook(w.id)}
                          className="text-[11px] py-1 px-2 text-slate-600"
                        >
                          {testingWebhookId === w.id ? 'Pinging...' : 'Ping'}
                        </Button>
                        <span className={`text-[10px] px-2 py-0.5 rounded-full font-semibold ${
                          w.active ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-200 text-slate-600'
                        }`}>
                          {w.active ? 'ACTIVE' : 'INACTIVE'}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </Card>
          </div>

          {/* Quick Integration Documentation Info Box */}
          <div className="p-5 bg-gradient-to-r from-slate-900 to-indigo-950 rounded-2xl text-white shadow-md">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
              <div>
                <h3 className="text-sm font-bold flex items-center gap-2 text-blue-300">
                  <Code className="w-4 h-4" /> External REST APIs Ready for Consumers
                </h3>
                <p className="text-xs text-slate-300 mt-1 max-w-2xl">
                  Third-party ERPs, CRMs, and procurement platforms can consume protected endpoints under{' '}
                  <code className="bg-slate-800 text-emerald-400 px-1.5 py-0.5 rounded font-mono">/api/v1/external/**</code> using{' '}
                  <code className="bg-slate-800 text-blue-300 px-1.5 py-0.5 rounded font-mono">X-API-KEY: sprs_live_...</code>.
                </p>
              </div>
              <div className="flex items-center gap-2 shrink-0">
                <a
                  href="/swagger-ui.html"
                  target="_blank"
                  rel="noreferrer"
                  className="px-3 py-2 bg-blue-600 hover:bg-blue-500 text-white rounded-xl text-xs font-semibold flex items-center gap-1.5 transition-colors"
                >
                  Interactive Swagger <ExternalLink className="w-3.5 h-3.5" />
                </a>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB CONTENT: API Keys */}
      {activeTab === 'api-keys' && (
        <Card className="p-6 border-slate-200 bg-white">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-bold text-slate-900">API Key Credentials</h3>
              <p className="text-xs text-slate-500 mt-0.5">
                Keys use secure SHA-256 hashing. The raw secret is only displayed once during generation.
              </p>
            </div>
            <Button variant="primary" size="sm" onClick={() => setIsApiKeyModalOpen(true)}>
              <Plus className="w-3.5 h-3.5 mr-1" /> Generate API Key
            </Button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-slate-200 bg-slate-50/70 text-[11px] font-semibold text-slate-500 uppercase tracking-wider">
                  <th className="py-3 px-4">Name / System</th>
                  <th className="py-3 px-4">Key Prefix</th>
                  <th className="py-3 px-4">Scopes</th>
                  <th className="py-3 px-4">Rate Limit</th>
                  <th className="py-3 px-4">Last Used</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-xs text-slate-700">
                {apiKeys.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="py-8 text-center text-slate-400">
                      No API keys found.
                    </td>
                  </tr>
                ) : (
                  apiKeys.map((k) => (
                    <tr key={k.id} className="hover:bg-slate-50/50 transition-colors">
                      <td className="py-3.5 px-4 font-semibold text-slate-900">
                        {k.name}
                        {k.createdByName && (
                          <span className="block text-[11px] font-normal text-slate-400">by {k.createdByName}</span>
                        )}
                      </td>
                      <td className="py-3.5 px-4 font-mono text-xs text-slate-600">
                        <span className="bg-slate-100 px-2 py-0.5 rounded border border-slate-200">
                          {k.keyPrefix}...
                        </span>
                      </td>
                      <td className="py-3.5 px-4">
                        <div className="flex flex-wrap gap-1 max-w-xs">
                          {k.scopes?.map((s) => (
                            <span key={s} className="text-[10px] bg-blue-50 text-blue-700 border border-blue-200 px-1.5 py-0.5 rounded font-medium">
                              {s}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td className="py-3.5 px-4 text-slate-600 font-mono text-xs">
                        {k.rateLimitPerMinute} req/min
                      </td>
                      <td className="py-3.5 px-4 text-slate-500 text-[11px]">
                        {k.lastUsedAt ? new Date(k.lastUsedAt).toLocaleString() : 'Never'}
                      </td>
                      <td className="py-3.5 px-4">
                        <span className={`text-[10px] px-2.5 py-0.5 rounded-full font-semibold ${
                          k.active ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-200 text-slate-600'
                        }`}>
                          {k.active ? 'ACTIVE' : 'REVOKED'}
                        </span>
                      </td>
                      <td className="py-3.5 px-4 text-right">
                        <div className="flex items-center justify-end gap-2">
                          <button
                            onClick={() => handleToggleKeyStatus(k.id, k.active)}
                            className={`text-xs px-2.5 py-1 rounded-lg font-medium transition-colors ${
                              k.active
                                ? 'text-amber-700 bg-amber-50 hover:bg-amber-100'
                                : 'text-emerald-700 bg-emerald-50 hover:bg-emerald-100'
                            }`}
                          >
                            {k.active ? 'Revoke' : 'Activate'}
                          </button>
                          <button
                            onClick={() => handleDeleteKey(k.id, k.name)}
                            className="p-1.5 text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                            title="Delete Key"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card>
      )}

      {/* TAB CONTENT: Webhooks */}
      {activeTab === 'webhooks' && (
        <Card className="p-6 border-slate-200 bg-white">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-bold text-slate-900">Webhook Subscriptions</h3>
              <p className="text-xs text-slate-500 mt-0.5">
                Outbound HTTP POST notifications signed with HMAC-SHA256 headers (<code className="text-slate-700">X-SPRS-Signature</code>).
              </p>
            </div>
            <Button
              variant="primary"
              size="sm"
              onClick={() => {
                setEditingWebhook(null);
                setIsWebhookModalOpen(true);
              }}
            >
              <Plus className="w-3.5 h-3.5 mr-1" /> Add Webhook
            </Button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-slate-200 bg-slate-50/70 text-[11px] font-semibold text-slate-500 uppercase tracking-wider">
                  <th className="py-3 px-4">Name</th>
                  <th className="py-3 px-4">Target Endpoint</th>
                  <th className="py-3 px-4">Events</th>
                  <th className="py-3 px-4">Health & Failures</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-xs text-slate-700">
                {webhooks.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="py-8 text-center text-slate-400">
                      No webhook subscriptions registered.
                    </td>
                  </tr>
                ) : (
                  webhooks.map((w) => (
                    <tr key={w.id} className="hover:bg-slate-50/50 transition-colors">
                      <td className="py-3.5 px-4 font-semibold text-slate-900">{w.name}</td>
                      <td className="py-3.5 px-4 font-mono text-[11px] text-slate-600 max-w-xs truncate" title={w.targetUrl}>
                        {w.targetUrl}
                      </td>
                      <td className="py-3.5 px-4">
                        <div className="flex flex-wrap gap-1 max-w-xs">
                          {w.eventTypes?.map((e) => (
                            <span key={e} className="text-[10px] bg-indigo-50 text-indigo-700 border border-indigo-200 px-1.5 py-0.5 rounded font-medium">
                              {e}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td className="py-3.5 px-4">
                        {w.failureCount > 0 ? (
                          <span className="text-xs text-rose-600 font-semibold flex items-center gap-1">
                            <AlertTriangle className="w-3.5 h-3.5" /> {w.failureCount} failure(s)
                          </span>
                        ) : (
                          <span className="text-xs text-emerald-600 font-semibold flex items-center gap-1">
                            <CheckCircle2 className="w-3.5 h-3.5" /> Healthy
                          </span>
                        )}
                        {w.lastSuccessAt && (
                          <span className="block text-[10px] text-slate-400 mt-0.5">
                            Last OK: {new Date(w.lastSuccessAt).toLocaleTimeString()}
                          </span>
                        )}
                      </td>
                      <td className="py-3.5 px-4">
                        <span className={`text-[10px] px-2.5 py-0.5 rounded-full font-semibold ${
                          w.active ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-200 text-slate-600'
                        }`}>
                          {w.active ? 'ACTIVE' : 'PAUSED'}
                        </span>
                      </td>
                      <td className="py-3.5 px-4 text-right">
                        <div className="flex items-center justify-end gap-1.5">
                          <Button
                            variant="secondary"
                            size="sm"
                            disabled={testingWebhookId === w.id}
                            onClick={() => handleTestPingWebhook(w.id)}
                            className="text-xs py-1 px-2.5"
                          >
                            {testingWebhookId === w.id ? 'Testing...' : 'Test Ping'}
                          </Button>
                          <button
                            onClick={() => {
                              setEditingWebhook(w);
                              setIsWebhookModalOpen(true);
                            }}
                            className="p-1.5 text-slate-500 hover:text-blue-600 hover:bg-slate-100 rounded-lg transition-colors"
                            title="Edit"
                          >
                            <Edit2 className="w-3.5 h-3.5" />
                          </button>
                          <button
                            onClick={() => handleToggleWebhookStatus(w.id, w.active)}
                            className={`p-1.5 rounded-lg transition-colors text-xs font-semibold ${
                              w.active ? 'text-amber-600 hover:bg-amber-50' : 'text-emerald-600 hover:bg-emerald-50'
                            }`}
                            title={w.active ? 'Pause Webhook' : 'Resume Webhook'}
                          >
                            {w.active ? 'Pause' : 'Resume'}
                          </button>
                          <button
                            onClick={() => handleDeleteWebhook(w.id, w.name)}
                            className="p-1.5 text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                            title="Delete"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card>
      )}

      {/* TAB CONTENT: Supplier Sync History */}
      {activeTab === 'sync' && (
        <Card className="p-6 border-slate-200 bg-white">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-bold text-slate-900">Supplier Synchronization History</h3>
              <p className="text-xs text-slate-500 mt-0.5">
                Audit log of all batch import, update, and upsert operations executed via ERP or API.
              </p>
            </div>
            <Button variant="primary" size="sm" onClick={() => setIsSyncModalOpen(true)}>
              <RefreshCw className="w-3.5 h-3.5 mr-1" /> Run Synchronization
            </Button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-slate-200 bg-slate-50/70 text-[11px] font-semibold text-slate-500 uppercase tracking-wider">
                  <th className="py-3 px-4">Started</th>
                  <th className="py-3 px-4">Source System</th>
                  <th className="py-3 px-4">Mode</th>
                  <th className="py-3 px-4">Total</th>
                  <th className="py-3 px-4">Created</th>
                  <th className="py-3 px-4">Updated</th>
                  <th className="py-3 px-4">Skipped</th>
                  <th className="py-3 px-4">Failed</th>
                  <th className="py-3 px-4">Duration</th>
                  <th className="py-3 px-4">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-xs text-slate-700">
                {syncHistory.length === 0 ? (
                  <tr>
                    <td colSpan={10} className="py-8 text-center text-slate-400">
                      No synchronization runs recorded.
                    </td>
                  </tr>
                ) : (
                  syncHistory.map((s) => (
                    <tr key={s.id} className="hover:bg-slate-50/50 transition-colors">
                      <td className="py-3.5 px-4 font-mono text-[11px] text-slate-600">
                        {s.startedAt ? new Date(s.startedAt).toLocaleString() : 'N/A'}
                      </td>
                      <td className="py-3.5 px-4 font-semibold text-slate-900">
                        {s.sourceSystem || 'External ERP'}
                        {s.executedByName && (
                          <span className="block text-[10px] font-normal text-slate-400">by {s.executedByName}</span>
                        )}
                      </td>
                      <td className="py-3.5 px-4">
                        <span className="text-[10px] bg-slate-100 text-slate-800 px-2 py-0.5 rounded font-mono font-semibold">
                          {s.syncMode}
                        </span>
                      </td>
                      <td className="py-3.5 px-4 font-bold text-slate-900">{s.totalRecords}</td>
                      <td className="py-3.5 px-4 text-emerald-600 font-semibold">{s.createdCount}</td>
                      <td className="py-3.5 px-4 text-blue-600 font-semibold">{s.updatedCount}</td>
                      <td className="py-3.5 px-4 text-slate-500">{s.skippedCount}</td>
                      <td className="py-3.5 px-4 text-rose-600 font-semibold">{s.failedCount}</td>
                      <td className="py-3.5 px-4 text-slate-500 font-mono text-[11px]">
                        {s.durationMs ? `${s.durationMs}ms` : '<10ms'}
                      </td>
                      <td className="py-3.5 px-4">
                        <span className={`text-[10px] px-2.5 py-0.5 rounded-full font-semibold ${
                          s.status === 'SUCCESS'
                            ? 'bg-emerald-100 text-emerald-800'
                            : s.status === 'PARTIAL_SUCCESS'
                            ? 'bg-amber-100 text-amber-800'
                            : 'bg-rose-100 text-rose-800'
                        }`}>
                          {s.status}
                        </span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card>
      )}

      {/* TAB CONTENT: Delivery Logs */}
      {activeTab === 'logs' && (
        <Card className="p-6 border-slate-200 bg-white">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-bold text-slate-900">Webhook Outgoing Delivery Logs</h3>
              <p className="text-xs text-slate-500 mt-0.5">
                Real-time record of all dispatched webhook payloads, response status codes, and latencies.
              </p>
            </div>
            <Button variant="secondary" size="sm" onClick={fetchAllData}>
              <RefreshCw className="w-3.5 h-3.5 mr-1" /> Refresh Logs
            </Button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-slate-200 bg-slate-50/70 text-[11px] font-semibold text-slate-500 uppercase tracking-wider">
                  <th className="py-3 px-4">Timestamp</th>
                  <th className="py-3 px-4">Webhook Name</th>
                  <th className="py-3 px-4">Event</th>
                  <th className="py-3 px-4">Target URL</th>
                  <th className="py-3 px-4">HTTP Status</th>
                  <th className="py-3 px-4">Latency</th>
                  <th className="py-3 px-4">Outcome</th>
                  <th className="py-3 px-4 text-right">Payload</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-xs text-slate-700">
                {deliveryLogs.length === 0 ? (
                  <tr>
                    <td colSpan={8} className="py-8 text-center text-slate-400">
                      No webhook deliveries logged yet.
                    </td>
                  </tr>
                ) : (
                  deliveryLogs.map((log) => (
                    <tr key={log.id} className="hover:bg-slate-50/50 transition-colors">
                      <td className="py-3.5 px-4 font-mono text-[11px] text-slate-500">
                        {log.deliveredAt ? new Date(log.deliveredAt).toLocaleTimeString() : 'N/A'}
                      </td>
                      <td className="py-3.5 px-4 font-semibold text-slate-900">{log.subscriptionName || 'Unknown'}</td>
                      <td className="py-3.5 px-4">
                        <span className="text-[10px] bg-indigo-50 text-indigo-700 border border-indigo-200 px-1.5 py-0.5 rounded font-medium">
                          {log.eventType}
                        </span>
                      </td>
                      <td className="py-3.5 px-4 font-mono text-[11px] text-slate-500 max-w-xs truncate" title={log.targetUrl}>
                        {log.targetUrl}
                      </td>
                      <td className="py-3.5 px-4">
                        <span className={`font-mono text-xs font-bold ${
                          log.responseStatus && log.responseStatus >= 200 && log.responseStatus < 300
                            ? 'text-emerald-600'
                            : 'text-rose-600'
                        }`}>
                          {log.responseStatus ? log.responseStatus : 'ERROR'}
                        </span>
                      </td>
                      <td className="py-3.5 px-4 font-mono text-[11px] text-slate-500">
                        {log.durationMs ? `${log.durationMs}ms` : '-'}
                      </td>
                      <td className="py-3.5 px-4">
                        <span className={`text-[10px] px-2 py-0.5 rounded-full font-semibold ${
                          log.status === 'SUCCESS' ? 'bg-emerald-100 text-emerald-800' : 'bg-rose-100 text-rose-800'
                        }`}>
                          {log.status}
                        </span>
                      </td>
                      <td className="py-3.5 px-4 text-right">
                        <Button
                          variant="secondary"
                          size="sm"
                          onClick={() => setSelectedPayloadLog(log)}
                          className="text-[11px] py-1 px-2"
                        >
                          Inspect
                        </Button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card>
      )}

      {/* Payload Inspection Modal */}
      {selectedPayloadLog && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full p-6 border border-slate-100 space-y-4">
            <div className="flex items-center justify-between border-b pb-3">
              <h3 className="text-sm font-bold text-slate-900">
                Webhook Payload Inspector (Delivery #{selectedPayloadLog.id})
              </h3>
              <button
                onClick={() => setSelectedPayloadLog(null)}
                className="text-slate-400 hover:text-slate-600 text-sm font-semibold"
              >
                Close
              </button>
            </div>

            <div className="space-y-3 text-xs">
              <div className="grid grid-cols-2 gap-2 bg-slate-50 p-3 rounded-xl border border-slate-200">
                <p><strong>Event:</strong> {selectedPayloadLog.eventType}</p>
                <p><strong>Target URL:</strong> {selectedPayloadLog.targetUrl}</p>
                <p><strong>HTTP Code:</strong> {selectedPayloadLog.responseStatus || 'N/A'}</p>
                <p><strong>Duration:</strong> {selectedPayloadLog.durationMs}ms</p>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 uppercase mb-1">JSON Payload Body</label>
                <pre className="p-3 bg-slate-900 text-slate-100 rounded-xl font-mono text-[11px] max-h-60 overflow-y-auto">
                  {selectedPayloadLog.payload || '// No payload'}
                </pre>
              </div>

              {selectedPayloadLog.responseBody && (
                <div>
                  <label className="block text-xs font-semibold text-slate-700 uppercase mb-1">Server Response Body</label>
                  <pre className="p-3 bg-slate-100 text-slate-800 rounded-xl font-mono text-[11px] max-h-32 overflow-y-auto">
                    {selectedPayloadLog.responseBody}
                  </pre>
                </div>
              )}

              {selectedPayloadLog.errorMessage && (
                <div className="p-3 bg-rose-50 border border-rose-200 text-rose-800 rounded-xl">
                  <strong>Error Message:</strong> {selectedPayloadLog.errorMessage}
                </div>
              )}
            </div>

            <div className="flex justify-end pt-2">
              <Button onClick={() => setSelectedPayloadLog(null)} variant="primary">
                Close Inspector
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* Modals */}
      <ApiKeyManagerModal
        isOpen={isApiKeyModalOpen}
        onClose={() => setIsApiKeyModalOpen(false)}
        onKeyCreated={() => fetchAllData()}
      />

      <WebhookModal
        isOpen={isWebhookModalOpen}
        onClose={() => {
          setIsWebhookModalOpen(false);
          setEditingWebhook(null);
        }}
        editingWebhook={editingWebhook}
        onSaved={() => fetchAllData()}
      />

      <SupplierSyncModal
        isOpen={isSyncModalOpen}
        onClose={() => setIsSyncModalOpen(false)}
        onSyncFinished={() => fetchAllData()}
      />
    </div>
  );
};
export default IntegrationsDashboard;
