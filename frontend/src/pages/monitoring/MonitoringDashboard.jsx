import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Activity, ShieldAlert, AlertTriangle, Wrench, CheckCircle, 
  Users, RefreshCw, ArrowRight, ShieldCheck, Clock, ExternalLink 
} from 'lucide-react';
import { monitoringService } from '../../services/monitoring.service';
import { Button } from '../../components/common/Button';

export const MonitoringDashboard = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [autoRefresh, setAutoRefresh] = useState(true);
  const navigate = useNavigate();

  const fetchSummary = async () => {
    try {
      const res = await monitoringService.getMonitoringSummary();
      if (res?.data) {
        setData(res.data);
      }
    } catch (err) {
      console.error('Failed to load monitoring summary:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSummary();
    let interval = null;
    if (autoRefresh) {
      interval = setInterval(fetchSummary, 20000); // 20s auto-refresh
    }
    return () => interval && clearInterval(interval);
  }, [autoRefresh]);

  const getHealthBadge = (status) => {
    switch (status) {
      case 'HEALTHY':
        return (
          <div className="flex items-center gap-2 bg-emerald-50 text-emerald-700 px-4 py-2 rounded-xl border border-emerald-200">
            <ShieldCheck className="h-5 w-5 text-emerald-600 animate-pulse" />
            <div>
              <div className="text-xs font-bold uppercase tracking-wider">System Health Status</div>
              <div className="text-sm font-extrabold">All Operations Stable</div>
            </div>
          </div>
        );
      case 'WARNING':
        return (
          <div className="flex items-center gap-2 bg-amber-50 text-amber-700 px-4 py-2 rounded-xl border border-amber-200">
            <AlertTriangle className="h-5 w-5 text-amber-600 animate-pulse" />
            <div>
              <div className="text-xs font-bold uppercase tracking-wider">System Health Status</div>
              <div className="text-sm font-extrabold">Warning: Elevated Risk</div>
            </div>
          </div>
        );
      default:
        return (
          <div className="flex items-center gap-2 bg-rose-50 text-rose-700 px-4 py-2 rounded-xl border border-rose-200">
            <ShieldAlert className="h-5 w-5 text-rose-600 animate-pulse" />
            <div>
              <div className="text-xs font-bold uppercase tracking-wider">System Health Status</div>
              <div className="text-sm font-extrabold">Attention Required</div>
            </div>
          </div>
        );
    }
  };

  const getPriorityBadge = (p) => {
    switch (p) {
      case 'CRITICAL': return <span className="text-[10px] px-2 py-0.5 font-bold rounded bg-rose-100 text-rose-700">CRITICAL</span>;
      case 'HIGH': return <span className="text-[10px] px-2 py-0.5 font-bold rounded bg-amber-100 text-amber-700">HIGH</span>;
      default: return <span className="text-[10px] px-2 py-0.5 font-bold rounded bg-blue-100 text-blue-700">{p}</span>;
    }
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-200">
      {/* Header Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 border-b border-slate-200/80 pb-5">
        <div>
          <div className="flex items-center gap-2">
            <Activity className="h-6 w-6 text-blue-600" />
            <h1 className="text-2xl font-bold tracking-tight text-slate-900">Real-Time Operational Monitoring</h1>
          </div>
          <p className="mt-1 text-sm text-slate-500">
            Live pulse monitoring active suppliers, critical risk exposures, open remediation plans, and health metrics.
          </p>
        </div>

        <div className="flex items-center gap-3">
          {data && getHealthBadge(data.systemHealthStatus)}
          <Button
            variant="outline"
            size="sm"
            onClick={fetchSummary}
            className="flex items-center gap-1.5"
            title="Refresh Live Data"
          >
            <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          </Button>
        </div>
      </div>

      {loading && !data ? (
        <div className="p-16 text-center text-sm text-slate-400">Aggregating live operational pulse...</div>
      ) : data ? (
        <>
          {/* Key Metrics Grid */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Active Suppliers</span>
                <Users className="h-4 w-4 text-blue-500" />
              </div>
              <div className="mt-2 flex items-baseline gap-2">
                <span className="text-2xl font-black text-slate-900">{data.activeSuppliers}</span>
                <span className="text-xs text-slate-400">/ {data.totalSuppliers} total</span>
              </div>
            </div>

            <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">High Risk Vendors</span>
                <ShieldAlert className="h-4 w-4 text-rose-500" />
              </div>
              <div className="mt-2 flex items-baseline gap-2">
                <span className="text-2xl font-black text-rose-600">{data.highRiskSuppliersCount}</span>
                <span className="text-xs text-slate-400">watchlisted</span>
              </div>
            </div>

            <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Critical Alerts</span>
                <AlertTriangle className="h-4 w-4 text-amber-500" />
              </div>
              <div className="mt-2 flex items-baseline gap-2">
                <span className="text-2xl font-black text-amber-600">{data.criticalAlertsCount}</span>
                <span className="text-xs text-slate-400">active alerts</span>
              </div>
            </div>

            <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Open Actions (CAP)</span>
                <Wrench className="h-4 w-4 text-purple-500" />
              </div>
              <div className="mt-2 flex items-baseline gap-2">
                <span className="text-2xl font-black text-purple-600">
                  {data.openImprovementActionsCount + data.inProgressImprovementActionsCount}
                </span>
                <span className="text-xs text-slate-400">in progress</span>
              </div>
            </div>
          </div>

          {/* Monitoring Two-Column Grid */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Critical Early Warning Alerts */}
            <div className="bg-white rounded-xl border border-slate-200 shadow-xs p-5 space-y-4">
              <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                <div className="flex items-center gap-2">
                  <AlertTriangle className="h-4 w-4 text-rose-600" />
                  <h3 className="text-sm font-bold text-slate-800">Critical Early Warning Alerts</h3>
                </div>
                <button
                  onClick={() => navigate('/notifications')}
                  className="text-xs font-semibold text-blue-600 hover:text-blue-800 flex items-center gap-1"
                >
                  View All <ArrowRight className="h-3 w-3" />
                </button>
              </div>

              {data.criticalAlerts?.length === 0 ? (
                <div className="py-8 text-center text-xs text-slate-400">
                  <CheckCircle className="mx-auto h-8 w-8 text-emerald-400 mb-2" />
                  No critical early warning alerts active.
                </div>
              ) : (
                <div className="space-y-2.5">
                  {data.criticalAlerts?.map((alert) => (
                    <div
                      key={alert.id}
                      onClick={() => navigate(`/suppliers/${alert.supplierId}`)}
                      className="p-3 rounded-lg border border-rose-100 bg-rose-50/40 hover:bg-rose-50 cursor-pointer transition-colors"
                    >
                      <div className="flex items-center justify-between mb-1">
                        <span className="text-xs font-bold text-slate-800">{alert.supplierName}</span>
                        <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-rose-100 text-rose-700">
                          {alert.severity}
                        </span>
                      </div>
                      <p className="text-xs text-slate-600">{alert.message}</p>
                      <span className="text-[10px] text-slate-400 mt-1 block">
                        Triggered: {alert.triggeredDate}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* High-Risk Suppliers Watchlist */}
            <div className="bg-white rounded-xl border border-slate-200 shadow-xs p-5 space-y-4">
              <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                <div className="flex items-center gap-2">
                  <ShieldAlert className="h-4 w-4 text-amber-600" />
                  <h3 className="text-sm font-bold text-slate-800">High-Risk Vendor Watchlist</h3>
                </div>
                <button
                  onClick={() => navigate('/ai-intelligence')}
                  className="text-xs font-semibold text-blue-600 hover:text-blue-800 flex items-center gap-1"
                >
                  AI Intelligence <ArrowRight className="h-3 w-3" />
                </button>
              </div>

              {data.topRiskWatchlist?.length === 0 ? (
                <div className="py-8 text-center text-xs text-slate-400">
                  <CheckCircle className="mx-auto h-8 w-8 text-emerald-400 mb-2" />
                  No suppliers currently in high-risk threshold.
                </div>
              ) : (
                <div className="space-y-2.5">
                  {data.topRiskWatchlist?.map((s) => (
                    <div
                      key={s.supplierId}
                      onClick={() => navigate(`/suppliers/${s.supplierId}`)}
                      className="p-3 rounded-lg border border-slate-100 bg-slate-50/60 hover:bg-slate-100/80 cursor-pointer transition-colors flex items-center justify-between"
                    >
                      <div>
                        <div className="text-xs font-bold text-slate-800">{s.supplierName}</div>
                        <div className="text-[11px] text-slate-400">{s.supplierCode} | Score: {s.overallRating?.toFixed(1) || '0.0'}%</div>
                      </div>
                      <div className="text-right">
                        <span className={`text-xs font-bold px-2 py-0.5 rounded ${
                          s.riskLevel === 'CRITICAL' ? 'bg-rose-100 text-rose-700' :
                          s.riskLevel === 'HIGH' ? 'bg-amber-100 text-amber-700' :
                          'bg-blue-100 text-blue-700'
                        }`}>
                          Risk Score: {s.riskScore?.toFixed(0)} ({s.riskLevel})
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* Urgent Improvement Actions Ledger */}
          <div className="bg-white rounded-xl border border-slate-200 shadow-xs p-5 space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <Wrench className="h-4 w-4 text-purple-600" />
                <h3 className="text-sm font-bold text-slate-800">Urgent Supplier Improvement Actions (CAP)</h3>
              </div>
              <button
                onClick={() => navigate('/improvement-actions')}
                className="text-xs font-semibold text-blue-600 hover:text-blue-800 flex items-center gap-1"
              >
                All Actions <ArrowRight className="h-3 w-3" />
              </button>
            </div>

            {data.urgentActions?.length === 0 ? (
              <div className="py-6 text-center text-xs text-slate-400">
                No urgent improvement actions currently open.
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead>
                    <tr className="border-b border-slate-100 text-slate-400 uppercase text-[10px]">
                      <th className="py-2 px-3">Action Item</th>
                      <th className="py-2 px-3">Supplier</th>
                      <th className="py-2 px-3">Priority</th>
                      <th className="py-2 px-3">Status</th>
                      <th className="py-2 px-3">Due Date</th>
                      <th className="py-2 px-3">Assignee</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {data.urgentActions?.map((action) => (
                      <tr key={action.id} className="hover:bg-slate-50">
                        <td className="py-2.5 px-3 font-semibold text-slate-800">{action.title}</td>
                        <td className="py-2.5 px-3 text-slate-600">{action.supplierName}</td>
                        <td className="py-2.5 px-3">{getPriorityBadge(action.priority)}</td>
                        <td className="py-2.5 px-3">
                          <span className="px-2 py-0.5 font-semibold rounded-full bg-blue-50 text-blue-700 text-[11px]">
                            {action.status}
                          </span>
                        </td>
                        <td className="py-2.5 px-3 text-slate-500">{action.dueDate || 'No deadline'}</td>
                        <td className="py-2.5 px-3 text-slate-600">{action.assignedUserName || 'Unassigned'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </>
      ) : null}
    </div>
  );
};

export default MonitoringDashboard;
