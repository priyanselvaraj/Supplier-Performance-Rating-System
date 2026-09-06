import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { biService } from '../../services/bi.service';
import { supplierService } from '../../services/supplier.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import {
  Sliders,
  TrendingUp,
  TrendingDown,
  Award,
  AlertTriangle,
  CheckCircle2,
  RefreshCw,
  ArrowRight,
  ShieldCheck,
  ChevronRight,
  Target,
  Zap,
  Layers,
  HelpCircle
} from 'lucide-react';
import {
  Chart as ChartJS,
  RadialLinearScale,
  PointElement,
  LineElement,
  Filler,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  Title
} from 'chart.js';
import { Radar, Bar } from 'react-chartjs-2';

ChartJS.register(
  RadialLinearScale,
  PointElement,
  LineElement,
  Filler,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  Title
);

export const PerformanceBenchmarking = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [suppliersList, setSuppliersList] = useState([]);
  const [selectedSupplierId, setSelectedSupplierId] = useState('');
  const [benchmarkData, setBenchmarkData] = useState(null);
  const [loadingSuppliers, setLoadingSuppliers] = useState(true);
  const [loadingBenchmark, setLoadingBenchmark] = useState(false);
  const [error, setError] = useState('');
  const [chartMode, setChartMode] = useState('radar'); // 'radar' | 'bar'

  // Load suppliers for dropdown
  useEffect(() => {
    const loadSuppliers = async () => {
      try {
        const res = await supplierService.getAllSuppliers({ size: 100 });
        const list = res.data?.content || res.data || [];
        setSuppliersList(list);

        const urlId = searchParams.get('supplierId');
        if (urlId && list.some(s => s.id === parseInt(urlId, 10))) {
          setSelectedSupplierId(urlId);
        } else if (list.length > 0) {
          setSelectedSupplierId(String(list[0].id));
        }
      } catch (err) {
        console.error('Failed to load supplier list:', err);
        setError('Failed to load supplier list.');
      } finally {
        setLoadingSuppliers(false);
      }
    };
    loadSuppliers();
  }, []);

  const fetchBenchmark = async (supplierId) => {
    if (!supplierId) return;
    setLoadingBenchmark(true);
    setError('');
    try {
      const res = await biService.getSupplierBenchmark(supplierId);
      const payload = res?.data !== undefined ? res.data : res;
      if (res?.success !== false && payload) {
        setBenchmarkData(payload);
        setSearchParams({ supplierId });
      } else {
        setError(res?.message || 'Failed to load supplier benchmark.');
      }
    } catch (err) {
      console.error('Error fetching benchmark:', err);
      setError('An error occurred while loading benchmark analytics.');
    } finally {
      setLoadingBenchmark(false);
    }
  };

  useEffect(() => {
    if (selectedSupplierId) {
      fetchBenchmark(selectedSupplierId);
    }
  }, [selectedSupplierId]);

  const handleSupplierChange = (e) => {
    const id = e.target.value;
    setSelectedSupplierId(id);
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'EXCEEDING':
        return <Badge variant="success" size="xs">Exceeding (+)</Badge>;
      case 'BELOW_BENCHMARK':
        return <Badge variant="danger" size="xs">Below Benchmark</Badge>;
      case 'AT_PAR':
      default:
        return <Badge variant="info" size="xs">At Par</Badge>;
    }
  };

  // Radar Chart Data
  const criteriaLabels = benchmarkData?.criteriaBenchmarks?.map(c => c.criteriaName) || [];
  const supplierScores = benchmarkData?.criteriaBenchmarks?.map(c => c.supplierScore || 0) || [];
  const categoryAvgs = benchmarkData?.criteriaBenchmarks?.map(c => c.categoryAverage || 0) || [];
  const overallAvgs = benchmarkData?.criteriaBenchmarks?.map(c => c.overallAverage || 0) || [];

  const radarData = {
    labels: criteriaLabels,
    datasets: [
      {
        label: `${benchmarkData?.supplierName || 'Supplier'} Score`,
        data: supplierScores,
        backgroundColor: 'rgba(99, 102, 241, 0.25)',
        borderColor: '#6366f1',
        pointBackgroundColor: '#6366f1',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: '#6366f1',
        borderWidth: 2,
      },
      {
        label: `${benchmarkData?.categoryName || 'Category'} Average`,
        data: categoryAvgs,
        backgroundColor: 'rgba(16, 185, 129, 0.15)',
        borderColor: '#10b981',
        pointBackgroundColor: '#10b981',
        borderDash: [4, 4],
        borderWidth: 2,
      },
      {
        label: 'Overall Company Average',
        data: overallAvgs,
        backgroundColor: 'rgba(148, 163, 184, 0.1)',
        borderColor: '#94a3b8',
        pointBackgroundColor: '#94a3b8',
        borderDash: [2, 2],
        borderWidth: 1.5,
      },
    ],
  };

  const barData = {
    labels: criteriaLabels,
    datasets: [
      {
        label: 'Supplier Score',
        data: supplierScores,
        backgroundColor: '#6366f1',
        borderRadius: 4,
      },
      {
        label: 'Category Average',
        data: categoryAvgs,
        backgroundColor: '#10b981',
        borderRadius: 4,
      },
      {
        label: 'Overall Average',
        data: overallAvgs,
        backgroundColor: '#94a3b8',
        borderRadius: 4,
      },
    ],
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1.5">
              <span className="px-2.5 py-0.5 text-xs font-semibold bg-purple-50 text-purple-700 rounded-full border border-purple-200 flex items-center gap-1">
                <Sliders className="h-3 w-3" /> Industry & Category Benchmarking
              </span>
              <span className="text-xs text-slate-400">• Percentile Rank Analysis</span>
            </div>
            <h1 className="text-2xl font-bold text-slate-800">Performance Benchmarking</h1>
            <p className="text-slate-500 text-sm mt-1">
              Evaluate vendor positioning against peers within the same category and historical enterprise baselines.
            </p>
          </div>

          {/* Supplier Selector */}
          <div className="flex items-center gap-3">
            <label className="text-xs font-semibold text-slate-700 whitespace-nowrap">Select Supplier:</label>
            <select
              value={selectedSupplierId}
              onChange={handleSupplierChange}
              disabled={loadingSuppliers || loadingBenchmark}
              className="px-3 py-2 bg-slate-50 border border-slate-300 rounded-xl text-xs font-medium text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:outline-none min-w-[220px]"
            >
              {suppliersList.map(s => (
                <option key={s.id} value={s.id}>
                  {s.name} ({s.supplierCode})
                </option>
              ))}
            </select>
            <Button
              variant="outline"
              onClick={() => fetchBenchmark(selectedSupplierId)}
              disabled={loadingBenchmark}
              className="text-xs p-2"
            >
              <RefreshCw className={`h-4 w-4 ${loadingBenchmark ? 'animate-spin' : ''}`} />
            </Button>
          </div>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-700 flex items-center gap-2">
          <AlertTriangle className="h-4 w-4 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {loadingBenchmark ? (
        <div className="py-20 text-center">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600 mx-auto mb-3"></div>
          <p className="text-xs text-slate-500">Calculating percentile distributions & criteria deltas...</p>
        </div>
      ) : benchmarkData ? (
        <>
          {/* Key Positioning Metric Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
              <p className="text-xs text-slate-500">Overall Supplier Score</p>
              <div className="flex items-baseline gap-2 mt-1">
                <span className="text-2xl font-bold text-slate-900">{benchmarkData.supplierScore}</span>
                <span className="text-xs text-slate-400">/ 100</span>
              </div>
              <p className="text-[11px] text-indigo-600 font-medium mt-1">
                {benchmarkData.categoryName} Category
              </p>
            </div>

            <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
              <p className="text-xs text-slate-500">Category Delta vs Peer Avg</p>
              <div className="flex items-baseline gap-2 mt-1">
                <span className={`text-2xl font-bold ${benchmarkData.categoryDelta >= 0 ? 'text-emerald-600' : 'text-rose-600'}`}>
                  {benchmarkData.categoryDelta >= 0 ? `+${benchmarkData.categoryDelta}` : benchmarkData.categoryDelta}
                </span>
                <span className="text-xs text-slate-400">pts</span>
              </div>
              <p className="text-[11px] text-slate-500 mt-1">
                Category Mean: <strong>{benchmarkData.categoryAverageScore}</strong>
              </p>
            </div>

            <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
              <p className="text-xs text-slate-500">Category Percentile Rank</p>
              <div className="flex items-baseline gap-2 mt-1">
                <span className="text-2xl font-bold text-purple-600">{benchmarkData.percentileRankInCategory}%</span>
                <span className="text-xs text-slate-400">Percentile</span>
              </div>
              <p className="text-[11px] text-slate-500 mt-1">
                Out of <strong>{benchmarkData.totalSuppliersInCategory}</strong> peers in category
              </p>
            </div>

            <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
              <p className="text-xs text-slate-500">Overall Company Percentile</p>
              <div className="flex items-baseline gap-2 mt-1">
                <span className="text-2xl font-bold text-indigo-600">{benchmarkData.percentileRankOverall}%</span>
                <span className="text-xs text-slate-400">Percentile</span>
              </div>
              <p className="text-[11px] text-slate-500 mt-1">
                Company-wide Mean: <strong>{benchmarkData.overallAverageScore}</strong>
              </p>
            </div>
          </div>

          {/* Benchmark Visualization & Strengths Grid */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Visual Multi-Criteria Chart */}
            <Card
              title="Multi-Dimensional Capability Radar"
              subtitle="Comparison of supplier score vs category and company averages"
              action={
                <div className="flex items-center gap-1 bg-slate-100 p-1 rounded-lg">
                  <button
                    onClick={() => setChartMode('radar')}
                    className={`px-2 py-0.5 text-xs font-semibold rounded ${
                      chartMode === 'radar' ? 'bg-white text-indigo-600 shadow-sm' : 'text-slate-600'
                    }`}
                  >
                    Radar
                  </button>
                  <button
                    onClick={() => setChartMode('bar')}
                    className={`px-2 py-0.5 text-xs font-semibold rounded ${
                      chartMode === 'bar' ? 'bg-white text-indigo-600 shadow-sm' : 'text-slate-600'
                    }`}
                  >
                    Bar
                  </button>
                </div>
              }
              className="lg:col-span-2"
            >
              <div className="h-80 flex items-center justify-center">
                {chartMode === 'radar' ? (
                  <Radar
                    data={radarData}
                    options={{
                      responsive: true,
                      maintainAspectRatio: false,
                      scales: {
                        r: {
                          min: 0,
                          max: 100,
                          ticks: { stepSize: 20, font: { size: 10 } },
                        },
                      },
                      plugins: {
                        legend: { position: 'bottom', labels: { boxWidth: 12 } },
                      },
                    }}
                  />
                ) : (
                  <Bar
                    data={barData}
                    options={{
                      responsive: true,
                      maintainAspectRatio: false,
                      scales: {
                        y: { min: 0, max: 100 },
                      },
                      plugins: {
                        legend: { position: 'bottom', labels: { boxWidth: 12 } },
                      },
                    }}
                  />
                )}
              </div>
            </Card>

            {/* Strengths & Gaps Insights */}
            <div className="space-y-4">
              {/* Strengths Card */}
              <Card title="Key Competitive Strengths" subtitle="Criteria exceeding category benchmark">
                <div className="space-y-2">
                  {benchmarkData.strengthAreas && benchmarkData.strengthAreas.length > 0 ? (
                    benchmarkData.strengthAreas.map((strength, idx) => (
                      <div key={idx} className="p-2.5 bg-emerald-50 border border-emerald-200 rounded-lg flex items-start gap-2 text-xs text-emerald-800">
                        <CheckCircle2 className="h-4 w-4 text-emerald-600 shrink-0 mt-0.5" />
                        <span className="font-medium">{strength}</span>
                      </div>
                    ))
                  ) : (
                    <p className="text-xs text-slate-400 py-3 text-center">No criteria substantially exceed category baseline.</p>
                  )}
                </div>
              </Card>

              {/* Performance Gaps Card */}
              <Card title="Improvement Gap Areas" subtitle="Criteria lagging behind category benchmark">
                <div className="space-y-2">
                  {benchmarkData.gapAreas && benchmarkData.gapAreas.length > 0 ? (
                    benchmarkData.gapAreas.map((gap, idx) => (
                      <div key={idx} className="p-2.5 bg-rose-50 border border-rose-200 rounded-lg flex items-start gap-2 text-xs text-rose-800">
                        <AlertTriangle className="h-4 w-4 text-rose-600 shrink-0 mt-0.5" />
                        <span className="font-medium">{gap}</span>
                      </div>
                    ))
                  ) : (
                    <p className="text-xs text-slate-400 py-3 text-center">No critical performance gaps detected against peers.</p>
                  )}
                </div>
              </Card>
            </div>
          </div>

          {/* Criteria Breakdown Benchmark Table */}
          <Card title="Detailed Criteria Benchmark Matrix" subtitle="Normalized score breakdown per evaluation criteria">
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm divide-y divide-slate-200">
                <thead className="bg-slate-50 text-slate-600 font-semibold text-xs uppercase tracking-wider">
                  <tr>
                    <th className="px-4 py-3">Evaluation Criteria</th>
                    <th className="px-4 py-3">Supplier Score</th>
                    <th className="px-4 py-3">Category Average</th>
                    <th className="px-4 py-3">Company Average</th>
                    <th className="px-4 py-3">Delta vs Category</th>
                    <th className="px-4 py-3">Benchmark Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 text-xs">
                  {benchmarkData.criteriaBenchmarks?.map((item, idx) => (
                    <tr key={idx} className="hover:bg-slate-50/50">
                      <td className="px-4 py-3 font-semibold text-slate-800">{item.criteriaName}</td>
                      <td className="px-4 py-3 font-bold text-slate-900">{item.supplierScore}</td>
                      <td className="px-4 py-3 text-slate-600">{item.categoryAverage}</td>
                      <td className="px-4 py-3 text-slate-500">{item.overallAverage}</td>
                      <td className="px-4 py-3 font-semibold">
                        <span className={item.deltaFromCategory >= 0 ? 'text-emerald-600' : 'text-rose-600'}>
                          {item.deltaFromCategory >= 0 ? `+${item.deltaFromCategory}` : item.deltaFromCategory} pts
                        </span>
                      </td>
                      <td className="px-4 py-3">{getStatusBadge(item.performanceStatus)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>
        </>
      ) : null}
    </div>
  );
};

export default PerformanceBenchmarking;
