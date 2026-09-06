import React, { useState, useEffect } from 'react';
import supplierPortalService from '../../services/supplierPortal.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { Input } from '../../components/common/Input';
import { Modal } from '../../components/common/Modal';
import {
  Building2,
  Mail,
  Phone,
  Globe,
  MapPin,
  Edit3,
  Clock,
  CheckCircle2,
  XCircle,
  AlertCircle,
  ShieldCheck
} from 'lucide-react';

export const SupplierProfile = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [successMsg, setSuccessMsg] = useState(null);

  const [formData, setFormData] = useState({
    contactPerson: '',
    phone: '',
    email: '',
    address: '',
    website: '',
    city: '',
    state: '',
    country: '',
  });

  const fetchProfile = async () => {
    try {
      setLoading(true);
      const res = await supplierPortalService.getProfile();
      if (res.success) {
        setProfile(res.data);
        setFormData({
          contactPerson: res.data.contactPerson || '',
          phone: res.data.phone || '',
          email: res.data.email || '',
          address: res.data.address || '',
          website: res.data.website || '',
          city: res.data.city || '',
          state: res.data.state || '',
          country: res.data.country || '',
        });
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load profile.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmitUpdate = async (e) => {
    e.preventDefault();
    try {
      setSubmitting(true);
      setError(null);
      const res = await supplierPortalService.submitProfileUpdateRequest(formData);
      if (res.success) {
        setSuccessMsg('Your profile update request was submitted successfully for procurement review.');
        setModalOpen(false);
        fetchProfile();
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit profile update request.');
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
          <h1 className="text-2xl font-bold text-slate-900">Company Profile & Information</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Manage your registered corporate details, contact representatives, and track change approval requests.
          </p>
        </div>
        <Button
          onClick={() => setModalOpen(true)}
          className="bg-emerald-600 hover:bg-emerald-700 text-white"
          disabled={profile?.hasPendingUpdateRequest}
        >
          <Edit3 className="h-4 w-4 mr-2" />
          {profile?.hasPendingUpdateRequest ? 'Update Pending Review' : 'Request Profile Update'}
        </Button>
      </div>

      {/* Success Notification */}
      {successMsg && (
        <div className="rounded-xl border border-emerald-200 bg-emerald-50 p-4 text-emerald-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="h-5 w-5 text-emerald-600" />
            <span className="text-sm font-medium">{successMsg}</span>
          </div>
          <button onClick={() => setSuccessMsg(null)} className="text-emerald-700 hover:text-emerald-900 text-xs font-bold">
            Dismiss
          </button>
        </div>
      )}

      {/* Pending Banner */}
      {profile?.hasPendingUpdateRequest && (
        <div className="rounded-xl border border-amber-200 bg-amber-50 p-4 text-amber-800 flex items-center gap-3">
          <Clock className="h-5 w-5 text-amber-600 flex-shrink-0" />
          <div className="text-sm">
            <p className="font-bold">Profile Update Under Review</p>
            <p className="text-amber-700 mt-0.5">
              You have a pending change request awaiting approval by the procurement compliance team.
            </p>
          </div>
        </div>
      )}

      {/* Main Profile Cards */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column: Summary Card */}
        <Card className="p-6 border-slate-200/80 shadow-xs lg:col-span-1">
          <div className="flex flex-col items-center text-center pb-6 border-b border-slate-100">
            <div className="h-20 w-20 rounded-2xl bg-emerald-100 border border-emerald-200 flex items-center justify-center text-emerald-800 text-2xl font-black shadow-sm mb-3">
              {profile?.name?.charAt(0) || 'S'}
            </div>
            <h2 className="text-lg font-bold text-slate-900">{profile?.name}</h2>
            <p className="text-xs font-mono text-slate-500 mt-0.5">{profile?.supplierCode}</p>
            <div className="mt-3 flex items-center gap-2">
              <Badge variant="SUCCESS">Active Vendor</Badge>
              <Badge variant={profile?.ratingCategory}>{profile?.ratingCategory || 'UNRATED'}</Badge>
            </div>
          </div>

          <div className="pt-5 space-y-4 text-sm">
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Industry Category</p>
              <p className="font-bold text-slate-800 mt-0.5">{profile?.categoryName || 'General Supplier'}</p>
            </div>
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Performance Rating</p>
              <p className="font-bold text-emerald-700 mt-0.5">{profile?.overallRating?.toFixed(1) || 'N/A'} / 100</p>
            </div>
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Total Evaluations</p>
              <p className="font-bold text-slate-800 mt-0.5">{profile?.totalEvaluations || 0} completed</p>
            </div>
          </div>
        </Card>

        {/* Right Column: Contact and Location Details */}
        <Card className="p-6 border-slate-200/80 shadow-xs lg:col-span-2">
          <h3 className="text-base font-bold text-slate-900 mb-5 flex items-center gap-2">
            <Building2 className="h-5 w-5 text-slate-600" />
            Verified Contact & Corporate Information
          </h3>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            <div className="flex items-start gap-3 p-4 rounded-xl bg-slate-50 border border-slate-100">
              <Building2 className="h-5 w-5 text-emerald-600 mt-0.5" />
              <div>
                <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Primary Contact</p>
                <p className="text-sm font-bold text-slate-800 mt-1">{profile?.contactPerson || 'Not Provided'}</p>
              </div>
            </div>

            <div className="flex items-start gap-3 p-4 rounded-xl bg-slate-50 border border-slate-100">
              <Mail className="h-5 w-5 text-emerald-600 mt-0.5" />
              <div>
                <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Official Email</p>
                <p className="text-sm font-bold text-slate-800 mt-1">{profile?.email || 'Not Provided'}</p>
              </div>
            </div>

            <div className="flex items-start gap-3 p-4 rounded-xl bg-slate-50 border border-slate-100">
              <Phone className="h-5 w-5 text-emerald-600 mt-0.5" />
              <div>
                <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Direct Phone</p>
                <p className="text-sm font-bold text-slate-800 mt-1">{profile?.phone || 'Not Provided'}</p>
              </div>
            </div>

            <div className="flex items-start gap-3 p-4 rounded-xl bg-slate-50 border border-slate-100">
              <Globe className="h-5 w-5 text-emerald-600 mt-0.5" />
              <div>
                <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Company Website</p>
                <p className="text-sm font-bold text-slate-800 mt-1">
                  {profile?.website ? (
                    <a href={profile.website} target="_blank" rel="noreferrer" className="text-blue-600 hover:underline">
                      {profile.website}
                    </a>
                  ) : (
                    'Not Provided'
                  )}
                </p>
              </div>
            </div>

            <div className="sm:col-span-2 flex items-start gap-3 p-4 rounded-xl bg-slate-50 border border-slate-100">
              <MapPin className="h-5 w-5 text-emerald-600 mt-0.5" />
              <div>
                <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Registered Address</p>
                <p className="text-sm font-medium text-slate-800 mt-1">
                  {profile?.address || 'No street address'}
                  {profile?.city && `, ${profile.city}`}
                  {profile?.state && `, ${profile.state}`}
                  {profile?.country && `, ${profile.country}`}
                </p>
              </div>
            </div>
          </div>
        </Card>
      </div>

      {/* Change Requests History Table */}
      <Card className="p-6 border-slate-200/80 shadow-xs">
        <h3 className="text-base font-bold text-slate-900 mb-4 flex items-center gap-2">
          <Clock className="h-5 w-5 text-slate-600" />
          Profile Update Request History
        </h3>

        {(!profile?.updateRequests || profile.updateRequests.length === 0) ? (
          <p className="text-sm text-slate-500 py-4 text-center">No profile change requests submitted yet.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="border-b border-slate-200 bg-slate-50/60 text-xs uppercase font-semibold text-slate-500">
                <tr>
                  <th className="py-3 px-4">Request Date</th>
                  <th className="py-3 px-4">Requested Changes</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4">Reviewed At</th>
                  <th className="py-3 px-4">Reviewer Remarks</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {profile.updateRequests.map((req) => (
                  <tr key={req.id} className="hover:bg-slate-50/50">
                    <td className="py-3.5 px-4 font-mono text-xs text-slate-600">
                      {req.createdAt ? new Date(req.createdAt).toLocaleDateString() : 'N/A'}
                    </td>
                    <td className="py-3.5 px-4 text-xs text-slate-700">
                      <p><span className="font-semibold">Contact:</span> {req.contactPerson || 'N/A'}</p>
                      <p><span className="font-semibold">Email:</span> {req.email || 'N/A'}</p>
                      <p><span className="font-semibold">Phone:</span> {req.phone || 'N/A'}</p>
                    </td>
                    <td className="py-3.5 px-4">
                      <Badge
                        variant={
                          req.status === 'APPROVED' ? 'SUCCESS' : req.status === 'REJECTED' ? 'DANGER' : 'WARNING'
                        }
                      >
                        {req.status}
                      </Badge>
                    </td>
                    <td className="py-3.5 px-4 text-xs text-slate-500">
                      {req.reviewedAt ? new Date(req.reviewedAt).toLocaleDateString() : 'Pending'}
                    </td>
                    <td className="py-3.5 px-4 text-xs text-slate-600 italic">
                      {req.reviewerNotes || '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>

      {/* Edit Profile Request Modal */}
      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title="Submit Profile Update Request"
      >
        <form onSubmit={handleSubmitUpdate} className="space-y-4">
          <p className="text-xs text-slate-500">
            Submitted changes will be routed to procurement managers for compliance verification before being applied.
          </p>

          <Input
            label="Contact Person"
            name="contactPerson"
            value={formData.contactPerson}
            onChange={handleInputChange}
            required
          />

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Email Address"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleInputChange}
              required
            />
            <Input
              label="Phone Number"
              name="phone"
              value={formData.phone}
              onChange={handleInputChange}
              required
            />
          </div>

          <Input
            label="Company Website"
            name="website"
            value={formData.website}
            onChange={handleInputChange}
            placeholder="https://..."
          />

          <Input
            label="Street Address"
            name="address"
            value={formData.address}
            onChange={handleInputChange}
          />

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
            <Input
              label="City"
              name="city"
              value={formData.city}
              onChange={handleInputChange}
            />
            <Input
              label="State / Province"
              name="state"
              value={formData.state}
              onChange={handleInputChange}
            />
            <Input
              label="Country"
              name="country"
              value={formData.country}
              onChange={handleInputChange}
            />
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <Button variant="secondary" type="button" onClick={() => setModalOpen(false)}>
              Cancel
            </Button>
            <Button
              variant="primary"
              type="submit"
              className="bg-emerald-600 hover:bg-emerald-700 text-white"
              disabled={submitting}
            >
              {submitting ? 'Submitting...' : 'Submit Request'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default SupplierProfile;
