import axios from "axios";
import { LoginRequest, AuthResponse, RefreshTokenRequest, User } from "../models";
import { config } from "../config/config";
import { webSocketService } from "./WebSocketService";

/**
 * Serviço de autenticação
 * Responsável por login, logout e renovação de tokens
 */
class AuthService {
  /**
   * Extrai expiração do JWT (em ms)
   */
  getTokenExpirationTime(): number | null {
    const token = localStorage.getItem(config.auth.tokenKey);
    if (!token) return null;

    try {
      const parts = token.split(".");
      if (parts.length !== 3) return null;

      const payloadBase64Url = parts[1];
      const payloadBase64 = payloadBase64Url.replace(/-/g, "+").replace(/_/g, "/");

      const jsonPayload = decodeURIComponent(
        atob(payloadBase64)
          .split("")
          .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
          .join("")
      );

      const payload = JSON.parse(jsonPayload);

      if (!payload?.exp || typeof payload.exp !== "number") return null;

      // exp vem em segundos
      return payload.exp * 1000;
    } catch (err) {
      console.error("Erro ao extrair expiração do token:", err);
      return null;
    }
  }

  /**
   * Realiza login do usuário
   * Usa axios direto (sem interceptors) para evitar loop
   */
  async login(credentials: LoginRequest): Promise<User> {
    const url = `${config.api.baseURL}/auth/login`;

    try {
      const response = await axios.post<AuthResponse>(url, credentials, {
        headers: { "Content-Type": "application/json" },
        timeout: 10000,
        validateStatus: (status) => status >= 200 && status < 300,
      });

      const { accessToken, refreshToken, expiresIn } = response.data;

      if (!accessToken || !refreshToken || !expiresIn) {
        throw new Error("Resposta do servidor está incompleta");
      }

      // Salva tokens
      localStorage.setItem(config.auth.tokenKey, accessToken);
      localStorage.setItem(config.auth.refreshTokenKey, refreshToken);
      localStorage.setItem(config.auth.usernameKey, credentials.username);

      const expiresAt = Date.now() + expiresIn * 1000;
      localStorage.setItem(config.auth.expiresAtKey, expiresAt.toString());

      return {
        username: credentials.username,
        token: accessToken,
        refreshToken,
        expiresAt,
      };
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response) {
          const message =
            (error.response.data as any)?.message ||
            (error.response.data as any)?.error ||
            error.response.statusText;

          if (error.response.status === 401) throw new Error("Usuário ou senha incorretos");
          if (error.response.status === 403) throw new Error("Acesso negado");
          if (error.response.status === 404) throw new Error("Endpoint de login não encontrado");
          if (error.response.status >= 500) throw new Error("Erro no servidor: " + message);

          throw new Error(message || `Erro ${error.response.status}`);
        }

        if (error.request) {
          throw new Error("Servidor não respondeu. Verifique se o backend está rodando.");
        }

        throw new Error("Erro ao configurar requisição: " + error.message);
      }

      throw new Error(error.message || "Erro desconhecido ao fazer login");
    }
  }

  /**
   * Logout do usuário
   */
  logout(): void {
    localStorage.removeItem(config.auth.tokenKey);
    localStorage.removeItem(config.auth.refreshTokenKey);
    localStorage.removeItem(config.auth.expiresAtKey);
    localStorage.removeItem(config.auth.usernameKey);

    sessionStorage.removeItem(config.auth.tokenKey);
    sessionStorage.removeItem(config.auth.refreshTokenKey);
    sessionStorage.removeItem(config.auth.expiresAtKey);
    sessionStorage.removeItem(config.auth.usernameKey);

    webSocketService.disconnect();

    window.dispatchEvent(new CustomEvent("logout"));
  }

  /**
   * Renova o token de acesso (COM ROTAÇÃO)
   * - backend retorna accessToken NOVO
   * - backend retorna refreshToken NOVO
   */
  async refreshToken(): Promise<User | null> {
    const refreshToken = localStorage.getItem(config.auth.refreshTokenKey);
    const username = localStorage.getItem(config.auth.usernameKey);

    if (!refreshToken || !username) return null;

    try {
      const request: RefreshTokenRequest = { refreshToken };

      const response = await axios.post<AuthResponse>(
        `${config.api.baseURL}/auth/refresh`,
        request,
        { headers: { "Content-Type": "application/json" }, timeout: 10000 }
      );

      const { accessToken, refreshToken: newRefreshToken, expiresIn } = response.data;

      if (!accessToken) {
        throw new Error("Backend não retornou accessToken no refresh");
      }

      // Rotation: refresh token novo é obrigatório
      if (!newRefreshToken) {
        throw new Error("Backend não retornou refreshToken novo (rotation ativa)");
      }

      localStorage.setItem(config.auth.tokenKey, accessToken);
      localStorage.setItem(config.auth.refreshTokenKey, newRefreshToken);

      const expiresAt = Date.now() + expiresIn * 1000;
      localStorage.setItem(config.auth.expiresAtKey, expiresAt.toString());

      return {
        username,
        token: accessToken,
        refreshToken: newRefreshToken,
        expiresAt,
      };
    } catch (error: any) {
      console.error("Refresh token error:", error);

      // Se refresh falhou por token inválido/expirado -> logout
      if (axios.isAxiosError(error)) {
        const status = error.response?.status;

        if (status === 401 || status === 403) {
          this.logout();
        }

        // erro de rede/timeout -> não derruba sessão automaticamente
        return null;
      }

      return null;
    }
  }

  /**
   * Verifica se o usuário está autenticado
   */
  isAuthenticated(): boolean {
    const token = localStorage.getItem(config.auth.tokenKey);
    if (!token) return false;

    const exp = this.getTokenExpirationTime();
    if (!exp) return false;

    return Date.now() < exp;
  }

  /**
   * Usuário atual do localStorage
   */
  getCurrentUser(): User | null {
    const token = localStorage.getItem(config.auth.tokenKey);
    const refreshToken = localStorage.getItem(config.auth.refreshTokenKey);
    const expiresAt = localStorage.getItem(config.auth.expiresAtKey);
    const username = localStorage.getItem(config.auth.usernameKey);

    if (!token || !refreshToken || !username) return null;

    return {
      username,
      token,
      refreshToken,
      expiresAt: expiresAt ? parseInt(expiresAt) : Date.now(),
    };
  }

  /**
   * Se o token estiver perto de expirar, tenta renovar
   */
  async checkAndRefreshToken(): Promise<void> {
    const exp = this.getTokenExpirationTime();
    if (!exp) return;

    const now = Date.now();
    const oneMinuteInMs = 60 * 1000;

    if (exp - now < oneMinuteInMs) {
      await this.refreshToken();
    }
  }
}

export const authService = new AuthService();
