import { BehaviorSubject, Observable } from 'rxjs';
import { User } from '../models';
import { authService } from '../services';


/**
 * Store de autenticação usando BehaviorSubject (RxJS)
 * Gerencia o estado global do usuário autenticado
 */
class AuthStore {
  getUser() {
    throw new Error('Method not implemented.');
  }
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null>;

  constructor() {
    // Inicializa com usuário do localStorage (se existir)
    const user = authService.getCurrentUser();
    this.currentUserSubject = new BehaviorSubject<User | null>(user);
    this.currentUser$ = this.currentUserSubject.asObservable();

    // Escuta evento de logout
    window.addEventListener('auth:logout', () => {
      this.setUser(null);
    });

    // Verifica token periodicamente (a cada 30 segundos)
    setInterval(() => {
      this.checkToken();
    }, 30000);
  }

  /**
   * Retorna o valor atual do usuário
   */
  get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Atualiza o usuário no store
   */
  setUser(user: User | null): void {
    this.currentUserSubject.next(user);
  }

  /**
   * Verifica se está autenticado
   */
  isAuthenticated(): boolean {
    return this.currentUserSubject.value !== null && authService.isAuthenticated();
  }

  /**
   * Verifica e renova token se necessário
   */
  private async checkToken(): Promise<void> {
    // Evita refresh se não autenticado
    if (!this.isAuthenticated()) return;

    try {
      await authService.checkAndRefreshToken();
    } catch (error) {
      // Se refresh falhar, limpa o estado
      this.clear();
    }
  }


  /**
   * Limpa o estado (logout)
   */
  clear(): void {
    this.currentUserSubject.next(null);
  }
}

// Exporta instância singleton
export const authStore = new AuthStore();
