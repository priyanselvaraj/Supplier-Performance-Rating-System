import React, { useState, useEffect } from 'react';
import supplierPortalService from '../../services/supplierPortal.service';
import { notificationService } from '../../services/notification.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import {
  Bell,
  CheckCircle2,
  AlertTriangle,
  Clock,
  CheckCheck
} from 'lucide-react';

export const SupplierNotifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const res = await supplierPortalService.getNotifications();
      if (res.success) {
        setNotifications(res.data || []);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load notifications.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  const handleMarkAsRead = async (id) => {
    try {
      await notificationService.markAsRead(id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, read: true } : n))
      );
    } catch (err) {
      console.error(err);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationService.markAllAsRead();
      setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    } catch (err) {
      console.error(err);
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-emerald-600 border-t-transparent"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Supplier Notifications & Alerts</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Audit releases, CAP action deadlines, and profile update verification notices.
          </p>
        </div>
        <Button
          variant="secondary"
          onClick={handleMarkAllAsRead}
          disabled={notifications.every((n) => n.read)}
        >
          <CheckCheck className="h-4 w-4 mr-1.5" /> Mark All as Read
        </Button>
      </div>

      {/* List */}
      {notifications.length === 0 ? (
        <Card className="p-12 text-center text-slate-500 border-slate-200/80">
          <Bell className="mx-auto h-12 w-12 text-slate-400 mb-3" />
          <h3 className="text-base font-bold text-slate-700">No Notifications</h3>
          <p className="text-xs text-slate-400 mt-1">You are all caught up on alerts and updates.</p>
        </Card>
      ) : (
        <div className="space-y-3">
          {notifications.map((item) => (
            <Card
              key={item.id}
              className={`p-4 border shadow-xs transition-colors flex items-start justify-between gap-4 ${
                item.read ? 'bg-white border-slate-200/80' : 'bg-emerald-50/30 border-emerald-200'
              }`}
            >
              <div className="flex items-start gap-3">
                <div
                  className={`mt-0.5 h-8 w-8 rounded-lg flex items-center justify-center ${
                    item.read
                      ? 'bg-slate-100 text-slate-500'
                      : 'bg-emerald-100 text-emerald-700 font-bold'
                  }`}
                >
                  <Bell className="h-4 w-4" />
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <h3 className="text-sm font-bold text-slate-900">{item.title}</h3>
                    {!item.read && (
                      <span className="h-2 w-2 rounded-full bg-emerald-500"></span>
                    )}
                  </div>
                  <p className="text-xs text-slate-600 mt-1 leading-relaxed">{item.message}</p>
                  <p className="text-[11px] text-slate-400 mt-2 flex items-center gap-1 font-mono">
                    <Clock className="h-3 w-3" />
                    {item.createdAt ? new Date(item.createdAt).toLocaleString() : 'N/A'}
                  </p>
                </div>
              </div>

              {!item.read && (
                <Button
                  size="xs"
                  variant="secondary"
                  onClick={() => handleMarkAsRead(item.id)}
                  className="flex-shrink-0"
                >
                  Mark Read
                </Button>
              )}
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default SupplierNotifications;
