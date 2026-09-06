import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { ratingService } from '../../services/rating.service';
import { Card } from '../../components/common/Card';
import { Table } from '../../components/common/Table';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { Award, Filter, ArrowUpRight, TrendingUp, TrendingDown, Minus, RefreshCw } from 'lucide-react';

export const RatingsPage = () => {
  const [ratings, setRatings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pagination, setPagination] = useState({ page: 0, size: 10, totalElements: 0, totalPages: 0 });

  // Filters & Sorting
  const [ratingFilter, setRatingFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [sortBy, setSortBy] = useState('ratingDate');
  const [sortDirection, setSortDirection] = useState('desc');

  const loadRatings = useCallback(async (page = 0, currentSortBy = sortBy, currentSortDir = sortDirection) => {
    setLoading(true);
    try {
      const params = {
        page,
        size: pagination.size,
        sortBy: currentSortBy,
        direction: currentSortDir,
        rating: ratingFilter || undefined,
        status: statusFilter || undefined,
        startDate: startDate || undefined,
        endDate: endDate || undefined,
      };

      const res = await ratingService.getAllRatings(params);
      if (res.success && res.data) {
        setRatings(res.data.content || []);
        setPagination({
          page: res.data.pageNumber || 0,
          size: res.data.pageSize || 10,
          totalElements: res.data.totalElements || 0,
          totalPages: res.data.totalPages || 0,
        });
      }
    } catch (err) {
      console.error('Failed to load ratings:', err);
    } finally {
      setLoading(false);
    }
  }, [pagination.size, ratingFilter, statusFilter, startDate, endDate, sortBy, sortDirection]);

  useEffect(() => {
    loadRatings(0);
  }, [loadRatings]);

  const getRatingBadgeVariant = (rating) => {
    switch (rating) {
      case 'EXCELLENT': return 'success';
      case 'VERY_GOOD': return 'info';
      case 'GOOD': return 'primary';
      case 'AVERAGE': return 'warning';
      case 'POOR': return 'danger';
      default: return 'default';
    }
  };

  const getStatusBadgeVariant = (status) => {
    switch (status) {
      case 'HIGH_PERFORMING': return 'success';
      case 'SATISFACTORY': return 'info';
      case 'NEEDS_IMPROVEMENT': return 'warning';
      case 'LOW_PERFORMING': return 'danger';
      default: return 'default';
    }
  };

  const handleSort = (columnKey, direction) => {
    setSortBy(columnKey);
    setSortDirection(direction);
  };

  const columns = [
    {
      header: 'Supplier',
      accessor: 'supplierName',
      sortKey: 'supplier.name',
      render: (row) => (
        <div>
          <Link
            to={`/ratings/supplier/${row.supplierId}`}
            className="font-semibold text-slate-900 hover:text-blue-600 flex items-center gap-1.5"
          >
            {row.supplierName}
            <ArrowUpRight className="h-3.5 w-3.5 text-slate-400" />
          </Link>
          <span className="text-xs text-slate-500 font-mono">{row.supplierCode}</span>
        </div>
      ),
    },
    {
      header: 'Score',
      accessor: 'score',
      sortKey: 'score',
      render: (row) => (
        <div className="flex items-center gap-2">
          <span className="text-base font-bold text-slate-900">{row.score?.toFixed(1)}</span>
          <span className="text-xs text-slate-400">/ 100</span>
        </div>
      ),
    },
    {
      header: 'Rating Classification',
      accessor: 'rating',
      sortKey: 'rating',
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
      sortKey: 'performanceStatus',
      render: (row) => (
        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
          row.performanceStatus === 'HIGH_PERFORMING' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' :
          row.performanceStatus === 'SATISFACTORY' ? 'bg-blue-50 text-blue-700 border border-blue-200' :
          row.performanceStatus === 'NEEDS_IMPROVEMENT' ? 'bg-amber-50 text-amber-700 border border-amber-200' :
          'bg-rose-50 text-rose-700 border border-rose-200'
        }`}>
          {row.performanceStatusDisplayName || row.performanceStatus}
        </span>
      ),
    },
    {
      header: 'Rating Date',
      accessor: 'ratingDate',
      sortKey: 'ratingDate',
      render: (row) => (
        <span className="text-xs text-slate-600 font-medium">{row.ratingDate}</span>
      ),
    },
    {
      header: 'Evaluation Record',
      accessor: 'evaluationCode',
      sortKey: 'evaluation.evaluationCode',
      render: (row) => row.evaluationId ? (
        <Link
          to={`/evaluations/${row.evaluationId}`}
          className="text-xs font-mono text-blue-600 hover:underline"
        >
          {row.evaluationCode || `EV-${row.evaluationId}`}
        </Link>
      ) : (
        <span className="text-xs text-slate-400">Direct Entry</span>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Performance Ratings Ledger</h1>
          <p className="text-sm text-slate-500 mt-1">
            Audit-grade performance scoring registry, multi-criteria tier calculation, and historical tracking.
          </p>
        </div>
        <Button variant="secondary" icon={RefreshCw} onClick={() => loadRatings(pagination.page)}>
          Refresh
        </Button>
      </div>

      {/* Filter Bar */}
      <Card bodyClassName="p-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div>
            <select
              value={ratingFilter}
              onChange={(e) => setRatingFilter(e.target.value)}
              className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white font-medium"
            >
              <option value="">All Rating Categories</option>
              <option value="EXCELLENT">EXCELLENT (≥ 85%)</option>
              <option value="GOOD">GOOD (70% - 84%)</option>
              <option value="AVERAGE">AVERAGE (50% - 69%)</option>
              <option value="POOR">POOR (&lt; 50%)</option>
            </select>
          </div>

          <div>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white font-medium"
            >
              <option value="">All Performance Statuses</option>
              <option value="HIGH_PERFORMING">High Performing</option>
              <option value="SATISFACTORY">Satisfactory</option>
              <option value="NEEDS_IMPROVEMENT">Needs Improvement</option>
              <option value="LOW_PERFORMING">Low Performing</option>
            </select>
          </div>

          <div>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white"
            />
          </div>

          <div>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white"
            />
          </div>
        </div>
      </Card>

      {/* Ratings Ledger Table */}
      <Card title="Performance Ratings Ledger" bodyClassName="p-0">
        <Table
          columns={columns}
          data={ratings}
          loading={loading}
          sortBy={sortBy}
          sortDirection={sortDirection}
          onSort={handleSort}
          emptyMessage="No rating records found matching current filters."
        />

        {/* Pagination Footer */}
        {pagination.totalPages > 1 && (
          <div className="flex items-center justify-between px-6 py-4 border-t border-slate-100">
            <span className="text-xs text-slate-500">
              Showing page <span className="font-semibold text-slate-800">{pagination.page + 1}</span> of{' '}
              <span className="font-semibold text-slate-800">{pagination.totalPages}</span> ({pagination.totalElements} total entries)
            </span>
            <div className="flex gap-2">
              <Button
                variant="secondary"
                size="sm"
                disabled={pagination.page === 0}
                onClick={() => loadRatings(pagination.page - 1)}
              >
                Previous
              </Button>
              <Button
                variant="secondary"
                size="sm"
                disabled={pagination.page >= pagination.totalPages - 1}
                onClick={() => loadRatings(pagination.page + 1)}
              >
                Next
              </Button>
            </div>
          </div>
        )}
      </Card>
    </div>
  );
};
