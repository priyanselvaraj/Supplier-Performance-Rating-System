import React, { useState, useEffect } from 'react';
import {
  Sliders,
  Plus,
  Search,
  Filter,
  CheckCircle2,
  AlertTriangle,
  XCircle,
  Edit2,
  Trash2,
  Play,
  RefreshCw,
  Power,
  Info,
  Layers,
  Sparkles,
  ArrowUpRight,
  TrendingUp,
  TrendingDown,
  X,
  Check
} from 'lucide-react';
import { biService } from '../../services/bi.service';

const KPI_CATEGORIES = [
  'ALL',
  'QUALITY',
  'DELIVERY',
  'COST',
  'SLA',
  'RISK',
  'COMPLIANCE',
  'OPERATIONAL'
];

const CALCULATION_TYPES = [
  { value: 'AVERAGE_RATING', label: 'Average Supplier Rating' },
  { value: 'CRITERIA_SCORE_AVG', label: 'Criteria Score Average' },
  { value: 'ON_TIME_DELIVERY_RATE', label: 'On-Time Delivery Rate (%)' },
  { value: 'QUALITY_ACCEPTANCE_RATE', label: 'Quality Acceptance Rate (%)' },
  { value: 'COMPLIANCE_RATE', label: 'Compliance & Audit Rate (%)' },
  { value: 'HIGH_RISK_SUPPLIER_COUNT', label: 'High Risk Supplier Count' },
  { value: 'CAP_CLOSURE_RATE', label: 'CAP Resolution Closure Rate (%)' },
  { value: 'PENDING_APPROVALS_COUNT', label: 'Pending Workflow Approvals Count' },
  { value: 'ESCALATED_WORKFLOWS_COUNT', label: 'Escalated Workflows Count' },
  { value: 'ACTIVE_SUPPLIERS_COUNT', label: 'Active Suppliers Count' }
];

export function KpiAdmin() {
  const [kpis, setKpis] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [statusFilter, setStatusFilter] = useState('ALL');

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState('create'); // 'create' or 'edit'
  const [editingKpiId, setEditingKpiId] = useState(null);
  const [formData, setFormData] = useState({
    kpiCode: '',
    name: '',
    description: '',
    category: 'QUALITY',
    calculationType: 'AVERAGE_RATING',
    unit: '%',
    targetValue: 90,
    warningThreshold: 80,
    criticalThreshold: 70,
    higherIsBetter: true,
    active: true
  });
  const [formSubmitting, setFormSubmitting] = useState(false);
  const [formError, setFormError] = useState(null);

  // Live Test Modal
  const [testResult, setTestResult] = useState(null);
  const [testingKpi, setTestingKpi] = useState(null);
  const [isTestModalOpen, setIsTestModalOpen] = useState(false);

  // Action feedback
  const [actionSuccess, setActionSuccess] = useState(null);

  const fetchKpis = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await biService.getAllKpiDefinitions();
      const list = Array.isArray(res) ? res : res.data || [];
      setKpis(list);
    } catch (err) {
      console.error('Failed to load KPIs:', err);
      setError(err.response?.data?.message || 'Failed to fetch KPI definitions');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchKpis();
  }, []);

  const showNotification = (msg) => {
    setActionSuccess(msg);
    setTimeout(() => {
      setActionSuccess(null);
    }, 4000);
  };

  const handleOpenCreateModal = () => {
    setModalMode('create');
    setEditingKpiId(null);
    setFormData({
      kpiCode: '',
      name: '',
      description: '',
      category: 'QUALITY',
      calculationType: 'AVERAGE_RATING',
      unit: '%',
      targetValue: 90,
      warningThreshold: 80,
      criticalThreshold: 70,
      higherIsBetter: true,
      active: true
    });
    setFormError(null);
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (kpi) => {
    setModalMode('edit');
    setEditingKpiId(kpi.id);
    setFormData({
      kpiCode: kpi.kpiCode || '',
      name: kpi.name || '',
      description: kpi.description || '',
      category: kpi.category || 'QUALITY',
      calculationType: kpi.calculationType || 'AVERAGE_RATING',
      unit: kpi.unit || '%',
      targetValue: kpi.targetValue ?? 90,
      warningThreshold: kpi.warningThreshold ?? 80,
      criticalThreshold: kpi.criticalThreshold ?? 70,
      higherIsBetter: kpi.higherIsBetter !== false,
      active: kpi.active !== false
    });
    setFormError(null);
    setIsModalOpen(true);
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    setFormSubmitting(true);
    setFormError(null);

    try {
      const payload = {
        ...formData,
        targetValue: parseFloat(formData.targetValue),
        warningThreshold: parseFloat(formData.warningThreshold),
        criticalThreshold: parseFloat(formData.criticalThreshold)
      };

      if (modalMode === 'create') {
        await biService.createKpiDefinition(payload);
        showNotification(`KPI "${formData.name}" created successfully.`);
      } else {
        await biService.updateKpiDefinition(editingKpiId, payload);
        showNotification(`KPI "${formData.name}" updated successfully.`);
      }

      setIsModalOpen(false);
      fetchKpis();
    } catch (err) {
      console.error('Failed to save KPI:', err);
      setFormError(err.response?.data?.message || 'Failed to save KPI definition.');
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleToggleActive = async (kpi) => {
    try {
      await biService.toggleKpiDefinition(kpi.id);
      showNotification(`KPI "${kpi.name}" status toggled.`);
      fetchKpis();
    } catch (err) {
      console.error('Failed to toggle KPI:', err);
      alert(err.response?.data?.message || 'Failed to toggle KPI status.');
    }
  };

  const handleDelete = async (kpi) => {
    if (!window.confirm(`Are you sure you want to delete KPI "${kpi.name}"?`)) {
      return;
    }
    try {
      await biService.deleteKpiDefinition(kpi.id);
      showNotification(`KPI "${kpi.name}" deleted.`);
      fetchKpis();
    } catch (err) {
      console.error('Failed to delete KPI:', err);
      alert(err.response?.data?.message || 'Failed to delete KPI.');
    }
  };

  const handleTestKpi = async (kpi) => {
    try {
      setTestingKpi(kpi);
      setTestResult(null);
      setIsTestModalOpen(true);
      const res = await biService.calculateKpiById(kpi.id);
      setTestResult(res.data || res);
    } catch (err) {
      console.error('Failed to calculate KPI test:', err);
      setTestResult({
        error: err.response?.data?.message || 'Calculation failed'
      });
    }
  };

  // Filtered list
  const filteredKpis = kpis.filter((kpi) => {
    const matchesSearch =
      (kpi.name || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
      (kpi.kpiCode || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
      (kpi.description || '').toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory =
      selectedCategory === 'ALL' || kpi.category === selectedCategory;
    const matchesStatus =
      statusFilter === 'ALL' ||
      (statusFilter === 'ACTIVE' && kpi.active) ||
      (statusFilter === 'INACTIVE' && !kpi.active);
    return matchesSearch && matchesCategory && matchesStatus;
  });

  return (
    <div className="space-y-6 pb-12">
      {/* Action Notification */}
      {actionSuccess && (
        <div className="fixed bottom-6 right-6 z-50 flex items-center gap-3 rounded-xl bg-slate-900 px-5 py-3.5 text-white shadow-2xl border border-slate-700 animate-slide-up">
          <CheckCircle2 className="h-5 w-5 text-emerald-400 flex-shrink-0" />
          <span className="text-sm font-medium">{actionSuccess}</span>
        </div>
      )}

      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 rounded-2xl bg-white p-6 shadow-sm border border-slate-200">
        <div>
          <div className="inline-flex items-center gap-2 rounded-full bg-blue-50 px-3 py-1 text-xs font-semibold text-blue-700 border border-blue-100">
            <Sliders className="h-3.5 w-3.5" />
            Governance & Rules Engine
          </div>
          <h1 className="mt-2 text-2xl font-bold tracking-tight text-slate-900">
            KPI Definition & Threshold Manager
          </h1>
          <p className="mt-1 text-sm text-slate-500">
            Configure system-wide KPIs, mathematical formulas, evaluation thresholds, and operational baselines.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={fetchKpis}
            className="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50 transition"
          >
            <RefreshCw className="h-4 w-4 text-slate-500" />
            Refresh
          </button>
          <button
            onClick={handleOpenCreateModal}
            className="inline-flex items-center gap-2 rounded-xl bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white hover:bg-blue-700 transition shadow-sm"
          >
            <Plus className="h-4 w-4" />
            Create KPI
          </button>
        </div>
      </div>

      {/* Filters Bar */}
      <div className="flex flex-col md:flex-row items-center gap-4 rounded-2xl bg-white p-4 shadow-sm border border-slate-200">
        <div className="relative flex-1 w-full">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
          <input
            type="text"
            placeholder="Search KPI by code, name, or description..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full rounded-xl border border-slate-200 pl-10 pr-4 py-2 text-sm focus:border-blue-500 focus:outline-none"
          />
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto">
          <div className="flex items-center gap-2">
            <Filter className="h-4 w-4 text-slate-400" />
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              className="rounded-xl border border-slate-200 px-3 py-2 text-xs font-semibold text-slate-700 focus:border-blue-500 focus:outline-none"
            >
              {KPI_CATEGORIES.map((cat) => (
                <option key={cat} value={cat}>
                  {cat === 'ALL' ? 'All Categories' : cat}
                </option>
              ))}
            </select>
          </div>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="rounded-xl border border-slate-200 px-3 py-2 text-xs font-semibold text-slate-700 focus:border-blue-500 focus:outline-none"
          >
            <option value="ALL">All Status</option>
            <option value="ACTIVE">Active Only</option>
            <option value="INACTIVE">Inactive Only</option>
          </select>
        </div>
      </div>

      {/* KPI Table */}
      <div className="rounded-2xl border border-slate-200 bg-white shadow-sm overflow-hidden">
        {loading ? (
          <div className="flex h-64 w-full items-center justify-center">
            <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-600 border-t-transparent" />
          </div>
        ) : error ? (
          <div className="p-8 text-center text-rose-600 text-sm">
            <AlertTriangle className="mx-auto h-8 w-8 mb-2" />
            {error}
          </div>
        ) : filteredKpis.length === 0 ? (
          <div className="p-12 text-center text-slate-500 text-sm">
            No KPI definitions found matching the selected filters.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 border-b border-slate-200 text-xs font-semibold text-slate-600 uppercase tracking-wider">
                <tr>
                  <th className="px-6 py-4">KPI Details</th>
                  <th className="px-6 py-4">Category</th>
                  <th className="px-6 py-4">Calculation Model</th>
                  <th className="px-6 py-4 text-center">Target</th>
                  <th className="px-6 py-4 text-center">Warning / Critical</th>
                  <th className="px-6 py-4 text-center">Active</th>
                  <th className="px-6 py-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredKpis.map((kpi) => (
                  <tr key={kpi.id} className="hover:bg-slate-50/80 transition">
                    <td className="px-6 py-4">
                      <div className="flex flex-col">
                        <div className="flex items-center gap-2">
                          <span className="font-bold text-slate-900">{kpi.name}</span>
                          <span className="rounded bg-slate-100 px-1.5 py-0.5 text-[10px] font-mono font-semibold text-slate-600">
                            {kpi.kpiCode}
                          </span>
                        </div>
                        {kpi.description && (
                          <p className="text-xs text-slate-500 mt-0.5 max-w-sm line-clamp-1">
                            {kpi.description}
                          </p>
                        )}
                      </div>
                    </td>

                    <td className="px-6 py-4">
                      <span className="inline-flex items-center rounded-md bg-blue-50 px-2.5 py-1 text-xs font-semibold text-blue-700 border border-blue-200">
                        {kpi.category}
                      </span>
                    </td>

                    <td className="px-6 py-4">
                      <span className="text-xs font-mono text-slate-600">
                        {kpi.calculationType}
                      </span>
                      <div className="text-[11px] text-slate-400 mt-0.5">
                        {kpi.higherIsBetter ? 'Higher is better' : 'Lower is better'}
                      </div>
                    </td>

                    <td className="px-6 py-4 text-center">
                      <span className="font-bold text-emerald-700">
                        {kpi.targetValue} {kpi.unit}
                      </span>
                    </td>

                    <td className="px-6 py-4 text-center">
                      <div className="inline-flex items-center gap-1.5 text-xs">
                        <span className="rounded bg-amber-50 px-1.5 py-0.5 font-semibold text-amber-700 border border-amber-200">
                          {kpi.warningThreshold} {kpi.unit}
                        </span>
                        <span className="text-slate-300">/</span>
                        <span className="rounded bg-rose-50 px-1.5 py-0.5 font-semibold text-rose-700 border border-rose-200">
                          {kpi.criticalThreshold} {kpi.unit}
                        </span>
                      </div>
                    </td>

                    <td className="px-6 py-4 text-center">
                      <button
                        onClick={() => handleToggleActive(kpi)}
                        className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold transition ${
                          kpi.active
                            ? 'bg-emerald-50 text-emerald-700 hover:bg-emerald-100 border border-emerald-200'
                            : 'bg-slate-100 text-slate-500 hover:bg-slate-200'
                        }`}
                      >
                        <Power className="h-3 w-3" />
                        {kpi.active ? 'Active' : 'Inactive'}
                      </button>
                    </td>

                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => handleTestKpi(kpi)}
                          title="Test / Recalculate Live"
                          className="p-1.5 text-blue-600 hover:bg-blue-50 rounded-lg transition"
                        >
                          <Play className="h-4 w-4" />
                        </button>
                        <button
                          onClick={() => handleOpenEditModal(kpi)}
                          title="Edit KPI"
                          className="p-1.5 text-slate-600 hover:bg-slate-100 rounded-lg transition"
                        >
                          <Edit2 className="h-4 w-4" />
                        </button>
                        <button
                          onClick={() => handleDelete(kpi)}
                          title="Delete KPI"
                          className="p-1.5 text-rose-600 hover:bg-rose-50 rounded-lg transition"
                        >
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Create / Edit Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 p-4 backdrop-blur-xs overflow-y-auto">
          <div className="relative w-full max-w-2xl rounded-2xl bg-white p-6 sm:p-8 shadow-2xl border border-slate-200 max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between pb-4 border-b border-slate-100">
              <div className="flex items-center gap-2">
                <Sliders className="h-5 w-5 text-blue-600" />
                <h3 className="text-lg font-bold text-slate-900">
                  {modalMode === 'create' ? 'Create KPI Definition' : 'Edit KPI Definition'}
                </h3>
              </div>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 p-1 rounded-lg"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            {formError && (
              <div className="mt-4 rounded-xl border border-rose-200 bg-rose-50 p-3.5 text-xs text-rose-700 font-medium">
                {formError}
              </div>
            )}

            <form onSubmit={handleFormSubmit} className="mt-6 space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    KPI Code <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. OTD_RATE"
                    value={formData.kpiCode}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        kpiCode: e.target.value.toUpperCase().replace(/\s+/g, '_')
                      })
                    }
                    className="w-full rounded-xl border border-slate-200 px-3.5 py-2 text-sm font-mono focus:border-blue-500 focus:outline-none"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    KPI Name <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. On-Time Delivery Rate"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="w-full rounded-xl border border-slate-200 px-3.5 py-2 text-sm focus:border-blue-500 focus:outline-none"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">
                  Description
                </label>
                <textarea
                  rows={2}
                  placeholder="Explain what this KPI measures and how it influences ratings..."
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full rounded-xl border border-slate-200 px-3.5 py-2 text-sm focus:border-blue-500 focus:outline-none"
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Category <span className="text-rose-500">*</span>
                  </label>
                  <select
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                    className="w-full rounded-xl border border-slate-200 px-3.5 py-2 text-sm focus:border-blue-500 focus:outline-none"
                  >
                    {KPI_CATEGORIES.filter((c) => c !== 'ALL').map((c) => (
                      <option key={c} value={c}>
                        {c}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Calculation Engine Type <span className="text-rose-500">*</span>
                  </label>
                  <select
                    value={formData.calculationType}
                    onChange={(e) =>
                      setFormData({ ...formData, calculationType: e.target.value })
                    }
                    className="w-full rounded-xl border border-slate-200 px-3.5 py-2 text-sm focus:border-blue-500 focus:outline-none"
                  >
                    {CALCULATION_TYPES.map((t) => (
                      <option key={t.value} value={t.value}>
                        {t.label}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Unit (e.g. %, pts)
                  </label>
                  <input
                    type="text"
                    value={formData.unit}
                    onChange={(e) => setFormData({ ...formData, unit: e.target.value })}
                    className="w-full rounded-xl border border-slate-200 px-3.5 py-2 text-sm focus:border-blue-500 focus:outline-none"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Target Value
                  </label>
                  <input
                    type="number"
                    step="0.1"
                    required
                    value={formData.targetValue}
                    onChange={(e) =>
                      setFormData({ ...formData, targetValue: e.target.value })
                    }
                    className="w-full rounded-xl border border-slate-200 px-3.5 py-2 text-sm focus:border-blue-500 focus:outline-none"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Warning Threshold
                  </label>
                  <input
                    type="number"
                    step="0.1"
                    required
                    value={formData.warningThreshold}
                    onChange={(e) =>
                      setFormData({ ...formData, warningThreshold: e.target.value })
                    }
                    className="w-full rounded-xl border border-slate-200 px-3.5 py-2 text-sm focus:border-blue-500 focus:outline-none"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Critical Threshold
                  </label>
                  <input
                    type="number"
                    step="0.1"
                    required
                    value={formData.criticalThreshold}
                    onChange={(e) =>
                      setFormData({ ...formData, criticalThreshold: e.target.value })
                    }
                    className="w-full rounded-xl border border-slate-200 px-3.5 py-2 text-sm focus:border-blue-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="flex items-center gap-6 pt-2">
                <label className="flex items-center gap-2 cursor-pointer text-xs font-semibold text-slate-700">
                  <input
                    type="checkbox"
                    checked={formData.higherIsBetter}
                    onChange={(e) =>
                      setFormData({ ...formData, higherIsBetter: e.target.checked })
                    }
                    className="rounded border-slate-300 text-blue-600 focus:ring-blue-500 h-4 w-4"
                  />
                  Higher is Better (Score increases as value increases)
                </label>

                <label className="flex items-center gap-2 cursor-pointer text-xs font-semibold text-slate-700">
                  <input
                    type="checkbox"
                    checked={formData.active}
                    onChange={(e) =>
                      setFormData({ ...formData, active: e.target.checked })
                    }
                    className="rounded border-slate-300 text-blue-600 focus:ring-blue-500 h-4 w-4"
                  />
                  Active (Include in automated scorecards)
                </label>
              </div>

              <div className="mt-6 flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50 transition"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={formSubmitting}
                  className="inline-flex items-center gap-2 rounded-xl bg-blue-600 px-5 py-2.5 text-sm font-semibold text-white hover:bg-blue-700 transition disabled:opacity-50"
                >
                  {formSubmitting && (
                    <RefreshCw className="h-4 w-4 animate-spin" />
                  )}
                  {modalMode === 'create' ? 'Create KPI' : 'Save Changes'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Live Calculation Test Modal */}
      {isTestModalOpen && testingKpi && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 p-4 backdrop-blur-xs">
          <div className="relative w-full max-w-md rounded-2xl bg-white p-6 shadow-2xl border border-slate-200">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <div className="flex items-center gap-2">
                <Play className="h-5 w-5 text-emerald-600" />
                <h3 className="text-base font-bold text-slate-900">Live KPI Engine Evaluation</h3>
              </div>
              <button
                onClick={() => setIsTestModalOpen(false)}
                className="text-slate-400 hover:text-slate-600"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            <div className="mt-4">
              <p className="text-xs text-slate-500">Testing Formula for:</p>
              <h4 className="text-sm font-bold text-slate-900">{testingKpi.name}</h4>
              <span className="font-mono text-xs text-slate-500">({testingKpi.kpiCode})</span>

              <div className="mt-4 rounded-xl border border-slate-200 bg-slate-50 p-4">
                {!testResult ? (
                  <div className="flex items-center justify-center py-6">
                    <RefreshCw className="h-6 w-6 animate-spin text-blue-600" />
                  </div>
                ) : testResult.error ? (
                  <div className="text-center text-xs text-rose-600 font-medium">
                    {testResult.error}
                  </div>
                ) : (
                  <div className="space-y-3">
                    <div className="flex items-center justify-between">
                      <span className="text-xs text-slate-500">Current Aggregated Value:</span>
                      <span className="text-base font-bold text-slate-900">
                        {testResult.currentValue != null
                          ? `${testResult.currentValue.toFixed(1)} ${testResult.unit || ''}`
                          : 'N/A'}
                      </span>
                    </div>

                    <div className="flex items-center justify-between">
                      <span className="text-xs text-slate-500">Target Threshold:</span>
                      <span className="text-xs font-semibold text-slate-700">
                        {testResult.targetValue} {testResult.unit || ''}
                      </span>
                    </div>

                    <div className="flex items-center justify-between">
                      <span className="text-xs text-slate-500">Computed Health Status:</span>
                      <span
                        className={`inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-bold ${
                          testResult.status === 'GOOD'
                            ? 'bg-emerald-100 text-emerald-800'
                            : testResult.status === 'WARNING'
                            ? 'bg-amber-100 text-amber-800'
                            : 'bg-rose-100 text-rose-800'
                        }`}
                      >
                        {testResult.status || 'NO_DATA'}
                      </span>
                    </div>

                    <div className="flex items-center justify-between">
                      <span className="text-xs text-slate-500">Achievement Rate:</span>
                      <span className="text-xs font-bold text-indigo-700">
                        {testResult.achievementPercentage != null
                          ? `${testResult.achievementPercentage.toFixed(1)}%`
                          : 'N/A'}
                      </span>
                    </div>
                  </div>
                )}
              </div>
            </div>

            <div className="mt-6 flex justify-end">
              <button
                onClick={() => setIsTestModalOpen(false)}
                className="rounded-xl bg-slate-900 px-4 py-2 text-xs font-semibold text-white hover:bg-slate-800 transition"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default KpiAdmin;
