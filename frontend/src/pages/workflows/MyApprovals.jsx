import React, { useState, useEffect, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { workflowService } from '../../services/workflow.service';
import {
  Inbox,
  CheckCircle2,
  XCircle,
  Clock,
  AlertTriangle,
  History,
  FileCheck,
  Eye,
  RefreshCw,
  Send,
  MessageSquare,
  Building,
  ShieldCheck
} from 'lucide-react';
import Modal from '../../components/common/Modal';

export const MyApprovals = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [activeTab, setActiveTab] = useState('pending'); // 'pending' | 'history'
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  // Pending Tasks
  const [pendingTasks, setPendingTasks] = useState([]);
  const [pendingPage, setPendingPage] = useState(0);
  const [pendingTotalPages, setPendingTotalPages] = useState(0);
  const [pendingTotal, setPendingTotal] = useState(0);

  // History Tasks
  const [historyTasks, setHistoryTasks] = useState([]);
  const [historyPage, setHistoryPage] = useState(0);
  const [historyTotalPages, setHistoryTotalPages] = useState(0);
  const [historyTotal, setHistoryTotal] = useState(0);

  // Modals for Approve / Reject
  const [selectedTask, setSelectedTask] = useState(null);
  const [approveModalOpen, setApproveModalOpen] = useState(false);
  const [rejectModalOpen, setRejectModalOpen] = useState(false);
  const [comments, setComments] = useState('');
  const [rejectionReason, setRejectionReason] = useState('');
  const [feedbackMsg, setFeedbackMsg] = useState({ type: '', text: '' });

  const fetchPending = useCallback(async () => {
    setLoading(true);
    try {
      const res = await workflowService.getMyPendingApprovals(pendingPage, 10);
      if (res.success && res.data) {
        setPendingTasks(res.data.content || []);
        setPendingTotalPages(res.data.totalPages || 0);
        setPendingTotal(res.data.totalElements || 0);
      }
    } catch (err) {
      console.error('Failed to fetch pending approvals:', err);
    } finally {
      setLoading(false);
    }
  }, [pendingPage]);

  const fetchHistory = useCallback(async () => {
    setLoading(true);
    try {
      const res = await workflowService.getMyApprovalHistory(null, historyPage, 10);
      if (res.success && res.data) {
        setHistoryTasks(res.data.content || []);
        setHistoryTotalPages(res.data.totalPages || 0);
        setHistoryTotal(res.data.totalElements || 0);
      }
    } catch (err) {
      console.error('Failed to fetch approval history:', err);
    } finally {
      setLoading(false);
    }
  }, [historyPage]);

  useEffect(() => {
    if (activeTab === 'pending') {
      fetchPending();
    } else {
      fetchHistory();
    }
  }, [activeTab, fetchPending, fetchHistory]);

  const openApproveModal = (task, e) => {
    e.stopPropagation();
    setSelectedTask(task);
    setComments('');
    setApproveModalOpen(true);
  };

  const openRejectModal = (task, e) => {
    e.stopPropagation();
    setSelectedTask(task);
    setRejectionReason('');
    setRejectModalOpen(true);
  };

  const handleApprove = async () => {
    if (!selectedTask) return;
    setActionLoading(true);
    try {
      const res = await workflowService.approveTask(selectedTask.id, comments);
      if (res.success) {
        setFeedbackMsg({
          type: 'success',
          text: `Task '${selectedTask.stepName}' approved successfully.`
        });
        setApproveModalOpen(false);
        fetchPending();
        setTimeout(() => setFeedbackMsg({ type: '', text: '' }), 5000);
      }
    } catch (err) {
      console.error('Approval failed:', err);
      setFeedbackMsg({
        type: 'error',
        text: err.response?.data?.message || 'Approval action failed. Please try again.'
      });
    } finally {
      setActionLoading(false);
    }
  };

  const handleReject = async () => {
    if (!selectedTask) return;
    if (!rejectionReason.trim()) {
      alert('Please provide a reason for rejecting this task.');
      return;
    }

    setActionLoading(true);
    try {
      const res = await workflowService.rejectTask(selectedTask.id, rejectionReason);
      if (res.success) {
        setFeedbackMsg({
          type: 'success',
          text: `Task '${selectedTask.stepName}' rejected.`
        });
        setRejectModalOpen(false);
        fetchPending();
        setTimeout(() => setFeedbackMsg({ type: '', text: '' }), 5000);
      }
    } catch (err) {
      console.error('Rejection failed:', err);
      setFeedbackMsg({
        type: 'error',
        text: err.response?.data?.message || 'Rejection action failed. Please try again.'
      });
    } finally {
      setActionLoading(false);
    }
  };

  const getSlaRemainingText = (task) => {
    if (!task.dueAt) return null;
    const due = new Date(task.dueAt);
    const now = new Date();
    const diffMs = due - now;
    const diffHrs = Math.round(diffMs / (1000 * 60 * 60));

    if (diffHrs < 0) {
      return (
        <span className="inline-flex items-center gap-1 text-xs font-semibold text-rose-600 bg-rose-50 px-2.5 py-1 rounded-md border border-rose-200">
          <AlertTriangle className="w-3.5 h-3.5 text-rose-500" /> Overdue by {Math.abs(diffHrs)}h
        </span>
      );
    }
    if (diffHrs <= 8) {
      return (
        <span className="inline-flex items-center gap-1 text-xs font-semibold text-amber-600 bg-amber-50 px-2.5 py-1 rounded-md border border-amber-200">
          <Clock className="w-3.5 h-3.5 text-amber-500 animate-pulse" /> {diffHrs}h remaining
        </span>
      );
    }
    return (
      <span className="inline-flex items-center gap-1 text-xs font-medium text-slate-600 bg-slate-100 px-2.5 py-1 rounded-md">
        <Clock className="w-3.5 h-3.5 text-slate-400" /> {diffHrs}h remaining
      </span>
    );
  };

  return (
    <div className="space-y-6 pb-12">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-6 rounded-2xl border border-slate-200 shadow-xs">
        <div>
          <div className="flex items-center gap-2 text-xs font-semibold text-blue-600 uppercase tracking-wider mb-1">
            <ShieldCheck className="w-4 h-4" />
            <span>Decision Inbox</span>
          </div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
            My Approvals & Delegated Tasks
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Review, authorize, or decline workflow stages assigned to your role.
          </p>
        </div>

        <div className="flex items-center gap-2 bg-slate-100 p-1 rounded-xl">
          <button
            onClick={() => setActiveTab('pending')}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg text-xs font-semibold transition-all ${
              activeTab === 'pending'
                ? 'bg-white text-blue-600 shadow-xs'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Inbox className="w-4 h-4" />
            <span>Pending Approvals</span>
            {pendingTotal > 0 && (
              <span className="px-2 py-0.5 text-[11px] rounded-full bg-blue-600 text-white font-bold">
                {pendingTotal}
              </span>
            )}
          </button>

          <button
            onClick={() => setActiveTab('history')}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg text-xs font-semibold transition-all ${
              activeTab === 'history'
                ? 'bg-white text-blue-600 shadow-xs'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <History className="w-4 h-4" />
            <span>Approval History</span>
            {historyTotal > 0 && (
              <span className="px-2 py-0.5 text-[11px] rounded-full bg-slate-200 text-slate-700 font-semibold">
                {historyTotal}
              </span>
            )}
          </button>
        </div>
      </div>

      {feedbackMsg.text && (
        <div
          className={`p-4 rounded-xl text-sm flex items-center justify-between border shadow-xs ${
            feedbackMsg.type === 'success'
              ? 'bg-emerald-50 border-emerald-200 text-emerald-800'
              : 'bg-rose-50 border-rose-200 text-rose-800'
          }`}
        >
          <div className="flex items-center gap-2">
            {feedbackMsg.type === 'success' ? (
              <CheckCircle2 className="w-4 h-4 text-emerald-600" />
            ) : (
              <XCircle className="w-4 h-4 text-rose-600" />
            )}
            <span>{feedbackMsg.text}</span>
          </div>
          <button
            onClick={() => setFeedbackMsg({ type: '', text: '' })}
            className="font-bold hover:opacity-75"
          >
            ✕
          </button>
        </div>
      )}

      {/* Tab: Pending Approvals */}
      {activeTab === 'pending' && (
        <div className="space-y-4">
          {loading ? (
            <div className="bg-white p-12 rounded-2xl border border-slate-200 text-center">
              <RefreshCw className="w-6 h-6 animate-spin text-blue-600 mx-auto mb-2" />
              <p className="text-sm font-medium text-slate-500">Loading pending approval tasks...</p>
            </div>
          ) : pendingTasks.length === 0 ? (
            <div className="bg-white p-16 rounded-2xl border border-slate-200 text-center max-w-lg mx-auto">
              <div className="w-14 h-14 rounded-full bg-emerald-50 text-emerald-600 flex items-center justify-center mx-auto mb-3">
                <CheckCircle2 className="w-7 h-7" />
              </div>
              <h3 className="text-base font-bold text-slate-800">You're All Caught Up!</h3>
              <p className="text-xs text-slate-500 mt-1">
                There are no pending approvals assigned to your role at this time.
              </p>
              <Link
                to="/workflows"
                className="mt-4 inline-flex items-center gap-2 px-4 py-2 text-xs font-semibold text-blue-600 hover:text-blue-800 bg-blue-50 rounded-xl"
              >
                Browse All Workflows →
              </Link>
            </div>
          ) : (
            pendingTasks.map((task) => (
              <div
                key={task.id}
                className="bg-white p-6 rounded-2xl border border-slate-200 hover:border-blue-300 shadow-xs transition-all flex flex-col md:flex-row md:items-center justify-between gap-6"
              >
                {/* Task Details */}
                <div className="space-y-3 flex-1">
                  <div className="flex flex-wrap items-center gap-2.5">
                    <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-blue-100 text-blue-800 border border-blue-200">
                      Step {task.stepOrder}: {task.stepName}
                    </span>
                    <span className="px-2 py-0.5 rounded text-xs font-medium bg-slate-100 text-slate-700">
                      Role: {task.requiredRole ? task.requiredRole.replace('ROLE_', '') : ''}
                    </span>
                    {getSlaRemainingText(task)}
                  </div>

                  <div>
                    <h3 className="text-base font-bold text-slate-900 hover:text-blue-600 cursor-pointer transition-colors"
                        onClick={() => navigate(`/workflows/${task.workflowInstanceId}`)}>
                      {task.workflowTitle || `Workflow #${task.workflowInstanceId}`}
                    </h3>
                    <p className="text-xs text-slate-500 mt-0.5 flex items-center gap-2">
                      <span>Workflow ID: #{task.workflowInstanceId}</span>
                      <span>•</span>
                      <span>Assigned: {task.assignedAt ? new Date(task.assignedAt).toLocaleString() : '—'}</span>
                    </p>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex items-center gap-2.5 flex-shrink-0">
                  <Link
                    to={`/workflows/${task.workflowInstanceId}`}
                    className="px-3.5 py-2 rounded-xl text-xs font-semibold text-slate-700 bg-slate-100 hover:bg-slate-200 transition-all flex items-center gap-1.5"
                  >
                    <Eye className="w-3.5 h-3.5" />
                    <span>View</span>
                  </Link>

                  <button
                    onClick={(e) => openRejectModal(task, e)}
                    className="px-4 py-2 rounded-xl text-xs font-semibold text-rose-700 bg-rose-50 hover:bg-rose-100 border border-rose-200 transition-all flex items-center gap-1.5"
                  >
                    <XCircle className="w-3.5 h-3.5" />
                    <span>Reject</span>
                  </button>

                  <button
                    onClick={(e) => openApproveModal(task, e)}
                    className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-700 shadow-sm shadow-emerald-600/20 transition-all flex items-center gap-1.5"
                  >
                    <CheckCircle2 className="w-3.5 h-3.5" />
                    <span>Approve</span>
                  </button>
                </div>
              </div>
            ))
          )}

          {/* Pagination */}
          {pendingTotalPages > 1 && (
            <div className="flex justify-center gap-2 pt-4">
              <button
                onClick={() => setPendingPage(p => Math.max(0, p - 1))}
                disabled={pendingPage === 0}
                className="px-3 py-1.5 text-xs font-semibold border rounded-lg bg-white disabled:opacity-40"
              >
                Previous
              </button>
              <span className="text-xs text-slate-600 py-1.5 font-medium">
                Page {pendingPage + 1} of {pendingTotalPages}
              </span>
              <button
                onClick={() => setPendingPage(p => Math.min(pendingTotalPages - 1, p + 1))}
                disabled={pendingPage >= pendingTotalPages - 1}
                className="px-3 py-1.5 text-xs font-semibold border rounded-lg bg-white disabled:opacity-40"
              >
                Next
              </button>
            </div>
          )}
        </div>
      )}

      {/* Tab: Approval History */}
      {activeTab === 'history' && (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-700">
              <thead className="bg-slate-50 text-xs font-semibold text-slate-500 uppercase tracking-wider border-b border-slate-200">
                <tr>
                  <th className="px-6 py-4">Workflow & Stage</th>
                  <th className="px-6 py-4">Decision</th>
                  <th className="px-6 py-4">Review Comments</th>
                  <th className="px-6 py-4">Action Date</th>
                  <th className="px-6 py-4 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {loading ? (
                  <tr>
                    <td colSpan="5" className="px-6 py-10 text-center text-slate-400">
                      Loading history...
                    </td>
                  </tr>
                ) : historyTasks.length === 0 ? (
                  <tr>
                    <td colSpan="5" className="px-6 py-12 text-center text-slate-400">
                      No past approval history found.
                    </td>
                  </tr>
                ) : (
                  historyTasks.map((task) => (
                    <tr key={task.id} className="hover:bg-slate-50/80 transition-colors">
                      <td className="px-6 py-4">
                        <div className="font-semibold text-slate-900">
                          {task.workflowTitle || `Workflow #${task.workflowInstanceId}`}
                        </div>
                        <div className="text-xs text-slate-500">
                          Step {task.stepOrder}: {task.stepName}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {task.status === 'APPROVED' ? (
                          <span className="inline-flex items-center gap-1 text-xs font-semibold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-full border border-emerald-200">
                            <CheckCircle2 className="w-3.5 h-3.5" /> Approved
                          </span>
                        ) : task.status === 'REJECTED' ? (
                          <span className="inline-flex items-center gap-1 text-xs font-semibold text-rose-700 bg-rose-50 px-2.5 py-1 rounded-full border border-rose-200">
                            <XCircle className="w-3.5 h-3.5" /> Rejected
                          </span>
                        ) : (
                          <span className="text-xs font-medium px-2 py-0.5 rounded bg-slate-100 text-slate-600">
                            {task.status}
                          </span>
                        )}
                      </td>
                      <td className="px-6 py-4">
                        <span className="text-xs text-slate-600 line-clamp-2 italic">
                          "{task.comments || 'No comments left.'}"
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-xs text-slate-500">
                        {task.actionedAt ? new Date(task.actionedAt).toLocaleString() : '—'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right text-xs">
                        <Link
                          to={`/workflows/${task.workflowInstanceId}`}
                          className="inline-flex items-center gap-1 px-3 py-1.5 rounded-lg text-blue-600 bg-blue-50 hover:bg-blue-100 font-semibold"
                        >
                          <Eye className="w-3.5 h-3.5" /> View
                        </Link>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          {/* History Pagination */}
          {historyTotalPages > 1 && (
            <div className="flex justify-center gap-2 p-4 border-t border-slate-200">
              <button
                onClick={() => setHistoryPage(p => Math.max(0, p - 1))}
                disabled={historyPage === 0}
                className="px-3 py-1.5 text-xs font-semibold border rounded-lg bg-white disabled:opacity-40"
              >
                Previous
              </button>
              <span className="text-xs text-slate-600 py-1.5 font-medium">
                Page {historyPage + 1} of {historyTotalPages}
              </span>
              <button
                onClick={() => setHistoryPage(p => Math.min(historyTotalPages - 1, p + 1))}
                disabled={historyPage >= historyTotalPages - 1}
                className="px-3 py-1.5 text-xs font-semibold border rounded-lg bg-white disabled:opacity-40"
              >
                Next
              </button>
            </div>
          )}
        </div>
      )}

      {/* Modal: Approve Task */}
      <Modal
        isOpen={approveModalOpen}
        onClose={() => !actionLoading && setApproveModalOpen(false)}
        title={`Approve Step: ${selectedTask?.stepName || ''}`}
      >
        <div className="space-y-4">
          <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 text-xs text-slate-600 space-y-1">
            <div><span className="font-semibold text-slate-700">Workflow:</span> {selectedTask?.workflowTitle}</div>
            <div><span className="font-semibold text-slate-700">Assigned Role:</span> {selectedTask?.requiredRole}</div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1.5">
              Approval Comments (Optional)
            </label>
            <textarea
              rows={3}
              value={comments}
              onChange={(e) => setComments(e.target.value)}
              placeholder="e.g. All requirements verified and compliant with procurement policy."
              className="w-full p-3 rounded-xl border border-slate-200 text-sm focus:outline-hidden focus:ring-2 focus:ring-emerald-500 focus:border-transparent"
            />
          </div>

          <div className="flex justify-end gap-2.5 pt-2">
            <button
              onClick={() => setApproveModalOpen(false)}
              disabled={actionLoading}
              className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100 transition-all"
            >
              Cancel
            </button>
            <button
              onClick={handleApprove}
              disabled={actionLoading}
              className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-700 shadow-sm shadow-emerald-600/20 transition-all flex items-center gap-1.5 disabled:opacity-50"
            >
              <CheckCircle2 className="w-4 h-4" />
              <span>{actionLoading ? 'Approving...' : 'Confirm Approval'}</span>
            </button>
          </div>
        </div>
      </Modal>

      {/* Modal: Reject Task */}
      <Modal
        isOpen={rejectModalOpen}
        onClose={() => !actionLoading && setRejectModalOpen(false)}
        title={`Reject Step: ${selectedTask?.stepName || ''}`}
      >
        <div className="space-y-4">
          <div className="p-3.5 rounded-xl bg-rose-50 border border-rose-200 text-xs text-rose-800 space-y-1">
            <p className="font-semibold flex items-center gap-1.5">
              <AlertTriangle className="w-4 h-4 text-rose-600" />
              Rejection will terminate this workflow and notify the requester.
            </p>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1.5">
              Rejection Reason <span className="text-rose-500">*</span>
            </label>
            <textarea
              rows={3}
              value={rejectionReason}
              onChange={(e) => setRejectionReason(e.target.value)}
              placeholder="Please explain why this request is being rejected..."
              className="w-full p-3 rounded-xl border border-rose-300 text-sm focus:outline-hidden focus:ring-2 focus:ring-rose-500 focus:border-transparent"
              required
            />
          </div>

          <div className="flex justify-end gap-2.5 pt-2">
            <button
              onClick={() => setRejectModalOpen(false)}
              disabled={actionLoading}
              className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100 transition-all"
            >
              Cancel
            </button>
            <button
              onClick={handleReject}
              disabled={actionLoading || !rejectionReason.trim()}
              className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-rose-600 hover:bg-rose-700 shadow-sm shadow-rose-600/20 transition-all flex items-center gap-1.5 disabled:opacity-50"
            >
              <XCircle className="w-4 h-4" />
              <span>{actionLoading ? 'Rejecting...' : 'Confirm Rejection'}</span>
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default MyApprovals;
