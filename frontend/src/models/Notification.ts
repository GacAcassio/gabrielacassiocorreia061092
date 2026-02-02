/**
 * Tipos de notificação (sincronizado com backend)
 */
export enum NotificationType {
  ALBUM_CREATED = 'ALBUM_CREATED',
  ALBUM_UPDATED = 'ALBUM_UPDATED',
  ALBUM_DELETED = 'ALBUM_DELETED',
  ALBUM_COVER_UPLOADED = 'ALBUM_COVER_UPLOADED',
  ARTIST_CREATED = 'ARTIST_CREATED',
  ARTIST_UPDATED = 'ARTIST_UPDATED',
  ARTIST_DELETED = 'ARTIST_DELETED',
  SYSTEM_NOTIFICATION = 'SYSTEM_NOTIFICATION',
}

/**
 * Interface da notificação recebida via WebSocket
 */
export interface Notification {
  type: NotificationType;
  title: string;
  message: string;
  data?: any;
  timestamp: string;
}

/**
 * Mapa de ícones por tipo de notificação
 */
export const NotificationIcons: Record<NotificationType, string> = {
  [NotificationType.ALBUM_CREATED]: '🎵',
  [NotificationType.ALBUM_UPDATED]: '✏️',
  [NotificationType.ALBUM_DELETED]: '🗑️',
  [NotificationType.ALBUM_COVER_UPLOADED]: '📸',
  [NotificationType.ARTIST_CREATED]: '🎤',
  [NotificationType.ARTIST_UPDATED]: '✏️',
  [NotificationType.ARTIST_DELETED]: '🗑️',
  [NotificationType.SYSTEM_NOTIFICATION]: '🔔',
};

/**
 * Mapa de cores por tipo de notificação
 */
export const NotificationColors: Record<NotificationType, string> = {
  [NotificationType.ALBUM_CREATED]: 'bg-green-500',
  [NotificationType.ALBUM_UPDATED]: 'bg-blue-500',
  [NotificationType.ALBUM_DELETED]: 'bg-red-500',
  [NotificationType.ALBUM_COVER_UPLOADED]: 'bg-purple-500',
  [NotificationType.ARTIST_CREATED]: 'bg-green-500',
  [NotificationType.ARTIST_UPDATED]: 'bg-blue-500',
  [NotificationType.ARTIST_DELETED]: 'bg-red-500',
  [NotificationType.SYSTEM_NOTIFICATION]: 'bg-gray-500',
};
