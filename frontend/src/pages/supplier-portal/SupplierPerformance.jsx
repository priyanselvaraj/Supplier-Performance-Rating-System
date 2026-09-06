import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import supplierPortalService from '../../services/supplierPortal.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import {
  Award,
  TrendingUp,
  TrendingDown,
  Minus,
  CheckSquare,
  Sparkles,
  ShieldAlert,
  ShieldCheck,
  Calendar,
  Layers,
  ArrowRight
} from 'lucide-react';

export const SupplierPerformance = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchPerformance = async () => {
    try {
      setLoading(true);
      const res = await supplierPortalService.getPerformance();
      if (res.success) {
        setData(res.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load supplier performance records.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPerformance();
  }, []);

  if (loading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-emerald-600 border-t-transparent"></div>
      </div>
    );
  }

  const renderTrendBadge = (trend) => {
    switch (trend) {
      case 'IMPROVING':
        return (
          <span className="inline-flex items-center gap-1 text-xs font-bold text-emerald-600 bg-emerald-50 px-2.5 py-1 rounded-full border border-emerald-200">
            <TrendingUp className="h-3.5 w-3.5" /> Improving
          </span>
        );
      case 'DECLINING':
        return (
          <span className="inline-flex items-center gap-1 text-xs font-bold text-rose-600 bg-rose-50 px-2.5 py-1 rounded-full border border-rose-200">
            <TrendingDown className="h-3.5 w-3.5" /> Declining
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-1 text-xs font-bold text-slate-600 bg-slate-100 px-2.5 py-1 rounded-full border border-slate-200">
            <Minus className="h-3.5 w-3.5" /> Stable
          </span>
        );
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Performance Scorecards & Historical Ratings</h1>
        <p className="text-sm text-slate-500 mt-0.5">
          Review your overall vendor quality scores, grading trajectory, and evaluation audit records.
        </p>
      </div>

      {/* Summary Scorecard Banner */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="p-6 border-slate-200/80 shadow-xs flex flex-col justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">Current Rating</p>
            <div className="mt-2 flex items-baseline gap-2">
              <span className="text-4xl font-black text-slate-900">
                {data?.currentScore != null ? data.currentScore.toFixed(1) : 'N/A'}
              </span>
              <span className="text-sm text-slate-400 font-medium">/ 100</span>
            </div>
          </div>
          <div className="mt-4 flex items-center justify-between pt-4 border-t border-slate-100">
            <Badge variant={data?.ratingCategory} size="md">{data?.ratingCategory || 'UNRATED'}</Badge>
            {renderTrendBadge(data?.performanceTrend)}
          </div>
        </Card>

        <Card className="p-6 border-slate-200/80 shadow-xs flex flex-col justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">Performance Tier</p>
            <div className="mt-2">
              <span className="text-xl font-bold text-slate-800">
                {data?.performanceStatus?.replace(/_/g, ' ') || 'SATISFACTORY'}
              </span>
            </div>
            <p className="text-xs text-slate-500 mt-1">
              Evaluated across {data?.totalEvaluations || 0} completed quality audit rounds.
            </p>
          </div>
          <div className="mt-4 pt-4 border-t border-slate-100 flex items-center gap-2">
            <ShieldCheck className="h-4 w-4 text-emerald-600" />
            <span className="text-xs font-medium text-emerald-700">Preferred Vendor Qualification</span>
          </div>
        </Card>

        <Card className="p-6 border-slate-200/80 shadow-xs flex flex-col justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">Score Delta</p>
            <div className="mt-2 flex items-baseline gap-1">
              <span className={`text-3xl font-black ${(data?.scoreDifference || 0) >= 0 ? 'text-emerald-600' : 'text-rose-600'}`}>
                {(data?.scoreDifference || 0) >= 0 ? `+${(data?.scoreDifference || 0).toFixed(1)}` : (data?.scoreDifference || 0).toFixed(1)}
              </span>
              <span className="text-xs text-slate-500 font-medium">pts from last audit</span>
            </div>
          </div>
          <div className="mt-4 pt-4 border-t border-slate-100 flex items-center justify-between">
            <Link to="/supplier-portal/evaluations" className="text-xs font-bold text-emerald-600 hover:text-emerald-700 flex items-center gap-1">
              View Scorecards <ArrowRight className="h-3.5 w-3.5" />
            </Link>
          </div>
        </Card>
      </div>

      {/* AI Intelligence Insights */}
      {data?.aiInsights && (
        <Card className="p-6 border-indigo-200 bg-gradient-to-br from-indigo-50/50 via-white to-purple-50/40 shadow-xs">
          <div className="flex items-center gap-2 mb-4 text-indigo-950 font-bold">
            <Sparkles className="h-5 w-5 text-indigo-600" />
            <h2 className="text-base">AI Predictive Evaluation & Risk Assessment</h2>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="p-4 rounded-xl bg-white border border-indigo-100">
              <p className="text-xs font-semibold text-slate-500 uppercase">Key Identified Strengths</p>
              <ul className="mt-2 space-y-1.5 text-xs text-slate-700">
                <li className="flex items-center gap-1.5">
                  <span className="h-1.5 w-1.5 rounded-full bg-emerald-500"></span>
                  Reliable delivery timelines and low RMA return rate.
                </li>
                <li className="flex items-center gap-1.5">
                  <span className="h-1.5 w-1.5 rounded-full bg-emerald-500"></span>
                  High responsiveness during technical engineering reviews.
                </li>
              </ul>
            </div>

            <div className="p-4 rounded-xl bg-white border border-indigo-100">
              <p className="text-xs font-semibold text-slate-500 uppercase">Improvement Opportunities</p>
              <ul className="mt-2 space-y-1.5 text-xs text-slate-700">
                <li className="flex items-center gap-1.5">
                  <span className="h-1.5 w-1.5 rounded-full bg-amber-500"></span>
                  Advance batch certification documentation lead times.
                </li>
                <li className="flex items-center gap-1.5">
                  <span className="h-1.5 w-1.5 rounded-full bg-blue-500"></span>
                  Maintain pricing transparency on bulk component options.
                </li>
              </ul>
            </div>
          </div>
        </Card>
      )}

      {/* Historical Rating Engine Records Table */}
      <Card className="p-6 border-slate-200/80 shadow-xs">
        <h2 className="text-base font-bold text-slate-900 mb-4 flex items-center gap-2">
          <Layers className="h-5 w-5 text-slate-600" />
          Rating Engine Audit History
        </h2>

        {(!data?.ratingHistory || data.ratingHistory.length === 0) ? (
          <p className="text-sm text-slate-500 py-6 text-center">No historical rating records available.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="border-b border-slate-200 bg-slate-50/60 text-xs uppercase font-semibold text-slate-500">
                <tr>
                  <th className="py-3 px-4">Rating Date</th>
                  <th className="py-3 px-4">Score</th>
                  <th className="py-3 px-4">Rating Category</th>
                  <th className="py-3 px-4">Performance Status</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {data.ratingHistory.map((item) => (
                  <tr key={item.id} className="hover:bg-slate-50/50">
                    <td className="py-3.5 px-4 font-mono text-xs text-slate-600">
                      {item.ratingDate || 'N/A'}
                    </td>
                    <td className="py-3.5 px-4 font-black text-slate-900">
                      {item.score?.toFixed(1)} / 100
                    </td>
                    <td className="py-3.5 px-4">
                      <Badge variant={item.rating?.name || item.rating}>{item.rating?.displayName || item.rating}</Badge>
                    </td>
                    <td className="py-3.5 px-4">
                      <span className="text-xs font-semibold text-slate-700">
                        {item.performanceStatus?.replace(/_/g, ' ') || 'ACTIVE'}
                      </span>
                    </td>
                    <td className="py-3.5 px-4 text-right">
                      {item.evaluationId && (
                        <Link to={`/supplier-portal/evaluations/${item.evaluationId}`}>
                          <Button size="xs" variant="secondary">
                            View Scorecard
                          </Button>
                        </Link>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  );
};

export default SupplierPerformance;
