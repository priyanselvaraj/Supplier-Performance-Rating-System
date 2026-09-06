import api from './api';

export const userService = {
  getAllUsers: async () => {
    const response = await api.get('/users');
    return response.data;
  },

  getUserById: async (id) => {
    const response = await api.get(`/users/${id}`);
    return response.data;
  },

  searchUsers: async (keyword) => {
    const response = await api.get('/users/search', { params: { keyword } });
    return response.data;
  },

  updateUser: async (id, userData) => {
    const response = await api.put(`/users/${id}`, userData);
    return response.data;
  },

  updateUserRoles: async (id, roles) => {
    const response = await api.put(`/users/${id}/roles`, { roles });
    return response.data;
  },

  toggleUserStatus: async (id) => {
    const response = await api.patch(`/users/${id}/toggle-status`);
    return response.data;
  },

  changePassword: async (id, passwordData) => {
    const response = await api.post(`/users/${id}/change-password`, passwordData);
    return response.data;
  },

  deleteUser: async (id) => {
    const response = await api.delete(`/users/${id}`);
    return response.data;
  }
};
