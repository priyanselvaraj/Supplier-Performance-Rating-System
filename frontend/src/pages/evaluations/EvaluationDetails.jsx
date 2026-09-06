import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { evaluationService } from '../../services/evaluation.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import {
  ArrowLeft,
  Printer,
  Building2,
  Calendar,
  User,
  CheckCircle,
  FileText,
  Award
} from 'lucide-react';

export const EvaluationDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [evaluation, setEvaluation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchEvaluation = async () => {
      setLoading(true);
      try {
        const res = await evaluationService.getEvaluationById(id);
        if (res.success) {
          setEvaluation(res.data);
        }
      } catch (err) {
        console.error(err);
        setError('Failed to load evaluation sheet details');
      } finally {
        setLoading(false);
      }
    };

    fetchEvaluation();
  }, [id]);

  const handlePrint = () => {
    window.print();
  };

  if (loading) {
    return (
      <div className="py-20 text-center text-slate-500">
        <div className="inline-block animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600 mb-3"></div>
        <p className="text-sm font-medium">Loading Evaluation Sheet...</p>
      </div>
    );
  }

  if (error || !evaluation) {
    return (
      <div className="text-center py-16">
        <p className="text-slate-600 font-medium">{error || 'Evaluation record not found.'}</p>
        <Button variant="secondary" onClick={() => navigate('/evaluations')} className="mt-4" icon={ArrowLeft}>
          Back to Evaluations
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-6 max-w-5xl mx-auto print:max-w-none print:m-0">
      {/* Top action bar (hidden during print) */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 print:hidden">
        <Link
          to="/evaluations"
          className="inline-flex items-center text-sm font-medium text-slate-600 hover:text-slate-900 transition-colors"
        >
          <ArrowLeft className="h-4 w-4 mr-1.5" /> Back to Evaluations
        </Link>
        <div className="flex items-center gap-3">
          <Button variant="secondary" icon={Printer} onClick={handlePrint}>
            Print / Export PDF
          </Button>
          <Link to={`/evaluations/new?supplierId=${evaluation.supplierId}`}>
            <Button variant="primary">
              New Evaluation
            </Button>
          </Link>
        </div>
      </div>

      {/* Printable Evaluation Audit Sheet */}
      <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6 sm:p-10 space-y-8 print:border-none print:shadow-none print:p-0">
        {/* Document Header */}
        <div className="border-b border-slate-200 pb-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="inline-block bg-blue-50 text-blue-700 text-xs font-bold uppercase tracking-wider px-2.5 py-1 rounded mb-2">
              Official Evaluation Audit Sheet
            </div>
            <h1 className="text-2xl font-extrabold text-slate-900">
              Supplier Performance Evaluation
            </h1>
            <p className="text-xs text-slate-500 mt-1 font-mono">
              Reference Document: <strong className="text-slate-800">{evaluation.evaluationCode}</strong>
            </p>
          </div>

          <div className="text-left sm:text-right bg-slate-50 sm:bg-transparent p-3 sm:p-0 rounded-xl">
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Overall Rating</p>
            <div className="flex items-center sm:justify-end gap-2.5 mt-0.5">
              <span className="text-3xl font-black text-slate-900">{evaluation.totalWeightedScore}%</span>
              <Badge variant={evaluation.ratingCategory} size="md" />
            </div>
          </div>
        </div>

        {/* Audit Meta Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 p-4 rounded-xl bg-slate-50 border border-slate-100 text-xs">
          <div>
            <span className="text-slate-400 font-medium block">Supplier Name</span>
            <Link
              to={`/suppliers/${evaluation.supplierId}`}
              className="font-bold text-slate-900 text-sm hover:text-blue-600 block mt-0.5"
            >
              {evaluation.supplierName}
            </Link>
            <span className="text-slate-500 font-mono text-[11px]">{evaluation.supplierCode}</span>
          </div>

          <div>
            <span className="text-slate-400 font-medium block">Supplier Category</span>
            <span className="font-semibold text-slate-800 text-sm block mt-0.5">
              {evaluation.supplierCategoryName}
            </span>
          </div>

          <div>
            <span className="text-slate-400 font-medium block">Evaluation Period</span>
            <span className="font-semibold text-slate-800 text-sm block mt-0.5">
              {evaluation.evaluationPeriod || 'N/A'}
            </span>
            <span className="text-slate-500">{evaluation.evaluationDate}</span>
          </div>

          <div>
            <span className="text-slate-400 font-medium block">Certified Evaluator</span>
            <span className="font-semibold text-slate-800 text-sm block mt-0.5">
              {evaluation.evaluatorName}
            </span>
            <span className="text-slate-500">@{evaluation.evaluatorUsername}</span>
          </div>
        </div>

        {/* Criteria Breakdown Table */}
        <div>
          <h3 className="text-base font-bold text-slate-900 mb-3">Criteria Scoring Breakdown</h3>
          <div className="overflow-x-auto border border-slate-200 rounded-xl">
            <table className="min-w-full divide-y divide-slate-200 text-left text-sm">
              <thead className="bg-slate-50 text-xs uppercase font-semibold text-slate-600">
                <tr>
                  <th className="px-4 py-3">Criterion</th>
                  <th className="px-4 py-3 text-center">Weight</th>
                  <th className="px-4 py-3 text-center">Score Obtained</th>
                  <th className="px-4 py-3 text-center">Max Score</th>
                  <th className="px-4 py-3 text-right">Weighted Contribution</th>
                  <th className="px-4 py-3">Remarks / Evidence</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 bg-white">
                {evaluation.scores?.map((score) => (
                  <tr key={score.id} className="hover:bg-slate-50/50">
                    <td className="px-4 py-3 font-semibold text-slate-800">
                      {score.criteriaName}
                      {score.criteriaCode && (
                        <span className="text-slate-400 text-xs font-mono ml-2">({score.criteriaCode})</span>
                      )}
                    </td>
                    <td className="px-4 py-3 text-center font-semibold text-blue-700 bg-blue-50/30">
                      {score.weight}%
                    </td>
                    <td className="px-4 py-3 text-center font-bold text-slate-900">
                      {score.scoreObtained}
                    </td>
                    <td className="px-4 py-3 text-center text-slate-500">
                      {score.maxScore}
                    </td>
                    <td className="px-4 py-3 text-right font-bold text-emerald-600">
                      {score.weightedScore} pts
                    </td>
                    <td className="px-4 py-3 text-xs text-slate-600 italic">
                      {score.remarks || '-'}
                    </td>
                  </tr>
                ))}
              </tbody>
              <tfoot className="bg-slate-50 font-bold border-t-2 border-slate-200 text-slate-900">
                <tr>
                  <td className="px-4 py-3">Total Calculated Rating</td>
                  <td className="px-4 py-3 text-center">
                    {evaluation.scores?.reduce((sum, s) => sum + s.weight, 0)}%
                  </td>
                  <td colSpan={2}></td>
                  <td className="px-4 py-3 text-right text-base text-blue-600">
                    {evaluation.totalWeightedScore}%
                  </td>
                  <td className="px-4 py-3">
                    <Badge variant={evaluation.ratingCategory} size="sm" />
                  </td>
                </tr>
              </tfoot>
            </table>
          </div>
        </div>

        {/* Qualitative Findings */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-4">
          <div className="p-4 rounded-xl bg-slate-50 border border-slate-200/80">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-700 mb-2">
              Key Strengths &amp; Commendations
            </h4>
            <p className="text-xs text-slate-700 whitespace-pre-line leading-relaxed">
              {evaluation.strengths || 'No specific strengths recorded.'}
            </p>
          </div>

          <div className="p-4 rounded-xl bg-slate-50 border border-slate-200/80">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-700 mb-2">
              Areas for Improvement &amp; Corrective Actions
            </h4>
            <p className="text-xs text-slate-700 whitespace-pre-line leading-relaxed">
              {evaluation.areasForImprovement || 'No corrective action items noted.'}
            </p>
          </div>
        </div>

        {evaluation.recommendation && (
          <div className="p-4 rounded-xl bg-blue-50/50 border border-blue-100">
            <h4 className="text-xs font-bold uppercase tracking-wider text-blue-900 mb-1.5">
              Evaluator Recommendation
            </h4>
            <p className="text-xs text-blue-900 whitespace-pre-line leading-relaxed">
              {evaluation.recommendation}
            </p>
          </div>
        )}

        {/* Audit Sign-off Footer */}
        <div className="pt-8 border-t border-slate-200 flex items-center justify-between text-xs text-slate-500">
          <div>
            <p>Generated on: {evaluation.createdAt?.substring(0, 10) || new Date().toISOString().substring(0, 10)}</p>
            <p className="mt-0.5">Supplier Performance Rating System (SPRS) v1.0</p>
          </div>
          <div className="text-right">
            <p className="font-semibold text-slate-700">Audit Status: Verified &amp; Recorded</p>
            <p className="mt-0.5 text-slate-400">Electronic Record ID: {evaluation.id}</p>
          </div>
        </div>
      </div>
    </div>
  );
};
