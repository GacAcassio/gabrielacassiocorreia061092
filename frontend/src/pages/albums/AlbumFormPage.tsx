import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { albumFacade, artistFacade } from '../../services/facades';
import { albumStore, artistStore } from '../../stores';
import { Loading, ErrorMessage } from '../../components';
import { isValidName, isValidYear } from '../../utils';
import { ArtistSummary } from '../../models';
import ArtistPicker from '../../components/ArtistPicker';


/**
 * Página de formulário de álbum (criar/editar) com upload
 */
const AlbumFormPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEditMode = id !== 'new' && !!id;

  const [title, setTitle] = useState('');
  const [releaseYear, setReleaseYear] = useState<number | ''>('');
  const [selectedArtistIds, setSelectedArtistIds] = useState<number[]>([]);
  const [availableArtists, setAvailableArtists] = useState<ArtistSummary[]>([]);
  const [coverFiles, setCoverFiles] = useState<File[]>([]);
  const [previewUrls, setPreviewUrls] = useState<string[]>([]);

  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);
  const [error, setError] = useState('');
  const [validationErrors, setValidationErrors] = useState<{
    title?: string;
    releaseYear?: string;
    artistIds?: string;
  }>({});

  useEffect(() => {
    loadArtists();
    if (isEditMode && id) {
      loadAlbum(parseInt(id));
    } else {
      setInitialLoading(false);
    }
  }, [id, isEditMode]);

  const loadArtists = async () => {
    try {
      await artistFacade.list({ page: 0, size: 1000 });
    } catch (err) {
      //console.error('Error loading artists:', err);
    }
  };

  useEffect(() => {
    const artistSub = artistStore.state$.subscribe(state => {
      if (state.artists) {
        setAvailableArtists(state.artists.content);
      }
    });

    const albumSub = albumStore.state$.subscribe(state => {
      if (state.selectedAlbum && isEditMode) {
        setTitle(state.selectedAlbum.title);
        setReleaseYear(state.selectedAlbum.releaseYear || '');
        setSelectedArtistIds(state.selectedAlbum.artists.map(a => a.id));
        if (state.selectedAlbum.coverUrls) {
          setPreviewUrls(state.selectedAlbum.coverUrls);
        }
      }
      setInitialLoading(false);
    });

    return () => {
      artistSub.unsubscribe();
      albumSub.unsubscribe();
    };
  }, [isEditMode]);

  const loadAlbum = async (albumId: number) => {
    try {
      await albumFacade.getById(albumId);
    } catch (err) {
      setError('Erro ao carregar álbum');
      setInitialLoading(false);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files || []);
    //console.log('Arquivos selecionados:', files.length);
    
    if (files.length === 0) return;

    // Validar tipo e tamanho
    const validFiles = files.filter(file => {
      const isImage = file.type.startsWith('image/');
      const isUnder5MB = file.size <= 5 * 1024 * 1024;
      
      // if (!isImage) {
      //   console.warn('Arquivo não é imagem:', file.name, file.type);
      // }
      // if (!isUnder5MB) {
      //   console.warn(' Arquivo muito grande:', file.name, (file.size / 1024 / 1024).toFixed(2), 'MB');
      // }
      
      return isImage && isUnder5MB;
    });

    // console.log('Arquivos válidos:', validFiles.length);
    validFiles.forEach(f => console.log('  -', f.name, f.type, (f.size / 1024).toFixed(2), 'KB'));

    setCoverFiles(validFiles);

    // Gerar previews
    const urls = validFiles.map(file => URL.createObjectURL(file));
    setPreviewUrls(urls);
  };

  const removePreview = (index: number) => {
    const newFiles = coverFiles.filter((_, i) => i !== index);
    const newUrls = previewUrls.filter((_, i) => i !== index);
    
    // Revogar URL antiga
    URL.revokeObjectURL(previewUrls[index]);
    
    setCoverFiles(newFiles);
    setPreviewUrls(newUrls);
  };

  const validate = (): boolean => {
    const errors: typeof validationErrors = {};

    if (!isValidName(title, 1, 200)) {
      errors.title = 'Título deve ter entre 1 e 200 caracteres';
    }

    if (releaseYear && !isValidYear(Number(releaseYear))) {
      errors.releaseYear = 'Ano inválido';
    }

    if (selectedArtistIds.length === 0) {
      errors.artistIds = 'Selecione pelo menos um artista';
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
      const albumData = {
        title: title.trim(),
        artistIds: selectedArtistIds,
        releaseYear: releaseYear ? Number(releaseYear) : undefined,
      };

      // console.log('Salvando álbum...', albumData);

      if (isEditMode && id) {
        await albumFacade.update(parseInt(id), albumData);
        // console.log('Álbum atualizado');
        
        // Upload de capas se houver
        if (coverFiles.length > 0) {
          // console.log('Iniciando upload de', coverFiles.length, 'capa(s)');
          await albumFacade.uploadCovers(parseInt(id), coverFiles);
          // console.log('Capas enviadas com sucesso');
        }
      } else {
        // console.log('Criando novo álbum...');
        await albumFacade.create(albumData);
        // console.log('Álbum criado');
        
        // Se criou com sucesso e tem capas, fazer upload
        const createdAlbum = albumStore.currentState.selectedAlbum;
        if (createdAlbum && coverFiles.length > 0) {
          // console.log('Iniciando upload de', coverFiles.length, 'capa(s) para álbum', createdAlbum.id);
          await albumFacade.uploadCovers(createdAlbum.id, coverFiles);
          // console.log(' Capas enviadas com sucesso');
        }
      }

      // console.log(' Processo concluído, redirecionando...');
      navigate('/albums');
    } catch (err: any) {
      // console.error(' Erro ao salvar:', err);
      setError(err.message || 'Erro ao salvar álbum');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    // Limpar URLs de preview
    previewUrls.forEach(url => URL.revokeObjectURL(url));
    navigate(isEditMode ? `/albums/${id}` : '/albums');
  };

  const toggleArtist = (artistId: number) => {
    setSelectedArtistIds(prev =>
      prev.includes(artistId)
        ? prev.filter(id => id !== artistId)
        : [...prev, artistId]
    );
  };

  if (initialLoading) {
    return (
      <div className="max-w-3xl mx-auto">
        <div className="bg-white shadow-md rounded-lg p-12">
          <Loading message="Carregando..." />
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto">
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
          {isEditMode ? 'Editar Álbum' : 'Novo Álbum'}
        </h1>

        {error && <ErrorMessage message={error} />}

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Título */}
          <div>
            <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-2">
              Título do Álbum *
            </label>
            <input
              id="title"
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className={`w-full px-4 py-2 border ${
                validationErrors.title ? 'border-red-500' : 'border-gray-300'
              } rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500`}
              placeholder="Digite o título do álbum"
              disabled={loading}
            />
            {validationErrors.title && (
              <p className="mt-1 text-sm text-red-600">{validationErrors.title}</p>
            )}
          </div>

          {/* Ano */}
          <div>
            <label htmlFor="releaseYear" className="block text-sm font-medium text-gray-700 mb-2">
              Ano de Lançamento
            </label>
            <input
              id="releaseYear"
              type="number"
              min="1900"
              max={new Date().getFullYear() + 1}
              value={releaseYear}
              onChange={(e) => setReleaseYear(e.target.value ? parseInt(e.target.value) : '')}
              className={`w-full px-4 py-2 border ${
                validationErrors.releaseYear ? 'border-red-500' : 'border-gray-300'
              } rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500`}
              placeholder="Ex: 2024"
              disabled={loading}
            />
            {validationErrors.releaseYear && (
              <p className="mt-1 text-sm text-red-600">{validationErrors.releaseYear}</p>
            )}
          </div>

          {/* Artistas */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Artistas * (pesquise e adicione)
            </label>

            <ArtistPicker
              availableArtists={availableArtists}
              selectedArtistIds={selectedArtistIds}
              onChange={setSelectedArtistIds}
              disabled={loading}
              error={validationErrors.artistIds}
            />
          </div>


          {/* Upload de Capas */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Capas do Álbum
            </label>
            <input
              type="file"
              accept="image/*"
              multiple
              onChange={handleFileChange}
              className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-primary-50 file:text-primary-700 hover:file:bg-primary-100"
              disabled={loading}
            />
            <p className="mt-1 text-sm text-gray-500">
              Selecione uma ou mais imagens (máx. 5MB cada)
            </p>

            {/* Preview */}
            {previewUrls.length > 0 && (
              <div className="mt-4 grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
                {previewUrls.map((url, index) => (
                  <div key={index} className="relative group">
                    <img
                      src={url}
                      alt={`Preview ${index + 1}`}
                      className="w-full h-32 object-cover rounded-lg"
                    />
                    <button
                      type="button"
                      onClick={() => removePreview(index)}
                      className="absolute top-2 right-2 bg-red-600 text-white rounded-full p-1 opacity-0 group-hover:opacity-100 transition-opacity"
                    >
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                ))}
              </div>
            )}
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
                <>{isEditMode ? 'Salvar Alterações' : 'Criar Álbum'}</>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AlbumFormPage;
