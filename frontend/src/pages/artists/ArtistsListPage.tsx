import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { artistFacade, authFacade } from '../../services/facades';
import { artistStore, authStore } from '../../stores';
import { Loading, ErrorMessage } from '../../components';
import ArtistCard from '../../components/ArtistCard';
import { useDebounce } from '../../hooks/useDebounce';
import { ArtistSummary } from '../../models';

/**
 * Página de listagem de artistas
 */
const ArtistsListPage: React.FC = () => {
  const navigate = useNavigate();
  const [artists, setArtists] = useState<ArtistSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [username, setUsername] = useState('');

  const debouncedSearch = useDebounce(searchTerm, 500);

  useEffect(() => {
    // Observa usuário autenticado
    const authSub = authStore.currentUser$.subscribe(user => {
      setUsername(user?.username || '');
    });

    return () => authSub.unsubscribe();
  }, []);

  useEffect(() => {
    loadArtists();
  }, [debouncedSearch, sortDirection, currentPage]);

  useEffect(() => {
    // Observa mudanças na store
    const subscription = artistStore.state$.subscribe(state => {
      if (state.artists) {
        setArtists(state.artists.content);
        setTotalPages(state.artists.totalPages);
      }
      setLoading(state.loading);
      setError(state.error);
    });

    return () => subscription.unsubscribe();
  }, []);

  const loadArtists = async () => {
    try {
      await artistFacade.list({
        page: currentPage,
        size: 12,
        name: debouncedSearch || undefined,
        sort: 'name',
        direction: sortDirection,
      });
    } catch (err) {
      console.error('Error loading artists:', err);
    }
  };

  const handleLogout = () => {
    authFacade.logout();
    navigate('/login');
  };

  const handleCreateArtist = () => {
    navigate('/artists/new');
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
    setCurrentPage(0); // Reset para primeira página ao buscar
  };

  const toggleSort = () => {
    setSortDirection(prev => prev === 'asc' ? 'desc' : 'asc');
  };

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="max-w-7xl mx-auto">
      {/* Header */}
      <div className="bg-white shadow-md rounded-lg p-6 mb-6">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div>
            <h1 className="text-3xl font-bold text-gray-800">Artistas</h1>
            <p className="text-gray-600 mt-1">
              Bem-vindo, <span className="font-semibold">{username}</span>
            </p>
          </div>
          <div className="flex flex-wrap gap-2">
            <button
              onClick={handleCreateArtist}
              className="bg-primary-600 hover:bg-primary-700 text-white px-6 py-2 rounded-lg font-semibold transition-colors flex items-center"
            >
              <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
              </svg>
              Novo Artista
            </button>
            <button
              onClick={handleLogout}
              className="bg-red-600 hover:bg-red-700 text-white px-6 py-2 rounded-lg font-semibold transition-colors"
            >
              Sair
            </button>
          </div>
        </div>
      </div>

      {/* Filtros */}
      <div className="bg-white shadow-md rounded-lg p-6 mb-6">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="flex-1">
            <label htmlFor="search" className="block text-sm font-medium text-gray-700 mb-2">
              Buscar por nome
            </label>
            <input
              id="search"
              type="text"
              placeholder="Digite o nome do artista..."
              value={searchTerm}
              onChange={handleSearchChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Ordenação
            </label>
            <button
              onClick={toggleSort}
              className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors flex items-center space-x-2"
            >
              <span>{sortDirection === 'asc' ? 'A-Z' : 'Z-A'}</span>
              <svg
                className={`w-4 h-4 transition-transform ${sortDirection === 'desc' ? 'rotate-180' : ''}`}
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
              </svg>
            </button>
          </div>
        </div>
      </div>

      {/* Loading */}
      {loading && !artists.length && (
        <div className="bg-white shadow-md rounded-lg p-12">
          <Loading message="Carregando artistas..." />
        </div>
      )}

      {/* Error */}
      {error && !loading && (
        <ErrorMessage message={error} onRetry={loadArtists} />
      )}

      {/* Lista de Artistas */}
      {!loading && !error && artists.length === 0 && (
        <div className="bg-white shadow-md rounded-lg p-12 text-center">
          <svg
            className="mx-auto h-12 w-12 text-gray-400"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <h3 className="mt-2 text-lg font-medium text-gray-900">Nenhum artista encontrado</h3>
          <p className="mt-1 text-sm text-gray-500">
            {searchTerm
              ? 'Tente buscar com outros termos.'
              : 'Comece criando um novo artista.'}
          </p>
          <div className="mt-6">
            <button
              onClick={handleCreateArtist}
              className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-primary-600 hover:bg-primary-700"
            >
              <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
              </svg>
              Novo Artista
            </button>
          </div>
        </div>
      )}

      {!loading && artists.length > 0 && (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
            {artists.map(artist => (
              <ArtistCard key={artist.id} artist={artist} />
            ))}
          </div>

          {/* Paginação */}
          {totalPages > 1 && (
            <div className="bg-white shadow-md rounded-lg p-4">
              <div className="flex items-center justify-between">
                <button
                  onClick={() => handlePageChange(currentPage - 1)}
                  disabled={currentPage === 0}
                  className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors"
                >
                  Anterior
                </button>

                <span className="text-sm text-gray-600">
                  Página {currentPage + 1} de {totalPages}
                </span>

                <button
                  onClick={() => handlePageChange(currentPage + 1)}
                  disabled={currentPage >= totalPages - 1}
                  className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors"
                >
                  Próxima
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default ArtistsListPage;
