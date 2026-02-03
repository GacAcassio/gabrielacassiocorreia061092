import axios, {
  AxiosError,
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from "axios";
import { config } from "../config/config";
import { authService } from "./AuthService";

type FailedRequest = {
  resolve: (value?: any) => void;
  reject: (error: any) => void;
  config: InternalAxiosRequestConfig & { _retry?: boolean };
};

class HttpClient {
  private instance: AxiosInstance;

  // controla refresh concorrente
  private isRefreshing = false;
  private failedQueue: FailedRequest[] = [];

  constructor() {
    this.instance = axios.create({
      baseURL: config.api.baseURL,
      timeout: config.api.timeout,
      // headers: { "Content-Type": "application/json" },
    });

    this.setupInterceptors();
  }

  private processQueue(error: any, token: string | null = null) {
    this.failedQueue.forEach((req) => {
      if (error) {
        req.reject(error);
      } else {
        if (token && req.config.headers) {
          req.config.headers.Authorization = `Bearer ${token}`;
        }
        req.resolve(this.instance(req.config));
      }
    });

    this.failedQueue = [];
  }

  private setupInterceptors(): void {
    // REQUEST: adiciona access token + controla Content-Type
    this.instance.interceptors.request.use(
      (requestConfig: InternalAxiosRequestConfig) => {
        const token = localStorage.getItem(config.auth.tokenKey);

        if (token) {
          requestConfig.headers.Authorization = `Bearer ${token}`;
        }

        // Detecta FormData
        const isFormData =
          typeof FormData !== "undefined" && requestConfig.data instanceof FormData;

        if (isFormData) {
          if (requestConfig.headers && "Content-Type" in requestConfig.headers) {
            delete requestConfig.headers["Content-Type"];
          }
        } else {
          // JSON normal
          requestConfig.headers["Content-Type"] = "application/json";
        }

        return requestConfig;
      },
      (error) => Promise.reject(error)
    );

    // RESPONSE: se 401 -> tenta refresh e retry
    this.instance.interceptors.response.use(
      (response) => response,
      async (error: AxiosError) => {
        const originalRequest = error.config as
          | (InternalAxiosRequestConfig & { _retry?: boolean })
          | undefined;

        const status = error.response?.status;

        // se não tiver config original, só rejeita
        if (!originalRequest) {
          return Promise.reject(error);
        }

        // evita loop infinito
        if (status !== 401 || originalRequest._retry) {
          return Promise.reject(error);
        }

        // não tenta refresh se o erro já veio do refresh endpoint
        if (originalRequest.url?.includes("/auth/refresh")) {
          authService.logout();
          window.location.href = "/login";
          return Promise.reject(error);
        }

        originalRequest._retry = true;

        // se já tem refresh em andamento, coloca na fila
        if (this.isRefreshing) {
          return new Promise((resolve, reject) => {
            this.failedQueue.push({ resolve, reject, config: originalRequest });
          });
        }

        this.isRefreshing = true;

        try {
          // tenta renovar
          const refreshedUser = await authService.refreshToken();

          if (!refreshedUser?.token) {
            throw new Error("Refresh falhou (token vazio)");
          }

          const newToken = refreshedUser.token;

          // atualiza header default do axios instance
          this.instance.defaults.headers.common.Authorization = `Bearer ${newToken}`;

          // resolve fila de requests pendentes
          this.processQueue(null, newToken);

          // reenvia request original
          if (originalRequest.headers) {
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
          }

          return this.instance(originalRequest);
        } catch (refreshError) {
          // se refresh falhar -> rejeita fila e desloga
          this.processQueue(refreshError, null);

          authService.logout();
          window.location.href = "/login";

          return Promise.reject(refreshError);
        } finally {
          this.isRefreshing = false;
        }
      }
    );
  }

  // Métodos HTTP
  async get<T = any>(
    url: string,
    requestConfig?: AxiosRequestConfig
  ): Promise<AxiosResponse<T>> {
    return this.instance.get<T>(url, requestConfig);
  }

  async post<T = any>(
    url: string,
    data?: any,
    requestConfig?: AxiosRequestConfig
  ): Promise<AxiosResponse<T>> {
    return this.instance.post<T>(url, data, requestConfig);
  }

  async put<T = any>(
    url: string,
    data?: any,
    requestConfig?: AxiosRequestConfig
  ): Promise<AxiosResponse<T>> {
    return this.instance.put<T>(url, data, requestConfig);
  }

  async delete<T = any>(
    url: string,
    requestConfig?: AxiosRequestConfig
  ): Promise<AxiosResponse<T>> {
    return this.instance.delete<T>(url, requestConfig);
  }

  async patch<T = any>(
    url: string,
    data?: any,
    requestConfig?: AxiosRequestConfig
  ): Promise<AxiosResponse<T>> {
    return this.instance.patch<T>(url, data, requestConfig);
  }
}

export const httpClient = new HttpClient();
