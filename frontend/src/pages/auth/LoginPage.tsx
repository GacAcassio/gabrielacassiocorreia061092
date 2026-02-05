import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authFacade } from '../../services/facades';
import { authStore } from '../../stores';
import { Loading } from '../../components';
import { config } from '../../config/config';

/**
 * Página de Login
 */
const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    // Se já estiver autenticado, redireciona para artistas
    const subscription = authStore.currentUser$.subscribe(user => {
      if (user && authStore.isAuthenticated()) {
        navigate('/artists');
      }
    });

    return () => subscription.unsubscribe();
  }, [navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await authFacade.login({ username, password });
      navigate('/artists');
    } catch (err: any) {
      setError(err.message || 'Erro ao fazer login. Verifique suas credenciais.');
    } finally {
      setLoading(false);
    }
  };

  // Auto-preencher com credenciais padrão (apenas para desenvolvimento)
  const fillDefaultCredentials = () => {
    setUsername('admin');
    setPassword('admin123');
  };

  return (
    <div className="min-h-[calc(100vh-200px)] flex items-center justify-center py-12 px-4">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Faça login na sua conta
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Sistema de Gerenciamento de Artistas
          </p>
        </div>

        <div className="bg-white shadow-lg rounded-lg p-8">
          {/* Info de Debug */}
          <div className="mb-4 bg-blue-50 border border-blue-200 rounded-lg p-3">
            <p className="text-xs text-blue-800">
              <strong>API:</strong> {config.api.baseURL}
            </p>
            <p className="text-xs text-blue-600 mt-1">
              Certifique-se que o backend está rodando!
            </p>
          </div>

          <form className="space-y-6" onSubmit={handleSubmit}>
            {error && (
              <div className="bg-red-50 border border-red-400 text-red-700 px-4 py-3 rounded relative">
                <strong className="font-bold">Erro: </strong>
                <span className="block sm:inline">{error}</span>
                
                {error.includes('Servidor não respondeu') && (
                  <div className="mt-2 text-sm">
                    <p className="font-semibold">Possíveis soluções:</p>
                    <ul className="list-disc list-inside mt-1">
                      <li>Verifique se o backend está rodando</li>
                      <li>Confirme a URL: {config.api.baseURL}</li>
                      <li>Verifique o CORS no backend</li>
                    </ul>
                  </div>
                )}
              </div>
            )}

            <div>
              <label htmlFor="username" className="block text-sm font-medium text-gray-700">
                Usuário
              </label>
              <input
                id="username"
                name="username"
                type="text"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="mt-1 appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-primary-500 focus:border-primary-500 focus:z-10 sm:text-sm"
                placeholder="Digite seu usuário"
                disabled={loading}
              />
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                Senha
              </label>
              <input
                id="password"
                name="password"
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="mt-1 appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-primary-500 focus:border-primary-500 focus:z-10 sm:text-sm"
                placeholder="Digite sua senha"
                disabled={loading}
              />
            </div>

            <div>
              <button
                type="submit"
                disabled={loading}
                className="w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {loading ? <Loading size="small" message="" /> : 'Entrar'}
              </button>
            </div>
          </form>

          <div className="mt-6">
            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-gray-300" />
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-white text-gray-500">Credenciais padrão</span>
              </div>
            </div>

            <div className="mt-4 text-center text-sm text-gray-600 bg-gray-50 rounded-lg p-4">
              <p className="font-semibold mb-2">Para testes:</p>
              <p><strong>Usuário:</strong> admin</p>
              <p><strong>Senha:</strong> admin123</p>
              
              <button
                type="button"
                onClick={fillDefaultCredentials}
                className="mt-3 text-primary-600 hover:text-primary-700 font-semibold text-sm"
              >
                Preencher automaticamente →
              </button>
            </div>
          </div>

          {/* Link para voltar */}
          <div className="mt-6 text-center">
            <button
              onClick={() => navigate('/')}
              className="text-sm text-gray-600 hover:text-gray-800"
            >
              ← Voltar para home
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
