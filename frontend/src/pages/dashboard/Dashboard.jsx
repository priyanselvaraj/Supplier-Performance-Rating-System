import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { dashboardService } from '../../services/dashboard.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import {
  Building2,
  Award,
  AlertTriangle,
  FileCheck,
  TrendingUp,
  PlusCircle,
  ArrowUpRight,
  ShieldAlert,
  Users
} from 'lucide-react';
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

ChartJS.register(
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  Title
);

export const Dashboard = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchDashboard = async () => {
    setLoading(true);
    try {
      const res = await dashboardService.getSummary();
      if (res.success) {
        setData(res.data);
      }
    } catch (err) {
      console.error(err);
      setError('Failed to load dashboard metrics');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  if (loading) {
    return (
      <div className="py-20 text-center text-slate-500">
        <div className="inline-block animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600 mb-3"></div>
        <p className="text-sm font-medium">Loading Dashboard Analytics...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-6 bg-rose-50 border border-rose-200 text-rose-700 rounded-xl text-center">
        <p>{error}</p>
        <Button variant="outline" size="sm" onClick={fetchDashboard} className="mt-3">
          Try Again
        </Button>
      </div>
    );
  }

  // Chart data configuration
  const doughnutData = {
    labels: ['Excellent (>=85%)', 'Good (70-84%)', 'Average (50-69%)', 'Poor (<50%)', 'Unrated'],
    datasets: [
      {
        data: [
          data?.excellentSuppliersCount || 0,
          data?.goodSuppliersCount || 0,
          data?.averageSuppliersCount || 0,
          data?.poorSuppliersCount || 0,
          data?.unratedSuppliersCount || 0,
        ],
        backgroundColor: [
          '#10b981', // emerald
          '#3b82f6', // blue
          '#f59e0b', // amber
          '#ef4444', // red
          '#94a3b8', // slate
        ],
        borderWidth: 2,
        borderColor: '#ffffff',
      },
    ],
  };

  // Safe array extractions
  const topPerformers = Array.isArray(data?.topPerformingSuppliers) ? data.topPerformingSuppliers : [];
  const lowPerformers = Array.isArray(data?.lowPerformingSuppliersList)
    ? data.lowPerformingSuppliersList
    : Array.isArray(data?.lowPerformingSuppliers)
      ? data.lowPerformingSuppliers
      : [];
  const recentEvals = Array.isArray(data?.recentEvaluations) ? data.recentEvaluations : [];

  return (
    <div className="space-y-6">
      {/* Header with Quick Actions */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Executive Dashboard</h1>
          <p className="text-sm text-slate-500 mt-1">
            Real-time overview of supplier health, ratings, and performance benchmarks.
          </p>
        </div>
        <div className="flex items-center gap-2.5">
          <Link to="/evaluations/new">
            <Button variant="primary" icon={PlusCircle}>
              Evaluate Supplier
            </Button>
          </Link>
          <Link to="/reports">
            <Button variant="secondary" icon={TrendingUp}>
              Full Reports
            </Button>
          </Link>
        </div>
      </div>

      {/* KPI Stats Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <Card className="hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Total Suppliers</p>
              <h3 className="text-2xl font-bold text-slate-900 mt-1">{data?.totalSuppliers || 0}</h3>
              <p className="text-xs text-emerald-600 font-medium mt-1">
                {data?.activeSuppliers || 0} Active ({Math.round(((data?.activeSuppliers || 0) / (data?.totalSuppliers || 1)) * 100)}%)
              </p>
            </div>
            <div className="h-12 w-12 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
              <Building2 className="h-6 w-6" />
            </div>
          </div>
        </Card>

        <Card className="hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Avg System Score</p>
              <h3 className="text-2xl font-bold text-slate-900 mt-1">
                {data?.averageSupplierRating ? `${data.averageSupplierRating}%` : 'N/A'}
              </h3>
              <p className="text-xs text-slate-500 mt-1">Weighted benchmark</p>
            </div>
            <div className="h-12 w-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
              <TrendingUp className="h-6 w-6" />
            </div>
          </div>
        </Card>

        <Card className="hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Top Rated</p>
              <h3 className="text-2xl font-bold text-emerald-600 mt-1">
                {data?.excellentSuppliersCount || 0}
              </h3>
              <p className="text-xs text-slate-500 mt-1">Rated Excellent (≥85%)</p>
            </div>
            <div className="h-12 w-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
              <Award className="h-6 w-6" />
            </div>
          </div>
        </Card>

        <Card className="hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Needs Attention</p>
              <h3 className="text-2xl font-bold text-rose-600 mt-1">
                {data?.poorSuppliersCount || 0}
              </h3>
              <p className="text-xs text-rose-600/80 font-medium mt-1">Rated Poor (&lt;50%)</p>
            </div>
            <div className="h-12 w-12 rounded-xl bg-rose-50 text-rose-600 flex items-center justify-center">
              <ShieldAlert className="h-6 w-6" />
            </div>
          </div>
        </Card>
      </div>

      {/* Visual Analytics & Distribution */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Rating Breakdown Chart */}
        <Card title="Performance Tier Distribution" subtitle="Suppliers categorized by overall weighted score">
          <div className="h-64 flex items-center justify-center pt-2">
            <Doughnut
              data={doughnutData}
              options={{
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                  legend: {
                    position: 'bottom',
                    labels: { boxWidth: 12, font: { size: 11 } },
                  },
                },
              }}
            />
          </div>
        </Card>

        {/* Top Performing Suppliers */}
        <Card
          title="Top Performing Suppliers"
          subtitle="Highest composite score"
          action={
            <Link to="/suppliers" className="text-xs font-semibold text-blue-600 hover:text-blue-700">
              View All
            </Link>
          }
        >
          {topPerformers.length === 0 ? (
            <p className="text-xs text-slate-400 py-8 text-center">No evaluated suppliers yet.</p>
          ) : (
            <div className="divide-y divide-slate-100">
              {topPerformers.map((sup) => (
                <div key={sup.id} className="py-3 flex items-center justify-between">
                  <div className="min-w-0 pr-3">
                    <Link to={`/suppliers/${sup.id}`} className="text-sm font-semibold text-slate-800 hover:text-blue-600 truncate block">
                      {sup.name}
                    </Link>
                    <span className="text-xs text-slate-500">{sup.category?.name || 'General'}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-bold text-emerald-600">{sup.overallRating}%</span>
                    <Badge variant={sup.ratingCategory} size="xs" />
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>

        {/* Low Performing Suppliers */}
        <Card
          title="Low Performing Suppliers"
          subtitle="Requires improvement plan"
          action={
            <Link to="/reports" className="text-xs font-semibold text-blue-600 hover:text-blue-700">
              Analysis
            </Link>
          }
        >
          {lowPerformers.length === 0 ? (
            <p className="text-xs text-slate-400 py-8 text-center">No low performing suppliers flagged.</p>
          ) : (
            <div className="divide-y divide-slate-100">
              {lowPerformers.map((sup) => (
                <div key={sup.id} className="py-3 flex items-center justify-between">
                  <div className="min-w-0 pr-3">
                    <Link to={`/suppliers/${sup.id}`} className="text-sm font-semibold text-slate-800 hover:text-blue-600 truncate block">
                      {sup.name}
                    </Link>
                    <span className="text-xs text-slate-500">{sup.category?.name || 'General'}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-bold text-rose-600">{sup.overallRating}%</span>
                    <Badge variant={sup.ratingCategory} size="xs" />
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>

      {/* Recent Evaluations Table */}
      <Card
        title="Recent Supplier Evaluations"
        subtitle="Latest scoring submissions and ratings"
        action={
          <Link to="/evaluations" className="text-xs font-semibold text-blue-600 hover:text-blue-700">
            View History
          </Link>
        }
      >
        {recentEvals.length === 0 ? (
          <p className="text-xs text-slate-400 py-8 text-center">No evaluations submitted yet.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-slate-200 text-left text-sm">
              <thead className="bg-slate-50/75 text-xs uppercase font-semibold text-slate-600">
                <tr>
                  <th className="px-4 py-3">Code</th>
                  <th className="px-4 py-3">Supplier</th>
                  <th className="px-4 py-3">Evaluator</th>
                  <th className="px-4 py-3">Date</th>
                  <th className="px-4 py-3">Period</th>
                  <th className="px-4 py-3">Score</th>
                  <th className="px-4 py-3">Rating</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 bg-white">
                {recentEvals.map((ev) => (
                  <tr key={ev.id} className="hover:bg-slate-50">
                    <td className="px-4 py-3 font-mono text-xs font-semibold text-blue-600">
                      <Link to={`/evaluations/${ev.id}`}>{ev.evaluationCode}</Link>
                    </td>
                    <td className="px-4 py-3 font-medium text-slate-800">{ev.supplierName}</td>
                    <td className="px-4 py-3 text-slate-600 text-xs">{ev.evaluatorName}</td>
                    <td className="px-4 py-3 text-slate-500 text-xs">{ev.evaluationDate}</td>
                    <td className="px-4 py-3 text-slate-500 text-xs">{ev.evaluationPeriod || 'N/A'}</td>
                    <td className="px-4 py-3 font-bold text-slate-900">{ev.totalWeightedScore}%</td>
                    <td className="px-4 py-3">
                      <Badge variant={ev.ratingCategory} size="sm" />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  );
};
