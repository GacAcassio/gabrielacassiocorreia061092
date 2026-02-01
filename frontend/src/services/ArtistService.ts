import { httpClient } from './HttpClient';
import { Artist, ArtistSummary, ArtistRequest, PageResponse, PageRequest } from '../models';

/**
 * Serviço para operações com artistas
 */
class ArtistService {
  private readonly basePath = '/artists';

  /**
   * Lista artistas com paginação e filtros
   */
  async list(params?: PageRequest & { name?: string }): Promise<PageResponse<ArtistSummary>> {
    try {
      const queryParams = new URLSearchParams();
      
      if (params?.page !== undefined) {
        queryParams.append('page', params.page.toString());
      }
      if (params?.size !== undefined) {
        queryParams.append('size', params.size.toString());
      }
      if (params?.sort) {
        queryParams.append('sort', params.sort);
      }
      if (params?.direction) {
        queryParams.append('direction', params.direction);
      }
      if (params?.name) {
        queryParams.append('name', params.name);
      }

      const response = await httpClient.get<PageResponse<ArtistSummary>>(
        `${this.basePath}?${queryParams.toString()}`
      );
      
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Erro ao listar artistas');
    }
  }

  /**
   * Busca artista por ID
   */
  async getById(id: number): Promise<Artist> {
    try {
      const response = await httpClient.get<Artist>(`${this.basePath}/${id}`);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Erro ao buscar artista');
    }
  }

  /**
   * Cria novo artista
   */
  async create(artist: ArtistRequest): Promise<Artist> {
    try {
      const response = await httpClient.post<Artist>(this.basePath, artist);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Erro ao criar artista');
    }
  }

  /**
   * Atualiza artista existente
   */
  async update(id: number, artist: ArtistRequest): Promise<Artist> {
    try {
      const response = await httpClient.put<Artist>(`${this.basePath}/${id}`, artist);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Erro ao atualizar artista');
    }
  }

  /**
   * Remove artista
   */
  async delete(id: number): Promise<void> {
    try {
      await httpClient.delete(`${this.basePath}/${id}`);
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Erro ao remover artista');
    }
  }
}

export const artistService = new ArtistService();
