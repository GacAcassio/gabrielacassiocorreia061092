import { httpClient } from './HttpClient';
import { Album, AlbumRequest, PageResponse, PageRequest } from '../models';
import { AlbumSummary } from '../models/Artist';

/**
 * Serviço para operações com álbuns
 */
class AlbumService {
  private readonly basePath = '/albums';

  /**
   * Lista álbuns com paginação
   */
  async list(params?: PageRequest): Promise<PageResponse<AlbumSummary>> {
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

      const response = await httpClient.get<PageResponse<AlbumSummary>>(
        `${this.basePath}?${queryParams.toString()}`
      );

      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Erro ao listar álbuns');
    }
  }

  /**
   * Busca álbuns por título (PAGINADO)
   * Endpoint esperado: GET /albums/search?title=...
   */
  async searchByTitle(
    params: PageRequest & { title: string }
  ): Promise<PageResponse<AlbumSummary>> {
    try {
      const queryParams = new URLSearchParams();

      // obrigatório
      queryParams.append('title', params.title);

      // paginação
      if (params?.page !== undefined) {
        queryParams.append('page', params.page.toString());
      }
      if (params?.size !== undefined) {
        queryParams.append('size', params.size.toString());
      }

      // ordenação (opcional)
      if (params?.sort) {
        queryParams.append('sort', params.sort);
      }
      if (params?.direction) {
        queryParams.append('direction', params.direction);
      }

      const response = await httpClient.get<PageResponse<AlbumSummary>>(
        `${this.basePath}/search?${queryParams.toString()}`
      );

      return response.data;
    } catch (error: any) {
      throw new Error(
        error.response?.data?.message || 'Erro ao buscar álbuns pelo título'
      );
    }
  }

  /**
   * Busca álbuns pelo nome do artista (PAGINADO)
   * Endpoint esperado: GET /albums/search/artist?name=...
   */
  async searchByArtistName(
    params: PageRequest & { name: string }
  ): Promise<PageResponse<AlbumSummary>> {
    try {
      const queryParams = new URLSearchParams();

      // obrigatório
      queryParams.append('name', params.name);

      // paginação
      if (params?.page !== undefined) {
        queryParams.append('page', params.page.toString());
      }
      if (params?.size !== undefined) {
        queryParams.append('size', params.size.toString());
      }

      // ordenação (opcional)
      if (params?.sort) {
        queryParams.append('sort', params.sort);
      }
      if (params?.direction) {
        queryParams.append('direction', params.direction);
      }

      const response = await httpClient.get<PageResponse<AlbumSummary>>(
        `${this.basePath}/search/artist?${queryParams.toString()}`
      );

      return response.data;
    } catch (error: any) {
      throw new Error(
        error.response?.data?.message || 'Erro ao buscar álbuns pelo artista'
      );
    }
  }

  /**
   * Busca álbum por ID
   */
  async getById(id: number): Promise<Album> {
    try {
      const response = await httpClient.get<Album>(`${this.basePath}/${id}`);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Erro ao buscar álbum');
    }
  }

  /**
   * Lista álbuns de um artista
   */
  async getByArtistId(
    artistId: number,
    params?: PageRequest
  ): Promise<PageResponse<AlbumSummary>> {
    try {
      const queryParams = new URLSearchParams();

      if (params?.page !== undefined) {
        queryParams.append('page', params.page.toString());
      }
      if (params?.size !== undefined) {
        queryParams.append('size', params.size.toString());
      }

      const response = await httpClient.get<PageResponse<AlbumSummary>>(
        `/artists/${artistId}/albums?${queryParams.toString()}`
      );

      return response.data;
    } catch (error: any) {
      throw new Error(
        error.response?.data?.message || 'Erro ao listar álbuns do artista'
      );
    }
  }

  /**
   * Cria novo álbum
   */
  async create(album: AlbumRequest): Promise<Album> {
    try {
      const response = await httpClient.post<Album>(this.basePath, album);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Erro ao criar álbum');
    }
  }

  /**
   * Atualiza álbum existente
   */
  async update(id: number, album: AlbumRequest): Promise<Album> {
    try {
      const response = await httpClient.put<Album>(
        `${this.basePath}/${id}`,
        album
      );
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Erro ao atualizar álbum');
    }
  }

  /**
   * Upload de capas do álbum
   */
  async uploadCovers(albumId: number, files: File[]): Promise<Album> {
    try {
      const formData = new FormData();

      files.forEach((file) => {
        formData.append('files', file);
      });

      const response = await httpClient.post<Album>(
        `${this.basePath}/${albumId}/covers`,
        formData
      );

      return response.data;
    } catch (error: any) {
      throw new Error(
        error.response?.data?.message || 'Erro ao fazer upload das capas'
      );
    }
  }

  /**
   * Remove álbum
   */
  async delete(id: number): Promise<void> {
    try {
      await httpClient.delete(`${this.basePath}/${id}`);
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Erro ao remover álbum');
    }
  }
}

export const albumService = new AlbumService();
