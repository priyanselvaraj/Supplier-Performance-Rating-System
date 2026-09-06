import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  TrendingUp,
  TrendingDown,
  Minus,
  AlertTriangle,
  ShieldCheck,
  Building2,
  Award,
  CheckCircle2,
  Clock,
  ArrowUpRight,
  Activity,
  Layers,
  Sparkles,
  RefreshCw,
  FileSpreadsheet,
  AlertCircle,
  ExternalLink,
  ChevronRight,
  PieChart,
  BarChart3,
  Sliders,
  ChevronDown
} from 'lucide-react';
import { biService } from '../../services/bi.service';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  Title
} from 'chart.js';
import { Doughnut, Bar } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title);

export function ExecutiveDashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchExecutiveDashboard = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await biService.getExecutiveDashboard();
      setData(res.data || res);
    } catch (err) {
      console.error('Failed to load executive dashboard data:', err);
      setError(err.response?.data?.message || 'Failed to fetch executive dashboard metrics');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchExecutiveDashboard();
  }, []);

  if (loading) {
    return (
      <div className="flex h-96 w-full items-center justify-center">
        <div className="flex flex-col items-center gap-3">
          <div className="h-10 w-10 animate-spin rounded-full border-4 border-blue-600 border-t-transparent shadow-md" />
          <p className="text-sm font-medium text-slate-500 animate-pulse">Aggregating Executive Intelligence...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-red-800">
        <div className="flex items-center gap-3">
          <AlertCircle className="h-6 w-6 text-red-600 flex-shrink-0" />
          <div>
            <h3 className="text-base font-semibold text-red-900">Executive Data Pipeline Error</h3>
            <p className="mt-1 text-sm text-red-700">{error}</p>
          </div>
        </div>
        <button
          onClick={fetchExecutiveDashboard}
          className="mt-4 inline-flex items-center gap-2 rounded-lg bg-red-600 px-4 py-2 text-xs font-semibold text-white hover:bg-red-700 transition"
        >
          <RefreshCw className="h-3.5 w-3.5" />
          Retry Connection
        </button>
      </div>
    );
  }

  const {
    supplierOverview = {},
    riskOverview = {},
    performanceOverview = {},
    operationalOverview = {},
    strategicInsights = []
  } = data || {};

  // Risk Distribution Chart Data
  const riskChartData = {
    labels: ['Low Risk', 'Medium Risk', 'High Risk'],
    datasets: [
      {
        data: [
          riskOverview.lowRiskSuppliers || 0,
          riskOverview.mediumRiskSuppliers || 0,
          riskOverview.highRiskSuppliers || 0
        ],
        backgroundColor: ['#10B981', '#F59E0B', '#EF4444'],
        hoverOffset: 4,
        borderWidth: 0
      }
    ]
  };

  // Performance Rating Distribution Chart Data
  const ratingDist = performanceOverview.ratingDistribution || [];
  const ratingChartData = {
    labels: ratingDist.map((item) => item.ratingLevel || item.grade || 'Tier'),
    datasets: [
      {
        label: 'Suppliers in Tier',
        data: ratingDist.map((item) => item.count || 0),
        backgroundColor: '#3B82F6',
        borderRadius: 6
      }
    ]
  };

  return (
    <div className="space-y-6 pb-12">
      {/* Header Banner */}
      <div className="flex flex-col gap-4 rounded-2xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 p-6 sm:p-8 text-white shadow-xl">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 rounded-full bg-blue-500/20 px-3 py-1 text-xs font-medium text-blue-300 border border-blue-400/30">
              <Sparkles className="h-3.5 w-3.5" />
              Executive Strategic Command
            </div>
            <h1 className="mt-2 text-2xl sm:text-3xl font-bold tracking-tight text-white">
              C-Suite Executive Overview
            </h1>
            <p className="mt-1 text-sm text-slate-300">
              High-level strategic insights across supply chain risk, vendor performance trajectories, and operational compliance.
            </p>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={fetchExecutiveDashboard}
              className="inline-flex items-center gap-2 rounded-xl bg-slate-800/80 px-4 py-2 text-sm font-semibold text-slate-200 hover:bg-slate-700 hover:text-white border border-slate-700 transition shadow-sm"
            >
              <RefreshCw className="h-4 w-4" />
              Refresh Insights
            </button>
            <Link
              to="/business-intelligence"
              className="inline-flex items-center gap-2 rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-500 transition shadow-md shadow-blue-500/20"
            >
              <BarChart3 className="h-4 w-4" />
              BI Analytics Center
            </Link>
          </div>
        </div>

        {/* Global Executive Metric Strip */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 pt-4 border-t border-slate-800/80">
          <div>
            <p className="text-xs font-medium text-slate-400">Total Suppliers</p>
            <p className="mt-1 text-xl sm:text-2xl font-bold text-white">{supplierOverview.totalSuppliers ?? 0}</p>
            <span className="text-[11px] text-emerald-400 font-medium">
              +{supplierOverview.newSuppliersLast30Days ?? 0} active in last 30d
            </span>
          </div>

          <div>
            <p className="text-xs font-medium text-slate-400">Avg Performance Score</p>
            <p className="mt-1 text-xl sm:text-2xl font-bold text-white">
              {supplierOverview.overallAverageRating != null ? supplierOverview.overallAverageRating.toFixed(1) : 'N/A'}
              <span className="text-xs font-normal text-slate-400"> / 100</span>
            </p>
            <div className="flex items-center gap-1 text-[11px] text-blue-400 font-medium">
              <span>Top Category: {supplierOverview.topCategory || 'General'}</span>
            </div>
          </div>

          <div>
            <p className="text-xs font-medium text-slate-400">High Risk Vendors</p>
            <p className="mt-1 text-xl sm:text-2xl font-bold text-rose-400">
              {riskOverview.highRiskSuppliers ?? 0}
            </p>
            <span className="text-[11px] text-slate-400">
              {riskOverview.criticalAlertsCount ?? 0} critical alerts pending
            </span>
          </div>

          <div>
            <p className="text-xs font-medium text-slate-400">CAP Closure Rate</p>
            <p className="mt-1 text-xl sm:text-2xl font-bold text-emerald-400">
              {operationalOverview.capClosureRate != null ? `${operationalOverview.capClosureRate.toFixed(1)}%` : '0%'}
            </p>
            <span className="text-[11px] text-slate-400">
              {operationalOverview.workflowSlaComplianceRate != null ? `${operationalOverview.workflowSlaComplianceRate.toFixed(1)}%` : '0%'} SLA compliance
            </span>
          </div>
        </div>
      </div>

      {/* Grid: Risk & Performance Trajectory */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Risk Breakdown Card */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="p-2 rounded-lg bg-rose-50 text-rose-600">
                <AlertTriangle className="h-5 w-5" />
              </div>
              <div>
                <h2 className="text-base font-bold text-slate-900">Supply Chain Risk Profile</h2>
                <p className="text-xs text-slate-500">Enterprise risk segmentation</p>
              </div>
            </div>
            <span className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-semibold text-slate-700">
              Avg Risk: {riskOverview.averageRiskScore != null ? riskOverview.averageRiskScore.toFixed(1) : '0.0'}
            </span>
          </div>

          <div className="mt-6 flex flex-col items-center justify-center">
            <div className="h-44 w-44">
              <Doughnut
                data={riskChartData}
                options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  cutout: '70%',
                  plugins: {
                    legend: { display: false }
                  }
                }}
              />
            </div>

            <div className="mt-4 grid grid-cols-3 w-full gap-2 text-center">
              <div className="rounded-xl bg-emerald-50 p-2.5">
                <p className="text-[11px] font-semibold text-emerald-700">Low Risk</p>
                <p className="text-base font-bold text-emerald-900">{riskOverview.lowRiskSuppliers ?? 0}</p>
              </div>
              <div className="rounded-xl bg-amber-50 p-2.5">
                <p className="text-[11px] font-semibold text-amber-700">Medium Risk</p>
                <p className="text-base font-bold text-amber-900">{riskOverview.mediumRiskSuppliers ?? 0}</p>
              </div>
              <div className="rounded-xl bg-rose-50 p-2.5">
                <p className="text-[11px] font-semibold text-rose-700">High Risk</p>
                <p className="text-base font-bold text-rose-900">{riskOverview.highRiskSuppliers ?? 0}</p>
              </div>
            </div>
          </div>

          {/* High Risk Vendors List */}
          {riskOverview.highRiskVendors && riskOverview.highRiskVendors.length > 0 && (
            <div className="mt-4 pt-4 border-t border-slate-100">
              <p className="text-xs font-semibold text-slate-700 mb-2">Flagged Critical Vendors:</p>
              <div className="flex flex-wrap gap-1.5">
                {riskOverview.highRiskVendors.map((vendor, idx) => (
                  <span
                    key={idx}
                    className="inline-flex items-center gap-1 rounded-md bg-rose-50 px-2 py-1 text-xs font-medium text-rose-700 border border-rose-200"
                  >
                    <AlertTriangle className="h-3 w-3 text-rose-500" />
                    {vendor}
                  </span>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Performance Distribution & Trajectory */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="p-2 rounded-lg bg-blue-50 text-blue-600">
                <TrendingUp className="h-5 w-5" />
              </div>
              <div>
                <h2 className="text-base font-bold text-slate-900">Performance Dynamics</h2>
                <p className="text-xs text-slate-500">Quarterly trend trajectories</p>
              </div>
            </div>
            <span
              className={`inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-semibold ${
                (performanceOverview.averageScoreQuarterChange || 0) >= 0
                  ? 'bg-emerald-50 text-emerald-700'
                  : 'bg-rose-50 text-rose-700'
              }`}
            >
              {(performanceOverview.averageScoreQuarterChange || 0) >= 0 ? '+' : ''}
              {performanceOverview.averageScoreQuarterChange != null
                ? performanceOverview.averageScoreQuarterChange.toFixed(1)
                : '0.0'}
              % QoQ
            </span>
          </div>

          {/* Trajectory Breakdown */}
          <div className="mt-4 grid grid-cols-3 gap-2">
            <div className="rounded-xl border border-emerald-100 bg-emerald-50/50 p-3 text-center">
              <div className="flex items-center justify-center text-emerald-600 mb-1">
                <TrendingUp className="h-4 w-4" />
              </div>
              <p className="text-xs font-semibold text-emerald-800">Improving</p>
              <p className="text-lg font-bold text-emerald-950">
                {performanceOverview.improvingSuppliersCount ?? 0}
              </p>
            </div>

            <div className="rounded-xl border border-slate-100 bg-slate-50/50 p-3 text-center">
              <div className="flex items-center justify-center text-slate-500 mb-1">
                <Minus className="h-4 w-4" />
              </div>
              <p className="text-xs font-semibold text-slate-700">Stable</p>
              <p className="text-lg font-bold text-slate-900">
                {performanceOverview.stableSuppliersCount ?? 0}
              </p>
            </div>

            <div className="rounded-xl border border-rose-100 bg-rose-50/50 p-3 text-center">
              <div className="flex items-center justify-center text-rose-600 mb-1">
                <TrendingDown className="h-4 w-4" />
              </div>
              <p className="text-xs font-semibold text-rose-800">Declining</p>
              <p className="text-lg font-bold text-rose-950">
                {performanceOverview.decliningSuppliersCount ?? 0}
              </p>
            </div>
          </div>

          {/* Rating Tiers Bar Chart */}
          <div className="mt-6">
            <p className="text-xs font-semibold text-slate-700 mb-2">Rating Tier Distribution</p>
            <div className="h-36">
              {ratingDist.length > 0 ? (
                <Bar
                  data={ratingChartData}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { display: false } },
                    scales: {
                      y: { beginAtZero: true, grid: { color: '#F1F5F9' } },
                      x: { grid: { display: false } }
                    }
                  }}
                />
              ) : (
                <div className="flex h-full items-center justify-center text-xs text-slate-400">
                  No evaluation tiers calculated
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Operational Governance & Workflow Health */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <div className="p-2 rounded-lg bg-indigo-50 text-indigo-600">
                  <Activity className="h-5 w-5" />
                </div>
                <div>
                  <h2 className="text-base font-bold text-slate-900">Operational Governance</h2>
                  <p className="text-xs text-slate-500">CAP & Workflow SLA metrics</p>
                </div>
              </div>
            </div>

            <div className="mt-5 space-y-3.5">
              <div className="flex items-center justify-between rounded-xl bg-slate-50 p-3">
                <div className="flex items-center gap-3">
                  <div className="p-2 rounded-lg bg-blue-100 text-blue-700">
                    <CheckCircle2 className="h-4 w-4" />
                  </div>
                  <div>
                    <p className="text-xs font-medium text-slate-600">Open CAPs</p>
                    <p className="text-sm font-bold text-slate-900">
                      {operationalOverview.openImprovementActions ?? 0}
                    </p>
                  </div>
                </div>
                <span className="text-xs font-semibold text-amber-600">
                  {operationalOverview.overdueImprovementActions ?? 0} overdue
                </span>
              </div>

              <div className="flex items-center justify-between rounded-xl bg-slate-50 p-3">
                <div className="flex items-center gap-3">
                  <div className="p-2 rounded-lg bg-indigo-100 text-indigo-700">
                    <Clock className="h-4 w-4" />
                  </div>
                  <div>
                    <p className="text-xs font-medium text-slate-600">Pending Approvals</p>
                    <p className="text-sm font-bold text-slate-900">
                      {operationalOverview.pendingWorkflowApprovals ?? 0}
                    </p>
                  </div>
                </div>
                <span className="text-xs font-semibold text-rose-600">
                  {operationalOverview.escalatedWorkflows ?? 0} escalated
                </span>
              </div>

              <div className="rounded-xl border border-slate-100 bg-gradient-to-br from-indigo-50/50 to-blue-50/50 p-3.5">
                <div className="flex justify-between items-center mb-1.5">
                  <span className="text-xs font-semibold text-slate-700">SLA Compliance Rate</span>
                  <span className="text-xs font-bold text-indigo-700">
                    {operationalOverview.workflowSlaComplianceRate != null
                      ? `${operationalOverview.workflowSlaComplianceRate.toFixed(1)}%`
                      : 'N/A'}
                  </span>
                </div>
                <div className="h-2 w-full rounded-full bg-slate-200 overflow-hidden">
                  <div
                    className="h-full bg-indigo-600 rounded-full transition-all duration-500"
                    style={{
                      width: `${Math.min(100, operationalOverview.workflowSlaComplianceRate || 0)}%`
                    }}
                  />
                </div>
              </div>
            </div>
          </div>

          <div className="mt-4 pt-4 border-t border-slate-100 flex items-center justify-between">
            <Link
              to="/workflows"
              className="text-xs font-semibold text-blue-600 hover:text-blue-800 flex items-center gap-1"
            >
              View Active Workflows <ChevronRight className="h-3.5 w-3.5" />
            </Link>
            <Link
              to="/improvement-actions"
              className="text-xs font-semibold text-blue-600 hover:text-blue-800 flex items-center gap-1"
            >
              View CAP Tasks <ChevronRight className="h-3.5 w-3.5" />
            </Link>
          </div>
        </div>
      </div>

      {/* Performer Lists: Top vs Bottom */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Top Suppliers */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <Award className="h-5 w-5 text-emerald-600" />
              <h3 className="text-base font-bold text-slate-900">Top Performing Suppliers</h3>
            </div>
            <span className="text-xs font-medium text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded-md">
              Score ≥ 85
            </span>
          </div>

          <div className="divide-y divide-slate-100">
            {performanceOverview.topSuppliers && performanceOverview.topSuppliers.length > 0 ? (
              performanceOverview.topSuppliers.slice(0, 5).map((sup, idx) => (
                <div key={idx} className="py-3 flex items-center justify-between hover:bg-slate-50 px-2 rounded-lg transition">
                  <div className="flex items-center gap-3">
                    <span className="h-6 w-6 rounded-full bg-emerald-100 text-emerald-700 text-xs font-bold flex items-center justify-center">
                      #{idx + 1}
                    </span>
                    <div>
                      <p className="text-sm font-semibold text-slate-900">{sup.supplierName}</p>
                      <p className="text-xs text-slate-500">{sup.category || 'General'}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="text-sm font-bold text-emerald-600">
                      {sup.score != null ? sup.score.toFixed(1) : 'N/A'}
                    </span>
                    <p className="text-[10px] text-slate-400 font-medium">Grade {sup.ratingGrade || 'A'}</p>
                  </div>
                </div>
              ))
            ) : (
              <p className="py-6 text-center text-xs text-slate-400">No top performers recorded</p>
            )}
          </div>
        </div>

        {/* Bottom Suppliers / Focus Needed */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <AlertTriangle className="h-5 w-5 text-rose-600" />
              <h3 className="text-base font-bold text-slate-900">Vendors Requiring Attention</h3>
            </div>
            <span className="text-xs font-medium text-rose-600 bg-rose-50 px-2 py-0.5 rounded-md">
              Score &lt; 65
            </span>
          </div>

          <div className="divide-y divide-slate-100">
            {performanceOverview.bottomSuppliers && performanceOverview.bottomSuppliers.length > 0 ? (
              performanceOverview.bottomSuppliers.slice(0, 5).map((sup, idx) => (
                <div key={idx} className="py-3 flex items-center justify-between hover:bg-slate-50 px-2 rounded-lg transition">
                  <div className="flex items-center gap-3">
                    <span className="h-6 w-6 rounded-full bg-rose-100 text-rose-700 text-xs font-bold flex items-center justify-center">
                      !
                    </span>
                    <div>
                      <p className="text-sm font-semibold text-slate-900">{sup.supplierName}</p>
                      <p className="text-xs text-slate-500">{sup.category || 'General'}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="text-sm font-bold text-rose-600">
                      {sup.score != null ? sup.score.toFixed(1) : 'N/A'}
                    </span>
                    <p className="text-[10px] text-slate-400 font-medium">Grade {sup.ratingGrade || 'D'}</p>
                  </div>
                </div>
              ))
            ) : (
              <p className="py-6 text-center text-xs text-slate-400">No critically low performers</p>
            )}
          </div>
        </div>
      </div>

      {/* Strategic AI Insights & Recommendations */}
      <div className="rounded-2xl border border-slate-200 bg-white p-6 sm:p-8 shadow-sm">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-xl bg-gradient-to-br from-indigo-500 to-purple-600 text-white shadow-md shadow-purple-500/20">
              <Sparkles className="h-6 w-6" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-slate-900">Strategic AI Recommendations & Executive Action Plan</h2>
              <p className="text-xs text-slate-500">Automated intelligence synthesis for executive decision making</p>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {strategicInsights && strategicInsights.length > 0 ? (
            strategicInsights.map((insight, idx) => {
              const isRisk = insight.type === 'RISK' || insight.type === 'ALERT';
              const isOpp = insight.type === 'OPPORTUNITY';
              return (
                <div
                  key={idx}
                  className={`rounded-xl border p-5 transition flex flex-col justify-between ${
                    isRisk
                      ? 'border-rose-200 bg-rose-50/40 hover:bg-rose-50/70'
                      : isOpp
                      ? 'border-emerald-200 bg-emerald-50/40 hover:bg-emerald-50/70'
                      : 'border-blue-200 bg-blue-50/40 hover:bg-blue-50/70'
                  }`}
                >
                  <div>
                    <div className="flex items-center justify-between mb-2.5">
                      <span
                        className={`text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-md ${
                          isRisk
                            ? 'bg-rose-100 text-rose-700'
                            : isOpp
                            ? 'bg-emerald-100 text-emerald-700'
                            : 'bg-blue-100 text-blue-700'
                        }`}
                      >
                        {insight.type || 'RECOMMENDATION'}
                      </span>
                    </div>
                    <h4 className="text-sm font-bold text-slate-900">{insight.title}</h4>
                    <p className="mt-1.5 text-xs text-slate-600 leading-relaxed">{insight.description}</p>
                  </div>

                  {insight.actionRecommendation && (
                    <div className="mt-4 pt-3 border-t border-slate-200/60">
                      <p className="text-[11px] font-semibold text-slate-700 flex items-center gap-1.5">
                        <ArrowUpRight className="h-3.5 w-3.5 text-indigo-600" />
                        Executive Action:
                      </p>
                      <p className="mt-1 text-xs text-slate-800 font-medium">{insight.actionRecommendation}</p>
                    </div>
                  )}
                </div>
              );
            })
          ) : (
            <div className="col-span-full py-8 text-center text-sm text-slate-500">
              No strategic risk anomalies detected in current active period.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default ExecutiveDashboard;