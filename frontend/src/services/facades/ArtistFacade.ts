import { artistService } from '../ArtistService';
import { artistStore } from '../../stores/ArtistStore';
import { ArtistRequest, PageRequest } from '../../models';

/**
 * Facade para operações com artistas
 * Centraliza as chamadas aos services e gerencia o estado na store
 */
class ArtistFacade {
  /**
   * Lista artistas com paginação e filtros
   */
  async list(params?: PageRequest & { name?: string }): Promise<void> {
    try {
      artistStore.setLoading(true);
      const artists = await artistService.list(params);
      artistStore.setArtists(artists);
    } catch (error: any) {
      artistStore.setError(error.message);
      throw error;
    } finally {
      artistStore.setLoading(false);
    }
  }

  /**
   * Busca artista por ID
   */
  async getById(id: number): Promise<void> {
    try {
      artistStore.setLoading(true);
      const artist = await artistService.getById(id);
      artistStore.setSelectedArtist(artist);
    } catch (error: any) {
      artistStore.setError(error.message);
      throw error;
    } finally {
      artistStore.setLoading(false);
    }
  }

  /**
   * Cria novo artista
   */
  async create(artist: ArtistRequest): Promise<void> {
    try {
      artistStore.setLoading(true);
      const newArtist = await artistService.create(artist);
      artistStore.setSelectedArtist(newArtist);
      
      // Recarrega a lista
      await this.list();
    } catch (error: any) {
      artistStore.setError(error.message);
      throw error;
    } finally {
      artistStore.setLoading(false);
    }
  }

  /**
   * Atualiza artista existente
   */
  async update(id: number, artist: ArtistRequest): Promise<void> {
    try {
      artistStore.setLoading(true);
      const updatedArtist = await artistService.update(id, artist);
      artistStore.setSelectedArtist(updatedArtist);
      
      // Recarrega a lista
      await this.list();
    } catch (error: any) {
      artistStore.setError(error.message);
      throw error;
    } finally {
      artistStore.setLoading(false);
    }
  }

  /**
   * Remove artista
   */
  async delete(id: number): Promise<void> {
    try {
      artistStore.setLoading(true);
      await artistService.delete(id);
      artistStore.clearSelectedArtist();
      
      // Recarrega a lista
      await this.list();
    } catch (error: any) {
      artistStore.setError(error.message);
      throw error;
    } finally {
      artistStore.setLoading(false);
    }
  }

  /**
   * Limpa o artista selecionado
   */
  clearSelected(): void {
    artistStore.clearSelectedArtist();
  }

  /**
   * Limpa erros
   */
  clearError(): void {
    artistStore.setError(null);
  }
}

export const artistFacade = new ArtistFacade();
