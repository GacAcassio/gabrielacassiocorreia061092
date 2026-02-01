import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { albumService } from '../../services';
import { authStore } from '../../stores';
import { Loading, ErrorMessage } from '../../components';
import { Album } from '../../models';
import { formatDateTime } from '../../utils';

/**
 * Página de detalhes do álbum (PÚBLICA - todos podem ver)
 * Botões de ação aparecem apenas para usuários logados
 */
const AlbumDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [album, setAlbum] = useState<Album | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [selectedCoverIndex, setSelectedCoverIndex] = useState(0);

  useEffect(() => {
    // Observa autenticação
    const authSub = authStore.currentUser$.subscribe(user => {
      setIsAuthenticated(user !== null);
    });

    return () => authSub.unsubscribe();
  }, []);

  useEffect(() => {
    if (id) {
      loadAlbum(parseInt(id));
    }
  }, [id]);

  const loadAlbum = async (albumId: number) => {
    setLoading(true);
    setError(null);
    try {
      const data = await albumService.getById(albumId);
      setAlbum(data);
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar álbum');
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    navigate('/albums');
  };

  const handleEdit = () => {
    navigate(`/albums/${id}/edit`);
  };

  if (loading) {
    return (
      <div className="max-w-5xl mx-auto">
        <div className="bg-white shadow-md rounded-lg p-12">
          <Loading message="Carregando álbum..." />
        </div>
      </div>
    );
  }

  if (error || !album) {
    return (
      <div className="max-w-5xl mx-auto">
        <ErrorMessage
          message={error || 'Álbum não encontrado'}
          onRetry={() => id && loadAlbum(parseInt(id))}
        />
        <div className="mt-4 text-center">
          <button
            onClick={handleBack}
            className="text-primary-600 hover:text-primary-700 font-semibold"
          >
            ← Voltar para álbuns
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto">
      {/* Breadcrumb */}
      <div className="mb-6">
        <button
          onClick={handleBack}
          className="text-primary-600 hover:text-primary-700 font-semibold flex items-center mb-4"
        >
          <svg className="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
          Voltar para álbuns
        </button>
      </div>

      {/* Card Principal */}
      <div className="bg-white shadow-lg rounded-lg overflow-hidden">
        <div className="grid md:grid-cols-2 gap-8 p-8">
          {/* Capa e Galeria */}
          <div>
            {/* Capa Principal */}
            <div className="aspect-square bg-gray-200 rounded-lg overflow-hidden shadow-xl mb-4">
              {album.coverUrls && album.coverUrls.length > 0 ? (
                <img
                  src={album.coverUrls[selectedCoverIndex]}
                  alt={album.title}
                  className="w-full h-full object-cover"
                />
              ) : (
                <div className="w-full h-full bg-gradient-to-br from-purple-400 to-pink-600 flex items-center justify-center">
                  <svg className="w-24 h-24 text-white" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M18 3a1 1 0 00-1.196-.98l-10 2A1 1 0 006 5v9.114A4.369 4.369 0 005 14c-1.657 0-3 .895-3 2s1.343 2 3 2 3-.895 3-2V7.82l8-1.6v5.894A4.37 4.37 0 0015 12c-1.657 0-3 .895-3 2s1.343 2 3 2 3-.895 3-2V3z" />
                  </svg>
                </div>
              )}
            </div>

            {/* Miniaturas */}
            {album.coverUrls && album.coverUrls.length > 1 && (
              <div className="grid grid-cols-4 gap-2">
                {album.coverUrls.map((url, index) => (
                  <div
                    key={index}
                    onClick={() => setSelectedCoverIndex(index)}
                    className={`aspect-square rounded-lg overflow-hidden cursor-pointer border-2 transition-all ${
                      selectedCoverIndex === index
                        ? 'border-primary-600 shadow-md'
                        : 'border-transparent hover:border-gray-300'
                    }`}
                  >
                    <img
                      src={url}
                      alt={`Capa ${index + 1}`}
                      className="w-full h-full object-cover"
                    />
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Informações */}
          <div>
            <div className="mb-6">
              <h1 className="text-4xl font-bold text-gray-800 mb-2">{album.title}</h1>
              {album.releaseYear && (
                <p className="text-xl text-gray-600">{album.releaseYear}</p>
              )}
            </div>

            {/* Artistas */}
            <div className="mb-6">
              <h2 className="text-lg font-semibold text-gray-700 mb-3 flex items-center">
                <svg className="w-5 h-5 mr-2 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                {album.artists.length === 1 ? 'Artista' : 'Artistas'}
              </h2>
              <div className="space-y-2">
                {album.artists.map(artist => (
                  <div
                    key={artist.id}
                    onClick={() => navigate(`/artists/${artist.id}`)}
                    className="flex items-center p-3 bg-gray-50 rounded-lg hover:bg-primary-50 cursor-pointer transition-colors border border-gray-200 hover:border-primary-300"
                  >
                    <div className="w-10 h-10 bg-gradient-to-br from-primary-400 to-purple-600 rounded-full flex items-center justify-center mr-3">
                      <span className="text-white font-bold">
                        {artist.name.charAt(0).toUpperCase()}
                      </span>
                    </div>
                    <span className="font-medium text-gray-800">{artist.name}</span>
                    <svg className="w-5 h-5 ml-auto text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                    </svg>
                  </div>
                ))}
              </div>
            </div>

            {/* Informações adicionais */}
            <div className="space-y-3 mb-6">
              <div className="flex items-center text-gray-600">
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
                </svg>
                <span className="text-sm">
                  {album.coverUrls.length} {album.coverUrls.length === 1 ? 'capa' : 'capas'}
                </span>
              </div>
              
              <div className="flex items-center text-gray-600">
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                <span className="text-sm">Cadastrado em {formatDateTime(album.createdAt)}</span>
              </div>

              <div className="flex items-center text-gray-600">
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
                <span className="text-sm">Atualizado em {formatDateTime(album.updatedAt)}</span>
              </div>
            </div>

            {/* Ações - Apenas para logados */}
            {isAuthenticated ? (
              <div className="pt-6 border-t">
                <button
                  onClick={handleEdit}
                  className="w-full bg-primary-600 hover:bg-primary-700 text-white px-6 py-3 rounded-lg font-semibold transition-colors shadow-md"
                >
                  Editar Álbum
                </button>
              </div>
            ) : (
              <div className="bg-gradient-to-r from-purple-50 to-pink-50 rounded-lg p-6 border border-purple-200">
                <h3 className="text-lg font-bold text-gray-800 mb-2">
                  Gostou deste álbum?
                </h3>
                <p className="text-gray-600 mb-4 text-sm">
                  Faça login para gerenciar álbuns e adicionar seus favoritos
                </p>
                <button
                  onClick={() => navigate('/login')}
                  className="w-full bg-purple-600 hover:bg-purple-700 text-white px-6 py-3 rounded-lg font-semibold transition-colors shadow-md"
                >
                  Fazer Login
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AlbumDetailPage;
