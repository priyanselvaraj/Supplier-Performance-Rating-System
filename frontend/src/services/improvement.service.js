import api from './api';

export const improvementService = {
  getActions: async (page = 0, size = 10, filters = {}) => {
    const params = { page, size, ...filters };
    const response = await api.get('/improvement-actions', { params });
    return response.data;
  },

  getActionById: async (id) => {
    const response = await api.get(`/improvement-actions/${id}`);
    return response.data;
  },

  getActionsBySupplier: async (supplierId) => {
    const response = await api.get(`/improvement-actions/supplier/${supplierId}`);
    return response.data;
  },

  createAction: async (data) => {
    const response = await api.post('/improvement-actions', data);
    return response.data;
  },

  updateAction: async (id, data) => {
    const response = await api.put(`/improvement-actions/${id}`, data);
    return response.data;
  },

  updateStatus: async (id, status, resolutionNotes = null) => {
    const response = await api.patch(`/improvement-actions/${id}/status`, { status, resolutionNotes });
    return response.data;
  },

  deleteAction: async (id) => {
    const response = await api.delete(`/improvement-actions/${id}`);
    return response.data;
  }
};
