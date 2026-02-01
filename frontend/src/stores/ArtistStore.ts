import { BehaviorSubject, Observable } from 'rxjs';
import { Artist, ArtistSummary, PageResponse } from '../models';

/**
 * Estado da store de artistas
 */
interface ArtistState {
  artists: PageResponse<ArtistSummary> | null;
  selectedArtist: Artist | null;
  loading: boolean;
  error: string | null;
}

/**
 * Store de artistas usando BehaviorSubject (RxJS)
 * Gerencia o estado global de artistas
 */
class ArtistStore {
  private stateSubject: BehaviorSubject<ArtistState>;
  public state$: Observable<ArtistState>;

  constructor() {
    const initialState: ArtistState = {
      artists: null,
      selectedArtist: null,
      loading: false,
      error: null,
    };

    this.stateSubject = new BehaviorSubject<ArtistState>(initialState);
    this.state$ = this.stateSubject.asObservable();
  }

  /**
   * Retorna o estado atual
   */
  get currentState(): ArtistState {
    return this.stateSubject.value;
  }

  /**
   * Define a lista de artistas
   */
  setArtists(artists: PageResponse<ArtistSummary>): void {
    this.stateSubject.next({
      ...this.currentState,
      artists,
      error: null,
    });
  }

  /**
   * Define o artista selecionado
   */
  setSelectedArtist(artist: Artist | null): void {
    this.stateSubject.next({
      ...this.currentState,
      selectedArtist: artist,
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
      artists: null,
      selectedArtist: null,
      loading: false,
      error: null,
    });
  }

  /**
   * Limpa apenas o artista selecionado
   */
  clearSelectedArtist(): void {
    this.stateSubject.next({
      ...this.currentState,
      selectedArtist: null,
    });
  }
}

// Exporta instância singleton
export const artistStore = new ArtistStore();
