import { albumService } from '../AlbumService';
import { albumStore } from '../../stores/AlbumStore';
import { AlbumRequest, PageRequest } from '../../models';

/**
 * Facade para operações com álbuns
 * Centraliza as chamadas aos services e gerencia o estado na store
 */
class AlbumFacade {
  /**
   * Lista álbuns com paginação
   */
  async list(params?: PageRequest): Promise<void> {
    try {
      albumStore.setLoading(true);
      const albums = await albumService.list(params);
      albumStore.setAlbums(albums);
    } catch (error: any) {
      albumStore.setError(error.message);
      throw error;
    } finally {
      albumStore.setLoading(false);
    }
  }

  /**
   * Busca álbum por ID
   */
  async getById(id: number): Promise<void> {
    try {
      albumStore.setLoading(true);
      const album = await albumService.getById(id);
      albumStore.setSelectedAlbum(album);
    } catch (error: any) {
      albumStore.setError(error.message);
      throw error;
    } finally {
      albumStore.setLoading(false);
    }
  }

  /**
   * Lista álbuns de um artista
   */
  async getByArtistId(artistId: number, params?: PageRequest): Promise<void> {
    try {
      albumStore.setLoading(true);
      const albums = await albumService.getByArtistId(artistId, params);
      albumStore.setAlbums(albums);
    } catch (error: any) {
      albumStore.setError(error.message);
      throw error;
    } finally {
      albumStore.setLoading(false);
    }
  }

  /**
   * Cria novo álbum
   */
  async create(album: AlbumRequest): Promise<void> {
    try {
      albumStore.setLoading(true);
      const newAlbum = await albumService.create(album);
      albumStore.setSelectedAlbum(newAlbum);
      
      // Recarrega a lista
      await this.list();
    } catch (error: any) {
      albumStore.setError(error.message);
      throw error;
    } finally {
      albumStore.setLoading(false);
    }
  }

  /**
   * Atualiza álbum existente
   */
  async update(id: number, album: AlbumRequest): Promise<void> {
    try {
      albumStore.setLoading(true);
      const updatedAlbum = await albumService.update(id, album);
      albumStore.setSelectedAlbum(updatedAlbum);
      
      // Recarrega a lista
      await this.list();
    } catch (error: any) {
      albumStore.setError(error.message);
      throw error;
    } finally {
      albumStore.setLoading(false);
    }
  }

  /**
   * Upload de capas do álbum
   */
  async uploadCovers(albumId: number, files: File[]): Promise<void> {
    try {
      albumStore.setLoading(true);
      const updatedAlbum = await albumService.uploadCovers(albumId, files);
      albumStore.setSelectedAlbum(updatedAlbum);
    } catch (error: any) {
      albumStore.setError(error.message);
      throw error;
    } finally {
      albumStore.setLoading(false);
    }
  }

  /**
   * Remove álbum
   */
  async delete(id: number): Promise<void> {
    try {
      albumStore.setLoading(true);
      await albumService.delete(id);
      albumStore.clearSelectedAlbum();
      
      // Recarrega a lista
      await this.list();
    } catch (error: any) {
      albumStore.setError(error.message);
      throw error;
    } finally {
      albumStore.setLoading(false);
    }
  }

  /**
   * Limpa o álbum selecionado
   */
  clearSelected(): void {
    albumStore.clearSelectedAlbum();
  }

  /**
   * Limpa erros
   */
  clearError(): void {
    albumStore.setError(null);
  }
}

export const albumFacade = new AlbumFacade();
