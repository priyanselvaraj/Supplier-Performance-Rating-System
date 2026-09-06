import React, { useState, useEffect } from 'react';
import supplierPortalService from '../../services/supplierPortal.service';
import { supplierService } from '../../services/supplier.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { Modal } from '../../components/common/Modal';
import { Input } from '../../components/common/Input';
import {
  ShieldCheck,
  UserPlus,
  Clock,
  CheckCircle2,
  XCircle,
  AlertCircle,
  Building2,
  Mail,
  Phone,
  User
} from 'lucide-react';

export const SupplierPortalAdmin = () => {
  const [requests, setRequests] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMsg, setSuccessMsg] = useState(null);

  // User Provisioning Modal
  const [userModalOpen, setUserModalOpen] = useState(false);
  const [userSubmitting, setUserSubmitting] = useState(false);
  const [userForm, setUserForm] = useState({
    supplierId: '',
    username: '',
    email: '',
    password: '',
    fullName: '',
    phone: '',
  });

  // Review Modal
  const [reviewModalOpen, setReviewModalOpen] = useState(false);
  const [selectedReq, setSelectedReq] = useState(null);
  const [reviewAction, setReviewAction] = useState('APPROVE'); // 'APPROVE' | 'REJECT'
  const [reviewerNotes, setReviewerNotes] = useState('');
  const [reviewSubmitting, setReviewSubmitting] = useState(false);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [reqRes, supRes] = await Promise.all([
        supplierPortalService.getPendingProfileRequests(),
        supplierService.getAllSuppliers(),
      ]);

      if (reqRes.success) setRequests(reqRes.data || []);
      if (supRes.success) setSuppliers(supRes.data || []);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load supplier portal administrative data.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCreateUser = async (e) => {
    e.preventDefault();
    if (!userForm.supplierId || !userForm.username || !userForm.email || !userForm.password) {
      setError('Please fill in all mandatory fields.');
      return;
    }

    try {
      setUserSubmitting(true);
      setError(null);
      const res = await supplierPortalService.createSupplierUser(userForm);
      if (res.success) {
        setSuccessMsg(`Supplier portal account "${userForm.username}" created successfully!`);
        setUserModalOpen(false);
        setUserForm({ supplierId: '', username: '', email: '', password: '', fullName: '', phone: '' });
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create supplier user account.');
    } finally {
      setUserSubmitting(false);
    }
  };

  const handleOpenReviewModal = (req, action) => {
    setSelectedReq(req);
    setReviewAction(action);
    setReviewerNotes(action === 'APPROVE' ? 'Approved - Verified company registration records.' : '');
    setReviewModalOpen(true);
  };

  const handleSubmitReview = async (e) => {
    e.preventDefault();
    if (!selectedReq) return;

    try {
      setReviewSubmitting(true);
      const approved = reviewAction === 'APPROVE';
      const res = await supplierPortalService.reviewProfileRequest(selectedReq.id, {
        approved,
        reviewerNotes: reviewerNotes.trim(),
      });
      if (res.success) {
        setSuccessMsg(`Profile update request ${approved ? 'approved and applied' : 'rejected'}.`);
        setReviewModalOpen(false);
        setSelectedReq(null);
        fetchData();
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to process request review.');
    } finally {
      setReviewSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-600 border-t-transparent"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Supplier Portal Administration</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Provision vendor self-service accounts and review company profile update submissions.
          </p>
        </div>
        <Button
          onClick={() => setUserModalOpen(true)}
          className="bg-blue-600 hover:bg-blue-700 text-white"
        >
          <UserPlus className="h-4 w-4 mr-2" /> Provision Vendor Account
        </Button>
      </div>

      {/* Notifications */}
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

      {error && (
        <div className="rounded-xl border border-rose-200 bg-rose-50 p-4 text-rose-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <AlertCircle className="h-5 w-5 text-rose-600" />
            <span className="text-sm font-medium">{error}</span>
          </div>
          <button onClick={() => setError(null)} className="text-xs font-bold text-rose-700 hover:text-rose-900">
            Dismiss
          </button>
        </div>
      )}

      {/* Pending Requests Section */}
      <Card className="p-6 border-slate-200/80 shadow-xs">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
            <Clock className="h-5 w-5 text-amber-600" />
            Pending Profile Change Requests ({requests.length})
          </h2>
        </div>

        {requests.length === 0 ? (
          <p className="text-sm text-slate-500 py-6 text-center">No pending profile update requests from suppliers.</p>
        ) : (
          <div className="space-y-4">
            {requests.map((req) => (
              <div key={req.id} className="p-4 rounded-xl border border-slate-200 bg-slate-50/50 flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-bold text-slate-900">{req.supplierName}</span>
                    <Badge variant="WARNING">PENDING REVIEW</Badge>
                  </div>
                  <p className="text-xs text-slate-500">
                    Requested by <strong className="text-slate-700">{req.requestedByUsername}</strong> on {req.createdAt ? new Date(req.createdAt).toLocaleString() : 'N/A'}
                  </p>
                  <div className="mt-2 grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs text-slate-700 bg-white p-3 rounded-lg border border-slate-200/60">
                    <div><strong>Contact:</strong> {req.contactPerson || '—'}</div>
                    <div><strong>Email:</strong> {req.email || '—'}</div>
                    <div><strong>Phone:</strong> {req.phone || '—'}</div>
                    <div><strong>Address:</strong> {req.address ? `${req.address}, ${req.city || ''} ${req.country || ''}` : '—'}</div>
                  </div>
                </div>

                <div className="flex items-center gap-2 flex-shrink-0">
                  <Button
                    size="sm"
                    className="bg-emerald-600 hover:bg-emerald-700 text-white"
                    onClick={() => handleOpenReviewModal(req, 'APPROVE')}
                  >
                    <CheckCircle2 className="h-4 w-4 mr-1.5" /> Approve
                  </Button>
                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => handleOpenReviewModal(req, 'REJECT')}
                  >
                    <XCircle className="h-4 w-4 mr-1.5" /> Reject
                  </Button>
                </div>
              </div>
            ))}
          </div>
        )}
      </Card>

      {/* Create Supplier User Modal */}
      <Modal
        isOpen={userModalOpen}
        onClose={() => setUserModalOpen(false)}
        title="Provision Supplier Portal Login Account"
      >
        <form onSubmit={handleCreateUser} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
              Select Supplier Company *
            </label>
            <select
              required
              value={userForm.supplierId}
              onChange={(e) => {
                const sId = e.target.value;
                const found = suppliers.find((s) => String(s.id) === String(sId));
                setUserForm({
                  ...userForm,
                  supplierId: sId,
                  email: found?.email || userForm.email,
                  fullName: found?.contactPerson || userForm.fullName,
                  phone: found?.phone || userForm.phone,
                });
              }}
              className="w-full p-2.5 bg-white border border-slate-200 rounded-xl text-xs text-slate-800 focus:ring-2 focus:ring-blue-500 focus:outline-hidden"
            >
              <option value="">-- Choose Supplier --</option>
              {suppliers.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.supplierCode} - {s.name}
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <Input
              label="Username *"
              required
              value={userForm.username}
              onChange={(e) => setUserForm({ ...userForm, username: e.target.value })}
              placeholder="e.g., supplier_apex"
            />
            <Input
              label="Password *"
              type="password"
              required
              value={userForm.password}
              onChange={(e) => setUserForm({ ...userForm, password: e.target.value })}
              placeholder="Min 6 characters"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <Input
              label="Full Name *"
              required
              value={userForm.fullName}
              onChange={(e) => setUserForm({ ...userForm, fullName: e.target.value })}
              placeholder="Contact Person Name"
            />
            <Input
              label="Official Email *"
              type="email"
              required
              value={userForm.email}
              onChange={(e) => setUserForm({ ...userForm, email: e.target.value })}
              placeholder="contact@supplier.com"
            />
          </div>

          <Input
            label="Phone Number"
            value={userForm.phone}
            onChange={(e) => setUserForm({ ...userForm, phone: e.target.value })}
            placeholder="+1 555-0100"
          />

          <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
            <Button variant="secondary" type="button" onClick={() => setUserModalOpen(false)}>
              Cancel
            </Button>
            <Button
              variant="primary"
              type="submit"
              className="bg-blue-600 hover:bg-blue-700 text-white"
              disabled={userSubmitting}
            >
              {userSubmitting ? 'Creating Account...' : 'Provision Account'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Review Modal */}
      <Modal
        isOpen={reviewModalOpen}
        onClose={() => setReviewModalOpen(false)}
        title={`${reviewAction === 'APPROVE' ? 'Approve' : 'Reject'} Profile Update Request`}
      >
        <form onSubmit={handleSubmitReview} className="space-y-4">
          <p className="text-xs text-slate-600">
            {reviewAction === 'APPROVE'
              ? `Approving this request will immediately update the official profile of "${selectedReq?.supplierName}".`
              : `Rejecting will leave the supplier's existing details unchanged and send your feedback remarks.`}
          </p>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
              Reviewer Notes / Feedback
            </label>
            <textarea
              rows={3}
              value={reviewerNotes}
              onChange={(e) => setReviewerNotes(e.target.value)}
              placeholder="Add explanation or audit verification notes..."
              className="w-full p-2.5 bg-white border border-slate-200 rounded-xl text-xs text-slate-800 focus:ring-2 focus:ring-blue-500 focus:outline-hidden"
            />
          </div>

          <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
            <Button variant="secondary" type="button" onClick={() => setReviewModalOpen(false)}>
              Cancel
            </Button>
            <Button
              variant={reviewAction === 'APPROVE' ? 'primary' : 'danger'}
              type="submit"
              className={reviewAction === 'APPROVE' ? 'bg-emerald-600 hover:bg-emerald-700 text-white' : ''}
              disabled={reviewSubmitting}
            >
              {reviewSubmitting ? 'Processing...' : `Confirm ${reviewAction === 'APPROVE' ? 'Approval' : 'Rejection'}`}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default SupplierPortalAdmin;
