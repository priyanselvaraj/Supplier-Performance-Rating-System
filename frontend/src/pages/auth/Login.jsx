import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  ShieldCheck,
  Lock,
  User,
  AlertCircle,
  CheckCircle2,
  ArrowRight,
  Eye,
  EyeOff,
  Check,
  X,
  Sparkles,
  Building2,
  TrendingUp,
  HelpCircle,
  FileText,
  ChevronRight,
  BarChart3,
  ExternalLink,
  PhoneCall,
  Globe2,
  Clock,
  Briefcase,
  Layers,
  Award
} from 'lucide-react';

export const Login = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, user: currentUser } = useAuth();

  const [formData, setFormData] = useState({ username: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [activeTab, setActiveTab] = useState('login'); // 'login' | 'register'

  // Modals state
  const [showAdvisorModal, setShowAdvisorModal] = useState(false);
  const [showBenchmarkModal, setShowBenchmarkModal] = useState(false);
  const [advisorForm, setAdvisorForm] = useState({ name: '', email: '', company: '', message: '' });
  const [advisorSuccess, setAdvisorSuccess] = useState(false);

  const from = location.state?.from?.pathname || '/dashboard';

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (error) setError('');
    if (successMsg) setSuccessMsg('');
  };

  const handleFillDemo = (username, password) => {
    setFormData({ username, password });
    setError('');
    setSuccessMsg('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.username || !formData.password) {
      setError('Please enter both username/email and password');
      return;
    }

    setLoading(true);
    setError('');
    setSuccessMsg('');

    try {
      const authUser = await login(formData);
      const isSupplier = authUser?.roles?.some(
        (r) => r === 'ROLE_SUPPLIER' || r === 'SUPPLIER' || r.toLowerCase() === 'supplier'
      );
      const targetRoute = isSupplier ? '/supplier-portal/dashboard' : from === '/login' ? '/dashboard' : from;

      if (authUser?.emailNotificationSent === false) {
        setSuccessMsg("Login successful. We couldn't send the login notification email right now.");
      } else {
        setSuccessMsg('Login successful! A login notification has been sent to your registered email.');
      }

      setTimeout(() => {
        navigate(targetRoute, { replace: true });
      }, 700);
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Invalid username or password';
      setError(msg);
      setLoading(false);
    }
  };

  const scrollToSection = (id) => {
    const el = document.getElementById(id);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
    }
  };

  const handleAdvisorSubmit = (e) => {
    e.preventDefault();
    setAdvisorSuccess(true);
    setTimeout(() => {
      setAdvisorSuccess(false);
      setShowAdvisorModal(false);
      setAdvisorForm({ name: '', email: '', company: '', message: '' });
    }, 1800);
  };

  return (
    <div className="min-h-screen bg-[#fbfaf6] text-stone-900 font-['Inter',sans-serif] selection:bg-emerald-200 selection:text-emerald-900">
      {/* 1. TOP ANNOUNCEMENT BANNER */}
      <div className="bg-[#13281c] text-[#e3e8e4] text-xs py-2.5 px-4 text-center border-b border-emerald-900/30">
        <div className="max-w-7xl mx-auto flex items-center justify-center gap-2 font-mono-tag tracking-wider flex-wrap">
          <span className="text-emerald-300 font-semibold uppercase tracking-wider">
            2026 GLOBAL PRODUCTION &amp; SUPPLIER ECOSYSTEM BENCHMARK REPORT
          </span>
          <button
            type="button"
            onClick={() => setShowBenchmarkModal(true)}
            className="text-amber-400 hover:text-amber-300 underline font-medium flex items-center gap-1 transition-colors ml-1 cursor-pointer"
          >
            EXPLORE KEY FINDINGS <ArrowRight className="h-3 w-3 inline" />
          </button>
        </div>
      </div>

      {/* 2. NAVIGATION BAR */}
      <header className="sticky top-0 z-40 bg-[#fbfaf6]/90 backdrop-blur-md border-b border-stone-200/80">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-20 flex items-center justify-between">
          {/* Brand Logo */}
          <div className="flex items-center gap-3">
            <div className="h-10 w-10 rounded-lg bg-[#12291d] text-white flex items-center justify-center font-bold text-sm tracking-wider font-mono-tag shadow-sm">
              APR
            </div>
            <div>
              <span className="text-lg font-bold text-stone-900 tracking-tight font-serif-display">
                APR Consulting
              </span>
              <p className="text-[9px] font-mono-tag tracking-widest text-stone-500 uppercase -mt-0.5">
                SUPPLIER &amp; PRODUCTION ADVISORY
              </p>
            </div>
          </div>

          {/* Desktop Nav Links */}
          <nav className="hidden md:flex items-center gap-8 text-sm font-medium text-stone-600">
            <button
              type="button"
              onClick={() => scrollToSection('challenges')}
              className="hover:text-stone-900 transition-colors cursor-pointer"
            >
              Challenges
            </button>
            <button
              type="button"
              onClick={() => scrollToSection('services')}
              className="hover:text-stone-900 transition-colors cursor-pointer"
            >
              Advisory Services
            </button>
            <button
              type="button"
              onClick={() => scrollToSection('methodology')}
              className="hover:text-stone-900 transition-colors cursor-pointer"
            >
              Methodology
            </button>
            <button
              type="button"
              onClick={() => scrollToSection('faqs')}
              className="hover:text-stone-900 transition-colors cursor-pointer"
            >
              FAQs
            </button>
          </nav>

          {/* Header Action Buttons */}
          <div className="flex items-center gap-3">
            <button
              type="button"
              onClick={() => scrollToSection('portal-card')}
              className="px-4 py-2 text-xs sm:text-sm font-medium text-stone-700 hover:text-stone-900 transition-colors cursor-pointer"
            >
              Workspace Login
            </button>
            <button
              type="button"
              onClick={() => setShowAdvisorModal(true)}
              className="px-4 sm:px-5 py-2 sm:py-2.5 rounded-full bg-[#12291d] hover:bg-[#193929] text-white text-xs sm:text-sm font-medium transition-all shadow-sm flex items-center gap-1.5 cursor-pointer"
            >
              Talk with an Advisor
            </button>
          </div>
        </div>
      </header>

      {/* 3. HERO SECTION */}
      <section className="relative overflow-hidden pt-8 pb-16 sm:pt-12 sm:pb-24 border-b border-stone-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-8 items-start">
            {/* Left Content Column */}
            <div className="lg:col-span-7 space-y-8 pt-2">
              {/* Category Pill Badge */}
              <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-emerald-50 border border-emerald-200/80 text-[11px] font-mono-tag font-medium text-emerald-800 tracking-wider uppercase">
                <span className="h-1.5 w-1.5 rounded-full bg-emerald-600 animate-pulse"></span>
                <span>MARKETING PRODUCTION CONSULTING &bull; GLOBAL ENTERPRISE ADVISORY</span>
              </div>

              {/* Main Headline (Serif font) */}
              <h1 className="font-serif-display text-4xl sm:text-5xl lg:text-[58px] font-bold leading-[1.12] text-stone-950 tracking-tight">
                Gain control over your content and supplier supply chain.
              </h1>

              {/* Supporting Subtitle */}
              <p className="text-stone-600 text-base sm:text-lg leading-relaxed max-w-2xl">
                Bridge the gap between creative integrity, operational goals, and your external agency ecosystem. We
                provide unbiased, data-driven should-cost modeling, bid management, and supplier intelligence.
              </p>

              {/* Action Buttons */}
              <div className="flex flex-wrap items-center gap-4 pt-2">
                <button
                  type="button"
                  onClick={() => scrollToSection('portal-card')}
                  className="px-6 py-3.5 rounded-xl bg-[#12291d] hover:bg-[#1a3b2b] text-white text-sm font-semibold transition-all shadow-md shadow-emerald-950/20 flex items-center gap-2 cursor-pointer"
                >
                  Access Evaluation Workspace <ArrowRight className="h-4 w-4" />
                </button>
                <button
                  type="button"
                  onClick={() => setShowAdvisorModal(true)}
                  className="px-6 py-3.5 rounded-xl bg-white hover:bg-stone-50 border border-stone-300 text-stone-800 text-sm font-semibold transition-all shadow-sm cursor-pointer"
                >
                  Request Strategy Briefing
                </button>
              </div>

              {/* Stats Metrics Row */}
              <div className="pt-10 border-t border-stone-200/90 grid grid-cols-3 gap-4 sm:gap-8">
                <div>
                  <div className="font-serif-display text-3xl sm:text-4xl font-bold text-stone-900">65+</div>
                  <p className="text-[10px] sm:text-xs font-mono-tag uppercase tracking-wider text-stone-500 mt-1">
                    COUNTRIES WITH ACTIVE PRODUCTION OVERSIGHT
                  </p>
                </div>
                <div>
                  <div className="font-serif-display text-3xl sm:text-4xl font-bold text-stone-900">25+</div>
                  <p className="text-[10px] sm:text-xs font-mono-tag uppercase tracking-wider text-stone-500 mt-1">
                    YEARS OF HISTORICAL COST BENCHMARKS
                  </p>
                </div>
                <div>
                  <div className="font-serif-display text-3xl sm:text-4xl font-bold text-stone-900">18–24%</div>
                  <p className="text-[10px] sm:text-xs font-mono-tag uppercase tracking-wider text-stone-500 mt-1">
                    TRAPPED CAPITAL UNLOCKED &amp; REINVESTED
                  </p>
                </div>
              </div>
            </div>

            {/* Right Column — Enterprise Workspace Portal Card */}
            <div className="lg:col-span-5" id="portal-card">
              <div className="bg-white rounded-3xl p-6 sm:p-8 shadow-2xl shadow-stone-300/40 border border-stone-200/90">
                {/* Header Tag */}
                <div className="mb-4">
                  <span className="inline-block px-3 py-1 rounded-full bg-emerald-50 border border-emerald-200 text-[10px] font-mono-tag font-semibold text-emerald-800 tracking-wider uppercase">
                    ENTERPRISE WORKSPACE PORTAL
                  </span>
                </div>

                {/* Tab Switcher */}
                <div className="flex bg-stone-100 p-1 rounded-xl mb-6 text-xs font-medium text-stone-600">
                  <button
                    type="button"
                    onClick={() => setActiveTab('login')}
                    className={`flex-1 py-2 rounded-lg transition-all cursor-pointer ${
                      activeTab === 'login'
                        ? 'bg-white text-stone-950 font-semibold shadow-sm'
                        : 'text-stone-500 hover:text-stone-900'
                    }`}
                  >
                    Sign in
                  </button>
                  <Link
                    to="/register"
                    className="flex-1 py-2 rounded-lg text-center text-stone-500 hover:text-stone-900 transition-all"
                  >
                    Register Account
                  </Link>
                </div>

                {/* Portal Title & Subtitle */}
                <div className="mb-6">
                  <h2 className="font-serif-display text-2xl sm:text-3xl font-bold text-stone-900 tracking-tight">
                    Welcome back.
                  </h2>
                  <p className="text-xs sm:text-sm text-stone-500 mt-1">
                    Sign in to access your supplier scorecards and evaluation telemetry.
                  </p>
                </div>

                {/* Error Banner */}
                {error && (
                  <div className="mb-5 p-3.5 rounded-xl bg-rose-50 border border-rose-200 flex items-start gap-2.5 text-rose-700 text-xs sm:text-sm">
                    <AlertCircle className="h-4 w-4 flex-shrink-0 text-rose-500 mt-0.5" />
                    <span>{error}</span>
                  </div>
                )}

                {/* Success Banner */}
                {successMsg && (
                  <div className="mb-5 p-3.5 rounded-xl bg-emerald-50 border border-emerald-200 flex items-start gap-2.5 text-emerald-800 text-xs sm:text-sm">
                    <CheckCircle2 className="h-4 w-4 flex-shrink-0 text-emerald-600 mt-0.5" />
                    <span>{successMsg}</span>
                  </div>
                )}

                {/* Login Form */}
                <form className="space-y-4" onSubmit={handleSubmit}>
                  <div>
                    <label className="block text-[11px] font-mono-tag font-semibold text-stone-600 uppercase tracking-wider mb-1.5">
                      CORPORATE EMAIL ADDRESS
                    </label>
                    <input
                      type="text"
                      name="username"
                      value={formData.username}
                      onChange={handleChange}
                      placeholder="e.g. admin, manager, or supplier_apex"
                      required
                      autoFocus
                      className="w-full px-4 py-3 bg-[#eef2f8] border border-stone-200 rounded-xl text-stone-900 text-sm placeholder-stone-400 focus:outline-none focus:ring-2 focus:ring-[#12291d] focus:bg-white transition-all"
                    />
                  </div>

                  <div>
                    <div className="flex items-center justify-between mb-1.5">
                      <label className="block text-[11px] font-mono-tag font-semibold text-stone-600 uppercase tracking-wider">
                        PASSWORD
                      </label>
                      <span className="text-[10px] font-mono-tag text-stone-400 uppercase tracking-wider">REQUIRED</span>
                    </div>
                    <div className="relative">
                      <input
                        type={showPassword ? 'text' : 'password'}
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        placeholder="••••••••••••"
                        required
                        className="w-full px-4 py-3 bg-[#eef2f8] border border-stone-200 rounded-xl text-stone-900 text-sm placeholder-stone-400 focus:outline-none focus:ring-2 focus:ring-[#12291d] focus:bg-white transition-all pr-11"
                      />
                      <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute right-3.5 top-1/2 -translate-y-1/2 text-stone-400 hover:text-stone-600 p-1 cursor-pointer"
                      >
                        {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                      </button>
                    </div>
                  </div>

                  <button
                    type="submit"
                    disabled={loading}
                    className="w-full py-3.5 px-4 rounded-xl bg-[#12291d] hover:bg-[#1a3b2b] text-white text-sm font-semibold transition-all shadow-md shadow-emerald-950/20 flex items-center justify-center gap-2 cursor-pointer disabled:opacity-60"
                  >
                    {loading ? (
                      <div className="h-5 w-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                    ) : (
                      <>
                        Enter workspace <ArrowRight className="h-4 w-4" />
                      </>
                    )}
                  </button>
                </form>

                {/* Need access text */}
                <div className="mt-5 text-center text-xs text-stone-500">
                  Need access?{' '}
                  <Link to="/register" className="font-semibold text-stone-900 hover:underline">
                    Register an evaluator account
                  </Link>
                </div>

                {/* Quick Demo Access Box */}
                <div className="mt-6 p-3.5 rounded-xl bg-stone-50 border border-stone-200">
                  <div className="flex items-center justify-between text-[11px] font-mono-tag text-stone-500 mb-2">
                    <span className="font-bold text-stone-700 uppercase tracking-wider">DEFAULT ACCESS</span>
                    <span className="text-stone-400">1-CLICK LOGIN</span>
                  </div>
                  <div className="grid grid-cols-3 gap-2">
                    <button
                      type="button"
                      onClick={() => handleFillDemo('admin', 'Admin@12345')}
                      className="p-2 text-left bg-white hover:bg-stone-100 rounded-lg border border-stone-200 transition-colors cursor-pointer"
                    >
                      <div className="text-[11px] font-bold text-emerald-800">Admin</div>
                      <div className="text-[9px] text-stone-400 truncate">admin</div>
                    </button>
                    <button
                      type="button"
                      onClick={() => handleFillDemo('manager', 'Manager@12345')}
                      className="p-2 text-left bg-white hover:bg-stone-100 rounded-lg border border-stone-200 transition-colors cursor-pointer"
                    >
                      <div className="text-[11px] font-bold text-indigo-800">Manager</div>
                      <div className="text-[9px] text-stone-400 truncate">manager</div>
                    </button>
                    <button
                      type="button"
                      onClick={() => handleFillDemo('supplier_apex', 'Supplier@12345')}
                      className="p-2 text-left bg-emerald-50/60 hover:bg-emerald-100/60 rounded-lg border border-emerald-200 transition-colors cursor-pointer"
                    >
                      <div className="text-[11px] font-bold text-emerald-700">Supplier</div>
                      <div className="text-[9px] text-emerald-600 truncate">Apex Micro</div>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 4. SECTION 2: KEY CHALLENGES BRANDS FACE (Light Section) */}
      <section id="challenges" className="py-20 bg-[#fbfaf6] border-b border-stone-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="max-w-3xl mb-14">
            <span className="text-xs font-mono-tag font-semibold text-stone-500 uppercase tracking-widest block mb-3">
              CONTENT SUPPLY CHAIN FRICTION
            </span>
            <h2 className="font-serif-display text-3xl sm:text-4xl lg:text-[44px] font-bold text-stone-900 tracking-tight leading-tight">
              Key challenges brands face.
            </h2>
            <p className="text-stone-600 text-base sm:text-lg mt-4 leading-relaxed">
              Working with multiple agency and production partners across channels often leads to inconsistent processes
              and poor financial oversight.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {/* Card 1 */}
            <div className="bg-white p-7 rounded-2xl border border-stone-200/90 shadow-sm flex flex-col justify-between hover:shadow-md transition-shadow">
              <div>
                <span className="text-xs font-mono-tag font-bold text-stone-400 block mb-4">01</span>
                <h3 className="font-serif-display text-xl font-bold text-stone-900 mb-3 leading-snug">
                  Limited Cost Benchmarking
                </h3>
                <p className="text-stone-600 text-sm leading-relaxed">
                  Absence of verified should-cost modeling reduces visibility into fair market rates, making it difficult
                  to assess true production value.
                </p>
              </div>
            </div>

            {/* Card 2 */}
            <div className="bg-white p-7 rounded-2xl border border-stone-200/90 shadow-sm flex flex-col justify-between hover:shadow-md transition-shadow">
              <div>
                <span className="text-xs font-mono-tag font-bold text-stone-400 block mb-4">02</span>
                <h3 className="font-serif-display text-xl font-bold text-stone-900 mb-3 leading-snug">
                  Bidding Spec Inconsistencies
                </h3>
                <p className="text-stone-600 text-sm leading-relaxed">
                  Differences in proposal formats and fee-to-production line items delay speeds-to-market and prevent
                  like-for-like negotiations.
                </p>
              </div>
            </div>

            {/* Card 3 */}
            <div className="bg-white p-7 rounded-2xl border border-stone-200/90 shadow-sm flex flex-col justify-between hover:shadow-md transition-shadow">
              <div>
                <span className="text-xs font-mono-tag font-bold text-stone-400 block mb-4">03</span>
                <h3 className="font-serif-display text-xl font-bold text-stone-900 mb-3 leading-snug">
                  Invoice Mismatches &amp; Drift
                </h3>
                <p className="text-stone-600 text-sm leading-relaxed">
                  Final actuals drift past approved estimates. Cost-plus items often lack proper receipts, causing
                  unbudgeted expenditure.
                </p>
              </div>
            </div>

            {/* Card 4 */}
            <div className="bg-white p-7 rounded-2xl border border-stone-200/90 shadow-sm flex flex-col justify-between hover:shadow-md transition-shadow">
              <div>
                <span className="text-xs font-mono-tag font-bold text-stone-400 block mb-4">04</span>
                <h3 className="font-serif-display text-xl font-bold text-stone-900 mb-3 leading-snug">
                  Siloed Governance Standards
                </h3>
                <p className="text-stone-600 text-sm leading-relaxed">
                  Disjointed criteria across media tiers and regional teams leave procurement leaders without a single
                  source of truth.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 5. SECTION 3: UNBIASED ADVISORY (Dark Forest Slate Section) */}
      <section id="services" className="py-24 bg-[#0e2218] text-stone-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="max-w-3xl mb-16">
            <span className="text-xs font-mono-tag font-semibold text-emerald-400 uppercase tracking-widest block mb-3">
              HOW APR HELPS
            </span>
            <h2 className="font-serif-display text-3xl sm:text-4xl lg:text-[46px] font-bold text-white tracking-tight leading-tight">
              Unbiased advisory across your production lifecycle.
            </h2>
            <p className="text-stone-300 text-base sm:text-lg mt-4 leading-relaxed">
              Gain a data-driven backbone bridging your creative ambitions with operational goals and supplier ecosystems.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {/* Capability 01 */}
            <div className="bg-[#122c1f] p-8 rounded-2xl border border-emerald-800/40 hover:border-emerald-700/60 transition-colors flex flex-col justify-between">
              <div>
                <span className="text-xs font-mono-tag font-semibold text-emerald-400/80 uppercase tracking-wider block mb-4">
                  CAPABILITY / 01
                </span>
                <h3 className="font-serif-display text-xl font-bold text-white mb-3 leading-snug">
                  Should-Cost Analysis for Budget Validation
                </h3>
                <p className="text-stone-300 text-sm leading-relaxed mb-6">
                  Leverage our benchmark data across thousands of global productions to establish clear financial
                  baselines before agency negotiations begin.
                </p>
              </div>
              <div className="pt-4 border-t border-emerald-900/50">
                <p className="text-[11px] font-mono-tag text-stone-400">
                  <strong className="text-emerald-400">KEY OUTCOME:</strong> Pre-bid validation eliminates cost creep
                  before contracts are signed.
                </p>
              </div>
            </div>

            {/* Capability 02 */}
            <div className="bg-[#122c1f] p-8 rounded-2xl border border-emerald-800/40 hover:border-emerald-700/60 transition-colors flex flex-col justify-between">
              <div>
                <span className="text-xs font-mono-tag font-semibold text-emerald-400/80 uppercase tracking-wider block mb-4">
                  CAPABILITY / 02
                </span>
                <h3 className="font-serif-display text-xl font-bold text-white mb-3 leading-snug">
                  Live Action &amp; Post-Production Bid Management
                </h3>
                <p className="text-stone-300 text-sm leading-relaxed mb-6">
                  Receive rigorous bid specification review, like-for-like vendor comparisons, and direct negotiation
                  guidance from veteran producers.
                </p>
              </div>
              <div className="pt-4 border-t border-emerald-900/50">
                <p className="text-[11px] font-mono-tag text-stone-400">
                  <strong className="text-emerald-400">KEY OUTCOME:</strong> Apples-to-apples clarity across Tier-1 and
                  specialized suppliers.
                </p>
              </div>
            </div>

            {/* Capability 03 */}
            <div className="bg-[#122c1f] p-8 rounded-2xl border border-emerald-800/40 hover:border-emerald-700/60 transition-colors flex flex-col justify-between">
              <div>
                <span className="text-xs font-mono-tag font-semibold text-emerald-400/80 uppercase tracking-wider block mb-4">
                  CAPABILITY / 03
                </span>
                <h3 className="font-serif-display text-xl font-bold text-white mb-3 leading-snug">
                  Agency Fee Analysis to Ensure Transparency
                </h3>
                <p className="text-stone-300 text-sm leading-relaxed mb-6">
                  Evaluate staffing assumptions, rate alignment, and fee-to-production relationships across your holding
                  company or in-house studio model.
                </p>
              </div>
              <div className="pt-4 border-t border-emerald-900/50">
                <p className="text-[11px] font-mono-tag text-stone-400">
                  <strong className="text-emerald-400">KEY OUTCOME:</strong> Unpack hidden agency markups and ensure
                  equitable partner remuneration.
                </p>
              </div>
            </div>

            {/* Capability 04 */}
            <div className="bg-[#122c1f] p-8 rounded-2xl border border-emerald-800/40 hover:border-emerald-700/60 transition-colors flex flex-col justify-between">
              <div>
                <span className="text-xs font-mono-tag font-semibold text-emerald-400/80 uppercase tracking-wider block mb-4">
                  CAPABILITY / 04
                </span>
                <h3 className="font-serif-display text-xl font-bold text-white mb-3 leading-snug">
                  Invoice Reconciliation &amp; 90-Day Actualization
                </h3>
                <p className="text-stone-300 text-sm leading-relaxed mb-6">
                  Execute end-to-end financial stewardship by systematically matching final vendor actuals against
                  approved POs and auditing third-party costs.
                </p>
              </div>
              <div className="pt-4 border-t border-emerald-900/50">
                <p className="text-[11px] font-mono-tag text-stone-400">
                  <strong className="text-emerald-400">KEY OUTCOME:</strong> Enforce strict billing milestones with
                  complete audit defense.
                </p>
              </div>
            </div>

            {/* Capability 05 */}
            <div className="bg-[#122c1f] p-8 rounded-2xl border border-emerald-800/40 hover:border-emerald-700/60 transition-colors flex flex-col justify-between">
              <div>
                <span className="text-xs font-mono-tag font-semibold text-emerald-400/80 uppercase tracking-wider block mb-4">
                  CAPABILITY / 05
                </span>
                <h3 className="font-serif-display text-xl font-bold text-white mb-3 leading-snug">
                  Production Decoupling &amp; Workflow Optimization
                </h3>
                <p className="text-stone-300 text-sm leading-relaxed mb-6">
                  Separate creative ideation from execution to build a more agile, targeted content pipeline utilizing
                  diverse vendor tiers and production hubs.
                </p>
              </div>
              <div className="pt-4 border-t border-emerald-900/50">
                <p className="text-[11px] font-mono-tag text-stone-400">
                  <strong className="text-emerald-400">KEY OUTCOME:</strong> Higher throughput and faster turnarounds
                  without quality loss.
                </p>
              </div>
            </div>

            {/* Capability 06 */}
            <div className="bg-[#122c1f] p-8 rounded-2xl border border-emerald-800/40 hover:border-emerald-700/60 transition-colors flex flex-col justify-between">
              <div>
                <span className="text-xs font-mono-tag font-semibold text-emerald-400/80 uppercase tracking-wider block mb-4">
                  CAPABILITY / 06
                </span>
                <h3 className="font-serif-display text-xl font-bold text-white mb-3 leading-snug">
                  Strategic Executive Producer Oversight
                </h3>
                <p className="text-stone-300 text-sm leading-relaxed mb-6">
                  Tap into a global hive of production wisdom across 65 countries. Receive high-level guidance throughout
                  the lifecycle, from brief to final wrap.
                </p>
              </div>
              <div className="pt-4 border-t border-emerald-900/50">
                <p className="text-[11px] font-mono-tag text-stone-400">
                  <strong className="text-emerald-400">KEY OUTCOME:</strong> Senior creative champions safeguard
                  creative integrity and ROI.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 6. SECTION 4: GOVERNANCE, NOT INTERFERENCE (Methodology) */}
      <section id="methodology" className="py-24 bg-[#fbfaf6] border-b border-stone-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto mb-16">
            <span className="text-xs font-mono-tag font-semibold text-stone-500 uppercase tracking-widest block mb-3">
              A PROVEN METHOD FOR ECOSYSTEM OPTIMIZATION
            </span>
            <h2 className="font-serif-display text-3xl sm:text-4xl lg:text-[46px] font-bold text-stone-900 tracking-tight">
              Governance, Not Interference.
            </h2>
            <p className="text-stone-600 text-base sm:text-lg mt-4 leading-relaxed">
              Partnering with an advisory team does not disrupt your creative relationships. We act as an objective,
              seamless extension of your internal team.
            </p>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 items-stretch">
            {/* Column 1: Your Brand */}
            <div className="bg-white p-8 rounded-3xl border border-stone-200/90 shadow-sm flex flex-col justify-between">
              <div>
                <span className="text-xs font-mono-tag font-bold text-stone-400 uppercase tracking-wider block mb-2">
                  01 &bull; STRATEGY
                </span>
                <h3 className="font-serif-display text-2xl font-bold text-stone-900 mb-6">Your Brand</h3>

                <ul className="space-y-4 text-stone-600 text-sm">
                  <li className="flex items-start gap-3">
                    <Check className="h-5 w-5 text-emerald-600 flex-shrink-0 mt-0.5" />
                    <span>Maintains direct creative ownership and agency relationships</span>
                  </li>
                  <li className="flex items-start gap-3">
                    <Check className="h-5 w-5 text-emerald-600 flex-shrink-0 mt-0.5" />
                    <span>Defines strategic campaign milestones and budget parameters</span>
                  </li>
                  <li className="flex items-start gap-3">
                    <Check className="h-5 w-5 text-emerald-600 flex-shrink-0 mt-0.5" />
                    <span>Exercises final approval on all creative and commercial decisions</span>
                  </li>
                </ul>
              </div>
            </div>

            {/* Column 2: APR Advisory (Highlighted Center Card) */}
            <div className="bg-white p-8 rounded-3xl border-2 border-[#12291d] shadow-xl shadow-stone-300/40 relative flex flex-col justify-between">
              <div className="absolute -top-3.5 left-1/2 -translate-x-1/2 px-4 py-1 rounded-full bg-[#12291d] text-white text-[10px] font-mono-tag font-bold tracking-wider uppercase">
                DATA-DRIVEN GOVERNANCE
              </div>

              <div>
                <span className="text-xs font-mono-tag font-bold text-emerald-700 uppercase tracking-wider block mb-2 mt-1">
                  02 &bull; ADVISORY
                </span>
                <h3 className="font-serif-display text-2xl font-bold text-stone-950 mb-6">APR Advisory</h3>

                <ul className="space-y-4 text-stone-700 text-sm">
                  <li className="flex items-start gap-3">
                    <Check className="h-5 w-5 text-emerald-700 font-bold flex-shrink-0 mt-0.5" />
                    <span>Provides unbiased should-cost modeling and rate card verification</span>
                  </li>
                  <li className="flex items-start gap-3">
                    <Check className="h-5 w-5 text-emerald-700 font-bold flex-shrink-0 mt-0.5" />
                    <span>Conducts spec reviews and like-for-like bid analyses</span>
                  </li>
                  <li className="flex items-start gap-3">
                    <Check className="h-5 w-5 text-emerald-700 font-bold flex-shrink-0 mt-0.5" />
                    <span>Audits invoices and provides automated supplier performance ratings</span>
                  </li>
                </ul>
              </div>
            </div>

            {/* Column 3: Creative Partners */}
            <div className="bg-white p-8 rounded-3xl border border-stone-200/90 shadow-sm flex flex-col justify-between">
              <div>
                <span className="text-xs font-mono-tag font-bold text-stone-400 uppercase tracking-wider block mb-2">
                  03 &bull; EXECUTION
                </span>
                <h3 className="font-serif-display text-2xl font-bold text-stone-900 mb-6">Creative Partners</h3>

                <ul className="space-y-4 text-stone-600 text-sm">
                  <li className="flex items-start gap-3">
                    <Check className="h-5 w-5 text-emerald-600 flex-shrink-0 mt-0.5" />
                    <span>Operates with clear, standardized production briefs</span>
                  </li>
                  <li className="flex items-start gap-3">
                    <Check className="h-5 w-5 text-emerald-600 flex-shrink-0 mt-0.5" />
                    <span>Experiences faster approvals with transparent budget alignment</span>
                  </li>
                  <li className="flex items-start gap-3">
                    <Check className="h-5 w-5 text-emerald-600 flex-shrink-0 mt-0.5" />
                    <span>Focuses craft on high-impact creative delivery</span>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 7. SECTION 5: FAQS ACCORDION */}
      <section id="faqs" className="py-20 bg-[#fbfaf6] border-b border-stone-200">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <span className="text-xs font-mono-tag font-semibold text-stone-500 uppercase tracking-widest block mb-2">
              FREQUENTLY ASKED QUESTIONS
            </span>
            <h2 className="font-serif-display text-3xl font-bold text-stone-900">
              Clear answers for procurement and brand leaders.
            </h2>
          </div>

          <div className="space-y-4">
            <div className="bg-white p-6 rounded-2xl border border-stone-200">
              <h4 className="font-semibold text-base text-stone-900 mb-2">
                How does SPRS calculate supplier performance ratings?
              </h4>
              <p className="text-stone-600 text-sm leading-relaxed">
                SPRS uses a multi-factor weighted scoring algorithm evaluating Quality (35%), Timeliness &amp; Delivery (25%),
                Cost Competitiveness (20%), and Customer Service &amp; Compliance (20%), augmented by AI anomaly detection.
              </p>
            </div>

            <div className="bg-white p-6 rounded-2xl border border-stone-200">
              <h4 className="font-semibold text-base text-stone-900 mb-2">
                Will suppliers receive email alerts upon account activity?
              </h4>
              <p className="text-stone-600 text-sm leading-relaxed">
                Yes. Every successful authentication triggers a secure login alert email directly to the user's verified
                registered email address with exact timestamp, IP address, and security contact details.
              </p>
            </div>

            <div className="bg-white p-6 rounded-2xl border border-stone-200">
              <h4 className="font-semibold text-base text-stone-900 mb-2">
                Can suppliers submit their own profile updates and certifications?
              </h4>
              <p className="text-stone-600 text-sm leading-relaxed">
                Yes. The dedicated Supplier Self-Service Portal enables vendors to view ratings, respond to improvement
                actions, upload compliance documents, and update banking and contact details.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* 8. FOOTER */}
      <footer className="bg-[#12291d] text-stone-300 py-12 border-t border-emerald-900/40">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col sm:flex-row items-center justify-between gap-6 pb-8 border-b border-emerald-900/60 text-xs">
            <div className="flex items-center gap-3">
              <div className="h-8 w-8 rounded-lg bg-emerald-800/80 text-white flex items-center justify-center font-bold font-mono-tag">
                APR
              </div>
              <span className="font-serif-display text-base font-bold text-white">
                APR Consulting &bull; Supplier Ledger
              </span>
            </div>
            <div className="flex items-center gap-6 text-stone-400 font-mono-tag">
              <span>SECURITY FIRST</span>
              <span>&bull;</span>
              <span>ISO 27001 ALIGNED</span>
              <span>&bull;</span>
              <span>ENTERPRISE GRADE</span>
            </div>
          </div>
          <div className="pt-8 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-stone-400">
            <p>&copy; {new Date().getFullYear()} Supplier Performance Rating System (SPRS). All rights reserved.</p>
            <div className="flex items-center gap-6">
              <button
                type="button"
                onClick={() => scrollToSection('portal-card')}
                className="hover:text-white cursor-pointer"
              >
                Portal Sign In
              </button>
              <Link to="/register" className="hover:text-white">
                Register
              </Link>
              <button
                type="button"
                onClick={() => setShowAdvisorModal(true)}
                className="hover:text-white cursor-pointer"
              >
                Contact Advisory
              </button>
            </div>
          </div>
        </div>
      </footer>

      {/* MODAL: TALK WITH AN ADVISOR / STRATEGY BRIEFING */}
      {showAdvisorModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-fade-in">
          <div className="bg-white rounded-3xl max-w-lg w-full p-6 sm:p-8 shadow-2xl border border-stone-200 relative">
            <button
              type="button"
              onClick={() => setShowAdvisorModal(false)}
              className="absolute top-5 right-5 text-stone-400 hover:text-stone-700 p-1.5 rounded-full hover:bg-stone-100 cursor-pointer"
            >
              <X className="h-5 w-5" />
            </button>

            <div className="mb-6">
              <span className="text-[10px] font-mono-tag font-bold text-emerald-800 uppercase tracking-wider px-3 py-1 bg-emerald-50 rounded-full border border-emerald-200">
                ADVISORY CONSULTATION
              </span>
              <h3 className="font-serif-display text-2xl font-bold text-stone-900 mt-2">
                Request a Strategy Briefing
              </h3>
              <p className="text-stone-500 text-xs sm:text-sm mt-1">
                Speak directly with an executive production strategist to benchmark your agency rate cards and supplier
                ecosystem.
              </p>
            </div>

            {advisorSuccess ? (
              <div className="p-6 bg-emerald-50 rounded-2xl border border-emerald-200 text-center">
                <CheckCircle2 className="h-10 w-10 text-emerald-600 mx-auto mb-2" />
                <h4 className="font-serif-display text-lg font-bold text-emerald-900">Briefing Request Received!</h4>
                <p className="text-xs text-emerald-700 mt-1">An advisor will reach out to you within 24 hours.</p>
              </div>
            ) : (
              <form onSubmit={handleAdvisorSubmit} className="space-y-4">
                <div>
                  <label className="block text-xs font-semibold text-stone-700 mb-1">Full Name</label>
                  <input
                    type="text"
                    required
                    value={advisorForm.name}
                    onChange={(e) => setAdvisorForm({ ...advisorForm, name: e.target.value })}
                    placeholder="e.g. John Doe"
                    className="w-full px-3.5 py-2.5 bg-stone-50 border border-stone-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-[#12291d]"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-stone-700 mb-1">Work Email</label>
                  <input
                    type="email"
                    required
                    value={advisorForm.email}
                    onChange={(e) => setAdvisorForm({ ...advisorForm, email: e.target.value })}
                    placeholder="e.g. john@enterprise.com"
                    className="w-full px-3.5 py-2.5 bg-stone-50 border border-stone-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-[#12291d]"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-stone-700 mb-1">Company / Organization</label>
                  <input
                    type="text"
                    required
                    value={advisorForm.company}
                    onChange={(e) => setAdvisorForm({ ...advisorForm, company: e.target.value })}
                    placeholder="e.g. Global Brands Inc."
                    className="w-full px-3.5 py-2.5 bg-stone-50 border border-stone-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-[#12291d]"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-stone-700 mb-1">Scope of Inquiry</label>
                  <textarea
                    rows="3"
                    value={advisorForm.message}
                    onChange={(e) => setAdvisorForm({ ...advisorForm, message: e.target.value })}
                    placeholder="Describe your supplier governance, agency modeling, or cost benchmarking needs..."
                    className="w-full px-3.5 py-2.5 bg-stone-50 border border-stone-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-[#12291d]"
                  ></textarea>
                </div>

                <button
                  type="submit"
                  className="w-full py-3 rounded-xl bg-[#12291d] hover:bg-[#1a3b2b] text-white font-semibold text-sm shadow-md cursor-pointer"
                >
                  Submit Strategy Request
                </button>
              </form>
            )}
          </div>
        </div>
      )}

      {/* MODAL: BENCHMARK REPORT KEY FINDINGS */}
      {showBenchmarkModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-fade-in">
          <div className="bg-white rounded-3xl max-w-xl w-full p-6 sm:p-8 shadow-2xl border border-stone-200 relative max-h-[90vh] overflow-y-auto">
            <button
              type="button"
              onClick={() => setShowBenchmarkModal(false)}
              className="absolute top-5 right-5 text-stone-400 hover:text-stone-700 p-1.5 rounded-full hover:bg-stone-100 cursor-pointer"
            >
              <X className="h-5 w-5" />
            </button>

            <div className="mb-6">
              <span className="text-[10px] font-mono-tag font-bold text-amber-800 uppercase tracking-wider px-3 py-1 bg-amber-50 rounded-full border border-amber-200">
                2026 GLOBAL BENCHMARK
              </span>
              <h3 className="font-serif-display text-2xl font-bold text-stone-900 mt-2">
                Executive Findings Summary
              </h3>
              <p className="text-stone-500 text-xs sm:text-sm mt-1">
                Insights distilled from over 12,000 multi-market production audits and agency fee analyses.
              </p>
            </div>

            <div className="space-y-4 text-sm text-stone-700">
              <div className="p-4 bg-stone-50 rounded-2xl border border-stone-200">
                <div className="font-bold text-stone-900 flex items-center gap-2">
                  <TrendingUp className="h-4 w-4 text-emerald-600" /> 18.4% Average Trapped Spend
                </div>
                <p className="text-xs text-stone-600 mt-1">
                  Enterprises deploying automated rate card verification and should-cost modeling recover an average of
                  18.4% in unbudgeted drift within the first 90 days.
                </p>
              </div>

              <div className="p-4 bg-stone-50 rounded-2xl border border-stone-200">
                <div className="font-bold text-stone-900 flex items-center gap-2">
                  <Clock className="h-4 w-4 text-indigo-600" /> 3x Faster Bidding Milestones
                </div>
                <p className="text-xs text-stone-600 mt-1">
                  Decoupled production briefs and standardized line-item comparisons accelerate procurement cycle times
                  from 22 days down to 7 days.
                </p>
              </div>

              <div className="p-4 bg-stone-50 rounded-2xl border border-stone-200">
                <div className="font-bold text-stone-900 flex items-center gap-2">
                  <ShieldCheck className="h-4 w-4 text-emerald-600" /> 100% Audit Defense
                </div>
                <p className="text-xs text-stone-600 mt-1">
                  Immutable evaluation logs and real-time PO-to-actuals reconciliation eliminate vendor dispute overhead
                  and ensure strict compliance.
                </p>
              </div>
            </div>

            <div className="mt-6 pt-4 border-t border-stone-200 flex justify-end">
              <button
                type="button"
                onClick={() => {
                  setShowBenchmarkModal(false);
                  scrollToSection('portal-card');
                }}
                className="px-5 py-2.5 rounded-xl bg-[#12291d] hover:bg-[#1a3b2b] text-white text-xs font-semibold cursor-pointer"
              >
                Access Evaluation Workspace
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
export default Login;

