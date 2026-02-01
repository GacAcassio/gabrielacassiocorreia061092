/**
 * Configurações da aplicação
 */
export const config = {
  api: {
    baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8085/api/v1',
    timeout: 30000,
  },
  websocket: {
    url: process.env.REACT_APP_WS_URL || 'http://localhost:8085/ws',
  },
  auth: {
    tokenKey: 'access_token',
    refreshTokenKey: 'refresh_token',
    expiresAtKey: 'expires_at',
    usernameKey: 'username',
  },
  pagination: {
    defaultPageSize: 10,
    defaultPage: 0,
  },
};
