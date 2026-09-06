import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { aiService } from '../../services/ai.service';
import { Card } from '../../components/common/Card';
import { Badge } from '../../components/common/Badge';
import { Button } from '../../components/common/Button';
import {
  Sparkles,
  ShieldAlert,
  TrendingDown,
  TrendingUp,
  Minus,
  AlertTriangle,
  Info,
  CheckCircle2,
  ArrowRight,
  RefreshCw,
  Eye,
  Activity,
  Lightbulb,
  Bell
} from 'lucide-react';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  Title
} from 'chart.js';
import { Doughnut, Bar } from 'react-chartjs-2';

ChartJS.register(
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  Title
);

export const AiIntelligencePage = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('watchlist'); // watchlist | alerts | recommendations

  const fetchAiDashboard = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await aiService.getAiDashboardSummary();
      if (res.success) {
        setData(res.data);
      }
    } catch (err) {
      console.error('Failed to load AI summary', err);
      setError('Failed to load AI intelligence analytics.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAiDashboard();
  }, []);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mb-4"></div>
        <p className="text-gray-500 font-medium">Running statistical models & risk intelligence engines...</p>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="p-8 text-center bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <AlertTriangle className="h-12 w-12 text-amber-500 mx-auto mb-4" />
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">Error Loading AI Insights</h3>
        <p className="text-gray-500 dark:text-gray-400 mb-6">{error || 'Unable to retrieve AI intelligence summary.'}</p>
        <Button onClick={fetchAiDashboard}>
          <RefreshCw className="h-4 w-4 mr-2" /> Retry
        </Button>
      </div>
    );
  }

  const getRiskBadgeVariant = (level) => {
    switch (level) {
      case 'CRITICAL': return 'danger';
      case 'HIGH': return 'danger';
      case 'MEDIUM': return 'warning';
      case 'LOW': return 'success';
      default: return 'default';
    }
  };

  const getSeverityBadgeVariant = (severity) => {
    switch (severity) {
      case 'CRITICAL': return 'danger';
      case 'HIGH': return 'danger';
      case 'WARNING': return 'warning';
      case 'INFO': return 'info';
      default: return 'default';
    }
  };

  const getTrendIcon = (trend) => {
    switch (trend) {
      case 'IMPROVING':
        return <span className="inline-flex items-center text-emerald-600 font-semibold text-xs"><TrendingUp className="h-3.5 w-3.5 mr-1" /> Improving</span>;
      case 'DECLINING':
        return <span className="inline-flex items-center text-rose-600 font-semibold text-xs"><TrendingDown className="h-3.5 w-3.5 mr-1" /> Declining</span>;
      case 'STABLE':
        return <span className="inline-flex items-center text-blue-600 font-semibold text-xs"><Minus className="h-3.5 w-3.5 mr-1" /> Stable</span>;
      default:
        return <span className="inline-flex items-center text-gray-400 text-xs"><Info className="h-3.5 w-3.5 mr-1" /> Insufficient Data</span>;
    }
  };

  const doughnutData = {
    labels: ['Low Risk', 'Medium Risk', 'High Risk', 'Critical Risk'],
    datasets: [
      {
        data: [
          data.lowRiskCount || 0,
          data.mediumRiskCount || 0,
          data.highRiskCount || 0,
          data.criticalRiskCount || 0
        ],
        backgroundColor: ['#10b981', '#f59e0b', '#f97316', '#ef4444'],
        borderWidth: 2,
        borderColor: '#ffffff'
      }
    ]
  };

  const topRiskNames = (data.topRiskSuppliers || []).slice(0, 5).map(s => s.supplierName);
  const topRiskScores = (data.topRiskSuppliers || []).slice(0, 5).map(s => s.riskScore);

  const riskBarData = {
    labels: topRiskNames.length > 0 ? topRiskNames : ['No Risk Data'],
    datasets: [
      {
        label: 'Risk Score (0 - 100)',
        data: topRiskScores.length > 0 ? topRiskScores : [0],
        backgroundColor: '#f97316',
        borderRadius: 6
      }
    ]
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <div className="p-2 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-lg text-white shadow-sm">
              <Sparkles className="h-6 w-6" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-900 dark:text-white">AI Supplier Intelligence & Predictive Analytics</h1>
              <p className="text-sm text-gray-500 dark:text-gray-400">
                Statistical trajectory forecasting, automated risk scoring, and actionable remediation insights.
              </p>
            </div>
          </div>
        </div>
        <Button variant="secondary" onClick={fetchAiDashboard} className="self-start sm:self-auto">
          <RefreshCw className="h-4 w-4 mr-2" /> Refresh Models
        </Button>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
        <Card className="p-5 border-l-4 border-l-indigo-500">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">Suppliers Analyzed</p>
              <h3 className="text-2xl font-bold text-gray-900 dark:text-white mt-1">{data.totalSuppliersAnalyzed}</h3>
            </div>
            <div className="p-3 bg-indigo-50 dark:bg-indigo-900/30 text-indigo-600 rounded-xl">
              <Activity className="h-5 w-5" />
            </div>
          </div>
          <p className="text-xs text-gray-500 mt-2">100% evaluated baseline</p>
        </Card>

        <Card className="p-5 border-l-4 border-l-rose-500">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">High / Critical Risk</p>
              <h3 className="text-2xl font-bold text-rose-600 mt-1">{data.highRiskCount + data.criticalRiskCount}</h3>
            </div>
            <div className="p-3 bg-rose-50 dark:bg-rose-900/30 text-rose-600 rounded-xl">
              <ShieldAlert className="h-5 w-5" />
            </div>
          </div>
          <p className="text-xs text-rose-500 mt-2">{data.criticalRiskCount} critical priority</p>
        </Card>

        <Card className="p-5 border-l-4 border-l-amber-500">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">Declining Trajectory</p>
              <h3 className="text-2xl font-bold text-amber-600 mt-1">{data.decliningSuppliersCount}</h3>
            </div>
            <div className="p-3 bg-amber-50 dark:bg-amber-900/30 text-amber-600 rounded-xl">
              <TrendingDown className="h-5 w-5" />
            </div>
          </div>
          <p className="text-xs text-amber-600 mt-2">Negative velocity detected</p>
        </Card>

        <Card className="p-5 border-l-4 border-l-purple-500">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">Early Warning Alerts</p>
              <h3 className="text-2xl font-bold text-purple-600 mt-1">{data.activeAlertsCount}</h3>
            </div>
            <div className="p-3 bg-purple-50 dark:bg-purple-900/30 text-purple-600 rounded-xl">
              <Bell className="h-5 w-5" />
            </div>
          </div>
          <p className="text-xs text-purple-500 mt-2">Active triggers</p>
        </Card>

        <Card className="p-5 border-l-4 border-l-emerald-500">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">System Risk Index</p>
              <h3 className="text-2xl font-bold text-emerald-600 mt-1">{data.averageSystemRiskScore} / 100</h3>
            </div>
            <div className="p-3 bg-emerald-50 dark:bg-emerald-900/30 text-emerald-600 rounded-xl">
              <CheckCircle2 className="h-5 w-5" />
            </div>
          </div>
          <p className="text-xs text-emerald-600 mt-2">Weighted average</p>
        </Card>
      </div>

      {/* Visual Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card className="p-5">
          <h3 className="text-base font-semibold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
            <ShieldAlert className="h-5 w-5 text-indigo-600" /> Supplier Risk Distribution
          </h3>
          <div className="h-60 flex items-center justify-center">
            <Doughnut data={doughnutData} options={{ maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }} />
          </div>
        </Card>

        <Card className="p-5 lg:col-span-2">
          <h3 className="text-base font-semibold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
            <Activity className="h-5 w-5 text-orange-500" /> Top Supplier Risk Scoring
          </h3>
          <div className="h-60">
            <Bar
              data={riskBarData}
              options={{
                maintainAspectRatio: false,
                scales: { y: { min: 0, max: 100 } },
                plugins: { legend: { display: false } }
              }}
            />
          </div>
        </Card>
      </div>

      {/* Tabbed Interactive Section */}
      <div className="space-y-4">
        <div className="flex border-b border-gray-200 dark:border-gray-700">
          <button
            onClick={() => setActiveTab('watchlist')}
            className={`pb-3 px-4 text-sm font-semibold border-b-2 flex items-center gap-2 transition-colors ${
              activeTab === 'watchlist'
                ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            <ShieldAlert className="h-4 w-4" /> High-Risk Watchlist ({data.topRiskSuppliers?.length || 0})
          </button>
          <button
            onClick={() => setActiveTab('alerts')}
            className={`pb-3 px-4 text-sm font-semibold border-b-2 flex items-center gap-2 transition-colors ${
              activeTab === 'alerts'
                ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            <Bell className="h-4 w-4" /> Early Warning Alerts ({data.activeAlertsCount || 0})
          </button>
          <button
            onClick={() => setActiveTab('recommendations')}
            className={`pb-3 px-4 text-sm font-semibold border-b-2 flex items-center gap-2 transition-colors ${
              activeTab === 'recommendations'
                ? 'border-indigo-600 text-indigo-600 dark:text-indigo-400'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            <Lightbulb className="h-4 w-4" /> Priority Recommendations ({data.topRecommendations?.length || 0})
          </button>
        </div>

        {/* Tab 1: Watchlist */}
        {activeTab === 'watchlist' && (
          <Card className="overflow-hidden">
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
                <thead className="bg-gray-50 dark:bg-gray-800">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Supplier</th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Current Score</th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Trajectory</th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Risk Level</th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Risk Score</th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Primary Risk Factor</th>
                    <th className="px-6 py-3 text-right text-xs font-semibold text-gray-500 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody className="bg-white dark:bg-gray-900 divide-y divide-gray-200 dark:divide-gray-800 text-sm">
                  {(data.topRiskSuppliers || []).map((s) => (
                    <tr key={s.supplierId} className="hover:bg-gray-50 dark:hover:bg-gray-800/50">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="font-medium text-gray-900 dark:text-white">{s.supplierName}</div>
                        <div className="text-xs text-gray-500">{s.supplierCode}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className="font-semibold text-gray-900 dark:text-white">{s.currentScore}%</span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {getTrendIcon(s.trend)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <Badge variant={getRiskBadgeVariant(s.riskLevel)}>{s.riskLevel}</Badge>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center gap-2">
                          <div className="w-16 bg-gray-200 dark:bg-gray-700 h-2 rounded-full overflow-hidden">
                            <div
                              className={`h-full ${s.riskScore >= 75 ? 'bg-red-500' : s.riskScore >= 50 ? 'bg-orange-500' : s.riskScore >= 25 ? 'bg-amber-500' : 'bg-emerald-500'}`}
                              style={{ width: `${Math.min(100, s.riskScore)}%` }}
                            />
                          </div>
                          <span className="font-semibold text-xs">{s.riskScore}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-xs text-gray-500 max-w-xs truncate">
                        {s.riskFactors && s.riskFactors.length > 0 ? s.riskFactors[0] : 'Normal operations'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right">
                        <Link to={`/suppliers/${s.supplierId}`}>
                          <Button size="sm" variant="secondary">
                            <Eye className="h-3.5 w-3.5 mr-1" /> View AI Details
                          </Button>
                        </Link>
                      </td>
                    </tr>
                  ))}
                  {(!data.topRiskSuppliers || data.topRiskSuppliers.length === 0) && (
                    <tr>
                      <td colSpan={7} className="px-6 py-8 text-center text-gray-500">
                        No supplier risk indicators recorded.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </Card>
        )}

        {/* Tab 2: Early Warning Alerts */}
        {activeTab === 'alerts' && (
          <div className="space-y-3">
            {(data.criticalAlerts || []).map((alert) => (
              <Card key={alert.id} className="p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div className="flex items-start gap-3">
                  <div className={`p-2 rounded-lg mt-0.5 ${
                    alert.severity === 'CRITICAL' ? 'bg-red-100 text-red-600 dark:bg-red-900/30' :
                    alert.severity === 'HIGH' ? 'bg-orange-100 text-orange-600 dark:bg-orange-900/30' :
                    'bg-amber-100 text-amber-600 dark:bg-amber-900/30'
                  }`}>
                    <AlertTriangle className="h-5 w-5" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <h4 className="font-semibold text-gray-900 dark:text-white text-sm">{alert.title}</h4>
                      <Badge variant={getSeverityBadgeVariant(alert.severity)} size="sm">{alert.severity}</Badge>
                      <span className="text-xs text-gray-400">Triggered: {alert.triggeredDate}</span>
                    </div>
                    <p className="text-xs text-gray-600 dark:text-gray-300 mt-1">{alert.message}</p>
                    <div className="text-xs font-medium text-indigo-600 dark:text-indigo-400 mt-1">
                      Supplier: {alert.supplierName} ({alert.supplierCode})
                    </div>
                  </div>
                </div>
                <Link to={`/suppliers/${alert.supplierId}`} className="self-end sm:self-center">
                  <Button size="sm" variant="secondary">
                    Investigate <ArrowRight className="h-3.5 w-3.5 ml-1" />
                  </Button>
                </Link>
              </Card>
            ))}
            {(!data.criticalAlerts || data.criticalAlerts.length === 0) && (
              <Card className="p-8 text-center text-gray-500">
                <CheckCircle2 className="h-10 w-10 text-emerald-500 mx-auto mb-2" />
                <p className="font-medium">No critical alerts detected across supplier network.</p>
              </Card>
            )}
          </div>
        )}

        {/* Tab 3: Recommendations */}
        {activeTab === 'recommendations' && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {(data.topRecommendations || []).map((rec) => (
              <Card key={rec.id} className="p-5 flex flex-col justify-between">
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-xs font-bold uppercase tracking-wider text-indigo-600 dark:text-indigo-400">
                      {rec.criterionName}
                    </span>
                    <Badge variant={rec.priority === 'CRITICAL' ? 'danger' : rec.priority === 'HIGH' ? 'warning' : 'info'} size="sm">
                      {rec.priority} Priority
                    </Badge>
                  </div>
                  <h4 className="font-semibold text-gray-900 dark:text-white text-sm mb-1">{rec.title}</h4>
                  <p className="text-xs text-gray-600 dark:text-gray-400 mb-3">{rec.recommendation}</p>
                  <div className="p-3 bg-gray-50 dark:bg-gray-800 rounded-lg border border-gray-100 dark:border-gray-700 text-xs">
                    <span className="font-semibold text-gray-700 dark:text-gray-300 block mb-1">Recommended Action Plan:</span>
                    <span className="text-gray-600 dark:text-gray-400">{rec.actionableSteps}</span>
                  </div>
                </div>
                <div className="mt-4 pt-3 border-t border-gray-100 dark:border-gray-800 flex items-center justify-between">
                  <span className="text-xs text-gray-500">Current Criterion Score: <strong className="text-gray-900 dark:text-white">{rec.percentage}%</strong></span>
                  <Link to={`/suppliers/${rec.supplierId}`}>
                    <Button size="sm" variant="secondary">
                      Apply Plan <ArrowRight className="h-3.5 w-3.5 ml-1" />
                    </Button>
                  </Link>
                </div>
              </Card>
            ))}
            {(!data.topRecommendations || data.topRecommendations.length === 0) && (
              <Card className="col-span-2 p-8 text-center text-gray-500">
                <CheckCircle2 className="h-10 w-10 text-emerald-500 mx-auto mb-2" />
                <p className="font-medium">All supplier criteria currently operate within optimal SLA targets.</p>
              </Card>
            )}
          </div>
        )}
      </div>

      {/* Decision Support Disclaimer */}
      <div className="p-4 bg-indigo-50 dark:bg-indigo-950/40 border border-indigo-100 dark:border-indigo-900 rounded-xl text-xs text-indigo-800 dark:text-indigo-300 flex items-start gap-3">
        <Info className="h-5 w-5 text-indigo-600 shrink-0 mt-0.5" />
        <div>
          <span className="font-semibold block mb-0.5">Enterprise Decision Support Transparency</span>
          Predictions and risk scores are calculated using transparent time-weighted regression and historical criteria scoring variance. This module is intended for operational guidance and decision support alongside human managerial judgment.
        </div>
      </div>
    </div>
  );
};
