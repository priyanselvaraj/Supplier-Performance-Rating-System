import api from './api';

export const dashboardService = {
  getSummary: async () => {
    const response = await api.get('/dashboard/summary');
    return response.data;
  },

  getSupplierStatistics: async () => {
    const response = await api.get('/dashboard/supplier-statistics');
    return response.data;
  },

  getEvaluationStatistics: async () => {
    const response = await api.get('/dashboard/evaluation-statistics');
    return response.data;
  },

  getPerformance: async (startDate, endDate) => {
    const params = {};
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    const response = await api.get('/dashboard/performance', { params });
    return response.data;
  },

  getRatingDistribution: async () => {
    const response = await api.get('/dashboard/rating-distribution');
    return response.data;
  },

  getPerformanceStatusDistribution: async () => {
    const response = await api.get('/dashboard/performance-status-distribution');
    return response.data;
  },

  getTopSuppliers: async (limit = 5) => {
    const response = await api.get('/dashboard/top-suppliers', { params: { limit } });
    return response.data;
  },

  getLowSuppliers: async (limit = 5) => {
    const response = await api.get('/dashboard/low-performing-suppliers', { params: { limit } });
    return response.data;
  },

  getRecentEvaluations: async (limit = 10) => {
    const response = await api.get('/dashboard/recent-evaluations', { params: { limit } });
    return response.data;
  },

  getPerformanceTrends: async (supplierId) => {
    const response = await api.get('/dashboard/performance-trends', { params: { supplierId } });
    return response.data;
  },

  getOverallPerformanceTrend: async (groupBy = 'MONTH') => {
    const response = await api.get('/dashboard/overall-performance-trend', { params: { groupBy } });
    return response.data;
  },

  getSuppliersByCategory: async () => {
    const response = await api.get('/dashboard/suppliers-by-category');
    return response.data;
  }
};
