import { authService } from '../AuthService';
import { authStore } from '../../stores/AuthStore';
import { LoginRequest } from '../../models';
import { httpClient } from '../HttpClient';


/**
 * Facade para operações de autenticação
 * Centraliza as chamadas aos services e gerencia o estado na store
 */
class AuthFacade {
  /**
   * Realiza login do usuário
   */
  async login(credentials: LoginRequest): Promise<void> {
    try {
      const user = await authService.login(credentials);
      authStore.setUser(user);
    } catch (error: any) {
      throw error;
    }
  }

  /**
   * Realiza logout do usuário
   */
  logout(): void {  
      authService.logout();
      authStore.clear();
      window.dispatchEvent(new Event('auth:logout'));
  }


  /**
   * Renova o token de acesso
   */
  async refreshToken(): Promise<void> {
    try {
      const user = await authService.refreshToken();
      if (user) {
        authStore.setUser(user);
      } else {
        // Se refresh retornou null, logout manual
        this.logout();
      }
    } catch (error: any) {
      this.logout();
      throw error;
    }
  }


  /**
   * Verifica se o usuário está autenticado
   */
  isAuthenticated(): boolean {
    return authStore.isAuthenticated();
  }

  /**
   * Inicializa o estado de autenticação
   * Deve ser chamado ao carregar a aplicação
   */
  initialize(): void {
      const user = authService.getCurrentUser();
      if (user && authService.isAuthenticated()) {
          authStore.setUser(user);
      } else {
          authStore.clear();
      }
  }
}

export const authFacade = new AuthFacade();
