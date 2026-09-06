import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/auth.service';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(authService.getStoredUser());
  const [token, setToken] = useState(authService.getToken());
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = async () => {
      const storedToken = authService.getToken();
      if (storedToken) {
        try {
          const res = await authService.getCurrentUser();
          if (res.success && res.data) {
            setUser(res.data);
            localStorage.setItem('user', JSON.stringify(res.data));
          }
        } catch (error) {
          console.error('Session restore failed:', error);
          authService.logout();
          setUser(null);
          setToken(null);
        }
      }
      setLoading(false);
    };

    initializeAuth();
  }, []);

  const login = async (credentials) => {
    const response = await authService.login(credentials);
    if (response.success && response.data) {
      setUser(response.data);
      setToken(response.data.token);
      return response.data;
    }
    throw new Error(response.message || 'Login failed');
  };

  const register = async (userData) => {
    return await authService.register(userData);
  };

  const updateProfile = async (profileData) => {
    const res = await authService.updateProfile(profileData);
    if (res.success && res.data) {
      setUser((prev) => ({ ...prev, ...res.data }));
      return res.data;
    }
    throw new Error(res.message || 'Profile update failed');
  };

  const logout = () => {
    authService.logout();
    setUser(null);
    setToken(null);
    window.location.href = '/login';
  };

  const isAdmin = () => {
    if (!user || !user.roles) return false;
    return user.roles.some(r => r === 'ROLE_ADMIN' || r === 'ADMIN' || r.toLowerCase() === 'admin');
  };

  const isManager = () => {
    if (!user || !user.roles) return false;
    return user.roles.some(r => r === 'ROLE_MANAGER' || r === 'MANAGER' || r.toLowerCase() === 'manager' || r === 'ROLE_ADMIN' || r === 'ADMIN');
  };

  const isSupplier = () => {
    if (!user || !user.roles) return false;
    return user.roles.some(r => r === 'ROLE_SUPPLIER' || r === 'SUPPLIER' || r.toLowerCase() === 'supplier');
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, updateProfile, logout, isAdmin, isManager, isSupplier }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
