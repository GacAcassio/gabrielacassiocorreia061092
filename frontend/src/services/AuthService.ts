import axios from 'axios';
import { httpClient } from './HttpClient';
import { LoginRequest, AuthResponse, RefreshTokenRequest, User } from '../models';
import { config } from '../config/config';

/**
 * Serviço de autenticação
 * Responsável por login, logout e renovação de tokens
 */
class AuthService {
  getTokenExpirationTime() {
    throw new Error('Method not implemented.');
  }
  /**
   * Realiza login do usuário
   * Usa axios diretamente (sem interceptors) para evitar loop
   */
  async login(credentials: LoginRequest): Promise<User> {
    const url = `${config.api.baseURL}/auth/login`;
    
    // console.log('INICIANDO LOGIN');
    // console.log('URL completa:', url);
    // console.log('Credentials:', { username: credentials.username, password: '***' });
    
    try {
      // Tenta fazer a requisição
      // console.log('Enviando requisição...');
      
      const response = await axios.post<AuthResponse>(
        url,
        credentials,
        {
          headers: {
            'Content-Type': 'application/json',
          },
          // Timeout de 10 segundos
          timeout: 10000,
          // Valida status codes
          validateStatus: (status) => {
            // console.log('Status recebido:', status);
            return status >= 200 && status < 300;
          },
        }
      );
      
      // console.log('Resposta recebida com sucesso!');
      // console.log('Status:', response.status);
      // console.log('Headers:', response.headers);
      // console.log('Data keys:', Object.keys(response.data));
      
      const { accessToken, refreshToken, expiresIn } = response.data;

      // Validar se os campos existem
      if (!accessToken || !refreshToken || !expiresIn) {
        // console.error('Resposta inválida - faltam campos:', response.data);
        throw new Error('Resposta do servidor está incompleta');
      }

      // console.log('Tokens recebidos:', {
      //   accessToken: accessToken.substring(0, 20) + '...',
      //   refreshToken: refreshToken.substring(0, 20) + '...',
      //   expiresIn
      // });

      // Calcula timestamp de expiração
      const expiresAt = Date.now() + (expiresIn * 1000);

      // Salva no localStorage
      localStorage.setItem(config.auth.tokenKey, accessToken);
      localStorage.setItem(config.auth.refreshTokenKey, refreshToken);
      localStorage.setItem(config.auth.expiresAtKey, expiresAt.toString());
      localStorage.setItem(config.auth.usernameKey, credentials.username);

      // console.log('Dados salvos no localStorage');
      // console.log('LOGIN CONCLUÍDO COM SUCESSO!');

      return {
        username: credentials.username,
        token: accessToken,
        refreshToken: refreshToken,
        expiresAt,
      };
    } catch (error: any) {
      // console.error('ERRO NO LOGIN');
      // console.error('URL tentada:', url);
      
      // Analisa o tipo de erro
      if (axios.isAxiosError(error)) {
        console.error('🔍 É um AxiosError');
        
        if (error.response) {
          // O servidor respondeu com um status de erro
          // console.error('Servidor respondeu com erro');
          // console.error('Status:', error.response.status);
          // console.error('Headers:', error.response.headers);
          // console.error('Data:', error.response.data);
                 
          //Extrai mensagem de erro
          const message = error.response.data?.message 
            || error.response.data?.error 
            || error.response.statusText;
          
          if (error.response.status === 401) {
            throw new Error('Usuário ou senha incorretos');
          } else if (error.response.status === 403) {
            throw new Error('Acesso negado');
          } else if (error.response.status === 404) {
            throw new Error('Endpoint de login não encontrado. Verifique o backend.');
          } else if (error.response.status >= 500) {
            throw new Error('Erro no servidor: ' + message);
          } else {
            throw new Error(message || `Erro ${error.response.status}`);
          }
          
          
        } else if (error.request) {
          // A requisição foi feita mas não houve resposta
          // console.error('Requisição enviada mas sem resposta');
          
          // Verifica se é erro de rede
          if (error.code === 'ECONNREFUSED') {
            throw new Error('Backend não está respondendo. Verifique se está rodando na porta 8080.');
          } else if (error.code === 'ERR_NETWORK') {
            throw new Error('Erro de rede. Verifique CORS e se o backend está acessível.');
          } else {
            throw new Error('Servidor não respondeu. Verifique se o backend está rodando em ' + config.api.baseURL);
          }
        } else {
          // Erro na configuração da requisição
          //console.error(' Erro na configuração:', error.message);
          throw new Error('Erro ao configurar requisição: ' + error.message);
        }
      } else {
        // Erro não identificado
        //console.error('Erro desconhecido:', error);
        throw new Error(error.message || 'Erro desconhecido ao fazer login');
      }
    }
  }

  /**
   * Realiza logout do usuário
   */
  logout(): void {
    localStorage.removeItem(config.auth.tokenKey);
    localStorage.removeItem(config.auth.refreshTokenKey);
    localStorage.removeItem(config.auth.expiresAtKey);
    localStorage.removeItem(config.auth.usernameKey);
  }

  /**
   * Renova o token de acesso
   */
  async refreshToken(): Promise<User | null> {
    try {
      const refreshToken = localStorage.getItem(config.auth.refreshTokenKey);
      const username = localStorage.getItem(config.auth.usernameKey);

      if (!refreshToken || !username) {
        return null;
      }

      const request: RefreshTokenRequest = { refreshToken };
      
      // Usa axios diretamente para refresh também
      const response = await axios.post<AuthResponse>(
        `${config.api.baseURL}/auth/refresh`,
        request,
        {
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );
      
      const { accessToken, refreshToken: newRefreshToken, expiresIn } = response.data;

      const expiresAt = Date.now() + (expiresIn * 1000);

      localStorage.setItem(config.auth.tokenKey, accessToken);
      localStorage.setItem(config.auth.refreshTokenKey, newRefreshToken);
      localStorage.setItem(config.auth.expiresAtKey, expiresAt.toString());

      return {
        username,
        token: accessToken,
        refreshToken: newRefreshToken,
        expiresAt,
      };
    } catch (error) {
      console.error('Refresh token error:', error);
      this.logout();
      return null;
    }
  }

  /**
   * Verifica se o usuário está autenticado
   */
  isAuthenticated(): boolean {
    const token = localStorage.getItem(config.auth.tokenKey);
    const expiresAt = localStorage.getItem(config.auth.expiresAtKey);

    if (!token || !expiresAt) {
      return false;
    }

    // Verifica se o token expirou
    const now = Date.now();
    if (now >= parseInt(expiresAt)) {
      this.logout();
      return false;
    }

    return true;
  }

  /**
   * Obtém o usuário atual do localStorage
   */
  getCurrentUser(): User | null {
    const token = localStorage.getItem(config.auth.tokenKey);
    const refreshToken = localStorage.getItem(config.auth.refreshTokenKey);
    const expiresAt = localStorage.getItem(config.auth.expiresAtKey);
    const username = localStorage.getItem(config.auth.usernameKey);

    if (!token || !refreshToken || !expiresAt || !username) {
      return null;
    }

    return {
      username,
      token,
      refreshToken,
      expiresAt: parseInt(expiresAt),
    };
  }

  /**
   * Verifica se o token está próximo de expirar (menos de 1 minuto)
   * e tenta renovar automaticamente
   */
  async checkAndRefreshToken(): Promise<void> {
    const expiresAt = localStorage.getItem(config.auth.expiresAtKey);
    
    if (!expiresAt) {
      return;
    }

    const now = Date.now();
    const expiresAtTimestamp = parseInt(expiresAt);
    const oneMinuteInMs = 60 * 1000;

    // Se faltar menos de 1 minuto para expirar, renova
    if (expiresAtTimestamp - now < oneMinuteInMs) {
      await this.refreshToken();
    }
  }
}

export const authService = new AuthService();
