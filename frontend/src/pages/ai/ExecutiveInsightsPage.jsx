import React, { useState, useEffect } from 'react';
import { aiService } from '../../services/ai.service';
import { Card } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Toast } from '../../components/common/Toast';
import {
  Sparkles,
  ShieldAlert,
  TrendingUp,
  AlertTriangle,
  Clock,
  CheckCircle,
  FileText,
  RefreshCw,
  Printer,
  Award,
  ChevronRight,
  HelpCircle,
  BarChart2
} from 'lucide-react';
import { Link } from 'react-router-dom';

export const ExecutiveInsightsPage = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [toast, setToast] = useState({ message: '', type: 'success' });

  useEffect(() => {
    loadInsights();
  }, []);

  const loadInsights = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await aiService.getExecutiveAiInsights();
      if (res.success) {
        setData(res.data);
      }
    } catch (err) {
      console.error(err);
      setError('Failed to load Executive AI insights');
    } finally {
      setLoading(false);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  if (loading) {
    return (
      <div className="py-24 text-center text-slate-500 space-y-3">
        <div className="inline-block animate-spin rounded-full h-10 w-10 border-b-2 border-purple-600"></div>
        <p className="text-sm font-medium animate-pulse">Generating Executive AI Decision Support Insights...</p>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="text-center py-16">
        <p className="text-slate-600 font-medium">{error || 'Insufficient executive data available.'}</p>
        <Button variant="secondary" onClick={loadInsights} className="mt-4" icon={RefreshCw}>
          Retry
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-6 max-w-6xl mx-auto print:max-w-none print:m-0">
      {toast?.message && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast({ message: '', type: 'success' })}
        />
      )}

      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 print:hidden">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <span className="px-2.5 py-0.5 rounded-md bg-purple-100 text-purple-700 font-bold text-xs flex items-center gap-1.5 shadow-xs">
              <Sparkles className="h-3.5 w-3.5" /> C-SUITE SYNTHESIS
            </span>
            <span className="text-xs bg-slate-100 text-slate-600 font-medium px-2 py-0.5 rounded">
              Generated {new Date(data.generatedAt).toLocaleDateString()}
            </span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">
            Executive AI Decision Support
          </h1>
          <p className="text-sm text-slate-500">
            Intelligent synthesis of supplier performance, risk distribution, and strategic procurement action items.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Button variant="secondary" icon={Printer} onClick={handlePrint}>
            Export Executive Briefing
          </Button>
          <Button variant="outline" icon={RefreshCw} onClick={loadInsights}>
            Refresh Analysis
          </Button>
        </div>
      </div>

      {/* Printable Briefing Header (Hidden on screen) */}
      <div className="hidden print:block border-b border-slate-200 pb-4 mb-4">
        <h1 className="text-2xl font-bold text-slate-900">SPRS Executive Decision Briefing</h1>
        <p className="text-xs text-slate-500">Generated on {new Date(data.generatedAt).toLocaleString()}</p>
      </div>

      {/* Top KPI Summary Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-4 bg-white rounded-2xl border border-slate-200/80 shadow-2xs space-y-1">
          <div className="flex items-center justify-between text-slate-500">
            <span className="text-xs font-semibold uppercase tracking-wider">Portfolio Health</span>
            <Award className="h-4 w-4 text-blue-600" />
          </div>
          <div className="text-2xl font-extrabold text-slate-900">
            {data.portfolioAverageScore}%
          </div>
          <span className="text-[11px] text-slate-500">Across {data.totalSuppliersEvaluated} active vendors</span>
        </div>

        <div className="p-4 bg-white rounded-2xl border border-slate-200/80 shadow-2xs space-y-1">
          <div className="flex items-center justify-between text-slate-500">
            <span className="text-xs font-semibold uppercase tracking-wider">High Risk Vendors</span>
            <ShieldAlert className="h-4 w-4 text-rose-600" />
          </div>
          <div className="text-2xl font-extrabold text-rose-600">
            {data.highRiskSuppliersCount}
          </div>
          <span className="text-[11px] text-slate-500">{data.criticalAlertsCount} active early warnings</span>
        </div>

        <div className="p-4 bg-white rounded-2xl border border-slate-200/80 shadow-2xs space-y-1">
          <div className="flex items-center justify-between text-slate-500">
            <span className="text-xs font-semibold uppercase tracking-wider">Overdue Workflows</span>
            <Clock className="h-4 w-4 text-amber-600" />
          </div>
          <div className="text-2xl font-extrabold text-amber-600">
            {data.overdueWorkflowsCount}
          </div>
          <span className="text-[11px] text-slate-500">Awaiting executive sign-off</span>
        </div>

        <div className="p-4 bg-white rounded-2xl border border-slate-200/80 shadow-2xs space-y-1">
          <div className="flex items-center justify-between text-slate-500">
            <span className="text-xs font-semibold uppercase tracking-wider">Active CAP Plans</span>
            <CheckCircle className="h-4 w-4 text-emerald-600" />
          </div>
          <div className="text-2xl font-extrabold text-slate-900">
            {data.openImprovementActionsCount}
          </div>
          <span className="text-[11px] text-slate-500">Remediation action plans</span>
        </div>
      </div>

      {/* AI Synthesis Executive Callout */}
      <div className="p-5 sm:p-6 bg-gradient-to-r from-purple-900 via-indigo-900 to-slate-900 text-white rounded-2xl shadow-sm space-y-3">
        <div className="flex items-center gap-2 text-purple-200 font-bold text-xs uppercase tracking-wider">
          <Sparkles className="h-4 w-4 text-purple-300" /> Executive AI Portfolio Narrative
        </div>
        <p className="text-sm sm:text-base leading-relaxed text-slate-100 font-medium">
          {data.executiveSummary}
        </p>
        <div className="pt-2 flex items-center gap-3 text-xs text-purple-200">
          <span>• Grounded in 100% verified audit data</span>
          <span>• Non-destructive advisory insights</span>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Performance Highlights */}
        <Card title="Performance Highlights & Strengths" subtitle="Positive operational metrics and category growth">
          <div className="space-y-3">
            {data.performanceHighlights?.map((item, idx) => (
              <div key={idx} className="p-3 bg-emerald-50/60 rounded-xl border border-emerald-100 flex items-start gap-2.5">
                <CheckCircle className="h-4 w-4 text-emerald-600 mt-0.5 flex-shrink-0" />
                <span className="text-xs font-medium text-emerald-950 leading-relaxed">{item}</span>
              </div>
            ))}
          </div>
        </Card>

        {/* Key Risks & Vulnerabilities */}
        <Card title="Key Operational Risks" subtitle="Suppliers breaching tolerance thresholds">
          <div className="space-y-3">
            {data.keyRisks?.map((item, idx) => (
              <div key={idx} className="p-3 bg-rose-50/60 rounded-xl border border-rose-100 flex items-start gap-2.5">
                <AlertTriangle className="h-4 w-4 text-rose-600 mt-0.5 flex-shrink-0" />
                <span className="text-xs font-medium text-rose-950 leading-relaxed">{item}</span>
              </div>
            ))}
          </div>
        </Card>
      </div>

      {/* Strategic AI Recommendations with Priority */}
      <Card
        title="Prioritized Strategic Recommendations"
        subtitle="AI suggested governance actions requiring human decision approval"
      >
        <div className="space-y-4">
          {data.strategicRecommendations?.map((rec, idx) => (
            <div
              key={idx}
              className="p-4 sm:p-5 bg-slate-50/80 rounded-xl border border-slate-200/90 space-y-2.5 hover:border-slate-300 transition-colors"
            >
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                <div className="flex items-center gap-2">
                  <span
                    className={`text-[10px] font-bold px-2 py-0.5 rounded uppercase tracking-wider ${
                      rec.priority === 'CRITICAL' || rec.priority === 'HIGH'
                        ? 'bg-rose-100 text-rose-700'
                        : 'bg-amber-100 text-amber-700'
                    }`}
                  >
                    {rec.priority} Priority
                  </span>
                  <h4 className="font-bold text-slate-800 text-sm">{rec.title}</h4>
                </div>
              </div>

              <p className="text-xs text-slate-600 leading-relaxed">
                <strong className="text-slate-700">Rationale:</strong> {rec.rationale}
              </p>

              <div className="p-3 bg-white rounded-lg border border-slate-200/80 text-xs font-medium text-slate-800 flex items-start gap-2">
                <CheckCircle className="h-4 w-4 text-blue-600 mt-0.5 flex-shrink-0" />
                <span><strong className="text-blue-700">Suggested Action:</strong> {rec.suggestedAction}</span>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
};
export default ExecutiveInsightsPage;
