import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { artistFacade } from '../../services/facades';
import { artistStore } from '../../stores';
import { Loading, ErrorMessage } from '../../components';
import { isValidName, sanitizeString } from '../../utils';

/**
 * Página de formulário de artista (criar/editar)
 */
const ArtistFormPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEditMode = id !== 'new' && !!id;

  const [name, setName] = useState('');
  const [bio, setBio] = useState('');
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(isEditMode);
  const [error, setError] = useState('');
  const [validationErrors, setValidationErrors] = useState<{
    name?: string;
    bio?: string;
  }>({});

  useEffect(() => {
    if (isEditMode && id) {
      loadArtist(parseInt(id));
    }
  }, [id, isEditMode]);

  useEffect(() => {
    const subscription = artistStore.state$.subscribe(state => {
      if (state.selectedArtist && isEditMode) {
        setName(state.selectedArtist.name);
        setBio(state.selectedArtist.bio || '');
      }
      setInitialLoading(false);
    });

    return () => subscription.unsubscribe();
  }, [isEditMode]);

  const loadArtist = async (artistId: number) => {
    try {
      await artistFacade.getById(artistId);
    } catch (err) {
      setError('Erro ao carregar artista');
      setInitialLoading(false);
    }
  };

  const validate = (): boolean => {
    const errors: typeof validationErrors = {};

    if (!isValidName(name, 2, 200)) {
      errors.name = 'Nome deve ter entre 2 e 200 caracteres';
    }

    if (bio && bio.length > 5000) {
      errors.bio = 'Biografia deve ter no máximo 5000 caracteres';
    }

    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!validate()) {
      return;
    }

    setLoading(true);

    try {
      const artistData = {
        name: sanitizeString(name),
        bio: bio.trim() || undefined,
      };

      if (isEditMode && id) {
        await artistFacade.update(parseInt(id), artistData);
      } else {
        await artistFacade.create(artistData);
      }

      navigate('/artists');
    } catch (err: any) {
      setError(err.message || 'Erro ao salvar artista');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate(isEditMode ? `/artists/${id}` : '/artists');
  };

  if (initialLoading) {
    return (
      <div className="max-w-2xl mx-auto">
        <div className="bg-white shadow-md rounded-lg p-12">
          <Loading message="Carregando artista..." />
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="mb-6">
        <button
          onClick={handleCancel}
          className="text-primary-600 hover:text-primary-700 font-semibold flex items-center"
        >
          <svg className="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
          Voltar
        </button>
      </div>

      <div className="bg-white shadow-lg rounded-lg p-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-6">
          {isEditMode ? 'Editar Artista' : 'Novo Artista'}
        </h1>

        {error && <ErrorMessage message={error} />}

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Nome */}
          <div>
            <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
              Nome do Artista *
            </label>
            <input
              id="name"
              type="text"
              required
              value={name}
              onChange={(e) => setName(e.target.value)}
              className={`w-full px-4 py-2 border ${
                validationErrors.name ? 'border-red-500' : 'border-gray-300'
              } rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500`}
              placeholder="Digite o nome do artista"
              disabled={loading}
            />
            {validationErrors.name && (
              <p className="mt-1 text-sm text-red-600">{validationErrors.name}</p>
            )}
          </div>

          {/* Biografia */}
          <div>
            <label htmlFor="bio" className="block text-sm font-medium text-gray-700 mb-2">
              Biografia
            </label>
            <textarea
              id="bio"
              rows={6}
              value={bio}
              onChange={(e) => setBio(e.target.value)}
              className={`w-full px-4 py-2 border ${
                validationErrors.bio ? 'border-red-500' : 'border-gray-300'
              } rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500`}
              placeholder="Digite a biografia do artista (opcional)"
              disabled={loading}
            />
            <div className="flex justify-between mt-1">
              {validationErrors.bio && (
                <p className="text-sm text-red-600">{validationErrors.bio}</p>
              )}
              <p className="text-sm text-gray-500 ml-auto">
                {bio.length}/5000 caracteres
              </p>
            </div>
          </div>

          {/* Botões */}
          <div className="flex flex-col-reverse sm:flex-row gap-3 pt-6 border-t">
            <button
              type="button"
              onClick={handleCancel}
              disabled={loading}
              className="flex-1 px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 font-semibold transition-colors disabled:opacity-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 bg-primary-600 hover:bg-primary-700 text-white px-6 py-2 rounded-lg font-semibold transition-colors disabled:opacity-50 flex items-center justify-center"
            >
              {loading ? (
                <Loading size="small" message="" />
              ) : (
                <>{isEditMode ? 'Salvar Alterações' : 'Criar Artista'}</>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ArtistFormPage;
