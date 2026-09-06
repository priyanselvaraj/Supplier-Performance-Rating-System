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
  }
};
