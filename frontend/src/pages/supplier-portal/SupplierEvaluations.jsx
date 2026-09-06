import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import supplierPortalService from '../../services/supplierPortal.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import {
  CheckSquare,
  Calendar,
  Award,
  ArrowRight,
  Search,
  FileCheck2
} from 'lucide-react';

export const SupplierEvaluations = () => {
  const [evaluations, setEvaluations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');

  const fetchEvaluations = async () => {
    try {
      setLoading(true);
      const res = await supplierPortalService.getEvaluations();
      if (res.success) {
        setEvaluations(res.data || []);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to retrieve evaluations.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEvaluations();
  }, []);

  const filteredEvaluations = evaluations.filter((ev) => {
    const q = searchQuery.toLowerCase();
    return (
      ev.evaluationCode?.toLowerCase().includes(q) ||
      ev.evaluationPeriod?.toLowerCase().includes(q) ||
      ev.ratingCategory?.toLowerCase().includes(q)
    );
  });

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
          <h1 className="text-2xl font-bold text-slate-900">Evaluation Scorecards & Audit History</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Detailed performance feedback, weighted score breakdowns, and evaluator recommendations.
          </p>
        </div>
        <div className="relative w-full sm:w-64">
          <Search className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
          <input
            type="text"
            placeholder="Search evaluations..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-9 pr-4 py-2 bg-white border border-slate-200 rounded-xl text-xs focus:ring-2 focus:ring-emerald-500 focus:outline-hidden"
          />
        </div>
      </div>

      {/* Evaluations List */}
      {filteredEvaluations.length === 0 ? (
        <Card className="p-12 text-center text-slate-500 border-slate-200/80">
          <FileCheck2 className="mx-auto h-12 w-12 text-slate-400 mb-3" />
          <h3 className="text-base font-bold text-slate-700">No Evaluation Scorecards Found</h3>
          <p className="text-xs text-slate-400 mt-1">
            {searchQuery ? 'No records match your search filter.' : 'Completed evaluation scorecards will be published here.'}
          </p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {filteredEvaluations.map((ev) => (
            <Card key={ev.id} className="p-5 border-slate-200/80 shadow-xs hover:shadow-md transition-shadow flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between gap-2 mb-3">
                  <div className="flex items-center gap-2">
                    <div className="rounded-lg bg-emerald-50 p-2 text-emerald-600">
                      <CheckSquare className="h-4 w-4" />
                    </div>
                    <div>
                      <p className="text-sm font-bold text-slate-900">{ev.evaluationCode}</p>
                      <p className="text-xs text-slate-500">{ev.evaluationPeriod}</p>
                    </div>
                  </div>
                  <Badge variant={ev.ratingCategory}>{ev.ratingCategory}</Badge>
                </div>

                {/* Score Big Display */}
                <div className="my-4 p-4 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between">
                  <div>
                    <p className="text-xs font-semibold text-slate-400 uppercase">Weighted Score</p>
                    <p className="text-2xl font-black text-slate-900 mt-0.5">
                      {ev.totalWeightedScore?.toFixed(1)} <span className="text-xs font-normal text-slate-400">/ 100</span>
                    </p>
                  </div>
                  <div className="text-right text-xs text-slate-500">
                    <p className="flex items-center gap-1">
                      <Calendar className="h-3.5 w-3.5" /> {ev.evaluationDate || 'N/A'}
                    </p>
                    <p className="mt-1 font-medium text-emerald-600">Completed & Verified</p>
                  </div>
                </div>

                {/* Feedback Snippet */}
                {ev.strengths && (
                  <div className="text-xs text-slate-600 mb-2 line-clamp-2">
                    <span className="font-semibold text-slate-700">Strengths:</span> {ev.strengths}
                  </div>
                )}
                {ev.recommendation && (
                  <div className="text-xs text-slate-600 line-clamp-2">
                    <span className="font-semibold text-slate-700">Recommendation:</span> {ev.recommendation}
                  </div>
                )}
              </div>

              <div className="mt-5 pt-4 border-t border-slate-100 flex items-center justify-between">
                <span className="text-xs text-slate-400 font-medium">
                  {ev.criteriaScores?.length || 5} Criteria Evaluated
                </span>
                <Link to={`/supplier-portal/evaluations/${ev.id}`}>
                  <Button size="sm" className="bg-emerald-600 hover:bg-emerald-700 text-white">
                    Inspect Scorecard <ArrowRight className="h-3.5 w-3.5 ml-1.5" />
                  </Button>
                </Link>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default SupplierEvaluations;
