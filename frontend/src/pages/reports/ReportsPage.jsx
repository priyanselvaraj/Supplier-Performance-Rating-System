import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { reportService } from '../../services/report.service';
import { supplierService } from '../../services/supplier.service';
import { evaluationService } from '../../services/evaluation.service';
import { categoryService } from '../../services/category.service';
import { useAuth } from '../../context/AuthContext';
import { Card } from '../../components/common/Card';
import { Table } from '../../components/common/Table';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import {
  Printer,
  TrendingUp,
  Download,
  Filter,
  Award,
  AlertCircle,
  Building2,
  Calendar,
  FileText,
  FileSpreadsheet,
  FileCode,
  CheckCircle2,
  Layers,
  ChevronRight
} from 'lucide-react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { Bar } from 'react-chartjs-2';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

export const ReportsPage = () => {
  const { isAdmin } = useAuth();
  const [activeTab, setActiveTab] = useState('supplier'); // 'supplier' | 'evaluation' | 'overall' | 'summary' | 'ledger'
  const [loading, setLoading] = useState(false);
  const [exporting, setExporting] = useState(false);

  // Common Lookups
  const [suppliers, setSuppliers] = useState([]);
  const [evaluations, setEvaluations] = useState([]);
  const [categories, setCategories] = useState([]);

  // Tab 1: Supplier Report State
  const [selectedSupplierId, setSelectedSupplierId] = useState('');
  const [supplierStartDate, setSupplierStartDate] = useState('');
  const [supplierEndDate, setSupplierEndDate] = useState('');
  const [supplierReport, setSupplierReport] = useState(null);

  // Tab 2: Evaluation Report State
  const [selectedEvalId, setSelectedEvalId] = useState('');
  const [evaluationReport, setEvaluationReport] = useState(null);

  // Tab 3: Overall Report State
  const [overallStartDate, setOverallStartDate] = useState('');
  const [overallEndDate, setOverallEndDate] = useState('');
  const [overallReport, setOverallReport] = useState(null);

  // Tab 4: Evaluation Summary State
  const [summaryReport, setSummaryReport] = useState(null);

  // Tab 5: Ledger State
  const [ledgerData, setLedgerData] = useState(null);
  const [keyword, setKeyword] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [status, setStatus] = useState('');
  const [ratingCategory, setRatingCategory] = useState('');

  // Initial lookup loading
  useEffect(() => {
    const fetchLookups = async () => {
      try {
        const [supRes, evalRes, catRes] = await Promise.all([
          supplierService.getAllSuppliers(),
          evaluationService.getEvaluations({ page: 0, size: 50 }),
          categoryService.getAllCategories(),
        ]);
        if (supRes.success) {
          setSuppliers(supRes.data || []);
          if (supRes.data?.length > 0) setSelectedSupplierId(supRes.data[0].id);
        }
        if (evalRes.success && evalRes.data?.content) {
          setEvaluations(evalRes.data.content || []);
          if (evalRes.data.content?.length > 0) setSelectedEvalId(evalRes.data.content[0].id);
        }
        if (catRes.success) setCategories(catRes.data || []);
      } catch (err) {
        console.error('Failed to load lookups for reports:', err);
      }
    };
    fetchLookups();
  }, []);

  // Fetch Supplier Report
  const loadSupplierReport = useCallback(async () => {
    if (!selectedSupplierId) return;
    setLoading(true);
    try {
      const res = await reportService.getSupplierReport(selectedSupplierId, {
        startDate: supplierStartDate || undefined,
        endDate: supplierEndDate || undefined,
      });
      if (res.success) setSupplierReport(res.data);
    } catch (err) {
      console.error('Failed to load supplier report:', err);
    } finally {
      setLoading(false);
    }
  }, [selectedSupplierId, supplierStartDate, supplierEndDate]);

  // Fetch Evaluation Report
  const loadEvaluationReport = useCallback(async () => {
    if (!selectedEvalId) return;
    setLoading(true);
    try {
      const res = await reportService.getEvaluationReport(selectedEvalId);
      if (res.success) setEvaluationReport(res.data);
    } catch (err) {
      console.error('Failed to load evaluation report:', err);
    } finally {
      setLoading(false);
    }
  }, [selectedEvalId]);

  // Fetch Overall Report
  const loadOverallReport = useCallback(async () => {
    setLoading(true);
    try {
      const res = await reportService.getOverallReport({
        startDate: overallStartDate || undefined,
        endDate: overallEndDate || undefined,
      });
      if (res.success) setOverallReport(res.data);
    } catch (err) {
      console.error('Failed to load overall report:', err);
    } finally {
      setLoading(false);
    }
  }, [overallStartDate, overallEndDate]);

  // Fetch Evaluation Summary Report
  const loadSummaryReport = useCallback(async () => {
    setLoading(true);
    try {
      const res = await reportService.getEvaluationSummaryReport();
      if (res.success) setSummaryReport(res.data);
    } catch (err) {
      console.error('Failed to load summary report:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  // Fetch Ledger
  const loadLedger = useCallback(async () => {
    setLoading(true);
    try {
      const res = await reportService.getPerformanceReport({
        keyword: keyword || undefined,
        categoryId: categoryId || undefined,
        status: status || undefined,
        ratingCategory: ratingCategory || undefined,
      });
      if (res.success) setLedgerData(res.data);
    } catch (err) {
      console.error('Failed to load ledger:', err);
    } finally {
      setLoading(false);
    }
  }, [keyword, categoryId, status, ratingCategory]);

  useEffect(() => {
    if (activeTab === 'supplier' && selectedSupplierId) loadSupplierReport();
    else if (activeTab === 'evaluation' && selectedEvalId) loadEvaluationReport();
    else if (activeTab === 'overall' && isAdmin()) loadOverallReport();
    else if (activeTab === 'summary' && isAdmin()) loadSummaryReport();
    else if (activeTab === 'ledger') loadLedger();
  }, [activeTab, selectedSupplierId, selectedEvalId, loadSupplierReport, loadEvaluationReport, loadOverallReport, loadSummaryReport, loadLedger, isAdmin]);

  // Export handlers
  const handleExportSupplier = async (format) => {
    if (!selectedSupplierId) return;
    setExporting(true);
    try {
      await reportService.exportSupplierReport(selectedSupplierId, format, {
        startDate: supplierStartDate || undefined,
        endDate: supplierEndDate || undefined,
      });
    } catch (err) {
      alert('Export failed: ' + (err.message || 'Error generating export file'));
    } finally {
      setExporting(false);
    }
  };

  const handleExportEvaluation = async (format) => {
    if (!selectedEvalId) return;
    setExporting(true);
    try {
      await reportService.exportEvaluationReport(selectedEvalId, format);
    } catch (err) {
      alert('Export failed: ' + (err.message || 'Error generating export file'));
    } finally {
      setExporting(false);
    }
  };

  const handleExportOverall = async (format) => {
    setExporting(true);
    try {
      await reportService.exportOverallReport(format, {
        startDate: overallStartDate || undefined,
        endDate: overallEndDate || undefined,
      });
    } catch (err) {
      alert('Export failed: ' + (err.message || 'Error generating export file'));
    } finally {
      setExporting(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 print:hidden">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2.5">
            <FileText className="h-6 w-6 text-blue-600" />
            Executive Reports &amp; Document Exports
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            Generate formal supplier performance reports, evaluation scorecards, and export formatted PDF, Excel (.xlsx), and CSV files.
          </p>
        </div>
        <Button variant="secondary" icon={Printer} onClick={() => window.print()}>
          Print View
        </Button>
      </div>

      {/* Navigation Tabs */}
      <div className="flex border-b border-slate-200 overflow-x-auto print:hidden gap-2">
        <button
          onClick={() => setActiveTab('supplier')}
          className={`pb-3 px-4 text-sm font-semibold border-b-2 whitespace-nowrap transition-all ${
            activeTab === 'supplier'
              ? 'border-blue-600 text-blue-600'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          Supplier Performance Report
        </button>

        <button
          onClick={() => setActiveTab('evaluation')}
          className={`pb-3 px-4 text-sm font-semibold border-b-2 whitespace-nowrap transition-all ${
            activeTab === 'evaluation'
              ? 'border-blue-600 text-blue-600'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          Evaluation Scorecard Report
        </button>

        {isAdmin() && (
          <button
            onClick={() => setActiveTab('overall')}
            className={`pb-3 px-4 text-sm font-semibold border-b-2 whitespace-nowrap transition-all ${
              activeTab === 'overall'
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-slate-500 hover:text-slate-700'
            }`}
          >
            Overall Performance (Admin)
          </button>
        )}

        {isAdmin() && (
          <button
            onClick={() => setActiveTab('summary')}
            className={`pb-3 px-4 text-sm font-semibold border-b-2 whitespace-nowrap transition-all ${
              activeTab === 'summary'
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-slate-500 hover:text-slate-700'
            }`}
          >
            Evaluation Summary (Admin)
          </button>
        )}

        <button
          onClick={() => setActiveTab('ledger')}
          className={`pb-3 px-4 text-sm font-semibold border-b-2 whitespace-nowrap transition-all ${
            activeTab === 'ledger'
              ? 'border-blue-600 text-blue-600'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          Filterable Ledger
        </button>
      </div>

      {/* ============================================================ */}
      {/* TAB 1: INDIVIDUAL SUPPLIER PERFORMANCE REPORT */}
      {/* ============================================================ */}
      {activeTab === 'supplier' && (
        <div className="space-y-6">
          {/* Controls Card */}
          <Card bodyClassName="p-4 print:hidden">
            <div className="grid grid-cols-1 sm:grid-cols-3 lg:grid-cols-4 gap-3 items-end">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Select Supplier</label>
                <select
                  value={selectedSupplierId}
                  onChange={(e) => setSelectedSupplierId(e.target.value)}
                  className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  {suppliers.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.supplierCode} - {s.name}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">From Date</label>
                <input
                  type="date"
                  value={supplierStartDate}
                  onChange={(e) => setSupplierStartDate(e.target.value)}
                  className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">To Date</label>
                <input
                  type="date"
                  value={supplierEndDate}
                  onChange={(e) => setSupplierEndDate(e.target.value)}
                  className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div className="flex gap-2">
                <Button variant="primary" size="md" onClick={loadSupplierReport} loading={loading}>
                  Generate
                </Button>
              </div>
            </div>

            {/* Export Buttons */}
            <div className="mt-4 pt-3 border-t border-slate-100 flex flex-wrap items-center gap-2">
              <span className="text-xs font-semibold text-slate-500 mr-2">Download Document:</span>
              <Button
                variant="secondary"
                size="sm"
                icon={FileText}
                disabled={exporting || !supplierReport}
                onClick={() => handleExportSupplier('pdf')}
              >
                PDF Format
              </Button>
              <Button
                variant="secondary"
                size="sm"
                icon={FileSpreadsheet}
                disabled={exporting || !supplierReport}
                onClick={() => handleExportSupplier('excel')}
              >
                Excel (.xlsx)
              </Button>
              <Button
                variant="secondary"
                size="sm"
                icon={FileCode}
                disabled={exporting || !supplierReport}
                onClick={() => handleExportSupplier('csv')}
              >
                CSV Format
              </Button>
            </div>
          </Card>

          {/* Supplier Report View */}
          {supplierReport ? (
            <div className="space-y-6">
              {/* Report Header Card */}
              <Card>
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-4">
                  <div>
                    <span className="text-xs font-mono text-blue-600 font-bold uppercase tracking-wider">
                      {supplierReport.supplierCode}
                    </span>
                    <h2 className="text-2xl font-bold text-slate-900 mt-0.5">{supplierReport.supplierName}</h2>
                    <p className="text-xs text-slate-500 mt-0.5">Category: {supplierReport.categoryName || 'General'}</p>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="text-right">
                      <span className="text-xs text-slate-400 block">Performance Status</span>
                      <span className="text-sm font-bold text-slate-800">{supplierReport.performanceStatusDisplayName}</span>
                    </div>
                    <Badge variant={supplierReport.active ? 'success' : 'danger'} size="md">
                      {supplierReport.active ? 'ACTIVE' : 'INACTIVE'}
                    </Badge>
                  </div>
                </div>

                {/* Score Stats Grid */}
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 pt-4">
                  <div>
                    <span className="text-xs text-slate-400">Latest Score</span>
                    <p className="text-2xl font-bold text-blue-600">{supplierReport.latestScore?.toFixed(1) || 'N/A'}</p>
                  </div>
                  <div>
                    <span className="text-xs text-slate-400">Rating Tier</span>
                    <p className="text-sm font-bold text-slate-900 mt-1">{supplierReport.latestRatingDisplayName || 'N/A'}</p>
                  </div>
                  <div>
                    <span className="text-xs text-slate-400">Trend</span>
                    <p className="text-sm font-bold text-slate-900 mt-1">{supplierReport.performanceTrendDisplayName || 'N/A'}</p>
                  </div>
                  <div>
                    <span className="text-xs text-slate-400">Score Delta</span>
                    <p className="text-sm font-bold text-slate-900 mt-1">
                      {supplierReport.scoreDifference != null
                        ? `${supplierReport.scoreDifference >= 0 ? '+' : ''}${supplierReport.scoreDifference.toFixed(1)} pts`
                        : 'Baseline'}
                    </p>
                  </div>
                </div>
              </Card>

              {/* History Table */}
              <Card title="Rating History Records" bodyClassName="p-0">
                <table className="w-full text-left text-xs border-collapse">
                  <thead className="bg-slate-50 border-b border-slate-200">
                    <tr>
                      <th className="py-2.5 px-4 font-semibold text-slate-700">Date</th>
                      <th className="py-2.5 px-4 font-semibold text-slate-700">Score</th>
                      <th className="py-2.5 px-4 font-semibold text-slate-700">Rating</th>
                      <th className="py-2.5 px-4 font-semibold text-slate-700">Status</th>
                      <th className="py-2.5 px-4 font-semibold text-slate-700">Evaluation Code</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {supplierReport.ratingHistory?.length > 0 ? (
                      supplierReport.ratingHistory.map((h, i) => (
                        <tr key={i} className="hover:bg-slate-50/50">
                          <td className="py-2.5 px-4 font-medium text-slate-800">{h.ratingDate}</td>
                          <td className="py-2.5 px-4 font-bold text-slate-900">{h.score?.toFixed(1)}</td>
                          <td className="py-2.5 px-4">{h.ratingDisplayName}</td>
                          <td className="py-2.5 px-4">{h.performanceStatusDisplayName}</td>
                          <td className="py-2.5 px-4 font-mono text-blue-600">{h.evaluationCode || 'N/A'}</td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan={5} className="py-4 text-center text-slate-400">
                          No rating history found for the selected period.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </Card>
            </div>
          ) : (
            <Card bodyClassName="p-8 text-center text-slate-400">
              Select a supplier and click Generate to view the performance report.
            </Card>
          )}
        </div>
      )}

      {/* ============================================================ */}
      {/* TAB 2: EVALUATION SCORECARD REPORT */}
      {/* ============================================================ */}
      {activeTab === 'evaluation' && (
        <div className="space-y-6">
          <Card bodyClassName="p-4 print:hidden">
            <div className="flex flex-wrap items-end gap-3">
              <div className="flex-1 min-w-[240px]">
                <label className="block text-xs font-semibold text-slate-700 mb-1">Select Evaluation</label>
                <select
                  value={selectedEvalId}
                  onChange={(e) => setSelectedEvalId(e.target.value)}
                  className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  {evaluations.map((ev) => (
                    <option key={ev.id} value={ev.id}>
                      {ev.evaluationCode} - {ev.supplierName || `Supplier #${ev.supplierId}`} ({ev.status})
                    </option>
                  ))}
                </select>
              </div>

              <Button variant="primary" onClick={loadEvaluationReport} loading={loading}>
                Generate
              </Button>
            </div>

            <div className="mt-4 pt-3 border-t border-slate-100 flex flex-wrap items-center gap-2">
              <span className="text-xs font-semibold text-slate-500 mr-2">Download Document:</span>
              <Button
                variant="secondary"
                size="sm"
                icon={FileText}
                disabled={exporting || !evaluationReport}
                onClick={() => handleExportEvaluation('pdf')}
              >
                PDF Scorecard
              </Button>
              <Button
                variant="secondary"
                size="sm"
                icon={FileSpreadsheet}
                disabled={exporting || !evaluationReport}
                onClick={() => handleExportEvaluation('excel')}
              >
                Excel (.xlsx)
              </Button>
              <Button
                variant="secondary"
                size="sm"
                icon={FileCode}
                disabled={exporting || !evaluationReport}
                onClick={() => handleExportEvaluation('csv')}
              >
                CSV Format
              </Button>
            </div>
          </Card>

          {evaluationReport ? (
            <div className="space-y-6">
              <Card>
                <div className="flex flex-col sm:flex-row justify-between border-b border-slate-100 pb-4">
                  <div>
                    <span className="text-xs font-mono text-blue-600 font-bold uppercase">{evaluationReport.evaluationCode}</span>
                    <h2 className="text-2xl font-bold text-slate-900 mt-0.5">{evaluationReport.supplierName}</h2>
                    <p className="text-xs text-slate-500 mt-0.5">
                      Evaluator: {evaluationReport.evaluatorName} • Date: {evaluationReport.evaluationDate}
                    </p>
                  </div>
                  <div className="text-right">
                    <span className="text-xs text-slate-400 block">Total Weighted Score</span>
                    <span className="text-3xl font-extrabold text-blue-600">{evaluationReport.totalScore?.toFixed(1)}%</span>
                  </div>
                </div>

                {/* Criteria Table */}
                <div className="mt-4">
                  <h4 className="text-xs font-semibold text-slate-500 uppercase mb-2">Criteria Breakdown</h4>
                  <table className="w-full text-left text-xs border-collapse">
                    <thead className="bg-slate-50 border-b border-slate-200">
                      <tr>
                        <th className="py-2 px-3 font-semibold text-slate-700">Criterion</th>
                        <th className="py-2 px-3 font-semibold text-slate-700">Weight</th>
                        <th className="py-2 px-3 font-semibold text-slate-700">Score</th>
                        <th className="py-2 px-3 font-semibold text-slate-700">Weighted</th>
                        <th className="py-2 px-3 font-semibold text-slate-700">Remarks</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {evaluationReport.criteriaScores?.map((c, i) => (
                        <tr key={i}>
                          <td className="py-2 px-3 font-semibold text-slate-800">{c.criteriaName}</td>
                          <td className="py-2 px-3">{c.weight}%</td>
                          <td className="py-2 px-3">{c.rawScore} / {c.maxScore}</td>
                          <td className="py-2 px-3 font-bold text-blue-600">{c.weightedScore?.toFixed(2)}</td>
                          <td className="py-2 px-3 text-slate-500">{c.comments || '-'}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                {/* Qualitative Feedback */}
                {(evaluationReport.strengths || evaluationReport.areasForImprovement || evaluationReport.recommendation) && (
                  <div className="mt-4 pt-4 border-t border-slate-100 grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
                    {evaluationReport.strengths && (
                      <div className="bg-emerald-50 p-2.5 rounded-lg border border-emerald-100">
                        <span className="font-bold text-emerald-800 block">Strengths:</span>
                        <p className="text-emerald-700 mt-1">{evaluationReport.strengths}</p>
                      </div>
                    )}
                    {evaluationReport.areasForImprovement && (
                      <div className="bg-amber-50 p-2.5 rounded-lg border border-amber-100">
                        <span className="font-bold text-amber-800 block">Areas for Improvement:</span>
                        <p className="text-amber-700 mt-1">{evaluationReport.areasForImprovement}</p>
                      </div>
                    )}
                    {evaluationReport.recommendation && (
                      <div className="bg-blue-50 p-2.5 rounded-lg border border-blue-100">
                        <span className="font-bold text-blue-800 block">Recommendation:</span>
                        <p className="text-blue-700 mt-1">{evaluationReport.recommendation}</p>
                      </div>
                    )}
                  </div>
                )}
              </Card>
            </div>
          ) : (
            <Card bodyClassName="p-8 text-center text-slate-400">
              Select an evaluation record and click Generate.
            </Card>
          )}
        </div>
      )}

      {/* ============================================================ */}
      {/* TAB 3: OVERALL SYSTEM PERFORMANCE REPORT (ADMIN) */}
      {/* ============================================================ */}
      {activeTab === 'overall' && isAdmin() && (
        <div className="space-y-6">
          <Card bodyClassName="p-4 print:hidden">
            <div className="flex flex-wrap items-end justify-between gap-3">
              <div className="flex flex-wrap items-center gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Start Date</label>
                  <input
                    type="date"
                    value={overallStartDate}
                    onChange={(e) => setOverallStartDate(e.target.value)}
                    className="px-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">End Date</label>
                  <input
                    type="date"
                    value={overallEndDate}
                    onChange={(e) => setOverallEndDate(e.target.value)}
                    className="px-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <Button variant="primary" size="sm" onClick={loadOverallReport} loading={loading}>
                  Generate Report
                </Button>
              </div>

              <div className="flex gap-2">
                <Button
                  variant="secondary"
                  size="sm"
                  icon={FileText}
                  disabled={exporting || !overallReport}
                  onClick={() => handleExportOverall('pdf')}
                >
                  Export PDF
                </Button>
                <Button
                  variant="secondary"
                  size="sm"
                  icon={FileSpreadsheet}
                  disabled={exporting || !overallReport}
                  onClick={() => handleExportOverall('excel')}
                >
                  Export Excel
                </Button>
                <Button
                  variant="secondary"
                  size="sm"
                  icon={FileCode}
                  disabled={exporting || !overallReport}
                  onClick={() => handleExportOverall('csv')}
                >
                  Export CSV
                </Button>
              </div>
            </div>
          </Card>

          {overallReport && (
            <div className="space-y-6">
              <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
                <Card>
                  <span className="text-xs text-slate-400">Total Suppliers</span>
                  <h3 className="text-3xl font-extrabold text-slate-900 mt-1">{overallReport.totalSuppliers}</h3>
                  <p className="text-xs text-slate-500 mt-1">
                    {overallReport.activeSuppliers} Active • {overallReport.inactiveSuppliers} Inactive
                  </p>
                </Card>

                <Card>
                  <span className="text-xs text-slate-400">Average Performance</span>
                  <h3 className="text-3xl font-extrabold text-blue-600 mt-1">{overallReport.averagePerformanceScore?.toFixed(1)}%</h3>
                  <p className="text-xs text-slate-500 mt-1">Across {overallReport.totalRatedSuppliers} rated vendors</p>
                </Card>

                <Card>
                  <span className="text-xs text-slate-400">Highest Score</span>
                  <h3 className="text-3xl font-extrabold text-emerald-600 mt-1">{overallReport.highestPerformanceScore?.toFixed(1)}%</h3>
                  <p className="text-xs text-slate-500 mt-1">Performance ceiling</p>
                </Card>

                <Card>
                  <span className="text-xs text-slate-400">Lowest Score</span>
                  <h3 className="text-3xl font-extrabold text-amber-600 mt-1">{overallReport.lowestPerformanceScore?.toFixed(1)}%</h3>
                  <p className="text-xs text-slate-500 mt-1">Underperforming baseline</p>
                </Card>
              </div>
            </div>
          )}
        </div>
      )}

      {/* ============================================================ */}
      {/* TAB 4: EVALUATION LIFECYCLE SUMMARY REPORT (ADMIN) */}
      {/* ============================================================ */}
      {activeTab === 'summary' && isAdmin() && (
        <div className="space-y-6">
          <Card>
            <h3 className="text-lg font-bold text-slate-900">Evaluation Lifecycle Status Breakdown</h3>
            <p className="text-xs text-slate-500 mt-1 mb-4">Real-time audit count across evaluation workflow stages</p>

            <div className="grid grid-cols-2 sm:grid-cols-5 gap-3">
              <div className="bg-slate-50 p-3 rounded-lg border border-slate-200">
                <span className="text-xs text-slate-500">Total Audits</span>
                <p className="text-2xl font-bold text-slate-900 mt-1">{summaryReport?.totalEvaluations || 0}</p>
              </div>
              <div className="bg-emerald-50 p-3 rounded-lg border border-emerald-200">
                <span className="text-xs text-emerald-600">Completed</span>
                <p className="text-2xl font-bold text-emerald-700 mt-1">{summaryReport?.completedEvaluations || 0}</p>
              </div>
              <div className="bg-blue-50 p-3 rounded-lg border border-blue-200">
                <span className="text-xs text-blue-600">Submitted</span>
                <p className="text-2xl font-bold text-blue-700 mt-1">{summaryReport?.submittedEvaluations || 0}</p>
              </div>
              <div className="bg-amber-50 p-3 rounded-lg border border-amber-200">
                <span className="text-xs text-amber-600">Draft</span>
                <p className="text-2xl font-bold text-amber-700 mt-1">{summaryReport?.draftEvaluations || 0}</p>
              </div>
              <div className="bg-rose-50 p-3 rounded-lg border border-rose-200">
                <span className="text-xs text-rose-600">Cancelled</span>
                <p className="text-2xl font-bold text-rose-700 mt-1">{summaryReport?.cancelledEvaluations || 0}</p>
              </div>
            </div>
          </Card>
        </div>
      )}

      {/* ============================================================ */}
      {/* TAB 5: FILTERABLE LEDGER */}
      {/* ============================================================ */}
      {activeTab === 'ledger' && (
        <div className="space-y-6">
          <Card bodyClassName="p-4 print:hidden">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
              <input
                type="text"
                placeholder="Search supplier..."
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                className="px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />

              <select
                value={categoryId}
                onChange={(e) => setCategoryId(e.target.value)}
                className="px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">All Categories</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </select>

              <select
                value={ratingCategory}
                onChange={(e) => setRatingCategory(e.target.value)}
                className="px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">All Rating Tiers</option>
                <option value="EXCELLENT">EXCELLENT (≥85%)</option>
                <option value="GOOD">GOOD (70-84%)</option>
                <option value="AVERAGE">AVERAGE (50-69%)</option>
                <option value="POOR">POOR (&lt;50%)</option>
              </select>

              <select
                value={status}
                onChange={(e) => setStatus(e.target.value)}
                className="px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">All Operational Statuses</option>
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
              </select>
            </div>
          </Card>

          <Card title="Filtered Supplier Performance Ledger" bodyClassName="p-0">
            <table className="w-full text-left text-xs border-collapse">
              <thead className="bg-slate-50 border-b border-slate-200">
                <tr>
                  <th className="py-2.5 px-4 font-semibold text-slate-700">Code</th>
                  <th className="py-2.5 px-4 font-semibold text-slate-700">Supplier Name</th>
                  <th className="py-2.5 px-4 font-semibold text-slate-700">Category</th>
                  <th className="py-2.5 px-4 font-semibold text-slate-700">Evaluations</th>
                  <th className="py-2.5 px-4 font-semibold text-slate-700">Rating</th>
                  <th className="py-2.5 px-4 font-semibold text-slate-700">Tier</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {ledgerData?.suppliers?.map((s) => (
                  <tr key={s.id} className="hover:bg-slate-50/50">
                    <td className="py-2.5 px-4 font-mono font-bold text-blue-600">{s.supplierCode}</td>
                    <td className="py-2.5 px-4 font-semibold text-slate-800">{s.name}</td>
                    <td className="py-2.5 px-4 text-slate-600">{s.category?.name || 'N/A'}</td>
                    <td className="py-2.5 px-4 text-slate-600">{s.totalEvaluations} records</td>
                    <td className="py-2.5 px-4 font-bold text-slate-900">
                      {s.totalEvaluations > 0 ? `${s.overallRating}%` : 'Unrated'}
                    </td>
                    <td className="py-2.5 px-4">
                      <Badge variant={s.ratingCategory} size="sm" />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Card>
        </div>
      )}
    </div>
  );
};
