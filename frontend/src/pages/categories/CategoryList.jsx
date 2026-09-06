import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { categoryService } from '../../services/category.service';
import { Card } from '../../components/common/Card';
import { Table } from '../../components/common/Table';
import { Button } from '../../components/common/Button';
import { Input } from '../../components/common/Input';
import { Modal } from '../../components/common/Modal';
import { Toast } from '../../components/common/Toast';
import { PlusCircle, Edit2, Trash2, Tags } from 'lucide-react';

export const CategoryList = () => {
  const { isAdmin } = useAuth();

  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState({ message: '', type: 'success' });

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [formData, setFormData] = useState({ name: '', code: '', description: '' });
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState('');

  const fetchCategories = async () => {
    setLoading(true);
    try {
      const res = await categoryService.getAllCategories();
      if (res.success) setCategories(res.data);
    } catch (err) {
      console.error(err);
      setToast({ message: 'Failed to load categories', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleOpenAdd = () => {
    setEditingCategory(null);
    setFormData({ name: '', code: '', description: '' });
    setFormError('');
    setIsModalOpen(true);
  };

  const handleOpenEdit = (cat) => {
    setEditingCategory(cat);
    setFormData({
      name: cat.name || '',
      code: cat.code || '',
      description: cat.description || '',
    });
    setFormError('');
    setIsModalOpen(true);
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Are you sure you want to delete category "${name}"?`)) return;

    try {
      await categoryService.deleteCategory(id);
      setToast({ message: `Category "${name}" deleted`, type: 'success' });
      fetchCategories();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete category';
      setToast({ message: msg, type: 'error' });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.name.trim()) {
      setFormError('Category name is required');
      return;
    }

    setSubmitting(true);
    setFormError('');

    try {
      if (editingCategory) {
        await categoryService.updateCategory(editingCategory.id, formData);
        setToast({ message: 'Category updated successfully', type: 'success' });
      } else {
        await categoryService.createCategory(formData);
        setToast({ message: 'Category created successfully', type: 'success' });
      }
      setIsModalOpen(false);
      fetchCategories();
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Operation failed';
      setFormError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  const columns = [
    {
      header: 'Category Code',
      accessor: 'code',
      render: (row) => (
        <span className="font-mono text-xs font-semibold text-slate-700 bg-slate-100 px-2 py-1 rounded">
          {row.code || 'N/A'}
        </span>
      ),
    },
    {
      header: 'Category Name',
      accessor: 'name',
      render: (row) => <span className="font-semibold text-slate-900">{row.name}</span>,
    },
    {
      header: 'Description',
      accessor: 'description',
      render: (row) => <span className="text-xs text-slate-600">{row.description || '-'}</span>,
    },
    ...(isAdmin()
      ? [
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
        ]
      : []),
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
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Supplier Categories</h1>
          <p className="text-sm text-slate-500 mt-1">
            Organize suppliers into functional procurement and supply chain categories.
          </p>
        </div>
        {isAdmin() && (
          <Button variant="primary" icon={PlusCircle} onClick={handleOpenAdd}>
            Add Category
          </Button>
        )}
      </div>

      <Card bodyClassName="p-0">
        <Table columns={columns} data={categories} loading={loading} />
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingCategory ? `Edit Category: ${editingCategory.name}` : 'Add Supplier Category'}
        maxWidth="max-w-lg"
      >
        {formError && (
          <div className="mb-4 p-3 bg-rose-50 border border-rose-200 text-rose-700 text-sm rounded-lg">
            {formError}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="Category Name"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            placeholder="e.g. Raw Materials"
            required
          />

          <Input
            label="Category Code"
            value={formData.code}
            onChange={(e) => setFormData({ ...formData, code: e.target.value })}
            placeholder="e.g. CAT-RAW"
            helperText="Short identifying code (optional)"
          />

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Description</label>
            <textarea
              rows="3"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              placeholder="Suppliers providing physical commodities..."
              className="w-full px-3.5 py-2 text-sm bg-white border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            ></textarea>
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <Button variant="secondary" onClick={() => setIsModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" variant="primary" loading={submitting}>
              {editingCategory ? 'Update' : 'Create'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
