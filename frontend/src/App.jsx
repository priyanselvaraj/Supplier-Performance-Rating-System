import React, { Suspense, lazy } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/layout/ProtectedRoute';
import { DashboardLayout } from './components/layout/DashboardLayout';
import { SupplierLayout } from './components/layout/SupplierLayout';

// Lazy-loaded Pages for Production Performance and Code Splitting
const Login = lazy(() => import('./pages/auth/Login').then(m => ({ default: m.Login || m.default })));
const Register = lazy(() => import('./pages/auth/Register').then(m => ({ default: m.Register || m.default })));
const Dashboard = lazy(() => import('./pages/dashboard/Dashboard').then(m => ({ default: m.Dashboard || m.default })));
const SupplierList = lazy(() => import('./pages/suppliers/SupplierList').then(m => ({ default: m.SupplierList || m.default })));
const SupplierDetails = lazy(() => import('./pages/suppliers/SupplierDetails').then(m => ({ default: m.SupplierDetails || m.default })));
const SupplierPortalAdmin = lazy(() => import('./pages/suppliers/SupplierPortalAdmin').then(m => ({ default: m.SupplierPortalAdmin || m.default })));
const CategoryList = lazy(() => import('./pages/categories/CategoryList').then(m => ({ default: m.CategoryList || m.default })));
const CriteriaList = lazy(() => import('./pages/criteria/CriteriaList').then(m => ({ default: m.CriteriaList || m.default })));
const EvaluationList = lazy(() => import('./pages/evaluations/EvaluationList').then(m => ({ default: m.EvaluationList || m.default })));
const EvaluationForm = lazy(() => import('./pages/evaluations/EvaluationForm').then(m => ({ default: m.EvaluationForm || m.default })));
const EvaluationDetails = lazy(() => import('./pages/evaluations/EvaluationDetails').then(m => ({ default: m.EvaluationDetails || m.default })));
const RatingsPage = lazy(() => import('./pages/ratings/RatingsPage').then(m => ({ default: m.RatingsPage || m.default })));
const SupplierRatingDetails = lazy(() => import('./pages/ratings/SupplierRatingDetails').then(m => ({ default: m.SupplierRatingDetails || m.default })));
const AnalyticsPage = lazy(() => import('./pages/analytics/AnalyticsPage').then(m => ({ default: m.AnalyticsPage || m.default })));
const AiIntelligencePage = lazy(() => import('./pages/ai/AiIntelligencePage').then(m => ({ default: m.AiIntelligencePage || m.default })));
const AiCopilotPage = lazy(() => import('./pages/ai/AiCopilotPage').then(m => ({ default: m.AiCopilotPage || m.default })));
const ExecutiveInsightsPage = lazy(() => import('./pages/ai/ExecutiveInsightsPage').then(m => ({ default: m.ExecutiveInsightsPage || m.default })));
const NotificationCenter = lazy(() => import('./pages/notifications/NotificationCenter').then(m => ({ default: m.NotificationCenter || m.default })));
const ImprovementActionsPage = lazy(() => import('./pages/improvement/ImprovementActionsPage').then(m => ({ default: m.ImprovementActionsPage || m.default })));
const MonitoringDashboard = lazy(() => import('./pages/monitoring/MonitoringDashboard').then(m => ({ default: m.MonitoringDashboard || m.default })));
const ReportsPage = lazy(() => import('./pages/reports/ReportsPage').then(m => ({ default: m.ReportsPage || m.default })));
const Profile = lazy(() => import('./pages/profile/Profile').then(m => ({ default: m.Profile || m.default })));
const UserManagement = lazy(() => import('./pages/users/UserManagement').then(m => ({ default: m.UserManagement || m.default })));

// Workflow & Approvals Pages (Phase 14)
const WorkflowDashboard = lazy(() => import('./pages/workflows/WorkflowDashboard').then(m => ({ default: m.WorkflowDashboard || m.default })));
const WorkflowDetails = lazy(() => import('./pages/workflows/WorkflowDetails').then(m => ({ default: m.WorkflowDetails || m.default })));
const MyApprovals = lazy(() => import('./pages/workflows/MyApprovals').then(m => ({ default: m.MyApprovals || m.default })));
const WorkflowConfigAdmin = lazy(() => import('./pages/workflows/WorkflowConfigAdmin').then(m => ({ default: m.WorkflowConfigAdmin || m.default })));

// Advanced Business Intelligence & KPI Builder Pages (Phase 15)
const BiDashboard = lazy(() => import('./pages/bi/BiDashboard').then(m => ({ default: m.BiDashboard || m.default })));
const SupplierComparison = lazy(() => import('./pages/bi/SupplierComparison').then(m => ({ default: m.SupplierComparison || m.default })));
const PerformanceBenchmarking = lazy(() => import('./pages/bi/PerformanceBenchmarking').then(m => ({ default: m.PerformanceBenchmarking || m.default })));
const ReportBuilder = lazy(() => import('./pages/bi/ReportBuilder').then(m => ({ default: m.ReportBuilder || m.default })));
const SavedReports = lazy(() => import('./pages/bi/SavedReports').then(m => ({ default: m.SavedReports || m.default })));
const ExecutiveDashboard = lazy(() => import('./pages/bi/ExecutiveDashboard').then(m => ({ default: m.ExecutiveDashboard || m.default })));
const KpiAdmin = lazy(() => import('./pages/bi/KpiAdmin').then(m => ({ default: m.KpiAdmin || m.default })));

// Enterprise Integrations & External APIs (Phase 16)
const IntegrationsDashboard = lazy(() => import('./pages/integrations/IntegrationsDashboard').then(m => ({ default: m.IntegrationsDashboard || m.default })));

// Supplier Portal Pages (Phase 13)
const SupplierDashboard = lazy(() => import('./pages/supplier-portal/SupplierDashboard').then(m => ({ default: m.SupplierDashboard || m.default })));
const SupplierProfile = lazy(() => import('./pages/supplier-portal/SupplierProfile').then(m => ({ default: m.SupplierProfile || m.default })));
const SupplierPerformance = lazy(() => import('./pages/supplier-portal/SupplierPerformance').then(m => ({ default: m.SupplierPerformance || m.default })));
const SupplierEvaluations = lazy(() => import('./pages/supplier-portal/SupplierEvaluations').then(m => ({ default: m.SupplierEvaluations || m.default })));
const SupplierEvaluationDetails = lazy(() => import('./pages/supplier-portal/SupplierEvaluationDetails').then(m => ({ default: m.SupplierEvaluationDetails || m.default })));
const SupplierImprovementActions = lazy(() => import('./pages/supplier-portal/SupplierImprovementActions').then(m => ({ default: m.SupplierImprovementActions || m.default })));
const SupplierDocuments = lazy(() => import('./pages/supplier-portal/SupplierDocuments').then(m => ({ default: m.SupplierDocuments || m.default })));
const SupplierCommunications = lazy(() => import('./pages/supplier-portal/SupplierCommunications').then(m => ({ default: m.SupplierCommunications || m.default })));
const SupplierNotifications = lazy(() => import('./pages/supplier-portal/SupplierNotifications').then(m => ({ default: m.SupplierNotifications || m.default })));
const SupplierAccount = lazy(() => import('./pages/supplier-portal/SupplierAccount').then(m => ({ default: m.SupplierAccount || m.default })));

const NotFoundPage = lazy(() => import('./pages/NotFoundPage'));

// Fallback spinner during route transitions
function PageLoadingFallback() {
  return (
    <div className="flex h-screen w-full items-center justify-center bg-slate-50">
      <div className="flex flex-col items-center gap-3">
        <div className="h-10 w-10 animate-spin rounded-full border-4 border-emerald-600 border-t-transparent shadow-sm"></div>
        <p className="text-sm font-medium text-slate-500 animate-pulse">Loading SPRS Portal...</p>
      </div>
    </div>
  );
}

export function App() {
  return (
    <AuthProvider>
      <Suspense fallback={<PageLoadingFallback />}>
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Supplier Self-Service Portal Routes (Phase 13) */}
          <Route
            element={
              <ProtectedRoute>
                <SupplierLayout />
              </ProtectedRoute>
            }
          >
            <Route path="/supplier-portal" element={<Navigate to="/supplier-portal/dashboard" replace />} />
            <Route path="/supplier-portal/dashboard" element={<SupplierDashboard />} />
            <Route path="/supplier-portal/profile" element={<SupplierProfile />} />
            <Route path="/supplier-portal/performance" element={<SupplierPerformance />} />
            <Route path="/supplier-portal/evaluations" element={<SupplierEvaluations />} />
            <Route path="/supplier-portal/evaluations/:id" element={<SupplierEvaluationDetails />} />
            <Route path="/supplier-portal/improvement-actions" element={<SupplierImprovementActions />} />
            <Route path="/supplier-portal/documents" element={<SupplierDocuments />} />
            <Route path="/supplier-portal/communications" element={<SupplierCommunications />} />
            <Route path="/supplier-portal/notifications" element={<SupplierNotifications />} />
            <Route path="/supplier-portal/account" element={<SupplierAccount />} />
          </Route>

          {/* Authenticated Internal ERP Dashboard Routes */}
          <Route
            element={
              <ProtectedRoute>
                <DashboardLayout />
              </ProtectedRoute>
            }
          >
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/monitoring" element={<MonitoringDashboard />} />
            <Route path="/suppliers" element={<SupplierList />} />
            <Route path="/suppliers/:id" element={<SupplierDetails />} />
            <Route path="/workflows" element={<WorkflowDashboard />} />
            <Route path="/workflows/:id" element={<WorkflowDetails />} />
            <Route path="/my-approvals" element={<MyApprovals />} />
            <Route path="/supplier-portal/admin/requests" element={<SupplierPortalAdmin />} />
            <Route path="/categories" element={<CategoryList />} />
            <Route path="/evaluations" element={<EvaluationList />} />
            <Route path="/evaluations/new" element={<EvaluationForm />} />
            <Route path="/evaluations/:id" element={<EvaluationDetails />} />
            <Route path="/ratings" element={<RatingsPage />} />
            <Route path="/ratings/supplier/:supplierId" element={<SupplierRatingDetails />} />
            <Route path="/improvement-actions" element={<ImprovementActionsPage />} />
            <Route path="/ai-intelligence" element={<AiIntelligencePage />} />
            <Route path="/ai-copilot" element={<AiCopilotPage />} />
            <Route path="/executive-insights" element={<ExecutiveInsightsPage />} />
            <Route path="/notifications" element={<NotificationCenter />} />
            <Route path="/analytics" element={<AnalyticsPage />} />
            <Route path="/reports" element={<ReportsPage />} />

            {/* Business Intelligence & Executive Routes (Phase 15) */}
            <Route path="/executive-dashboard" element={<ExecutiveDashboard />} />
            <Route path="/business-intelligence" element={<BiDashboard />} />
            <Route path="/business-intelligence/supplier-comparison" element={<SupplierComparison />} />
            <Route path="/business-intelligence/benchmarks" element={<PerformanceBenchmarking />} />
            <Route path="/business-intelligence/report-builder" element={<ReportBuilder />} />
            <Route path="/business-intelligence/saved-reports" element={<SavedReports />} />

            <Route path="/profile" element={<Profile />} />

            {/* Admin Only Routes */}
            <Route
              path="/admin/integrations"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <IntegrationsDashboard />
                </ProtectedRoute>
              }
            />
            <Route path="/integrations" element={<Navigate to="/admin/integrations" replace />} />
            <Route
              path="/admin/kpis"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <KpiAdmin />
                </ProtectedRoute>
              }
            />
            <Route
              path="/admin/workflows"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <WorkflowConfigAdmin />
                </ProtectedRoute>
              }
            />
            <Route
              path="/criteria"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <CriteriaList />
                </ProtectedRoute>
              }
            />
            <Route
              path="/evaluation-criteria"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <CriteriaList />
                </ProtectedRoute>
              }
            />
            <Route
              path="/users"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <UserManagement />
                </ProtectedRoute>
              }
            />
          </Route>

          {/* 404 Catch-All Route */}
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Suspense>
    </AuthProvider>
  );
}

export default App;
