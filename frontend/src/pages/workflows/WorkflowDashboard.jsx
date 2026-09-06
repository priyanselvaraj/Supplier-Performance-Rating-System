import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { workflowService } from '../../services/workflow.service';
import {
  GitPullRequest,
  CheckCircle2,
  Clock,
  AlertTriangle,
  XCircle,
  ShieldAlert,
  Search,
  Filter,
  RefreshCw,
  Eye,
  SlidersHorizontal,
  ChevronRight,
  ArrowUpDown,
  Calendar,
  Layers,
  Inbox
} from 'lucide-react';
import Badge from '../../components/common/Badge';

export const WorkflowDashboard = () => {
  const navigate = useNavigate();
  const { user, isAdmin } = useAuth();

  const [loading, setLoading] = useState(true);
  const [scanning, setScanning] = useState(false);
  const [workflows, setWorkflows] = useState([]);
  const [summary, setSummary] = useState({
    totalWorkflows: 0,
    pendingApprovals: 0,
    inProgressWorkflows: 0,
    approvedWorkflows: 0,
    rejectedWorkflows: 0,
    overdueTasks: 0,
    escalatedWorkflows: 0,
    myPendingTasks: 0
  });

  // Filters & Pagination
  const [filters, setFilters] = useState({
    type: '',
    status: '',
    keyword: '',
    startDate: '',
    endDate: ''
  });
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [sortBy, setSortBy] = useState('startedAt');
  const [direction, setDirection] = useState('desc');
  const [notificationMsg, setNotificationMsg] = useState('');

  const fetchSummary = useCallback(async () => {
    try {
      const res = await workflowService.getWorkflowSummary();
      if (res.success && res.data) {
        setSummary(res.data);
      }
    } catch (err) {
      console.error('Failed to fetch workflow summary:', err);
    }
  }, []);

  const fetchWorkflows = useCallback(async () => {
    setLoading(true);
    try {
      const params = {
        page,
        size,
        sortBy,
        direction,
        ...(filters.type && { type: filters.type }),
        ...(filters.status && { status: filters.status }),
        ...(filters.keyword && { keyword: filters.keyword }),
        ...(filters.startDate && { startDate: filters.startDate }),
        ...(filters.endDate && { endDate: filters.endDate })
      };

      const res = await workflowService.getWorkflows(params);
      if (res.success && res.data) {
        setWorkflows(res.data.content || []);
        setTotalPages(res.data.totalPages || 0);
        setTotalElements(res.data.totalElements || 0);
      }
    } catch (err) {
      console.error('Failed to fetch workflows:', err);
    } finally {
      setLoading(false);
    }
  }, [page, size, sortBy, direction, filters]);

  useEffect(() => {
    fetchSummary();
  }, [fetchSummary]);

  useEffect(() => {
    fetchWorkflows();
  }, [fetchWorkflows]);

  const handleFilterChange = (key, value) => {
    setFilters(prev => ({ ...prev, [key]: value }));
    setPage(0);
  };

  const handleResetFilters = () => {
    setFilters({
      type: '',
      status: '',
      keyword: '',
      startDate: '',
      endDate: ''
    });
    setPage(0);
  };

  const handleRunOverdueScan = async () => {
    setScanning(true);
    try {
      const res = await workflowService.checkOverdueTasks();
      if (res.success) {
        setNotificationMsg(`SLA Scanner executed: ${res.data?.escalatedCount || 0} overdue tasks auto-escalated.`);
        fetchSummary();
        fetchWorkflows();
        setTimeout(() => setNotificationMsg(''), 5000);
      }
    } catch (err) {
      console.error('Overdue scanner failed:', err);
    } finally {
      setScanning(false);
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'APPROVED':
        return <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200"><CheckCircle2 className="w-3.5 h-3.5" /> Approved</span>;
      case 'IN_PROGRESS':
        return <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-blue-50 text-blue-700 border border-blue-200"><Clock className="w-3.5 h-3.5 animate-pulse" /> In Progress</span>;
      case 'PENDING':
        return <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-slate-100 text-slate-700 border border-slate-200"><Clock className="w-3.5 h-3.5" /> Pending</span>;
      case 'REJECTED':
        return <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-rose-50 text-rose-700 border border-rose-200"><XCircle className="w-3.5 h-3.5" /> Rejected</span>;
      case 'ESCALATED':
        return <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-purple-50 text-purple-700 border border-purple-200"><ShieldAlert className="w-3.5 h-3.5 text-purple-600" /> Escalated</span>;
      case 'CANCELLED':
        return <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-slate-100 text-slate-500 border border-slate-200">Cancelled</span>;
      default:
        return <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-slate-100 text-slate-700">{status}</span>;
    }
  };

  const getTypeBadge = (type) => {
    switch (type) {
      case 'SUPPLIER_PROFILE_UPDATE':
        return <span className="px-2 py-0.5 rounded text-[11px] font-medium bg-indigo-50 text-indigo-700 border border-indigo-100">Profile Update</span>;
      case 'SUPPLIER_DOCUMENT_REVIEW':
        return <span className="px-2 py-0.5 rounded text-[11px] font-medium bg-cyan-50 text-cyan-700 border border-cyan-100">Document Review</span>;
      case 'EVALUATION_APPROVAL':
        return <span className="px-2 py-0.5 rounded text-[11px] font-medium bg-emerald-50 text-emerald-700 border border-emerald-100">Evaluation Sign-Off</span>;
      case 'IMPROVEMENT_ACTION_CLOSURE':
        return <span className="px-2 py-0.5 rounded text-[11px] font-medium bg-amber-50 text-amber-700 border border-amber-100">CAP Closure</span>;
      case 'SUPPLIER_STATUS_CHANGE':
        return <span className="px-2 py-0.5 rounded text-[11px] font-medium bg-purple-50 text-purple-700 border border-purple-100">Status Change</span>;
      default:
        return <span className="px-2 py-0.5 rounded text-[11px] font-medium bg-slate-100 text-slate-600">{type}</span>;
    }
  };

  const getSlaHealthBadge = (health) => {
    switch (health) {
      case 'ON_TIME':
        return <span className="inline-flex items-center gap-1 text-[11px] font-medium text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded-full"><span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span> On Time</span>;
      case 'NEAR_DUE':
        return <span className="inline-flex items-center gap-1 text-[11px] font-medium text-amber-600 bg-amber-50 px-2 py-0.5 rounded-full"><span className="w-1.5 h-1.5 rounded-full bg-amber-500 animate-ping"></span> Near Due</span>;
      case 'OVERDUE':
        return <span className="inline-flex items-center gap-1 text-[11px] font-medium text-rose-600 bg-rose-50 px-2 py-0.5 rounded-full"><AlertTriangle className="w-3 h-3 text-rose-500" /> Overdue</span>;
      case 'ESCALATED':
        return <span className="inline-flex items-center gap-1 text-[11px] font-medium text-purple-600 bg-purple-50 px-2 py-0.5 rounded-full"><ShieldAlert className="w-3 h-3 text-purple-600" /> Escalated</span>;
      default:
        return <span className="text-[11px] text-slate-400">—</span>;
    }
  };

  return (
    <div className="space-y-6 pb-12">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-6 rounded-2xl border border-slate-200/80 shadow-xs">
        <div>
          <div className="flex items-center gap-2 text-xs font-semibold text-blue-600 uppercase tracking-wider mb-1">
            <GitPullRequest className="w-4 h-4" />
            <span>Governance & Approvals</span>
          </div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
            Workflow & Multi-Level Approvals
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Monitor active business workflows, SLA deadlines, task delegations, and automated escalations.
          </p>
        </div>

        <div className="flex items-center gap-3 flex-wrap">
          <Link
            to="/my-approvals"
            className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-blue-600 text-white text-sm font-semibold hover:bg-blue-700 shadow-sm shadow-blue-500/20 transition-all"
          >
            <Inbox className="w-4 h-4" />
            <span>My Approvals</span>
            {summary.myPendingTasks > 0 && (
              <span className="ml-1 px-2 py-0.5 text-xs bg-white text-blue-700 rounded-full font-bold">
                {summary.myPendingTasks}
              </span>
            )}
          </Link>

          {isAdmin() && (
            <>
              <Link
                to="/admin/workflows"
                className="inline-flex items-center gap-2 px-3.5 py-2.5 rounded-xl bg-slate-100 text-slate-700 text-sm font-semibold hover:bg-slate-200 transition-all border border-slate-200"
              >
                <SlidersHorizontal className="w-4 h-4 text-slate-500" />
                <span>Workflow Config</span>
              </Link>

              <button
                onClick={handleRunOverdueScan}
                disabled={scanning}
                className="inline-flex items-center gap-2 px-3.5 py-2.5 rounded-xl bg-amber-50 text-amber-700 text-sm font-semibold hover:bg-amber-100 border border-amber-200 transition-all disabled:opacity-50"
                title="Scan for SLA deadline breaches and auto-escalate"
              >
                <RefreshCw className={`w-4 h-4 ${scanning ? 'animate-spin' : ''}`} />
                <span>{scanning ? 'Scanning...' : 'SLA Scan'}</span>
              </button>
            </>
          )}
        </div>
      </div>

      {notificationMsg && (
        <div className="p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm flex items-center justify-between shadow-xs">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-600" />
            <span>{notificationMsg}</span>
          </div>
          <button onClick={() => setNotificationMsg('')} className="text-emerald-600 hover:text-emerald-900 font-bold">✕</button>
        </div>
      )}

      {/* Summary KPI Cards */}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3.5">
        {/* Total Workflows */}
        <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-xs flex flex-col justify-between">
          <div className="flex items-center justify-between text-slate-500 mb-2">
            <span className="text-xs font-semibold uppercase tracking-wider">Total</span>
            <Layers className="w-4 h-4 text-slate-400" />
          </div>
          <div className="text-2xl font-bold text-slate-900">{summary.totalWorkflows}</div>
          <div className="text-[11px] text-slate-400 mt-1">All lifecycle instances</div>
        </div>

        {/* Pending Approvals */}
        <div className="bg-white p-4 rounded-xl border border-blue-200/80 bg-blue-50/20 shadow-xs flex flex-col justify-between">
          <div className="flex items-center justify-between text-blue-600 mb-2">
            <span className="text-xs font-semibold uppercase tracking-wider">In Progress</span>
            <Clock className="w-4 h-4 text-blue-500" />
          </div>
          <div className="text-2xl font-bold text-blue-700">{summary.pendingApprovals}</div>
          <div className="text-[11px] text-blue-600/80 mt-1">Active review stages</div>
        </div>

        {/* Approved */}
        <div className="bg-white p-4 rounded-xl border border-emerald-200/80 bg-emerald-50/20 shadow-xs flex flex-col justify-between">
          <div className="flex items-center justify-between text-emerald-600 mb-2">
            <span className="text-xs font-semibold uppercase tracking-wider">Approved</span>
            <CheckCircle2 className="w-4 h-4 text-emerald-500" />
          </div>
          <div className="text-2xl font-bold text-emerald-700">{summary.approvedWorkflows}</div>
          <div className="text-[11px] text-emerald-600/80 mt-1">Successfully signed off</div>
        </div>

        {/* Rejected */}
        <div className="bg-white p-4 rounded-xl border border-rose-200/80 bg-rose-50/20 shadow-xs flex flex-col justify-between">
          <div className="flex items-center justify-between text-rose-600 mb-2">
            <span className="text-xs font-semibold uppercase tracking-wider">Rejected</span>
            <XCircle className="w-4 h-4 text-rose-500" />
          </div>
          <div className="text-2xl font-bold text-rose-700">{summary.rejectedWorkflows}</div>
          <div className="text-[11px] text-rose-600/80 mt-1">Review declined</div>
        </div>

        {/* Overdue */}
        <div className="bg-white p-4 rounded-xl border border-amber-200/80 bg-amber-50/20 shadow-xs flex flex-col justify-between">
          <div className="flex items-center justify-between text-amber-600 mb-2">
            <span className="text-xs font-semibold uppercase tracking-wider">Overdue SLA</span>
            <AlertTriangle className="w-4 h-4 text-amber-500" />
          </div>
          <div className="text-2xl font-bold text-amber-700">{summary.overdueTasks}</div>
          <div className="text-[11px] text-amber-600/80 mt-1">Breached deadline</div>
        </div>

        {/* Escalated */}
        <div className="bg-white p-4 rounded-xl border border-purple-200/80 bg-purple-50/20 shadow-xs flex flex-col justify-between">
          <div className="flex items-center justify-between text-purple-600 mb-2">
            <span className="text-xs font-semibold uppercase tracking-wider">Escalated</span>
            <ShieldAlert className="w-4 h-4 text-purple-500" />
          </div>
          <div className="text-2xl font-bold text-purple-700">{summary.escalatedWorkflows}</div>
          <div className="text-[11px] text-purple-600/80 mt-1">Elevated to Admin</div>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-4">
        <div className="flex flex-col lg:flex-row gap-3 items-center justify-between">
          {/* Keyword Search */}
          <div className="relative flex-1 w-full">
            <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Search by title, resource, requester, or vendor name..."
              value={filters.keyword}
              onChange={(e) => handleFilterChange('keyword', e.target.value)}
              className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 text-sm focus:outline-hidden focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
            />
          </div>

          {/* Filters */}
          <div className="flex flex-wrap items-center gap-2.5 w-full lg:w-auto">
            {/* Type Filter */}
            <select
              value={filters.type}
              onChange={(e) => handleFilterChange('type', e.target.value)}
              className="px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-medium text-slate-700 bg-white focus:outline-hidden focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All Types</option>
              <option value="SUPPLIER_PROFILE_UPDATE">Profile Update</option>
              <option value="SUPPLIER_DOCUMENT_REVIEW">Document Review</option>
              <option value="EVALUATION_APPROVAL">Evaluation Sign-Off</option>
              <option value="IMPROVEMENT_ACTION_CLOSURE">CAP Closure</option>
              <option value="SUPPLIER_STATUS_CHANGE">Status Change</option>
            </select>

            {/* Status Filter */}
            <select
              value={filters.status}
              onChange={(e) => handleFilterChange('status', e.target.value)}
              className="px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-medium text-slate-700 bg-white focus:outline-hidden focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All Statuses</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="PENDING">Pending</option>
              <option value="APPROVED">Approved</option>
              <option value="REJECTED">Rejected</option>
              <option value="ESCALATED">Escalated</option>
            </select>

            {(filters.type || filters.status || filters.keyword || filters.startDate || filters.endDate) && (
              <button
                onClick={handleResetFilters}
                className="px-3 py-2 text-xs font-semibold text-slate-600 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 rounded-lg transition-all"
              >
                Reset
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Workflows Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-slate-700">
            <thead className="bg-slate-50/80 text-xs font-semibold text-slate-500 uppercase tracking-wider border-b border-slate-200">
              <tr>
                <th className="px-6 py-4">Workflow & Type</th>
                <th className="px-6 py-4">Status</th>
                <th className="px-6 py-4">Stage & Progress</th>
                <th className="px-6 py-4">Required Role</th>
                <th className="px-6 py-4">SLA Health</th>
                <th className="px-6 py-4">Initiator / Started</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200/80">
              {loading ? (
                <tr>
                  <td colSpan="7" className="px-6 py-12 text-center text-slate-400">
                    <div className="flex flex-col items-center gap-2">
                      <RefreshCw className="w-6 h-6 animate-spin text-blue-600" />
                      <span className="text-sm font-medium">Loading workflows...</span>
                    </div>
                  </td>
                </tr>
              ) : workflows.length === 0 ? (
                <tr>
                  <td colSpan="7" className="px-6 py-16 text-center text-slate-500">
                    <div className="flex flex-col items-center gap-2 max-w-sm mx-auto">
                      <div className="h-12 w-12 rounded-full bg-slate-100 flex items-center justify-center text-slate-400 mb-1">
                        <GitPullRequest className="w-6 h-6" />
                      </div>
                      <p className="font-semibold text-slate-800">No workflows found</p>
                      <p className="text-xs text-slate-400">
                        There are currently no workflow instances matching your selected filter criteria.
                      </p>
                    </div>
                  </td>
                </tr>
              ) : (
                workflows.map((wf) => {
                  const percentComplete = wf.totalSteps > 0
                    ? Math.min(100, Math.round((wf.currentStepOrder / wf.totalSteps) * 100))
                    : 0;

                  return (
                    <tr
                      key={wf.id}
                      className="hover:bg-slate-50/80 transition-colors cursor-pointer group"
                      onClick={() => navigate(`/workflows/${wf.id}`)}
                    >
                      {/* Title & Type */}
                      <td className="px-6 py-4">
                        <div className="space-y-1">
                          <div className="font-semibold text-slate-900 group-hover:text-blue-600 transition-colors flex items-center gap-2">
                            <span>#{wf.id}</span>
                            <span>{wf.title}</span>
                          </div>
                          <div className="flex items-center gap-2">
                            {getTypeBadge(wf.workflowType)}
                            <span className="text-xs text-slate-400 font-mono">
                              {wf.relatedResourceType} #{wf.relatedResourceId}
                            </span>
                          </div>
                        </div>
                      </td>

                      {/* Status */}
                      <td className="px-6 py-4 whitespace-nowrap">
                        {getStatusBadge(wf.status)}
                      </td>

                      {/* Progress */}
                      <td className="px-6 py-4">
                        <div className="w-36 space-y-1.5">
                          <div className="flex justify-between text-xs font-medium text-slate-600">
                            <span>Step {wf.currentStepOrder} of {wf.totalSteps}</span>
                            <span>{percentComplete}%</span>
                          </div>
                          <div className="w-full h-1.5 bg-slate-100 rounded-full overflow-hidden">
                            <div
                              className={`h-full rounded-full transition-all ${
                                wf.status === 'APPROVED'
                                  ? 'bg-emerald-500'
                                  : wf.status === 'REJECTED'
                                  ? 'bg-rose-500'
                                  : wf.status === 'ESCALATED'
                                  ? 'bg-purple-500'
                                  : 'bg-blue-600'
                              }`}
                              style={{ width: `${percentComplete}%` }}
                            />
                          </div>
                          <div className="text-[11px] text-slate-500 truncate max-w-[150px]" title={wf.currentStepName}>
                            {wf.currentStepName || '—'}
                          </div>
                        </div>
                      </td>

                      {/* Required Role */}
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-slate-100 text-slate-700">
                          {wf.currentRequiredRole ? wf.currentRequiredRole.replace('ROLE_', '') : '—'}
                        </span>
                      </td>

                      {/* SLA Health */}
                      <td className="px-6 py-4 whitespace-nowrap">
                        {getSlaHealthBadge(wf.slaHealth)}
                      </td>

                      {/* Initiator & Date */}
                      <td className="px-6 py-4 whitespace-nowrap text-xs text-slate-500">
                        <div className="font-medium text-slate-800">{wf.initiatedByName || 'System'}</div>
                        <div className="text-slate-400">
                          {wf.startedAt ? new Date(wf.startedAt).toLocaleDateString() : '—'}
                        </div>
                      </td>

                      {/* Actions */}
                      <td className="px-6 py-4 whitespace-nowrap text-right text-xs font-medium">
                        <Link
                          to={`/workflows/${wf.id}`}
                          onClick={(e) => e.stopPropagation()}
                          className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-blue-600 bg-blue-50 hover:bg-blue-100 hover:text-blue-800 font-semibold transition-all"
                        >
                          <Eye className="w-3.5 h-3.5" />
                          <span>View</span>
                        </Link>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-6 py-4 border-t border-slate-200 bg-slate-50/50">
            <span className="text-xs text-slate-500">
              Showing <span className="font-semibold text-slate-700">{page * size + 1}</span> to{' '}
              <span className="font-semibold text-slate-700">
                {Math.min((page + 1) * size, totalElements)}
              </span>{' '}
              of <span className="font-semibold text-slate-700">{totalElements}</span> entries
            </span>
            <div className="flex items-center gap-2">
              <button
                onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-600 hover:bg-white disabled:opacity-40 disabled:cursor-not-allowed transition-all"
              >
                Previous
              </button>
              <span className="text-xs text-slate-600 px-2 font-medium">
                Page {page + 1} of {totalPages}
              </span>
              <button
                onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                className="px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-600 hover:bg-white disabled:opacity-40 disabled:cursor-not-allowed transition-all"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default WorkflowDashboard;
