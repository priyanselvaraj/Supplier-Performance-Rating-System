import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { criteriaService } from '../../services/criteria.service';
import { Card } from '../../components/common/Card';
import { Table } from '../../components/common/Table';
import { Button } from '../../components/common/Button';
import { Input } from '../../components/common/Input';
import { Badge } from '../../components/common/Badge';
import { Modal } from '../../components/common/Modal';
import { Toast } from '../../components/common/Toast';
import { PlusCircle, Edit2, Trash2, CheckCircle2, AlertTriangle } from 'lucide-react';

export const CriteriaList = () => {
  const { isAdmin } = useAuth();

  const [criteria, setCriteria] = useState([]);
  const [totalWeight, setTotalWeight] = useState(0);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState({ message: '', type: 'success' });

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCriteria, setEditingCriteria] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    code: '',
    description: '',
    weight: 20.0,
    maxScore: 100.0,
    displayOrder: 1,
    active: true,
  });
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState('');

  const fetchCriteria = async () => {
    setLoading(true);
    try {
      const [listRes, weightRes] = await Promise.all([
        criteriaService.getAllCriteria(),
        criteriaService.getTotalActiveWeights(),
      ]);

      if (listRes.success) setCriteria(listRes.data);
      if (weightRes.success) setTotalWeight(weightRes.data || 0);
    } catch (err) {
      console.error(err);
      setToast({ message: 'Failed to load criteria', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCriteria();
  }, []);

  const handleOpenAdd = () => {
    setEditingCriteria(null);
    setFormData({
      name: '',
      code: '',
      description: '',
      weight: 20.0,
      maxScore: 100.0,
      displayOrder: criteria.length + 1,
      active: true,
    });
    setFormError('');
    setIsModalOpen(true);
  };

  const handleOpenEdit = (crit) => {
    setEditingCriteria(crit);
    setFormData({
      name: crit.name || '',
      code: crit.code || '',
      description: crit.description || '',
      weight: crit.weight || 0,
      maxScore: crit.maxScore || 100,
      displayOrder: crit.displayOrder || 1,
      active: crit.active,
    });
    setFormError('');
    setIsModalOpen(true);
  };

  const handleToggleActive = async (id) => {
    try {
      await criteriaService.toggleCriteriaActive(id);
      fetchCriteria();
      setToast({ message: 'Criteria status toggled', type: 'success' });
    } catch (err) {
      console.error(err);
      setToast({ message: 'Failed to update criteria status', type: 'error' });
    }
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Are you sure you want to delete criteria "${name}"?`)) return;

    try {
      await criteriaService.deleteCriteria(id);
      setToast({ message: `Criteria "${name}" deleted`, type: 'success' });
      fetchCriteria();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete criteria';
      setToast({ message: msg, type: 'error' });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.name.trim() || formData.weight === '') {
      setFormError('Criteria name and weight are required');
      return;
    }

    setSubmitting(true);
    setFormError('');

    try {
      if (editingCriteria) {
        await criteriaService.updateCriteria(editingCriteria.id, formData);
        setToast({ message: 'Criteria updated successfully', type: 'success' });
      } else {
        await criteriaService.createCriteria(formData);
        setToast({ message: 'Criteria created successfully', type: 'success' });
      }
      setIsModalOpen(false);
      fetchCriteria();
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Operation failed';
      setFormError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  const columns = [
    {
      header: 'Order',
      accessor: 'displayOrder',
      render: (row) => <span className="font-semibold text-slate-500">#{row.displayOrder}</span>,
    },
    {
      header: 'Criteria Name',
      accessor: 'name',
      render: (row) => (
        <div>
          <span className="font-semibold text-slate-900 block">{row.name}</span>
          <span className="text-xs text-slate-500">{row.description || '-'}</span>
        </div>
      ),
    },
    {
      header: 'Weight (%)',
      accessor: 'weight',
      render: (row) => (
        <span className="font-bold text-slate-900 bg-blue-50 text-blue-700 px-2.5 py-1 rounded-md text-xs">
          {row.weight}%
        </span>
      ),
    },
    {
      header: 'Max Score',
      accessor: 'maxScore',
      render: (row) => <span className="text-xs font-semibold text-slate-700">{row.maxScore} pts</span>,
    },
    {
      header: 'Status',
      accessor: 'active',
      render: (row) => (
        <button
          onClick={() => handleToggleActive(row.id)}
          className={`px-2.5 py-1 text-xs font-semibold rounded-full border transition-colors ${
            row.active
              ? 'bg-emerald-50 text-emerald-700 border-emerald-200 hover:bg-emerald-100'
              : 'bg-slate-100 text-slate-500 border-slate-200 hover:bg-slate-200'
          }`}
        >
          {row.active ? 'Active' : 'Inactive'}
        </button>
      ),
    },
    {
      header: 'Actions',
      className: 'text-right',
      cellClassName: 'text-right',
      render: (row) => (
        <div className="flex items-center justify-end gap-2">
          <button
            onClick={() => handleOpenEdit(row)}
            className="p-1.5 text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
          >
            <Edit2 className="h-4 w-4" />
          </button>
          <button
            onClick={() => handleDelete(row.id, row.name)}
            className="p-1.5 text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
          >
            <Trash2 className="h-4 w-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {toast.message && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast({ message: '', type: 'success' })}
        />
      )}

      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Evaluation Criteria & Weights</h1>
          <p className="text-sm text-slate-500 mt-1">
            Configure scoring parameters, dynamic weights, and evaluation formulas.
          </p>
        </div>
        <Button variant="primary" icon={PlusCircle} onClick={handleOpenAdd}>
          Add Criteria
        </Button>
      </div>

      {/* Total Weight Status Card */}
      <div
        className={`p-4 rounded-xl border flex items-center justify-between gap-4 ${
          totalWeight === 100
            ? 'bg-emerald-50 border-emerald-200 text-emerald-900'
            : 'bg-amber-50 border-amber-200 text-amber-900'
        }`}
      >
        <div className="flex items-center gap-3">
          {totalWeight === 100 ? (
            <CheckCircle2 className="h-6 w-6 text-emerald-600 flex-shrink-0" />
          ) : (
            <AlertTriangle className="h-6 w-6 text-amber-600 flex-shrink-0" />
          )}
          <div>
            <h4 className="font-semibold text-sm">
              Total Active Weight: <span className="font-bold text-base">{totalWeight}%</span>
            </h4>
            <p className="text-xs opacity-90 mt-0.5">
              {totalWeight === 100
                ? 'Evaluation weights are balanced to exactly 100%.'
                : `Weights should sum to 100% for balanced evaluations (Current: ${totalWeight}%).`}
            </p>
          </div>
        </div>
      </div>

      <Card bodyClassName="p-0">
        <Table columns={columns} data={criteria} loading={loading} />
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingCriteria ? `Edit Criteria: ${editingCriteria.name}` : 'Add Evaluation Criteria'}
        maxWidth="max-w-lg"
      >
        {formError && (
          <div className="mb-4 p-3 bg-rose-50 border border-rose-200 text-rose-700 text-sm rounded-lg">
            {formError}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="Criteria Name"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            placeholder="e.g. Delivery Performance"
            required
          />

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Criteria Code"
              value={formData.code}
              onChange={(e) => setFormData({ ...formData, code: e.target.value })}
              placeholder="e.g. CRIT-DELV"
            />

            <Input
              label="Display Order"
              type="number"
              value={formData.displayOrder}
              onChange={(e) => setFormData({ ...formData, displayOrder: parseInt(e.target.value) || 1 })}
              required
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Weight Percentage (%)"
              type="number"
              step="0.5"
              min="0"
              max="100"
              value={formData.weight}
              onChange={(e) => setFormData({ ...formData, weight: parseFloat(e.target.value) || 0 })}
              required
            />

            <Input
              label="Maximum Score (pts)"
              type="number"
              min="1"
              max="100"
              value={formData.maxScore}
              onChange={(e) => setFormData({ ...formData, maxScore: parseFloat(e.target.value) || 100 })}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Description / Guidelines</label>
            <textarea
              rows="3"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              placeholder="Specific guidelines for evaluators when scoring this criterion..."
              className="w-full px-3.5 py-2 text-sm bg-white border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            ></textarea>
          </div>

          <div className="flex items-center gap-2 pt-2">
            <input
              type="checkbox"
              id="criteriaActive"
              checked={formData.active}
              onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
              className="rounded text-blue-600 focus:ring-blue-500 h-4 w-4"
            />
            <label htmlFor="criteriaActive" className="text-sm font-medium text-slate-700">
              Active in Evaluation Form
            </label>
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <Button variant="secondary" onClick={() => setIsModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" variant="primary" loading={submitting}>
              {editingCriteria ? 'Update Criteria' : 'Create Criteria'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
