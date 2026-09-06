import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldAlert, ArrowLeft, Home } from 'lucide-react';
import { Button } from '../components/common/Button';

export function NotFoundPage() {
  return (
    <div className="min-h-screen bg-slate-900 flex items-center justify-center p-4">
      <div className="max-w-md w-full text-center bg-slate-800 border border-slate-700 rounded-2xl p-8 shadow-2xl">
        <div className="inline-flex items-center justify-center h-20 w-20 rounded-full bg-rose-500/10 text-rose-400 mb-6 border border-rose-500/20">
          <ShieldAlert className="h-10 w-10" />
        </div>

        <h1 className="text-6xl font-extrabold text-white tracking-tight mb-2">404</h1>
        <h2 className="text-xl font-bold text-slate-200 mb-3">Page Not Found</h2>
        <p className="text-sm text-slate-400 mb-8 leading-relaxed">
          The page you requested could not be located in the Supplier Performance Rating System. It may have been moved or does not exist.
        </p>

        <div className="flex flex-col sm:flex-row items-center justify-center gap-3">
          <Link to="/dashboard" className="w-full sm:w-auto">
            <Button variant="primary" className="w-full justify-center">
              <Home className="h-4 w-4 mr-2" />
              Return to Dashboard
            </Button>
          </Link>
          <Link to="/login" className="w-full sm:w-auto">
            <Button variant="outline" className="w-full justify-center border-slate-600 text-slate-300 hover:bg-slate-700">
              <ArrowLeft className="h-4 w-4 mr-2" />
              Go to Login
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
}

export default NotFoundPage;
