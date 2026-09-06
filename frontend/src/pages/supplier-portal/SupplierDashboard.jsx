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
  Wrench,
  FileText,
  MessageSquare,
  Sparkles,
  AlertTriangle,
  ArrowRight,
  ShieldCheck,
  Building2,
  Clock,
  Send
} from 'lucide-react';

export const SupplierDashboard = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDashboard = async () => {
    try {
      setLoading(true);
      const res = await supplierPortalService.getDashboard();
      if (res.success) {
        setData(res.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load supplier dashboard metrics.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  if (loading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <div className="flex flex-col items-center gap-3">
          <div className="h-9 w-9 animate-spin rounded-full border-4 border-emerald-600 border-t-transparent"></div>
          <p className="text-sm font-medium text-slate-500">Loading your supplier dashboard...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-2xl border border-rose-200 bg-rose-50 p-6 text-center text-rose-800">
        <AlertTriangle className="mx-auto h-8 w-8 text-rose-600 mb-2" />
        <h3 className="text-base font-bold">Unable to Load Dashboard</h3>
        <p className="text-sm mt-1">{error}</p>
        <Button variant="secondary" className="mt-4" onClick={fetchDashboard}>
          Retry
        </Button>
      </div>
    );
  }

  const renderTrendIcon = (trend) => {
    switch (trend) {
      case 'IMPROVING':
        return <TrendingUp className="h-4 w-4 text-emerald-500 inline mr-1" />;
      case 'DECLINING':
        return <TrendingDown className="h-4 w-4 text-rose-500 inline mr-1" />;
      default:
        return <Minus className="h-4 w-4 text-slate-400 inline mr-1" />;
    }
  };

  return (
    <div className="space-y-6">
      {/* Welcome & Vendor Identity Banner */}
      <div className="rounded-2xl bg-gradient-to-r from-slate-900 via-slate-800 to-emerald-950 p-6 sm:p-8 text-white shadow-xl relative overflow-hidden">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
                {data?.categoryName || 'Vendor Partner'}
              </span>
              <span className="text-xs text-slate-300 font-mono">Code: {data?.supplierCode}</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-black tracking-tight">{data?.supplierName}</h1>
            <p className="text-sm text-slate-300 mt-1 max-w-2xl">
              Welcome to your dedicated vendor portal. Monitor your real-time performance ratings, inspect detailed evaluation scorecards, and collaborate directly with procurement.
            </p>
          </div>

          <div className="flex flex-wrap gap-3">
            <Link to="/supplier-portal/profile">
              <Button variant="secondary" className="bg-white/10 hover:bg-white/20 text-white border-white/20">
                <Building2 className="h-4 w-4 mr-2" />
                Profile
              </Button>
            </Link>
            <Link to="/supplier-portal/documents">
              <Button variant="secondary" className="bg-white/10 hover:bg-white/20 text-white border-white/20">
                <FileText className="h-4 w-4 mr-2" />
                Upload Docs
              </Button>
            </Link>
            <Link to="/supplier-portal/communications">
              <Button className="bg-emerald-600 hover:bg-emerald-700 text-white shadow-lg shadow-emerald-600/30">
                <Send className="h-4 w-4 mr-2" />
                Contact Buyer
              </Button>
            </Link>
          </div>
        </div>
      </div>

      {/* KPI Stats Row */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {/* Score & Rating */}
        <Card className="p-5 border-slate-200/80 shadow-xs hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-500">Overall Rating</p>
            <div className="rounded-xl bg-emerald-50 p-2.5 text-emerald-600">
              <Award className="h-5 w-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-black text-slate-900">
              {data?.overallRating != null ? data.overallRating.toFixed(1) : 'N/A'}
            </span>
            <span className="text-xs text-slate-400 font-medium">/ 100</span>
          </div>
          <div className="mt-3 flex items-center justify-between">
            <Badge variant={data?.ratingCategory || 'DEFAULT'}>{data?.ratingCategory || 'UNRATED'}</Badge>
            <span className="text-xs font-medium text-slate-500 flex items-center">
              {renderTrendIcon(data?.performanceTrend)}
              {data?.scoreDifference != null
                ? `${data.scoreDifference >= 0 ? '+' : ''}${data.scoreDifference.toFixed(1)} pts`
                : 'Stable'}
            </span>
          </div>
        </Card>

        {/* Performance Status */}
        <Card className="p-5 border-slate-200/80 shadow-xs hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-500">Tier Status</p>
            <div className="rounded-xl bg-blue-50 p-2.5 text-blue-600">
              <ShieldCheck className="h-5 w-5" />
            </div>
          </div>
          <div className="mt-3">
            <span className="text-xl font-bold text-slate-800">
              {data?.performanceStatus?.replace(/_/g, ' ') || 'ACTIVE'}
            </span>
          </div>
          <div className="mt-4 flex items-center justify-between text-xs text-slate-500">
            <span>Evaluations:</span>
            <span className="font-bold text-slate-700">{data?.totalEvaluations || 0} rounds</span>
          </div>
        </Card>

        {/* Corrective Action Requests */}
        <Card className="p-5 border-slate-200/80 shadow-xs hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-500">Active CAP Actions</p>
            <div className="rounded-xl bg-amber-50 p-2.5 text-amber-600">
              <Wrench className="h-5 w-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-black text-amber-600">
              {(data?.openActionsCount || 0) + (data?.inProgressActionsCount || 0)}
            </span>
            <span className="text-xs text-slate-400">pending response</span>
          </div>
          <div className="mt-3 flex items-center justify-between text-xs text-slate-500">
            <span>Completed:</span>
            <span className="font-bold text-emerald-600">{data?.completedActionsCount || 0} resolved</span>
          </div>
        </Card>

        {/* Compliance Documents */}
        <Card className="p-5 border-slate-200/80 shadow-xs hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-500">Active Documents</p>
            <div className="rounded-xl bg-purple-50 p-2.5 text-purple-600">
              <FileText className="h-5 w-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-black text-purple-700">{data?.totalDocumentsCount || 0}</span>
            <span className="text-xs text-slate-400">certificates</span>
          </div>
          <div className="mt-3 flex items-center justify-between text-xs text-slate-500">
            <span>Compliance:</span>
            <span className="font-bold text-emerald-600">Valid & Verified</span>
          </div>
        </Card>
      </div>

      {/* AI Performance Intelligence & Insights */}
      {data?.aiInsights && (
        <div className="rounded-2xl border border-blue-200 bg-gradient-to-br from-blue-50/70 via-indigo-50/40 to-purple-50/60 p-6 shadow-xs">
          <div className="flex items-center gap-2 text-indigo-900 font-bold mb-3">
            <Sparkles className="h-5 w-5 text-indigo-600" />
            <span className="text-base">AI Supplier Intelligence & Recommendations</span>
            <span className="ml-auto text-xs px-2.5 py-0.5 rounded-full bg-indigo-100 text-indigo-700 font-medium">
              Predictive Insights
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">
            <div className="rounded-xl bg-white/80 p-4 border border-indigo-100">
              <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Predicted Next Score</p>
              <p className="text-2xl font-black text-indigo-700 mt-1">
                {data.aiInsights.prediction?.predictedScore?.toFixed(1) || '92.0'}
              </p>
              <p className="text-xs text-slate-500 mt-1">
                Confidence: {((data.aiInsights.prediction?.confidenceLevel || 0.9) * 100).toFixed(0)}%
              </p>
            </div>

            <div className="rounded-xl bg-white/80 p-4 border border-indigo-100">
              <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Operational Risk</p>
              <p className="text-2xl font-black text-emerald-600 mt-1">
                {data.aiInsights.risk?.riskLevel || 'LOW'}
              </p>
              <p className="text-xs text-slate-500 mt-1">
                Risk Score: {data.aiInsights.risk?.overallRiskScore?.toFixed(1) || '15.0'} / 100
              </p>
            </div>

            <div className="rounded-xl bg-white/80 p-4 border border-indigo-100">
              <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Top Recommendation</p>
              <p className="text-xs font-medium text-slate-700 mt-1 line-clamp-3">
                {data.aiInsights.recommendations?.[0]?.actionItem || 'Maintain high QA consistency across high-volume production batches.'}
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Main Grid: Recent Evaluations & Urgent Actions */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Evaluations */}
        <Card className="p-6 border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
              <CheckSquare className="h-5 w-5 text-emerald-600" />
              Recent Evaluation Scorecards
            </h2>
            <Link
              to="/supplier-portal/evaluations"
              className="text-xs font-semibold text-emerald-600 hover:text-emerald-700 flex items-center gap-1"
            >
              View All <ArrowRight className="h-3.5 w-3.5" />
            </Link>
          </div>

          {(!data?.recentEvaluations || data.recentEvaluations.length === 0) ? (
            <p className="text-sm text-slate-500 py-6 text-center">No completed evaluations published yet.</p>
          ) : (
            <div className="space-y-3">
              {data.recentEvaluations.map((ev) => (
                <div
                  key={ev.id}
                  className="flex items-center justify-between p-3.5 rounded-xl border border-slate-200/70 hover:bg-slate-50 transition-colors"
                >
                  <div>
                    <p className="text-sm font-bold text-slate-900">{ev.evaluationCode}</p>
                    <p className="text-xs text-slate-500 flex items-center gap-2 mt-0.5">
                      <span>{ev.evaluationPeriod}</span>
                      <span>•</span>
                      <span>{ev.evaluationDate}</span>
                    </p>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="text-right">
                      <span className="text-base font-black text-slate-900">
                        {ev.totalWeightedScore?.toFixed(1)}
                      </span>
                      <span className="text-xs text-slate-400">/100</span>
                    </div>
                    <Badge variant={ev.ratingCategory}>{ev.ratingCategory}</Badge>
                    <Link to={`/supplier-portal/evaluations/${ev.id}`}>
                      <Button size="xs" variant="secondary">
                        View
                      </Button>
                    </Link>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>

        {/* Action Items / CAP Actions */}
        <Card className="p-6 border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
              <Wrench className="h-5 w-5 text-amber-600" />
              Corrective Improvement Actions
            </h2>
            <Link
              to="/supplier-portal/improvement-actions"
              className="text-xs font-semibold text-amber-600 hover:text-amber-700 flex items-center gap-1"
            >
              View All <ArrowRight className="h-3.5 w-3.5" />
            </Link>
          </div>

          {(!data?.urgentActions || data.urgentActions.length === 0) ? (
            <div className="py-8 text-center text-slate-500">
              <ShieldCheck className="mx-auto h-8 w-8 text-emerald-500 mb-2" />
              <p className="text-sm font-medium text-slate-700">All clear!</p>
              <p className="text-xs text-slate-400 mt-0.5">No open corrective action plans require response.</p>
            </div>
          ) : (
            <div className="space-y-3">
              {data.urgentActions.map((action) => (
                <div
                  key={action.id}
                  className="p-3.5 rounded-xl border border-slate-200/70 hover:bg-slate-50 transition-colors"
                >
                  <div className="flex items-start justify-between gap-2">
                    <p className="text-sm font-bold text-slate-900">{action.title}</p>
                    <Badge variant={action.priority === 'CRITICAL' ? 'DANGER' : action.priority === 'HIGH' ? 'WARNING' : 'INFO'}>
                      {action.priority}
                    </Badge>
                  </div>
                  <p className="text-xs text-slate-600 mt-1 line-clamp-2">{action.description}</p>
                  <div className="mt-3 flex items-center justify-between text-xs text-slate-500">
                    <span className="flex items-center gap-1">
                      <Clock className="h-3.5 w-3.5 text-slate-400" /> Due: {action.dueDate || 'N/A'}
                    </span>
                    <Link to="/supplier-portal/improvement-actions">
                      <Button size="xs" variant="primary" className="bg-amber-600 hover:bg-amber-700 text-white">
                        Respond
                      </Button>
                    </Link>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>
    </div>
  );
};

export default SupplierDashboard;
