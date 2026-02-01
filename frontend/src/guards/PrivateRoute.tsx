import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { authStore } from '../stores';
import { Loading } from '../components';

interface PrivateRouteProps {
  children: React.ReactElement;
}

/**
 * Guard para proteger rotas privadas
 * Redireciona para login se não estiver autenticado
 */
const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);

  useEffect(() => {
    // Verifica autenticação inicial
    setIsAuthenticated(authStore.isAuthenticated());

    // Observa mudanças no estado de autenticação
    const subscription = authStore.currentUser$.subscribe(user => {
      setIsAuthenticated(user !== null && authStore.isAuthenticated());
    });

    return () => subscription.unsubscribe();
  }, []);

  // Loading enquanto verifica autenticação
  if (isAuthenticated === null) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Loading message="Verificando autenticação..." />
      </div>
    );
  }

  // Redireciona para login se não autenticado
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Renderiza componente filho se autenticado
  return children;
};

export default PrivateRoute;
