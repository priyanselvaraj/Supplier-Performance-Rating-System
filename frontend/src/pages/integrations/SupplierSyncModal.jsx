import React, { useState } from 'react';
import Modal from '../../components/common/Modal';
import { Button } from '../../components/common/Button';
import { RefreshCw, CheckCircle, AlertTriangle, FileCode, Check, ArrowRight } from 'lucide-react';
import integrationService from '../../services/integration.service';

const SAMPLE_PAYLOAD = [
  {
    "supplierCode": "ERP-SUP-101",
    "name": "Apex Microelectronics Corp",
    "contactPerson": "Sarah Chen",
    "email": "sarah.chen@apexmicro.com",
    "phone": "+1-408-555-0199",
    "address": "2500 Silicon Valley Way, San Jose, CA",
    "website": "https://apexmicro.com",
    "categoryName": "Electronics & Hardware",
    "status": "ACTIVE"
  },
  {
    "supplierCode": "ERP-SUP-102",
    "name": "Global Cargo & Logistics Ltd",
    "contactPerson": "David Miller",
    "email": "david.m@globalcargo.com",
    "phone": "+1-312-555-0844",
    "address": "800 Freight Terminal Rd, Chicago, IL",
    "website": "https://globalcargo.com",
    "categoryName": "Logistics & Shipping",
    "status": "ACTIVE"
  }
];

export const SupplierSyncModal = ({ isOpen, onClose, onSyncFinished }) => {
  const [sourceSystem, setSourceSystem] = useState('SAP ERP Procurement');
  const [syncMode, setSyncMode] = useState('UPSERT');
  const [integrationType, setIntegrationType] = useState('SUPPLIER_CATALOG');
  const [jsonData, setJsonData] = useState(JSON.stringify(SAMPLE_PAYLOAD, null, 2));
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [syncResult, setSyncResult] = useState(null);

  const handleLoadSample = () => {
    setJsonData(JSON.stringify(SAMPLE_PAYLOAD, null, 2));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    let parsedSuppliers = [];
    try {
      parsedSuppliers = JSON.parse(jsonData);
      if (!Array.isArray(parsedSuppliers)) {
        throw new Error('JSON data must be an array of supplier objects');
      }
    } catch (parseErr) {
      setError('Invalid JSON format: ' + parseErr.message);
      return;
    }

    try {
      setLoading(true);
      const payload = {
        sourceSystem: sourceSystem.trim() || 'External System',
        integrationType: integrationType,
        syncMode: syncMode,
        suppliers: parsedSuppliers
      };

      const result = await integrationService.syncSuppliers(payload);
      setSyncResult(result);
      if (onSyncFinished) onSyncFinished(result);
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to execute supplier sync');
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setSyncResult(null);
    setError('');
    onClose();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title={syncResult ? 'Synchronization Complete' : 'Execute Supplier Synchronization'}
      maxWidth="max-w-2xl"
    >
      {syncResult ? (
        <div className="space-y-4">
          <div className={`p-4 rounded-xl border flex items-center gap-3 ${
            syncResult.status === 'SUCCESS'
              ? 'bg-emerald-50 border-emerald-200 text-emerald-900'
              : syncResult.status === 'PARTIAL_SUCCESS'
              ? 'bg-amber-50 border-amber-200 text-amber-900'
              : 'bg-rose-50 border-rose-200 text-rose-900'
          }`}>
            {syncResult.status === 'SUCCESS' ? (
              <CheckCircle className="w-6 h-6 text-emerald-600 shrink-0" />
            ) : (
              <AlertTriangle className="w-6 h-6 text-amber-600 shrink-0" />
            )}
            <div>
              <h4 className="font-semibold text-sm">
                Status: {syncResult.status} ({syncResult.message})
              </h4>
              <p className="text-xs opacity-80 mt-0.5">Execution Duration: {syncResult.durationMs} ms</p>
            </div>
          </div>

          <div className="grid grid-cols-4 gap-2.5 text-center">
            <div className="p-3 bg-emerald-50/70 border border-emerald-200 rounded-xl">
              <span className="text-xs text-emerald-700 font-medium">Created</span>
              <p className="text-xl font-bold text-emerald-800 mt-0.5">{syncResult.createdCount}</p>
            </div>
            <div className="p-3 bg-blue-50/70 border border-blue-200 rounded-xl">
              <span className="text-xs text-blue-700 font-medium">Updated</span>
              <p className="text-xl font-bold text-blue-800 mt-0.5">{syncResult.updatedCount}</p>
            </div>
            <div className="p-3 bg-slate-100 border border-slate-200 rounded-xl">
              <span className="text-xs text-slate-600 font-medium">Skipped</span>
              <p className="text-xl font-bold text-slate-800 mt-0.5">{syncResult.skippedCount}</p>
            </div>
            <div className="p-3 bg-rose-50/70 border border-rose-200 rounded-xl">
              <span className="text-xs text-rose-700 font-medium">Failed</span>
              <p className="text-xl font-bold text-rose-800 mt-0.5">{syncResult.failedCount}</p>
            </div>
          </div>

          {syncResult.errors && syncResult.errors.length > 0 && (
            <div className="p-3 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-800 max-h-36 overflow-y-auto">
              <p className="font-semibold mb-1">Errors Encountered ({syncResult.errors.length}):</p>
              <ul className="list-disc pl-4 space-y-0.5 font-mono text-[11px]">
                {syncResult.errors.map((err, idx) => (
                  <li key={idx}>{err}</li>
                ))}
              </ul>
            </div>
          )}

          <div className="flex justify-end pt-2">
            <Button onClick={handleClose} variant="primary">
              Done
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

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Source System Name
              </label>
              <input
                type="text"
                required
                placeholder="e.g. SAP ERP, Oracle NetSuite"
                value={sourceSystem}
                onChange={(e) => setSourceSystem(e.target.value)}
                className="w-full px-3.5 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Integration Type
              </label>
              <select
                value={integrationType}
                onChange={(e) => setIntegrationType(e.target.value)}
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none bg-white"
              >
                <option value="SUPPLIER_CATALOG">Supplier Catalog Feed</option>
                <option value="ERP_PURCHASING">ERP Purchasing Sync</option>
                <option value="CUSTOM_API">Custom API Integration</option>
                <option value="CSV_SYNC">CSV Batch Import</option>
              </select>
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
              Synchronization Strategy (Sync Mode)
            </label>
            <div className="grid grid-cols-3 gap-2">
              {[
                { id: 'UPSERT', label: 'UPSERT (Recommended)', desc: 'Insert new and update existing records' },
                { id: 'CREATE_ONLY', label: 'CREATE ONLY', desc: 'Add new records only; skip existing' },
                { id: 'UPDATE_EXISTING', label: 'UPDATE ONLY', desc: 'Update existing records; skip new' }
              ].map((m) => (
                <div
                  key={m.id}
                  onClick={() => setSyncMode(m.id)}
                  className={`p-2.5 rounded-xl border cursor-pointer transition-all ${
                    syncMode === m.id
                      ? 'bg-blue-50/70 border-blue-400 text-blue-900 shadow-sm'
                      : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100/70'
                  }`}
                >
                  <div className="flex items-center gap-1.5">
                    <input
                      type="radio"
                      name="syncMode"
                      checked={syncMode === m.id}
                      onChange={() => {}}
                      className="text-blue-600 pointer-events-none"
                    />
                    <span className="text-xs font-semibold">{m.label}</span>
                  </div>
                  <p className="text-[11px] text-slate-500 mt-1 leading-tight">{m.desc}</p>
                </div>
              ))}
            </div>
          </div>

          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="text-xs font-semibold text-slate-700 uppercase tracking-wider">
                Supplier Data Batch (JSON Array)
              </label>
              <button
                type="button"
                onClick={handleLoadSample}
                className="text-xs text-blue-600 hover:text-blue-700 font-medium flex items-center gap-1"
              >
                <FileCode className="w-3.5 h-3.5" /> Reset Template
              </button>
            </div>
            <textarea
              rows={8}
              value={jsonData}
              onChange={(e) => setJsonData(e.target.value)}
              className="w-full p-3 font-mono text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none bg-slate-900 text-slate-100"
              placeholder="[ { 'supplierCode': '...', 'name': '...', 'email': '...' } ]"
            />
          </div>

          <div className="flex justify-end gap-2.5 pt-2 border-t border-slate-100">
            <Button type="button" variant="secondary" onClick={onClose}>
              Cancel
            </Button>
            <Button type="submit" variant="primary" disabled={loading}>
              {loading ? (
                <>
                  <RefreshCw className="w-4 h-4 animate-spin mr-1.5" /> Synchronizing...
                </>
              ) : (
                'Run Synchronization'
              )}
            </Button>
          </div>
        </form>
      )}
    </Modal>
  );
};
export default SupplierSyncModal;
