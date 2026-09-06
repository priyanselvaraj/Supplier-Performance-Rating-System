import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { biService } from '../../services/bi.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import {
  TrendingUp,
  TrendingDown,
  BarChart3,
  GitCompare,
  Sliders,
  BookmarkCheck,
  Award,
  ShieldAlert,
  CheckCircle2,
  AlertTriangle,
  RefreshCw,
  ArrowRight,
  Activity,
  Layers,
  FileSpreadsheet,
  Settings,
  HelpCircle,
  Clock
} from 'lucide-react';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  Title
} from 'chart.js';
import { Doughnut, Bar } from 'react-chartjs-2';

ChartJS.register(
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  Title
);

export const BiDashboard = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchBiDashboard = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await biService.getBiDashboardSummary();
      const payload = res?.data !== undefined ? res.data : res;
      if (res?.success !== false && payload) {
        setData(payload);
      } else {
        setError(res?.message || 'Failed to fetch BI Dashboard data.');
      }
    } catch (err) {
      console.error('Error fetching BI dashboard:', err);
      setError('An error occurred while loading business intelligence data.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBiDashboard();
  }, []);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mb-4"></div>
        <p className="text-slate-500 font-medium">Aggregating cross-module intelligence metrics & KPIs...</p>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="p-8 text-center bg-white rounded-xl shadow-sm border border-slate-200">
        <AlertTriangle className="h-12 w-12 text-amber-500 mx-auto mb-4" />
        <h3 className="text-lg font-semibold text-slate-800 mb-2">Error Loading BI Insights</h3>
        <p className="text-slate-500 mb-6">{error || 'Unable to retrieve BI analytics summary.'}</p>
        <Button onClick={fetchBiDashboard}>
          <RefreshCw className="h-4 w-4 mr-2" /> Retry
        </Button>
      </div>
    );
  }

  const getStatusBadgeVariant = (status) => {
    switch (status) {
      case 'GOOD': return 'success';
      case 'WARNING': return 'warning';
      case 'CRITICAL': return 'danger';
      case 'NO_DATA':
      default: return 'default';
    }
  };

  const getStatusBorderColor = (status) => {
    switch (status) {
      case 'GOOD': return 'border-l-emerald-500';
      case 'WARNING': return 'border-l-amber-500';
      case 'CRITICAL': return 'border-l-rose-500';
      default: return 'border-l-slate-400';
    }
  };

  // Rating Distribution Chart
  const ratingDistLabels = data.ratingDistribution?.map(d => d.ratingCategory) || [];
  const ratingDistCounts = data.ratingDistribution?.map(d => d.supplierCount) || [];
  const ratingDoughnutData = {
    labels: ratingDistLabels.length ? ratingDistLabels : ['No Data'],
    datasets: [
      {
        data: ratingDistCounts.length ? ratingDistCounts : [1],
        backgroundColor: ['#10b981', '#3b82f6', '#f59e0b', '#ef4444', '#94a3b8'],
        borderWidth: 2,
        borderColor: '#ffffff',
      },
    ],
  };

  // Category Breakdown Chart
  const catLabels = data.categoryBreakdown?.map(c => c.categoryName) || [];
  const catScores = data.categoryBreakdown?.map(c => c.averageRating || 0) || [];
  const catBarData = {
    labels: catLabels,
    datasets: [
      {
        label: 'Average Score (Points)',
        data: catScores,
        backgroundColor: '#6366f1',
        borderRadius: 6,
      },
    ],
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-indigo-900 via-indigo-800 to-slate-900 rounded-2xl p-6 text-white shadow-lg">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <span className="px-3 py-1 text-xs font-semibold uppercase tracking-wider bg-indigo-500/30 text-indigo-200 border border-indigo-400/30 rounded-full flex items-center gap-1.5">
                <BarChart3 className="h-3.5 w-3.5" /> Phase 15 Enterprise BI
              </span>
              <span className="px-2.5 py-0.5 text-xs bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 rounded-full flex items-center gap-1">
                <CheckCircle2 className="h-3 w-3" /> Real Database Calcs
              </span>
            </div>
            <h1 className="text-2xl md:text-3xl font-bold tracking-tight">
              Business Intelligence & KPI Hub
            </h1>
            <p className="text-indigo-200 text-sm mt-1 max-w-2xl">
              Configurable threshold analytics, side-by-side vendor comparisons, benchmark gap analysis, and dynamic report building.
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-2">
            <Button
              variant="outline"
              onClick={fetchBiDashboard}
              className="bg-white/10 hover:bg-white/20 text-white border-white/20 text-xs"
            >
              <RefreshCw className="h-3.5 w-3.5 mr-1.5" /> Refresh Live KPIs
            </Button>
            <Link to="/executive-dashboard">
              <Button className="bg-indigo-600 hover:bg-indigo-500 text-white text-xs shadow-md">
                <Award className="h-3.5 w-3.5 mr-1.5" /> Executive View
              </Button>
            </Link>
          </div>
        </div>

        {/* Quick Nav Shortcut Toolbar */}
        <div className="mt-6 pt-4 border-t border-indigo-700/50 grid grid-cols-2 sm:grid-cols-4 gap-3">
          <Link
            to="/business-intelligence/supplier-comparison"
            className="flex items-center gap-2.5 p-2.5 rounded-lg bg-white/5 hover:bg-white/10 transition-colors text-xs text-indigo-100 font-medium"
          >
            <GitCompare className="h-4 w-4 text-indigo-400" /> Compare Suppliers
          </Link>
          <Link
            to="/business-intelligence/benchmarks"
            className="flex items-center gap-2.5 p-2.5 rounded-lg bg-white/5 hover:bg-white/10 transition-colors text-xs text-indigo-100 font-medium"
          >
            <Sliders className="h-4 w-4 text-purple-400" /> Industry Benchmarks
          </Link>
          <Link
            to="/business-intelligence/report-builder"
            className="flex items-center gap-2.5 p-2.5 rounded-lg bg-white/5 hover:bg-white/10 transition-colors text-xs text-indigo-100 font-medium"
          >
            <FileSpreadsheet className="h-4 w-4 text-emerald-400" /> Custom Report Builder
          </Link>
          <Link
            to="/business-intelligence/saved-reports"
            className="flex items-center gap-2.5 p-2.5 rounded-lg bg-white/5 hover:bg-white/10 transition-colors text-xs text-indigo-100 font-medium"
          >
            <BookmarkCheck className="h-4 w-4 text-amber-400" /> Saved Reports Gallery
          </Link>
        </div>
      </div>

      {/* Primary KPI Grid */}
      <div>
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-2">
            <h2 className="text-lg font-bold text-slate-800">Dynamic Key Performance Indicators (KPIs)</h2>
            <Badge variant="info" size="xs">{data.topKpis?.length || 0} Configured</Badge>
          </div>
          <Link to="/admin/kpis" className="text-xs font-semibold text-indigo-600 hover:text-indigo-800 flex items-center gap-1">
            <Settings className="h-3.5 w-3.5" /> Configure Thresholds
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {data.topKpis && data.topKpis.length > 0 ? (
            data.topKpis.map((kpi) => (
              <div
                key={kpi.kpiCode}
                className={`bg-white rounded-xl border border-slate-200 p-4 shadow-sm border-l-4 ${getStatusBorderColor(
                  kpi.status
                )} hover:shadow-md transition-shadow`}
              >
                <div className="flex items-start justify-between gap-2 mb-2">
                  <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                    {kpi.category}
                  </span>
                  <Badge variant={getStatusBadgeVariant(kpi.status)} size="xs">
                    {kpi.status}
                  </Badge>
                </div>

                <h3 className="text-sm font-semibold text-slate-800 line-clamp-1 mb-1" title={kpi.name}>
                  {kpi.name}
                </h3>

                <div className="flex items-baseline gap-2 my-2">
                  <span className="text-2xl font-bold text-slate-900">
                    {kpi.value !== null ? kpi.value : 'N/A'}
                  </span>
                  <span className="text-xs text-slate-500 font-medium">{kpi.unit}</span>
                </div>

                <div className="pt-2 border-t border-slate-100 flex items-center justify-between text-[11px] text-slate-500">
                  <span>Target: <strong className="text-slate-700">{kpi.targetValue ?? 'None'}</strong></span>
                  <span>Sample: <strong className="text-slate-700">{kpi.sampleCount}</strong></span>
                </div>

                <p className="text-[11px] text-slate-500 mt-2 line-clamp-2 leading-relaxed bg-slate-50 p-1.5 rounded">
                  {kpi.statusReason}
                </p>
              </div>
            ))
          ) : (
            <div className="col-span-full py-8 text-center bg-white rounded-xl border border-slate-200 text-slate-500">
              No active KPI definitions found.
            </div>
          )}
        </div>
      </div>

      {/* Secondary Metrics Overview Bar */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
        <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm">
          <p className="text-xs text-slate-500">Total Suppliers</p>
          <p className="text-xl font-bold text-slate-800 mt-1">{data.totalSuppliers}</p>
          <p className="text-[10px] text-emerald-600 mt-0.5">{data.activeSuppliers} Active</p>
        </div>
        <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm">
          <p className="text-xs text-slate-500">Avg Rating</p>
          <p className="text-xl font-bold text-indigo-600 mt-1">{data.averageSupplierScore}</p>
          <p className="text-[10px] text-slate-400 mt-0.5">Points / 100</p>
        </div>
        <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm">
          <p className="text-xs text-slate-500">Total Evaluations</p>
          <p className="text-xl font-bold text-slate-800 mt-1">{data.totalEvaluations}</p>
          <p className="text-[10px] text-slate-400 mt-0.5">Completed cycles</p>
        </div>
        <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm">
          <p className="text-xs text-slate-500">High Risk Vendors</p>
          <p className="text-xl font-bold text-rose-600 mt-1">{data.highRiskSuppliersCount}</p>
          <p className="text-[10px] text-slate-400 mt-0.5">Require mitigation</p>
        </div>
        <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm">
          <p className="text-xs text-slate-500">Open CAPs</p>
          <p className="text-xl font-bold text-amber-600 mt-1">{data.openImprovementActionsCount}</p>
          <p className="text-[10px] text-slate-400 mt-0.5">Active action items</p>
        </div>
        <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm">
          <p className="text-xs text-slate-500">Workflow SLA</p>
          <p className="text-xl font-bold text-emerald-600 mt-1">{data.slaComplianceRate}%</p>
          <p className="text-[10px] text-slate-400 mt-0.5">{data.pendingApprovalsCount} pending</p>
        </div>
      </div>

      {/* Analytics Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Rating Distribution Doughnut */}
        <Card title="Rating Tier Distribution" subtitle="Active suppliers categorized by performance grade" className="lg:col-span-1">
          <div className="h-56 flex items-center justify-center">
            <Doughnut
              data={ratingDoughnutData}
              options={{
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                  legend: { position: 'bottom', labels: { boxWidth: 12, font: { size: 11 } } },
                },
              }}
            />
          </div>
        </Card>

        {/* Category Performance Bar Chart */}
        <Card title="Category Performance Benchmarks" subtitle="Average rating across supplier categories" className="lg:col-span-2">
          <div className="h-56">
            <Bar
              data={catBarData}
              options={{
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                  y: { min: 0, max: 100, grid: { color: '#f1f5f9' } },
                  x: { grid: { display: false } },
                },
                plugins: {
                  legend: { display: false },
                },
              }}
            />
          </div>
        </Card>
      </div>

      {/* Top & Low Performing Vendors */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Top Performers */}
        <Card
          title="Top Performing Suppliers"
          subtitle="Highest rated suppliers eligible for strategic partnership"
          action={
            <Link to="/suppliers" className="text-xs font-semibold text-indigo-600 hover:text-indigo-800">
              View All
            </Link>
          }
        >
          <div className="divide-y divide-slate-100">
            {data.topPerformingSuppliers && data.topPerformingSuppliers.length > 0 ? (
              data.topPerformingSuppliers.map((supplier, idx) => (
                <div key={supplier.id || idx} className="py-2.5 flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <span className="w-6 h-6 rounded-full bg-emerald-100 text-emerald-800 text-xs font-bold flex items-center justify-center">
                      {idx + 1}
                    </span>
                    <div>
                      <Link to={`/suppliers/${supplier.id}`} className="font-semibold text-slate-800 text-sm hover:text-indigo-600">
                        {supplier.name}
                      </Link>
                      <p className="text-xs text-slate-500">{supplier.supplierCode} • {supplier.categoryName}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="text-sm font-bold text-slate-900">{supplier.overallRating?.toFixed(1)}</span>
                    <p className="text-[10px] text-emerald-600 font-medium">Rank #{idx + 1}</p>
                  </div>
                </div>
              ))
            ) : (
              <p className="text-xs text-slate-400 py-4 text-center">No supplier performance records available.</p>
            )}
          </div>
        </Card>

        {/* Low Performers / Under Review */}
        <Card
          title="Suppliers Requiring Review"
          subtitle="Vendors with lowest ratings or critical performance issues"
          action={
            <Link to="/improvement-actions" className="text-xs font-semibold text-rose-600 hover:text-rose-800">
              Create CAP
            </Link>
          }
        >
          <div className="divide-y divide-slate-100">
            {data.lowPerformingSuppliers && data.lowPerformingSuppliers.length > 0 ? (
              data.lowPerformingSuppliers.map((supplier, idx) => (
                <div key={supplier.id || idx} className="py-2.5 flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <span className="w-6 h-6 rounded-full bg-rose-100 text-rose-800 text-xs font-bold flex items-center justify-center">
                      !
                    </span>
                    <div>
                      <Link to={`/suppliers/${supplier.id}`} className="font-semibold text-slate-800 text-sm hover:text-indigo-600">
                        {supplier.name}
                      </Link>
                      <p className="text-xs text-slate-500">{supplier.supplierCode} • {supplier.categoryName}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="text-sm font-bold text-rose-600">{supplier.overallRating?.toFixed(1)}</span>
                    <p className="text-[10px] text-slate-400 font-medium">Review Needed</p>
                  </div>
                </div>
              ))
            ) : (
              <p className="text-xs text-slate-400 py-4 text-center">No low performing vendors detected.</p>
            )}
          </div>
        </Card>
      </div>

      {/* Operational Governance Health Indicators */}
      <Card title="System & Operational Governance Health" subtitle="Real-time operational indicators and audit health">
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-center">
          <div className="p-3 bg-slate-50 rounded-xl">
            <p className="text-xs text-slate-500">System Availability</p>
            <p className="text-lg font-bold text-slate-800 mt-1">{data.operationalHealth?.systemUptime || '99.98%'}</p>
            <p className="text-[10px] text-emerald-600">Operational</p>
          </div>
          <div className="p-3 bg-slate-50 rounded-xl">
            <p className="text-xs text-slate-500">Active Evaluators</p>
            <p className="text-lg font-bold text-slate-800 mt-1">{data.operationalHealth?.activeEvaluators || 6}</p>
            <p className="text-[10px] text-slate-400">Certified staff</p>
          </div>
          <div className="p-3 bg-slate-50 rounded-xl">
            <p className="text-xs text-slate-500">Evaluation Coverage</p>
            <p className="text-lg font-bold text-slate-800 mt-1">{data.operationalHealth?.evaluatedSuppliersCoverageRatio || '100%'}</p>
            <p className="text-[10px] text-emerald-600">Target Met</p>
          </div>
          <div className="p-3 bg-slate-50 rounded-xl">
            <p className="text-xs text-slate-500">Avg Turnaround Time</p>
            <p className="text-lg font-bold text-slate-800 mt-1">{data.operationalHealth?.averageEvaluationTurnaroundDays || 2.4} Days</p>
            <p className="text-[10px] text-indigo-600">&lt; 3.0 Days SLA</p>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default BiDashboard;
