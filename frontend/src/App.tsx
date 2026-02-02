import React, { useEffect } from 'react';
import { BrowserRouter, Routes, Route, Link, useLocation, Navigate, useNavigate } from 'react-router-dom';
import { authFacade } from './services/facades';
import { authStore } from './stores';
import { PrivateRoute } from './guards';
import { NotificationContainer } from './components';
import {
  LoginPage,
  ArtistsListPage,
  ArtistDetailPage,
  ArtistFormPage,
  AlbumsListPage,
  AlbumDetailPage,
  AlbumFormPage,
} from './pages';
import './App.css';

/**
 * Componente de conteúdo da Home
 */
const HomePage: React.FC = () => {
  const navigate = useNavigate();
  
  return (
    <div className="text-center mt-10">
      <h2 className="text-3xl font-bold text-gray-800 mb-4">
        Projeto Seplag
      </h2>
      <p className="text-gray-600 mb-8">
        Sprint 8
      </p>
      
      <div className="max-w-2xl mx-auto">
        <div className="bg-gradient-to-r from-primary-500 to-purple-600 text-white rounded-lg shadow-lg p-8">
          <h3 className="text-2xl font-bold mb-3"> Hello world!</h3>
          <p className="mb-6">
            ...
          </p>
          <button
            onClick={() => navigate('/login')}
            className="bg-white text-primary-600 px-8 py-3 rounded-lg font-bold hover:bg-gray-100 transition-colors shadow-md"
          >
            Fazer Login →
          </button>
        </div>
      </div>
    </div>
  );
};

/**
 * Componente de Header com navegação
 */
const Header: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [isAuthenticated, setIsAuthenticated] = React.useState(false);
  
  React.useEffect(() => {
    const subscription = authStore.currentUser$.subscribe(user => {
      setIsAuthenticated(user !== null);
    });
    return () => subscription.unsubscribe();
  }, []);

  const handleLogout = () => {
    authFacade.logout();
    navigate('/home');
  };

  const isActive = (path: string) => {
    if (path === '/') return location.pathname === '/';
    return location.pathname.startsWith(path);
  };
  
  const linkClass = (path: string) => `
    px-4 py-2 rounded-lg transition-colors font-medium
    ${isActive(path) 
      ? 'bg-white text-primary-600' 
      : 'text-white hover:bg-primary-700'
    }
  `;

  return (
    <header className="bg-primary-600 text-white shadow-lg">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between py-4">
          <Link to="/" className="text-2xl font-bold hover:text-primary-100 transition-colors">
            Album repo
          </Link>
          
          <nav className="flex items-center space-x-2">
            {!isAuthenticated ? (
              <>
                <Link to="/" className={linkClass('/')}>
                  Home
                </Link>
                <Link to="/login" className={linkClass('/login')}>
                  Login
                </Link>
              </>
            ) : (
              <>
                <Link to="/artists" className={linkClass('/artists')}>
                  Artistas
                </Link>
                <Link to="/albums" className={linkClass('/albums')}>
                  Álbuns
                </Link>
                <button
                  onClick={handleLogout}
                  className="px-4 py-2 rounded-lg transition-colors font-medium text-white hover:bg-red-600 bg-red-500"
                >
                  Logout
                </button>
              </>
            )}
          </nav>
        </div>
      </div>
    </header>
  );
};

/**
 * Componente raiz da aplicação
 */
function App() {
  useEffect(() => {
    authFacade.initialize();

    // Listener para token expirado
    const handleTokenExpired = () => {
      authFacade.logout();
      window.location.href = '/home';
    };

    window.addEventListener('auth:logout', handleTokenExpired);

    return () => {
      window.removeEventListener('auth:logout', handleTokenExpired);
    };
  }, []);

  return (
    <BrowserRouter>
      <div className="App min-h-screen bg-gray-50">
        {/* Sistema de Notificações */}
        <NotificationContainer />
        <Header />
        
        <main className="container mx-auto p-4 min-h-[calc(100vh-180px)]">
          <Routes>
            {/* Home */}
            <Route path="/" element={<HomePage />} />
            
            {/* Auth */}
            <Route path="/login" element={<LoginPage />} />
            
            {/* TODAS AS ROTAS PROTEGIDAS */}
            <Route path="/artists" element={<PrivateRoute><ArtistsListPage /></PrivateRoute>} />
            <Route path="/artists/new" element={<PrivateRoute><ArtistFormPage /></PrivateRoute>} />
            <Route path="/artists/:id" element={<PrivateRoute><ArtistDetailPage /></PrivateRoute>} />
            <Route path="/artists/:id/edit" element={<PrivateRoute><ArtistFormPage /></PrivateRoute>} />
            
            <Route path="/albums" element={<PrivateRoute><AlbumsListPage /></PrivateRoute>} />
            <Route path="/albums/new" element={<PrivateRoute><AlbumFormPage /></PrivateRoute>} />
            <Route path="/albums/:id" element={<PrivateRoute><AlbumDetailPage /></PrivateRoute>} />
            <Route path="/albums/:id/edit" element={<PrivateRoute><AlbumFormPage /></PrivateRoute>} />

            {/* 404 */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>

        <footer className="bg-gray-800 text-white py-6 mt-16">
          <div className="container mx-auto px-4 text-center">
            <p className="text-sm">Album repo © 2026</p>
            <p className="text-xs text-gray-400 mt-2">
              Gabriel Acassio Correia
            </p>
          </div>
        </footer>
      </div>
    </BrowserRouter>
  );
}

export default App;
