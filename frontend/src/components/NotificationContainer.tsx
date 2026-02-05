import React, { useEffect, useState } from 'react';
import { webSocketService } from '../services/WebSocketService';
import { Notification } from '../models/Notification';
import NotificationToast from './NotificationToast';

/**
 * Container para gerenciar e exibir notificações
 */
const NotificationContainer: React.FC = () => {
  const [notifications, setNotifications] = useState<(Notification & { id: string })[]>([]);

  useEffect(() => {
    // Conecta ao WebSocket
    webSocketService.connect();

    // Adiciona listener para novas notificações
    const removeListener = webSocketService.addListener((notification) => {
      const notificationWithId = {
        ...notification,
        id: `${Date.now()}-${Math.random()}`,
      };
      
      setNotifications(prev => [...prev, notificationWithId]);

      // Toca som (opcional)
      playNotificationSound( );
    });

    // Cleanup
    return () => {
      removeListener();
      webSocketService.disconnect();
    };
  }, []);

  const removeNotification = (id: string) => {
    setNotifications(prev => prev.filter(n => n.id !== id));
  };

  const playNotificationSound = () => {
    // Som de notificação 
     const audio = new Audio('/notification_message-notification-21-270139.mp3');
    audio.play().catch(e => console.log('Não foi possível tocar som'));
  };

  return (
    <div className="fixed top-4 right-4 z-50 space-y-2 pointer-events-none">
      <div className="pointer-events-auto space-y-2">
        {notifications.map(notification => (
          <NotificationToast
            key={notification.id}
            notification={notification}
            onClose={() => removeNotification(notification.id)}
          />
        ))}
      </div>
    </div>
  );
};

export default NotificationContainer;
