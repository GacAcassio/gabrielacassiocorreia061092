/**
 * Model para Artista
 */
export interface Artist {
  id: number;
  name: string;
  bio?: string;
  albumCount: number;
  albums?: AlbumSummary[];
  createdAt?: string;
  updatedAt?: string;
}

/**
 * DTO resumido de álbum (usado em Artist)
 */
export interface AlbumSummary {
  id: number;
  title: string;
  releaseYear?: number;
  artistNames?: string[];
  coverUrl?: string;
}

/**
 * DTO para listagem de artistas
 */
export interface ArtistSummary {
  id: number;
  name: string;
  albumCount: number;
}

/**
 * DTO para criar/atualizar artista
 */
export interface ArtistRequest {
  name: string;
  bio?: string;
}
