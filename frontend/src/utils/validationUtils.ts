/**
 * Utilitários de validação
 */

/**
 * Valida se o email está em formato válido
 */
export const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

/**
 * Valida se a senha atende aos requisitos mínimos
 */
export const isValidPassword = (password: string): boolean => {
  // Mínimo 6 caracteres
  return password.length >= 6;
};

/**
 * Valida se o nome tem tamanho adequado
 */
export const isValidName = (name: string, minLength: number = 2, maxLength: number = 200): boolean => {
  const trimmedName = name.trim();
  return trimmedName.length >= minLength && trimmedName.length <= maxLength;
};

/**
 * Valida se o ano está em formato válido
 */
export const isValidYear = (year: number | undefined): boolean => {
  if (!year) return true; // Ano é opcional
  const currentYear = new Date().getFullYear();
  return year >= 1900 && year <= currentYear + 1;
};

/**
 * Remove espaços extras de uma string
 */
export const sanitizeString = (str: string): string => {
  return str.trim().replace(/\s+/g, ' ');
};
