import api from './api';

export const aiService = {
  getAiDashboardSummary: async () => {
    const response = await api.get('/ai/dashboard');
    return response.data;
  },

  getSupplierPrediction: async (supplierId) => {
    const response = await api.get(`/ai/suppliers/${supplierId}/prediction`);
    return response.data;
  },

  getSupplierRisk: async (supplierId) => {
    const response = await api.get(`/ai/suppliers/${supplierId}/risk`);
    return response.data;
  },

  getSupplierTrend: async (supplierId) => {
    const response = await api.get(`/ai/suppliers/${supplierId}/trend`);
    return response.data;
  },

  getSupplierRecommendations: async (supplierId) => {
    const response = await api.get(`/ai/suppliers/${supplierId}/recommendations`);
    return response.data;
  },

  getSupplierAlerts: async (supplierId) => {
    const response = await api.get(`/ai/suppliers/${supplierId}/alerts`);
    return response.data;
  },

  getAllAlerts: async () => {
    const response = await api.get('/ai/alerts');
    return response.data;
  },

  getSupplierInsights: async (supplierId) => {
    const response = await api.get(`/ai/suppliers/${supplierId}/insights`);
    return response.data;
  },

  // Phase 18: AI Copilot & Decision Support Services
  queryCopilot: async (data) => {
    const response = await api.post('/ai/copilot/query', data);
    return response.data;
  },

  getCopilotHistory: async () => {
    const response = await api.get('/ai/copilot/history');
    return response.data;
  },

  submitFeedback: async (historyId, data) => {
    const response = await api.post(`/ai/copilot/feedback/${historyId}`, data);
    return response.data;
  },

  compareSuppliersAi: async (supplierIds) => {
    const response = await api.post('/ai/suppliers/compare', { supplierIds });
    return response.data;
  },

  getExecutiveAiInsights: async () => {
    const response = await api.get('/ai/executive-insights');
    return response.data;
  },

  getWorkflowRecommendations: async () => {
    const response = await api.get('/ai/workflows/recommendations');
    return response.data;
  },

  submitRecommendationDecision: async (recommendationId, data) => {
    const response = await api.post(`/ai/recommendations/${recommendationId}/decision`, data);
    return response.data;
  },

  getSupplierRecommendationDecisions: async (supplierId) => {
    const response = await api.get(`/ai/suppliers/${supplierId}/decisions`);
    return response.data;
  }
};
