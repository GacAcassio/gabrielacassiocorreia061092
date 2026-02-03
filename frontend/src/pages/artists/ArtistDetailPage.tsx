import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { artistFacade } from '../../services/facades';
import { artistStore } from '../../stores';
import { Loading, ErrorMessage } from '../../components';
import { Artist } from '../../models';
import { formatDateTime } from '../../utils';

/**
 * Página de detalhes do artista
 */
const ArtistDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [artist, setArtist] = useState<Artist | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      loadArtist(parseInt(id));
    }
  }, [id]);

  useEffect(() => {
    const subscription = artistStore.state$.subscribe(state => {
      setArtist(state.selectedArtist);
      setLoading(state.loading);
      setError(state.error);
    });

    return () => subscription.unsubscribe();
  }, []);

  const loadArtist = async (artistId: number) => {
    try {
      await artistFacade.getById(artistId);
    } catch (err) {
      console.error('Error loading artist:', err);
    }
  };

  const handleEdit = () => {
    navigate(`/artists/${id}/edit`);
  };

  const handleDelete = async () => {
    if (!id || !artist) return;

    const confirmed = window.confirm(
      `Tem certeza que deseja excluir o artista "${artist.name}"?`
    );

    if (confirmed) {
      try {
        await artistFacade.delete(parseInt(id));
        navigate('/artists');
      } catch (err) {
        alert('Erro ao excluir artista');
      }
    }
  };

  const handleBack = () => {
    navigate('/artists');
  };

  if (loading) {
    return (
      <div className="max-w-4xl mx-auto">
        <div className="bg-white shadow-md rounded-lg p-12">
          <Loading message="Carregando artista..." />
        </div>
      </div>
    );
  }

  if (error || !artist) {
    return (
      <div className="max-w-4xl mx-auto">
        <ErrorMessage
          message={error || 'Artista não encontrado'}
          onRetry={() => id && loadArtist(parseInt(id))}
        />
        <div className="mt-4 text-center">
          <button
            onClick={handleBack}
            className="text-primary-600 hover:text-primary-700 font-semibold"
          >
            ← Voltar para listagem
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto">
      {/* Header */}
      <div className="mb-6">
        <button
          onClick={handleBack}
          className="text-primary-600 hover:text-primary-700 font-semibold flex items-center mb-4"
        >
          <svg className="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
          Voltar
        </button>
      </div>

      {/* Card Principal */}
      <div className="bg-white shadow-lg rounded-lg overflow-hidden">
        {/* Header do Card */}
        <div className="bg-gradient-to-r from-primary-500 to-primary-700 px-6 py-8">
          <div className="flex items-center">
            <div className="w-24 h-24 bg-white rounded-full flex items-center justify-center shadow-lg">
              <span className="text-primary-600 font-bold text-4xl">
                {artist.name.charAt(0).toUpperCase()}
              </span>
            </div>
            <div className="ml-6 text-white">
              <h1 className="text-3xl font-bold">{artist.name}</h1>
              <p className="text-primary-100 mt-1">
                {artist.albumCount} {artist.albumCount === 1 ? 'álbum' : 'álbuns'}
              </p>
            </div>
          </div>
        </div>

        {/* Conteúdo */}
        <div className="p-6">
          {/* Biografia */}
          <div className="mb-6">
            <h2 className="text-xl font-bold text-gray-800 mb-3">Biografia</h2>
            {artist.bio ? (
              <p className="text-gray-700 leading-relaxed whitespace-pre-line">{artist.bio}</p>
            ) : (
              <p className="text-gray-500 italic">Sem biografia cadastrada.</p>
            )}
          </div>

          {/* Informações */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
            <div className="bg-gray-50 rounded-lg p-4">
              <p className="text-sm text-gray-600">Criado em</p>
              <p className="text-gray-800 font-semibold">{formatDateTime(artist.createdAt)}</p>
            </div>
            <div className="bg-gray-50 rounded-lg p-4">
              <p className="text-sm text-gray-600">Atualizado em</p>
              <p className="text-gray-800 font-semibold">{formatDateTime(artist.updatedAt)}</p>
            </div>
          </div>

          {/* Álbuns */}
          <div className="mb-6">
            <h2 className="text-xl font-bold text-gray-800 mb-3">Álbuns</h2>
            {artist.albums && artist.albums.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {artist.albums.map(album => (
                  <div
                    key={album.id}
                    className="border border-gray-200 rounded-lg p-4 hover:border-primary-500 transition-colors cursor-pointer"
                    onClick={() => navigate(`/albums/${album.id}`)}
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <h3 className="font-semibold text-gray-800">{album.title}</h3>
                        {album.releaseYear && (
                          <p className="text-sm text-gray-600">{album.releaseYear}</p>
                        )}
                      </div>
                      {album.coverUrl && (
                        <img
                          src={album.coverUrl}
                          alt={album.title}
                          className="w-16 h-16 object-cover rounded"
                        />
                      )}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-500 italic">Nenhum álbum cadastrado.</p>
            )}
          </div>

          {/* Ações */}
          <div className="flex flex-wrap gap-3 pt-6 border-t">
            <button
              onClick={handleEdit}
              className="flex-1 md:flex-initial bg-primary-600 hover:bg-primary-700 text-white px-6 py-2 rounded-lg font-semibold transition-colors"
            >
              Editar
            </button>
            <button
              onClick={handleDelete}
              className="flex-1 md:flex-initial bg-red-600 hover:bg-red-700 text-white px-6 py-2 rounded-lg font-semibold transition-colors"
            >
              Excluir
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ArtistDetailPage;
