import api from './api';

export const monitoringService = {
  getMonitoringSummary: async () => {
    const response = await api.get('/monitoring/summary');
    return response.data;
  }
};
