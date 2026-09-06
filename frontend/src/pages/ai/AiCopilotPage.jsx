import React, { useState, useEffect, useRef } from 'react';
import { aiService } from '../../services/ai.service';
import { Card } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { Toast } from '../../components/common/Toast';
import {
  Sparkles,
  Send,
  ThumbsUp,
  ThumbsDown,
  History,
  Bot,
  User,
  ShieldCheck,
  AlertCircle,
  HelpCircle,
  RefreshCw,
  TrendingUp,
  Building2,
  AlertTriangle,
  CheckCircle2,
  ChevronRight,
  MessageSquare
} from 'lucide-react';

const SUGGESTED_QUERIES = [
  'Which suppliers are performing poorly?',
  'Why is a supplier considered high risk?',
  'Which suppliers improved this quarter?',
  'Compare top 2 suppliers in the system',
  'Which improvement actions are overdue?',
  'Give me an executive portfolio summary'
];

export const AiCopilotPage = () => {
  const [messages, setMessages] = useState([
    {
      id: 'welcome',
      sender: 'ai',
      text: `### Welcome to SPRS Supplier Management AI Copilot\n\nI am your intelligent decision-support copilot. I analyze live scorecards, risk indicators, multi-cycle regression trends, and open workflow actions to provide explainable insights.\n\n**How can I assist your procurement decisions today?**`,
      queryIntent: 'Welcome & System Overview',
      confidence: 'HIGH',
      suggestedActions: [
        'Explore underperforming suppliers',
        'Review high-risk vendors'
      ],
      followUps: [
        'Which suppliers are performing poorly?',
        'Which suppliers are considered high risk?',
        'Which improvement actions are overdue?'
      ],
      createdAt: new Date().toISOString()
    }
  ]);

  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [history, setHistory] = useState([]);
  const [showHistory, setShowHistory] = useState(false);
  const [toast, setToast] = useState({ message: '', type: 'success' });
  const [feedbackModal, setFeedbackModal] = useState(null); // { historyId, helpful: true/false }
  const [feedbackReason, setFeedbackReason] = useState('');

  const messagesEndRef = useRef(null);

  useEffect(() => {
    loadHistory();
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView?.({ behavior: 'smooth' });
  }, [messages]);

  const loadHistory = async () => {
    try {
      const res = await aiService.getCopilotHistory();
      if (res.success) {
        setHistory(res.data);
      }
    } catch (err) {
      console.error('Failed to load copilot query history:', err);
    }
  };

  const handleSend = async (queryText) => {
    const textToSend = queryText || input;
    if (!textToSend.trim() || loading) return;

    const userMessage = {
      id: Date.now().toString(),
      sender: 'user',
      text: textToSend,
      createdAt: new Date().toISOString()
    };

    setMessages((prev) => [...prev, userMessage]);
    if (!queryText) setInput('');
    setLoading(true);

    try {
      const res = await aiService.queryCopilot({ question: textToSend });
      if (res.success && res.data) {
        const aiMessage = {
          id: res.data.id || (Date.now() + 1).toString(),
          sender: 'ai',
          text: res.data.answer,
          queryIntent: res.data.queryIntent,
          intentCategory: res.data.intentCategory,
          confidence: res.data.confidence,
          dataCitations: res.data.dataCitations || [],
          relevantData: res.data.relevantData || [],
          suggestedActions: res.data.suggestedActions || [],
          followUps: res.data.followUpQuestions || [],
          createdAt: res.data.createdAt || new Date().toISOString()
        };
        setMessages((prev) => [...prev, aiMessage]);
        loadHistory();
      }
    } catch (err) {
      console.error('Copilot query error:', err);
      const errorMessage = {
        id: (Date.now() + 1).toString(),
        sender: 'ai',
        text: `### Query Processing Error\n\nUnable to retrieve analytics at this moment: ${err.response?.data?.message || err.message}.\n\nPlease try rephrasing your question or check system permissions.`,
        isError: true,
        createdAt: new Date().toISOString()
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const handleFeedbackSubmit = async () => {
    if (!feedbackModal) return;
    try {
      await aiService.submitFeedback(feedbackModal.historyId, {
        helpful: feedbackModal.helpful,
        feedbackReason: feedbackReason.trim() || 'No additional comments'
      });
      setToast({ message: 'Thank you for your feedback! It helps improve decision support.', type: 'success' });
      setFeedbackModal(null);
      setFeedbackReason('');
      loadHistory();
    } catch (err) {
      setToast({ message: 'Failed to record feedback.', type: 'error' });
    }
  };

  const renderFormattedMarkdown = (text) => {
    if (!text) return null;
    const lines = text.split('\n');

    return (
      <div className="space-y-2 text-sm leading-relaxed">
        {lines.map((line, idx) => {
          if (line.startsWith('### ')) {
            return (
              <h3 key={idx} className="text-base font-bold text-slate-900 mt-3 mb-1">
                {line.replace('### ', '')}
              </h3>
            );
          }
          if (line.startsWith('#### ')) {
            return (
              <h4 key={idx} className="text-sm font-bold text-slate-800 mt-2 mb-1">
                {line.replace('#### ', '')}
              </h4>
            );
          }
          if (line.startsWith('> ')) {
            return (
              <div key={idx} className="p-3 bg-blue-50/70 border-l-4 border-blue-600 rounded-r-lg text-xs text-blue-900 my-2 font-medium">
                {line.replace('> ', '')}
              </div>
            );
          }
          if (line.startsWith('- ')) {
            return (
              <div key={idx} className="flex items-start gap-2 pl-2">
                <span className="h-1.5 w-1.5 rounded-full bg-blue-600 mt-2 flex-shrink-0"></span>
                <span>{line.replace('- ', '')}</span>
              </div>
            );
          }
          if (line.match(/^\d+\.\s/)) {
            return (
              <div key={idx} className="flex items-start gap-2 pl-2">
                <span className="font-bold text-blue-600 text-xs mt-0.5">{line.match(/^\d+\./)[0]}</span>
                <span>{line.replace(/^\d+\.\s/, '')}</span>
              </div>
            );
          }
          if (!line.trim()) {
            return <div key={idx} className="h-1" />;
          }
          return <p key={idx}>{line}</p>;
        })}
      </div>
    );
  };

  return (
    <div className="space-y-6 max-w-6xl mx-auto">
      {toast?.message && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast({ message: '', type: 'success' })}
        />
      )}

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <span className="px-2.5 py-0.5 rounded-md bg-purple-100 text-purple-700 font-bold text-xs flex items-center gap-1.5 shadow-xs">
              <Sparkles className="h-3.5 w-3.5" /> AI COPILOT
            </span>
            <span className="text-xs bg-slate-100 text-slate-600 font-medium px-2 py-0.5 rounded">
              Advisory Decision Support
            </span>
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">
            Supplier Management Copilot
          </h1>
          <p className="text-sm text-slate-500">
            Ask natural language analytics questions grounded in real authorized supplier data.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Button
            variant="secondary"
            icon={History}
            onClick={() => setShowHistory(!showHistory)}
          >
            {showHistory ? 'Hide History' : 'Query History'}
          </Button>
          <Button
            variant="outline"
            icon={RefreshCw}
            onClick={() =>
              setMessages([
                {
                  id: 'reset',
                  sender: 'ai',
                  text: `### Session Reset\n\nAI Copilot is ready. Ask any analytical question or select a quick prompt below.`,
                  createdAt: new Date().toISOString()
                }
              ])
            }
          >
            New Session
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 items-start">
        {/* Main Conversation Container */}
        <div className={`space-y-4 ${showHistory ? 'lg:col-span-3' : 'lg:col-span-4'}`}>
          <div className="bg-white rounded-2xl border border-slate-200/80 shadow-xs flex flex-col h-[640px]">
            {/* Messages Scroll Area */}
            <div className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6">
              {messages.map((m) => (
                <div
                  key={m.id}
                  className={`flex gap-3.5 ${
                    m.sender === 'user' ? 'justify-end' : 'justify-start'
                  }`}
                >
                  {m.sender === 'ai' && (
                    <div className="h-9 w-9 rounded-xl bg-purple-600 text-white flex items-center justify-center flex-shrink-0 shadow-sm">
                      <Bot className="h-5 w-5" />
                    </div>
                  )}

                  <div
                    className={`max-w-3xl rounded-2xl p-4 sm:p-5 space-y-3 ${
                      m.sender === 'user'
                        ? 'bg-blue-600 text-white shadow-xs'
                        : m.isError
                        ? 'bg-rose-50 border border-rose-200 text-rose-800'
                        : 'bg-slate-50/90 border border-slate-200/80 text-slate-800'
                    }`}
                  >
                    {/* Message Header for AI */}
                    {m.sender === 'ai' && !m.isError && (
                      <div className="flex items-center justify-between border-b border-slate-200/60 pb-2.5 text-xs text-slate-500">
                        <div className="flex items-center gap-2">
                          <span className="font-bold text-purple-700 bg-purple-50 px-2 py-0.5 rounded border border-purple-200">
                            {m.queryIntent || 'AI Decision Support'}
                          </span>
                          <span className="flex items-center gap-1 text-emerald-600 font-semibold">
                            <ShieldCheck className="h-3.5 w-3.5" /> Authorized Data Grounded
                          </span>
                        </div>
                        <span className="text-[11px] text-slate-400">
                          {new Date(m.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                        </span>
                      </div>
                    )}

                    {/* Content */}
                    <div>
                      {m.sender === 'user' ? (
                        <p className="text-sm font-medium">{m.text}</p>
                      ) : (
                        renderFormattedMarkdown(m.text)
                      )}
                    </div>

                    {/* Data Citations & Suggested Next Actions */}
                    {m.sender === 'ai' && !m.isError && (
                      <>
                        {m.dataCitations && m.dataCitations.length > 0 && (
                          <div className="pt-2 border-t border-slate-200/60">
                            <span className="text-[11px] font-bold text-slate-500 uppercase tracking-wider block mb-1">
                              Data Sources & Evidence:
                            </span>
                            <ul className="text-xs text-slate-500 space-y-0.5">
                              {m.dataCitations.map((c, i) => (
                                <li key={i} className="flex items-center gap-1.5">
                                  <span className="h-1 w-1 rounded-full bg-slate-400"></span>
                                  <span>{c}</span>
                                </li>
                              ))}
                            </ul>
                          </div>
                        )}

                        {m.suggestedActions && m.suggestedActions.length > 0 && (
                          <div className="p-3 bg-white rounded-xl border border-slate-200/80 space-y-1.5 shadow-2xs">
                            <span className="text-[11px] font-bold text-blue-700 uppercase tracking-wider block">
                              Recommended Human Next Steps:
                            </span>
                            <ul className="text-xs text-slate-700 space-y-1">
                              {m.suggestedActions.map((act, i) => (
                                <li key={i} className="flex items-start gap-1.5 font-medium">
                                  <CheckCircle2 className="h-3.5 w-3.5 text-blue-600 mt-0.5 flex-shrink-0" />
                                  <span>{act}</span>
                                </li>
                              ))}
                            </ul>
                          </div>
                        )}

                        {/* Follow up Prompts */}
                        {m.followUps && m.followUps.length > 0 && (
                          <div className="pt-2 flex flex-wrap gap-1.5 items-center">
                            <span className="text-[11px] text-slate-400 font-medium mr-1">Follow-up:</span>
                            {m.followUps.map((f, i) => (
                              <button
                                key={i}
                                type="button"
                                onClick={() => handleSend(f)}
                                className="text-[11px] bg-white border border-slate-200 hover:border-purple-300 hover:bg-purple-50 text-slate-700 hover:text-purple-800 font-medium px-2.5 py-1 rounded-full transition-all shadow-2xs cursor-pointer flex items-center gap-1"
                              >
                                {f} <ChevronRight className="h-2.5 w-2.5" />
                              </button>
                            ))}
                          </div>
                        )}

                        {/* Feedback Toolbar */}
                        {m.id !== 'welcome' && (
                          <div className="flex items-center justify-end gap-2 pt-1 text-slate-400">
                            <span className="text-[11px]">Was this response helpful?</span>
                            <button
                              onClick={() => setFeedbackModal({ historyId: m.id, helpful: true })}
                              className="p-1 hover:text-emerald-600 hover:bg-emerald-50 rounded transition-colors"
                              title="Helpful"
                            >
                              <ThumbsUp className="h-3.5 w-3.5" />
                            </button>
                            <button
                              onClick={() => setFeedbackModal({ historyId: m.id, helpful: false })}
                              className="p-1 hover:text-rose-600 hover:bg-rose-50 rounded transition-colors"
                              title="Not helpful"
                            >
                              <ThumbsDown className="h-3.5 w-3.5" />
                            </button>
                          </div>
                        )}
                      </>
                    )}
                  </div>

                  {m.sender === 'user' && (
                    <div className="h-9 w-9 rounded-xl bg-slate-900 text-white flex items-center justify-center flex-shrink-0 shadow-sm">
                      <User className="h-5 w-5" />
                    </div>
                  )}
                </div>
              ))}

              {loading && (
                <div className="flex gap-3.5 items-start">
                  <div className="h-9 w-9 rounded-xl bg-purple-600 text-white flex items-center justify-center flex-shrink-0 shadow-sm animate-pulse">
                    <Bot className="h-5 w-5" />
                  </div>
                  <div className="p-4 bg-slate-50 rounded-2xl border border-slate-200 text-slate-500 text-xs flex items-center gap-2.5">
                    <div className="h-4 w-4 border-2 border-purple-600 border-t-transparent rounded-full animate-spin"></div>
                    <span className="font-medium animate-pulse">Retrieving authorized analytics and calculating metrics...</span>
                  </div>
                </div>
              )}
              <div ref={messagesEndRef} />
            </div>

            {/* Quick Prompt Chips */}
            <div className="px-4 py-2 border-t border-slate-100 bg-slate-50/50 flex flex-nowrap overflow-x-auto gap-2 text-xs scrollbar-none">
              <span className="text-slate-400 font-semibold self-center flex-shrink-0 text-[11px]">Suggested:</span>
              {SUGGESTED_QUERIES.map((q, idx) => (
                <button
                  key={idx}
                  type="button"
                  onClick={() => handleSend(q)}
                  disabled={loading}
                  className="flex-shrink-0 bg-white border border-slate-200 hover:border-purple-300 hover:bg-purple-50 text-slate-700 hover:text-purple-800 px-3 py-1.5 rounded-lg text-xs font-medium transition-all shadow-2xs cursor-pointer disabled:opacity-50"
                >
                  {q}
                </button>
              ))}
            </div>

            {/* Input Bar */}
            <form
              onSubmit={(e) => {
                e.preventDefault();
                handleSend();
              }}
              className="p-3 sm:p-4 border-t border-slate-200 flex items-center gap-3 bg-white rounded-b-2xl"
            >
              <input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Ask any supplier performance, risk, or comparative question..."
                disabled={loading}
                className="flex-1 px-4 py-2.5 text-sm bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 focus:bg-white transition-all"
              />
              <Button
                type="submit"
                variant="primary"
                disabled={!input.trim() || loading}
                icon={Send}
                className="bg-purple-600 hover:bg-purple-700"
              >
                Send
              </Button>
            </form>
          </div>
        </div>

        {/* History Drawer */}
        {showHistory && (
          <div className="lg:col-span-1 bg-white rounded-2xl border border-slate-200/80 p-4 shadow-xs space-y-4 max-h-[640px] overflow-y-auto">
            <div className="flex items-center justify-between pb-2 border-b border-slate-100">
              <div className="flex items-center gap-2">
                <History className="h-4 w-4 text-slate-500" />
                <h3 className="font-bold text-slate-800 text-sm">Past Inquiries</h3>
              </div>
              <span className="text-[11px] bg-slate-100 text-slate-600 font-semibold px-2 py-0.5 rounded-full">
                {history.length}
              </span>
            </div>

            {history.length === 0 ? (
              <p className="text-xs text-slate-400 py-6 text-center">No query history yet.</p>
            ) : (
              <div className="space-y-2">
                {history.map((item) => (
                  <div
                    key={item.id}
                    onClick={() => handleSend(item.question)}
                    className="p-2.5 rounded-xl border border-slate-100 bg-slate-50/70 hover:bg-purple-50/50 hover:border-purple-200 transition-all cursor-pointer space-y-1"
                  >
                    <p className="text-xs font-semibold text-slate-800 line-clamp-2">{item.question}</p>
                    <div className="flex items-center justify-between text-[10px] text-slate-400">
                      <span>{item.intentCategory || 'ANALYTICS'}</span>
                      <span>{new Date(item.createdAt).toLocaleDateString()}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>

      {/* Feedback Dialog Modal */}
      {feedbackModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-slate-200 p-6 max-w-md w-full shadow-xl space-y-4">
            <div className="flex items-center gap-3">
              <div className={`h-10 w-10 rounded-xl flex items-center justify-center ${feedbackModal.helpful ? 'bg-emerald-100 text-emerald-600' : 'bg-rose-100 text-rose-600'}`}>
                {feedbackModal.helpful ? <ThumbsUp className="h-5 w-5" /> : <ThumbsDown className="h-5 w-5" />}
              </div>
              <div>
                <h3 className="text-base font-bold text-slate-900">
                  {feedbackModal.helpful ? 'Positive Response Feedback' : 'Improve Response Quality'}
                </h3>
                <p className="text-xs text-slate-500">
                  {feedbackModal.helpful ? 'What made this answer helpful?' : 'How could this answer be improved?'}
                </p>
              </div>
            </div>

            <textarea
              rows="3"
              value={feedbackReason}
              onChange={(e) => setFeedbackReason(e.target.value)}
              placeholder="e.g. Accurate metric citations, missing specific details..."
              className="w-full px-3.5 py-2 text-xs bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:bg-white"
            />

            <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100">
              <Button variant="secondary" size="sm" onClick={() => setFeedbackModal(null)}>
                Cancel
              </Button>
              <Button variant="primary" size="sm" onClick={handleFeedbackSubmit} className="bg-purple-600 hover:bg-purple-700">
                Submit Feedback
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
export default AiCopilotPage;
