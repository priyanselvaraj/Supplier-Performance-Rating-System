import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { biService } from '../../services/bi.service';
import { supplierService } from '../../services/supplier.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import {
  GitCompare,
  CheckSquare,
  Square,
  AlertTriangle,
  RefreshCw,
  Award,
  ShieldAlert,
  ArrowRight,
  TrendingUp,
  BarChart2,
  CheckCircle2,
  FileSpreadsheet,
  Layers,
  Info
} from 'lucide-react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Tooltip,
  Legend,
  Title
} from 'chart.js';
import { Bar } from 'react-chartjs-2';

ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip, Legend, Title);

export const SupplierComparison = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [suppliersList, setSuppliersList] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [comparisonData, setComparisonData] = useState(null);
  const [loadingSuppliers, setLoadingSuppliers] = useState(true);
  const [comparing, setComparing] = useState(false);
  const [error, setError] = useState('');

  // Load all available suppliers for selection
  useEffect(() => {
    const loadSuppliers = async () => {
      try {
        const res = await supplierService.getAllSuppliers({ size: 100 });
        const list = res.data?.content || res.data || [];
        setSuppliersList(list);

        // Pre-select from URL if present (e.g. ?ids=1,2)
        const urlIds = searchParams.get('ids');
        if (urlIds) {
          const parsed = urlIds.split(',').map(id => parseInt(id, 10)).filter(Boolean);
          if (parsed.length >= 2) {
            setSelectedIds(parsed);
          } else if (list.length >= 2) {
            setSelectedIds([list[0].id, list[1].id]);
          }
        } else if (list.length >= 2) {
          setSelectedIds([list[0].id, list[1].id]);
        }
      } catch (err) {
        console.error('Failed to load suppliers:', err);
        setError('Failed to load supplier list.');
      } finally {
        setLoadingSuppliers(false);
      }
    };
    loadSuppliers();
  }, []);

  // Run comparison when selectedIds has at least 2 suppliers
  const executeComparison = async () => {
    if (selectedIds.length < 2) {
      setError('Please select at least 2 suppliers to compare.');
      return;
    }
    setComparing(true);
    setError('');
    try {
      const res = await biService.compareSuppliers({ supplierIds: selectedIds });
      const payload = res?.data !== undefined ? res.data : res;
      if (res?.success !== false && payload) {
        setComparisonData(payload);
        setSearchParams({ ids: selectedIds.join(',') });
      } else {
        setError(res?.message || 'Comparison failed.');
      }
    } catch (err) {
      console.error('Error during comparison:', err);
      setError(err.response?.data?.message || 'Failed to compare selected suppliers.');
    } finally {
      setComparing(false);
    }
  };

  useEffect(() => {
    if (selectedIds.length >= 2 && !loadingSuppliers) {
      executeComparison();
    }
  }, [selectedIds.length]);

  const toggleSupplier = (id) => {
    if (selectedIds.includes(id)) {
      if (selectedIds.length <= 2) {
        alert('At least 2 suppliers must be selected for comparison.');
        return;
      }
      setSelectedIds(selectedIds.filter(item => item !== id));
    } else {
      if (selectedIds.length >= 5) {
        alert('You can compare a maximum of 5 suppliers simultaneously.');
        return;
      }
      setSelectedIds([...selectedIds, id]);
    }
  };

  const getRatingTierColor = (cat) => {
    switch (cat) {
      case 'EXCELLENT': return 'bg-emerald-100 text-emerald-800 border-emerald-300';
      case 'GOOD': return 'bg-blue-100 text-blue-800 border-blue-300';
      case 'AVERAGE': return 'bg-amber-100 text-amber-800 border-amber-300';
      case 'POOR': return 'bg-rose-100 text-rose-800 border-rose-300';
      default: return 'bg-slate-100 text-slate-800 border-slate-300';
    }
  };

  const getRiskColor = (level) => {
    switch (level) {
      case 'HIGH':
      case 'CRITICAL': return 'text-rose-600 bg-rose-50 border-rose-200';
      case 'MEDIUM': return 'text-amber-600 bg-amber-50 border-amber-200';
      case 'LOW': return 'text-emerald-600 bg-emerald-50 border-emerald-200';
      default: return 'text-slate-600 bg-slate-50 border-slate-200';
    }
  };

  // Grouped Bar Chart Data
  const chartColors = ['#4f46e5', '#06b6d4', '#10b981', '#f59e0b', '#ec4899'];
  const chartLabels = comparisonData?.criteriaNames || [];
  const chartDatasets = comparisonData?.suppliers?.map((sup, idx) => ({
    label: sup.supplierName,
    data: chartLabels.map(crit => sup.criteriaScores?.[crit] || 0),
    backgroundColor: chartColors[idx % chartColors.length],
    borderRadius: 4,
  })) || [];

  const chartData = {
    labels: chartLabels,
    datasets: chartDatasets,
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1.5">
              <span className="px-2.5 py-0.5 text-xs font-semibold bg-indigo-50 text-indigo-700 rounded-full border border-indigo-200 flex items-center gap-1">
                <GitCompare className="h-3 w-3" /> Side-by-Side Analytics
              </span>
              <span className="text-xs text-slate-400">• Up to 5 Suppliers</span>
            </div>
            <h1 className="text-2xl font-bold text-slate-800">Supplier Comparison Matrix</h1>
            <p className="text-slate-500 text-sm mt-1">
              Cross-evaluate vendor capabilities, criteria strengths, risk exposure, and governance compliance in a single view.
            </p>
          </div>

          <div className="flex items-center gap-2">
            <Button
              onClick={executeComparison}
              disabled={comparing || selectedIds.length < 2}
              className="bg-indigo-600 hover:bg-indigo-700 text-white text-xs"
            >
              <RefreshCw className={`h-3.5 w-3.5 mr-1.5 ${comparing ? 'animate-spin' : ''}`} />
              Re-Calculate Comparison
            </Button>
          </div>
        </div>

        {/* Supplier Picker Chips */}
        <div className="mt-6 pt-4 border-t border-slate-100">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-slate-600 uppercase tracking-wider">
              Select Suppliers to Compare ({selectedIds.length}/5 Selected)
            </span>
            <span className="text-xs text-slate-400">Click to toggle supplier</span>
          </div>

          {loadingSuppliers ? (
            <p className="text-xs text-slate-400">Loading suppliers list...</p>
          ) : (
            <div className="flex flex-wrap gap-2">
              {suppliersList.map((s) => {
                const isSelected = selectedIds.includes(s.id);
                return (
                  <button
                    key={s.id}
                    onClick={() => toggleSupplier(s.id)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-medium border flex items-center gap-1.5 transition-all ${
                      isSelected
                        ? 'bg-indigo-50 border-indigo-400 text-indigo-700 shadow-sm'
                        : 'bg-white border-slate-200 text-slate-600 hover:border-slate-300'
                    }`}
                  >
                    {isSelected ? (
                      <CheckSquare className="h-3.5 w-3.5 text-indigo-600" />
                    ) : (
                      <Square className="h-3.5 w-3.5 text-slate-400" />
                    )}
                    <span>{s.name}</span>
                    <span className="text-[10px] text-slate-400">({s.supplierCode})</span>
                  </button>
                );
              })}
            </div>
          )}
        </div>
      </div>

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-700 flex items-center gap-2">
          <AlertTriangle className="h-4 w-4 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {comparisonData && (
        <>
          {/* Summary Stats Overview */}
          <div className="grid grid-cols-2 sm:grid-cols-5 gap-3">
            <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm">
              <p className="text-xs text-slate-500">Suppliers Compared</p>
              <p className="text-xl font-bold text-slate-800 mt-1">{comparisonData.summaryStats?.totalCompared}</p>
            </div>
            <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm">
              <p className="text-xs text-slate-500">Highest Score</p>
              <p className="text-xl font-bold text-emerald-600 mt-1">{comparisonData.summaryStats?.highestScore}</p>
            </div>
            <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm">
              <p className="text-xs text-slate-500">Lowest Score</p>
              <p className="text-xl font-bold text-rose-600 mt-1">{comparisonData.summaryStats?.lowestScore}</p>
            </div>
            <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm">
              <p className="text-xs text-slate-500">Group Average</p>
              <p className="text-xl font-bold text-indigo-600 mt-1">{comparisonData.summaryStats?.averageScore}</p>
            </div>
            <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm">
              <p className="text-xs text-slate-500">Score Spread (Delta)</p>
              <p className="text-xl font-bold text-slate-700 mt-1">±{comparisonData.summaryStats?.scoreRangeDelta} pts</p>
            </div>
          </div>

          {/* Side-by-Side Matrix Table */}
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-x-auto">
            <table className="w-full text-left text-sm divide-y divide-slate-200">
              <thead className="bg-slate-50/80">
                <tr>
                  <th className="px-6 py-4 font-semibold text-slate-600 w-1/4">Evaluation Attribute</th>
                  {comparisonData.suppliers.map((s, idx) => (
                    <th key={s.supplierId} className="px-6 py-4 font-semibold text-slate-800 min-w-[200px]">
                      <div className="flex items-center gap-2 mb-1">
                        <span
                          className="w-3 h-3 rounded-full"
                          style={{ backgroundColor: chartColors[idx % chartColors.length] }}
                        />
                        <span className="font-bold">{s.supplierName}</span>
                      </div>
                      <p className="text-xs text-slate-500 font-normal">{s.supplierCode} • {s.categoryName}</p>
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {/* Overall Rating Tier */}
                <tr className="bg-indigo-50/20">
                  <td className="px-6 py-3 font-semibold text-slate-700">Overall Rating Score</td>
                  {comparisonData.suppliers.map(s => (
                    <td key={s.supplierId} className="px-6 py-3">
                      <div className="flex items-center gap-2">
                        <span className="text-lg font-bold text-slate-900">{s.overallScore?.toFixed(1)}</span>
                        <span className={`px-2 py-0.5 text-[10px] font-bold rounded border ${getRatingTierColor(s.ratingCategory)}`}>
                          {s.ratingCategory}
                        </span>
                      </div>
                    </td>
                  ))}
                </tr>

                {/* Risk Evaluation */}
                <tr>
                  <td className="px-6 py-3 font-semibold text-slate-700">Operational Risk Level</td>
                  {comparisonData.suppliers.map(s => (
                    <td key={s.supplierId} className="px-6 py-3">
                      <span className={`px-2.5 py-0.5 text-xs font-semibold rounded-full border ${getRiskColor(s.riskLevel)}`}>
                        {s.riskLevel} Risk ({s.riskScore}/100)
                      </span>
                    </td>
                  ))}
                </tr>

                {/* Performance Trajectory */}
                <tr>
                  <td className="px-6 py-3 font-semibold text-slate-700">Performance Trajectory</td>
                  {comparisonData.suppliers.map(s => (
                    <td key={s.supplierId} className="px-6 py-3">
                      <Badge variant={s.performanceTrend === 'IMPROVING' ? 'success' : s.performanceTrend === 'DECLINING' ? 'danger' : 'default'} size="xs">
                        {s.performanceTrend}
                      </Badge>
                    </td>
                  ))}
                </tr>

                {/* Criteria Header */}
                <tr className="bg-slate-50 font-bold text-slate-700 text-xs uppercase tracking-wider">
                  <td colSpan={comparisonData.suppliers.length + 1} className="px-6 py-2">
                    Criteria Specific Breakdown
                  </td>
                </tr>

                {/* Criteria Rows */}
                {comparisonData.criteriaBreakdown.map(row => (
                  <tr key={row.criteriaName} className="hover:bg-slate-50/50">
                    <td className="px-6 py-3 text-slate-700">
                      <div className="font-medium">{row.criteriaName}</div>
                      <div className="text-[10px] text-slate-400">Weight: {row.weightPercentage}%</div>
                    </td>
                    {comparisonData.suppliers.map(s => {
                      const score = row.supplierScores?.[s.supplierId] ?? 0;
                      return (
                        <td key={s.supplierId} className="px-6 py-3">
                          <div className="flex items-center gap-2">
                            <span className="text-sm font-semibold text-slate-800 w-10">{score}</span>
                            <div className="w-24 bg-slate-100 rounded-full h-2 overflow-hidden">
                              <div
                                className={`h-2 rounded-full ${
                                  score >= 80 ? 'bg-emerald-500' : score >= 60 ? 'bg-amber-500' : 'bg-rose-500'
                                }`}
                                style={{ width: `${Math.min(100, score)}%` }}
                              />
                            </div>
                          </div>
                        </td>
                      );
                    })}
                  </tr>
                ))}

                {/* Operational Governance Header */}
                <tr className="bg-slate-50 font-bold text-slate-700 text-xs uppercase tracking-wider">
                  <td colSpan={comparisonData.suppliers.length + 1} className="px-6 py-2">
                    Operational Actions & Governance
                  </td>
                </tr>

                {/* Open CAPs */}
                <tr>
                  <td className="px-6 py-3 font-semibold text-slate-700">Active CAP Action Plans</td>
                  {comparisonData.suppliers.map(s => (
                    <td key={s.supplierId} className="px-6 py-3 text-slate-700">
                      <span className={s.openImprovementActionsCount > 0 ? 'text-amber-600 font-bold' : 'text-slate-500'}>
                        {s.openImprovementActionsCount} Open Actions
                      </span>
                      <p className="text-[10px] text-slate-400">({s.completedImprovementActionsCount} resolved)</p>
                    </td>
                  ))}
                </tr>

                {/* SLA Compliance */}
                <tr>
                  <td className="px-6 py-3 font-semibold text-slate-700">SLA Authorization Rate</td>
                  {comparisonData.suppliers.map(s => (
                    <td key={s.supplierId} className="px-6 py-3 text-slate-800 font-semibold">
                      {s.slaComplianceRate}%
                    </td>
                  ))}
                </tr>

                {/* Quick Profile Link */}
                <tr className="bg-slate-50/50">
                  <td className="px-6 py-3 font-semibold text-slate-700">Deep Dive Profile</td>
                  {comparisonData.suppliers.map(s => (
                    <td key={s.supplierId} className="px-6 py-3">
                      <Link
                        to={`/suppliers/${s.supplierId}`}
                        className="text-xs font-semibold text-indigo-600 hover:text-indigo-800 flex items-center gap-1"
                      >
                        View Full Details <ArrowRight className="h-3 w-3" />
                      </Link>
                    </td>
                  ))}
                </tr>
              </tbody>
            </table>
          </div>

          {/* Visual Criteria Comparison Bar Chart */}
          <Card title="Multi-Criteria Comparative Analysis" subtitle="Side-by-side performance scores by evaluation category">
            <div className="h-72">
              <Bar
                data={chartData}
                options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  scales: {
                    y: { min: 0, max: 100, grid: { color: '#f1f5f9' } },
                    x: { grid: { display: false } },
                  },
                  plugins: {
                    legend: { position: 'top' },
                  },
                }}
              />
            </div>
          </Card>
        </>
      )}
    </div>
  );
};

export default SupplierComparison;
