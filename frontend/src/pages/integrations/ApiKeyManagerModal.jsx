import React, { useState } from 'react';
import Modal from '../../components/common/Modal';
import { Button } from '../../components/common/Button';
import { Key, Shield, Copy, Check, AlertTriangle, Clock } from 'lucide-react';
import integrationService from '../../services/integration.service';

const AVAILABLE_SCOPES = [
  { id: 'SUPPLIER_READ', label: 'Supplier Read', desc: 'Query and fetch supplier profile details' },
  { id: 'SUPPLIER_WRITE', label: 'Supplier Write', desc: 'Create and register new suppliers' },
  { id: 'PERFORMANCE_READ', label: 'Performance Read', desc: 'Access supplier ratings, scores & KPI metrics' },
  { id: 'EVALUATION_READ', label: 'Evaluation Read', desc: 'Read historical evaluation records and scorecards' },
  { id: 'REPORT_READ', label: 'Report Read', desc: 'Fetch aggregate enterprise summary reports' },
  { id: 'SYNC_MANAGE', label: 'Sync Manage', desc: 'Execute batch supplier synchronization' }
];

export const ApiKeyManagerModal = ({ isOpen, onClose, onKeyCreated }) => {
  const [name, setName] = useState('');
  const [selectedScopes, setSelectedScopes] = useState(['SUPPLIER_READ', 'PERFORMANCE_READ']);
  const [expiresInDays, setExpiresInDays] = useState('90');
  const [rateLimit, setRateLimit] = useState(60);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Post-creation one-time secret view
  const [createdKeyData, setCreatedKeyData] = useState(null);
  const [copied, setCopied] = useState(false);

  const handleToggleScope = (scopeId) => {
    if (selectedScopes.includes(scopeId)) {
      if (selectedScopes.length === 1) return; // Keep at least one
      setSelectedScopes(selectedScopes.filter((s) => s !== scopeId));
    } else {
      setSelectedScopes([...selectedScopes, scopeId]);
    }
  };

  const handleSelectAllScopes = () => {
    if (selectedScopes.length === AVAILABLE_SCOPES.length) {
      setSelectedScopes(['SUPPLIER_READ']);
    } else {
      setSelectedScopes(AVAILABLE_SCOPES.map((s) => s.id));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name.trim()) {
      setError('Please provide a descriptive key name.');
      return;
    }
    if (selectedScopes.length === 0) {
      setError('Please select at least one permission scope.');
      return;
    }

    try {
      setLoading(true);
      setError('');

      let expiresAt = null;
      if (expiresInDays !== 'never') {
        const days = parseInt(expiresInDays, 10);
        const expDate = new Date();
        expDate.setDate(expDate.getDate() + days);
        expiresAt = expDate.toISOString().slice(0, 19);
      }

      const payload = {
        name: name.trim(),
        scopes: selectedScopes,
        expiresAt: expiresAt,
        rateLimitPerMinute: parseInt(rateLimit, 10) || 60
      };

      const result = await integrationService.createApiKey(payload);
      setCreatedKeyData(result);
      if (onKeyCreated) onKeyCreated(result);
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to create API key');
    } finally {
      setLoading(false);
    }
  };

  const handleCopyKey = () => {
    if (createdKeyData?.rawApiKey) {
      navigator.clipboard.writeText(createdKeyData.rawApiKey);
      setCopied(true);
      setTimeout(() => setCopied(false), 2500);
    }
  };

  const handleClose = () => {
    setName('');
    setSelectedScopes(['SUPPLIER_READ', 'PERFORMANCE_READ']);
    setExpiresInDays('90');
    setRateLimit(60);
    setError('');
    setCreatedKeyData(null);
    setCopied(false);
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title={createdKeyData ? 'API Key Generated' : 'Create Enterprise API Key'} maxWidth="max-w-xl">
      {createdKeyData ? (
        <div className="space-y-5">
          <div className="p-4 bg-amber-50 border border-amber-200 rounded-xl flex items-start gap-3">
            <AlertTriangle className="w-5 h-5 text-amber-600 shrink-0 mt-0.5" />
            <div>
              <h4 className="text-sm font-semibold text-amber-900">Save Your API Key Now</h4>
              <p className="text-xs text-amber-700 mt-1">
                For security reasons, this secret key will <strong>NEVER</strong> be displayed again. If you lose it, you will need to generate a new key.
              </p>
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-1.5">
              API Key ({createdKeyData.name})
            </label>
            <div className="flex items-center gap-2 p-3 bg-slate-900 text-emerald-400 font-mono text-sm rounded-xl border border-slate-800 break-all select-all">
              <Key className="w-4 h-4 text-emerald-400 shrink-0" />
              <span className="flex-1 select-all">{createdKeyData.rawApiKey}</span>
              <button
                type="button"
                onClick={handleCopyKey}
                className="p-2 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-lg transition-colors shrink-0"
                title="Copy to Clipboard"
              >
                {copied ? <Check className="w-4 h-4 text-emerald-400" /> : <Copy className="w-4 h-4" />}
              </button>
            </div>
            {copied && <p className="text-xs text-emerald-600 font-medium mt-1.5 flex items-center gap-1"><Check className="w-3.5 h-3.5" /> Copied to clipboard!</p>}
          </div>

          <div className="bg-slate-50 p-3.5 rounded-xl border border-slate-200 text-xs text-slate-600 space-y-1">
            <p><strong>Prefix:</strong> {createdKeyData.keyPrefix}</p>
            <p><strong>Granted Scopes:</strong> {createdKeyData.scopes?.join(', ')}</p>
            <p><strong>Rate Limit:</strong> {createdKeyData.rateLimitPerMinute} requests / min</p>
          </div>

          <div className="flex justify-end pt-2">
            <Button onClick={handleClose} variant="primary">
              I Have Stored the Key Securely
            </Button>
          </div>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <div className="p-3 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-xl flex items-center gap-2">
              <AlertTriangle className="w-4 h-4 shrink-0 text-rose-500" />
              {error}
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
              Key Name / Client System <span className="text-rose-500">*</span>
            </label>
            <input
              type="text"
              required
              placeholder="e.g. SAP Production ERP, NetSuite Sync Connector"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full px-3.5 py-2 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            />
          </div>

          <div>
            <div className="flex items-center justify-between mb-1.5">
              <label className="text-xs font-semibold text-slate-700 uppercase tracking-wider">
                Permission Scopes <span className="text-rose-500">*</span>
              </label>
              <button
                type="button"
                onClick={handleSelectAllScopes}
                className="text-xs text-blue-600 hover:text-blue-700 font-medium"
              >
                {selectedScopes.length === AVAILABLE_SCOPES.length ? 'Deselect All' : 'Select All'}
              </button>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
              {AVAILABLE_SCOPES.map((scope) => {
                const isSelected = selectedScopes.includes(scope.id);
                return (
                  <div
                    key={scope.id}
                    onClick={() => handleToggleScope(scope.id)}
                    className={`p-2.5 rounded-xl border cursor-pointer transition-all ${
                      isSelected
                        ? 'bg-blue-50/70 border-blue-400 text-blue-900 shadow-sm'
                        : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100/70'
                    }`}
                  >
                    <div className="flex items-center gap-2">
                      <input
                        type="checkbox"
                        checked={isSelected}
                        onChange={() => {}}
                        className="rounded text-blue-600 focus:ring-blue-500 pointer-events-none"
                      />
                      <span className="text-xs font-semibold">{scope.label}</span>
                    </div>
                    <p className="text-[11px] text-slate-500 mt-1 ml-5 leading-tight">{scope.desc}</p>
                  </div>
                );
              })}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3 pt-1">
            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Expiration
              </label>
              <select
                value={expiresInDays}
                onChange={(e) => setExpiresInDays(e.target.value)}
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none bg-white"
              >
                <option value="30">30 Days</option>
                <option value="90">90 Days (Recommended)</option>
                <option value="180">180 Days</option>
                <option value="365">1 Year</option>
                <option value="never">Never Expires</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Rate Limit (req/min)
              </label>
              <input
                type="number"
                min="10"
                max="1000"
                value={rateLimit}
                onChange={(e) => setRateLimit(e.target.value)}
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>
          </div>

          <div className="flex justify-end gap-2.5 pt-3 border-t border-slate-100">
            <Button type="button" variant="secondary" onClick={handleClose}>
              Cancel
            </Button>
            <Button type="submit" variant="primary" disabled={loading}>
              {loading ? 'Generating...' : 'Generate API Key'}
            </Button>
          </div>
        </form>
      )}
    </Modal>
  );
};
export default ApiKeyManagerModal;
