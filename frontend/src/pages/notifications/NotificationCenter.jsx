import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Bell, Check, CheckCheck, Trash2, Filter, AlertTriangle, 
  FileText, TrendingUp, Sparkles, Wrench, Shield, Settings,
  ArrowRight, RefreshCw, Eye, Mail, Send, X
} from 'lucide-react';
import { notificationService } from '../../services/notification.service';
import { Button } from '../../components/common/Button';

export const NotificationCenter = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [unreadOnly, setUnreadOnly] = useState(false);
  const [selectedType, setSelectedType] = useState('ALL');
  const [showPreferences, setShowPreferences] = useState(false);
  const [preferences, setPreferences] = useState([]);
  const [prefLoading, setPrefLoading] = useState(false);
  const [showTestEmailModal, setShowTestEmailModal] = useState(false);
  const [testEmailInput, setTestEmailInput] = useState('priyanselvaraj756@gmail.com');
  const [sendingTestEmail, setSendingTestEmail] = useState(false);
  const [testEmailStatus, setTestEmailStatus] = useState(null);
  const navigate = useNavigate();

  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const typeParam = selectedType === 'ALL' ? null : selectedType;
      const res = await notificationService.getNotifications(page, 10, unreadOnly, typeParam);
      if (res?.data) {
        setNotifications(res.data.content || []);
        setTotalPages(res.data.totalPages || 0);
        setTotalElements(res.data.totalElements || 0);
      }
    } catch (err) {
      console.error('Failed to load notifications:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchPreferences = async () => {
    setPrefLoading(true);
    try {
      const res = await notificationService.getPreferences();
      if (res?.data) {
        setPreferences(res.data);
      }
    } catch (err) {
      console.error('Failed to load preferences:', err);
    } finally {
      setPrefLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, [page, unreadOnly, selectedType]);

  const handleMarkAsRead = async (id) => {
    try {
      await notificationService.markAsRead(id);
      setNotifications(prev => prev.map(n => n.id === id ? { ...n, read: true } : n));
    } catch (err) {
      console.error('Failed to mark as read:', err);
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await notificationService.markAllAsRead();
      setNotifications(prev => prev.map(n => ({ ...n, read: true })));
    } catch (err) {
      console.error('Failed to mark all as read:', err);
    }
  };

  const handleDelete = async (id) => {
    try {
      await notificationService.deleteNotification(id);
      setNotifications(prev => prev.filter(n => n.id !== id));
      setTotalElements(prev => Math.max(0, prev - 1));
    } catch (err) {
      console.error('Failed to delete notification:', err);
    }
  };

  const handleTogglePreference = async (index, channel) => {
    const updated = [...preferences];
    updated[index][channel] = !updated[index][channel];
    setPreferences(updated);

    try {
      await notificationService.updatePreferences(updated.map(p => ({
        notificationType: p.notificationType,
        inAppEnabled: p.inAppEnabled,
        emailEnabled: p.emailEnabled,
        smsEnabled: p.smsEnabled
      })));
    } catch (err) {
      console.error('Failed to update preferences:', err);
    }
  };

  const handleSendTestEmail = async (e) => {
    e.preventDefault();
    if (!testEmailInput || !testEmailInput.includes('@')) {
      setTestEmailStatus({ type: 'error', message: 'Please provide a valid email address.' });
      return;
    }
    setSendingTestEmail(true);
    setTestEmailStatus(null);
    try {
      await notificationService.sendTestEmail(testEmailInput.trim());
      setTestEmailStatus({ 
        type: 'success', 
        message: `Test email successfully dispatched to ${testEmailInput.trim()}! Please check your Gmail Inbox and Spam folder.` 
      });
    } catch (err) {
      setTestEmailStatus({ 
        type: 'error', 
        message: err.response?.data?.message || 'Failed to dispatch test email. Check server SMTP settings.' 
      });
    } finally {
      setSendingTestEmail(false);
    }
  };

  const getIcon = (type) => {
    switch (type) {
      case 'ALERT': return <AlertTriangle className="h-5 w-5 text-rose-600" />;
      case 'EVALUATION': return <FileText className="h-5 w-5 text-blue-600" />;
      case 'RATING': return <TrendingUp className="h-5 w-5 text-emerald-600" />;
      case 'AI_INSIGHT': return <Sparkles className="h-5 w-5 text-purple-600" />;
      case 'IMPROVEMENT_ACTION': return <Wrench className="h-5 w-5 text-amber-600" />;
      case 'SECURITY': return <Shield className="h-5 w-5 text-indigo-600" />;
      default: return <Shield className="h-5 w-5 text-slate-600" />;
    }
  };

  const getPriorityBadge = (priority) => {
    switch (priority) {
      case 'CRITICAL': return <span className="text-xs px-2.5 py-0.5 font-bold rounded-full bg-rose-100 text-rose-700">CRITICAL</span>;
      case 'HIGH': return <span className="text-xs px-2.5 py-0.5 font-bold rounded-full bg-amber-100 text-amber-700">HIGH</span>;
      case 'LOW': return <span className="text-xs px-2.5 py-0.5 font-bold rounded-full bg-slate-100 text-slate-600">LOW</span>;
      default: return <span className="text-xs px-2.5 py-0.5 font-bold rounded-full bg-blue-100 text-blue-700">MEDIUM</span>;
    }
  };

  const handleNavigateResource = (item) => {
    if (item.relatedResourceType === 'SUPPLIER' && item.relatedResourceId) {
      navigate(`/suppliers/${item.relatedResourceId}`);
    } else if (item.relatedResourceType === 'EVALUATION' && item.relatedResourceId) {
      navigate(`/evaluations/${item.relatedResourceId}`);
    } else if (item.relatedResourceType === 'IMPROVEMENT_ACTION') {
      navigate('/improvement-actions');
    }
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-200">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 border-b border-slate-200/80 pb-5">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-bold tracking-tight text-slate-900">Notification Center</h1>
            <span className="rounded-full bg-blue-100 px-2.5 py-0.5 text-xs font-semibold text-blue-700">
              {totalElements} total
            </span>
          </div>
          <p className="mt-1 text-sm text-slate-500">
            Real-time activity feed, early warning performance alerts, and system event notifications.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2.5">
          <Button
            variant="outline"
            size="sm"
            onClick={() => {
              setShowTestEmailModal(true);
              setTestEmailStatus(null);
            }}
            className="flex items-center gap-1.5 bg-blue-50 text-blue-700 border-blue-200 hover:bg-blue-100"
          >
            <Mail className="h-4 w-4" /> Send to Gmail
          </Button>

          <Button
            variant="outline"
            size="sm"
            onClick={() => {
              setShowPreferences(!showPreferences);
              if (!showPreferences) fetchPreferences();
            }}
            className="flex items-center gap-1.5"
          >
            <Settings className="h-4 w-4" /> Preferences
          </Button>

          <Button
            variant="outline"
            size="sm"
            onClick={handleMarkAllRead}
            className="flex items-center gap-1.5"
          >
            <CheckCheck className="h-4 w-4 text-blue-600" /> Mark all read
          </Button>

          <Button
            variant="ghost"
            size="sm"
            onClick={fetchNotifications}
            title="Refresh"
          >
            <RefreshCw className="h-4 w-4 text-slate-600" />
          </Button>
        </div>
      </div>

      {/* Send Test Email to Gmail Modal */}
      {showTestEmailModal && (
        <div className="bg-white border border-blue-200 rounded-xl p-5 shadow-md space-y-4 animate-in fade-in duration-150">
          <div className="flex items-center justify-between border-b border-slate-100 pb-3">
            <div className="flex items-center gap-2">
              <div className="p-2 rounded-lg bg-blue-100 text-blue-700">
                <Mail className="h-5 w-5" />
              </div>
              <div>
                <h3 className="text-sm font-bold text-slate-900">Send Notification to Gmail</h3>
                <p className="text-xs text-slate-500">Dispatch a live HTML test notification directly to any Gmail inbox.</p>
              </div>
            </div>
            <button
              onClick={() => setShowTestEmailModal(false)}
              className="p-1 text-slate-400 hover:text-slate-600 rounded-md"
            >
              <X className="h-4 w-4" />
            </button>
          </div>

          <form onSubmit={handleSendTestEmail} className="space-y-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Recipient Gmail Address
              </label>
              <input
                type="email"
                value={testEmailInput}
                onChange={(e) => setTestEmailInput(e.target.value)}
                placeholder="e.g. your.email@gmail.com"
                required
                className="w-full text-sm px-3 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {testEmailStatus && (
              <div className={`p-3 rounded-lg text-xs font-medium flex items-center gap-2 ${
                testEmailStatus.type === 'success' 
                  ? 'bg-emerald-50 text-emerald-800 border border-emerald-200' 
                  : 'bg-rose-50 text-rose-800 border border-rose-200'
              }`}>
                {testEmailStatus.type === 'success' ? <Check className="h-4 w-4 text-emerald-600 shrink-0" /> : <AlertTriangle className="h-4 w-4 text-rose-600 shrink-0" />}
                <span>{testEmailStatus.message}</span>
              </div>
            )}

            <div className="flex items-center justify-end gap-2 pt-1">
              <Button
                type="button"
                variant="ghost"
                size="sm"
                onClick={() => setShowTestEmailModal(false)}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="primary"
                size="sm"
                disabled={sendingTestEmail}
                className="flex items-center gap-1.5"
              >
                <Send className="h-3.5 w-3.5" />
                {sendingTestEmail ? 'Sending to Gmail...' : 'Send Message Now'}
              </Button>
            </div>
          </form>
        </div>
      )}

      {/* Preferences Drawer / Modal */}
      {showPreferences && (
        <div className="bg-slate-50 border border-slate-200 rounded-xl p-5 shadow-xs space-y-4">
          <div className="flex items-center justify-between border-b border-slate-200 pb-3">
            <div>
              <h3 className="text-sm font-bold text-slate-800">Notification Preferences</h3>
              <p className="text-xs text-slate-500">Customize notification channels (In-App, Email, SMS) for your account.</p>
            </div>
            <button
              onClick={() => setShowPreferences(false)}
              className="text-xs font-semibold text-slate-500 hover:text-slate-800"
            >
              Close
            </button>
          </div>

          {prefLoading ? (
            <div className="text-xs text-slate-400 py-4 text-center">Loading preferences...</div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              {preferences.map((p, idx) => (
                <div key={p.notificationType} className="bg-white p-3 rounded-lg border border-slate-200 flex items-center justify-between">
                  <div>
                    <span className="text-xs font-semibold text-slate-800">{p.displayName}</span>
                    <p className="text-[11px] text-slate-400">Type: {p.notificationType}</p>
                  </div>
                  <div className="flex items-center gap-3">
                    <label className="flex items-center gap-1 text-xs text-slate-600 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={p.inAppEnabled}
                        onChange={() => handleTogglePreference(idx, 'inAppEnabled')}
                        className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
                      />
                      In-App
                    </label>
                    <label className="flex items-center gap-1 text-xs text-slate-600 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={p.emailEnabled}
                        onChange={() => handleTogglePreference(idx, 'emailEnabled')}
                        className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
                      />
                      Email
                    </label>
                    <label className="flex items-center gap-1 text-xs text-slate-600 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={p.smsEnabled}
                        onChange={() => handleTogglePreference(idx, 'smsEnabled')}
                        className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
                      />
                      SMS
                    </label>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Filters Bar */}
      <div className="flex flex-wrap items-center justify-between gap-3 bg-white p-3 rounded-xl border border-slate-200 shadow-xs">
        <div className="flex flex-wrap items-center gap-1.5">
          {['ALL', 'ALERT', 'EVALUATION', 'RATING', 'IMPROVEMENT_ACTION', 'AI_INSIGHT', 'SECURITY', 'SYSTEM'].map((type) => (
            <button
              key={type}
              onClick={() => {
                setSelectedType(type);
                setPage(0);
              }}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                selectedType === type
                  ? 'bg-blue-600 text-white shadow-xs'
                  : 'text-slate-600 hover:bg-slate-100'
              }`}
            >
              {type === 'ALL' ? 'All Types' : type.replace('_', ' ')}
            </button>
          ))}
        </div>

        <div className="flex items-center gap-2">
          <label className="flex items-center gap-1.5 text-xs font-medium text-slate-700 cursor-pointer bg-slate-50 px-3 py-1.5 rounded-lg border border-slate-200">
            <input
              type="checkbox"
              checked={unreadOnly}
              onChange={(e) => {
                setUnreadOnly(e.target.checked);
                setPage(0);
              }}
              className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
            />
            Unread only
          </label>
        </div>
      </div>

      {/* Notifications List */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-xs divide-y divide-slate-100 overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-sm text-slate-400">Loading notification history...</div>
        ) : notifications.length === 0 ? (
          <div className="p-12 text-center">
            <Bell className="mx-auto h-10 w-10 text-slate-300 mb-3" />
            <h3 className="text-sm font-semibold text-slate-700">No notifications found</h3>
            <p className="text-xs text-slate-400 mt-1">There are no notifications matching your selected criteria.</p>
          </div>
        ) : (
          notifications.map((item) => (
            <div
              key={item.id}
              className={`p-4 sm:p-5 hover:bg-slate-50/80 transition-colors flex flex-col sm:flex-row sm:items-center justify-between gap-4 ${
                !item.read ? 'bg-blue-50/30' : ''
              }`}
            >
              <div className="flex items-start gap-3.5">
                <div className="h-10 w-10 rounded-xl bg-slate-100 flex items-center justify-center flex-shrink-0 mt-0.5">
                  {getIcon(item.notificationType)}
                </div>
                <div className="space-y-1">
                  <div className="flex flex-wrap items-center gap-2">
                    <span className={`text-sm font-bold ${!item.read ? 'text-slate-900' : 'text-slate-700'}`}>
                      {item.title}
                    </span>
                    {getPriorityBadge(item.priority)}
                    {!item.read && (
                      <span className="h-2 w-2 rounded-full bg-blue-600 animate-pulse" title="Unread" />
                    )}
                  </div>
                  <p className="text-xs text-slate-600 max-w-2xl leading-relaxed">
                    {item.message}
                  </p>
                  <div className="flex items-center gap-3 text-[11px] text-slate-400 pt-1">
                    <span>{(() => {
                      try {
                        const d = new Date(item.createdAt);
                        return isNaN(d.getTime()) ? String(item.createdAt || '') : `${d.toLocaleDateString()} at ${d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
                      } catch {
                        return String(item.createdAt || '');
                      }
                    })()}</span>
                    {item.relatedResourceType && (
                      <span className="font-medium text-slate-500 bg-slate-100 px-2 py-0.5 rounded">
                        {item.relatedResourceType} #{item.relatedResourceId}
                      </span>
                    )}
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-2 self-end sm:self-center">
                {item.relatedResourceType && (
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => handleNavigateResource(item)}
                    className="flex items-center gap-1 text-xs"
                  >
                    View <ArrowRight className="h-3.5 w-3.5" />
                  </Button>
                )}

                {!item.read && (
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => handleMarkAsRead(item.id)}
                    title="Mark as read"
                    className="text-xs text-blue-600 hover:text-blue-800"
                  >
                    <Check className="h-4 w-4" />
                  </Button>
                )}

                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => handleDelete(item.id)}
                  title="Delete notification"
                  className="text-xs text-slate-400 hover:text-rose-600"
                >
                  <Trash2 className="h-4 w-4" />
                </Button>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-between border-t border-slate-200 pt-4">
          <span className="text-xs text-slate-500">
            Page {page + 1} of {totalPages} ({totalElements} notifications)
          </span>
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="sm"
              disabled={page === 0}
              onClick={() => setPage(p => Math.max(0, p - 1))}
            >
              Previous
            </Button>
            <Button
              variant="outline"
              size="sm"
              disabled={page >= totalPages - 1}
              onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
            >
              Next
            </Button>
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationCenter;
