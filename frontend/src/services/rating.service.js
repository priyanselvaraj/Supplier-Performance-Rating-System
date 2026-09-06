import api from './api';

export const ratingService = {
  getAllRatings: async (params = {}) => {
    const response = await api.get('/ratings', { params });
    return response.data;
  },

  getRatingById: async (id) => {
    const response = await api.get(`/ratings/${id}`);
    return response.data;
  },

  getLatestSupplierRating: async (supplierId) => {
    const response = await api.get(`/ratings/supplier/${supplierId}/latest`);
    return response.data;
  },

  getSupplierRatingHistory: async (supplierId) => {
    const response = await api.get(`/ratings/supplier/${supplierId}/history`);
    return response.data;
  },

  getSupplierPerformanceSummary: async (supplierId) => {
    const response = await api.get(`/ratings/supplier/${supplierId}/summary`);
    return response.data;
  },

  getHighPerformingSuppliers: async () => {
    const response = await api.get('/ratings/high-performing');
    return response.data;
  },

  getNeedsImprovementSuppliers: async () => {
    const response = await api.get('/ratings/needs-improvement');
    return response.data;
  }
};
