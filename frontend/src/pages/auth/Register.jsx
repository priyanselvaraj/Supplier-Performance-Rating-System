import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { supplierService } from '../../services/supplier.service';
import { Input } from '../../components/common/Input';
import { Button } from '../../components/common/Button';
import { UserPlus, Check, X, AlertCircle, Sparkles, UserCheck, Shield, Building2, ShieldCheck } from 'lucide-react';

export const Register = () => {
  const navigate = useNavigate();
  const { register } = useAuth();

  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    fullName: '',
    phone: '',
    department: '',
    role: 'manager',
    supplierId: '',
  });

  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const fetchSuppliers = async () => {
      try {
        const res = await supplierService.getAllSuppliers();
        if (res.success && res.data) {
          setSuppliers(res.data);
        }
      } catch (err) {
        console.error('Failed to load suppliers for registration dropdown:', err);
      }
    };
    fetchSuppliers();
  }, []);

  // Password rules validation
  const rules = {
    length: formData.password.length >= 8,
    upper: /[A-Z]/.test(formData.password),
    lower: /[a-z]/.test(formData.password),
    number: /[0-9]/.test(formData.password),
    special: /[@#$%^&+=!._\-]/.test(formData.password),
  };

  const isPasswordValid = Object.values(rules).every(Boolean);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (error) setError('');
  };

  const handleFillPreset = (preset) => {
    setFormData({
      username: preset.username,
      fullName: preset.fullName,
      email: preset.email,
      phone: preset.phone,
      department: preset.department,
      role: preset.role,
      supplierId: preset.supplierId || '',
      password: preset.password,
    });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!isPasswordValid) {
      setError('Please satisfy all password complexity requirements');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const payload = {
        username: formData.username,
        email: formData.email,
        password: formData.password,
        fullName: formData.fullName,
        phone: formData.phone,
        department: formData.department,
        roles: [formData.role],
        supplierId: formData.role === 'supplier' && formData.supplierId ? Number(formData.supplierId) : undefined,
      };

      await register(payload);
      setSuccess('Account registered successfully! Redirecting to login...');
      setTimeout(() => {
        navigate('/login');
      }, 1500);
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Registration failed';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col justify-center py-12 sm:px-6 lg:px-8 bg-slate-900 text-slate-100">
      <div className="sm:mx-auto sm:w-full sm:max-w-md text-center">
        <div className="inline-flex items-center justify-center h-14 w-14 rounded-2xl bg-blue-600 shadow-xl shadow-blue-500/20 mb-4">
          <UserPlus className="h-8 w-8 text-white" />
        </div>
        <h2 className="text-3xl font-extrabold text-white tracking-tight">
          Create SPRS Account
        </h2>
        <p className="mt-2 text-sm text-slate-400">
          Supplier Performance Rating & Vendor Self-Service System
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-xl px-4">
        <div className="bg-white py-8 px-6 shadow-2xl rounded-2xl sm:px-10 text-slate-900 border border-slate-100">
          {error && (
            <div className="mb-5 p-3.5 rounded-xl bg-rose-50 border border-rose-200 flex items-start gap-2.5 text-rose-700 text-sm">
              <AlertCircle className="h-5 w-5 flex-shrink-0 text-rose-500" />
              <span>{error}</span>
            </div>
          )}

          {success && (
            <div className="mb-5 p-3.5 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm font-medium text-center">
              {success}
            </div>
          )}

          {/* Quick Preset 1-Click Buttons */}
          <div className="mb-6 pb-6 border-b border-slate-100">
            <div className="flex items-center gap-2 mb-2.5">
              <Sparkles className="h-4 w-4 text-amber-500" />
              <p className="text-xs font-semibold text-slate-600 uppercase tracking-wider">
                Quick Sample Registration Presets
              </p>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
              <button
                type="button"
                onClick={() =>
                  handleFillPreset({
                    username: 'alex_procure',
                    fullName: 'Alex Carter',
                    email: 'alex.carter@sprsystem.com',
                    phone: '+1 555-0182',
                    department: 'Procurement & Logistics',
                    role: 'manager',
                    password: 'SecurePass@123',
                  })
                }
                className="px-2.5 py-2 text-xs font-medium text-slate-700 bg-slate-50 hover:bg-blue-50 hover:border-blue-300 rounded-lg transition-colors text-left border border-slate-200"
              >
                <div className="font-bold text-blue-600 truncate text-[11px]">Procure Lead</div>
                <div className="text-[10px] text-slate-500 mt-0.5">Manager Role</div>
              </button>

              <button
                type="button"
                onClick={() =>
                  handleFillPreset({
                    username: 'maria_auditor',
                    fullName: 'Maria Garcia',
                    email: 'maria.garcia@sprsystem.com',
                    phone: '+1 555-0193',
                    department: 'Quality Assurance',
                    role: 'manager',
                    password: 'SecurePass@123',
                  })
                }
                className="px-2.5 py-2 text-xs font-medium text-slate-700 bg-slate-50 hover:bg-blue-50 hover:border-blue-300 rounded-lg transition-colors text-left border border-slate-200"
              >
                <div className="font-bold text-indigo-600 truncate text-[11px]">Auditor</div>
                <div className="text-[10px] text-slate-500 mt-0.5">Manager Role</div>
              </button>

              <button
                type="button"
                onClick={() =>
                  handleFillPreset({
                    username: 'sarah_vendor',
                    fullName: 'Sarah Jenkins',
                    email: 'sarah.jenkins@apexmicro.com',
                    phone: '+1 408-555-0144',
                    department: 'Vendor Operations',
                    role: 'supplier',
                    supplierId: suppliers[0]?.id || '1',
                    password: 'SecurePass@123',
                  })
                }
                className="px-2.5 py-2 text-xs font-medium text-slate-700 bg-emerald-50 hover:bg-emerald-100 hover:border-emerald-300 rounded-lg transition-colors text-left border border-emerald-200"
              >
                <div className="font-bold text-emerald-700 truncate text-[11px]">Supplier</div>
                <div className="text-[10px] text-emerald-600 mt-0.5">Vendor Partner</div>
              </button>
            </div>
          </div>

          <form className="space-y-4" onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input
                label="Username"
                name="username"
                value={formData.username}
                onChange={handleChange}
                placeholder="e.g. alex_procure or sarah_vendor"
                required
              />

              <Input
                label="Full Name"
                name="fullName"
                value={formData.fullName}
                onChange={handleChange}
                placeholder="e.g. Sarah Jenkins"
                required
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input
                label="Email Address"
                name="email"
                type="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="e.g. sarah.jenkins@apexmicro.com"
                required
              />

              <Input
                label="Mobile / Phone Number"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                placeholder="e.g. +1 555-0182 or +91 9876543210"
                helperText="Primary contact for system alerts"
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Account Role <span className="text-rose-500">*</span>
                </label>
                <select
                  name="role"
                  value={formData.role}
                  onChange={handleChange}
                  className="w-full px-3.5 py-2 text-sm bg-white border border-slate-300 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="manager">Manager (Evaluator & Reports)</option>
                  <option value="supplier">Supplier (Vendor Self-Service Portal)</option>
                </select>
              </div>

              {formData.role === 'supplier' ? (
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">
                    Linked Supplier Company
                  </label>
                  <select
                    name="supplierId"
                    value={formData.supplierId}
                    onChange={handleChange}
                    className="w-full px-3.5 py-2 text-sm bg-white border border-emerald-300 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-emerald-500 font-medium text-slate-800"
                  >
                    <option value="">-- Associate Vendor Company --</option>
                    {suppliers.map((s) => (
                      <option key={s.id} value={s.id}>
                        {s.supplierCode} - {s.name}
                      </option>
                    ))}
                  </select>
                </div>
              ) : (
                <Input
                  label="Department"
                  name="department"
                  value={formData.department}
                  onChange={handleChange}
                  placeholder="e.g. Procurement & Logistics"
                />
              )}
            </div>

            {formData.role === 'supplier' && (
              <Input
                label="Vendor Department / Division"
                name="department"
                value={formData.department}
                onChange={handleChange}
                placeholder="e.g. Quality Assurance & Order Fulfilment"
              />
            )}

            <Input
              label="Password"
              name="password"
              type="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="••••••••"
              required
            />

            {/* Live Password Complexity Checklist */}
            <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200 text-xs space-y-1.5">
              <p className="font-semibold text-slate-700 mb-1">Password Requirements:</p>
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

            <Button
              type="submit"
              variant="primary"
              className={`w-full py-2.5 text-base font-semibold shadow-md ${
                formData.role === 'supplier'
                  ? 'bg-emerald-600 hover:bg-emerald-700 focus:ring-emerald-500 shadow-emerald-600/20'
                  : 'shadow-blue-600/20'
              }`}
              loading={loading}
              disabled={!isPasswordValid}
            >
              {formData.role === 'supplier' ? 'Create Supplier Portal Account' : 'Create Account'}
            </Button>
          </form>

          <div className="mt-6 text-center text-xs text-slate-500">
            Already have an account?{' '}
            <Link to="/login" className="font-semibold text-blue-600 hover:text-blue-500">
              Sign in
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;
