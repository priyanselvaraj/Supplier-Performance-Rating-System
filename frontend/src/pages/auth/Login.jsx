import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Input } from '../../components/common/Input';
import { Button } from '../../components/common/Button';
import { ShieldCheck, Lock, User, AlertCircle } from 'lucide-react';

export const Login = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  const [formData, setFormData] = useState({ username: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const from = location.state?.from?.pathname || '/dashboard';

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (error) setError('');
  };

  const handleFillDemo = (username, password) => {
    setFormData({ username, password });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.username || !formData.password) {
      setError('Please enter both username/email and password');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const authUser = await login(formData);
      const isSupplier = authUser?.roles?.some(r => r === 'ROLE_SUPPLIER' || r === 'SUPPLIER' || r.toLowerCase() === 'supplier');
      if (isSupplier) {
        navigate('/supplier-portal/dashboard', { replace: true });
      } else {
        navigate(from === '/login' ? '/dashboard' : from, { replace: true });
      }
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Invalid username or password';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col justify-center py-12 sm:px-6 lg:px-8 bg-slate-900 text-slate-100">
      <div className="sm:mx-auto sm:w-full sm:max-w-md text-center">
        <div className="inline-flex items-center justify-center h-14 w-14 rounded-2xl bg-blue-600 shadow-xl shadow-blue-500/20 mb-4">
          <ShieldCheck className="h-8 w-8 text-white" />
        </div>
        <h2 className="text-3xl font-extrabold text-white tracking-tight">
          SPRS Portal Login
        </h2>
        <p className="mt-2 text-sm text-slate-400">
          Supplier Performance Rating & Vendor Self-Service System
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md px-4">
        <div className="bg-white py-8 px-6 shadow-2xl rounded-2xl sm:px-10 text-slate-900 border border-slate-100">
          {error && (
            <div className="mb-5 p-3.5 rounded-xl bg-rose-50 border border-rose-200 flex items-start gap-2.5 text-rose-700 text-sm">
              <AlertCircle className="h-5 w-5 flex-shrink-0 text-rose-500" />
              <span>{error}</span>
            </div>
          )}

          <form className="space-y-4" onSubmit={handleSubmit}>
            <Input
              label="Username or Email"
              name="username"
              value={formData.username}
              onChange={handleChange}
              placeholder="e.g. admin, manager, or supplier_apex"
              required
              autoFocus
            />

            <Input
              label="Password"
              name="password"
              type="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="••••••••"
              required
            />

            <Button
              type="submit"
              variant="primary"
              className="w-full py-2.5 text-base font-semibold shadow-md shadow-blue-600/20"
              loading={loading}
            >
              Sign In
            </Button>
          </form>

          {/* Quick Demo Credentials */}
          <div className="mt-6 pt-6 border-t border-slate-100">
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2.5 text-center">
              Quick Demo Logins
            </p>
            <div className="grid grid-cols-3 gap-2">
              <button
                type="button"
                onClick={() => handleFillDemo('admin', 'Admin@12345')}
                className="p-2 text-xs font-medium text-slate-700 bg-slate-100 hover:bg-slate-200 rounded-lg transition-colors text-left border border-slate-200"
              >
                <div className="font-bold text-blue-600 text-[11px]">Admin</div>
                <div className="text-[9px] text-slate-500 truncate">admin</div>
              </button>
              <button
                type="button"
                onClick={() => handleFillDemo('manager', 'Manager@12345')}
                className="p-2 text-xs font-medium text-slate-700 bg-slate-100 hover:bg-slate-200 rounded-lg transition-colors text-left border border-slate-200"
              >
                <div className="font-bold text-indigo-600 text-[11px]">Manager</div>
                <div className="text-[9px] text-slate-500 truncate">manager</div>
              </button>
              <button
                type="button"
                onClick={() => handleFillDemo('supplier_apex', 'Supplier@12345')}
                className="p-2 text-xs font-medium text-slate-700 bg-emerald-50 hover:bg-emerald-100 rounded-lg transition-colors text-left border border-emerald-200"
              >
                <div className="font-bold text-emerald-700 text-[11px]">Supplier</div>
                <div className="text-[9px] text-emerald-600 truncate">Apex Micro</div>
              </button>
            </div>
          </div>

          <div className="mt-6 text-center text-xs text-slate-500">
            Don't have an account?{' '}
            <Link to="/register" className="font-semibold text-blue-600 hover:text-blue-500">
              Register here
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
