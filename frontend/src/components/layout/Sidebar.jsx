import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard,
  Building2,
  Tags,
  CheckSquare,
  Sliders,
  BarChart3,
  TrendingUp,
  Award,
  Users,
  UserCheck,
  PlusCircle,
  X,
  FileText,
  Sparkles,
  Bell,
  Wrench,
  Activity,
  GitPullRequest,
  Inbox,
  SlidersHorizontal,
  Layers,
  PieChart,
  Target,
  Database,
  Bot
} from 'lucide-react';

export const Sidebar = ({ isOpen, onClose }) => {
  const { isAdmin } = useAuth();

  const navItems = [
    { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/ai-copilot', label: 'AI Copilot', icon: Bot },
    { to: '/executive-insights', label: 'Executive AI Insights', icon: Sparkles },
    { to: '/executive-dashboard', label: 'Executive Overview', icon: PieChart },
    { to: '/business-intelligence', label: 'Business Intelligence', icon: Layers },
    { to: '/workflows', label: 'Workflows', icon: GitPullRequest },
    { to: '/my-approvals', label: 'My Approvals', icon: Inbox },
    { to: '/monitoring', label: 'Live Monitoring', icon: Activity },
    { to: '/suppliers', label: 'Suppliers', icon: Building2 },
    { to: '/categories', label: 'Categories', icon: Tags },
    { to: '/evaluations', label: 'Evaluations', icon: CheckSquare },
    { to: '/evaluations/new', label: 'New Evaluation', icon: PlusCircle },
    { to: '/ratings', label: 'Ratings Engine', icon: Award },
    { to: '/improvement-actions', label: 'Improvement Actions', icon: Wrench },
    { to: '/ai-intelligence', label: 'AI Intelligence', icon: Sparkles },
    { to: '/notifications', label: 'Notifications', icon: Bell },
    { to: '/analytics', label: 'Analytics', icon: TrendingUp },
    { to: '/reports', label: 'Reports & Exports', icon: FileText },
    ...(isAdmin() ? [{ to: '/admin/integrations', label: 'Integrations & APIs', icon: Database }] : []),
    ...(isAdmin() ? [{ to: '/admin/kpis', label: 'KPI Thresholds', icon: Target }] : []),
    ...(isAdmin() ? [{ to: '/admin/workflows', label: 'Workflow Config', icon: SlidersHorizontal }] : []),
    ...(isAdmin() ? [{ to: '/criteria', label: 'Evaluation Criteria', icon: Sliders }] : []),
    ...(isAdmin() ? [{ to: '/users', label: 'User Management', icon: Users }] : []),
    { to: '/profile', label: 'My Profile', icon: UserCheck },
  ];

  return (
    <>
      {/* Mobile overlay */}
      {isOpen && (
        <div
          className="fixed inset-0 z-40 bg-slate-900/50 backdrop-blur-xs lg:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar navigation */}
      <aside
        className={`fixed top-0 bottom-0 left-0 z-40 w-64 bg-slate-900 text-slate-300 transition-transform duration-300 ease-in-out lg:translate-x-0 flex flex-col ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        {/* Brand Header */}
        <div className="flex h-16 items-center justify-between px-6 border-b border-slate-800 bg-slate-950/40">
          <div className="flex items-center gap-3">
            <div className="h-8 w-8 rounded-lg bg-blue-600 flex items-center justify-center text-white font-bold shadow-md shadow-blue-500/20">
              S
            </div>
            <div>
              <span className="font-bold text-white tracking-wide text-base block leading-tight">SPRS</span>
              <span className="text-[10px] text-slate-400 font-medium">Rating System</span>
            </div>
          </div>
          <button
            onClick={onClose}
            className="lg:hidden text-slate-400 hover:text-white p-1 rounded-md"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Navigation list */}
        <nav className="flex-1 overflow-y-auto py-4 px-3 space-y-1.5">
          <div className="px-3 pb-2 text-[11px] font-semibold text-slate-400 uppercase tracking-wider">
            Main Menu
          </div>
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                onClick={() => onClose && onClose()}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                    isActive
                      ? 'bg-blue-600 text-white shadow-sm shadow-blue-600/30 font-semibold'
                      : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200'
                  }`
                }
              >
                <Icon className="h-4 w-4 flex-shrink-0" />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>
      </aside>
    </>
  );
};
