import React from 'react';
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';

export const Toast = ({ message, type = 'success', onClose }) => {
  if (!message) return null;

  const icons = {
    success: <CheckCircle2 className="h-5 w-5 text-emerald-500 flex-shrink-0" />,
    error: <AlertCircle className="h-5 w-5 text-rose-500 flex-shrink-0" />,
    info: <Info className="h-5 w-5 text-blue-500 flex-shrink-0" />,
  };

  const borders = {
    success: 'border-emerald-200 bg-emerald-50 text-emerald-900',
    error: 'border-rose-200 bg-rose-50 text-rose-900',
    info: 'border-blue-200 bg-blue-50 text-blue-900',
  };

  return (
    <div className="fixed bottom-5 right-5 z-50 max-w-md animate-slide-up">
      <div className={`flex items-start p-4 rounded-xl border shadow-lg ${borders[type] || borders.info}`}>
        {icons[type] || icons.info}
        <div className="ml-3 mr-6 text-sm font-medium">{message}</div>
        {onClose && (
          <button onClick={onClose} className="ml-auto -mx-1.5 -my-1.5 p-1.5 rounded-lg hover:bg-black/5">
            <X className="h-4 w-4" />
          </button>
        )}
      </div>
    </div>
  );
};
