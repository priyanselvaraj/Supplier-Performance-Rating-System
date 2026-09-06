import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { supplierService } from '../../services/supplier.service';
import { evaluationService } from '../../services/evaluation.service';
import { aiService } from '../../services/ai.service';
import { improvementService } from '../../services/improvement.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import { Table } from '../../components/common/Table';
import { Toast } from '../../components/common/Toast';
import { SupplierFormModal } from './SupplierFormModal';
import {
  ArrowLeft,
  Building2,
  Mail,
  Phone,
  MapPin,
  Calendar,
  Award,
  CheckSquare,
  Edit2,
  FileText,
  Sparkles,
  ShieldAlert,
  TrendingDown,
  TrendingUp,
  Minus,
  AlertTriangle,
  Lightbulb,
  Info,
  Wrench,
  Plus,
  Check,
  X,
  PlusCircle,
  ShieldCheck
} from 'lucide-react';

export const SupplierDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [supplier, setSupplier] = useState(null);
  const [evaluations, setEvaluations] = useState([]);
  const [aiInsights, setAiInsights] = useState(null);
  const [improvementActions, setImprovementActions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [toast, setToast] = useState({ message: '', type: 'success' });

  const loadData = async () => {
    setLoading(true);
    try {
      const [supRes, evalRes, aiRes, actionRes] = await Promise.all([
        supplierService.getSupplierById(id).catch((err) => {
          console.error('Error loading supplier:', err);
          return null;
        }),
        evaluationService.getEvaluationsBySupplier(id).catch((err) => {
          console.error('Error loading evaluations:', err);
          return null;
        }),
        aiService.getSupplierInsights(id).catch(() => null),
        improvementService.getActionsBySupplier(id).catch(() => null)
      ]);

      if (supRes && supRes.success) setSupplier(supRes.data);
      if (evalRes && evalRes.success) setEvaluations(evalRes.data || []);
      if (aiRes && aiRes.success) setAiInsights(aiRes.data);
      if (actionRes && (actionRes.data || actionRes.success)) {
        setImprovementActions(actionRes.data || []);
      }
    } catch (err) {
      console.error(err);
      setToast({ message: 'Failed to load supplier details', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [id]);

  const handleDecision = async (rec, status) => {
    try {
      await aiService.submitRecommendationDecision(1, {
        recommendationRef: rec.id,
        status: status,
        decisionNotes: `Human decision: ${status}`,
        actionTitle: status === 'ACTION_CREATED' ? `Remediate ${rec.criterionName}: ${rec.title || 'CAP'}` : null,
        actionDescription: status === 'ACTION_CREATED' ? rec.actionableSteps : null,
        targetCompletionDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
      });
      setToast({
        message: status === 'ACTION_CREATED'
          ? 'Corrective Action Plan (CAP) successfully initiated from recommendation!'
          : `Recommendation marked as ${status.toLowerCase()}.`,
        type: 'success'
      });
      loadData();
    } catch (err) {
      console.error(err);
      setToast({ message: 'Failed to record recommendation decision.', type: 'error' });
    }
  };

  if (loading) {
    return (
      <div className="py-20 text-center text-slate-500">
        <div className="inline-block animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600 mb-3"></div>
        <p className="text-sm font-medium">Loading Supplier Profile...</p>
      </div>
    );
  }

  if (!supplier) {
    return (
      <div className="text-center py-16">
        <p className="text-slate-600 font-medium">Supplier not found.</p>
        <Button variant="secondary" onClick={() => navigate('/suppliers')} className="mt-4" icon={ArrowLeft}>
          Back to Suppliers
        </Button>
      </div>
    );
  }

  const evaluationColumns = [
    {
      header: 'Evaluation Code',
      accessor: 'evaluationCode',
      render: (row) => (
        <span className="font-mono text-xs font-semibold text-blue-600">
          <Link to={`/evaluations/${row.id}`} className="hover:underline">
            {row.evaluationCode}
          </Link>
        </span>
      ),
    },
    {
      header: 'Evaluation Date',
      accessor: 'evaluationDate',
      render: (row) => <span className="text-xs text-slate-600">{row.evaluationDate}</span>,
    },
    {
      header: 'Period',
      accessor: 'evaluationPeriod',
      render: (row) => <span className="text-xs font-medium text-slate-700">{row.evaluationPeriod || 'N/A'}</span>,
    },
    {
      header: 'Evaluator',
      accessor: 'evaluatorName',
      render: (row) => <span className="text-xs text-slate-700">{row.evaluatorName}</span>,
    },
    {
      header: 'Score',
      accessor: 'totalWeightedScore',
      render: (row) => (
        <span className="font-bold text-slate-900 text-sm">
          {row.totalWeightedScore}%
        </span>
      ),
    },
    {
      header: 'Rating Category',
      accessor: 'ratingCategory',
      render: (row) => <Badge variant={row.ratingCategory} size="sm" />,
    },
    {
      header: 'Action',
      className: 'text-right',
      cellClassName: 'text-right',
      render: (row) => (
        <Link to={`/evaluations/${row.id}`}>
          <Button variant="ghost" size="sm" icon={FileText}>
            View Sheet
          </Button>
        </Link>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {toast?.message && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast({ message: '', type: 'success' })}
        />
      )}

      {/* Top back button & Action Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <Link
          to="/suppliers"
          className="inline-flex items-center text-sm font-medium text-slate-600 hover:text-slate-900 transition-colors"
        >
          <ArrowLeft className="h-4 w-4 mr-1.5" /> Back to Suppliers
        </Link>
        <div className="flex items-center gap-3">
          <Button variant="secondary" icon={Edit2} onClick={() => setIsEditModalOpen(true)}>
            Edit Profile
          </Button>
          <Link to={`/evaluations/new?supplierId=${supplier.id}`}>
            <Button variant="primary" icon={CheckSquare}>
              Evaluate Supplier
            </Button>
          </Link>
        </div>
      </div>

      {/* Hero Profile Card */}
      <Card bodyClassName="p-6 sm:p-8 bg-gradient-to-r from-slate-900 to-slate-800 text-white rounded-xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="flex items-start gap-4">
            <div className="h-16 w-16 rounded-2xl bg-blue-600/30 border border-blue-400/30 flex items-center justify-center text-white flex-shrink-0">
              <Building2 className="h-8 w-8 text-blue-400" />
            </div>
            <div>
              <div className="flex items-center gap-3 flex-wrap">
                <h1 className="text-2xl font-bold text-white tracking-tight">{supplier.name}</h1>
                <Badge variant={supplier.status} size="sm" />
                <span className="font-mono text-xs text-slate-300 bg-white/10 px-2.5 py-1 rounded-md">
                  {supplier.supplierCode}
                </span>
              </div>
              <p className="text-sm text-slate-300 mt-1">
                Category: <span className="text-white font-medium">{supplier.category?.name || 'Unassigned'}</span>
              </p>
            </div>
          </div>

          <div className="flex items-center gap-6 border-t md:border-t-0 md:border-l border-white/10 pt-4 md:pt-0 md:pl-8">
            <div className="text-left md:text-right">
              <p className="text-xs uppercase tracking-wider text-slate-400 font-semibold">Overall Composite Score</p>
              <div className="flex items-center md:justify-end gap-3 mt-1">
                <span className="text-3xl font-extrabold text-white">
                  {supplier.totalEvaluations > 0 ? `${supplier.overallRating}%` : 'N/A'}
                </span>
                <Badge variant={supplier.ratingCategory} size="md" />
              </div>
              <p className="text-xs text-slate-400 mt-1">
                Based on {supplier.totalEvaluations} evaluations
              </p>
            </div>
          </div>
        </div>
      </Card>

      {/* Metadata and Contact Information Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card title="Contact Information" className="md:col-span-2">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            <div className="flex items-start gap-3">
              <Mail className="h-5 w-5 text-slate-400 mt-0.5" />
              <div>
                <p className="text-xs text-slate-500 font-medium">Official Email</p>
                <p className="text-sm font-semibold text-slate-800 mt-0.5">{supplier.email}</p>
              </div>
            </div>

            <div className="flex items-start gap-3">
              <Phone className="h-5 w-5 text-slate-400 mt-0.5" />
              <div>
                <p className="text-xs text-slate-500 font-medium">Phone Number</p>
                <p className="text-sm font-semibold text-slate-800 mt-0.5">{supplier.phone || 'N/A'}</p>
              </div>
            </div>

            <div className="flex items-start gap-3">
              <Building2 className="h-5 w-5 text-slate-400 mt-0.5" />
              <div>
                <p className="text-xs text-slate-500 font-medium">Primary Contact</p>
                <p className="text-sm font-semibold text-slate-800 mt-0.5">{supplier.contactPerson || 'N/A'}</p>
              </div>
            </div>

            <div className="flex items-start gap-3">
              <MapPin className="h-5 w-5 text-slate-400 mt-0.5" />
              <div>
                <p className="text-xs text-slate-500 font-medium">Location</p>
                <p className="text-sm font-semibold text-slate-800 mt-0.5">
                  {[supplier.city, supplier.country].filter(Boolean).join(', ') || 'N/A'}
                </p>
                {supplier.address && <p className="text-xs text-slate-500 mt-1">{supplier.address}</p>}
              </div>
            </div>
          </div>
        </Card>

        <Card title="Performance Summary">
          <div className="space-y-4">
            <div>
              <div className="flex justify-between text-xs font-semibold mb-1">
                <span className="text-slate-600">Performance Score</span>
                <span className="text-slate-900">{supplier.overallRating}%</span>
              </div>
              <div className="w-full bg-slate-100 rounded-full h-2.5">
                <div
                  className={`h-2.5 rounded-full ${
                    supplier.overallRating >= 85
                      ? 'bg-emerald-500'
                      : supplier.overallRating >= 70
                      ? 'bg-blue-500'
                      : supplier.overallRating >= 50
                      ? 'bg-amber-500'
                      : 'bg-rose-500'
                  }`}
                  style={{ width: `${Math.min(supplier.overallRating, 100)}%` }}
                ></div>
              </div>
            </div>

            <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs">
              <span className="text-slate-500">Tier Classification</span>
              <span className="font-semibold text-slate-800">{supplier.ratingCategory}</span>
            </div>

            <div className="flex items-center justify-between text-xs">
              <span className="text-slate-500">Total Evaluations</span>
              <span className="font-semibold text-slate-800">{supplier.totalEvaluations} records</span>
            </div>
          </div>
        </Card>
      </div>

      {/* AI Intelligence & Predictive Analytics Insights */}
      {aiInsights && (
        <div className="space-y-4">
          <div className="flex items-center gap-2">
            <div className="p-1.5 bg-indigo-600 rounded-lg text-white">
              <Sparkles className="h-4 w-4" />
            </div>
            <div>
              <h3 className="text-base font-bold text-gray-900 dark:text-white">AI Predictive Intelligence & Risk Assessment</h3>
              <p className="text-xs text-gray-500">Machine-assisted forecasting, risk scoring, and prescriptive remediation</p>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {/* Prediction Card */}
            <Card title="Performance Prediction">
              <div className="space-y-3">
                {aiInsights.prediction?.sufficientData ? (
                  <>
                    <div className="flex items-baseline justify-between">
                      <span className="text-xs text-gray-500">Next Cycle Forecast</span>
                      <span className="text-2xl font-black text-indigo-600 dark:text-indigo-400">
                        {aiInsights.prediction.predictedScore}%
                      </span>
                    </div>
                    <div className="flex items-center justify-between text-xs pt-2 border-t border-gray-100 dark:border-gray-800">
                      <span className="text-gray-500">Trajectory Velocity</span>
                      <span className="font-semibold">{aiInsights.prediction.velocityRate > 0 ? `+${aiInsights.prediction.velocityRate}` : aiInsights.prediction.velocityRate} pts/cycle</span>
                    </div>
                    <div className="flex items-center justify-between text-xs">
                      <span className="text-gray-500">Confidence Rating</span>
                      <Badge variant={aiInsights.prediction.confidence === 'HIGH' ? 'success' : aiInsights.prediction.confidence === 'MEDIUM' ? 'warning' : 'default'} size="sm">
                        {aiInsights.prediction.confidence}
                      </Badge>
                    </div>
                    <p className="text-xs text-gray-600 dark:text-gray-400 bg-gray-50 dark:bg-gray-800 p-2.5 rounded-lg border border-gray-100 dark:border-gray-700">
                      {aiInsights.prediction.explanation}
                    </p>
                  </>
                ) : (
                  <div className="py-4 text-center">
                    <Info className="h-8 w-8 text-indigo-400 mx-auto mb-2" />
                    <p className="text-xs font-semibold text-gray-700 dark:text-gray-300">Insufficient Data for Prediction</p>
                    <p className="text-xs text-gray-500 mt-1">Requires at least 3 completed evaluation cycles to establish baseline trajectory.</p>
                  </div>
                )}
              </div>
            </Card>

            {/* Risk Assessment Card */}
            <Card title="Supplier Risk Intelligence">
              <div className="space-y-3">
                <div className="flex items-baseline justify-between">
                  <span className="text-xs text-gray-500">Risk Score</span>
                  <div className="flex items-center gap-2">
                    <span className={`text-2xl font-black ${
                      aiInsights.risk.riskScore >= 75 ? 'text-red-600' :
                      aiInsights.risk.riskScore >= 50 ? 'text-orange-600' :
                      aiInsights.risk.riskScore >= 25 ? 'text-amber-600' :
                      'text-emerald-600'
                    }`}>
                      {aiInsights.risk.riskScore}
                    </span>
                    <Badge variant={
                      aiInsights.risk.riskLevel === 'CRITICAL' || aiInsights.risk.riskLevel === 'HIGH' ? 'danger' :
                      aiInsights.risk.riskLevel === 'MEDIUM' ? 'warning' : 'success'
                    } size="sm">
                      {aiInsights.risk.riskLevel}
                    </Badge>
                  </div>
                </div>

                <div className="w-full bg-gray-100 dark:bg-gray-800 rounded-full h-2 overflow-hidden">
                  <div
                    className={`h-full ${
                      aiInsights.risk.riskScore >= 75 ? 'bg-red-500' :
                      aiInsights.risk.riskScore >= 50 ? 'bg-orange-500' :
                      aiInsights.risk.riskScore >= 25 ? 'bg-amber-500' :
                      'bg-emerald-500'
                    }`}
                    style={{ width: `${Math.min(100, aiInsights.risk.riskScore)}%` }}
                  />
                </div>

                <div className="pt-2 border-t border-gray-100 dark:border-gray-800 space-y-1">
                  <span className="text-xs font-semibold text-gray-700 dark:text-gray-300 block">Identified Factors:</span>
                  {(aiInsights.risk.riskFactors || []).map((factor, idx) => (
                    <div key={idx} className="flex items-start gap-1.5 text-xs text-gray-500">
                      <span className="text-amber-500 font-bold">•</span>
                      <span>{factor}</span>
                    </div>
                  ))}
                </div>
              </div>
            </Card>

            {/* Recommendations & Remediation Card with Human Decision Controls */}
            <Card title="Prescriptive Recommendations" subtitle="Human-controlled decision actions">
              <div className="space-y-3 max-h-72 overflow-y-auto pr-1">
                {(aiInsights.recommendations || []).map((rec) => (
                  <div key={rec.id} className="p-3 bg-slate-50 dark:bg-gray-800 rounded-xl border border-slate-200/80 dark:border-gray-700 text-xs space-y-2">
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-slate-800 dark:text-white">{rec.criterionName}</span>
                      <Badge variant={rec.priority === 'CRITICAL' ? 'danger' : rec.priority === 'HIGH' ? 'warning' : 'info'} size="sm">
                        {rec.priority}
                      </Badge>
                    </div>
                    <p className="text-slate-600 dark:text-gray-300 leading-relaxed">{rec.recommendation}</p>
                    <div className="text-indigo-600 dark:text-indigo-400 font-medium bg-indigo-50/50 dark:bg-indigo-950/30 p-2 rounded-lg border border-indigo-100 dark:border-indigo-900/40">
                      <strong>Suggested Action:</strong> {rec.actionableSteps}
                    </div>

                    {/* Human Action Decision Buttons */}
                    <div className="pt-2 flex flex-wrap items-center justify-end gap-1.5 border-t border-slate-200/60 dark:border-gray-700">
                      <button
                        type="button"
                        onClick={() => handleDecision(rec, 'ACCEPTED')}
                        className="text-[10px] font-bold px-2 py-1 rounded bg-emerald-50 text-emerald-700 border border-emerald-200 hover:bg-emerald-100 flex items-center gap-1 transition-colors cursor-pointer"
                        title="Accept recommendation without creating action plan"
                      >
                        <Check className="h-3 w-3" /> Accept
                      </button>
                      <button
                        type="button"
                        onClick={() => handleDecision(rec, 'DISMISSED')}
                        className="text-[10px] font-bold px-2 py-1 rounded bg-slate-100 text-slate-600 border border-slate-200 hover:bg-slate-200 flex items-center gap-1 transition-colors cursor-pointer"
                        title="Dismiss recommendation"
                      >
                        <X className="h-3 w-3" /> Dismiss
                      </button>
                      <button
                        type="button"
                        onClick={() => handleDecision(rec, 'ACTION_CREATED')}
                        className="text-[10px] font-bold px-2 py-1 rounded bg-blue-600 text-white hover:bg-blue-700 flex items-center gap-1 transition-colors cursor-pointer shadow-2xs"
                        title="Initiate official Corrective Action Plan (CAP)"
                      >
                        <PlusCircle className="h-3 w-3" /> Create CAP Action
                      </button>
                    </div>
                  </div>
                ))}
                {(!aiInsights.recommendations || aiInsights.recommendations.length === 0) && (
                  <p className="text-xs text-slate-500 py-4 text-center">No current criteria remediation required.</p>
                )}
              </div>
            </Card>
          </div>
        </div>
      )}

      {/* Supplier Improvement Actions (CAP) */}
      <Card
        title="Supplier Improvement Actions (CAP)"
        subtitle="Active remediation tasks, corrective action plans, and audit follow-ups"
      >
        <div className="space-y-3">
          {(!improvementActions || improvementActions.length === 0) ? (
            <div className="py-6 text-center text-xs text-slate-400">
              No improvement actions recorded for this supplier.
            </div>
          ) : (
            <div className="divide-y divide-slate-100">
              {(improvementActions || []).map((action) => (
                <div key={action.id} className="py-3 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-bold text-slate-800">{action.title}</span>
                      <span className={`text-[10px] px-2 py-0.5 font-bold rounded ${
                        action.priority === 'CRITICAL' ? 'bg-rose-100 text-rose-700' :
                        action.priority === 'HIGH' ? 'bg-amber-100 text-amber-700' :
                        'bg-blue-100 text-blue-700'
                      }`}>
                        {action.priority}
                      </span>
                      <span className="text-[10px] px-2 py-0.5 font-semibold rounded-full bg-slate-100 text-slate-700">
                        {action.status}
                      </span>
                    </div>
                    <p className="text-xs text-slate-500 mt-1">{action.description}</p>
                    {action.resolutionNotes && (
                      <div className="mt-1 text-[11px] text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded inline-block">
                        Resolution: {action.resolutionNotes}
                      </div>
                    )}
                  </div>
                  <div className="text-right text-xs text-slate-400">
                    <div>Due: {action.dueDate || 'No deadline'}</div>
                    <div className="text-[11px] text-slate-500">Assigned: {action.assignedUserName || 'Unassigned'}</div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </Card>

      {/* Historical Evaluation Records */}
      <Card
        title="Evaluation History"
        subtitle="Complete chronological record of supplier audits and evaluations"
        bodyClassName="p-0"
      >
        <Table
          columns={evaluationColumns}
          data={evaluations}
          emptyMessage="No evaluations have been recorded for this supplier yet."
        />
      </Card>

      {/* Edit Modal */}
      <SupplierFormModal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        supplier={supplier}
        onSuccess={() => {
          setToast({ message: 'Supplier profile updated successfully', type: 'success' });
          loadData();
        }}
      />
    </div>
  );
};
