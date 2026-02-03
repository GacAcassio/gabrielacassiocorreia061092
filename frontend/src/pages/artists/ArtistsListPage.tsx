import React, { useEffect, useRef, useState } from 'react';
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
  const [searchLoading, setSearchLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [searchTerm, setSearchTerm] = useState('');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [username, setUsername] = useState('');

  // debounce mantém a busca "automática" enquanto digita
  const debouncedSearch = useDebounce(searchTerm, 500);

  // evita condição de corrida (requisição antiga sobrescrever a nova)
  const requestIdRef = useRef(0);

  useEffect(() => {
    // Observa usuário autenticado
    const authSub = authStore.currentUser$.subscribe(user => {
      setUsername(user?.username || '');
    });

    return () => authSub.unsubscribe();
  }, []);

  // quando o usuário digita, o debounce muda e a lista é filtrada após a consulta
  useEffect(() => {
    loadArtists();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedSearch, sortDirection, currentPage]);

  useEffect(() => {
    // Observa mudanças na store e atualiza a lista na tela
    const subscription = artistStore.state$.subscribe(state => {
      if (state.artists) {
        // aqui garante que a lista exibida é a lista filtrada que veio da API
        setArtists(state.artists.content);
        setTotalPages(state.artists.totalPages);
      }
      setLoading(state.loading);
      setError(state.error);
    });

    return () => subscription.unsubscribe();
  }, []);

  const loadArtists = async () => {
    const requestId = ++requestIdRef.current;

    const isSearching = !!debouncedSearch?.trim();
    if (isSearching) setSearchLoading(true);

    try {
      await artistFacade.list({
        page: currentPage,
        size: 12,
        name: debouncedSearch?.trim() || undefined, // ✅ envia o filtro pro backend
        sort: 'name',
        direction: sortDirection,
      });

      // ignora resposta antiga se já houve uma mais nova
      if (requestId !== requestIdRef.current) return;
    } catch (err) {
      console.error('Error loading artists:', err);
    } finally {
      if (requestId === requestIdRef.current) {
        setSearchLoading(false);
      }
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
    setCurrentPage(0); // reset para primeira página ao buscar
  };

  const handleClearSearch = () => {
    setSearchTerm('');
    setCurrentPage(0);
  };

  // botão de pesquisar (busca imediata sem esperar debounce)
  const handleSearchClick = () => {
    setCurrentPage(0);
    loadArtists();
  };

  const toggleSort = () => {
    setSortDirection(prev => (prev === 'asc' ? 'desc' : 'asc'));
    setCurrentPage(0);
  };

  const handlePageChange = (newPage: number) => {
    if (newPage < 0 || newPage >= totalPages) return;
    setCurrentPage(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const isFiltering = !!debouncedSearch?.trim();
  const disablePagination = loading || searchLoading;

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

            {/* Se quiser o botão de logout no header */}
            {/* <button
              onClick={handleLogout}
              className="px-6 py-2 rounded-lg border border-gray-300 hover:bg-gray-50 transition-colors"
            >
              Sair
            </button> */}
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

            <div className="flex gap-2">
              <div className="relative flex-1">
                <input
                  id="search"
                  type="text"
                  placeholder="Digite o nome do artista..."
                  value={searchTerm}
                  onChange={handleSearchChange}
                  className="w-full px-4 py-2 pr-10 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                />

                {/* Botão limpar */}
                {searchTerm && (
                  <button
                    type="button"
                    onClick={handleClearSearch}
                    className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                    title="Limpar busca"
                  >
                    ✕
                  </button>
                )}
              </div>

              <button
                type="button"
                onClick={handleSearchClick}
                disabled={searchLoading}
                className="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg font-semibold transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {searchLoading ? 'Buscando...' : 'Pesquisar'}
              </button>
            </div>

            <div className="mt-2 flex items-center gap-2 text-xs text-gray-500">
              {searchLoading && (
                <span className="text-primary-600 font-medium">Buscando...</span>
              )}
              {!searchLoading && searchTerm && (
                <span>Você pode clicar em “Pesquisar” para aplicar imediatamente.</span>
              )}
            </div>
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

        {/* Resumo */}
        <div className="mt-4 flex items-center justify-between text-sm text-gray-600">
          <span>
            {isFiltering ? (
              <>
                Filtrando por: <span className="font-semibold">"{debouncedSearch.trim()}"</span>
              </>
            ) : (
              'Exibindo todos os artistas'
            )}
          </span>

          <span>
            Resultados nesta página: <span className="font-semibold">{artists.length}</span>
          </span>
        </div>
      </div>

      {/* Loading inicial */}
      {loading && artists.length === 0 && (
        <div className="bg-white shadow-md rounded-lg p-12">
          <Loading message="Carregando artistas..." />
        </div>
      )}

      {/* Error */}
      {error && !loading && (
        <ErrorMessage message={error} onRetry={loadArtists} />
      )}

      {/* Nenhum resultado */}
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

          <h3 className="mt-2 text-lg font-medium text-gray-900">
            Nenhum artista encontrado
          </h3>

          <p className="mt-1 text-sm text-gray-500">
            {searchTerm ? 'Tente buscar com outros termos.' : 'Comece criando um novo artista.'}
          </p>

          <div className="mt-6 flex justify-center gap-2">
            {searchTerm && (
              <button
                onClick={handleClearSearch}
                className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md text-sm font-medium hover:bg-gray-50"
              >
                Limpar busca
              </button>
            )}

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

      {/* Lista */}
      {!loading && !error && artists.length > 0 && (
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
                  disabled={currentPage === 0 || disablePagination}
                  className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors"
                >
                  Anterior
                </button>

                <span className="text-sm text-gray-600">
                  Página {currentPage + 1} de {totalPages}
                </span>

                <button
                  onClick={() => handlePageChange(currentPage + 1)}
                  disabled={currentPage >= totalPages - 1 || disablePagination}
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
