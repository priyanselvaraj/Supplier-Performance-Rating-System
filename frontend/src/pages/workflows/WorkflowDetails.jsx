import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { workflowService } from '../../services/workflow.service';
import {
  GitPullRequest,
  CheckCircle2,
  Clock,
  XCircle,
  ShieldAlert,
  ArrowLeft,
  UserCheck,
  Building,
  Calendar,
  FileText,
  AlertTriangle,
  History,
  Info,
  Layers,
  Send,
  RefreshCw,
  Sliders,
  Check,
  ChevronRight,
  ShieldCheck
} from 'lucide-react';
import Modal from '../../components/common/Modal';

export const WorkflowDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAdmin, hasRole } = useAuth();

  const [workflow, setWorkflow] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [feedbackMsg, setFeedbackMsg] = useState({ type: '', text: '' });

  // Action Modals
  const [activeTask, setActiveTask] = useState(null);
  const [approveModalOpen, setApproveModalOpen] = useState(false);
  const [rejectModalOpen, setRejectModalOpen] = useState(false);
  const [escalateModalOpen, setEscalateModalOpen] = useState(false);
  const [comments, setComments] = useState('');
  const [rejectionReason, setRejectionReason] = useState('');
  const [escalationReason, setEscalationReason] = useState('');

  const fetchDetails = useCallback(async () => {
    setLoading(true);
    try {
      const res = await workflowService.getWorkflowById(id);
      if (res.success && res.data) {
        setWorkflow(res.data);
      }
    } catch (err) {
      console.error('Failed to fetch workflow details:', err);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchDetails();
  }, [fetchDetails]);

  // Find current pending task
  const currentPendingTask = workflow?.tasks?.find(t => t.status === 'PENDING' || t.status === 'ESCALATED');

  // Check if logged-in user can action current task
  const canActionCurrentTask = () => {
    if (!currentPendingTask || !user) return false;
    if (isAdmin()) return true;
    const reqRole = currentPendingTask.requiredRole;
    if (reqRole === 'ROLE_MANAGER' && hasRole && hasRole('MANAGER')) return true;
    if (reqRole === 'ROLE_ADMIN' && hasRole && hasRole('ADMIN')) return true;
    return false;
  };

  const handleApprove = async () => {
    if (!currentPendingTask) return;
    setActionLoading(true);
    try {
      const res = await workflowService.approveTask(currentPendingTask.id, comments);
      if (res.success) {
        setFeedbackMsg({
          type: 'success',
          text: `Task '${currentPendingTask.stepName}' approved successfully.`
        });
        setApproveModalOpen(false);
        fetchDetails();
        setTimeout(() => setFeedbackMsg({ type: '', text: '' }), 5000);
      }
    } catch (err) {
      console.error('Approval error:', err);
      setFeedbackMsg({
        type: 'error',
        text: err.response?.data?.message || 'Failed to approve task.'
      });
    } finally {
      setActionLoading(false);
    }
  };

  const handleReject = async () => {
    if (!currentPendingTask) return;
    if (!rejectionReason.trim()) {
      alert('Please provide a reason for rejecting this workflow stage.');
      return;
    }
    setActionLoading(true);
    try {
      const res = await workflowService.rejectTask(currentPendingTask.id, rejectionReason);
      if (res.success) {
        setFeedbackMsg({
          type: 'success',
          text: `Workflow rejected at '${currentPendingTask.stepName}'.`
        });
        setRejectModalOpen(false);
        fetchDetails();
        setTimeout(() => setFeedbackMsg({ type: '', text: '' }), 5000);
      }
    } catch (err) {
      console.error('Rejection error:', err);
      setFeedbackMsg({
        type: 'error',
        text: err.response?.data?.message || 'Failed to reject task.'
      });
    } finally {
      setActionLoading(false);
    }
  };

  const handleEscalate = async () => {
    if (!currentPendingTask) return;
    if (!escalationReason.trim()) {
      alert('Please provide an escalation justification.');
      return;
    }
    setActionLoading(true);
    try {
      const res = await workflowService.escalateTask(currentPendingTask.id, escalationReason, 'ROLE_ADMIN');
      if (res.success) {
        setFeedbackMsg({
          type: 'success',
          text: `Task escalated to Administrator successfully.`
        });
        setEscalateModalOpen(false);
        fetchDetails();
        setTimeout(() => setFeedbackMsg({ type: '', text: '' }), 5000);
      }
    } catch (err) {
      console.error('Escalation error:', err);
      setFeedbackMsg({
        type: 'error',
        text: err.response?.data?.message || 'Failed to escalate task.'
      });
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="bg-white p-16 rounded-2xl border border-slate-200 text-center">
        <RefreshCw className="w-8 h-8 animate-spin text-blue-600 mx-auto mb-3" />
        <p className="text-sm font-medium text-slate-500">Loading workflow execution timeline...</p>
      </div>
    );
  }

  if (!workflow) {
    return (
      <div className="bg-white p-16 rounded-2xl border border-slate-200 text-center max-w-md mx-auto">
        <AlertTriangle className="w-10 h-10 text-amber-500 mx-auto mb-3" />
        <h2 className="text-lg font-bold text-slate-900">Workflow Not Found</h2>
        <p className="text-xs text-slate-500 mt-1">The requested workflow instance does not exist or has been removed.</p>
        <Link to="/workflows" className="mt-4 inline-flex items-center gap-1.5 px-4 py-2 text-xs font-semibold text-blue-600 bg-blue-50 rounded-xl">
          ← Back to Workflows
        </Link>
      </div>
    );
  }

  // Parse metadata if JSON string
  let parsedMetadata = null;
  try {
    if (workflow.metadata) {
      parsedMetadata = typeof workflow.metadata === 'string' ? JSON.parse(workflow.metadata) : workflow.metadata;
    }
  } catch (ignored) {}

  return (
    <div className="space-y-6 pb-12">
      {/* Back Link & Header */}
      <div className="flex flex-col gap-4">
        <Link
          to="/workflows"
          className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-blue-600 transition-colors w-fit"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Workflows</span>
        </Link>

        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs flex flex-col lg:flex-row lg:items-center justify-between gap-6">
          <div className="space-y-2">
            <div className="flex flex-wrap items-center gap-2.5">
              <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-blue-50 text-blue-700 border border-blue-200">
                #{workflow.id}
              </span>
              <span className="px-2.5 py-1 rounded text-xs font-semibold bg-slate-100 text-slate-700">
                {workflow.workflowType ? workflow.workflowType.replace(/_/g, ' ') : ''}
              </span>
              {workflow.status === 'APPROVED' && (
                <span className="inline-flex items-center gap-1 text-xs font-bold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-full border border-emerald-200">
                  <CheckCircle2 className="w-3.5 h-3.5" /> Approved & Completed
                </span>
              )}
              {workflow.status === 'IN_PROGRESS' && (
                <span className="inline-flex items-center gap-1 text-xs font-bold text-blue-700 bg-blue-50 px-2.5 py-1 rounded-full border border-blue-200">
                  <Clock className="w-3.5 h-3.5 animate-pulse" /> In Progress (Step {workflow.currentStepOrder}/{workflow.totalSteps})
                </span>
              )}
              {workflow.status === 'REJECTED' && (
                <span className="inline-flex items-center gap-1 text-xs font-bold text-rose-700 bg-rose-50 px-2.5 py-1 rounded-full border border-rose-200">
                  <XCircle className="w-3.5 h-3.5" /> Rejected
                </span>
              )}
              {workflow.status === 'ESCALATED' && (
                <span className="inline-flex items-center gap-1 text-xs font-bold text-purple-700 bg-purple-50 px-2.5 py-1 rounded-full border border-purple-200">
                  <ShieldAlert className="w-3.5 h-3.5" /> Escalated to Admin
                </span>
              )}
            </div>

            <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
              {workflow.title}
            </h1>

            <div className="flex flex-wrap items-center gap-4 text-xs text-slate-500 pt-1">
              <span>Initiated by: <strong className="text-slate-700">{workflow.initiatedByName || 'System'}</strong></span>
              <span>•</span>
              <span>Started: <strong className="text-slate-700">{workflow.startedAt ? new Date(workflow.startedAt).toLocaleString() : '—'}</strong></span>
              {workflow.completedAt && (
                <>
                  <span>•</span>
                  <span>Completed: <strong className="text-slate-700">{new Date(workflow.completedAt).toLocaleString()}</strong></span>
                </>
              )}
            </div>
          </div>

          {/* Action Buttons for Authorized Approver */}
          {canActionCurrentTask() && (workflow.status === 'IN_PROGRESS' || workflow.status === 'ESCALATED') && (
            <div className="flex items-center gap-2.5 bg-slate-50 p-3 rounded-xl border border-slate-200">
              <button
                onClick={() => {
                  setActiveTask(currentPendingTask);
                  setComments('');
                  setApproveModalOpen(true);
                }}
                className="px-4 py-2 text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl shadow-sm transition-all flex items-center gap-1.5"
              >
                <CheckCircle2 className="w-4 h-4" />
                <span>Approve Step</span>
              </button>

              <button
                onClick={() => {
                  setActiveTask(currentPendingTask);
                  setRejectionReason('');
                  setRejectModalOpen(true);
                }}
                className="px-4 py-2 text-xs font-semibold text-rose-700 bg-rose-50 hover:bg-rose-100 border border-rose-200 rounded-xl transition-all flex items-center gap-1.5"
              >
                <XCircle className="w-4 h-4" />
                <span>Reject</span>
              </button>

              {isAdmin() && (
                <button
                  onClick={() => {
                    setActiveTask(currentPendingTask);
                    setEscalationReason('');
                    setEscalateModalOpen(true);
                  }}
                  className="px-3.5 py-2 text-xs font-semibold text-purple-700 bg-purple-50 hover:bg-purple-100 border border-purple-200 rounded-xl transition-all flex items-center gap-1.5"
                >
                  <ShieldAlert className="w-4 h-4 text-purple-600" />
                  <span>Escalate</span>
                </button>
              )}
            </div>
          )}
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
          <button onClick={() => setFeedbackMsg({ type: '', text: '' })} className="font-bold">✕</button>
        </div>
      )}

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column: Visual Step Flow Timeline (2 Cols) */}
        <div className="lg:col-span-2 space-y-6">
          {/* Step Timeline Card */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-6">
            <div className="flex items-center justify-between border-b border-slate-100 pb-4">
              <div className="flex items-center gap-2 text-slate-900 font-bold text-base">
                <GitPullRequest className="w-5 h-5 text-blue-600" />
                <span>Multi-Stage Approval Hierarchy</span>
              </div>
              <span className="text-xs font-medium text-slate-500">
                {workflow.tasks?.length || 0} configured step(s)
              </span>
            </div>

            {/* Visual Step Timeline */}
            <div className="space-y-6 relative before:absolute before:inset-0 before:left-5 before:w-0.5 before:bg-slate-200 before:z-0">
              {workflow.tasks?.map((task, idx) => {
                const isApproved = task.status === 'APPROVED';
                const isRejected = task.status === 'REJECTED';
                const isEscalated = task.status === 'ESCALATED';
                const isPending = task.status === 'PENDING';

                return (
                  <div key={task.id} className="relative flex items-start gap-4 z-10">
                    {/* Status Circle Icon */}
                    <div
                      className={`w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 font-bold text-sm shadow-xs ${
                        isApproved
                          ? 'bg-emerald-600 text-white'
                          : isRejected
                          ? 'bg-rose-600 text-white'
                          : isEscalated
                          ? 'bg-purple-600 text-white animate-pulse'
                          : isPending
                          ? 'bg-blue-600 text-white ring-4 ring-blue-100'
                          : 'bg-slate-100 text-slate-400 border border-slate-300'
                      }`}
                    >
                      {isApproved ? (
                        <Check className="w-5 h-5" />
                      ) : isRejected ? (
                        <XCircle className="w-5 h-5" />
                      ) : isEscalated ? (
                        <ShieldAlert className="w-5 h-5" />
                      ) : (
                        <span>{task.stepOrder}</span>
                      )}
                    </div>

                    {/* Step Card */}
                    <div
                      className={`flex-1 p-5 rounded-2xl border transition-all ${
                        isPending
                          ? 'bg-blue-50/30 border-blue-200 shadow-xs'
                          : isApproved
                          ? 'bg-emerald-50/20 border-emerald-200'
                          : isRejected
                          ? 'bg-rose-50/20 border-rose-200'
                          : isEscalated
                          ? 'bg-purple-50/20 border-purple-200'
                          : 'bg-slate-50/60 border-slate-200'
                      }`}
                    >
                      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                        <div>
                          <div className="flex items-center gap-2">
                            <h3 className="font-bold text-slate-900 text-sm">
                              Step {task.stepOrder}: {task.stepName}
                            </h3>
                            <span className="text-[11px] font-semibold px-2 py-0.5 rounded bg-slate-100 text-slate-700">
                              {task.requiredRole ? task.requiredRole.replace('ROLE_', '') : ''}
                            </span>
                          </div>
                          {task.stepInstructions && (
                            <p className="text-xs text-slate-500 mt-1 italic">
                              "{task.stepInstructions}"
                            </p>
                          )}
                        </div>

                        <div>
                          {isApproved && (
                            <span className="inline-flex items-center gap-1 text-xs font-bold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-md border border-emerald-200">
                              <CheckCircle2 className="w-3.5 h-3.5" /> Approved
                            </span>
                          )}
                          {isRejected && (
                            <span className="inline-flex items-center gap-1 text-xs font-bold text-rose-700 bg-rose-50 px-2.5 py-1 rounded-md border border-rose-200">
                              <XCircle className="w-3.5 h-3.5" /> Rejected
                            </span>
                          )}
                          {isEscalated && (
                            <span className="inline-flex items-center gap-1 text-xs font-bold text-purple-700 bg-purple-50 px-2.5 py-1 rounded-md border border-purple-200">
                              <ShieldAlert className="w-3.5 h-3.5" /> Escalated
                            </span>
                          )}
                          {isPending && (
                            <span className="inline-flex items-center gap-1 text-xs font-bold text-blue-700 bg-blue-50 px-2.5 py-1 rounded-md border border-blue-200 animate-pulse">
                              <Clock className="w-3.5 h-3.5" /> Awaiting Action
                            </span>
                          )}
                        </div>
                      </div>

                      {/* Decision Details & Comments */}
                      <div className="mt-3.5 pt-3.5 border-t border-slate-200/60 text-xs text-slate-600 space-y-1.5">
                        <div className="flex flex-wrap items-center justify-between gap-2">
                          <span>
                            Assigned: <strong className="text-slate-800">{task.assignedAt ? new Date(task.assignedAt).toLocaleString() : '—'}</strong>
                          </span>
                          {task.dueAt && (
                            <span className={new Date(task.dueAt) < new Date() && isPending ? 'text-rose-600 font-bold' : 'text-slate-500'}>
                              SLA Due: {new Date(task.dueAt).toLocaleString()}
                            </span>
                          )}
                        </div>

                        {task.actionedByName && (
                          <div>
                            Actioned By: <strong className="text-slate-800">{task.actionedByName}</strong> on{' '}
                            {task.actionedAt ? new Date(task.actionedAt).toLocaleString() : '—'}
                          </div>
                        )}

                        {task.comments && (
                          <div className="p-2.5 rounded-lg bg-white border border-slate-200 text-slate-700 italic">
                            "{task.comments}"
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Audit History Log */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
            <div className="flex items-center gap-2 text-slate-900 font-bold text-base border-b border-slate-100 pb-3">
              <History className="w-5 h-5 text-blue-600" />
              <span>Immutable Audit Trail</span>
            </div>

            <div className="divide-y divide-slate-100">
              {workflow.auditLogs?.map((log) => (
                <div key={log.id} className="py-3 flex items-start justify-between gap-4 text-xs">
                  <div className="space-y-0.5">
                    <div className="flex items-center gap-2">
                      <span className="font-semibold text-slate-900">{log.eventType}</span>
                      {log.performedByName && (
                        <span className="text-slate-500 font-medium">by {log.performedByName}</span>
                      )}
                    </div>
                    <p className="text-slate-600">{log.description}</p>
                  </div>
                  <span className="text-slate-400 font-mono whitespace-nowrap text-[11px]">
                    {log.timestamp ? new Date(log.timestamp).toLocaleString() : '—'}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right Column: Resource Snapshot & Escalation Info (1 Col) */}
        <div className="space-y-6">
          {/* Target Resource Card */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
            <div className="flex items-center gap-2 text-slate-900 font-bold text-sm border-b border-slate-100 pb-3">
              <FileText className="w-4 h-4 text-blue-600" />
              <span>Related Entity Context</span>
            </div>

            <div className="space-y-2.5 text-xs">
              <div className="flex justify-between py-1 border-b border-slate-100">
                <span className="text-slate-500">Resource Type:</span>
                <strong className="text-slate-800">{workflow.relatedResourceType}</strong>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-100">
                <span className="text-slate-500">Resource ID:</span>
                <strong className="text-slate-800">#{workflow.relatedResourceId}</strong>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-100">
                <span className="text-slate-500">Workflow Blueprint:</span>
                <strong className="text-slate-800">{workflow.workflowDefinitionName}</strong>
              </div>
            </div>

            {parsedMetadata && (
              <div className="space-y-1.5 pt-2">
                <span className="text-xs font-semibold text-slate-700 block">Payload Metadata:</span>
                <div className="bg-slate-50 p-3 rounded-xl border border-slate-200 font-mono text-[11px] text-slate-700 space-y-1 overflow-x-auto">
                  {Object.entries(parsedMetadata).map(([k, v]) => (
                    <div key={k} className="flex justify-between gap-2">
                      <span className="text-slate-500">{k}:</span>
                      <span className="font-semibold text-slate-900">{String(v)}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Escalations History Card */}
          {workflow.escalations && workflow.escalations.length > 0 && (
            <div className="bg-purple-50/40 p-6 rounded-2xl border border-purple-200 shadow-xs space-y-4">
              <div className="flex items-center gap-2 text-purple-900 font-bold text-sm border-b border-purple-200 pb-3">
                <ShieldAlert className="w-4 h-4 text-purple-600" />
                <span>Escalation History</span>
              </div>

              <div className="space-y-3">
                {workflow.escalations.map((esc) => (
                  <div key={esc.id} className="p-3 bg-white rounded-xl border border-purple-200 text-xs space-y-1">
                    <div className="flex justify-between font-bold text-purple-900">
                      <span>Level {esc.escalationLevel} Escalation</span>
                      <span className="text-[11px] text-slate-400">
                        {esc.escalatedAt ? new Date(esc.escalatedAt).toLocaleDateString() : ''}
                      </span>
                    </div>
                    <p className="text-slate-700">{esc.reason}</p>
                    <div className="text-[11px] text-purple-700 font-medium">
                      From: {esc.escalatedFromRole} → To: {esc.escalatedToRole}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Approve Modal */}
      <Modal
        isOpen={approveModalOpen}
        onClose={() => !actionLoading && setApproveModalOpen(false)}
        title={`Approve Step: ${activeTask?.stepName || ''}`}
      >
        <div className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1.5">
              Approval Comments (Optional)
            </label>
            <textarea
              rows={3}
              value={comments}
              onChange={(e) => setComments(e.target.value)}
              placeholder="e.g. All requirements verified and approved."
              className="w-full p-3 rounded-xl border border-slate-200 text-sm focus:outline-hidden focus:ring-2 focus:ring-emerald-500"
            />
          </div>

          <div className="flex justify-end gap-2.5 pt-2">
            <button
              onClick={() => setApproveModalOpen(false)}
              disabled={actionLoading}
              className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-xl"
            >
              Cancel
            </button>
            <button
              onClick={handleApprove}
              disabled={actionLoading}
              className="px-4 py-2 text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl flex items-center gap-1.5"
            >
              <CheckCircle2 className="w-4 h-4" />
              <span>{actionLoading ? 'Approving...' : 'Confirm Approval'}</span>
            </button>
          </div>
        </div>
      </Modal>

      {/* Reject Modal */}
      <Modal
        isOpen={rejectModalOpen}
        onClose={() => !actionLoading && setRejectModalOpen(false)}
        title={`Reject Step: ${activeTask?.stepName || ''}`}
      >
        <div className="space-y-4">
          <div className="p-3.5 rounded-xl bg-rose-50 border border-rose-200 text-xs text-rose-800">
            Rejection will terminate this workflow and flag the resource as rejected.
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1.5">
              Rejection Reason <span className="text-rose-500">*</span>
            </label>
            <textarea
              rows={3}
              value={rejectionReason}
              onChange={(e) => setRejectionReason(e.target.value)}
              placeholder="Provide reason for declining this request..."
              className="w-full p-3 rounded-xl border border-rose-300 text-sm focus:outline-hidden focus:ring-2 focus:ring-rose-500"
              required
            />
          </div>

          <div className="flex justify-end gap-2.5 pt-2">
            <button
              onClick={() => setRejectModalOpen(false)}
              disabled={actionLoading}
              className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-xl"
            >
              Cancel
            </button>
            <button
              onClick={handleReject}
              disabled={actionLoading || !rejectionReason.trim()}
              className="px-4 py-2 text-xs font-semibold text-white bg-rose-600 hover:bg-rose-700 rounded-xl flex items-center gap-1.5"
            >
              <XCircle className="w-4 h-4" />
              <span>{actionLoading ? 'Rejecting...' : 'Confirm Rejection'}</span>
            </button>
          </div>
        </div>
      </Modal>

      {/* Escalate Modal */}
      <Modal
        isOpen={escalateModalOpen}
        onClose={() => !actionLoading && setEscalateModalOpen(false)}
        title="Escalate Approval Task"
      >
        <div className="space-y-4">
          <div className="p-3.5 rounded-xl bg-purple-50 border border-purple-200 text-xs text-purple-800">
            Escalating will transfer responsibility to Administrator and broadcast high-priority alerts.
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1.5">
              Escalation Reason <span className="text-purple-600">*</span>
            </label>
            <textarea
              rows={3}
              value={escalationReason}
              onChange={(e) => setEscationReason ? setEscalationReason(e.target.value) : setEscalationReason(e.target.value)}
              placeholder="e.g. Reviewer unavailable; SLA deadline approaching fast."
              className="w-full p-3 rounded-xl border border-purple-300 text-sm focus:outline-hidden focus:ring-2 focus:ring-purple-500"
              required
            />
          </div>

          <div className="flex justify-end gap-2.5 pt-2">
            <button
              onClick={() => setEscalateModalOpen(false)}
              disabled={actionLoading}
              className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-xl"
            >
              Cancel
            </button>
            <button
              onClick={handleEscalate}
              disabled={actionLoading || !escalationReason.trim()}
              className="px-4 py-2 text-xs font-semibold text-white bg-purple-600 hover:bg-purple-700 rounded-xl flex items-center gap-1.5"
            >
              <ShieldAlert className="w-4 h-4" />
              <span>{actionLoading ? 'Escalating...' : 'Confirm Escalation'}</span>
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default WorkflowDetails;
