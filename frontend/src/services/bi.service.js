import api from './api';

export const biService = {
  // BI Dashboard Summary
  getBiDashboardSummary: async () => {
    const response = await api.get('/bi/dashboard');
    return response.data;
  },

  // KPI Calculations
  calculateAllActiveKpis: async () => {
    const response = await api.get('/bi/kpis/calculate');
    return response.data;
  },

  calculateKpi: async (kpiCode) => {
    const response = await api.get(`/bi/kpis/calculate/${kpiCode}`);
    return response.data;
  },

  calculateKpiById: async (id) => {
    const response = await api.get(`/bi/kpis/calculate/id/${id}`);
    return response.data;
  },

  // Trends
  getPerformanceTrends: async (params = {}) => {
    const response = await api.get('/bi/trends', { params });
    return response.data;
  },

  // Supplier Comparison
  compareSuppliers: async (comparisonRequest) => {
    const response = await api.post('/bi/supplier-comparison', comparisonRequest);
    return response.data;
  },

  // Benchmarking
  getSupplierBenchmark: async (supplierId) => {
    const response = await api.get(`/bi/benchmarks/supplier/${supplierId}`);
    return response.data;
  },

  // Dynamic Report Builder & Preview
  generateReportPreview: async (reportRequest) => {
    const response = await api.post('/bi/reports/preview', reportRequest);
    return response.data;
  },

  // Saved Reports
  getSavedReports: async () => {
    const response = await api.get('/bi/saved-reports');
    return response.data;
  },

  getSavedReportById: async (id) => {
    const response = await api.get(`/bi/saved-reports/${id}`);
    return response.data;
  },

  saveReport: async (reportRequest) => {
    const response = await api.post('/bi/saved-reports', reportRequest);
    return response.data;
  },

  updateSavedReport: async (id, reportRequest) => {
    const response = await api.put(`/bi/saved-reports/${id}`, reportRequest);
    return response.data;
  },

  deleteSavedReport: async (id) => {
    const response = await api.delete(`/bi/saved-reports/${id}`);
    return response.data;
  },

  // Executive Dashboard
  getExecutiveDashboard: async () => {
    const response = await api.get('/bi/executive/dashboard');
    return response.data;
  },

  // KPI Administration (Admin Only)
  getAllKpiDefinitions: async () => {
    const response = await api.get('/admin/kpis');
    return response.data;
  },

  getKpiDefinitionById: async (id) => {
    const response = await api.get(`/admin/kpis/${id}`);
    return response.data;
  },

  createKpiDefinition: async (kpiRequest) => {
    const response = await api.post('/admin/kpis', kpiRequest);
    return response.data;
  },

  updateKpiDefinition: async (id, kpiRequest) => {
    const response = await api.put(`/admin/kpis/${id}`, kpiRequest);
    return response.data;
  },

  toggleKpiDefinition: async (id) => {
    const response = await api.patch(`/admin/kpis/${id}/toggle`);
    return response.data;
  },

  deleteKpiDefinition: async (id) => {
    const response = await api.delete(`/admin/kpis/${id}`);
    return response.data;
  },
};
