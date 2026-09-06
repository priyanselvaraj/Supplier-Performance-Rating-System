import React, { useState, useEffect } from 'react';
import supplierPortalService from '../../services/supplierPortal.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { Modal } from '../../components/common/Modal';
import {
  Wrench,
  Clock,
  CheckCircle2,
  AlertTriangle,
  Send,
  UserCheck,
  ShieldCheck,
  MessageSquare
} from 'lucide-react';

export const SupplierImprovementActions = () => {
  const [actions, setActions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedAction, setSelectedAction] = useState(null);
  const [responseNotes, setResponseNotes] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [successMsg, setSuccessMsg] = useState(null);

  const fetchActions = async () => {
    try {
      setLoading(true);
      const res = await supplierPortalService.getImprovementActions();
      if (res.success) {
        setActions(res.data || []);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load improvement actions.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchActions();
  }, []);

  const handleOpenResponseModal = (action) => {
    setSelectedAction(action);
    setResponseNotes('');
  };

  const handleCloseModal = () => {
    setSelectedAction(null);
    setResponseNotes('');
  };

  const handleSubmitResponse = async (e) => {
    e.preventDefault();
    if (!responseNotes.trim() || !selectedAction) return;

    try {
      setSubmitting(true);
      const res = await supplierPortalService.respondToImprovementAction(selectedAction.id, {
        responseNotes: responseNotes.trim(),
      });
      if (res.success) {
        setSuccessMsg('Your progress response was submitted to the procurement coordinator.');
        handleCloseModal();
        fetchActions();
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit action response.');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-emerald-600 border-t-transparent"></div>
      </div>
    );
  }

  const getPriorityBadge = (priority) => {
    switch (priority) {
      case 'CRITICAL':
        return <Badge variant="DANGER">CRITICAL</Badge>;
      case 'HIGH':
        return <Badge variant="WARNING">HIGH</Badge>;
      case 'MEDIUM':
        return <Badge variant="INFO">MEDIUM</Badge>;
      default:
        return <Badge variant="DEFAULT">LOW</Badge>;
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'COMPLETED':
        return <Badge variant="SUCCESS">COMPLETED</Badge>;
      case 'IN_PROGRESS':
        return <Badge variant="INFO">IN PROGRESS</Badge>;
      default:
        return <Badge variant="WARNING">OPEN</Badge>;
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Corrective Action Plans (CAP) & Improvements</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Review quality and operational improvement items assigned by the procurement audit committee and provide progress milestones.
        </p>
      </div>

      {/* Success Banner */}
      {successMsg && (
        <div className="rounded-xl border border-emerald-200 bg-emerald-50 p-4 text-emerald-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="h-5 w-5 text-emerald-600" />
            <span className="text-sm font-medium">{successMsg}</span>
          </div>
          <button onClick={() => setSuccessMsg(null)} className="text-xs font-bold text-emerald-700 hover:text-emerald-900">
            Dismiss
          </button>
        </div>
      )}

      {/* Actions Grid / List */}
      {actions.length === 0 ? (
        <Card className="p-12 text-center text-slate-500 border-slate-200/80">
          <ShieldCheck className="mx-auto h-12 w-12 text-emerald-500 mb-3" />
          <h3 className="text-base font-bold text-slate-700">No Action Plans Assigned</h3>
          <p className="text-xs text-slate-400 mt-1">
            Your vendor performance meets all operational and quality standards without outstanding corrective requirements.
          </p>
        </Card>
      ) : (
        <div className="space-y-4">
          {actions.map((action) => (
            <Card key={action.id} className="p-6 border-slate-200/80 shadow-xs hover:shadow-md transition-shadow">
              <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
                <div className="space-y-2 flex-1">
                  <div className="flex flex-wrap items-center gap-2">
                    <h2 className="text-base font-bold text-slate-900">{action.title}</h2>
                    {getPriorityBadge(action.priority)}
                    {getStatusBadge(action.status)}
                  </div>
                  <p className="text-sm text-slate-600 leading-relaxed">{action.description}</p>
                </div>

                {action.status !== 'COMPLETED' && (
                  <Button
                    onClick={() => handleOpenResponseModal(action)}
                    className="bg-amber-600 hover:bg-amber-700 text-white flex-shrink-0"
                    size="sm"
                  >
                    <Send className="h-3.5 w-3.5 mr-1.5" /> Submit Response
                  </Button>
                )}
              </div>

              {/* Coordinator and Timeline Info */}
              <div className="mt-4 pt-4 border-t border-slate-100 grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs text-slate-500">
                <div className="flex items-center gap-1.5">
                  <Clock className="h-4 w-4 text-slate-400" />
                  <span>Due Date: <strong className="text-slate-700">{action.dueDate || 'Unspecified'}</strong></span>
                </div>
                <div className="flex items-center gap-1.5">
                  <UserCheck className="h-4 w-4 text-slate-400" />
                  <span>Buyer Coordinator: <strong className="text-slate-700">{action.assignedUserName || 'Procurement Team'}</strong></span>
                </div>
                {action.completedAt && (
                  <div className="flex items-center gap-1.5 text-emerald-600">
                    <CheckCircle2 className="h-4 w-4" />
                    <span>Resolved on {new Date(action.completedAt).toLocaleDateString()}</span>
                  </div>
                )}
              </div>

              {/* Progress & Resolution Notes History */}
              {action.resolutionNotes && (
                <div className="mt-4 p-4 rounded-xl bg-slate-50 border border-slate-200/70 text-xs">
                  <p className="font-bold text-slate-700 flex items-center gap-1.5 mb-2">
                    <MessageSquare className="h-3.5 w-3.5 text-slate-500" />
                    Response & Milestone Activity Log:
                  </p>
                  <pre className="whitespace-pre-wrap font-sans text-slate-600 leading-relaxed">
                    {action.resolutionNotes}
                  </pre>
                </div>
              )}
            </Card>
          ))}
        </div>
      )}

      {/* Response Modal */}
      <Modal
        isOpen={!!selectedAction}
        onClose={handleCloseModal}
        title={`Respond to CAP Action: ${selectedAction?.title || ''}`}
      >
        <form onSubmit={handleSubmitResponse} className="space-y-4">
          <p className="text-xs text-slate-600">
            Submit your root-cause analysis, corrective countermeasures implemented, or milestone ETA to the procurement team.
          </p>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
              Action Plan Countermeasure & Progress Notes
            </label>
            <textarea
              rows={5}
              value={responseNotes}
              onChange={(e) => setResponseNotes(e.target.value)}
              required
              placeholder="Detail actions taken, QA test validation outcomes, or updated milestone dates..."
              className="w-full p-3 bg-white border border-slate-200 rounded-xl text-xs text-slate-800 focus:ring-2 focus:ring-emerald-500 focus:outline-hidden"
            />
          </div>

          <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
            <Button variant="secondary" type="button" onClick={handleCloseModal}>
              Cancel
            </Button>
            <Button
              variant="primary"
              type="submit"
              className="bg-emerald-600 hover:bg-emerald-700 text-white"
              disabled={submitting || !responseNotes.trim()}
            >
              {submitting ? 'Submitting...' : 'Send Update'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default SupplierImprovementActions;
