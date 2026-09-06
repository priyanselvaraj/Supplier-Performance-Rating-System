import api from './api';

export const criteriaService = {
  getAllCriteria: async () => {
    const response = await api.get('/criteria');
    return response.data;
  },

  getActiveCriteria: async () => {
    const response = await api.get('/criteria/active');
    return response.data;
  },

  getCriteriaById: async (id) => {
    const response = await api.get(`/criteria/${id}`);
    return response.data;
  },

  getTotalActiveWeights: async () => {
    const response = await api.get('/criteria/total-weight');
    return response.data;
  },

  createCriteria: async (criteriaData) => {
    const response = await api.post('/criteria', criteriaData);
    return response.data;
  },

  updateCriteria: async (id, criteriaData) => {
    const response = await api.put(`/criteria/${id}`, criteriaData);
    return response.data;
  },

  toggleCriteriaActive: async (id) => {
    const response = await api.patch(`/criteria/${id}/toggle-active`);
    return response.data;
  },

  deleteCriteria: async (id) => {
    const response = await api.delete(`/criteria/${id}`);
    return response.data;
  }
};
