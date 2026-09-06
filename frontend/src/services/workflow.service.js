import api from './api';

export const workflowService = {
  // Workflow Instances
  getWorkflows: async (params = {}) => {
    const response = await api.get('/workflows', { params });
    return response.data;
  },

  getWorkflowSummary: async () => {
    const response = await api.get('/workflows/summary');
    return response.data;
  },

  getWorkflowById: async (id) => {
    const response = await api.get(`/workflows/${id}`);
    return response.data;
  },

  getWorkflowsByResource: async (resourceType, resourceId) => {
    const response = await api.get(`/workflows/resource/${resourceType}/${resourceId}`);
    return response.data;
  },

  // Approvals & Tasks
  getMyPendingApprovals: async (page = 0, size = 10) => {
    const response = await api.get('/approvals/my', { params: { page, size } });
    return response.data;
  },

  getMyApprovalHistory: async (status = null, page = 0, size = 10) => {
    const params = { page, size };
    if (status) params.status = status;
    const response = await api.get('/approvals/history', { params });
    return response.data;
  },

  getApprovalTaskById: async (id) => {
    const response = await api.get(`/approvals/${id}`);
    return response.data;
  },

  approveTask: async (id, comments = '') => {
    const response = await api.post(`/approvals/${id}/approve`, { comments });
    return response.data;
  },

  rejectTask: async (id, rejectionReason = '') => {
    const response = await api.post(`/approvals/${id}/reject`, { rejectionReason });
    return response.data;
  },

  // Escalations
  getEscalations: async (page = 0, size = 20) => {
    const response = await api.get('/escalations', { params: { page, size } });
    return response.data;
  },

  checkOverdueTasks: async () => {
    const response = await api.post('/escalations/check-overdue');
    return response.data;
  },

  escalateTask: async (taskId, reason, targetRole = 'ROLE_ADMIN', targetUserId = null) => {
    const response = await api.post(`/escalations/${taskId}`, { reason, targetRole, targetUserId });
    return response.data;
  },

  // Workflow Definitions (Admin)
  getWorkflowDefinitions: async () => {
    const response = await api.get('/admin/workflows/definitions');
    return response.data;
  },

  getWorkflowDefinitionById: async (id) => {
    const response = await api.get(`/admin/workflows/definitions/${id}`);
    return response.data;
  },

  createWorkflowDefinition: async (data) => {
    const response = await api.post('/admin/workflows/definitions', data);
    return response.data;
  },

  updateWorkflowDefinition: async (id, data) => {
    const response = await api.put(`/admin/workflows/definitions/${id}`, data);
    return response.data;
  },

  toggleWorkflowDefinition: async (id) => {
    const response = await api.patch(`/admin/workflows/definitions/${id}/toggle`);
    return response.data;
  },

  deleteWorkflowDefinition: async (id) => {
    const response = await api.delete(`/admin/workflows/definitions/${id}`);
    return response.data;
  }
};

export default workflowService;
