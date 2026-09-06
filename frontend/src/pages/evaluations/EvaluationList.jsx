import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { evaluationService } from '../../services/evaluation.service';
import { supplierService } from '../../services/supplier.service';
import { Card } from '../../components/common/Card';
import { Table } from '../../components/common/Table';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Toast } from '../../components/common/Toast';
import {
  PlusCircle,
  Eye,
  Trash2,
  Calendar,
  Filter,
  ChevronLeft,
  ChevronRight,
  FileText
} from 'lucide-react';

export const EvaluationList = () => {
  const { isAdmin } = useAuth();

  const [evaluations, setEvaluations] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState({ message: '', type: 'success' });

  // Filters & Sorting
  const [supplierId, setSupplierId] = useState('');
  const [ratingCategory, setRatingCategory] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [sortBy, setSortBy] = useState('evaluationDate');
  const [sortDirection, setSortDirection] = useState('desc');
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const fetchSuppliers = async () => {
    try {
      const res = await supplierService.getAllSuppliers();
      if (res.success) setSuppliers(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchEvaluations = useCallback(async () => {
    setLoading(true);
    try {
      const params = {
        supplierId: supplierId || undefined,
        ratingCategory: ratingCategory || undefined,
        startDate: startDate || undefined,
        endDate: endDate || undefined,
        sortBy,
        direction: sortDirection,
        page,
        size: pageSize,
      };

      const res = await evaluationService.getEvaluations(params);
      if (res.success && res.data) {
        setEvaluations(res.data.content);
        setTotalPages(res.data.totalPages);
        setTotalElements(res.data.totalElements);
      }
    } catch (err) {
      console.error(err);
      setToast({ message: 'Failed to load evaluations', type: 'error' });
    } finally {
      setLoading(false);
    }
  }, [supplierId, ratingCategory, startDate, endDate, sortBy, sortDirection, page, pageSize]);

  useEffect(() => {
    fetchSuppliers();
  }, []);

  useEffect(() => {
    fetchEvaluations();
  }, [fetchEvaluations]);

  const handleSort = (columnKey, direction) => {
    setSortBy(columnKey);
    setSortDirection(direction);
    setPage(0);
  };

  const handleDelete = async (id, code) => {
    if (!window.confirm(`Are you sure you want to delete evaluation record "${code}"?`)) return;

    try {
      await evaluationService.deleteEvaluation(id);
      setToast({ message: `Evaluation "${code}" deleted successfully`, type: 'success' });
      fetchEvaluations();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete evaluation';
      setToast({ message: msg, type: 'error' });
    }
  };

  const columns = [
    {
      header: 'Evaluation Code',
      accessor: 'evaluationCode',
      sortKey: 'evaluationCode',
      render: (row) => (
        <span className="font-mono text-xs font-semibold text-blue-600">
          <Link to={`/evaluations/${row.id}`} className="hover:underline">
            {row.evaluationCode}
          </Link>
        </span>
      ),
    },
    {
      header: 'Supplier',
      accessor: 'supplierName',
      sortKey: 'supplier.name',
      render: (row) => (
        <div>
          <Link to={`/suppliers/${row.supplierId}`} className="font-semibold text-slate-900 hover:text-blue-600 block">
            {row.supplierName}
          </Link>
          <span className="text-xs text-slate-500 font-mono">{row.supplierCode}</span>
        </div>
      ),
    },
    {
      header: 'Evaluator',
      accessor: 'evaluatorName',
      sortKey: 'evaluator.fullName',
      render: (row) => (
        <div className="text-xs">
          <p className="font-medium text-slate-800">{row.evaluatorName}</p>
          <p className="text-slate-500">@{row.evaluatorUsername}</p>
        </div>
      ),
    },
    {
      header: 'Date & Period',
      accessor: 'evaluationDate',
      sortKey: 'evaluationDate',
      render: (row) => (
        <div className="text-xs">
          <p className="font-medium text-slate-800">{row.evaluationDate}</p>
          <p className="text-slate-500">{row.evaluationPeriod || 'N/A'}</p>
        </div>
      ),
    },
    {
      header: 'Score',
      accessor: 'totalWeightedScore',
      sortKey: 'totalWeightedScore',
      render: (row) => <span className="font-bold text-slate-900 text-sm">{row.totalWeightedScore}%</span>,
    },
    {
      header: 'Rating Category',
      accessor: 'ratingCategory',
      sortKey: 'ratingCategory',
      render: (row) => <Badge variant={row.ratingCategory} size="sm" />,
    },
    {
      header: 'Actions',
      sortable: false,
      className: 'text-right',
      cellClassName: 'text-right',
      render: (row) => (
        <div className="flex items-center justify-end gap-2">
          <Link
            to={`/evaluations/${row.id}`}
            className="p-1.5 text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
            title="View Evaluation"
          >
            <Eye className="h-4 w-4" />
          </Link>
          {isAdmin() && (
            <button
              onClick={() => handleDelete(row.id, row.evaluationCode)}
              className="p-1.5 text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
              title="Delete Record"
            >
              <Trash2 className="h-4 w-4" />
            </button>
          )}
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
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Supplier Evaluations</h1>
          <p className="text-sm text-slate-500 mt-1">
            Historical log of all performance audits, scores, and tier determinations.
          </p>
        </div>
        <Link to="/evaluations/new">
          <Button variant="primary" icon={PlusCircle}>
            New Evaluation
          </Button>
        </Link>
      </div>

      {/* Filter Bar */}
      <Card bodyClassName="p-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          <div>
            <select
              value={supplierId}
              onChange={(e) => {
                setSupplierId(e.target.value);
                setPage(0);
              }}
              className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white"
            >
              <option value="">All Suppliers</option>
              {suppliers.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.name} ({s.supplierCode})
                </option>
              ))}
            </select>
          </div>

          <div>
            <select
              value={ratingCategory}
              onChange={(e) => {
                setRatingCategory(e.target.value);
                setPage(0);
              }}
              className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white"
            >
              <option value="">All Rating Tiers</option>
              <option value="EXCELLENT">EXCELLENT (≥85%)</option>
              <option value="GOOD">GOOD (70-84%)</option>
              <option value="AVERAGE">AVERAGE (50-69%)</option>
              <option value="POOR">POOR (&lt;50%)</option>
            </select>
          </div>

          <div>
            <input
              type="date"
              value={startDate}
              onChange={(e) => {
                setStartDate(e.target.value);
                setPage(0);
              }}
              className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white"
              title="Filter Start Date"
            />
          </div>

          <div>
            <input
              type="date"
              value={endDate}
              onChange={(e) => {
                setEndDate(e.target.value);
                setPage(0);
              }}
              className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white"
              title="Filter End Date"
            />
          </div>
        </div>
      </Card>

      <Card bodyClassName="p-0">
        <Table
          columns={columns}
          data={evaluations}
          loading={loading}
          sortBy={sortBy}
          sortDirection={sortDirection}
          onSort={handleSort}
          emptyMessage="No evaluations found matching the selected filters."
        />

        {totalPages > 1 && (
          <div className="px-6 py-4 border-t border-slate-100 flex items-center justify-between">
            <p className="text-xs text-slate-500">
              Showing <span className="font-semibold text-slate-800">{page * pageSize + 1}</span> to{' '}
              <span className="font-semibold text-slate-800">
                {Math.min((page + 1) * pageSize, totalElements)}
              </span>{' '}
              of <span className="font-semibold text-slate-800">{totalElements}</span> evaluations
            </p>
            <div className="flex items-center gap-2">
              <Button
                variant="secondary"
                size="sm"
                disabled={page === 0}
                onClick={() => setPage(page - 1)}
                icon={ChevronLeft}
              >
                Previous
              </Button>
              <span className="text-xs font-semibold px-2 text-slate-700">
                Page {page + 1} of {totalPages}
              </span>
              <Button
                variant="secondary"
                size="sm"
                disabled={page >= totalPages - 1}
                onClick={() => setPage(page + 1)}
              >
                Next <ChevronRight className="h-4 w-4 ml-1 inline" />
              </Button>
            </div>
          </div>
        )}
      </Card>
    </div>
  );
};
