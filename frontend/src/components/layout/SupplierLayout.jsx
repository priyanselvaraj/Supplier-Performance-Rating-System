import React, { useState } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { NotificationBell } from '../notifications/NotificationBell';
import {
  LayoutDashboard,
  Building2,
  Award,
  CheckSquare,
  Wrench,
  FileText,
  MessageSquare,
  Bell,
  UserCheck,
  LogOut,
  Menu,
  X,
  ShieldCheck,
  ExternalLink
} from 'lucide-react';

export const SupplierLayout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { user, logout, isAdmin, isManager } = useAuth();
  const navigate = useNavigate();

  const navItems = [
    { to: '/supplier-portal/dashboard', label: 'Overview', icon: LayoutDashboard },
    { to: '/supplier-portal/profile', label: 'Company Profile', icon: Building2 },
    { to: '/supplier-portal/performance', label: 'My Performance', icon: Award },
    { to: '/supplier-portal/evaluations', label: 'Evaluations', icon: CheckSquare },
    { to: '/supplier-portal/improvement-actions', label: 'CAP Actions', icon: Wrench },
    { to: '/supplier-portal/documents', label: 'Documents & Certs', icon: FileText },
    { to: '/supplier-portal/communications', label: 'Communications', icon: MessageSquare },
    { to: '/supplier-portal/notifications', label: 'Notifications', icon: Bell },
    { to: '/supplier-portal/account', label: 'User Account', icon: UserCheck },
  ];

  return (
    <div className="min-h-screen bg-slate-50 flex">
      {/* Mobile backdrop */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-40 bg-slate-900/60 backdrop-blur-xs lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Supplier Portal Sidebar */}
      <aside
        className={`fixed top-0 bottom-0 left-0 z-40 w-64 bg-slate-900 text-slate-300 transition-transform duration-300 ease-in-out lg:translate-x-0 flex flex-col ${
          sidebarOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        {/* Brand Header */}
        <div className="flex h-16 items-center justify-between px-6 border-b border-slate-800 bg-slate-950/60">
          <div className="flex items-center gap-3">
            <div className="h-8 w-8 rounded-lg bg-emerald-600 flex items-center justify-center text-white font-bold shadow-md shadow-emerald-600/20">
              <ShieldCheck className="h-5 w-5" />
            </div>
            <div>
              <span className="font-bold text-white tracking-wide text-sm block leading-tight">SUPPLIER PORTAL</span>
              <span className="text-[10px] text-emerald-400 font-semibold tracking-wider uppercase">Self-Service</span>
            </div>
          </div>
          <button
            onClick={() => setSidebarOpen(false)}
            className="lg:hidden text-slate-400 hover:text-white p-1 rounded-md"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Supplier Tenant Info */}
        <div className="px-4 py-3.5 mx-3 mt-3 rounded-xl bg-slate-800/80 border border-slate-700/60">
          <p className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Vendor Account</p>
          <p className="text-sm font-bold text-white truncate">{user?.supplierName || user?.fullName || 'Authorized Supplier'}</p>
          <div className="mt-1 flex items-center gap-1.5">
            <span className="inline-block w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
            <span className="text-[11px] text-emerald-300 font-medium">Verified Vendor</span>
          </div>
        </div>

        {/* Navigation List */}
        <nav className="flex-1 overflow-y-auto py-3 px-3 space-y-1">
          <div className="px-3 pb-1 pt-2 text-[11px] font-semibold text-slate-400 uppercase tracking-wider">
            Supplier Menu
          </div>
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                onClick={() => setSidebarOpen(false)}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                    isActive
                      ? 'bg-emerald-600 text-white shadow-sm shadow-emerald-600/30 font-semibold'
                      : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200'
                  }`
                }
              >
                <Icon className="h-4 w-4 flex-shrink-0" />
                <span>{item.label}</span>
              </NavLink>
            );
          })}

          {/* Admin / Manager Switch back link */}
          {(isAdmin() || isManager()) && (
            <div className="pt-4 mt-4 border-t border-slate-800">
              <button
                onClick={() => navigate('/dashboard')}
                className="w-full flex items-center justify-between px-3.5 py-2 rounded-xl text-xs font-semibold text-blue-400 hover:bg-blue-950/40 hover:text-blue-300 transition-colors"
              >
                <span>Back to Internal ERP</span>
                <ExternalLink className="h-3.5 w-3.5" />
              </button>
            </div>
          )}
        </nav>

        {/* User Footer */}
        <div className="p-3 border-t border-slate-800 bg-slate-950/40">
          <button
            onClick={logout}
            className="w-full flex items-center gap-3 px-3.5 py-2 text-sm text-rose-400 hover:bg-rose-950/40 hover:text-rose-300 rounded-xl transition-colors font-medium"
          >
            <LogOut className="h-4 w-4" />
            <span>Sign Out</span>
          </button>
        </div>
      </aside>

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0 lg:pl-64">
        {/* Top Header */}
        <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-slate-200 bg-white px-4 sm:px-6 shadow-xs">
          <div className="flex items-center gap-3">
            <button
              onClick={() => setSidebarOpen(!sidebarOpen)}
              className="lg:hidden text-slate-600 hover:text-slate-900 p-1.5 rounded-lg border border-slate-200"
            >
              <Menu className="h-5 w-5" />
            </button>
            <div>
              <h1 className="text-base font-bold text-slate-900 flex items-center gap-2">
                <span>Supplier Self-Service Portal</span>
                <span className="hidden sm:inline-block px-2 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                  {user?.supplierName || 'Portal'}
                </span>
              </h1>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <NotificationBell />
            <div className="h-8 w-px bg-slate-200 mx-1"></div>
            <NavLink
              to="/supplier-portal/account"
              className="flex items-center gap-2.5 p-1 rounded-xl hover:bg-slate-100 transition-colors"
              title="View Account Settings"
            >
              <div className="h-9 w-9 rounded-full bg-emerald-100 border border-emerald-300 flex items-center justify-center text-emerald-800 font-bold text-sm">
                {user?.fullName?.charAt(0) || user?.username?.charAt(0) || 'S'}
              </div>
              <div className="hidden md:block text-left leading-tight">
                <p className="text-xs font-bold text-slate-800">{user?.fullName || user?.username}</p>
                <p className="text-[11px] text-emerald-600 font-semibold">{user?.roles?.[0]?.replace('ROLE_', '') || 'SUPPLIER'}</p>
              </div>
            </NavLink>
          </div>
        </header>

        {/* Page Outlet */}
        <main className="flex-1 p-4 sm:p-6 lg:p-8 max-w-7xl w-full mx-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default SupplierLayout;
