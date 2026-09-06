import api from './api';

export const supplierPortalService = {
  // 1. Dashboard & Profile
  getDashboard: async () => {
    const res = await api.get('/supplier-portal/dashboard');
    return res.data;
  },

  getProfile: async () => {
    const res = await api.get('/supplier-portal/profile');
    return res.data;
  },

  submitProfileUpdateRequest: async (data) => {
    const res = await api.post('/supplier-portal/profile/request-update', data);
    return res.data;
  },

  // 2. Performance & Evaluations
  getPerformance: async () => {
    const res = await api.get('/supplier-portal/performance');
    return res.data;
  },

  getEvaluations: async () => {
    const res = await api.get('/supplier-portal/evaluations');
    return res.data;
  },

  getEvaluationDetails: async (id) => {
    const res = await api.get(`/supplier-portal/evaluations/${id}`);
    return res.data;
  },

  // 3. Improvement Actions
  getImprovementActions: async () => {
    const res = await api.get('/supplier-portal/improvement-actions');
    return res.data;
  },

  respondToImprovementAction: async (id, data) => {
    const res = await api.post(`/supplier-portal/improvement-actions/${id}/respond`, data);
    return res.data;
  },

  // 4. Documents
  getDocuments: async () => {
    const res = await api.get('/supplier-portal/documents');
    return res.data;
  },

  uploadDocument: async (formData) => {
    const res = await api.post('/supplier-portal/documents', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return res.data;
  },

  downloadDocument: async (id) => {
    const res = await api.get(`/supplier-portal/documents/${id}/download`, {
      responseType: 'blob',
    });
    return res.data;
  },

  deleteDocument: async (id) => {
    const res = await api.delete(`/supplier-portal/documents/${id}`);
    return res.data;
  },

  // 5. Communications
  getCommunications: async () => {
    const res = await api.get('/supplier-portal/communications');
    return res.data;
  },

  sendCommunication: async (data) => {
    const res = await api.post('/supplier-portal/communications', data);
    return res.data;
  },

  // 6. Notifications
  getNotifications: async () => {
    const res = await api.get('/supplier-portal/notifications');
    return res.data;
  },

  // 7. Admin / Procurement Manager Operations
  createSupplierUser: async (data) => {
    const res = await api.post('/supplier-portal/admin/users', data);
    return res.data;
  },

  getPendingProfileRequests: async () => {
    const res = await api.get('/supplier-portal/admin/profile-requests');
    return res.data;
  },

  reviewProfileRequest: async (id, data) => {
    const res = await api.put(`/supplier-portal/admin/profile-requests/${id}`, data);
    return res.data;
  },

  reviewDocument: async (id, data) => {
    const res = await api.put(`/supplier-portal/admin/documents/${id}/review`, data);
    return res.data;
  },
};

export default supplierPortalService;
