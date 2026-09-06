import React, { useState, useEffect } from 'react';
import supplierPortalService from '../../services/supplierPortal.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { Input } from '../../components/common/Input';
import { Modal } from '../../components/common/Modal';
import {
  MessageSquare,
  Send,
  Building2,
  UserCheck,
  Clock,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';

export const SupplierCommunications = () => {
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [successMsg, setSuccessMsg] = useState(null);

  const [formData, setFormData] = useState({
    subject: '',
    message: '',
    relatedResourceType: 'GENERAL',
  });

  const fetchCommunications = async () => {
    try {
      setLoading(true);
      const res = await supplierPortalService.getCommunications();
      if (res.success) {
        setMessages(res.data || []);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load messages.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCommunications();
  }, []);

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!formData.subject.trim() || !formData.message.trim()) return;

    try {
      setSubmitting(true);
      const res = await supplierPortalService.sendCommunication(formData);
      if (res.success) {
        setSuccessMsg('Message dispatched to the corporate procurement team.');
        setModalOpen(false);
        setFormData({ subject: '', message: '', relatedResourceType: 'GENERAL' });
        fetchCommunications();
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to send message.');
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

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Procurement Communications & Inquiries</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Direct two-way messaging thread with your assigned corporate procurement managers and auditors.
          </p>
        </div>
        <Button
          onClick={() => setModalOpen(true)}
          className="bg-emerald-600 hover:bg-emerald-700 text-white"
        >
          <Send className="h-4 w-4 mr-2" /> New Message to Buyer
        </Button>
      </div>

      {/* Success Notification */}
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

      {/* Message List */}
      {messages.length === 0 ? (
        <Card className="p-12 text-center text-slate-500 border-slate-200/80">
          <MessageSquare className="mx-auto h-12 w-12 text-slate-400 mb-3" />
          <h3 className="text-base font-bold text-slate-700">No Communications Found</h3>
          <p className="text-xs text-slate-400 mt-1">
            Start a discussion regarding delivery schedules, contract terms, or QA evaluations.
          </p>
          <Button
            onClick={() => setModalOpen(true)}
            variant="secondary"
            className="mt-4"
          >
            Compose Message
          </Button>
        </Card>
      ) : (
        <div className="space-y-4">
          {messages.map((item) => (
            <Card
              key={item.id}
              className={`p-5 border shadow-xs transition-shadow ${
                item.fromSupplier
                  ? 'border-emerald-200 bg-emerald-50/20'
                  : 'border-blue-200 bg-blue-50/20'
              }`}
            >
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-center gap-2.5">
                  <div
                    className={`h-9 w-9 rounded-xl flex items-center justify-center font-bold text-xs ${
                      item.fromSupplier
                        ? 'bg-emerald-100 text-emerald-800 border border-emerald-300'
                        : 'bg-blue-100 text-blue-800 border border-blue-300'
                    }`}
                  >
                    {item.fromSupplier ? <Building2 className="h-4 w-4" /> : <UserCheck className="h-4 w-4" />}
                  </div>
                  <div>
                    <h3 className="text-sm font-bold text-slate-900">{item.subject}</h3>
                    <p className="text-xs text-slate-500 flex items-center gap-1.5 mt-0.5">
                      <span>Sender: <strong className="text-slate-700">{item.senderName}</strong></span>
                      <span>•</span>
                      <span className="font-medium text-slate-400">
                        {item.fromSupplier ? 'Vendor Account' : 'Corporate Procurement'}
                      </span>
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2 text-xs text-slate-400">
                  <Clock className="h-3.5 w-3.5" />
                  <span>{item.createdAt ? new Date(item.createdAt).toLocaleString() : 'N/A'}</span>
                </div>
              </div>

              <div className="mt-3.5 pt-3 border-t border-slate-200/60 text-xs text-slate-800 leading-relaxed whitespace-pre-wrap">
                {item.message}
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Compose Modal */}
      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title="Send Inquiry to Corporate Procurement Team"
      >
        <form onSubmit={handleSendMessage} className="space-y-4">
          <Input
            label="Subject"
            required
            value={formData.subject}
            onChange={(e) => setFormData({ ...formData, subject: e.target.value })}
            placeholder="e.g., Q2 Production Capacity Allocation"
          />

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
              Related Topic / Department
            </label>
            <select
              value={formData.relatedResourceType}
              onChange={(e) => setFormData({ ...formData, relatedResourceType: e.target.value })}
              className="w-full p-2.5 bg-white border border-slate-200 rounded-xl text-xs text-slate-800 focus:ring-2 focus:ring-emerald-500 focus:outline-hidden"
            >
              <option value="GENERAL">General Procurement Inquiry</option>
              <option value="EVALUATION">Quality Audit & Performance Scorecard</option>
              <option value="IMPROVEMENT_ACTION">Corrective Action Plan (CAP)</option>
              <option value="CONTRACT">Contract & Pricing Negotiation</option>
              <option value="LOGISTICS">Delivery Schedules & ASN Shipment</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
              Message Content
            </label>
            <textarea
              rows={5}
              required
              value={formData.message}
              onChange={(e) => setFormData({ ...formData, message: e.target.value })}
              placeholder="Describe your inquiry, feedback, or update..."
              className="w-full p-3 bg-white border border-slate-200 rounded-xl text-xs text-slate-800 focus:ring-2 focus:ring-emerald-500 focus:outline-hidden"
            />
          </div>

          <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
            <Button variant="secondary" type="button" onClick={() => setModalOpen(false)}>
              Cancel
            </Button>
            <Button
              variant="primary"
              type="submit"
              className="bg-emerald-600 hover:bg-emerald-700 text-white"
              disabled={submitting}
            >
              {submitting ? 'Sending...' : 'Send Message'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default SupplierCommunications;
