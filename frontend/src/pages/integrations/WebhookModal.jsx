import React, { useState, useEffect } from 'react';
import Modal from '../../components/common/Modal';
import { Button } from '../../components/common/Button';
import { Webhook, Shield, AlertTriangle, Radio } from 'lucide-react';
import integrationService from '../../services/integration.service';

const EVENT_TYPES = [
  { id: 'SUPPLIER_CREATED', label: 'Supplier Created', desc: 'Triggered when a new supplier is registered' },
  { id: 'SUPPLIER_UPDATED', label: 'Supplier Updated', desc: 'Triggered when supplier profile or metadata is modified' },
  { id: 'SUPPLIER_RATING_UPDATED', label: 'Rating Updated', desc: 'Triggered when ratings or KPI grades are recalculated' },
  { id: 'EVALUATION_COMPLETED', label: 'Evaluation Completed', desc: 'Triggered when an evaluation scorecard is finalized' },
  { id: 'HIGH_RISK_SUPPLIER_DETECTED', label: 'High Risk Detected', desc: 'Triggered when supplier rating falls into high risk territory' },
  { id: 'IMPROVEMENT_ACTION_CREATED', label: 'CAP Created', desc: 'Triggered when a corrective action plan is assigned' },
  { id: 'WORKFLOW_COMPLETED', label: 'Workflow Completed', desc: 'Triggered when an approval chain finishes' }
];

export const WebhookModal = ({ isOpen, onClose, editingWebhook, onSaved }) => {
  const [name, setName] = useState('');
  const [targetUrl, setTargetUrl] = useState('');
  const [selectedEvents, setSelectedEvents] = useState(['SUPPLIER_CREATED', 'SUPPLIER_UPDATED', 'EVALUATION_COMPLETED']);
  const [secretToken, setSecretToken] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (editingWebhook) {
      setName(editingWebhook.name || '');
      setTargetUrl(editingWebhook.targetUrl || '');
      setSelectedEvents(editingWebhook.eventTypes || ['SUPPLIER_CREATED']);
      setSecretToken(editingWebhook.secretToken || '');
    } else {
      setName('');
      setTargetUrl('');
      setSelectedEvents(['SUPPLIER_CREATED', 'SUPPLIER_UPDATED', 'EVALUATION_COMPLETED']);
      setSecretToken('');
    }
    setError('');
  }, [editingWebhook, isOpen]);

  const handleToggleEvent = (eventId) => {
    if (selectedEvents.includes(eventId)) {
      if (selectedEvents.length === 1) return;
      setSelectedEvents(selectedEvents.filter((e) => e !== eventId));
    } else {
      setSelectedEvents([...selectedEvents, eventId]);
    }
  };

  const handleSelectAll = () => {
    if (selectedEvents.length === EVENT_TYPES.length) {
      setSelectedEvents(['SUPPLIER_CREATED']);
    } else {
      setSelectedEvents(EVENT_TYPES.map((e) => e.id));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name.trim()) {
      setError('Please provide a webhook subscription name.');
      return;
    }
    if (!targetUrl.trim() || !targetUrl.startsWith('http')) {
      setError('Please enter a valid HTTPS / HTTP target URL.');
      return;
    }
    if (selectedEvents.length === 0) {
      setError('Please select at least one event type.');
      return;
    }

    try {
      setLoading(true);
      setError('');

      const payload = {
        name: name.trim(),
        targetUrl: targetUrl.trim(),
        eventTypes: selectedEvents,
        secretToken: secretToken.trim() || null
      };

      if (editingWebhook?.id) {
        await integrationService.updateWebhook(editingWebhook.id, payload);
      } else {
        await integrationService.createWebhook(payload);
      }

      if (onSaved) onSaved();
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to save webhook subscription');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={editingWebhook ? 'Edit Webhook Subscription' : 'Create Webhook Subscription'}
      maxWidth="max-w-xl"
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        {error && (
          <div className="p-3 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-xl flex items-center gap-2">
            <AlertTriangle className="w-4 h-4 shrink-0 text-rose-500" />
            {error}
          </div>
        )}

        <div>
          <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
            Subscription Name <span className="text-rose-500">*</span>
          </label>
          <input
            type="text"
            required
            placeholder="e.g. SAP Real-Time Procurement Receiver"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="w-full px-3.5 py-2 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
            Target Endpoint URL <span className="text-rose-500">*</span>
          </label>
          <input
            type="url"
            required
            placeholder="https://api.your-company.com/webhooks/sprs-receiver"
            value={targetUrl}
            onChange={(e) => setTargetUrl(e.target.value)}
            className="w-full px-3.5 py-2 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 font-mono text-xs outline-none"
          />
          <p className="text-[11px] text-slate-400 mt-1">Must accept HTTP POST requests with JSON payloads.</p>
        </div>

        <div>
          <div className="flex items-center justify-between mb-1.5">
            <label className="text-xs font-semibold text-slate-700 uppercase tracking-wider">
              Subscribed Event Types <span className="text-rose-500">*</span>
            </label>
            <button
              type="button"
              onClick={handleSelectAll}
              className="text-xs text-blue-600 hover:text-blue-700 font-medium"
            >
              {selectedEvents.length === EVENT_TYPES.length ? 'Deselect All' : 'Select All'}
            </button>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 max-h-48 overflow-y-auto pr-1">
            {EVENT_TYPES.map((ev) => {
              const isSelected = selectedEvents.includes(ev.id);
              return (
                <div
                  key={ev.id}
                  onClick={() => handleToggleEvent(ev.id)}
                  className={`p-2.5 rounded-xl border cursor-pointer transition-all ${
                    isSelected
                      ? 'bg-blue-50/70 border-blue-400 text-blue-900 shadow-sm'
                      : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100/70'
                  }`}
                >
                  <div className="flex items-center gap-2">
                    <input
                      type="checkbox"
                      checked={isSelected}
                      onChange={() => {}}
                      className="rounded text-blue-600 focus:ring-blue-500 pointer-events-none"
                    />
                    <span className="text-xs font-semibold">{ev.label}</span>
                  </div>
                  <p className="text-[11px] text-slate-500 mt-1 ml-5 leading-tight">{ev.desc}</p>
                </div>
              );
            })}
          </div>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
            HMAC Secret Token (Optional)
          </label>
          <input
            type="text"
            placeholder="Leave blank to automatically generate a secure secret (whsec_...)"
            value={secretToken}
            onChange={(e) => setSecretToken(e.target.value)}
            className="w-full px-3.5 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 font-mono outline-none"
          />
          <p className="text-[11px] text-slate-400 mt-1">
            Used to compute HMAC-SHA256 signature in the <code className="text-slate-600">X-SPRS-Signature</code> header.
          </p>
        </div>

        <div className="flex justify-end gap-2.5 pt-3 border-t border-slate-100">
          <Button type="button" variant="secondary" onClick={onClose}>
            Cancel
          </Button>
          <Button type="submit" variant="primary" disabled={loading}>
            {loading ? 'Saving...' : editingWebhook ? 'Update Subscription' : 'Create Subscription'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};
export default WebhookModal;
