import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import supplierPortalService from '../../services/supplierPortal.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import {
  ArrowLeft,
  CheckSquare,
  Award,
  Calendar,
  Layers,
  Sparkles,
  AlertCircle,
  ThumbsUp,
  TrendingUp,
  FileText
} from 'lucide-react';

export const SupplierEvaluationDetails = () => {
  const { id } = useParams();
  const [evaluation, setEvaluation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDetails = async () => {
      try {
        setLoading(true);
        const res = await supplierPortalService.getEvaluationDetails(id);
        if (res.success) {
          setEvaluation(res.data);
        }
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to load evaluation scorecard details.');
      } finally {
        setLoading(false);
      }
    };

    fetchDetails();
  }, [id]);

  if (loading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-emerald-600 border-t-transparent"></div>
      </div>
    );
  }

  if (error || !evaluation) {
    return (
      <div className="rounded-2xl border border-rose-200 bg-rose-50 p-6 text-center text-rose-800">
        <AlertCircle className="mx-auto h-8 w-8 text-rose-600 mb-2" />
        <h3 className="text-base font-bold">Unable to Load Scorecard</h3>
        <p className="text-sm mt-1">{error || 'Evaluation record not found or unauthorized.'}</p>
        <Link to="/supplier-portal/evaluations">
          <Button variant="secondary" className="mt-4">
            <ArrowLeft className="h-4 w-4 mr-2" /> Back to Evaluations
          </Button>
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Back Button & Header */}
      <div className="flex items-center gap-3">
        <Link to="/supplier-portal/evaluations">
          <Button variant="secondary" size="sm">
            <ArrowLeft className="h-4 w-4 mr-1.5" /> Back
          </Button>
        </Link>
        <div>
          <h1 className="text-2xl font-bold text-slate-900">
            Evaluation Scorecard: {evaluation.evaluationCode}
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Audit Period: <span className="font-semibold text-slate-700">{evaluation.evaluationPeriod}</span> • Audit Date: <span className="font-semibold text-slate-700">{evaluation.evaluationDate}</span>
          </p>
        </div>
      </div>

      {/* Score Summary Banner */}
      <div className="rounded-2xl bg-gradient-to-r from-slate-900 to-slate-800 p-6 text-white shadow-md flex flex-col sm:flex-row sm:items-center justify-between gap-6">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">Total Weighted Score</p>
          <div className="mt-2 flex items-baseline gap-2">
            <span className="text-5xl font-black text-white">
              {evaluation.totalWeightedScore?.toFixed(1)}
            </span>
            <span className="text-sm text-slate-400">/ 100</span>
          </div>
          <p className="text-xs text-emerald-400 mt-2 font-medium">
            Evaluated & Certified by Corporate Procurement
          </p>
        </div>

        <div className="flex flex-col items-start sm:items-end gap-2">
          <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">Performance Classification</p>
          <Badge variant={evaluation.ratingCategory} size="md">
            {evaluation.ratingCategory}
          </Badge>
          <span className="text-xs text-slate-400 font-mono mt-1">Status: COMPLETED</span>
        </div>
      </div>

      {/* Criteria Breakdown Scorecard */}
      <Card className="p-6 border-slate-200/80 shadow-xs">
        <h2 className="text-base font-bold text-slate-900 mb-4 flex items-center gap-2">
          <Layers className="h-5 w-5 text-emerald-600" />
          Weighted Criteria Scorecard Breakdown
        </h2>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-slate-200 bg-slate-50/60 text-xs uppercase font-semibold text-slate-500">
              <tr>
                <th className="py-3 px-4">Evaluation Criteria</th>
                <th className="py-3 px-4 text-center">Weight</th>
                <th className="py-3 px-4 text-center">Score Obtained</th>
                <th className="py-3 px-4 text-center">Weighted Score</th>
                <th className="py-3 px-4">Auditor Remarks</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {evaluation.criteriaScores?.map((score) => (
                <tr key={score.criteriaId} className="hover:bg-slate-50/50">
                  <td className="py-3.5 px-4">
                    <p className="font-bold text-slate-900">{score.criteriaName}</p>
                    <p className="text-xs text-slate-500 mt-0.5">{score.description}</p>
                  </td>
                  <td className="py-3.5 px-4 text-center font-semibold text-slate-700">
                    {score.weight}%
                  </td>
                  <td className="py-3.5 px-4 text-center">
                    <span className="font-bold text-slate-900">{score.scoreObtained?.toFixed(1)}</span>
                    <span className="text-xs text-slate-400"> / {score.maxScore || 100}</span>
                  </td>
                  <td className="py-3.5 px-4 text-center font-black text-emerald-700">
                    {score.weightedScore?.toFixed(2)}
                  </td>
                  <td className="py-3.5 px-4 text-xs text-slate-600 italic">
                    {score.remarks || 'Standard compliance verified.'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>

      {/* Qualitative Feedback Sections */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Strengths */}
        <Card className="p-5 border-slate-200/80 shadow-xs bg-emerald-50/30">
          <div className="flex items-center gap-2 text-emerald-800 font-bold mb-2">
            <ThumbsUp className="h-4 w-4 text-emerald-600" />
            <h3 className="text-sm">Observed Strengths</h3>
          </div>
          <p className="text-xs text-slate-700 leading-relaxed">
            {evaluation.strengths || 'Consistent adherence to delivery schedules and high batch compliance.'}
          </p>
        </Card>

        {/* Improvement Areas */}
        <Card className="p-5 border-slate-200/80 shadow-xs bg-amber-50/30">
          <div className="flex items-center gap-2 text-amber-800 font-bold mb-2">
            <TrendingUp className="h-4 w-4 text-amber-600" />
            <h3 className="text-sm">Areas for Improvement</h3>
          </div>
          <p className="text-xs text-slate-700 leading-relaxed">
            {evaluation.areasForImprovement || 'Proactive lead-time notification during component supply chain bottlenecks.'}
          </p>
        </Card>

        {/* Recommendations */}
        <Card className="p-5 border-slate-200/80 shadow-xs bg-blue-50/30">
          <div className="flex items-center gap-2 text-blue-800 font-bold mb-2">
            <Sparkles className="h-4 w-4 text-blue-600" />
            <h3 className="text-sm">Recommendations</h3>
          </div>
          <p className="text-xs text-slate-700 leading-relaxed">
            {evaluation.recommendation || 'Continue quarterly quality reviews and maintain current lead-time standards.'}
          </p>
        </Card>
      </div>
    </div>
  );
};

export default SupplierEvaluationDetails;
