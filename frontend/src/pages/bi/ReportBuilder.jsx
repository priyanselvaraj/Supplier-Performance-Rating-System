import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { biService } from '../../services/bi.service';
import { categoryService } from '../../services/category.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import {
  FileSpreadsheet,
  Sliders,
  Play,
  Save,
  BookmarkCheck,
  Eye,
  CheckCircle2,
  AlertTriangle,
  RefreshCw,
  BarChart3,
  PieChart,
  Table as TableIcon,
  Layers,
  Lock,
  Globe,
  Download,
  Check
} from 'lucide-react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  ArcElement,
  Tooltip,
  Legend,
  Title
} from 'chart.js';
import { Bar, Line, Doughnut } from 'react-chartjs-2';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  ArcElement,
  Tooltip,
  Legend,
  Title
);

export const ReportBuilder = () => {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [loadingConfig, setLoadingConfig] = useState(true);

  // Builder Form State
  const [title, setTitle] = useState('Custom Supplier Performance Intelligence');
  const [reportType, setReportType] = useState('PERFORMANCE_SUMMARY');
  const [reportScope, setReportScope] = useState('ALL_SUPPLIERS');
  const [categoryId, setCategoryId] = useState('');
  const [ratingCategory, setRatingCategory] = useState('');
  const [status, setStatus] = useState('');
  const [visualization, setVisualization] = useState('BAR_CHART');

  // Preview State
  const [previewData, setPreviewData] = useState(null);
  const [previewLoading, setPreviewLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // Save Modal State
  const [showSaveModal, setShowSaveModal] = useState(false);
  const [saveName, setSaveName] = useState('');
  const [saveDesc, setSaveDesc] = useState('');
  const [isPublic, setIsPublic] = useState(false);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    const loadCategories = async () => {
      try {
        const res = await categoryService.getAllCategories();
        setCategories(res.data || []);
      } catch (err) {
        console.error('Failed to load categories:', err);
      } finally {
        setLoadingConfig(false);
      }
    };
    loadCategories();
  }, []);

  const buildPreviewRequest = () => {
    return {
      reportTitle: title,
      reportType,
      reportScope,
      categoryId: categoryId ? parseInt(categoryId, 10) : null,
      ratingCategory: ratingCategory || null,
      status: status || null,
      visualization,
      selectedMetrics: ['SCORE', 'RISK', 'EVALUATIONS', 'TIER'],
    };
  };

  const handleGeneratePreview = async () => {
    setPreviewLoading(true);
    setError('');
    setSuccessMsg('');
    try {
      const payload = buildPreviewRequest();
      const res = await biService.generateReportPreview(payload);
      const payloadData = res?.data !== undefined ? res.data : res;
      if (res?.success !== false && payloadData) {
        setPreviewData(payloadData);
      } else {
        setError(res?.message || 'Failed to generate report preview.');
      }
    } catch (err) {
      console.error('Preview error:', err);
      setError('An error occurred while generating report preview.');
    } finally {
      setPreviewLoading(false);
    }
  };

  useEffect(() => {
    if (!loadingConfig) {
      handleGeneratePreview();
    }
  }, [loadingConfig]);

  const handleSaveReport = async (e) => {
    e.preventDefault();
    if (!saveName.trim()) {
      setError('Please provide a name for this saved report.');
      return;
    }
    setSaving(true);
    setError('');
    try {
      const req = {
        name: saveName.trim(),
        description: saveDesc.trim(),
        reportType,
        scope: reportScope,
        visualization,
        isPublic,
        selectedMetrics: ['SCORE', 'RISK', 'EVALUATIONS'],
        filters: JSON.stringify({
          categoryId: categoryId || null,
          ratingCategory: ratingCategory || null,
          status: status || null,
        }),
      };

      const res = await biService.saveReport(req);
      if (res.success) {
        setSuccessMsg(`Report '${res.data.name}' saved to gallery successfully!`);
        setShowSaveModal(false);
        setSaveName('');
        setSaveDesc('');
      } else {
        setError(res.message || 'Failed to save report configuration.');
      }
    } catch (err) {
      console.error('Save report error:', err);
      setError(err.response?.data?.message || 'Failed to save report.');
    } finally {
      setSaving(false);
    }
  };

  // Render chart preview based on visualization format
  const renderChart = () => {
    if (!previewData?.chartData) return null;
    const labels = previewData.chartData.labels || [];
    const seriesData = previewData.chartData.series?.[0]?.data || [];

    if (visualization === 'BAR_CHART') {
      const data = {
        labels,
        datasets: [
          {
            label: 'Performance Score',
            data: seriesData,
            backgroundColor: '#6366f1',
            borderRadius: 6,
          },
        ],
      };
      return (
        <div className="h-64">
          <Bar
            data={data}
            options={{
              responsive: true,
              maintainAspectRatio: false,
              scales: { y: { min: 0, max: 100 } },
              plugins: { legend: { display: false } },
            }}
          />
        </div>
      );
    }

    if (visualization === 'LINE_CHART') {
      const data = {
        labels,
        datasets: [
          {
            label: 'Performance Score',
            data: seriesData,
            borderColor: '#6366f1',
            backgroundColor: 'rgba(99, 102, 241, 0.1)',
            fill: true,
            tension: 0.3,
          },
        ],
      };
      return (
        <div className="h-64">
          <Line
            data={data}
            options={{
              responsive: true,
              maintainAspectRatio: false,
              scales: { y: { min: 0, max: 100 } },
            }}
          />
        </div>
      );
    }

    if (visualization === 'PIE_CHART') {
      const data = {
        labels,
        datasets: [
          {
            data: seriesData,
            backgroundColor: ['#6366f1', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#ec4899', '#64748b'],
          },
        ],
      };
      return (
        <div className="h-64 flex items-center justify-center">
          <Doughnut data={data} options={{ responsive: true, maintainAspectRatio: false }} />
        </div>
      );
    }

    return null;
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1.5">
              <span className="px-2.5 py-0.5 text-xs font-semibold bg-emerald-50 text-emerald-700 rounded-full border border-emerald-200 flex items-center gap-1">
                <FileSpreadsheet className="h-3 w-3" /> Custom BI Query & Report Builder
              </span>
              <span className="text-xs text-slate-400">• Multi-Format Exports</span>
            </div>
            <h1 className="text-2xl font-bold text-slate-800">Dynamic Report Builder</h1>
            <p className="text-slate-500 text-sm mt-1">
              Construct ad-hoc intelligence queries, customize criteria filters, live preview data visualizations, and save blueprints for repeat execution.
            </p>
          </div>

          <div className="flex items-center gap-2">
            <Link to="/business-intelligence/saved-reports">
              <Button variant="outline" className="text-xs">
                <BookmarkCheck className="h-3.5 w-3.5 mr-1.5 text-amber-600" /> Saved Reports Gallery
              </Button>
            </Link>
            <Button
              onClick={() => {
                setSaveName(title);
                setShowSaveModal(true);
              }}
              className="bg-emerald-600 hover:bg-emerald-700 text-white text-xs"
            >
              <Save className="h-3.5 w-3.5 mr-1.5" /> Save Blueprint
            </Button>
          </div>
        </div>
      </div>

      {successMsg && (
        <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-xl text-xs text-emerald-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="h-4 w-4 text-emerald-600 shrink-0" />
            <span>{successMsg}</span>
          </div>
          <Link to="/business-intelligence/saved-reports" className="font-bold underline text-emerald-900">
            View in Gallery &rarr;
          </Link>
        </div>
      )}

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-700 flex items-center gap-2">
          <AlertTriangle className="h-4 w-4 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Main Builder Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Left Filter Config Sidebar */}
        <div className="lg:col-span-1 space-y-4">
          <Card title="Query & Filter Blueprint" subtitle="Define report parameters">
            <div className="space-y-4 text-xs">
              {/* Report Title */}
              <div>
                <label className="block font-semibold text-slate-700 mb-1">Report Title</label>
                <input
                  type="text"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  placeholder="Enter custom title"
                />
              </div>

              {/* Report Type */}
              <div>
                <label className="block font-semibold text-slate-700 mb-1">Intelligence Focus</label>
                <select
                  value={reportType}
                  onChange={(e) => setReportType(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                >
                  <option value="PERFORMANCE_SUMMARY">Performance Summary</option>
                  <option value="RISK_AUDIT">Operational Risk Audit</option>
                  <option value="EXECUTIVE_KPI">Executive KPI Overview</option>
                  <option value="CUSTOM">Custom Ad-Hoc Report</option>
                </select>
              </div>

              {/* Scope */}
              <div>
                <label className="block font-semibold text-slate-700 mb-1">Scope</label>
                <select
                  value={reportScope}
                  onChange={(e) => setReportScope(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                >
                  <option value="ALL_SUPPLIERS">All Suppliers (Global)</option>
                  <option value="CATEGORY">By Category</option>
                </select>
              </div>

              {/* Category Filter (Conditional) */}
              {reportScope === 'CATEGORY' && (
                <div>
                  <label className="block font-semibold text-slate-700 mb-1">Select Category</label>
                  <select
                    value={categoryId}
                    onChange={(e) => setCategoryId(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  >
                    <option value="">All Categories</option>
                    {categories.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.name}
                      </option>
                    ))}
                  </select>
                </div>
              )}

              {/* Rating Category Tier Filter */}
              <div>
                <label className="block font-semibold text-slate-700 mb-1">Performance Tier</label>
                <select
                  value={ratingCategory}
                  onChange={(e) => setRatingCategory(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                >
                  <option value="">All Rating Tiers</option>
                  <option value="EXCELLENT">Excellent (&gt; 90)</option>
                  <option value="GOOD">Good (75 - 90)</option>
                  <option value="AVERAGE">Average (60 - 75)</option>
                  <option value="POOR">Poor (&lt; 60)</option>
                </select>
              </div>

              {/* Status Filter */}
              <div>
                <label className="block font-semibold text-slate-700 mb-1">Supplier Status</label>
                <select
                  value={status}
                  onChange={(e) => setStatus(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                >
                  <option value="">All Statuses</option>
                  <option value="ACTIVE">Active Only</option>
                  <option value="INACTIVE">Inactive Only</option>
                </select>
              </div>

              {/* Visualization Style */}
              <div>
                <label className="block font-semibold text-slate-700 mb-1">Visualization Format</label>
                <div className="grid grid-cols-2 gap-2 mt-1">
                  <button
                    type="button"
                    onClick={() => setVisualization('BAR_CHART')}
                    className={`p-2 rounded-lg border text-center flex flex-col items-center gap-1 transition-all ${
                      visualization === 'BAR_CHART'
                        ? 'bg-indigo-50 border-indigo-500 text-indigo-700 font-bold shadow-sm'
                        : 'bg-white border-slate-200 text-slate-600'
                    }`}
                  >
                    <BarChart3 className="h-4 w-4" /> Bar Chart
                  </button>
                  <button
                    type="button"
                    onClick={() => setVisualization('LINE_CHART')}
                    className={`p-2 rounded-lg border text-center flex flex-col items-center gap-1 transition-all ${
                      visualization === 'LINE_CHART'
                        ? 'bg-indigo-50 border-indigo-500 text-indigo-700 font-bold shadow-sm'
                        : 'bg-white border-slate-200 text-slate-600'
                    }`}
                  >
                    <Sliders className="h-4 w-4" /> Line Trend
                  </button>
                  <button
                    type="button"
                    onClick={() => setVisualization('PIE_CHART')}
                    className={`p-2 rounded-lg border text-center flex flex-col items-center gap-1 transition-all ${
                      visualization === 'PIE_CHART'
                        ? 'bg-indigo-50 border-indigo-500 text-indigo-700 font-bold shadow-sm'
                        : 'bg-white border-slate-200 text-slate-600'
                    }`}
                  >
                    <PieChart className="h-4 w-4" /> Doughnut
                  </button>
                  <button
                    type="button"
                    onClick={() => setVisualization('TABLE')}
                    className={`p-2 rounded-lg border text-center flex flex-col items-center gap-1 transition-all ${
                      visualization === 'TABLE'
                        ? 'bg-indigo-50 border-indigo-500 text-indigo-700 font-bold shadow-sm'
                        : 'bg-white border-slate-200 text-slate-600'
                    }`}
                  >
                    <TableIcon className="h-4 w-4" /> Table Only
                  </button>
                </div>
              </div>

              {/* Action Button */}
              <div className="pt-2">
                <Button
                  onClick={handleGeneratePreview}
                  disabled={previewLoading}
                  className="w-full bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold py-2.5"
                >
                  <Play className={`h-3.5 w-3.5 mr-1.5 ${previewLoading ? 'animate-spin' : ''}`} />
                  {previewLoading ? 'Processing Query...' : 'Run Live Preview'}
                </Button>
              </div>
            </div>
          </Card>
        </div>

        {/* Right Live Preview Canvas */}
        <div className="lg:col-span-3 space-y-6">
          {previewLoading ? (
            <div className="bg-white rounded-xl border border-slate-200 p-12 text-center">
              <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600 mx-auto mb-3"></div>
              <p className="text-xs text-slate-500">Querying database engine & generating dynamic charts...</p>
            </div>
          ) : previewData ? (
            <>
              {/* Summary Stats Cards */}
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                {previewData.summaryMetrics?.map((card, idx) => (
                  <div key={idx} className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
                    <p className="text-xs text-slate-500">{card.label}</p>
                    <p className="text-2xl font-bold text-slate-900 mt-1">{card.value}</p>
                    <span className="text-[10px] text-indigo-600 font-medium">Metric Validated</span>
                  </div>
                ))}
              </div>

              {/* Dynamic Chart Display */}
              {visualization !== 'TABLE' && (
                <Card
                  title={`${title} - Visual Analytics`}
                  subtitle={`Visualization format: ${visualization.replace('_', ' ')}`}
                >
                  {renderChart()}
                </Card>
              )}

              {/* Live Preview Data Table */}
              <Card
                title={`Data Records Preview (${previewData.totalRecords} Matching Suppliers)`}
                subtitle="Live database results reflecting active query filters"
                action={
                  <span className="text-xs text-slate-400">
                    Generated: {new Date(previewData.generatedAt).toLocaleTimeString()}
                  </span>
                }
              >
                <div className="overflow-x-auto">
                  <table className="w-full text-left text-sm divide-y divide-slate-200">
                    <thead className="bg-slate-50 text-slate-600 text-xs font-semibold uppercase tracking-wider">
                      <tr>
                        {previewData.tableHeaders?.map((header, idx) => (
                          <th key={idx} className="px-4 py-3">{header}</th>
                        ))}
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100 text-xs">
                      {previewData.tableData && previewData.tableData.length > 0 ? (
                        previewData.tableData.map((row, rIdx) => (
                          <tr key={rIdx} className="hover:bg-slate-50/50">
                            {previewData.tableHeaders?.map((header, cIdx) => (
                              <td key={cIdx} className="px-4 py-2.5 text-slate-700">
                                {header === 'Rating Tier' ? (
                                  <Badge variant={row[header] === 'EXCELLENT' ? 'success' : row[header] === 'POOR' ? 'danger' : 'info'} size="xs">
                                    {row[header]}
                                  </Badge>
                                ) : header === 'Risk Level' ? (
                                  <span className={row[header] === 'HIGH' ? 'text-rose-600 font-bold' : 'text-slate-600'}>
                                    {row[header]}
                                  </span>
                                ) : header === 'Supplier Name' ? (
                                  <Link to={`/suppliers/${row.id}`} className="font-semibold text-indigo-600 hover:underline">
                                    {row[header]}
                                  </Link>
                                ) : (
                                  row[header] ?? '-'
                                )}
                              </td>
                            ))}
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td colSpan={previewData.tableHeaders?.length || 5} className="px-4 py-8 text-center text-slate-400">
                            No suppliers match the selected filter criteria.
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </Card>
            </>
          ) : (
            <div className="bg-white rounded-xl border border-slate-200 p-12 text-center text-slate-400">
              Configure parameters on the left and click "Run Live Preview".
            </div>
          )}
        </div>
      </div>

      {/* Save Blueprint Modal */}
      {showSaveModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl border border-slate-200 animate-in fade-in zoom-in-95">
            <h3 className="text-lg font-bold text-slate-900 mb-1">Save Report Configuration</h3>
            <p className="text-xs text-slate-500 mb-4">
              Save this query blueprint to your reports library for instant 1-click execution.
            </p>

            <form onSubmit={handleSaveReport} className="space-y-4 text-xs">
              <div>
                <label className="block font-semibold text-slate-700 mb-1">Report Blueprint Name *</label>
                <input
                  type="text"
                  required
                  value={saveName}
                  onChange={(e) => setSaveName(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  placeholder="e.g., Monthly Electronics Quality Audit"
                />
              </div>

              <div>
                <label className="block font-semibold text-slate-700 mb-1">Description</label>
                <textarea
                  rows={2}
                  value={saveDesc}
                  onChange={(e) => setSaveDesc(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  placeholder="Optional context about the report focus..."
                />
              </div>

              {/* Public Toggle */}
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 flex items-center justify-between">
                <div className="flex items-center gap-2">
                  {isPublic ? <Globe className="h-4 w-4 text-indigo-600" /> : <Lock className="h-4 w-4 text-slate-500" />}
                  <div>
                    <p className="font-semibold text-slate-800">{isPublic ? 'Public Blueprint' : 'Private to Me'}</p>
                    <p className="text-[10px] text-slate-400">
                      {isPublic ? 'Visible to all authorized company users' : 'Isolated exclusively to your account'}
                    </p>
                  </div>
                </div>
                <input
                  type="checkbox"
                  checked={isPublic}
                  onChange={(e) => setIsPublic(e.target.checked)}
                  className="h-4 w-4 rounded text-indigo-600 focus:ring-indigo-500 border-slate-300"
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setShowSaveModal(false)}
                  className="text-xs"
                >
                  Cancel
                </Button>
                <Button
                  type="submit"
                  disabled={saving}
                  className="bg-emerald-600 hover:bg-emerald-700 text-white text-xs"
                >
                  {saving ? 'Saving...' : 'Confirm & Save Blueprint'}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default ReportBuilder;
