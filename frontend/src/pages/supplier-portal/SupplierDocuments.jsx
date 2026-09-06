import React, { useState, useEffect } from 'react';
import supplierPortalService from '../../services/supplierPortal.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { Modal } from '../../components/common/Modal';
import { Input } from '../../components/common/Input';
import {
  FileText,
  UploadCloud,
  Download,
  Trash2,
  CheckCircle2,
  AlertCircle,
  FileCheck,
  Clock,
  ShieldCheck
} from 'lucide-react';

export const SupplierDocuments = () => {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [uploadModalOpen, setUploadModalOpen] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [successMsg, setSuccessMsg] = useState(null);

  const [selectedFile, setSelectedFile] = useState(null);
  const [documentType, setDocumentType] = useState('ISO Quality Certification');
  const [notes, setNotes] = useState('');

  const fetchDocuments = async () => {
    try {
      setLoading(true);
      const res = await supplierPortalService.getDocuments();
      if (res.success) {
        setDocuments(res.data || []);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to retrieve documents.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDocuments();
  }, []);

  const handleUploadSubmit = async (e) => {
    e.preventDefault();
    if (!selectedFile) {
      setError('Please select a file to upload.');
      return;
    }

    try {
      setUploading(true);
      setError(null);
      const formData = new FormData();
      formData.append('file', selectedFile);
      formData.append('documentType', documentType);
      if (notes.trim()) {
        formData.append('notes', notes.trim());
      }

      const res = await supplierPortalService.uploadDocument(formData);
      if (res.success) {
        setSuccessMsg(`Document "${res.data.documentName}" uploaded successfully.`);
        setUploadModalOpen(false);
        setSelectedFile(null);
        setNotes('');
        fetchDocuments();
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Document upload failed.');
    } finally {
      setUploading(false);
    }
  };

  const handleDownload = async (doc) => {
    try {
      const blob = await supplierPortalService.downloadDocument(doc.id);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', doc.documentName);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      setError('Failed to download document. Please try again.');
    }
  };

  const handleDelete = async (docId, docName) => {
    if (!window.confirm(`Are you sure you want to delete "${docName}"?`)) return;

    try {
      const res = await supplierPortalService.deleteDocument(docId);
      if (res.success) {
        setSuccessMsg('Document removed successfully.');
        fetchDocuments();
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete document.');
    }
  };

  const formatFileSize = (bytes) => {
    if (!bytes) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  };

  if (loading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-emerald-600 border-t-transparent"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Compliance & Quality Documents</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Upload and maintain verified ISO certificates, environmental compliance filings, and insurance policies.
          </p>
        </div>
        <Button
          onClick={() => setUploadModalOpen(true)}
          className="bg-emerald-600 hover:bg-emerald-700 text-white"
        >
          <UploadCloud className="h-4 w-4 mr-2" /> Upload Document
        </Button>
      </div>

      {/* Alerts */}
      {successMsg && (
        <div className="rounded-xl border border-emerald-200 bg-emerald-50 p-4 text-emerald-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="h-5 w-5 text-emerald-600" />
            <span className="text-sm font-medium">{successMsg}</span>
          </div>
          <button onClick={() => setSuccessMsg(null)} className="text-xs font-bold text-emerald-700 hover:text-emerald-900">
            Dismiss
          </button>
        </div>
      )}

      {error && (
        <div className="rounded-xl border border-rose-200 bg-rose-50 p-4 text-rose-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <AlertCircle className="h-5 w-5 text-rose-600" />
            <span className="text-sm font-medium">{error}</span>
          </div>
          <button onClick={() => setError(null)} className="text-xs font-bold text-rose-700 hover:text-rose-900">
            Dismiss
          </button>
        </div>
      )}

      {/* Documents Grid */}
      {documents.length === 0 ? (
        <Card className="p-12 text-center text-slate-500 border-slate-200/80">
          <FileCheck className="mx-auto h-12 w-12 text-slate-400 mb-3" />
          <h3 className="text-base font-bold text-slate-700">No Documents Uploaded</h3>
          <p className="text-xs text-slate-400 mt-1">
            Upload required quality management, RoHS, or liability insurance certificates for compliance validation.
          </p>
          <Button
            onClick={() => setUploadModalOpen(true)}
            variant="secondary"
            className="mt-4"
          >
            Upload First Document
          </Button>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {documents.map((doc) => (
            <Card key={doc.id} className="p-5 border-slate-200/80 shadow-xs hover:shadow-md transition-shadow flex flex-col justify-between">
              <div>
                <div className="flex items-start justify-between gap-2 mb-3">
                  <div className="h-10 w-10 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center flex-shrink-0">
                    <FileText className="h-5 w-5" />
                  </div>
                  <Badge variant={doc.status === 'ACTIVE' ? 'SUCCESS' : doc.status === 'REJECTED' ? 'DANGER' : 'WARNING'}>
                    {doc.status}
                  </Badge>
                </div>

                <h3 className="text-sm font-bold text-slate-900 line-clamp-1" title={doc.documentName}>
                  {doc.documentName}
                </h3>
                <p className="text-xs font-semibold text-emerald-700 mt-0.5">{doc.documentType}</p>

                {doc.notes && (
                  <p className="text-xs text-slate-500 mt-2 line-clamp-2 italic bg-slate-50 p-2 rounded-lg border border-slate-100">
                    "{doc.notes}"
                  </p>
                )}

                <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-400 font-mono">
                  <span>{formatFileSize(doc.fileSize)}</span>
                  <span>{doc.createdAt ? new Date(doc.createdAt).toLocaleDateString() : 'N/A'}</span>
                </div>
              </div>

              <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
                <Button
                  size="xs"
                  variant="secondary"
                  onClick={() => handleDownload(doc)}
                  className="flex-1"
                >
                  <Download className="h-3.5 w-3.5 mr-1" /> Download
                </Button>
                <Button
                  size="xs"
                  variant="danger"
                  onClick={() => handleDelete(doc.id, doc.documentName)}
                  className="px-2"
                  title="Delete Document"
                >
                  <Trash2 className="h-3.5 w-3.5" />
                </Button>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Upload Modal */}
      <Modal
        isOpen={uploadModalOpen}
        onClose={() => setUploadModalOpen(false)}
        title="Upload Compliance Certificate or Document"
      >
        <form onSubmit={handleUploadSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
              Document Category / Type
            </label>
            <select
              value={documentType}
              onChange={(e) => setDocumentType(e.target.value)}
              className="w-full p-2.5 bg-white border border-slate-200 rounded-xl text-xs text-slate-800 focus:ring-2 focus:ring-emerald-500 focus:outline-hidden"
            >
              <option value="ISO Quality Certification">ISO Quality Certification (e.g. 9001 / 14001)</option>
              <option value="Environmental & RoHS Compliance">Environmental & RoHS / REACH Compliance</option>
              <option value="Commercial Liability Insurance">Commercial Liability Insurance Policy</option>
              <option value="Security & Data Privacy Audit">Security & SOC2 / Cyber Audit</option>
              <option value="Business Registration / W-9">Business Registration / W-9 / Tax ID</option>
              <option value="Product Specification Sheet">Product Specification & Technical Sheet</option>
              <option value="Other Compliance Document">Other Compliance Document</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
              Select File (PDF, PNG, JPG, DOCX, XLSX, ZIP — Max 15MB)
            </label>
            <input
              type="file"
              required
              onChange={(e) => setSelectedFile(e.target.files[0])}
              className="w-full text-xs text-slate-600 file:mr-3 file:py-2 file:px-3 file:rounded-xl file:border-0 file:text-xs file:font-semibold file:bg-emerald-50 file:text-emerald-700 hover:file:bg-emerald-100"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
              Document Notes / Description (Optional)
            </label>
            <textarea
              rows={3}
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="e.g., Annual renewal certificate valid through Dec 2027..."
              className="w-full p-2.5 bg-white border border-slate-200 rounded-xl text-xs text-slate-800 focus:ring-2 focus:ring-emerald-500 focus:outline-hidden"
            />
          </div>

          <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
            <Button variant="secondary" type="button" onClick={() => setUploadModalOpen(false)}>
              Cancel
            </Button>
            <Button
              variant="primary"
              type="submit"
              className="bg-emerald-600 hover:bg-emerald-700 text-white"
              disabled={uploading || !selectedFile}
            >
              {uploading ? 'Uploading...' : 'Upload Document'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default SupplierDocuments;
