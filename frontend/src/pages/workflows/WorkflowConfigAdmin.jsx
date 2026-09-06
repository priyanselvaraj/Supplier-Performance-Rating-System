import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { workflowService } from '../../services/workflow.service';
import {
  SlidersHorizontal,
  Plus,
  Edit2,
  Trash2,
  ToggleLeft,
  ToggleRight,
  CheckCircle2,
  AlertTriangle,
  ArrowLeft,
  Layers,
  Clock,
  ShieldAlert,
  Save,
  PlusCircle,
  X,
  RefreshCw,
  GitBranch
} from 'lucide-react';
import Modal from '../../components/common/Modal';

export const WorkflowConfigAdmin = () => {
  const { isAdmin } = useAuth();

  const [definitions, setDefinitions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [feedbackMsg, setFeedbackMsg] = useState({ type: '', text: '' });

  // Modal State for Create / Edit
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    workflowType: 'SUPPLIER_PROFILE_UPDATE',
    description: '',
    active: true,
    steps: [
      {
        stepOrder: 1,
        stepName: 'Procurement Manager Review',
        requiredRole: 'ROLE_MANAGER',
        slaHours: 24,
        escalationRole: 'ROLE_ADMIN',
        autoEscalate: true,
        instructions: ''
      }
    ]
  });

  const fetchDefinitions = useCallback(async () => {
    setLoading(true);
    try {
      const res = await workflowService.getWorkflowDefinitions();
      if (res.success && res.data) {
        setDefinitions(res.data || []);
      }
    } catch (err) {
      console.error('Failed to fetch workflow definitions:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchDefinitions();
  }, [fetchDefinitions]);

  const handleToggleActive = async (id, e) => {
    e.stopPropagation();
    try {
      const res = await workflowService.toggleWorkflowDefinition(id);
      if (res.success) {
        setDefinitions(prev =>
          prev.map(d => (d.id === id ? { ...d, active: !d.active } : d))
        );
        setFeedbackMsg({
          type: 'success',
          text: `Workflow definition status updated.`
        });
        setTimeout(() => setFeedbackMsg({ type: '', text: '' }), 4000);
      }
    } catch (err) {
      console.error('Failed to toggle workflow definition:', err);
    }
  };

  const openCreateModal = () => {
    setEditingId(null);
    setFormData({
      name: '',
      workflowType: 'SUPPLIER_STATUS_CHANGE',
      description: '',
      active: true,
      steps: [
        {
          stepOrder: 1,
          stepName: 'Initial Review',
          requiredRole: 'ROLE_MANAGER',
          slaHours: 24,
          escalationRole: 'ROLE_ADMIN',
          autoEscalate: true,
          instructions: 'Perform primary assessment.'
        }
      ]
    });
    setIsModalOpen(true);
  };

  const openEditModal = (def, e) => {
    e.stopPropagation();
    setEditingId(def.id);
    setFormData({
      name: def.name || '',
      workflowType: def.workflowType,
      description: def.description || '',
      active: def.active,
      steps: def.steps && def.steps.length > 0
        ? def.steps.map(s => ({
            stepOrder: s.stepOrder,
            stepName: s.stepName,
            requiredRole: s.requiredRole,
            slaHours: s.slaHours,
            escalationRole: s.escalationRole || 'ROLE_ADMIN',
            autoEscalate: s.autoEscalate !== false,
            instructions: s.instructions || ''
          }))
        : [
            {
              stepOrder: 1,
              stepName: 'Step 1',
              requiredRole: 'ROLE_MANAGER',
              slaHours: 24,
              escalationRole: 'ROLE_ADMIN',
              autoEscalate: true,
              instructions: ''
            }
          ]
    });
    setIsModalOpen(true);
  };

  const handleAddStep = () => {
    setFormData(prev => ({
      ...prev,
      steps: [
        ...prev.steps,
        {
          stepOrder: prev.steps.length + 1,
          stepName: `Step ${prev.steps.length + 1}`,
          requiredRole: 'ROLE_ADMIN',
          slaHours: 24,
          escalationRole: 'ROLE_ADMIN',
          autoEscalate: true,
          instructions: ''
        }
      ]
    }));
  };

  const handleRemoveStep = (index) => {
    if (formData.steps.length <= 1) {
      alert('A workflow blueprint must have at least one step.');
      return;
    }
    const updated = formData.steps
      .filter((_, i) => i !== index)
      .map((step, idx) => ({ ...step, stepOrder: idx + 1 }));
    setFormData(prev => ({ ...prev, steps: updated }));
  };

  const handleStepChange = (index, field, value) => {
    const updated = [...formData.steps];
    updated[index] = { ...updated[index], [field]: value };
    setFormData(prev => ({ ...prev, steps: updated }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.name.trim()) {
      alert('Workflow name is required.');
      return;
    }
    setActionLoading(true);
    try {
      if (editingId) {
        await workflowService.updateWorkflowDefinition(editingId, formData);
        setFeedbackMsg({ type: 'success', text: 'Workflow blueprint updated successfully.' });
      } else {
        await workflowService.createWorkflowDefinition(formData);
        setFeedbackMsg({ type: 'success', text: 'New workflow blueprint created successfully.' });
      }
      setIsModalOpen(false);
      fetchDefinitions();
      setTimeout(() => setFeedbackMsg({ type: '', text: '' }), 4000);
    } catch (err) {
      console.error('Save failed:', err);
      setFeedbackMsg({
        type: 'error',
        text: err.response?.data?.message || 'Failed to save workflow definition.'
      });
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div className="space-y-6 pb-12">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-6 rounded-2xl border border-slate-200 shadow-xs">
        <div>
          <div className="flex items-center gap-2 text-xs font-semibold text-blue-600 uppercase tracking-wider mb-1">
            <SlidersHorizontal className="w-4 h-4" />
            <span>Workflow Administration</span>
          </div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
            Workflow Definitions & SLA Rules
          </h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Configure multi-stage approval hierarchies, required approver roles, SLA time limits, and auto-escalations.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Link
            to="/workflows"
            className="px-4 py-2.5 rounded-xl border border-slate-200 text-xs font-semibold text-slate-700 hover:bg-slate-50 transition-all"
          >
            ← Workflow Dashboard
          </Link>
          <button
            onClick={openCreateModal}
            className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-blue-600 text-white text-xs font-semibold hover:bg-blue-700 shadow-sm transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>New Definition</span>
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
              <AlertTriangle className="w-4 h-4 text-rose-600" />
            )}
            <span>{feedbackMsg.text}</span>
          </div>
          <button onClick={() => setFeedbackMsg({ type: '', text: '' })} className="font-bold">✕</button>
        </div>
      )}

      {/* Definitions List */}
      <div className="space-y-4">
        {loading ? (
          <div className="bg-white p-12 rounded-2xl border border-slate-200 text-center">
            <RefreshCw className="w-6 h-6 animate-spin text-blue-600 mx-auto mb-2" />
            <p className="text-sm font-medium text-slate-500">Loading workflow blueprints...</p>
          </div>
        ) : definitions.length === 0 ? (
          <div className="bg-white p-16 rounded-2xl border border-slate-200 text-center">
            <Layers className="w-10 h-10 text-slate-400 mx-auto mb-2" />
            <p className="font-bold text-slate-800">No workflow definitions configured.</p>
          </div>
        ) : (
          definitions.map((def) => (
            <div
              key={def.id}
              className={`bg-white p-6 rounded-2xl border transition-all ${
                def.active ? 'border-slate-200 shadow-xs' : 'border-slate-200/60 bg-slate-50/40 opacity-75'
              }`}
            >
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-100 pb-4">
                <div className="space-y-1">
                  <div className="flex items-center gap-3">
                    <h3 className="text-base font-bold text-slate-900">{def.name}</h3>
                    <span className="px-2 py-0.5 rounded text-xs font-semibold bg-blue-50 text-blue-700 border border-blue-100">
                      {def.workflowType}
                    </span>
                    {def.active ? (
                      <span className="px-2 py-0.5 text-[11px] font-bold text-emerald-700 bg-emerald-50 rounded-full">
                        Active
                      </span>
                    ) : (
                      <span className="px-2 py-0.5 text-[11px] font-medium text-slate-500 bg-slate-100 rounded-full">
                        Inactive
                      </span>
                    )}
                  </div>
                  <p className="text-xs text-slate-500">{def.description}</p>
                </div>

                <div className="flex items-center gap-2">
                  <button
                    onClick={(e) => handleToggleActive(def.id, e)}
                    className="p-2 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-lg transition-all"
                    title={def.active ? 'Disable workflow' : 'Enable workflow'}
                  >
                    {def.active ? (
                      <ToggleRight className="w-6 h-6 text-emerald-600" />
                    ) : (
                      <ToggleLeft className="w-6 h-6 text-slate-400" />
                    )}
                  </button>

                  <button
                    onClick={(e) => openEditModal(def, e)}
                    className="px-3.5 py-1.5 rounded-lg text-xs font-semibold text-blue-600 bg-blue-50 hover:bg-blue-100 transition-all flex items-center gap-1.5"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                    <span>Edit Blueprint</span>
                  </button>
                </div>
              </div>

              {/* Steps overview */}
              <div className="mt-4 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                {def.steps?.map((step) => (
                  <div
                    key={step.id || step.stepOrder}
                    className="p-3.5 bg-slate-50 rounded-xl border border-slate-200 text-xs space-y-1.5"
                  >
                    <div className="flex items-center justify-between font-bold text-slate-900">
                      <span>Step {step.stepOrder}: {step.stepName}</span>
                    </div>
                    <div className="flex items-center gap-1.5 text-slate-600">
                      <span className="font-semibold text-slate-700">Role:</span>
                      <span className="px-1.5 py-0.5 rounded bg-white border border-slate-200 text-[11px]">
                        {step.requiredRole ? step.requiredRole.replace('ROLE_', '') : ''}
                      </span>
                    </div>
                    <div className="flex items-center justify-between text-slate-500 text-[11px]">
                      <span>SLA: <strong>{step.slaHours}h</strong></span>
                      <span>Escalate: <strong>{step.escalationRole ? step.escalationRole.replace('ROLE_', '') : 'ADMIN'}</strong></span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))
        )}
      </div>

      {/* Modal: Create / Edit Definition */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => !actionLoading && setIsModalOpen(false)}
        title={editingId ? 'Edit Workflow Blueprint' : 'Create Workflow Blueprint'}
        size="lg"
      >
        <form onSubmit={handleSubmit} className="space-y-5">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Workflow Name <span className="text-rose-500">*</span>
              </label>
              <input
                type="text"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                placeholder="e.g. Vendor Profile Modification Review"
                className="w-full p-2.5 rounded-xl border border-slate-200 text-sm focus:outline-hidden focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Workflow Category Type <span className="text-rose-500">*</span>
              </label>
              <select
                value={formData.workflowType}
                onChange={(e) => setFormData({ ...formData, workflowType: e.target.value })}
                disabled={!!editingId}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-sm bg-white focus:outline-hidden focus:ring-2 focus:ring-blue-500 disabled:bg-slate-100"
              >
                <option value="SUPPLIER_PROFILE_UPDATE">SUPPLIER_PROFILE_UPDATE</option>
                <option value="SUPPLIER_DOCUMENT_REVIEW">SUPPLIER_DOCUMENT_REVIEW</option>
                <option value="EVALUATION_APPROVAL">EVALUATION_APPROVAL</option>
                <option value="IMPROVEMENT_ACTION_CLOSURE">IMPROVEMENT_ACTION_CLOSURE</option>
                <option value="SUPPLIER_STATUS_CHANGE">SUPPLIER_STATUS_CHANGE</option>
              </select>
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Description
            </label>
            <textarea
              rows={2}
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              placeholder="Describe the governance process and triggers for this workflow..."
              className="w-full p-2.5 rounded-xl border border-slate-200 text-sm focus:outline-hidden focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Sequential Step Builder */}
          <div className="space-y-3 pt-2 border-t border-slate-200">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold text-slate-800 uppercase tracking-wider">
                Approval Hierarchy Steps ({formData.steps.length})
              </span>
              <button
                type="button"
                onClick={handleAddStep}
                className="inline-flex items-center gap-1 px-3 py-1.5 rounded-lg bg-blue-50 text-blue-700 text-xs font-semibold hover:bg-blue-100 transition-all"
              >
                <PlusCircle className="w-3.5 h-3.5" />
                <span>Add Step</span>
              </button>
            </div>

            <div className="space-y-3 max-h-80 overflow-y-auto pr-1">
              {formData.steps.map((step, idx) => (
                <div
                  key={idx}
                  className="p-4 bg-slate-50 rounded-xl border border-slate-200 space-y-3 relative group"
                >
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-xs text-blue-700">
                      Step {step.stepOrder}
                    </span>
                    {formData.steps.length > 1 && (
                      <button
                        type="button"
                        onClick={() => handleRemoveStep(idx)}
                        className="text-slate-400 hover:text-rose-600 p-1"
                        title="Remove step"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    )}
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                    <div>
                      <label className="block text-[11px] font-semibold text-slate-600 mb-1">Step Name</label>
                      <input
                        type="text"
                        value={step.stepName}
                        onChange={(e) => handleStepChange(idx, 'stepName', e.target.value)}
                        className="w-full p-2 rounded-lg border border-slate-200 text-xs bg-white"
                        required
                      />
                    </div>

                    <div>
                      <label className="block text-[11px] font-semibold text-slate-600 mb-1">Required Role</label>
                      <select
                        value={step.requiredRole}
                        onChange={(e) => handleStepChange(idx, 'requiredRole', e.target.value)}
                        className="w-full p-2 rounded-lg border border-slate-200 text-xs bg-white"
                      >
                        <option value="ROLE_MANAGER">ROLE_MANAGER</option>
                        <option value="ROLE_ADMIN">ROLE_ADMIN</option>
                      </select>
                    </div>

                    <div>
                      <label className="block text-[11px] font-semibold text-slate-600 mb-1">SLA Hours</label>
                      <input
                        type="number"
                        min="1"
                        max="720"
                        value={step.slaHours}
                        onChange={(e) => handleStepChange(idx, 'slaHours', parseInt(e.target.value) || 24)}
                        className="w-full p-2 rounded-lg border border-slate-200 text-xs bg-white"
                        required
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-[11px] font-semibold text-slate-600 mb-1">Instructions for Approvers</label>
                    <input
                      type="text"
                      value={step.instructions}
                      onChange={(e) => handleStepChange(idx, 'instructions', e.target.value)}
                      placeholder="e.g. Inspect uploaded ISO certificate accreditation seal."
                      className="w-full p-2 rounded-lg border border-slate-200 text-xs bg-white"
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="flex justify-end gap-2.5 pt-3 border-t border-slate-200">
            <button
              type="button"
              onClick={() => setIsModalOpen(false)}
              disabled={actionLoading}
              className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-xl"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={actionLoading}
              className="px-5 py-2 text-xs font-semibold text-white bg-blue-600 hover:bg-blue-700 rounded-xl flex items-center gap-1.5 shadow-sm"
            >
              <Save className="w-4 h-4" />
              <span>{actionLoading ? 'Saving...' : 'Save Blueprint'}</span>
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default WorkflowConfigAdmin;
