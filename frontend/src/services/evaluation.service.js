import api from './api';

export const evaluationService = {
  getEvaluations: async (params = {}) => {
    const response = await api.get('/evaluations', { params });
    return response.data;
  },

  getMyEvaluations: async (params = {}) => {
    const response = await api.get('/evaluations/my-evaluations', { params });
    return response.data;
  },

  getEvaluationById: async (id) => {
    const response = await api.get(`/evaluations/${id}`);
    return response.data;
  },

  getEvaluationByCode: async (code) => {
    const response = await api.get(`/evaluations/code/${code}`);
    return response.data;
  },

  getEvaluationsBySupplierId: async (supplierId) => {
    const response = await api.get(`/evaluations/supplier/${supplierId}`);
    return response.data;
  },

  getEvaluationsBySupplier: async (supplierId) => {
    const response = await api.get(`/evaluations/supplier/${supplierId}`);
    return response.data;
  },

  submitEvaluation: async (evaluationData) => {
    const response = await api.post('/evaluations', evaluationData);
    return response.data;
  },

  saveDraft: async (evaluationData) => {
    const response = await api.post('/evaluations/draft', evaluationData);
    return response.data;
  },

  updateEvaluation: async (id, evaluationData) => {
    const response = await api.put(`/evaluations/${id}`, evaluationData);
    return response.data;
  },

  submitDraft: async (id) => {
    const response = await api.patch(`/evaluations/${id}/submit`);
    return response.data;
  },

  completeEvaluation: async (id) => {
    const response = await api.patch(`/evaluations/${id}/complete`);
    return response.data;
  },

  cancelEvaluation: async (id) => {
    const response = await api.patch(`/evaluations/${id}/cancel`);
    return response.data;
  },

  deleteEvaluation: async (id) => {
    const response = await api.delete(`/evaluations/${id}`);
    return response.data;
  }
};
