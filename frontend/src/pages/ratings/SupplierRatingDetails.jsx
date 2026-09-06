import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ratingService } from '../../services/rating.service';
import { Card } from '../../components/common/Card';
import { Table } from '../../components/common/Table';
import { Button } from '../../components/common/Button';
import {
  Award,
  ArrowLeft,
  TrendingUp,
  TrendingDown,
  Minus,
  CheckCircle2,
  Calendar,
  Building2,
  FileText
} from 'lucide-react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js';
import { Line } from 'react-chartjs-2';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

export const SupplierRatingDetails = () => {
  const { supplierId } = useParams();
  const [summary, setSummary] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const [sumRes, histRes] = await Promise.all([
          ratingService.getSupplierPerformanceSummary(supplierId),
          ratingService.getSupplierRatingHistory(supplierId),
        ]);

        if (sumRes.success) setSummary(sumRes.data);
        if (histRes.success) setHistory(histRes.data);
      } catch (err) {
        console.error('Failed to load supplier rating details:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [supplierId]);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!summary) {
    return (
      <Card bodyClassName="p-8 text-center">
        <h3 className="text-lg font-bold text-slate-800">Supplier Rating Not Found</h3>
        <p className="text-sm text-slate-500 mt-2">No rating record was found for this supplier.</p>
        <Link to="/ratings" className="mt-4 inline-block">
          <Button variant="secondary" icon={ArrowLeft}>Back to Ratings</Button>
        </Link>
      </Card>
    );
  }

  // Prepare line chart data
  const reversedHistory = [...history].reverse();
  const chartLabels = reversedHistory.map(h => h.ratingDate || 'N/A');
  const chartScores = reversedHistory.map(h => h.score);

  const lineChartData = {
    labels: chartLabels,
    datasets: [
      {
        label: 'Performance Score',
        data: chartScores,
        borderColor: '#2563eb',
        backgroundColor: 'rgba(37, 99, 235, 0.1)',
        fill: true,
        tension: 0.3,
        pointBackgroundColor: '#2563eb',
        pointRadius: 5,
      },
    ],
  };

  const getTrendIcon = (trend) => {
    switch (trend) {
      case 'IMPROVING': return <TrendingUp className="h-5 w-5 text-emerald-500" />;
      case 'DECLINING': return <TrendingDown className="h-5 w-5 text-rose-500" />;
      default: return <Minus className="h-5 w-5 text-blue-500" />;
    }
  };

  const columns = [
    {
      header: 'Rating Date',
      accessor: 'ratingDate',
      render: (row) => <span className="font-semibold text-slate-900">{row.ratingDate}</span>,
    },
    {
      header: 'Score',
      accessor: 'score',
      render: (row) => (
        <span className="font-bold text-slate-900 text-sm">
          {row.score?.toFixed(1)} / 100
        </span>
      ),
    },
    {
      header: 'Rating Classification',
      accessor: 'rating',
      render: (row) => (
        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold ${
          row.rating === 'EXCELLENT' ? 'bg-emerald-100 text-emerald-800' :
          row.rating === 'VERY_GOOD' ? 'bg-blue-100 text-blue-800' :
          row.rating === 'GOOD' ? 'bg-cyan-100 text-cyan-800' :
          row.rating === 'AVERAGE' ? 'bg-amber-100 text-amber-800' :
          'bg-rose-100 text-rose-800'
        }`}>
          {row.ratingDisplayName || row.rating}
        </span>
      ),
    },
    {
      header: 'Performance Status',
      accessor: 'performanceStatus',
      render: (row) => (
        <span className="text-xs font-medium text-slate-700 bg-slate-100 px-2.5 py-1 rounded">
          {row.performanceStatusDisplayName || row.performanceStatus}
        </span>
      ),
    },
    {
      header: 'Evaluation Record',
      accessor: 'evaluationCode',
      render: (row) => row.evaluationId ? (
        <Link to={`/evaluations/${row.evaluationId}`} className="text-xs font-mono text-blue-600 hover:underline">
          {row.evaluationCode || `EV-${row.evaluationId}`}
        </Link>
      ) : (
        <span className="text-xs text-slate-400">Direct Entry</span>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div className="flex items-center gap-3">
          <Link to="/ratings">
            <Button variant="secondary" size="sm" icon={ArrowLeft}>
              Back
            </Button>
          </Link>
          <div>
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
              {summary.supplierName}
              <span className="text-xs font-mono font-normal text-slate-500 bg-slate-100 px-2 py-0.5 rounded">
                {summary.supplierCode}
              </span>
            </h1>
            <p className="text-sm text-slate-500 mt-0.5">
              Category: {summary.categoryName || 'General'} • Status: {summary.active ? 'ACTIVE' : 'INACTIVE'}
            </p>
          </div>
        </div>

        <div className="flex gap-2">
          <Link to={`/reports`}>
            <Button variant="primary" icon={FileText}>
              Export Reports
            </Button>
          </Link>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card>
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Latest Score</p>
          <div className="flex items-baseline gap-2 mt-1">
            <span className="text-3xl font-extrabold text-blue-600">{summary.latestScore?.toFixed(1)}</span>
            <span className="text-xs text-slate-400">/ 100</span>
          </div>
          <p className="text-xs text-slate-500 mt-1">Classification: {summary.latestRatingDisplayName}</p>
        </Card>

        <Card>
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Performance Trend</p>
          <div className="flex items-center gap-2 mt-1">
            {getTrendIcon(summary.performanceTrend)}
            <span className="text-xl font-bold text-slate-800">{summary.performanceTrendDisplayName}</span>
          </div>
          <p className="text-xs text-slate-500 mt-1">
            {summary.scoreDifference != null
              ? `${summary.scoreDifference >= 0 ? '+' : ''}${summary.scoreDifference.toFixed(1)} pts vs prior eval`
              : 'First evaluation'}
          </p>
        </Card>

        <Card>
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Performance Status</p>
          <div className="mt-1">
            <span className="text-lg font-bold text-slate-900">{summary.performanceStatusDisplayName}</span>
          </div>
          <p className="text-xs text-slate-500 mt-1">Operational Rating Level</p>
        </Card>

        <Card>
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Total Evaluations</p>
          <div className="mt-1">
            <span className="text-3xl font-extrabold text-slate-900">{summary.totalEvaluations}</span>
          </div>
          <p className="text-xs text-slate-500 mt-1">Last rated: {summary.latestRatingDate || 'N/A'}</p>
        </Card>
      </div>

      {/* Performance Progression Chart */}
      {history.length > 1 && (
        <Card title="Score Progression Over Time" subtitle="Chronological rating milestones">
          <div className="h-64 pt-2">
            <Line
              data={lineChartData}
              options={{
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                  y: { min: 0, max: 100 },
                },
              }}
            />
          </div>
        </Card>
      )}

      {/* History Table */}
      <Card title="Historical Rating Scorecards" bodyClassName="p-0">
        <Table columns={columns} data={history} emptyMessage="No rating history records found." />
      </Card>
    </div>
  );
};
