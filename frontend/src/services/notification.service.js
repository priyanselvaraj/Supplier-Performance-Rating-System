import api from './api';

export const notificationService = {
  getNotifications: async (page = 0, size = 10, unreadOnly = false, type = null) => {
    const params = { page, size };
    if (unreadOnly) params.unreadOnly = true;
    if (type) params.type = type;
    const response = await api.get('/notifications', { params });
    return response.data;
  },

  getRecentNotifications: async () => {
    const response = await api.get('/notifications/recent');
    return response.data;
  },

  getUnreadNotifications: async () => {
    const response = await api.get('/notifications/unread');
    return response.data;
  },

  getUnreadCount: async () => {
    const response = await api.get('/notifications/count');
    return response.data;
  },

  markAsRead: async (id) => {
    const response = await api.put(`/notifications/${id}/read`);
    return response.data;
  },

  markAllAsRead: async () => {
    const response = await api.put('/notifications/read-all');
    return response.data;
  },

  deleteNotification: async (id) => {
    const response = await api.delete(`/notifications/${id}`);
    return response.data;
  },

  getPreferences: async () => {
    const response = await api.get('/notifications/preferences');
    return response.data;
  },

  updatePreferences: async (preferences) => {
    const response = await api.put('/notifications/preferences', preferences);
    return response.data;
  },

  sendTestEmail: async (toEmail) => {
    const params = toEmail ? { to: toEmail } : {};
    const response = await api.post('/notifications/test-email', null, { params });
    return response.data;
  },

  connectSse: (onNotification, onError) => {
    const token = localStorage.getItem('token');
    // Standard EventSource does not support custom headers, but we can connect or fallback to polling if token in query/cookie
    // Or fetch-based streaming / polling fallback for 100% cross-browser reliability
    const pollInterval = setInterval(async () => {
      try {
        const res = await notificationService.getUnreadCount();
        if (res?.data?.unreadCount !== undefined) {
          onNotification && onNotification({ type: 'COUNT_UPDATE', count: res.data.unreadCount });
        }
      } catch (err) {
        onError && onError(err);
      }
    }, 15000);

    return () => clearInterval(pollInterval);
  }
};
