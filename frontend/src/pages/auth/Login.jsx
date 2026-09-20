import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  ArrowRight,
  Eye,
  EyeOff,
  AlertCircle,
  CheckCircle2,
  ChevronDown,
  ChevronUp,
  X,
  Send,
  HelpCircle,
  BarChart3,
  Check
} from 'lucide-react';

export const Login = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  const [formData, setFormData] = useState({ username: '', password: '' });
  const [activeTab, setActiveTab] = useState('login');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // Modals state
  const [showAdvisorModal, setShowAdvisorModal] = useState(false);
  const [showBenchmarkModal, setShowBenchmarkModal] = useState(false);
  const [advisorFormData, setAdvisorFormData] = useState({
    name: '',
    workEmail: '',
    company: '',
    spendRange: '$10M - $50M',
    notes: ''
  });
  const [advisorSubmitted, setAdvisorSubmitted] = useState(false);

  // FAQ Accordion State
  const [openFaq, setOpenFaq] = useState(null);

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
      setError('Please enter both corporate email and password');
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
        setSuccessMsg("Login successful! A login notification has been sent to your registered email.");
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

  const handleAdvisorSubmit = (e) => {
    e.preventDefault();
    setAdvisorSubmitted(true);
    setTimeout(() => {
      setAdvisorSubmitted(false);
      setShowAdvisorModal(false);
      setAdvisorFormData({
        name: '',
        workEmail: '',
        company: '',
        spendRange: '$10M - $50M',
        notes: ''
      });
    }, 2200);
  };

  const scrollToSection = (id) => {
    const el = document.getElementById(id);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
    }
  };

  const toggleFaq = (index) => {
    setOpenFaq(openFaq === index ? null : index);
  };

  const faqs = [
    {
      q: 'How does APR operate without disrupting existing creative agency relationships?',
      a: 'We act as an objective, independent advisor that establishes transparent should-cost benchmarks and fair bidding parameters. Agencies appreciate clear guidelines, while brand procurement gains 100% financial clarity.'
    },
    {
      q: 'What typical cost optimization and ROI do enterprise brands experience?',
      a: 'Across 65+ countries and thousands of production bids, enterprise clients average an 18–24% spend optimization on production budgets, delivering an average 4.2x ROI on advisory fees in Year 1.'
    },
    {
      q: 'How long does implementation take for the Supplier Performance Rating System?',
      a: 'Our cloud platform is pre-configured with standardized advertising and production rate cards. Full rollout and supplier onboarding typically occur within 2 to 3 weeks.'
    },
    {
      q: 'Is our supplier scorecard and pricing benchmark data confidential?',
      a: 'Yes. All bid evaluations, agency rate cards, and performance rating telemetry are protected by enterprise-grade encryption and isolated tenant environments.'
    }
  ];

  return (
    <div className="min-h-screen bg-[#fbfaf6] text-stone-900 font-['Inter',sans-serif] selection:bg-emerald-200 selection:text-emerald-900">
      {/* 1. TOP ANNOUNCEMENT BAR */}
      <div className="bg-[#112319] text-[#e3e8e4] text-xs py-2.5 px-4 text-center border-b border-emerald-950">
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

      {/* 2. STICKY HEADER NAVIGATION */}
      <header className="sticky top-0 z-40 bg-[#fbfaf6]/95 backdrop-blur-md border-b border-stone-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-20 flex items-center justify-between">
          {/* Logo */}
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

          {/* Nav Links */}
          <nav className="hidden md:flex items-center gap-8 text-sm font-medium text-stone-700">
            <button
              type="button"
              onClick={() => scrollToSection('challenges')}
              className="hover:text-stone-950 transition-colors cursor-pointer"
            >
              Challenges
            </button>
            <button
              type="button"
              onClick={() => scrollToSection('services')}
              className="hover:text-stone-950 transition-colors cursor-pointer"
            >
              Advisory Services
            </button>
            <button
              type="button"
              onClick={() => scrollToSection('methodology')}
              className="hover:text-stone-950 transition-colors cursor-pointer"
            >
              Methodology
            </button>
            <button
              type="button"
              onClick={() => scrollToSection('faqs')}
              className="hover:text-stone-950 transition-colors cursor-pointer"
            >
              FAQs
            </button>
          </nav>

          {/* Header Action Buttons */}
          <div className="flex items-center gap-3">
            <button
              type="button"
              onClick={() => scrollToSection('portal-card')}
              className="px-4 py-2 text-xs sm:text-sm font-medium text-stone-700 bg-white hover:bg-stone-50 border border-stone-300 rounded-full transition-colors cursor-pointer shadow-sm"
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
              <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-emerald-50 border border-emerald-300 text-[11px] font-mono-tag font-medium text-emerald-800 tracking-wider uppercase">
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
                  <div className="font-serif-display text-3xl sm:text-4xl font-bold text-stone-900">18&ndash;24%</div>
                  <p className="text-[10px] sm:text-xs font-mono-tag uppercase tracking-wider text-stone-500 mt-1">
                    TRAPPED CAPITAL UNLOCKED &amp; REINVESTED
                  </p>
                </div>
              </div>
            </div>

            {/* Right Login Card Column */}
            <div id="portal-card" className="lg:col-span-5">
              <div className="bg-white rounded-3xl p-7 sm:p-9 shadow-xl border border-stone-200/80">
                {/* Card Tag Badge */}
                <div className="flex justify-center mb-6">
                  <span className="inline-flex items-center px-3 py-1 rounded-full bg-emerald-50 border border-emerald-200 text-[10px] font-mono-tag font-semibold text-emerald-800 uppercase tracking-widest">
                    ENTERPRISE WORKSPACE PORTAL
                  </span>
                </div>

                {/* Tab Switcher */}
                <div className="flex p-1 bg-stone-100 rounded-full mb-6 text-xs">
                  <button
                    type="button"
                    onClick={() => setActiveTab('login')}
                    className={`flex-1 py-2 rounded-full transition-all cursor-pointer ${
                      activeTab === 'login'
                        ? 'bg-white text-stone-950 font-semibold shadow-sm'
                        : 'text-stone-500 hover:text-stone-900'
                    }`}
                  >
                    Sign in
                  </button>
                  <Link
                    to="/register"
                    className="flex-1 py-2 rounded-full text-center text-stone-500 hover:text-stone-900 transition-all font-medium"
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
                <div className="mt-6 p-3.5 rounded-xl bg-[#f8f9fa] border border-stone-200">
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
                <h3 className="font-serif-display text-lg font-bold text-stone-900 mb-3">
                  Limited Cost Benchmarking
                </h3>
                <p className="text-stone-600 text-xs sm:text-sm leading-relaxed">
                  Brands lack comprehensive comparative market data to assess whether agency bid estimates reflect fair
                  market rates.
                </p>
              </div>
            </div>

            {/* Card 2 */}
            <div className="bg-white p-7 rounded-2xl border border-stone-200/90 shadow-sm flex flex-col justify-between hover:shadow-md transition-shadow">
              <div>
                <span className="text-xs font-mono-tag font-bold text-stone-400 block mb-4">02</span>
                <h3 className="font-serif-display text-lg font-bold text-stone-900 mb-3">
                  Bidding Spec Inconsistencies
                </h3>
                <p className="text-stone-600 text-xs sm:text-sm leading-relaxed">
                  Fragmented project briefs and changing scope specifications make accurate triple-bid comparisons nearly
                  impossible.
                </p>
              </div>
            </div>

            {/* Card 3 */}
            <div className="bg-white p-7 rounded-2xl border border-stone-200/90 shadow-sm flex flex-col justify-between hover:shadow-md transition-shadow">
              <div>
                <span className="text-xs font-mono-tag font-bold text-stone-400 block mb-4">03</span>
                <h3 className="font-serif-display text-lg font-bold text-stone-900 mb-3">
                  Invoice Mismatches &amp; Drift
                </h3>
                <p className="text-stone-600 text-xs sm:text-sm leading-relaxed">
                  Final billing often contains unapproved change orders, scope creep, or duplicate service fees across
                  subsidiaries.
                </p>
              </div>
            </div>

            {/* Card 4 */}
            <div className="bg-white p-7 rounded-2xl border border-stone-200/90 shadow-sm flex flex-col justify-between hover:shadow-md transition-shadow">
              <div>
                <span className="text-xs font-mono-tag font-bold text-stone-400 block mb-4">04</span>
                <h3 className="font-serif-display text-lg font-bold text-stone-900 mb-3">
                  Siloed Governance Standards
                </h3>
                <p className="text-stone-600 text-xs sm:text-sm leading-relaxed">
                  Internal procurement and creative marketing operate with differing performance criteria and supplier
                  evaluations.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 5. SECTION 3: HOW APR HELPS (Dark Green Section) */}
      <section id="services" className="py-20 bg-[#0e2218] text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="max-w-3xl mb-14">
            <span className="text-xs font-mono-tag font-semibold text-emerald-400 uppercase tracking-widest block mb-3">
              HOW APR HELPS
            </span>
            <h2 className="font-serif-display text-3xl sm:text-4xl lg:text-[44px] font-bold text-white tracking-tight leading-tight">
              Unbiased advisory across your production lifecycle.
            </h2>
            <p className="text-emerald-100/80 text-base sm:text-lg mt-4 leading-relaxed">
              We operate as an extension of your marketing and procurement teams to establish rate visibility and
              supplier accountability.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {/* Service 1 */}
            <div className="p-8 rounded-2xl bg-[#142d20] border border-emerald-900/60 hover:border-emerald-700/60 transition-colors">
              <span className="text-xs font-mono-tag font-semibold text-emerald-400 uppercase tracking-wider block mb-3">
                COST BENCHMARKING
              </span>
              <h3 className="font-serif-display text-xl font-bold text-white mb-3">
                Should-Cost Analysis
              </h3>
              <p className="text-emerald-100/70 text-sm leading-relaxed">
                Objective line-item rate modeling utilizing proprietary benchmark data from 65+ countries to eliminate
                unnecessary markups.
              </p>
            </div>

            {/* Service 2 */}
            <div className="p-8 rounded-2xl bg-[#142d20] border border-emerald-900/60 hover:border-emerald-700/60 transition-colors">
              <span className="text-xs font-mono-tag font-semibold text-emerald-400 uppercase tracking-wider block mb-3">
                COMPETITIVE PROCUREMENT
              </span>
              <h3 className="font-serif-display text-xl font-bold text-white mb-3">
                Bid Management &amp; Review
              </h3>
              <p className="text-emerald-100/70 text-sm leading-relaxed">
                Independent oversight of agency and vendor bidding to ensure true apples-to-apples evaluation and
                commercial transparency.
              </p>
            </div>

            {/* Service 3 */}
            <div className="p-8 rounded-2xl bg-[#142d20] border border-emerald-900/60 hover:border-emerald-700/60 transition-colors">
              <span className="text-xs font-mono-tag font-semibold text-emerald-400 uppercase tracking-wider block mb-3">
                COMMERCIAL CLARITY
              </span>
              <h3 className="font-serif-display text-xl font-bold text-white mb-3">
                Agency Fee Analysis
              </h3>
              <p className="text-emerald-100/70 text-sm leading-relaxed">
                Detailed assessment of agency staffing plans, hourly rate cards, overhead allocations, and scope of work
                alignment.
              </p>
            </div>

            {/* Service 4 */}
            <div className="p-8 rounded-2xl bg-[#142d20] border border-emerald-900/60 hover:border-emerald-700/60 transition-colors">
              <span className="text-xs font-mono-tag font-semibold text-emerald-400 uppercase tracking-wider block mb-3">
                FINANCIAL INTEGRITY
              </span>
              <h3 className="font-serif-display text-xl font-bold text-white mb-3">
                Invoice Reconciliation
              </h3>
              <p className="text-emerald-100/70 text-sm leading-relaxed">
                Post-production audit and billing verification ensuring final supplier invoices match contracted quotes
                and deliverables.
              </p>
            </div>

            {/* Service 5 */}
            <div className="p-8 rounded-2xl bg-[#142d20] border border-emerald-900/60 hover:border-emerald-700/60 transition-colors">
              <span className="text-xs font-mono-tag font-semibold text-emerald-400 uppercase tracking-wider block mb-3">
                STRATEGIC SOURCING
              </span>
              <h3 className="font-serif-display text-xl font-bold text-white mb-3">
                Production Decoupling
              </h3>
              <p className="text-emerald-100/70 text-sm leading-relaxed">
                Separating creative ideation from content execution to drive efficiency and direct specialist supplier
                contracting.
              </p>
            </div>

            {/* Service 6 */}
            <div className="p-8 rounded-2xl bg-[#142d20] border border-emerald-900/60 hover:border-emerald-700/60 transition-colors">
              <span className="text-xs font-mono-tag font-semibold text-emerald-400 uppercase tracking-wider block mb-3">
                CREATIVE ADVISORY
              </span>
              <h3 className="font-serif-display text-xl font-bold text-white mb-3">
                Executive Producer Oversight
              </h3>
              <p className="text-emerald-100/70 text-sm leading-relaxed">
                Dedicated seasoned production consultants embedded with your teams to safeguard creative vision while
                controlling spend.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* 6. SECTION 4: METHODOLOGY */}
      <section id="methodology" className="py-20 bg-[#fbfaf6] border-b border-stone-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="max-w-3xl mb-14">
            <span className="text-xs font-mono-tag font-semibold text-stone-500 uppercase tracking-widest block mb-3">
              A PROVEN METHOD FOR ECOSYSTEM OPTIMIZATION
            </span>
            <h2 className="font-serif-display text-3xl sm:text-4xl lg:text-[44px] font-bold text-stone-900 tracking-tight leading-tight">
              Governance, Not Interference.
            </h2>
            <p className="text-stone-600 text-base sm:text-lg mt-4 leading-relaxed">
              Our 3-pillar framework aligns brand creative directors, procurement executives, and external production
              partners.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {/* Pillar 1 */}
            <div className="bg-white p-8 rounded-2xl border border-stone-200 shadow-sm flex flex-col justify-between">
              <div>
                <span className="text-xs font-mono-tag font-bold text-stone-400 block mb-3">01 / STRATEGY</span>
                <h3 className="font-serif-display text-xl font-bold text-stone-900 mb-3">Your Brand</h3>
                <p className="text-stone-600 text-sm leading-relaxed">
                  Defines creative vision, strategic objectives, and overall marketing campaign deliverables.
                </p>
              </div>
              <div className="mt-6 pt-6 border-t border-stone-100 text-xs text-stone-500 font-mono-tag">
                Scope &bull; Campaign KPIs &bull; Brand Guardrails
              </div>
            </div>

            {/* Pillar 2 (Highlighted) */}
            <div className="bg-[#12291d] text-white p-8 rounded-2xl border border-emerald-900 shadow-lg flex flex-col justify-between relative overflow-hidden">
              <div className="absolute top-0 right-0 w-32 h-32 bg-emerald-600/10 rounded-full blur-2xl"></div>
              <div>
                <span className="text-xs font-mono-tag font-bold text-emerald-400 block mb-3">
                  02 / ADVISORY (THE LEDGER)
                </span>
                <h3 className="font-serif-display text-xl font-bold text-white mb-3">APR Advisory</h3>
                <p className="text-emerald-100/80 text-sm leading-relaxed">
                  Provides should-cost baseline intelligence, rate validation, supplier scorecards, and approval workflows.
                </p>
              </div>
              <div className="mt-6 pt-6 border-t border-emerald-900/60 text-xs text-emerald-300 font-mono-tag">
                Real-Time Benchmark &bull; Bid Audit &bull; Rating Telemetry
              </div>
            </div>

            {/* Pillar 3 */}
            <div className="bg-white p-8 rounded-2xl border border-stone-200 shadow-sm flex flex-col justify-between">
              <div>
                <span className="text-xs font-mono-tag font-bold text-stone-400 block mb-3">03 / EXECUTION</span>
                <h3 className="font-serif-display text-xl font-bold text-stone-900 mb-3">Creative Partners</h3>
                <p className="text-stone-600 text-sm leading-relaxed">
                  Agencies, production houses, and post studios execute content with crystal-clear specifications and
                  standard rates.
                </p>
              </div>
              <div className="mt-6 pt-6 border-t border-stone-100 text-xs text-stone-500 font-mono-tag">
                Shoot Execution &bull; Post-Production &bull; Asset Delivery
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 7. SECTION 5: FAQS (Accordion) */}
      <section id="faqs" className="py-20 bg-[#fbfaf6] border-b border-stone-200">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <span className="text-xs font-mono-tag font-semibold text-stone-500 uppercase tracking-widest block mb-3">
              FREQUENTLY ASKED QUESTIONS
            </span>
            <h2 className="font-serif-display text-3xl sm:text-4xl font-bold text-stone-900 tracking-tight">
              Common Advisory Questions
            </h2>
          </div>

          <div className="space-y-4">
            {faqs.map((faq, idx) => (
              <div
                key={idx}
                className="bg-white rounded-2xl border border-stone-200 overflow-hidden shadow-sm transition-all"
              >
                <button
                  type="button"
                  onClick={() => toggleFaq(idx)}
                  className="w-full px-6 py-5 text-left flex items-center justify-between gap-4 font-semibold text-stone-900 hover:text-emerald-900 transition-colors cursor-pointer"
                >
                  <span className="text-base sm:text-lg">{faq.q}</span>
                  {openFaq === idx ? (
                    <ChevronUp className="h-5 w-5 text-stone-400 flex-shrink-0" />
                  ) : (
                    <ChevronDown className="h-5 w-5 text-stone-400 flex-shrink-0" />
                  )}
                </button>
                {openFaq === idx && (
                  <div className="px-6 pb-6 pt-1 text-sm sm:text-base text-stone-600 leading-relaxed border-t border-stone-100">
                    {faq.a}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 8. FOOTER */}
      <footer className="bg-[#12291d] text-emerald-100/70 py-12 border-t border-emerald-950">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-10">
            <div className="space-y-3">
              <div className="flex items-center gap-2">
                <div className="h-8 w-8 rounded-lg bg-white text-[#12291d] flex items-center justify-center font-bold text-xs tracking-wider font-mono-tag">
                  APR
                </div>
                <span className="text-base font-bold text-white font-serif-display">APR Consulting</span>
              </div>
              <p className="text-xs text-emerald-100/60 leading-relaxed">
                Global marketing production consulting and supplier ecosystem performance rating system.
              </p>
            </div>

            <div>
              <h4 className="text-xs font-mono-tag font-semibold text-white uppercase tracking-wider mb-3">
                Navigation
              </h4>
              <ul className="space-y-2 text-xs">
                <li>
                  <button
                    type="button"
                    onClick={() => scrollToSection('challenges')}
                    className="hover:text-white cursor-pointer"
                  >
                    Friction &amp; Challenges
                  </button>
                </li>
                <li>
                  <button
                    type="button"
                    onClick={() => scrollToSection('services')}
                    className="hover:text-white cursor-pointer"
                  >
                    Advisory Services
                  </button>
                </li>
                <li>
                  <button
                    type="button"
                    onClick={() => scrollToSection('methodology')}
                    className="hover:text-white cursor-pointer"
                  >
                    Proven Methodology
                  </button>
                </li>
                <li>
                  <button
                    type="button"
                    onClick={() => scrollToSection('faqs')}
                    className="hover:text-white cursor-pointer"
                  >
                    Executive FAQs
                  </button>
                </li>
              </ul>
            </div>

            <div>
              <h4 className="text-xs font-mono-tag font-semibold text-white uppercase tracking-wider mb-3">
                Workspace
              </h4>
              <ul className="space-y-2 text-xs">
                <li>
                  <button
                    type="button"
                    onClick={() => scrollToSection('portal-card')}
                    className="hover:text-white cursor-pointer"
                  >
                    Sign In to Portal
                  </button>
                </li>
                <li>
                  <Link to="/register" className="hover:text-white">
                    Register Evaluator Account
                  </Link>
                </li>
                <li>
                  <button
                    type="button"
                    onClick={() => setShowBenchmarkModal(true)}
                    className="hover:text-white cursor-pointer"
                  >
                    2026 Ecosystem Report
                  </button>
                </li>
              </ul>
            </div>

            <div>
              <h4 className="text-xs font-mono-tag font-semibold text-white uppercase tracking-wider mb-3">
                Enterprise Assurance
              </h4>
              <div className="p-3 rounded-xl bg-[#193627] border border-emerald-900/60 text-[11px] text-emerald-100/80">
                <div className="font-semibold text-white mb-1">SOC-2 Type II Certified</div>
                Enterprise data isolation, SSO integration, and encrypted supplier scorecards.
              </div>
            </div>
          </div>

          <div className="pt-8 border-t border-emerald-900/60 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-emerald-100/50">
            <div>&copy; {new Date().getFullYear()} APR Consulting Inc. All rights reserved.</div>
            <div className="flex items-center gap-6">
              <span className="hover:text-emerald-100 cursor-pointer">Confidentiality Policy</span>
              <span className="hover:text-emerald-100 cursor-pointer">Terms of Service</span>
              <span className="hover:text-emerald-100 cursor-pointer">Security Whitepaper</span>
            </div>
          </div>
        </div>
      </footer>

      {/* 9. MODAL 1: BENCHMARK REPORT MODAL */}
      {showBenchmarkModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
          <div className="bg-white rounded-3xl max-w-2xl w-full p-6 sm:p-8 shadow-2xl border border-stone-200 relative max-h-[90vh] overflow-y-auto">
            <button
              type="button"
              onClick={() => setShowBenchmarkModal(false)}
              className="absolute top-5 right-5 p-2 rounded-full hover:bg-stone-100 text-stone-400 hover:text-stone-700 transition-colors cursor-pointer"
            >
              <X className="h-5 w-5" />
            </button>

            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-50 border border-emerald-200 text-[10px] font-mono-tag font-medium text-emerald-800 uppercase tracking-wider mb-4">
              <BarChart3 className="h-3 w-3" />
              <span>2026 Executive Benchmark Intelligence</span>
            </div>

            <h3 className="font-serif-display text-2xl sm:text-3xl font-bold text-stone-900 mb-2">
              Production &amp; Supplier Ecosystem Report
            </h3>
            <p className="text-stone-600 text-sm mb-6">
              Aggregated across $3.8B+ in marketing production bids spanning 65 countries in 2025&ndash;2026.
            </p>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-6">
              <div className="p-4 rounded-xl bg-stone-50 border border-stone-200">
                <div className="text-2xl font-bold font-serif-display text-emerald-800">23.4%</div>
                <div className="text-xs font-semibold text-stone-800 mt-1">Average Rate Variance</div>
                <p className="text-[11px] text-stone-500 mt-1">
                  Disparity between unbenchmarked initial agency estimates and actual should-cost market value.
                </p>
              </div>

              <div className="p-4 rounded-xl bg-stone-50 border border-stone-200">
                <div className="text-2xl font-bold font-serif-display text-emerald-800">91%</div>
                <div className="text-xs font-semibold text-stone-800 mt-1">Compliance Rate</div>
                <p className="text-[11px] text-stone-500 mt-1">
                  Adherence to master rate cards achieved within 90 days of implementing rating scorecards.
                </p>
              </div>

              <div className="p-4 rounded-xl bg-stone-50 border border-stone-200">
                <div className="text-2xl font-bold font-serif-display text-emerald-800">4.2x</div>
                <div className="text-xs font-semibold text-stone-800 mt-1">Year 1 Advisory ROI</div>
                <p className="text-[11px] text-stone-500 mt-1">
                  Cost savings delivered per dollar invested in independent production oversight.
                </p>
              </div>

              <div className="p-4 rounded-xl bg-stone-50 border border-stone-200">
                <div className="text-2xl font-bold font-serif-display text-emerald-800">14 Days</div>
                <div className="text-xs font-semibold text-stone-800 mt-1">Cycle Time Reduction</div>
                <p className="text-[11px] text-stone-500 mt-1">
                  Faster bid approval turnaround when using standardized electronic scorecards.
                </p>
              </div>
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t border-stone-200">
              <button
                type="button"
                onClick={() => setShowBenchmarkModal(false)}
                className="px-5 py-2.5 rounded-xl bg-stone-100 hover:bg-stone-200 text-stone-700 text-xs sm:text-sm font-semibold transition-colors cursor-pointer"
              >
                Close Report
              </button>
              <button
                type="button"
                onClick={() => {
                  setShowBenchmarkModal(false);
                  setShowAdvisorModal(true);
                }}
                className="px-5 py-2.5 rounded-xl bg-[#12291d] hover:bg-[#1a3b2b] text-white text-xs sm:text-sm font-semibold transition-colors flex items-center gap-1.5 cursor-pointer"
              >
                Request Custom Benchmark <ArrowRight className="h-4 w-4" />
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 10. MODAL 2: TALK WITH AN ADVISOR MODAL */}
      {showAdvisorModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
          <div className="bg-white rounded-3xl max-w-lg w-full p-6 sm:p-8 shadow-2xl border border-stone-200 relative max-h-[90vh] overflow-y-auto">
            <button
              type="button"
              onClick={() => setShowAdvisorModal(false)}
              className="absolute top-5 right-5 p-2 rounded-full hover:bg-stone-100 text-stone-400 hover:text-stone-700 transition-colors cursor-pointer"
            >
              <X className="h-5 w-5" />
            </button>

            {advisorSubmitted ? (
              <div className="py-8 text-center space-y-4">
                <div className="h-16 w-16 bg-emerald-100 text-emerald-700 rounded-full flex items-center justify-center mx-auto">
                  <Check className="h-8 w-8" />
                </div>
                <h3 className="font-serif-display text-2xl font-bold text-stone-900">
                  Strategy Briefing Requested
                </h3>
                <p className="text-sm text-stone-600 max-w-xs mx-auto">
                  An APR Senior Production Advisor will review your profile and contact you within 1 business day.
                </p>
              </div>
            ) : (
              <div>
                <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-50 border border-emerald-200 text-[10px] font-mono-tag font-medium text-emerald-800 uppercase tracking-wider mb-4">
                  <HelpCircle className="h-3 w-3" />
                  <span>Strategic Production Consultation</span>
                </div>

                <h3 className="font-serif-display text-2xl font-bold text-stone-900 mb-1">
                  Request Strategy Briefing
                </h3>
                <p className="text-stone-600 text-xs sm:text-sm mb-5">
                  Discuss your brand's production decoupling, rate cards, and supplier scorecards with an APR executive.
                </p>

                <form onSubmit={handleAdvisorSubmit} className="space-y-4">
                  <div>
                    <label className="block text-[11px] font-mono-tag font-semibold text-stone-600 uppercase tracking-wider mb-1">
                      Full Name
                    </label>
                    <input
                      type="text"
                      required
                      placeholder="e.g. Eleanor Vance"
                      value={advisorFormData.name}
                      onChange={(e) => setAdvisorFormData({ ...advisorFormData, name: e.target.value })}
                      className="w-full px-3.5 py-2.5 bg-[#eef2f8] border border-stone-200 rounded-xl text-stone-900 text-sm focus:outline-none focus:ring-2 focus:ring-[#12291d] focus:bg-white transition-all"
                    />
                  </div>

                  <div>
                    <label className="block text-[11px] font-mono-tag font-semibold text-stone-600 uppercase tracking-wider mb-1">
                      Work Email
                    </label>
                    <input
                      type="email"
                      required
                      placeholder="name@enterprise.com"
                      value={advisorFormData.workEmail}
                      onChange={(e) => setAdvisorFormData({ ...advisorFormData, workEmail: e.target.value })}
                      className="w-full px-3.5 py-2.5 bg-[#eef2f8] border border-stone-200 rounded-xl text-stone-900 text-sm focus:outline-none focus:ring-2 focus:ring-[#12291d] focus:bg-white transition-all"
                    />
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    <div>
                      <label className="block text-[11px] font-mono-tag font-semibold text-stone-600 uppercase tracking-wider mb-1">
                        Company Name
                      </label>
                      <input
                        type="text"
                        required
                        placeholder="e.g. Apex Global"
                        value={advisorFormData.company}
                        onChange={(e) => setAdvisorFormData({ ...advisorFormData, company: e.target.value })}
                        className="w-full px-3.5 py-2.5 bg-[#eef2f8] border border-stone-200 rounded-xl text-stone-900 text-sm focus:outline-none focus:ring-2 focus:ring-[#12291d] focus:bg-white transition-all"
                      />
                    </div>

                    <div>
                      <label className="block text-[11px] font-mono-tag font-semibold text-stone-600 uppercase tracking-wider mb-1">
                        Annual Spend
                      </label>
                      <select
                        value={advisorFormData.spendRange}
                        onChange={(e) => setAdvisorFormData({ ...advisorFormData, spendRange: e.target.value })}
                        className="w-full px-3.5 py-2.5 bg-[#eef2f8] border border-stone-200 rounded-xl text-stone-900 text-sm focus:outline-none focus:ring-2 focus:ring-[#12291d] focus:bg-white transition-all"
                      >
                        <option value="Under $10M">Under $10M</option>
                        <option value="$10M - $50M">$10M - $50M</option>
                        <option value="$50M - $150M">$50M - $150M</option>
                        <option value="$150M+">$150M+</option>
                      </select>
                    </div>
                  </div>

                  <div>
                    <label className="block text-[11px] font-mono-tag font-semibold text-stone-600 uppercase tracking-wider mb-1">
                      Primary Objectives
                    </label>
                    <textarea
                      rows="2"
                      placeholder="e.g. Scope decoupling, agency fee audit, scorecard rollout..."
                      value={advisorFormData.notes}
                      onChange={(e) => setAdvisorFormData({ ...advisorFormData, notes: e.target.value })}
                      className="w-full px-3.5 py-2.5 bg-[#eef2f8] border border-stone-200 rounded-xl text-stone-900 text-sm focus:outline-none focus:ring-2 focus:ring-[#12291d] focus:bg-white transition-all"
                    ></textarea>
                  </div>

                  <button
                    type="submit"
                    className="w-full py-3.5 rounded-xl bg-[#12291d] hover:bg-[#1a3b2b] text-white text-sm font-semibold transition-all shadow-md flex items-center justify-center gap-2 cursor-pointer mt-2"
                  >
                    <Send className="h-4 w-4" /> Submit Strategy Request
                  </button>
                </form>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
