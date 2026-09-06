import React from 'react';

export const Badge = ({ children, variant = 'default', size = 'sm', className = '' }) => {
  const getVariantStyles = (v) => {
    const val = String(v || '').toUpperCase();

    switch (val) {
      case 'EXCELLENT':
      case 'SUCCESS':
      case 'ACTIVE':
        return 'bg-emerald-50 text-emerald-700 border-emerald-200';
      case 'GOOD':
      case 'INFO':
      case 'ROLE_MANAGER':
      case 'MANAGER':
        return 'bg-blue-50 text-blue-700 border-blue-200';
      case 'AVERAGE':
      case 'WARNING':
      case 'PENDING_REVIEW':
        return 'bg-amber-50 text-amber-700 border-amber-200';
      case 'POOR':
      case 'DANGER':
      case 'INACTIVE':
        return 'bg-rose-50 text-rose-700 border-rose-200';
      case 'ROLE_ADMIN':
      case 'ADMIN':
        return 'bg-purple-50 text-purple-700 border-purple-200';
      case 'UNRATED':
      default:
        return 'bg-slate-100 text-slate-700 border-slate-200';
    }
  };

  const sizes = {
    xs: 'px-2 py-0.5 text-[10px]',
    sm: 'px-2.5 py-0.5 text-xs',
    md: 'px-3 py-1 text-sm',
  };

  return (
    <span
      className={`inline-flex items-center font-medium rounded-full border ${getVariantStyles(variant)} ${sizes[size] || sizes.sm} ${className}`}
    >
      {children || variant}
    </span>
  );
};export default Badge;
