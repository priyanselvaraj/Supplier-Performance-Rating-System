import React, { useState, useEffect } from 'react';
import { dashboardService } from '../../services/dashboard.service';
import { Card } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { Table } from '../../components/common/Table';
import { Link } from 'react-router-dom';
import {
  TrendingUp,
  BarChart3,
  PieChart,
  Calendar,
  Building2,
  CheckCircle2,
  AlertTriangle,
  RefreshCw,
  Award
} from 'lucide-react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { Bar, Pie, Line, Doughnut } from 'react-chartjs-2';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
);

export const AnalyticsPage = () => {
  const [loading, setLoading] = useState(true);
  const [performance, setPerformance] = useState(null);
  const [ratingDist, setRatingDist] = useState([]);
  const [statusDist, setStatusDist] = useState([]);
  const [categoryDist, setCategoryDist] = useState([]);
  const [trends, setTrends] = useState([]);
  const [topSuppliers, setTopSuppliers] = useState([]);
  const [lowSuppliers, setLowSuppliers] = useState([]);

  // Date filter
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [trendGroup, setTrendGroup] = useState('MONTH');

  const loadAnalytics = async () => {
    setLoading(true);
    try {
      const [
        perfRes,
        rateRes,
        statRes,
        catRes,
        trendRes,
        topRes,
        lowRes
      ] = await Promise.all([
        dashboardService.getPerformance(startDate || null, endDate || null),
        dashboardService.getRatingDistribution(),
        dashboardService.getPerformanceStatusDistribution(),
        dashboardService.getSuppliersByCategory(),
        dashboardService.getOverallPerformanceTrend(trendGroup),
        dashboardService.getTopSuppliers(5),
        dashboardService.getLowSuppliers(5),
      ]);

      if (perfRes.success) setPerformance(perfRes.data);
      if (rateRes.success) setRatingDist(rateRes.data);
      if (statRes.success) setStatusDist(statRes.data);
      if (catRes.success) setCategoryDist(catRes.data);
      if (trendRes.success) setTrends(trendRes.data);
      if (topRes.success) setTopSuppliers(topRes.data);
      if (lowRes.success) setLowSuppliers(lowRes.data);
    } catch (err) {
      console.error('Failed to load analytics:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAnalytics();
  }, [trendGroup]);

  // 1. Rating Distribution Chart
  const ratingLabels = ratingDist.map(r => r.ratingDisplayName || r.rating);
  const ratingCounts = ratingDist.map(r => r.count);
  const ratingColors = ['#10b981', '#3b82f6', '#06b6d4', '#f59e0b', '#ef4444'];

  const ratingBarData = {
    labels: ratingLabels,
    datasets: [
      {
        label: 'Suppliers Count',
        data: ratingCounts,
        backgroundColor: ratingColors,
        borderRadius: 6,
      },
    ],
  };

  // 2. Status Distribution (Doughnut)
  const statusLabels = statusDist.map(s => s.statusDisplayName || s.status);
  const statusCounts = statusDist.map(s => s.count);
  const statusDoughnutData = {
    labels: statusLabels,
    datasets: [
      {
        data: statusCounts,
        backgroundColor: ['#10b981', '#3b82f6', '#f59e0b', '#ef4444'],
        borderWidth: 2,
        borderColor: '#ffffff',
      },
    ],
  };

  // 3. Category Distribution (Bar)
  const categoryLabels = categoryDist.map(c => c.categoryName);
  const categoryCounts = categoryDist.map(c => c.supplierCount);
  const categoryBarData = {
    labels: categoryLabels,
    datasets: [
      {
        label: 'Suppliers in Category',
        data: categoryCounts,
        backgroundColor: '#6366f1',
        borderRadius: 6,
      },
    ],
  };

  // 4. Trend Line Data
  const trendLabels = trends.map(t => t.period);
  const trendScores = trends.map(t => t.averageScore);
  const trendLineData = {
    labels: trendLabels,
    datasets: [
      {
        label: 'Average Score',
        data: trendScores,
        borderColor: '#2563eb',
        backgroundColor: 'rgba(37, 99, 235, 0.1)',
        fill: true,
        tension: 0.3,
        pointRadius: 5,
      },
    ],
  };

  return (
    <div className="space-y-6">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2.5">
            <TrendingUp className="h-6 w-6 text-blue-600" />
            Performance Analytics &amp; Visualizations
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            Deep statistical breakdown of supplier ratings, category representations, and historical score trajectories.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="secondary" icon={RefreshCw} onClick={loadAnalytics}>
            Refresh
          </Button>
        </div>
      </div>

      {/* Date Filter Bar */}
      <Card bodyClassName="p-4">
        <div className="flex flex-wrap items-center gap-3">
          <div className="flex items-center gap-2 text-xs font-semibold text-slate-700">
            <Calendar className="h-4 w-4 text-blue-600" />
            <span>Filter Period:</span>
          </div>
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="px-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <span className="text-xs text-slate-400">to</span>
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="px-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <Button size="sm" variant="primary" onClick={loadAnalytics}>
            Apply Filter
          </Button>
        </div>
      </Card>

      {/* KPI Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card>
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Benchmark Average Score</p>
          <h3 className="text-3xl font-extrabold text-blue-600 mt-1">
            {performance?.averageScore != null ? performance.averageScore.toFixed(1) : '0.0'}%
          </h3>
          <p className="text-xs text-slate-500 mt-1">Total Rated Vendors: {performance?.totalRatedSuppliers || 0}</p>
        </Card>

        <Card>
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Highest Recorded Score</p>
          <h3 className="text-3xl font-extrabold text-emerald-600 mt-1">
            {performance?.highestScore != null ? performance.highestScore.toFixed(1) : 'N/A'}
          </h3>
          <p className="text-xs text-slate-500 mt-1">Top tier ceiling</p>
        </Card>

        <Card>
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Lowest Recorded Score</p>
          <h3 className="text-3xl font-extrabold text-amber-600 mt-1">
            {performance?.lowestScore != null ? performance.lowestScore.toFixed(1) : 'N/A'}
          </h3>
          <p className="text-xs text-slate-500 mt-1">Underperforming baseline</p>
        </Card>

        <Card>
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Evaluated Suppliers</p>
          <h3 className="text-3xl font-extrabold text-slate-900 mt-1">
            {performance?.totalRatedSuppliers || 0}
          </h3>
          <p className="text-xs text-slate-500 mt-1">With completed audits</p>
        </Card>
      </div>

      {/* Chart Grid Row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Rating Classification Distribution */}
        <Card title="Rating Classification Distribution" subtitle="Suppliers grouped by Phase 5 tier standards">
          <div className="h-64 pt-2">
            <Bar
              data={ratingBarData}
              options={{
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } },
              }}
            />
          </div>
        </Card>

        {/* Operational Status Breakdown */}
        <Card title="Operational Performance Breakdown" subtitle="Vendor operational readiness status">
          <div className="h-64 pt-2 flex items-center justify-center">
            {statusDist.length > 0 ? (
              <Doughnut
                data={statusDoughnutData}
                options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  plugins: { legend: { position: 'right' } },
                }}
              />
            ) : (
              <p className="text-xs text-slate-400">No status data available</p>
            )}
          </div>
        </Card>
      </div>

      {/* Chart Grid Row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Category Representation */}
        <Card title="Suppliers by Category" subtitle="Distribution across procurement sectors">
          <div className="h-64 pt-2">
            <Bar
              data={categoryBarData}
              options={{
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } },
              }}
            />
          </div>
        </Card>

        {/* Overall Trend Timeline */}
        <Card
          title="Overall Performance Trend"
          subtitle="System-wide average score trajectory"
          headerAction={
            <div className="flex gap-1 bg-slate-100 p-1 rounded-lg">
              {['MONTH', 'QUARTER', 'YEAR'].map((g) => (
                <button
                  key={g}
                  onClick={() => setTrendGroup(g)}
                  className={`px-2.5 py-1 text-xs font-semibold rounded-md transition-all ${
                    trendGroup === g ? 'bg-white text-blue-600 shadow-xs' : 'text-slate-500 hover:text-slate-800'
                  }`}
                >
                  {g}
                </button>
              ))}
            </div>
          }
        >
          <div className="h-64 pt-2">
            {trends.length > 0 ? (
              <Line
                data={trendLineData}
                options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  scales: { y: { min: 0, max: 100 } },
                }}
              />
            ) : (
              <div className="h-full flex items-center justify-center text-xs text-slate-400">
                No historical trend data for this interval
              </div>
            )}
          </div>
        </Card>
      </div>

      {/* Top & Low Performer Lists */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title="Top Performing Suppliers" subtitle="Highest overall score ranking">
          <div className="divide-y divide-slate-100">
            {topSuppliers.map((s, idx) => (
              <div key={s.supplierId || idx} className="py-2.5 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <span className="font-bold text-xs text-slate-400">#{idx + 1}</span>
                  <div>
                    <Link to={`/suppliers/${s.supplierId}`} className="text-sm font-semibold text-slate-900 hover:text-blue-600">
                      {s.supplierName}
                    </Link>
                    <span className="text-xs text-slate-500 block font-mono">{s.supplierCode}</span>
                  </div>
                </div>
                <div className="text-right">
                  <span className="text-sm font-bold text-emerald-600">{s.latestScore?.toFixed(1)}</span>
                  <span className="text-xs text-slate-400 block">{s.ratingDisplayName}</span>
                </div>
              </div>
            ))}
          </div>
        </Card>

        <Card title="Suppliers Needing Improvement" subtitle="Vendors with lowest scores requiring review">
          <div className="divide-y divide-slate-100">
            {lowSuppliers.map((s, idx) => (
              <div key={s.supplierId || idx} className="py-2.5 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <span className="font-bold text-xs text-amber-500">#{idx + 1}</span>
                  <div>
                    <Link to={`/suppliers/${s.supplierId}`} className="text-sm font-semibold text-slate-900 hover:text-blue-600">
                      {s.supplierName}
                    </Link>
                    <span className="text-xs text-slate-500 block font-mono">{s.supplierCode}</span>
                  </div>
                </div>
                <div className="text-right">
                  <span className="text-sm font-bold text-rose-600">{s.latestScore?.toFixed(1)}</span>
                  <span className="text-xs text-slate-400 block">{s.ratingDisplayName}</span>
                </div>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </div>
  );
};
