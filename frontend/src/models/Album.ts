import { ArtistSummary } from './Artist';

/**
 * Model para Álbum
 */
export interface Album {
  id: number;
  title: string;
  releaseYear?: number;
  artists: ArtistSummaryInAlbum[];
  coverUrls: string[];
  createdAt?: string;
  updatedAt?: string;
}

/**
 * DTO resumido de artista (usado em Album)
 */
export interface ArtistSummaryInAlbum {
  id: number;
  name: string;
}

/**
 * DTO para criar/atualizar álbum
 */
export interface AlbumRequest {
  title: string;
  artistIds: number[];
  releaseYear?: number;
}
