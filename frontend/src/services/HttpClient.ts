import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { config } from '../config/config';
import { authService } from './AuthService';

/**
 * Cliente HTTP com autenticação obrigatória
 * - Adiciona token Bearer automaticamente
 * - Logout automático em 401 (exceto logout manual)
 */
class HttpClient {
  private instance: AxiosInstance;
  private isLoggingOut: boolean = false; // flag de logout manual

  constructor() {
    this.instance = axios.create({
      baseURL: config.api.baseURL,
      timeout: config.api.timeout,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  /**
   * Sinaliza início/fim do logout manual
   */
  public setLoggingOut(value: boolean) {
    this.isLoggingOut = value;
  }

  /**
   * Configura interceptors
   */
  private setupInterceptors(): void {
    // Request interceptor - adiciona token
    this.instance.interceptors.request.use(
      (requestConfig) => {
        const token = localStorage.getItem(config.auth.tokenKey);
        if (token && requestConfig.headers) {
          requestConfig.headers.Authorization = `Bearer ${token}`;
        }
        return requestConfig;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor - logout automático em 401
    this.instance.interceptors.response.use(
      (response) => response,
      async (error) => {
        const status = error.response?.status;

        // Logout automático somente se 401 e não for logout manual
        if (status === 401 && !this.isLoggingOut) {
          console.log('❌ Token inválido/expirado (401) - fazendo logout automático');

          // Limpa tokens
          authService.logout();

          window.location.href = '/home';
          
        }

        return Promise.reject(error);
      }
    );
  }

  /**
   * GET request
   */
  async get<T = any>(url: string, requestConfig?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.instance.get<T>(url, requestConfig);
  }

  /**
   * POST request
   */
  async post<T = any>(url: string, data?: any, requestConfig?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.instance.post<T>(url, data, requestConfig);
  }

  /**
   * PUT request
   */
  async put<T = any>(url: string, data?: any, requestConfig?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.instance.put<T>(url, data, requestConfig);
  }

  /**
   * DELETE request
   */
  async delete<T = any>(url: string, requestConfig?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.instance.delete<T>(url, requestConfig);
  }

  /**
   * PATCH request
   */
  async patch<T = any>(url: string, data?: any, requestConfig?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.instance.patch<T>(url, data, requestConfig);
  }
}

export const httpClient = new HttpClient();
