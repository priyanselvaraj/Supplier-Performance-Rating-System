import React, { useState, useEffect } from 'react';
import { 
  Wrench, Plus, CheckCircle, Clock, AlertTriangle, XCircle, 
  Search, Filter, Calendar, User, Building, Trash2, Edit, Check
} from 'lucide-react';
import { improvementService } from '../../services/improvement.service';
import { supplierService } from '../../services/supplier.service';
import { useAuth } from '../../context/AuthContext';
import { Button } from '../../components/common/Button';

export const ImprovementActionsPage = () => {
  const { user } = useAuth();
  const isAdmin = user?.roles?.includes('ROLE_ADMIN');
  const [actions, setActions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [priorityFilter, setPriorityFilter] = useState('ALL');
  const [suppliers, setSuppliers] = useState([]);

  // Modals state
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showStatusModal, setShowStatusModal] = useState(false);
  const [selectedAction, setSelectedAction] = useState(null);

  // Form states
  const [formData, setFormData] = useState({
    supplierId: '',
    title: '',
    description: '',
    priority: 'MEDIUM',
    dueDate: '',
    resolutionNotes: ''
  });
  const [statusUpdateData, setStatusUpdateData] = useState({
    status: 'IN_PROGRESS',
    resolutionNotes: ''
  });

  const fetchActions = async () => {
    setLoading(true);
    try {
      const filters = {};
      if (statusFilter !== 'ALL') filters.status = statusFilter;
      if (priorityFilter !== 'ALL') filters.priority = priorityFilter;

      const res = await improvementService.getActions(page, 10, filters);
      if (res?.data) {
        setActions(res.data.content || []);
        setTotalPages(res.data.totalPages || 0);
        setTotalElements(res.data.totalElements || 0);
      }
    } catch (err) {
      console.error('Failed to fetch improvement actions:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchSuppliers = async () => {
    try {
      const res = await supplierService.getAllSuppliers(0, 100);
      if (res?.data?.content) {
        setSuppliers(res.data.content);
      }
    } catch (err) {
      console.error('Failed to load suppliers:', err);
    }
  };

  useEffect(() => {
    fetchActions();
  }, [page, statusFilter, priorityFilter]);

  useEffect(() => {
    fetchSuppliers();
  }, []);

  const handleCreateSubmit = async (e) => {
    e.preventDefault();
    try {
      await improvementService.createAction({
        supplierId: Number(formData.supplierId),
        title: formData.title,
        description: formData.description,
        priority: formData.priority,
        dueDate: formData.dueDate || null,
        resolutionNotes: formData.resolutionNotes || null
      });
      setShowCreateModal(false);
      setFormData({
        supplierId: '',
        title: '',
        description: '',
        priority: 'MEDIUM',
        dueDate: '',
        resolutionNotes: ''
      });
      fetchActions();
    } catch (err) {
      console.error('Failed to create improvement action:', err);
      alert(err.response?.data?.message || 'Failed to create action');
    }
  };

  const handleStatusSubmit = async (e) => {
    e.preventDefault();
    if (!selectedAction) return;
    try {
      await improvementService.updateStatus(
        selectedAction.id,
        statusUpdateData.status,
        statusUpdateData.resolutionNotes
      );
      setShowStatusModal(false);
      setSelectedAction(null);
      fetchActions();
    } catch (err) {
      console.error('Failed to update action status:', err);
      alert(err.response?.data?.message || 'Failed to update status');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this improvement action?')) return;
    try {
      await improvementService.deleteAction(id);
      fetchActions();
    } catch (err) {
      console.error('Failed to delete action:', err);
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'OPEN':
        return <span className="inline-flex items-center gap-1 text-xs px-2.5 py-0.5 font-semibold rounded-full bg-blue-100 text-blue-700"><Clock className="h-3 w-3" /> Open</span>;
      case 'IN_PROGRESS':
        return <span className="inline-flex items-center gap-1 text-xs px-2.5 py-0.5 font-semibold rounded-full bg-amber-100 text-amber-700"><Clock className="h-3 w-3" /> In Progress</span>;
      case 'COMPLETED':
        return <span className="inline-flex items-center gap-1 text-xs px-2.5 py-0.5 font-semibold rounded-full bg-emerald-100 text-emerald-700"><CheckCircle className="h-3 w-3" /> Completed</span>;
      default:
        return <span className="inline-flex items-center gap-1 text-xs px-2.5 py-0.5 font-semibold rounded-full bg-slate-100 text-slate-600"><XCircle className="h-3 w-3" /> Cancelled</span>;
    }
  };

  const getPriorityBadge = (priority) => {
    switch (priority) {
      case 'CRITICAL': return <span className="text-xs px-2 py-0.5 font-bold rounded bg-rose-100 text-rose-700">CRITICAL</span>;
      case 'HIGH': return <span className="text-xs px-2 py-0.5 font-bold rounded bg-amber-100 text-amber-700">HIGH</span>;
      case 'LOW': return <span className="text-xs px-2 py-0.5 font-bold rounded bg-slate-100 text-slate-600">LOW</span>;
      default: return <span className="text-xs px-2 py-0.5 font-bold rounded bg-blue-100 text-blue-700">MEDIUM</span>;
    }
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-200">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 border-b border-slate-200/80 pb-5">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-bold tracking-tight text-slate-900">Supplier Improvement Actions</h1>
            <span className="rounded-full bg-blue-100 px-2.5 py-0.5 text-xs font-semibold text-blue-700">
              {totalElements} active
            </span>
          </div>
          <p className="mt-1 text-sm text-slate-500">
            Collaborative remediation tracking, quality corrective action plans (CAP), and due date monitoring.
          </p>
        </div>

        <Button
          variant="primary"
          onClick={() => setShowCreateModal(true)}
          className="flex items-center gap-2"
        >
          <Plus className="h-4 w-4" /> New Improvement Action
        </Button>
      </div>

      {/* Filter Toolbar */}
      <div className="flex flex-wrap items-center justify-between gap-3 bg-white p-3.5 rounded-xl border border-slate-200 shadow-xs">
        {/* Status Tabs */}
        <div className="flex flex-wrap items-center gap-1.5">
          {['ALL', 'OPEN', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'].map((st) => (
            <button
              key={st}
              onClick={() => {
                setStatusFilter(st);
                setPage(0);
              }}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                statusFilter === st
                  ? 'bg-blue-600 text-white shadow-xs'
                  : 'text-slate-600 hover:bg-slate-100'
              }`}
            >
              {st === 'ALL' ? 'All Statuses' : st.replace('_', ' ')}
            </button>
          ))}
        </div>

        {/* Priority Filter */}
        <div className="flex items-center gap-2">
          <span className="text-xs text-slate-500 font-medium">Priority:</span>
          <select
            value={priorityFilter}
            onChange={(e) => {
              setPriorityFilter(e.target.value);
              setPage(0);
            }}
            className="text-xs rounded-lg border-slate-200 bg-slate-50 py-1.5 px-2.5 font-medium text-slate-700 focus:border-blue-500 focus:ring-blue-500"
          >
            <option value="ALL">All Priorities</option>
            <option value="CRITICAL">Critical</option>
            <option value="HIGH">High</option>
            <option value="MEDIUM">Medium</option>
            <option value="LOW">Low</option>
          </select>
        </div>
      </div>

      {/* Actions Ledger Table */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-xs overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-sm text-slate-400">Loading improvement actions...</div>
        ) : actions.length === 0 ? (
          <div className="p-12 text-center">
            <Wrench className="mx-auto h-10 w-10 text-slate-300 mb-3" />
            <h3 className="text-sm font-semibold text-slate-700">No improvement actions found</h3>
            <p className="text-xs text-slate-400 mt-1">Create an improvement action or adjust filters to view items.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-slate-100 bg-slate-50/75 text-[11px] font-semibold text-slate-500 uppercase tracking-wider">
                  <th className="py-3 px-4">Action Item & Description</th>
                  <th className="py-3 px-4">Supplier</th>
                  <th className="py-3 px-4">Priority</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4">Due Date</th>
                  <th className="py-3 px-4">Assigned To</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-xs">
                {actions.map((a) => (
                  <tr key={a.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="py-3.5 px-4 max-w-xs">
                      <div className="font-bold text-slate-800">{a.title}</div>
                      <p className="text-slate-500 line-clamp-2 mt-0.5">{a.description}</p>
                      {a.resolutionNotes && (
                        <div className="mt-1 text-[11px] text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200 inline-block">
                          Note: {a.resolutionNotes}
                        </div>
                      )}
                    </td>
                    <td className="py-3.5 px-4">
                      <div className="font-semibold text-slate-800">{a.supplierName}</div>
                      <span className="text-[11px] font-mono text-slate-400">{a.supplierCode}</span>
                    </td>
                    <td className="py-3.5 px-4">{getPriorityBadge(a.priority)}</td>
                    <td className="py-3.5 px-4">{getStatusBadge(a.status)}</td>
                    <td className="py-3.5 px-4">
                      <div className="flex items-center gap-1 text-slate-600">
                        <Calendar className="h-3.5 w-3.5 text-slate-400" />
                        <span>{a.dueDate || 'No deadline'}</span>
                      </div>
                    </td>
                    <td className="py-3.5 px-4">
                      <div className="flex items-center gap-1.5 text-slate-700">
                        <User className="h-3.5 w-3.5 text-slate-400" />
                        <span>{a.assignedUserName || 'Unassigned'}</span>
                      </div>
                    </td>
                    <td className="py-3.5 px-4 text-right">
                      <div className="flex items-center justify-end gap-1">
                        <button
                          onClick={() => {
                            setSelectedAction(a);
                            setStatusUpdateData({
                              status: a.status,
                              resolutionNotes: a.resolutionNotes || ''
                            });
                            setShowStatusModal(true);
                          }}
                          className="px-2.5 py-1 text-xs font-semibold text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                          title="Update Status"
                        >
                          Update Status
                        </button>
                        {isAdmin && (
                          <button
                            onClick={() => handleDelete(a.id)}
                            className="p-1 text-slate-400 hover:text-rose-600 rounded"
                            title="Delete Action"
                          >
                            <Trash2 className="h-4 w-4" />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-between border-t border-slate-200 pt-4">
          <span className="text-xs text-slate-500">
            Page {page + 1} of {totalPages} ({totalElements} actions)
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

      {/* Create Action Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 p-4 animate-in fade-in duration-150">
          <div className="w-full max-w-lg rounded-xl bg-white p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-base font-bold text-slate-900">Create Improvement Action</h3>
              <button onClick={() => setShowCreateModal(false)} className="text-slate-400 hover:text-slate-600">✕</button>
            </div>

            <form onSubmit={handleCreateSubmit} className="space-y-3.5">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Target Supplier *</label>
                <select
                  required
                  value={formData.supplierId}
                  onChange={(e) => setFormData({ ...formData, supplierId: e.target.value })}
                  className="w-full text-xs rounded-lg border border-slate-300 p-2 focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Select Supplier</option>
                  {suppliers.map(s => (
                    <option key={s.id} value={s.id}>{s.name} ({s.supplierCode})</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Action Title *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g., Mandatory Quality Audit & ISO Review"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  className="w-full text-xs rounded-lg border border-slate-300 p-2 focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Description & Requirements *</label>
                <textarea
                  required
                  rows={3}
                  placeholder="Detail root causes, required corrective actions, and acceptance criteria..."
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full text-xs rounded-lg border border-slate-300 p-2 focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Priority</label>
                  <select
                    value={formData.priority}
                    onChange={(e) => setFormData({ ...formData, priority: e.target.value })}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2 focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                    <option value="CRITICAL">Critical</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Target Due Date</label>
                  <input
                    type="date"
                    value={formData.dueDate}
                    onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2 focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-3 border-t border-slate-100">
                <Button type="button" variant="outline" size="sm" onClick={() => setShowCreateModal(false)}>
                  Cancel
                </Button>
                <Button type="submit" variant="primary" size="sm">
                  Create Action Plan
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Update Status Modal */}
      {showStatusModal && selectedAction && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 p-4 animate-in fade-in duration-150">
          <div className="w-full max-w-md rounded-xl bg-white p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-base font-bold text-slate-900">Update Action Status</h3>
              <button onClick={() => setShowStatusModal(false)} className="text-slate-400 hover:text-slate-600">✕</button>
            </div>

            <form onSubmit={handleStatusSubmit} className="space-y-3.5">
              <div>
                <span className="text-xs text-slate-500 font-medium">Action Item:</span>
                <p className="text-sm font-bold text-slate-800">{selectedAction.title}</p>
                <span className="text-xs text-slate-400">Supplier: {selectedAction.supplierName}</span>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">New Status *</label>
                <select
                  value={statusUpdateData.status}
                  onChange={(e) => setStatusUpdateData({ ...statusUpdateData, status: e.target.value })}
                  className="w-full text-xs rounded-lg border border-slate-300 p-2 focus:ring-2 focus:ring-blue-500"
                >
                  <option value="OPEN">Open</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="COMPLETED">Completed</option>
                  <option value="CANCELLED">Cancelled</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Resolution / Progress Notes</label>
                <textarea
                  rows={3}
                  placeholder="Summarize outcome, audit verification, or next steps..."
                  value={statusUpdateData.resolutionNotes}
                  onChange={(e) => setStatusUpdateData({ ...statusUpdateData, resolutionNotes: e.target.value })}
                  className="w-full text-xs rounded-lg border border-slate-300 p-2 focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div className="flex justify-end gap-2 pt-3 border-t border-slate-100">
                <Button type="button" variant="outline" size="sm" onClick={() => setShowStatusModal(false)}>
                  Cancel
                </Button>
                <Button type="submit" variant="primary" size="sm">
                  Save Changes
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default ImprovementActionsPage;
