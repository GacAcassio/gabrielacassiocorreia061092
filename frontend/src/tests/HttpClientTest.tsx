import React, { useState, useEffect } from 'react';
import { authFacade, artistFacade } from '../services/facades';
import { authStore, artistStore } from '../stores';

/**
 * Componente para testar HttpClient, Services e Facades
 */
const HttpClientTest: React.FC = () => {
  const [status, setStatus] = useState('Aguardando teste...');
  const [user, setUser] = useState<any>(null);
  const [artists, setArtists] = useState<any>(null);

  useEffect(() => {
    // Subscribe to auth store
    const authSub = authStore.currentUser$.subscribe(currentUser => {
      setUser(currentUser);
    });

    // Subscribe to artist store
    const artistSub = artistStore.state$.subscribe(state => {
      setArtists(state.artists);
    });

    return () => {
      authSub.unsubscribe();
      artistSub.unsubscribe();
    };
  }, []);

  // Teste 1: Login
  const testLogin = async () => {
    setStatus('Testando login...');
    try {
      await authFacade.login({
        username: 'admin',
        password: 'admin123'
      });
      setStatus('Login OK! Token salvo no localStorage e AuthStore atualizada.');
    } catch (error: any) {
      setStatus(`Login falhou: ${error.message}`);
    }
  };

  // Teste 2: Listar artistas (requer autenticação)
  const testListArtists = async () => {
    setStatus('Testando listagem de artistas (verifica se token está sendo enviado)...');
    try {
      await artistFacade.list({ page: 0, size: 10 });
      setStatus('Listagem OK! Interceptor adicionou o token automaticamente.');
    } catch (error: any) {
      setStatus(`Listagem falhou: ${error.message}`);
    }
  };

  // Teste 3: Buscar por nome
  const testSearchArtists = async () => {
    setStatus('Testando busca de artistas por nome...');
    try {
      await artistFacade.list({ page: 0, size: 10, name: 'Beatles' });
      setStatus('Busca OK!');
    } catch (error: any) {
      setStatus(`Busca falhou: ${error.message}`);
    }
  };

  // Teste 4: Testar renovação de token
  const testTokenRefresh = async () => {
    setStatus('Testando renovação manual de token...');
    try {
      await authFacade.refreshToken();
      setStatus('Renovação OK! Novo token salvo.');
    } catch (error: any) {
      setStatus(`Renovação falhou: ${error.message}`);
    }
  };

  // Teste 5: Testar requisição sem autenticação (deve falhar)
  const testUnauthorized = async () => {
    setStatus('estando requisição sem token (deve falhar com 401)...');
    authFacade.logout();
    try {
      await artistFacade.list();
      setStatus('Teste falhou: deveria ter retornado 401');
    } catch (error: any) {
      setStatus(' OK! Retornou erro como esperado: ' + error.message);
    }
  };

  // Teste 6: Logout
  const testLogout = () => {
    authFacade.logout();
    setStatus('Logout OK! LocalStorage limpo e AuthStore atualizada.');
  };

  return (
    <div className="max-w-4xl mx-auto p-8">
      <div className="bg-white shadow-lg rounded-lg p-6">
        <h2 className="text-3xl font-bold mb-6 text-gray-800">
           Testes de Integração
        </h2>
        
        <div className="mb-8 p-4 bg-blue-50 border border-blue-200 rounded">
          <p className="text-sm text-blue-800">
            <strong>Instruções:</strong> Execute os testes na ordem. Certifique-se que o backend está rodando em <code>http://localhost:8080</code>
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
          <button 
            onClick={testLogin}
            className="bg-blue-500 hover:bg-blue-600 text-white font-semibold px-6 py-3 rounded-lg transition-colors"
          >
            1️⃣ Testar Login
          </button>
          
          <button 
            onClick={testListArtists}
            className="bg-green-500 hover:bg-green-600 text-white font-semibold px-6 py-3 rounded-lg transition-colors"
          >
            2️⃣ Listar Artistas
          </button>
          
          <button 
            onClick={testSearchArtists}
            className="bg-purple-500 hover:bg-purple-600 text-white font-semibold px-6 py-3 rounded-lg transition-colors"
          >
            3️⃣ Buscar Artistas
          </button>
          
          <button 
            onClick={testTokenRefresh}
            className="bg-yellow-500 hover:bg-yellow-600 text-white font-semibold px-6 py-3 rounded-lg transition-colors"
          >
            4️⃣ Renovar Token
          </button>
          
          <button 
            onClick={testUnauthorized}
            className="bg-orange-500 hover:bg-orange-600 text-white font-semibold px-6 py-3 rounded-lg transition-colors"
          >
            5️⃣ Testar 401
          </button>
          
          <button 
            onClick={testLogout}
            className="bg-red-500 hover:bg-red-600 text-white font-semibold px-6 py-3 rounded-lg transition-colors"
          >
            6️⃣ Logout
          </button>
        </div>

        {/* Status */}
        <div className="mb-8 p-4 bg-gray-100 rounded-lg">
          <h3 className="font-bold mb-2 text-gray-700">Status do Teste:</h3>
          <p className="text-gray-800">{status}</p>
        </div>

        {/* Estado Atual */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <h3 className="font-bold mb-2 text-gray-700">Estado AuthStore:</h3>
            <div className="bg-gray-900 text-green-400 p-4 rounded-lg font-mono text-sm overflow-auto max-h-64">
              <pre>{JSON.stringify({
                isAuthenticated: authStore.isAuthenticated(),
                user: user ? {
                  username: user.username,
                  hasToken: !!user.token,
                  hasRefreshToken: !!user.refreshToken,
                  expiresAt: new Date(user.expiresAt).toLocaleString()
                } : null
              }, null, 2)}</pre>
            </div>
          </div>

          <div>
            <h3 className="font-bold mb-2 text-gray-700">Estado ArtistStore:</h3>
            <div className="bg-gray-900 text-green-400 p-4 rounded-lg font-mono text-sm overflow-auto max-h-64">
              <pre>{JSON.stringify({
                totalArtists: artists?.totalElements || 0,
                currentPage: artists?.pageNumber || 0,
                loading: artistStore.currentState.loading,
                error: artistStore.currentState.error,
              }, null, 2)}</pre>
            </div>
          </div>
        </div>

        {/* LocalStorage */}
        <div className="mt-8">
          <h3 className="font-bold mb-2 text-gray-700">LocalStorage:</h3>
          <div className="bg-gray-900 text-green-400 p-4 rounded-lg font-mono text-sm overflow-auto max-h-64">
            <pre>{JSON.stringify({
              access_token: localStorage.getItem('access_token') ? '✓ Presente' : '✗ Ausente',
              refresh_token: localStorage.getItem('refresh_token') ? '✓ Presente' : '✗ Ausente',
              expires_at: localStorage.getItem('expires_at') 
                ? new Date(parseInt(localStorage.getItem('expires_at')!)).toLocaleString()
                : '✗ Ausente',
              username: localStorage.getItem('username') || '✗ Ausente',
            }, null, 2)}</pre>
          </div>
        </div>

        {/* Lista de Artistas (se houver) */}
        {artists && artists.content && artists.content.length > 0 && (
          <div className="mt-8">
            <h3 className="font-bold mb-2 text-gray-700">Artistas Carregados:</h3>
            <div className="bg-white border rounded-lg overflow-hidden">
              <table className="min-w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-2 text-left">ID</th>
                    <th className="px-4 py-2 text-left">Nome</th>
                    <th className="px-4 py-2 text-left">Álbuns</th>
                  </tr>
                </thead>
                <tbody>
                  {artists.content.map((artist: any) => (
                    <tr key={artist.id} className="border-t">
                      <td className="px-4 py-2">{artist.id}</td>
                      <td className="px-4 py-2">{artist.name}</td>
                      <td className="px-4 py-2">{artist.albumCount}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default HttpClientTest;
