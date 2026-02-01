/**
 * DTO para requisição de login
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * DTO para resposta de autenticação
 */
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number; // em segundos
}

/**
 * DTO para renovação de token
 */
export interface RefreshTokenRequest {
  refreshToken: string;
}

/**
 * Model do usuário autenticado
 */
export interface User {
  username: string;
  token: string;
  refreshToken: string;
  expiresAt: number; // timestamp
}
