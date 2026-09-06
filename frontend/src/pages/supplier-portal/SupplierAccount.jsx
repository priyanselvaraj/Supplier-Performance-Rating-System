import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { userService } from '../../services/user.service';
import { Card } from '../../components/common/Card';
import { Input } from '../../components/common/Input';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Toast } from '../../components/common/Toast';
import {
  User,
  Lock,
  Mail,
  Phone,
  Building2,
  ShieldCheck,
  Check,
  X,
  AlertCircle,
  Save,
  CheckCircle2,
  KeyRound
} from 'lucide-react';

export const SupplierAccount = () => {
  const { user, updateProfile } = useAuth();

  // Profile Details State
  const [profileData, setProfileData] = useState({
    fullName: '',
    email: '',
    phone: '',
  });
  const [profileLoading, setProfileLoading] = useState(false);
  const [profileError, setProfileError] = useState('');
  const [profileSuccess, setProfileSuccess] = useState('');

  // Password State
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [pwdLoading, setPwdLoading] = useState(false);
  const [pwdError, setPwdError] = useState('');
  const [pwdSuccess, setPwdSuccess] = useState('');

  const [toast, setToast] = useState({ message: '', type: 'success' });

  useEffect(() => {
    if (user) {
      setProfileData({
        fullName: user.fullName || '',
        email: user.email || '',
        phone: user.phone || '',
      });
    }
  }, [user]);

  const handleProfileSubmit = async (e) => {
    e.preventDefault();
    if (!profileData.fullName || !profileData.email) {
      setProfileError('Full Name and Email Address are required');
      return;
    }

    setProfileLoading(true);
    setProfileError('');
    setProfileSuccess('');

    try {
      await updateProfile(profileData);
      setProfileSuccess('Account contact details updated successfully!');
      setToast({ message: 'Account details updated successfully!', type: 'success' });
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Failed to update account details';
      setProfileError(msg);
    } finally {
      setProfileLoading(false);
    }
  };

  const rules = {
    length: passwordData.newPassword.length >= 8,
    upper: /[A-Z]/.test(passwordData.newPassword),
    lower: /[a-z]/.test(passwordData.newPassword),
    number: /[0-9]/.test(passwordData.newPassword),
    special: /[@#$%^&+=!._\-]/.test(passwordData.newPassword),
  };

  const isPasswordValid = Object.values(rules).every(Boolean);

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    if (!passwordData.currentPassword) {
      setPwdError('Please enter your current password');
      return;
    }

    if (!isPasswordValid) {
      setPwdError('New password must satisfy all complexity requirements');
      return;
    }

    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setPwdError('New password and confirmation do not match');
      return;
    }

    setPwdLoading(true);
    setPwdError('');
    setPwdSuccess('');

    try {
      await userService.changePassword(user.id, {
        currentPassword: passwordData.currentPassword,
        newPassword: passwordData.newPassword,
      });

      setPwdSuccess('Password changed successfully!');
      setToast({ message: 'Password updated successfully!', type: 'success' });
      setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' });
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Failed to change password';
      setPwdError(msg);
    } finally {
      setPwdLoading(false);
    }
  };

  return (
    <div className="space-y-6 max-w-5xl mx-auto">
      {toast.message && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast({ message: '', type: 'success' })}
        />
      )}

      <div>
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Supplier User Account Settings</h1>
        <p className="text-sm text-slate-500 mt-1">
          Manage your personal credentials, contact details, and account security within the Supplier Portal.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* User Identity Card */}
        <Card className="lg:col-span-1 text-center p-6 border-slate-200/80 shadow-xs">
          <div className="flex flex-col items-center">
            <div className="h-20 w-20 rounded-2xl bg-emerald-100 text-emerald-800 flex items-center justify-center font-bold text-2xl border-2 border-emerald-200 mb-3 shadow-xs">
              {(user?.fullName || user?.username || 'S')[0].toUpperCase()}
            </div>
            <h3 className="font-bold text-slate-900 text-lg">{user?.fullName || user?.username}</h3>
            <p className="text-xs text-slate-500 font-mono mt-0.5">@{user?.username}</p>

            <div className="flex flex-wrap justify-center gap-1.5 mt-3">
              <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                Authorized Supplier User
              </span>
            </div>

            <div className="w-full mt-6 pt-6 border-t border-slate-100 text-left space-y-3.5 text-xs">
              <div className="flex items-center gap-3 text-slate-600">
                <Building2 className="h-4 w-4 text-emerald-600 flex-shrink-0" />
                <div className="min-w-0">
                  <span className="text-[10px] text-slate-400 block uppercase font-semibold">Vendor Company</span>
                  <span className="font-bold text-slate-800 truncate block">
                    {user?.supplierName || 'Associated Supplier'}
                  </span>
                </div>
              </div>

              <div className="flex items-center gap-3 text-slate-600">
                <Mail className="h-4 w-4 text-emerald-600 flex-shrink-0" />
                <div className="min-w-0">
                  <span className="text-[10px] text-slate-400 block uppercase font-semibold">Email</span>
                  <span className="font-medium text-slate-800 truncate block">{user?.email}</span>
                </div>
              </div>

              <div className="flex items-center gap-3 text-slate-600">
                <Phone className="h-4 w-4 text-emerald-600 flex-shrink-0" />
                <div>
                  <span className="text-[10px] text-slate-400 block uppercase font-semibold">Contact Phone</span>
                  <span className="font-medium text-slate-800">{user?.phone || 'Not provided'}</span>
                </div>
              </div>
            </div>
          </div>
        </Card>

        {/* Edit Profile & Mobile/Email Card */}
        <div className="lg:col-span-2 space-y-6">
          <Card
            className="p-6 border-slate-200/80 shadow-xs"
            title="Representative Contact Details"
            subtitle="Update your full name, email address, and direct telephone contact"
          >
            {profileError && (
              <div className="mb-4 p-3 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-lg flex items-start gap-2">
                <AlertCircle className="h-4 w-4 flex-shrink-0 text-rose-500 mt-0.5" />
                <span>{profileError}</span>
              </div>
            )}

            {profileSuccess && (
              <div className="mb-4 p-3 bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs rounded-lg flex items-start gap-2">
                <CheckCircle2 className="h-4 w-4 flex-shrink-0 text-emerald-500 mt-0.5" />
                <span>{profileSuccess}</span>
              </div>
            )}

            <form onSubmit={handleProfileSubmit} className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <Input
                  label="Contact Person Full Name"
                  name="fullName"
                  value={profileData.fullName}
                  onChange={(e) => setProfileData({ ...profileData, fullName: e.target.value })}
                  placeholder="e.g. Sarah Jenkins"
                  required
                />

                <Input
                  label="Official Email Address"
                  name="email"
                  type="email"
                  value={profileData.email}
                  onChange={(e) => setProfileData({ ...profileData, email: e.target.value })}
                  placeholder="orders@apexmicro.com"
                  required
                />
              </div>

              <Input
                label="Direct Mobile / Telephone"
                name="phone"
                value={profileData.phone}
                onChange={(e) => setProfileData({ ...profileData, phone: e.target.value })}
                placeholder="+1 408-555-0144"
                helperText="Primary contact number for urgent procurement notifications"
              />

              <div className="pt-2 flex justify-end">
                <Button type="submit" className="bg-emerald-600 hover:bg-emerald-700 text-white" loading={profileLoading}>
                  <Save className="h-4 w-4 mr-2" /> Save Account Details
                </Button>
              </div>
            </form>
          </Card>

          {/* Change Password Card */}
          <Card
            className="p-6 border-slate-200/80 shadow-xs"
            title="Change Account Password"
            subtitle="Ensure your portal account uses a secure password meeting enterprise security rules"
          >
            {pwdError && (
              <div className="mb-4 p-3 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-lg flex items-start gap-2">
                <AlertCircle className="h-4 w-4 flex-shrink-0 text-rose-500 mt-0.5" />
                <span>{pwdError}</span>
              </div>
            )}

            {pwdSuccess && (
              <div className="mb-4 p-3 bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs rounded-lg flex items-start gap-2">
                <CheckCircle2 className="h-4 w-4 flex-shrink-0 text-emerald-500 mt-0.5" />
                <span>{pwdSuccess}</span>
              </div>
            )}

            <form onSubmit={handlePasswordChange} className="space-y-4">
              <Input
                label="Current Password"
                type="password"
                value={passwordData.currentPassword}
                onChange={(e) => setPasswordData({ ...passwordData, currentPassword: e.target.value })}
                placeholder="••••••••"
                required
              />

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <Input
                  label="New Password"
                  type="password"
                  value={passwordData.newPassword}
                  onChange={(e) => setPasswordData({ ...passwordData, newPassword: e.target.value })}
                  placeholder="••••••••"
                  required
                />

                <Input
                  label="Confirm New Password"
                  type="password"
                  value={passwordData.confirmPassword}
                  onChange={(e) => setPasswordData({ ...passwordData, confirmPassword: e.target.value })}
                  placeholder="••••••••"
                  required
                />
              </div>

              {/* Password Checklist */}
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 text-xs space-y-1">
                <p className="font-semibold text-slate-700 mb-1">New Password Requirements:</p>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-1">
                  <div className={`flex items-center gap-1.5 ${rules.length ? 'text-emerald-600 font-medium' : 'text-slate-500'}`}>
                    {rules.length ? <Check className="h-3.5 w-3.5" /> : <X className="h-3.5 w-3.5 text-slate-400" />}
                    Minimum 8 characters
                  </div>
                  <div className={`flex items-center gap-1.5 ${rules.upper ? 'text-emerald-600 font-medium' : 'text-slate-500'}`}>
                    {rules.upper ? <Check className="h-3.5 w-3.5" /> : <X className="h-3.5 w-3.5 text-slate-400" />}
                    At least 1 uppercase (A-Z)
                  </div>
                  <div className={`flex items-center gap-1.5 ${rules.lower ? 'text-emerald-600 font-medium' : 'text-slate-500'}`}>
                    {rules.lower ? <Check className="h-3.5 w-3.5" /> : <X className="h-3.5 w-3.5 text-slate-400" />}
                    At least 1 lowercase (a-z)
                  </div>
                  <div className={`flex items-center gap-1.5 ${rules.number ? 'text-emerald-600 font-medium' : 'text-slate-500'}`}>
                    {rules.number ? <Check className="h-3.5 w-3.5" /> : <X className="h-3.5 w-3.5 text-slate-400" />}
                    At least 1 number (0-9)
                  </div>
                  <div className={`flex items-center gap-1.5 ${rules.special ? 'text-emerald-600 font-medium' : 'text-slate-500'}`}>
                    {rules.special ? <Check className="h-3.5 w-3.5" /> : <X className="h-3.5 w-3.5 text-slate-400" />}
                    At least 1 special symbol (@$!%*?&)
                  </div>
                </div>
              </div>

              <div className="pt-2 flex justify-end">
                <Button
                  type="submit"
                  className="bg-emerald-600 hover:bg-emerald-700 text-white"
                  loading={pwdLoading}
                  disabled={!isPasswordValid}
                >
                  <KeyRound className="h-4 w-4 mr-2" /> Update Password
                </Button>
              </div>
            </form>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default SupplierAccount;
