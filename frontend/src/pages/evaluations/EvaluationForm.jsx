import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { supplierService } from '../../services/supplier.service';
import { criteriaService } from '../../services/criteria.service';
import { evaluationService } from '../../services/evaluation.service';
import { Card } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { Input } from '../../components/common/Input';
import { Badge } from '../../components/common/Badge';
import { Toast } from '../../components/common/Toast';
import {
  ArrowLeft,
  Calculator,
  CheckCircle,
  HelpCircle,
  Building2,
  Calendar,
  AlertCircle
} from 'lucide-react';

export const EvaluationForm = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const initialSupplierId = searchParams.get('supplierId') || '';

  const [suppliers, setSuppliers] = useState([]);
  const [criteriaList, setCriteriaList] = useState([]);
  const [selectedSupplierId, setSelectedSupplierId] = useState(initialSupplierId);

  const [evaluationDate, setEvaluationDate] = useState(new Date().toISOString().split('T')[0]);
  const [evaluationPeriod, setEvaluationPeriod] = useState(`Q3 ${new Date().getFullYear()}`);
  const [generalComments, setGeneralComments] = useState('');
  const [strengths, setStrengths] = useState('');
  const [areasForImprovement, setAreasForImprovement] = useState('');
  const [recommendation, setRecommendation] = useState('');

  // Criterion scores state: { [criteriaId]: { scoreObtained: number, remarks: string } }
  const [scores, setScores] = useState({});

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [toast, setToast] = useState({ message: '', type: 'success' });

  useEffect(() => {
    const loadInitialData = async () => {
      setLoading(true);
      try {
        const [supRes, critRes] = await Promise.all([
          supplierService.getAllSuppliers(),
          criteriaService.getActiveCriteria(),
        ]);

        if (supRes.success) setSuppliers(supRes.data);
        if (critRes.success) {
          setCriteriaList(critRes.data);
          // Initialize scores map with default 80% score
          const initialScores = {};
          critRes.data.forEach((c) => {
            initialScores[c.id] = {
              scoreObtained: Math.round((c.maxScore || 100) * 0.8),
              remarks: '',
            };
          });
          setScores(initialScores);
        }
      } catch (err) {
        console.error(err);
        setError('Failed to load supplier evaluation form prerequisites');
      } finally {
        setLoading(false);
      }
    };

    loadInitialData();
  }, []);

  // Live Weighted Score Calculation
  const calculateLiveScore = () => {
    let totalWeighted = 0;
    let totalWeight = 0;

    criteriaList.forEach((c) => {
      const scoreObj = scores[c.id];
      if (scoreObj && scoreObj.scoreObtained !== undefined) {
        const obtained = parseFloat(scoreObj.scoreObtained) || 0;
        const max = c.maxScore || 100;
        const weight = c.weight || 0;

        const weightedPart = (obtained / max) * weight;
        totalWeighted += weightedPart;
        totalWeight += weight;
      }
    });

    const finalPercent = totalWeight > 0 ? (totalWeighted / totalWeight) * 100 : 0;
    return Math.round(finalPercent * 100) / 100;
  };

  const currentScore = calculateLiveScore();

  const getTier = (score) => {
    if (score >= 85) return 'EXCELLENT';
    if (score >= 70) return 'GOOD';
    if (score >= 50) return 'AVERAGE';
    return 'POOR';
  };

  const handleScoreChange = (criteriaId, scoreObtained) => {
    setScores((prev) => ({
      ...prev,
      [criteriaId]: {
        ...prev[criteriaId],
        scoreObtained: parseFloat(scoreObtained) || 0,
      },
    }));
  };

  const handleRemarksChange = (criteriaId, remarks) => {
    setScores((prev) => ({
      ...prev,
      [criteriaId]: {
        ...prev[criteriaId],
        remarks,
      },
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedSupplierId) {
      setError('Please select a supplier to evaluate');
      return;
    }

    if (criteriaList.length === 0) {
      setError('No active evaluation criteria found');
      return;
    }

    setSubmitting(true);
    setError('');

    try {
      const scoreRequests = criteriaList.map((c) => ({
        criteriaId: c.id,
        scoreObtained: scores[c.id]?.scoreObtained || 0,
        remarks: scores[c.id]?.remarks || '',
      }));

      const payload = {
        supplierId: parseInt(selectedSupplierId),
        evaluationDate,
        evaluationPeriod,
        generalComments,
        strengths,
        areasForImprovement,
        recommendation,
        scores: scoreRequests,
      };

      const res = await evaluationService.submitEvaluation(payload);
      if (res.success && res.data) {
        setToast({ message: 'Supplier evaluated successfully! Redirecting...', type: 'success' });
        setTimeout(() => {
          navigate(`/evaluations/${res.data.id}`);
        }, 1200);
      }
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Evaluation submission failed';
      setError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="py-20 text-center text-slate-500">
        <div className="inline-block animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600 mb-3"></div>
        <p className="text-sm font-medium">Loading Evaluation Matrix...</p>
      </div>
    );
  }

  const selectedSupplier = suppliers.find((s) => String(s.id) === String(selectedSupplierId));

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
          <Link
            to="/evaluations"
            className="inline-flex items-center text-xs font-medium text-slate-500 hover:text-slate-800 mb-2 transition-colors"
          >
            <ArrowLeft className="h-3.5 w-3.5 mr-1" /> Back to Evaluations
          </Link>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Perform Supplier Evaluation</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Score performance criteria to calculate the weighted composite rating.
          </p>
        </div>

        {/* Live Floating Rating Preview Badge */}
        <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div>
            <span className="text-[11px] font-semibold uppercase text-slate-500 block">Calculated Rating</span>
            <div className="flex items-center gap-2 mt-0.5">
              <span className="text-2xl font-extrabold text-slate-900">{currentScore}%</span>
              <Badge variant={getTier(currentScore)} size="sm" />
            </div>
          </div>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 text-rose-700 text-sm rounded-xl flex items-start gap-3">
          <AlertCircle className="h-5 w-5 flex-shrink-0 text-rose-500" />
          <span>{error}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Step 1: Supplier & Audit Context */}
        <Card title="1. Evaluation Metadata & Target Supplier">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Target Supplier <span className="text-rose-500">*</span>
              </label>
              <select
                value={selectedSupplierId}
                onChange={(e) => setSelectedSupplierId(e.target.value)}
                required
                className="w-full px-3.5 py-2 text-sm bg-white border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Select a Supplier</option>
                {suppliers.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.name} ({s.supplierCode}) - {s.category?.name}
                  </option>
                ))}
              </select>
            </div>

            <Input
              label="Evaluation Date"
              type="date"
              value={evaluationDate}
              onChange={(e) => setEvaluationDate(e.target.value)}
              required
            />

            <Input
              label="Evaluation Period / Cycle"
              value={evaluationPeriod}
              onChange={(e) => setEvaluationPeriod(e.target.value)}
              placeholder="e.g. Q3 2026 or Aug 2026"
              required
            />
          </div>

          {selectedSupplier && (
            <div className="mt-4 p-3 bg-blue-50/60 rounded-lg border border-blue-100 flex items-center justify-between text-xs text-blue-900">
              <div className="flex items-center gap-2">
                <Building2 className="h-4 w-4 text-blue-600" />
                <span>
                  Current Rating: <strong className="font-semibold">{selectedSupplier.overallRating}%</strong> (
                  {selectedSupplier.ratingCategory})
                </span>
              </div>
              <span>Category: {selectedSupplier.category?.name || 'N/A'}</span>
            </div>
          )}
        </Card>

        {/* Step 2: Multi-Criteria Scoring Table */}
        <Card
          title="2. Criteria Scoring Matrix"
          subtitle="Rate the supplier on each weighted dimension from 0 to Maximum Score."
        >
          <div className="space-y-6">
            {criteriaList.map((crit, idx) => {
              const currentVal = scores[crit.id]?.scoreObtained ?? 0;
              const maxVal = crit.maxScore || 100;
              const weightVal = crit.weight || 0;
              const weightedContribution = Math.round(((currentVal / maxVal) * weightVal) * 100) / 100;

              return (
                <div
                  key={crit.id}
                  className="p-4 bg-slate-50/75 rounded-xl border border-slate-200/80 space-y-3"
                >
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="h-6 w-6 rounded-full bg-blue-100 text-blue-700 font-bold text-xs flex items-center justify-center">
                          {idx + 1}
                        </span>
                        <h4 className="font-semibold text-slate-800 text-sm">{crit.name}</h4>
                        <span className="text-xs bg-slate-200 text-slate-700 font-semibold px-2 py-0.5 rounded">
                          Weight: {crit.weight}%
                        </span>
                      </div>
                      {crit.description && (
                        <p className="text-xs text-slate-500 mt-1 pl-8">{crit.description}</p>
                      )}
                    </div>

                    <div className="text-right sm:pl-4">
                      <span className="text-xs text-slate-500 block">Weighted Contribution</span>
                      <span className="text-base font-bold text-blue-600">
                        {weightedContribution} / {weightVal} pts
                      </span>
                    </div>
                  </div>

                  {/* Slider & Number Input */}
                  <div className="pl-0 sm:pl-8 grid grid-cols-1 sm:grid-cols-4 gap-4 items-center">
                    <div className="sm:col-span-3 flex items-center gap-3">
                      <input
                        type="range"
                        min="0"
                        max={maxVal}
                        step="1"
                        value={currentVal}
                        onChange={(e) => handleScoreChange(crit.id, e.target.value)}
                        className="w-full h-2 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
                      />
                    </div>
                    <div className="flex items-center gap-2">
                      <input
                        type="number"
                        min="0"
                        max={maxVal}
                        value={currentVal}
                        onChange={(e) => handleScoreChange(crit.id, e.target.value)}
                        className="w-20 px-2.5 py-1.5 text-sm font-semibold text-center bg-white border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      />
                      <span className="text-xs text-slate-500 font-medium">/ {maxVal} max</span>
                    </div>
                  </div>

                  {/* Remarks input */}
                  <div className="pl-0 sm:pl-8">
                    <input
                      type="text"
                      placeholder="Add specific remarks / evidence for this score (optional)..."
                      value={scores[crit.id]?.remarks || ''}
                      onChange={(e) => handleRemarksChange(crit.id, e.target.value)}
                      className="w-full px-3 py-1.5 text-xs bg-white border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </Card>

        {/* Step 3: Observations & Recommendations */}
        <Card title="3. Qualitative Assessment & Recommendations">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Key Strengths</label>
              <textarea
                rows="3"
                value={strengths}
                onChange={(e) => setStrengths(e.target.value)}
                placeholder="High reliability in batch delivery, zero defective lots..."
                className="w-full px-3.5 py-2 text-sm bg-white border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              ></textarea>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Areas for Improvement</label>
              <textarea
                rows="3"
                value={areasForImprovement}
                onChange={(e) => setAreasForImprovement(e.target.value)}
                placeholder="Invoicing turnaround time, weekend response speed..."
                className="w-full px-3.5 py-2 text-sm bg-white border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              ></textarea>
            </div>
          </div>

          <div className="mt-4">
            <label className="block text-sm font-medium text-slate-700 mb-1">Overall Recommendation & Action Plan</label>
            <textarea
              rows="3"
              value={recommendation}
              onChange={(e) => setRecommendation(e.target.value)}
              placeholder="Recommended for contract renewal with volume discount tier..."
              className="w-full px-3.5 py-2 text-sm bg-white border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            ></textarea>
          </div>
        </Card>

        {/* Form Submission Buttons */}
        <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-200">
          <Button variant="secondary" onClick={() => navigate('/evaluations')}>
            Cancel
          </Button>
          <Button type="submit" variant="primary" size="lg" loading={submitting}>
            Submit Evaluation &amp; Compute Rating
          </Button>
        </div>
      </form>
    </div>
  );
};
