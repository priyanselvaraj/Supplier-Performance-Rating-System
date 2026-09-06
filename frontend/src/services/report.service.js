import api from './api';

export const reportService = {
  getSupplierReport: async (supplierId, params = {}) => {
    const response = await api.get(`/reports/supplier/${supplierId}`, { params });
    return response.data;
  },

  getEvaluationReport: async (evaluationId) => {
    const response = await api.get(`/reports/evaluation/${evaluationId}`);
    return response.data;
  },

  getOverallReport: async (params = {}) => {
    const response = await api.get('/reports/overall', { params });
    return response.data;
  },

  getEvaluationSummaryReport: async (params = {}) => {
    const response = await api.get('/reports/evaluations/summary', { params });
    return response.data;
  },

  getSupplierHistory: async (supplierId, params = {}) => {
    const response = await api.get(`/reports/supplier/${supplierId}/rating-history`, { params });
    return response.data;
  },

  getPerformanceReport: async (params = {}) => {
    const response = await api.get('/reports/performance', { params });
    return response.data;
  },

  getTopSuppliers: async (limit = 5) => {
    const response = await api.get('/reports/top-suppliers', { params: { limit } });
    return response.data;
  },

  getLowSuppliers: async (limit = 5) => {
    const response = await api.get('/reports/low-suppliers', { params: { limit } });
    return response.data;
  },

  // Export functions with blob handling
  exportSupplierReport: async (supplierId, format = 'pdf', params = {}) => {
    const response = await api.get(`/reports/supplier/${supplierId}/export/${format}`, {
      params,
      responseType: 'blob'
    });
    const filename = `supplier-report-${supplierId}.${format === 'excel' ? 'xlsx' : format}`;
    downloadBlob(response.data, filename);
  },

  exportOverallReport: async (format = 'pdf', params = {}) => {
    const response = await api.get(`/reports/overall/export/${format}`, {
      params,
      responseType: 'blob'
    });
    const filename = `overall-performance-report.${format === 'excel' ? 'xlsx' : format}`;
    downloadBlob(response.data, filename);
  },

  exportEvaluationReport: async (evaluationId, format = 'pdf') => {
    const response = await api.get(`/reports/evaluation/${evaluationId}/export/${format}`, {
      responseType: 'blob'
    });
    const filename = `evaluation-scorecard-${evaluationId}.${format === 'excel' ? 'xlsx' : format}`;
    downloadBlob(response.data, filename);
  }
};

function downloadBlob(blobData, filename) {
  const url = window.URL.createObjectURL(new Blob([blobData]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', filename);
  document.body.appendChild(link);
  link.click();
  link.parentNode.removeChild(link);
  window.URL.revokeObjectURL(url);
}
