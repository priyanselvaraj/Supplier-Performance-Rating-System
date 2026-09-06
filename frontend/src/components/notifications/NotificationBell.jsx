import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Bell, Check, CheckCheck, AlertTriangle, FileText, TrendingUp, Sparkles, Wrench, Shield, ArrowRight } from 'lucide-react';
import { notificationService } from '../../services/notification.service';

export const NotificationBell = () => {
  const [unreadCount, setUnreadCount] = useState(0);
  const [recentNotifications, setRecentNotifications] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();

  const fetchUnreadCount = async () => {
    try {
      const res = await notificationService.getUnreadCount();
      if (res?.data?.unreadCount !== undefined) {
        setUnreadCount(res.data.unreadCount);
      }
    } catch (err) {
      console.error('Failed to fetch unread count:', err);
    }
  };

  const fetchRecent = async () => {
    setLoading(true);
    try {
      const res = await notificationService.getRecentNotifications();
      if (res?.data) {
        setRecentNotifications(res.data);
      }
    } catch (err) {
      console.error('Failed to fetch recent notifications:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUnreadCount();
    const cleanup = notificationService.connectSse((event) => {
      if (event.type === 'COUNT_UPDATE') {
        setUnreadCount(event.count);
      } else {
        fetchUnreadCount();
      }
    });

    const handleOutsideClick = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleOutsideClick);

    return () => {
      cleanup && cleanup();
      document.removeEventListener('mousedown', handleOutsideClick);
    };
  }, []);

  const handleToggle = () => {
    if (!isOpen) {
      fetchRecent();
      fetchUnreadCount();
    }
    setIsOpen(!isOpen);
  };

  const handleMarkAsRead = async (id, e) => {
    e.stopPropagation();
    try {
      await notificationService.markAsRead(id);
      setRecentNotifications(prev => prev.map(n => n.id === id ? { ...n, read: true } : n));
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (err) {
      console.error('Failed to mark notification as read:', err);
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await notificationService.markAllAsRead();
      setRecentNotifications(prev => prev.map(n => ({ ...n, read: true })));
      setUnreadCount(0);
    } catch (err) {
      console.error('Failed to mark all as read:', err);
    }
  };

  const handleItemClick = (notification) => {
    if (!notification.read) {
      handleMarkAsRead(notification.id, { stopPropagation: () => {} });
    }
    setIsOpen(false);

    if (notification.relatedResourceType === 'SUPPLIER' && notification.relatedResourceId) {
      navigate(`/suppliers/${notification.relatedResourceId}`);
    } else if (notification.relatedResourceType === 'EVALUATION' && notification.relatedResourceId) {
      navigate(`/evaluations/${notification.relatedResourceId}`);
    } else if (notification.relatedResourceType === 'IMPROVEMENT_ACTION') {
      navigate('/improvement-actions');
    } else {
      navigate('/notifications');
    }
  };

  const getIcon = (type) => {
    switch (type) {
      case 'ALERT': return <AlertTriangle className="h-4 w-4 text-rose-600" />;
      case 'EVALUATION': return <FileText className="h-4 w-4 text-blue-600" />;
      case 'RATING': return <TrendingUp className="h-4 w-4 text-emerald-600" />;
      case 'AI_INSIGHT': return <Sparkles className="h-4 w-4 text-purple-600" />;
      case 'IMPROVEMENT_ACTION': return <Wrench className="h-4 w-4 text-amber-600" />;
      default: return <Shield className="h-4 w-4 text-slate-600" />;
    }
  };

  const formatDate = (val) => {
    if (!val) return '';
    try {
      const d = new Date(val);
      return isNaN(d.getTime()) ? String(val) : `${d.toLocaleDateString()} ${d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
    } catch {
      return String(val || '');
    }
  };

  const getPriorityBadge = (priority) => {
    switch (priority) {
      case 'CRITICAL': return <span className="text-[10px] px-1.5 py-0.5 font-bold rounded bg-rose-100 text-rose-700">CRITICAL</span>;
      case 'HIGH': return <span className="text-[10px] px-1.5 py-0.5 font-bold rounded bg-amber-100 text-amber-700">HIGH</span>;
      case 'LOW': return <span className="text-[10px] px-1.5 py-0.5 font-bold rounded bg-slate-100 text-slate-600">LOW</span>;
      default: return <span className="text-[10px] px-1.5 py-0.5 font-bold rounded bg-blue-100 text-blue-700">MEDIUM</span>;
    }
  };

  return (
    <div className="relative" ref={dropdownRef}>
      <button
        onClick={handleToggle}
        className="relative p-2 text-slate-600 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-colors focus:outline-none"
        title="Notifications"
      >
        <Bell className="h-5 w-5" />
        {unreadCount > 0 && (
          <span className="absolute top-1 right-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-rose-500 px-1 text-[10px] font-bold text-white shadow-xs animate-pulse">
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 sm:w-96 rounded-xl border border-slate-200 bg-white shadow-xl z-50 overflow-hidden animate-in fade-in slide-in-from-top-2 duration-150">
          <div className="flex items-center justify-between border-b border-slate-100 px-4 py-3 bg-slate-50/80">
            <div className="flex items-center gap-2">
              <span className="font-semibold text-slate-800 text-sm">Notifications</span>
              {unreadCount > 0 && (
                <span className="rounded-full bg-blue-100 px-2 py-0.5 text-xs font-semibold text-blue-700">
                  {unreadCount} new
                </span>
              )}
            </div>
            {unreadCount > 0 && (
              <button
                onClick={handleMarkAllRead}
                className="flex items-center gap-1 text-xs font-medium text-blue-600 hover:text-blue-800 transition-colors"
              >
                <CheckCheck className="h-3.5 w-3.5" />
                Mark all read
              </button>
            )}
          </div>

          <div className="max-h-80 overflow-y-auto divide-y divide-slate-100">
            {loading ? (
              <div className="py-8 text-center text-xs text-slate-400">Loading notifications...</div>
            ) : recentNotifications.length === 0 ? (
              <div className="py-8 text-center text-xs text-slate-400">No recent notifications</div>
            ) : (
              recentNotifications.map((item) => (
                <div
                  key={item.id}
                  onClick={() => handleItemClick(item)}
                  className={`p-3.5 hover:bg-slate-50 cursor-pointer transition-colors flex gap-3 ${
                    !item.read ? 'bg-blue-50/40' : ''
                  }`}
                >
                  <div className="mt-0.5 flex-shrink-0">
                    <div className="h-8 w-8 rounded-lg bg-slate-100 flex items-center justify-center">
                      {getIcon(item.notificationType)}
                    </div>
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center justify-between gap-1 mb-1">
                      <span className={`text-xs font-semibold truncate ${!item.read ? 'text-slate-900' : 'text-slate-700'}`}>
                        {item.title}
                      </span>
                      {getPriorityBadge(item.priority)}
                    </div>
                    <p className="text-xs text-slate-600 line-clamp-2 leading-relaxed mb-1.5">
                      {item.message}
                    </p>
                    <div className="flex items-center justify-between text-[11px] text-slate-400">
                      <span>{formatDate(item.createdAt)}</span>
                      {!item.read && (
                        <button
                          onClick={(e) => handleMarkAsRead(item.id, e)}
                          className="hover:text-blue-600 font-medium flex items-center gap-0.5"
                          title="Mark as read"
                        >
                          <Check className="h-3 w-3" /> Read
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>

          <div className="border-t border-slate-100 bg-slate-50/80 p-2.5 text-center">
            <button
              onClick={() => {
                setIsOpen(false);
                navigate('/notifications');
              }}
              className="inline-flex items-center gap-1.5 text-xs font-semibold text-blue-600 hover:text-blue-800 transition-colors"
            >
              View all in Notification Center <ArrowRight className="h-3.5 w-3.5" />
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
