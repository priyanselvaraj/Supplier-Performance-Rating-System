import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export const ProtectedRoute = ({ children, requiredRole }) => {
  const { user, token, loading, isAdmin, isSupplier } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!token || !user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (requiredRole === 'ADMIN' && !isAdmin()) {
    return <Navigate to={isSupplier() ? "/supplier-portal/dashboard" : "/dashboard"} replace />;
  }

  // If a supplier user attempts to access internal procurement routes directly
  if (isSupplier() && !location.pathname.startsWith('/supplier-portal')) {
    if (location.pathname === '/profile') {
      return <Navigate to="/supplier-portal/account" replace />;
    }
    return <Navigate to="/supplier-portal/dashboard" replace />;
  }

  // If internal users (non-supplier) attempt to access supplier portal without supplier role
  if (!isSupplier() && location.pathname.startsWith('/supplier-portal') && !location.pathname.startsWith('/supplier-portal/admin') && !isAdmin() && !user?.supplierId) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};
