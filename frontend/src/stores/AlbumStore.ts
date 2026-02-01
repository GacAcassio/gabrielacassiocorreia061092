import { BehaviorSubject, Observable } from 'rxjs';
import { Album, PageResponse } from '../models';
import { AlbumSummary } from '../models/Artist';

/**
 * Estado da store de álbuns
 */
interface AlbumState {
  albums: PageResponse<AlbumSummary> | null;
  selectedAlbum: Album | null;
  loading: boolean;
  error: string | null;
}

/**
 * Store de álbuns usando BehaviorSubject (RxJS)
 * Gerencia o estado global de álbuns
 */
class AlbumStore {
  private stateSubject: BehaviorSubject<AlbumState>;
  public state$: Observable<AlbumState>;

  constructor() {
    const initialState: AlbumState = {
      albums: null,
      selectedAlbum: null,
      loading: false,
      error: null,
    };

    this.stateSubject = new BehaviorSubject<AlbumState>(initialState);
    this.state$ = this.stateSubject.asObservable();
  }

  /**
   * Retorna o estado atual
   */
  get currentState(): AlbumState {
    return this.stateSubject.value;
  }

  /**
   * Define a lista de álbuns
   */
  setAlbums(albums: PageResponse<AlbumSummary>): void {
    this.stateSubject.next({
      ...this.currentState,
      albums,
      error: null,
    });
  }

  /**
   * Define o álbum selecionado
   */
  setSelectedAlbum(album: Album | null): void {
    this.stateSubject.next({
      ...this.currentState,
      selectedAlbum: album,
      error: null,
    });
  }

  /**
   * Define o estado de loading
   */
  setLoading(loading: boolean): void {
    this.stateSubject.next({
      ...this.currentState,
      loading,
    });
  }

  /**
   * Define um erro
   */
  setError(error: string | null): void {
    this.stateSubject.next({
      ...this.currentState,
      error,
      loading: false,
    });
  }

  /**
   * Limpa o estado
   */
  clear(): void {
    this.stateSubject.next({
      albums: null,
      selectedAlbum: null,
      loading: false,
      error: null,
    });
  }

  /**
   * Limpa apenas o álbum selecionado
   */
  clearSelectedAlbum(): void {
    this.stateSubject.next({
      ...this.currentState,
      selectedAlbum: null,
    });
  }
}

// Exporta instância singleton
export const albumStore = new AlbumStore();
