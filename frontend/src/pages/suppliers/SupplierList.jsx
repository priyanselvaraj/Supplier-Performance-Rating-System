import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { supplierService } from '../../services/supplier.service';
import { categoryService } from '../../services/category.service';
import { useAuth } from '../../context/AuthContext';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { Table } from '../../components/common/Table';
import { Toast } from '../../components/common/Toast';
import { SupplierFormModal } from './SupplierFormModal';
import {
  Search,
  PlusCircle,
  Eye,
  Edit2,
  Trash2,
  CheckSquare,
  ChevronLeft,
  ChevronRight,
  ArrowUpDown
} from 'lucide-react';

export const SupplierList = () => {
  const { isAdmin } = useAuth();

  const [suppliers, setSuppliers] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState({ message: '', type: 'success' });

  // Filters and Pagination
  const [keyword, setKeyword] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [status, setStatus] = useState('');
  const [ratingCategory, setRatingCategory] = useState('');
  const [sortBy, setSortBy] = useState('name');
  const [sortDirection, setSortDirection] = useState('asc');
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingSupplier, setEditingSupplier] = useState(null);

  const fetchCategories = async () => {
    try {
      const res = await categoryService.getAllCategories();
      if (res.success) setCategories(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchSuppliers = useCallback(async () => {
    setLoading(true);
    try {
      const params = {
        keyword: keyword || undefined,
        categoryId: categoryId || undefined,
        status: status || undefined,
        ratingCategory: ratingCategory || undefined,
        sortBy,
        direction: sortDirection,
        page,
        size: pageSize,
      };

      const res = await supplierService.getSuppliers(params);
      if (res.success && res.data) {
        setSuppliers(res.data.content);
        setTotalPages(res.data.totalPages);
        setTotalElements(res.data.totalElements);
      }
    } catch (err) {
      console.error(err);
      setToast({ message: 'Failed to load suppliers', type: 'error' });
    } finally {
      setLoading(false);
    }
  }, [keyword, categoryId, status, ratingCategory, sortBy, sortDirection, page, pageSize]);

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    const debounceTimer = setTimeout(() => {
      fetchSuppliers();
    }, 300);
    return () => clearTimeout(debounceTimer);
  }, [fetchSuppliers]);

  const handleSort = (columnKey, direction) => {
    setSortBy(columnKey);
    setSortDirection(direction);
    setPage(0);
  };

  const handleQuickSortChange = (e) => {
    const val = e.target.value;
    if (!val) return;
    const [prop, dir] = val.split(':');
    setSortBy(prop);
    setSortDirection(dir);
    setPage(0);
  };

  const handleOpenAddModal = () => {
    setEditingSupplier(null);
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (sup) => {
    setEditingSupplier(sup);
    setIsModalOpen(true);
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Are you sure you want to delete supplier "${name}"? This action cannot be undone.`)) {
      return;
    }

    try {
      await supplierService.deleteSupplier(id);
      setToast({ message: `Supplier "${name}" deleted successfully`, type: 'success' });
      fetchSuppliers();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete supplier';
      setToast({ message: msg, type: 'error' });
    }
  };

  const columns = [
    {
      header: 'Code',
      accessor: 'supplierCode',
      sortKey: 'supplierCode',
      render: (row) => (
        <span className="font-mono text-xs font-semibold text-blue-600">
          <Link to={`/suppliers/${row.id}`} className="hover:underline">
            {row.supplierCode}
          </Link>
        </span>
      ),
    },
    {
      header: 'Supplier Name',
      accessor: 'name',
      sortKey: 'name',
      render: (row) => (
        <div>
          <Link to={`/suppliers/${row.id}`} className="font-semibold text-slate-900 hover:text-blue-600 block">
            {row.name}
          </Link>
          <span className="text-xs text-slate-500">{row.email}</span>
        </div>
      ),
    },
    {
      header: 'Category',
      accessor: 'category',
      sortKey: 'category',
      render: (row) => (
        <span className="text-xs font-medium text-slate-700 bg-slate-100 px-2.5 py-1 rounded-md">
          {row.category?.name || 'N/A'}
        </span>
      ),
    },
    {
      header: 'Contact Person',
      accessor: 'contactPerson',
      sortKey: 'contactPerson',
      render: (row) => (
        <div className="text-xs text-slate-600">
          <p className="font-medium text-slate-700">{row.contactPerson || '-'}</p>
          <p>{row.phone || '-'}</p>
        </div>
      ),
    },
    {
      header: 'Status',
      accessor: 'status',
      sortKey: 'status',
      render: (row) => <Badge variant={row.status} size="sm" />,
    },
    {
      header: 'Rating Score',
      accessor: 'overallRating',
      sortKey: 'overallRating',
      render: (row) => (
        <div className="flex items-center gap-2">
          <span className="font-bold text-slate-900">
            {row.totalEvaluations > 0 ? `${row.overallRating}%` : 'Unrated'}
          </span>
          <Badge variant={row.ratingCategory} size="xs" />
        </div>
      ),
    },
    {
      header: 'Evaluations',
      accessor: 'totalEvaluations',
      sortKey: 'totalEvaluations',
      render: (row) => (
        <span className="text-xs text-slate-600 font-medium">
          {row.totalEvaluations} records
        </span>
      ),
    },
    {
      header: 'Actions',
      sortable: false,
      className: 'text-right',
      cellClassName: 'text-right',
      render: (row) => (
        <div className="flex items-center justify-end gap-1.5">
          <Link
            to={`/evaluations/new?supplierId=${row.id}`}
            title="Evaluate Supplier"
            className="p-1.5 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
          >
            <CheckSquare className="h-4 w-4" />
          </Link>
          <Link
            to={`/suppliers/${row.id}`}
            title="View Details"
            className="p-1.5 text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
          >
            <Eye className="h-4 w-4" />
          </Link>
          <button
            onClick={() => handleOpenEditModal(row)}
            title="Edit Supplier"
            className="p-1.5 text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
          >
            <Edit2 className="h-4 w-4" />
          </button>
          {isAdmin() && (
            <button
              onClick={() => handleDelete(row.id, row.name)}
              title="Delete Supplier"
              className="p-1.5 text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
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

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Supplier Directory</h1>
          <p className="text-sm text-slate-500 mt-1">
            Manage organization vendor profiles, track evaluations, and monitor performance tiers. Click table column headers to sort.
          </p>
        </div>
        <Button variant="primary" icon={PlusCircle} onClick={handleOpenAddModal}>
          Add Supplier
        </Button>
      </div>

      {/* Filter & Sort Bar */}
      <Card bodyClassName="p-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
          <div className="relative">
            <Search className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
            <input
              type="text"
              placeholder="Search by name, code, contact..."
              value={keyword}
              onChange={(e) => {
                setKeyword(e.target.value);
                setPage(0);
              }}
              className="w-full pl-9 pr-3.5 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-colors"
            />
          </div>

          <div>
            <select
              value={categoryId}
              onChange={(e) => {
                setCategoryId(e.target.value);
                setPage(0);
              }}
              className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white"
            >
              <option value="">All Categories</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <select
              value={status}
              onChange={(e) => {
                setStatus(e.target.value);
                setPage(0);
              }}
              className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white"
            >
              <option value="">All Statuses</option>
              <option value="ACTIVE">ACTIVE</option>
              <option value="INACTIVE">INACTIVE</option>
              <option value="PENDING_REVIEW">PENDING_REVIEW</option>
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
              <option value="UNRATED">UNRATED</option>
            </select>
          </div>

          <div>
            <select
              value={`${sortBy}:${sortDirection}`}
              onChange={handleQuickSortChange}
              className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white font-medium text-slate-700"
            >
              <option value="name:asc">Sort: Name (A → Z)</option>
              <option value="name:desc">Sort: Name (Z → A)</option>
              <option value="overallRating:desc">Sort: Rating (Highest First)</option>
              <option value="overallRating:asc">Sort: Rating (Lowest First)</option>
              <option value="supplierCode:asc">Sort: Code (A → Z)</option>
              <option value="totalEvaluations:desc">Sort: Most Evaluations</option>
              <option value="createdAt:desc">Sort: Newest Added</option>
            </select>
          </div>
        </div>
      </Card>

      {/* Supplier Data Table */}
      <Card bodyClassName="p-0">
        <Table
          columns={columns}
          data={suppliers}
          loading={loading}
          sortBy={sortBy}
          sortDirection={sortDirection}
          onSort={handleSort}
          emptyMessage="No suppliers matched your search and filter criteria."
        />

        {/* Pagination Footer */}
        {totalPages > 1 && (
          <div className="px-6 py-4 border-t border-slate-100 flex items-center justify-between">
            <p className="text-xs text-slate-500">
              Showing <span className="font-semibold text-slate-800">{page * pageSize + 1}</span> to{' '}
              <span className="font-semibold text-slate-800">
                {Math.min((page + 1) * pageSize, totalElements)}
              </span>{' '}
              of <span className="font-semibold text-slate-800">{totalElements}</span> suppliers
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

      {/* Add / Edit Modal */}
      <SupplierFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSaved={() => {
          setIsModalOpen(false);
          fetchSuppliers();
        }}
        supplier={editingSupplier}
      />
    </div>
  );
};

export default SupplierList;
