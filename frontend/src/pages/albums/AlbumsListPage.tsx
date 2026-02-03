import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { albumFacade } from '../../services/facades';
import { albumStore, authStore } from '../../stores';
import { Loading, ErrorMessage } from '../../components';
import { AlbumSummary } from '../../models/Artist';
import { useDebounce } from '../../hooks/useDebounce';

type SearchMode = 'title' | 'artist';

/**
 * Página de listagem de álbuns (PÚBLICA - todos podem ver)
 * Botões de ação aparecem apenas para usuários logados
 */
const AlbumsListPage: React.FC = () => {
  const navigate = useNavigate();

  const [albums, setAlbums] = useState<AlbumSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchLoading, setSearchLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Pesquisa
  const [searchTerm, setSearchTerm] = useState('');
  const [searchMode, setSearchMode] = useState<SearchMode>('title');

  // debounce (busca automática)
  const debouncedSearch = useDebounce(searchTerm, 500);

  // evita condição de corrida (resposta antiga sobrescrever nova)
  const requestIdRef = useRef(0);

  const isFiltering = !!debouncedSearch.trim();

  useEffect(() => {
    // Observa autenticação
    const authSub = authStore.currentUser$.subscribe(user => {
      setIsAuthenticated(user !== null);
    });

    return () => authSub.unsubscribe();
  }, []);

  useEffect(() => {
    // Observa store de álbuns
    const subscription = albumStore.state$.subscribe(state => {
      if (state.albums) {
        setAlbums(state.albums.content);
        setTotalPages(state.albums.totalPages);
      }
      setLoading(state.loading);
      setError(state.error);
    });

    return () => subscription.unsubscribe();
  }, []);

  // Sempre que trocar página, termo (debounced) ou modo, carrega
  useEffect(() => {
    loadAlbums();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, debouncedSearch, searchMode]);

  const loadAlbums = async () => {
    const requestId = ++requestIdRef.current;

    try {
      setSearchLoading(true);

      const term = debouncedSearch.trim();

      // Se tem termo -> filtra
      if (term.length > 0) {
        if (searchMode === 'title') {
          await albumFacade.searchByTitle({
            page: currentPage,
            size: 12,
            title: term,
          });
        } else {
          await albumFacade.searchByArtistName({
            page: currentPage,
            size: 12,
            name: term,
          });
        }

        if (requestId !== requestIdRef.current) return;
        return;
      }

      // Caso contrário lista normal
      await albumFacade.list({
        page: currentPage,
        size: 12,
      });

      if (requestId !== requestIdRef.current) return;
    } catch (err) {
      console.error('Error loading albums:', err);
    } finally {
      if (requestId === requestIdRef.current) {
        setSearchLoading(false);
      }
    }
  };

  const handleClearSearch = () => {
    setSearchTerm('');
    setCurrentPage(0);
  };

  const handleSearchClick = () => {
    // busca manual imediata (sem esperar debounce)
    setCurrentPage(0);
    loadAlbums();
  };

  const handlePageChange = (newPage: number) => {
    if (newPage < 0 || newPage >= totalPages) return;

    setCurrentPage(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const disablePagination = loading || searchLoading;

  return (
    <div className="max-w-7xl mx-auto">
      {/* Header */}
      <div className="bg-white shadow-md rounded-lg p-6 mb-6">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div>
            <h1 className="text-3xl font-bold text-gray-800">Álbuns</h1>
            {isAuthenticated ? (
              <p className="text-gray-600 mt-1">Gerencie sua coleção de álbuns</p>
            ) : (
              <p className="text-gray-600 mt-1">
                Explore os álbuns.{' '}
                <button
                  onClick={() => navigate('/login')}
                  className="text-primary-600 hover:underline font-semibold"
                >
                  Faça login
                </button>{' '}
                para gerenciá-los.
              </p>
            )}
          </div>

          {isAuthenticated && (
            <button
              onClick={() => navigate('/albums/new')}
              className="bg-primary-600 hover:bg-primary-700 text-white px-6 py-2 rounded-lg font-semibold transition-colors flex items-center"
            >
              <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
              </svg>
              Novo Álbum
            </button>
          )}
        </div>
      </div>

      {/*  Filtros de Pesquisa */}
      <div className="bg-white shadow-md rounded-lg p-6 mb-6">
        <div className="flex flex-col md:flex-row gap-4 items-end">
          <div className="relative flex-1">
            <label htmlFor="search" className="block text-sm font-medium text-gray-700 mb-2">
              Buscar
            </label>

            <input
              id="search"
              type="text"
              placeholder={searchMode === 'title' ? 'Digite o título do álbum...' : 'Digite o nome do artista...'}
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setCurrentPage(0);
              }}
              className="w-full px-4 py-2 pr-10 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            />

            {/* Botão limpar (corrigido) */}
            {searchTerm.trim().length > 0 && (
              <button
                type="button"
                onClick={handleClearSearch}
                className="absolute right-3 top-[42px] text-gray-400 hover:text-gray-600"
                title="Limpar busca"
              >
                ✕
              </button>
            )}
          </div>

          <div className="min-w-[200px]">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Tipo de busca
            </label>
            <select
              value={searchMode}
              onChange={(e) => {
                setSearchMode(e.target.value as SearchMode);
                setCurrentPage(0);
              }}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-primary-500"
            >
              <option value="title">Título</option>
              <option value="artist">Artista</option>
            </select>
          </div>

          <div className="flex gap-2">
            <button
              onClick={handleSearchClick}
              disabled={searchLoading}
              className="bg-primary-600 hover:bg-primary-700 text-white px-6 py-2 rounded-lg font-semibold transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {searchLoading ? 'Buscando...' : 'Pesquisar'}
            </button>
          </div>
        </div>

        {/* Resumo */}
        <div className="mt-4 flex items-center justify-between text-sm text-gray-600">
          <span>
            {isFiltering ? (
              <>
                Filtrando por: <span className="font-semibold">"{debouncedSearch.trim()}"</span> (
                {searchMode === 'title' ? 'Título' : 'Artista'})
              </>
            ) : (
              'Exibindo todos os álbuns'
            )}
          </span>

          <span>
            Resultados nesta página: <span className="font-semibold">{albums.length}</span>
          </span>
        </div>
      </div>

      {/* Loading */}
      {loading && !albums.length && (
        <div className="bg-white shadow-md rounded-lg p-12">
          <Loading message="Carregando álbuns..." />
        </div>
      )}

      {/* Error */}
      {error && !loading && (
        <ErrorMessage message={error} onRetry={loadAlbums} />
      )}

      {/* Empty */}
      {!loading && !error && albums.length === 0 && (
        <div className="bg-white shadow-md rounded-lg p-12 text-center">
          <h3 className="text-lg font-medium text-gray-900">Nenhum álbum encontrado</h3>
          <p className="mt-1 text-sm text-gray-500">
            Tente outro termo de busca.
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

            {isAuthenticated && (
              <button
                onClick={() => navigate('/albums/new')}
                className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-primary-600 hover:bg-primary-700"
              >
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                </svg>
                Novo Álbum
              </button>
            )}
          </div>
        </div>
      )}

      {/* Lista */}
      {!loading && albums.length > 0 && (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mb-6">
            {albums.map(album => (
              <div
                key={album.id}
                onClick={() => navigate(`/albums/${album.id}`)}
                className="bg-white rounded-lg shadow-md hover:shadow-xl transition-shadow cursor-pointer overflow-hidden"
              >
                {album.coverUrl ? (
                  <img src={album.coverUrl} alt={album.title} className="w-full h-48 object-cover" />
                ) : (
                  <div className="w-full h-48 bg-gradient-to-br from-gray-200 to-gray-300 flex items-center justify-center">
                    <svg className="w-16 h-16 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M18 3a1 1 0 00-1.196-.98l-10 2A1 1 0 006 5v9.114A4.369 4.369 0 005 14c-1.657 0-3 .895-3 2s1.343 2 3 2 3-.895 3-2V7.82l8-1.6v5.894A4.37 4.37 0 0015 12c-1.657 0-3 .895-3 2s1.343 2 3 2 3-.895 3-2V3z" />
                    </svg>
                  </div>
                )}

                <div className="p-4">
                  <h3 className="font-semibold text-gray-800 truncate">{album.title}</h3>
                  <p className="text-sm text-gray-600 mt-1">
                    {album.artistNames && album.artistNames.length > 0
                      ? album.artistNames.join(', ')
                      : 'Sem artista'}
                  </p>
                  {album.releaseYear && (
                    <p className="text-xs text-gray-500 mt-1">{album.releaseYear}</p>
                  )}
                </div>
              </div>
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

export default AlbumsListPage;
