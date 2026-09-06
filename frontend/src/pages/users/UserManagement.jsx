import React, { useState, useEffect } from 'react';
import { userService } from '../../services/user.service';
import { Card } from '../../components/common/Card';
import { Table } from '../../components/common/Table';
import { Button } from '../../components/common/Button';
import { Input } from '../../components/common/Input';
import { Badge } from '../../components/common/Badge';
import { Modal } from '../../components/common/Modal';
import { Toast } from '../../components/common/Toast';
import { Search, Shield, UserCheck, Trash2, Edit2, UserX, Phone, Mail } from 'lucide-react';

export const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState({ message: '', type: 'success' });

  // Full Edit Modal State
  const [editingUser, setEditingUser] = useState(null);
  const [editFormData, setEditFormData] = useState({
    fullName: '',
    email: '',
    phone: '',
    department: '',
    active: true,
    roles: [],
  });
  const [savingUser, setSavingUser] = useState(false);
  const [editError, setEditError] = useState('');

  const fetchUsers = async (searchKw = keyword) => {
    setLoading(true);
    try {
      const res = await userService.searchUsers(searchKw);
      if (res.success) setUsers(res.data);
    } catch (err) {
      console.error(err);
      setToast({ message: 'Failed to load user accounts', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    fetchUsers(keyword);
  };

  const handleToggleStatus = async (id, username) => {
    try {
      await userService.toggleUserStatus(id);
      setToast({ message: `Status updated for user "${username}"`, type: 'success' });
      fetchUsers();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to toggle user status';
      setToast({ message: msg, type: 'error' });
    }
  };

  const handleDeleteUser = async (id, username) => {
    if (!window.confirm(`Are you sure you want to delete user "${username}"?`)) return;

    try {
      await userService.deleteUser(id);
      setToast({ message: `User "${username}" deleted successfully`, type: 'success' });
      fetchUsers();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete user';
      setToast({ message: msg, type: 'error' });
    }
  };

  const handleOpenEditModal = (u) => {
    setEditingUser(u);
    setEditFormData({
      fullName: u.fullName || '',
      email: u.email || '',
      phone: u.phone || '',
      department: u.department || '',
      active: u.active,
      roles: u.roles ? [...u.roles] : ['ROLE_MANAGER'],
    });
    setEditError('');
  };

  const handleSaveUser = async (e) => {
    e.preventDefault();
    if (!editingUser) return;
    if (!editFormData.fullName || !editFormData.email) {
      setEditError('Full Name and Email Address are required');
      return;
    }

    setSavingUser(true);
    setEditError('');

    try {
      await userService.updateUser(editingUser.id, editFormData);
      setToast({ message: `User "${editingUser.username}" details updated successfully`, type: 'success' });
      setEditingUser(null);
      fetchUsers();
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Failed to update user';
      setEditError(msg);
    } finally {
      setSavingUser(false);
    }
  };

  const columns = [
    {
      header: 'Username',
      accessor: 'username',
      render: (row) => (
        <div>
          <span className="font-semibold text-slate-900 block">{row.username}</span>
          <span className="text-xs text-slate-500">{row.email}</span>
        </div>
      ),
    },
    {
      header: 'Full Name',
      accessor: 'fullName',
      render: (row) => <span className="text-slate-800 font-medium">{row.fullName}</span>,
    },
    {
      header: 'Contact Info',
      accessor: 'phone',
      render: (row) => (
        <div className="text-xs text-slate-600">
          <p className="font-medium text-slate-700">{row.phone || '-'}</p>
          <p className="text-slate-400">{row.department || '-'}</p>
        </div>
      ),
    },
    {
      header: 'Assigned Roles',
      accessor: 'roles',
      render: (row) => (
        <div className="flex flex-wrap gap-1">
          {row.roles?.map((r, i) => (
            <Badge key={i} variant={r} size="xs" />
          ))}
        </div>
      ),
    },
    {
      header: 'Status',
      accessor: 'active',
      render: (row) => (
        <button
          onClick={() => handleToggleStatus(row.id, row.username)}
          className={`px-2.5 py-1 text-xs font-semibold rounded-full border transition-colors ${
            row.active
              ? 'bg-emerald-50 text-emerald-700 border-emerald-200 hover:bg-emerald-100'
              : 'bg-rose-50 text-rose-700 border-rose-200 hover:bg-rose-100'
          }`}
        >
          {row.active ? 'Active' : 'Disabled'}
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
            onClick={() => handleOpenEditModal(row)}
            title="Edit User & Roles"
            className="p-1.5 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
          >
            <Edit2 className="h-4 w-4" />
          </button>
          {row.username !== 'admin' && (
            <button
              onClick={() => handleDeleteUser(row.id, row.username)}
              title="Delete User"
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

      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">User Administration</h1>
          <p className="text-sm text-slate-500 mt-1">
            Manage system access, verified email and mobile numbers, roles, and account statuses.
          </p>
        </div>
      </div>

      {/* Search Bar */}
      <Card bodyClassName="p-4">
        <form onSubmit={handleSearch} className="flex gap-3">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
            <input
              type="text"
              placeholder="Search by username, full name, mobile number, or email..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              className="w-full pl-9 pr-3.5 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white"
            />
          </div>
          <Button type="submit" variant="primary">
            Search
          </Button>
        </form>
      </Card>

      <Card bodyClassName="p-0">
        <Table columns={columns} data={users} loading={loading} />
      </Card>

      {/* Edit User & Roles Modal */}
      <Modal
        isOpen={!!editingUser}
        onClose={() => setEditingUser(null)}
        title={`Edit User: ${editingUser?.username}`}
        maxWidth="max-w-lg"
      >
        {editError && (
          <div className="mb-4 p-3 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-lg">
            {editError}
          </div>
        )}

        <form onSubmit={handleSaveUser} className="space-y-4">
          <Input
            label="Full Name"
            value={editFormData.fullName}
            onChange={(e) => setEditFormData({ ...editFormData, fullName: e.target.value })}
            placeholder="e.g. John Doe"
            required
          />

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Email Address"
              type="email"
              value={editFormData.email}
              onChange={(e) => setEditFormData({ ...editFormData, email: e.target.value })}
              placeholder="john@example.com"
              required
            />

            <Input
              label="Mobile / Phone Number"
              value={editFormData.phone}
              onChange={(e) => setEditFormData({ ...editFormData, phone: e.target.value })}
              placeholder="+1 555-0199 or +91 9876543210"
            />
          </div>

          <Input
            label="Department"
            value={editFormData.department}
            onChange={(e) => setEditFormData({ ...editFormData, department: e.target.value })}
            placeholder="Procurement / Logistics"
          />

          <div className="pt-2">
            <label className="block text-sm font-medium text-slate-700 mb-2">Role Permissions</label>
            <div className="space-y-2">
              <label className="flex items-center gap-3 p-3 bg-slate-50 rounded-xl border border-slate-200 cursor-pointer hover:bg-slate-100/80">
                <input
                  type="checkbox"
                  checked={editFormData.roles.some((r) => r.includes('MANAGER'))}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setEditFormData({ ...editFormData, roles: [...editFormData.roles, 'ROLE_MANAGER'] });
                    } else {
                      setEditFormData({ ...editFormData, roles: editFormData.roles.filter((r) => !r.includes('MANAGER')) });
                    }
                  }}
                  className="rounded text-blue-600 focus:ring-blue-500 h-4 w-4"
                />
                <div>
                  <span className="text-sm font-semibold text-slate-800 block">ROLE_MANAGER</span>
                  <span className="text-xs text-slate-500">Can view suppliers, evaluate, and view reports</span>
                </div>
              </label>

              <label className="flex items-center gap-3 p-3 bg-slate-50 rounded-xl border border-slate-200 cursor-pointer hover:bg-slate-100/80">
                <input
                  type="checkbox"
                  checked={editFormData.roles.some((r) => r.includes('ADMIN'))}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setEditFormData({ ...editFormData, roles: [...editFormData.roles, 'ROLE_ADMIN'] });
                    } else {
                      setEditFormData({ ...editFormData, roles: editFormData.roles.filter((r) => !r.includes('ADMIN')) });
                    }
                  }}
                  className="rounded text-blue-600 focus:ring-blue-500 h-4 w-4"
                />
                <div>
                  <span className="text-sm font-semibold text-purple-700 block">ROLE_ADMIN</span>
                  <span className="text-xs text-slate-500">Full administrative access, criteria, users, &amp; deletions</span>
                </div>
              </label>
            </div>
          </div>

          <div className="flex items-center gap-2 pt-2">
            <input
              type="checkbox"
              id="userActiveCheck"
              checked={editFormData.active}
              onChange={(e) => setEditFormData({ ...editFormData, active: e.target.checked })}
              className="rounded text-blue-600 focus:ring-blue-500 h-4 w-4"
            />
            <label htmlFor="userActiveCheck" className="text-sm font-medium text-slate-700">
              Account Active / Enabled
            </label>
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <Button variant="secondary" onClick={() => setEditingUser(null)}>
              Cancel
            </Button>
            <Button type="submit" variant="primary" loading={savingUser}>
              Save User Changes
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default UserManagement;
