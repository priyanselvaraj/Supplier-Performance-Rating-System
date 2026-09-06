import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { biService } from '../../services/bi.service';
import { useAuth } from '../../context/AuthContext';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import {
  BookmarkCheck,
  Plus,
  Play,
  FileSpreadsheet,
  Globe,
  Lock,
  Trash2,
  Edit,
  AlertTriangle,
  RefreshCw,
  ExternalLink,
  BarChart2,
  Calendar,
  User as UserIcon,
  X
} from 'lucide-react';

export const SavedReports = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filterType, setFilterType] = useState('ALL'); // 'ALL' | 'MY_REPORTS' | 'PUBLIC'

  // Modal execution state
  const [executingReport, setExecutingReport] = useState(null);
  const [executionResult, setExecutionResult] = useState(null);
  const [executingLoading, setExecutingLoading] = useState(false);

  const fetchSavedReports = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await biService.getSavedReports();
      const list = Array.isArray(res) ? res : res?.data || [];
      if (res?.success !== false) {
        setReports(list);
      } else {
        setError(res?.message || 'Failed to load saved reports.');
      }
    } catch (err) {
      console.error('Error loading saved reports:', err);
      setError('An error occurred while retrieving saved reports gallery.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSavedReports();
  }, []);

  const handleDeleteReport = async (id, name) => {
    if (!window.confirm(`Are you sure you want to delete saved report '${name}'?`)) return;
    try {
      const res = await biService.deleteSavedReport(id);
      if (res.success) {
        setReports(reports.filter(r => r.id !== id));
      } else {
        alert(res.message || 'Failed to delete report.');
      }
    } catch (err) {
      console.error('Delete error:', err);
      alert(err.response?.data?.message || 'Failed to delete report.');
    }
  };

  const handleExecuteReport = async (report) => {
    setExecutingReport(report);
    setExecutingLoading(true);
    setExecutionResult(null);
    try {
      let parsedFilters = {};
      if (report.filters) {
        try {
          parsedFilters = JSON.parse(report.filters);
        } catch (ignored) {}
      }

      const payload = {
        reportTitle: report.name,
        reportType: report.reportType,
        reportScope: report.scope,
        visualization: report.visualization,
        categoryId: parsedFilters.categoryId || null,
        ratingCategory: parsedFilters.ratingCategory || null,
        status: parsedFilters.status || null,
      };

      const res = await biService.generateReportPreview(payload);
      if (res.success) {
        setExecutionResult(res.data);
      }
    } catch (err) {
      console.error('Failed to execute saved report:', err);
    } finally {
      setExecutingLoading(false);
    }
  };

  const filteredReports = reports.filter(r => {
    if (filterType === 'MY_REPORTS') {
      return r.createdById === user?.id || r.createdByUsername === user?.username;
    }
    if (filterType === 'PUBLIC') {
      return r.isPublic === true;
    }
    return true;
  });

  const isOwnerOrAdmin = (r) => {
    if (!user) return false;
    const isAdmin = user.roles?.some(role => role === 'ROLE_ADMIN' || role.name === 'ROLE_ADMIN');
    return isAdmin || r.createdById === user.id || r.createdByUsername === user.username;
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1.5">
              <span className="px-2.5 py-0.5 text-xs font-semibold bg-amber-50 text-amber-700 rounded-full border border-amber-200 flex items-center gap-1">
                <BookmarkCheck className="h-3 w-3" /> Saved Blueprints Gallery
              </span>
              <span className="text-xs text-slate-400">• 1-Click Instant Execution</span>
            </div>
            <h1 className="text-2xl font-bold text-slate-800">Saved Intelligence Reports</h1>
            <p className="text-slate-500 text-sm mt-1">
              Access your personal and organizational saved report blueprints with automated live data binding.
            </p>
          </div>

          <div className="flex items-center gap-2">
            <Link to="/business-intelligence/report-builder">
              <Button className="bg-indigo-600 hover:bg-indigo-700 text-white text-xs">
                <Plus className="h-3.5 w-3.5 mr-1.5" /> Build New Report
              </Button>
            </Link>
          </div>
        </div>

        {/* Filter Navigation Tabs */}
        <div className="flex items-center gap-2 mt-6 pt-4 border-t border-slate-100">
          <button
            onClick={() => setFilterType('ALL')}
            className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-all ${
              filterType === 'ALL'
                ? 'bg-slate-900 text-white shadow-sm'
                : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
            }`}
          >
            All Accessible ({reports.length})
          </button>
          <button
            onClick={() => setFilterType('MY_REPORTS')}
            className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-all ${
              filterType === 'MY_REPORTS'
                ? 'bg-indigo-600 text-white shadow-sm'
                : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
            }`}
          >
            My Reports Only
          </button>
          <button
            onClick={() => setFilterType('PUBLIC')}
            className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-all ${
              filterType === 'PUBLIC'
                ? 'bg-emerald-600 text-white shadow-sm'
                : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
            }`}
          >
            Public Blueprints
          </button>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-700 flex items-center gap-2">
          <AlertTriangle className="h-4 w-4 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {loading ? (
        <div className="py-20 text-center">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600 mx-auto mb-3"></div>
          <p className="text-xs text-slate-500">Loading saved reports gallery...</p>
        </div>
      ) : filteredReports.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredReports.map((report) => (
            <div
              key={report.id}
              className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm hover:shadow-md transition-all flex flex-col justify-between"
            >
              <div>
                <div className="flex items-start justify-between gap-2 mb-2">
                  <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                    {report.reportType?.replace('_', ' ')}
                  </span>
                  <div className="flex items-center gap-1.5">
                    {report.isPublic ? (
                      <span className="px-2 py-0.5 text-[10px] font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200 rounded-full flex items-center gap-1">
                        <Globe className="h-3 w-3" /> Public
                      </span>
                    ) : (
                      <span className="px-2 py-0.5 text-[10px] font-semibold bg-slate-100 text-slate-600 border border-slate-200 rounded-full flex items-center gap-1">
                        <Lock className="h-3 w-3" /> Private
                      </span>
                    )}
                  </div>
                </div>

                <h3 className="text-base font-bold text-slate-800 line-clamp-1 mb-1" title={report.name}>
                  {report.name}
                </h3>

                <p className="text-xs text-slate-500 line-clamp-2 leading-relaxed mb-4">
                  {report.description || 'No description provided.'}
                </p>

                <div className="space-y-1.5 pt-3 border-t border-slate-100 text-[11px] text-slate-500">
                  <div className="flex items-center justify-between">
                    <span>Scope:</span>
                    <strong className="text-slate-700">{report.scope?.replace('_', ' ')}</strong>
                  </div>
                  <div className="flex items-center justify-between">
                    <span>Visualization:</span>
                    <strong className="text-indigo-600 font-semibold">{report.visualization?.replace('_', ' ')}</strong>
                  </div>
                  <div className="flex items-center justify-between">
                    <span>Created by:</span>
                    <strong className="text-slate-700">{report.createdByFullName || report.createdByUsername}</strong>
                  </div>
                </div>
              </div>

              <div className="mt-5 pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
                <Button
                  onClick={() => handleExecuteReport(report)}
                  className="bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold px-3 py-1.5 flex items-center gap-1"
                >
                  <Play className="h-3 w-3" /> Run Report
                </Button>

                <div className="flex items-center gap-1">
                  {isOwnerOrAdmin(report) && (
                    <button
                      onClick={() => handleDeleteReport(report.id, report.name)}
                      className="p-1.5 text-slate-400 hover:text-rose-600 rounded-lg hover:bg-rose-50 transition-colors"
                      title="Delete Report"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-xl border border-slate-200 p-12 text-center">
          <BookmarkCheck className="h-12 w-12 text-slate-300 mx-auto mb-3" />
          <h3 className="text-base font-semibold text-slate-800 mb-1">No Saved Reports Found</h3>
          <p className="text-xs text-slate-500 max-w-sm mx-auto mb-6">
            You haven't saved any custom report blueprints yet. Use the Report Builder to query and save reports.
          </p>
          <Link to="/business-intelligence/report-builder">
            <Button className="bg-indigo-600 hover:bg-indigo-700 text-white text-xs">
              <Plus className="h-3.5 w-3.5 mr-1.5" /> Launch Report Builder
            </Button>
          </Link>
        </div>
      )}

      {/* 1-Click Execution Preview Modal */}
      {executingReport && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
          <div className="bg-white rounded-2xl max-w-4xl w-full max-h-[90vh] flex flex-col shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 overflow-hidden">
            {/* Modal Header */}
            <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/80">
              <div>
                <span className="text-[10px] font-bold text-indigo-600 uppercase tracking-wider">
                  Live Report Execution
                </span>
                <h3 className="text-lg font-bold text-slate-900">{executingReport.name}</h3>
              </div>
              <button
                onClick={() => setExecutingReport(null)}
                className="p-1 text-slate-400 hover:text-slate-600 rounded-lg"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            {/* Modal Body */}
            <div className="p-6 overflow-y-auto space-y-6">
              {executingLoading ? (
                <div className="py-16 text-center">
                  <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600 mx-auto mb-3"></div>
                  <p className="text-xs text-slate-500">Executing saved query blueprint against live data...</p>
                </div>
              ) : executionResult ? (
                <>
                  {/* Summary Metric Cards */}
                  <div className="grid grid-cols-3 gap-3">
                    {executionResult.summaryMetrics?.map((card, idx) => (
                      <div key={idx} className="bg-slate-50 p-3 rounded-xl border border-slate-200">
                        <p className="text-xs text-slate-500">{card.label}</p>
                        <p className="text-xl font-bold text-slate-900 mt-0.5">{card.value}</p>
                      </div>
                    ))}
                  </div>

                  {/* Results Table */}
                  <div className="border border-slate-200 rounded-xl overflow-hidden">
                    <table className="w-full text-left text-sm divide-y divide-slate-200">
                      <thead className="bg-slate-50 text-slate-600 text-xs font-semibold">
                        <tr>
                          {executionResult.tableHeaders?.map((header, idx) => (
                            <th key={idx} className="px-4 py-2.5">{header}</th>
                          ))}
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-slate-100 text-xs">
                        {executionResult.tableData?.map((row, rIdx) => (
                          <tr key={rIdx} className="hover:bg-slate-50/50">
                            {executionResult.tableHeaders?.map((header, cIdx) => (
                              <td key={cIdx} className="px-4 py-2 text-slate-700">
                                {header === 'Rating Tier' ? (
                                  <Badge variant={row[header] === 'EXCELLENT' ? 'success' : row[header] === 'POOR' ? 'danger' : 'info'} size="xs">
                                    {row[header]}
                                  </Badge>
                                ) : (
                                  row[header] ?? '-'
                                )}
                              </td>
                            ))}
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </>
              ) : (
                <p className="text-xs text-slate-400 py-8 text-center">Failed to execute report.</p>
              )}
            </div>

            {/* Modal Footer */}
            <div className="px-6 py-3 border-t border-slate-100 flex items-center justify-between bg-slate-50">
              <span className="text-xs text-slate-400">
                Executed: {new Date().toLocaleTimeString()}
              </span>
              <Button
                variant="outline"
                onClick={() => setExecutingReport(null)}
                className="text-xs"
              >
                Close View
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SavedReports;
