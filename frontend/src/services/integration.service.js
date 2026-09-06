import api from './api';

const integrationService = {
  // 1. Health & Monitoring
  getHealthSummary: async () => {
    const response = await api.get('/admin/integrations/health');
    return response.data;
  },

  // 2. API Keys
  getApiKeys: async () => {
    const response = await api.get('/admin/integrations/api-keys');
    return response.data;
  },

  createApiKey: async (data) => {
    const response = await api.post('/admin/integrations/api-keys', data);
    return response.data;
  },

  toggleApiKeyStatus: async (id, active) => {
    const response = await api.patch(`/admin/integrations/api-keys/${id}/status`, { active });
    return response.data;
  },

  deleteApiKey: async (id) => {
    const response = await api.delete(`/admin/integrations/api-keys/${id}`);
    return response.data;
  },

  // 3. Webhooks
  getWebhooks: async () => {
    const response = await api.get('/admin/integrations/webhooks');
    return response.data;
  },

  createWebhook: async (data) => {
    const response = await api.post('/admin/integrations/webhooks', data);
    return response.data;
  },

  updateWebhook: async (id, data) => {
    const response = await api.put(`/admin/integrations/webhooks/${id}`, data);
    return response.data;
  },

  toggleWebhookStatus: async (id, active) => {
    const response = await api.patch(`/admin/integrations/webhooks/${id}/status`, { active });
    return response.data;
  },

  deleteWebhook: async (id) => {
    const response = await api.delete(`/admin/integrations/webhooks/${id}`);
    return response.data;
  },

  testWebhook: async (id) => {
    const response = await api.post(`/admin/integrations/webhooks/${id}/test`);
    return response.data;
  },

  // 4. Delivery Logs
  getDeliveryLogs: async () => {
    const response = await api.get('/admin/integrations/delivery-logs');
    return response.data;
  },

  getDeliveryLogsBySubscription: async (subId) => {
    const response = await api.get(`/admin/integrations/delivery-logs/subscription/${subId}`);
    return response.data;
  },

  // 5. Supplier Synchronization Engine
  syncSuppliers: async (data) => {
    const response = await api.post('/admin/integrations/sync/suppliers', data);
    return response.data;
  },

  getSyncHistory: async () => {
    const response = await api.get('/admin/integrations/sync/history');
    return response.data;
  },

  getSyncHistoryById: async (id) => {
    const response = await api.get(`/admin/integrations/sync/history/${id}`);
    return response.data;
  }
};

export default integrationService;
