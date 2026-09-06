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
  Building,
  Shield,
  Check,
  X,
  AlertCircle,
  Save,
  CheckCircle2
} from 'lucide-react';

export const Profile = () => {
  const { user, updateProfile } = useAuth();

  // Profile Details State
  const [profileData, setProfileData] = useState({
    fullName: '',
    email: '',
    phone: '',
    department: '',
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
        department: user.department || '',
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
      setProfileSuccess('Profile details (email & mobile number) updated successfully!');
      setToast({ message: 'Profile updated successfully!', type: 'success' });
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Failed to update profile';
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
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Account Settings &amp; Profile</h1>
        <p className="text-sm text-slate-500 mt-1">
          Manage your personal identity, verified email, mobile contact number, and login security credentials.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* User Identity Card */}
        <Card className="lg:col-span-1 text-center">
          <div className="flex flex-col items-center">
            <div className="h-20 w-20 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center font-bold text-2xl border-2 border-blue-200 mb-3 shadow-inner">
              {(user?.fullName || user?.username || 'U')[0].toUpperCase()}
            </div>
            <h3 className="font-bold text-slate-900 text-lg">{user?.fullName || user?.username}</h3>
            <p className="text-xs text-slate-500 font-mono mt-0.5">@{user?.username}</p>

            <div className="flex flex-wrap justify-center gap-1 mt-3">
              {user?.roles?.map((r, i) => (
                <Badge key={i} variant={r} size="sm" />
              ))}
            </div>

            <div className="w-full mt-6 pt-6 border-t border-slate-100 text-left space-y-3.5 text-xs">
              <div className="flex items-center gap-3 text-slate-600">
                <Mail className="h-4 w-4 text-blue-500 flex-shrink-0" />
                <div className="min-w-0">
                  <span className="text-[10px] text-slate-400 block uppercase font-semibold">Email</span>
                  <span className="font-medium text-slate-800 truncate block">{user?.email}</span>
                </div>
              </div>
              <div className="flex items-center gap-3 text-slate-600">
                <Phone className="h-4 w-4 text-emerald-500 flex-shrink-0" />
                <div>
                  <span className="text-[10px] text-slate-400 block uppercase font-semibold">Mobile Number</span>
                  <span className="font-medium text-slate-800">{user?.phone || 'Not provided'}</span>
                </div>
              </div>
              <div className="flex items-center gap-3 text-slate-600">
                <Building className="h-4 w-4 text-purple-500 flex-shrink-0" />
                <div>
                  <span className="text-[10px] text-slate-400 block uppercase font-semibold">
                    {user?.supplierName ? 'Vendor Organization' : 'Department'}
                  </span>
                  <span className="font-medium text-slate-800">
                    {user?.supplierName || user?.department || 'Corporate Procurement'}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </Card>

        {/* Edit Profile & Mobile/Email Card */}
        <div className="lg:col-span-2 space-y-6">
          <Card
            title="Personal & Contact Details"
            subtitle="Update your full name, email address, mobile number, and department"
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
                  label="Full Name"
                  name="fullName"
                  value={profileData.fullName}
                  onChange={(e) => setProfileData({ ...profileData, fullName: e.target.value })}
                  placeholder="e.g. John Doe"
                  required
                />

                <Input
                  label="Email Address"
                  name="email"
                  type="email"
                  value={profileData.email}
                  onChange={(e) => setProfileData({ ...profileData, email: e.target.value })}
                  placeholder="john@example.com"
                  required
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <Input
                  label="Mobile / Phone Number"
                  name="phone"
                  value={profileData.phone}
                  onChange={(e) => setProfileData({ ...profileData, phone: e.target.value })}
                  placeholder="+1 555-0199 or +91 9876543210"
                  helperText="Primary contact number for alerts"
                />

                <Input
                  label="Department"
                  name="department"
                  value={profileData.department}
                  onChange={(e) => setProfileData({ ...profileData, department: e.target.value })}
                  placeholder="Global Procurement / Operations"
                />
              </div>

              <div className="pt-2 flex justify-end">
                <Button type="submit" variant="primary" icon={Save} loading={profileLoading}>
                  Save Profile Changes
                </Button>
              </div>
            </form>
          </Card>

          {/* Change Password Card */}
          <Card
            title="Change Account Password"
            subtitle="Ensure your account uses a secure password meeting enterprise complexity rules"
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
                <Button type="submit" variant="primary" loading={pwdLoading} disabled={!isPasswordValid}>
                  Update Password
                </Button>
              </div>
            </form>
          </Card>
        </div>
      </div>
    </div>
  );
};
