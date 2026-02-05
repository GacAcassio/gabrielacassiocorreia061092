import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Notification, NotificationIcons, NotificationColors } from '../models/Notification';

interface NotificationToastProps {
  notification: Notification;
  onClose: () => void;
}

/**
 * Componente de Toast para exibir notificações
 * Permite navegação ao clicar (se tiver dados com ID)
 */
const NotificationToast: React.FC<NotificationToastProps> = ({ notification, onClose }) => {
  const navigate = useNavigate();
  const [isVisible, setIsVisible] = useState(false);
  const [isLeaving, setIsLeaving] = useState(false);

  useEffect(() => {
    // Animação de entrada
    setTimeout(() => setIsVisible(true), 10);

    // Auto-dismiss após 5 segundos
    const timer = setTimeout(() => {
      handleClose();
    }, 5000);

    return () => clearTimeout(timer);
  }, []);

  const handleClose = () => {
    setIsLeaving(true);
    setTimeout(() => {
      setIsVisible(false);
      onClose();
    }, 300);
  };

  /**
   * Navega ao álbum ou artista quando clica na notificação
   */
  const handleClick = () => {
    if (!notification.data) return;

    // Extrai ID do data
    const id = notification.data.id || notification.data.albumId || notification.data.artistId;
    if (!id) return;

    // Determina rota baseado no tipo
    let route = '';
    
    if (notification.type.includes('ALBUM')) {
      route = `/albums/${id}`;
    } else if (notification.type.includes('ARTIST')) {
      route = `/artists/${id}`;
    }

    if (route) {
      navigate(route);
      handleClose();
    }
  };

  const icon = NotificationIcons[notification.type];
  const colorClass = NotificationColors[notification.type];
  const isClickable = notification.data && (notification.data.id || notification.data.albumId || notification.data.artistId);

  return (
    <div
      className={`
        transform transition-all duration-300 ease-in-out
        ${isVisible && !isLeaving ? 'translate-x-0 opacity-100' : 'translate-x-full opacity-0'}
      `}
    >
      <div 
        className={`
          bg-white rounded-lg shadow-lg overflow-hidden max-w-sm w-full border-l-4 border-primary-600
          ${isClickable ? 'cursor-pointer hover:shadow-xl transition-shadow' : ''}
        `}
        onClick={isClickable ? handleClick : undefined}
      >
        <div className="p-4">
          <div className="flex items-start">
            {/* Ícone */}
            <div className={`flex-shrink-0 w-10 h-10 rounded-full ${colorClass} flex items-center justify-center text-white text-xl`}>
              {icon}
            </div>

            {/* Conteúdo */}
            <div className="ml-3 flex-1">
              <p className="text-sm font-semibold text-gray-900">
                {notification.title}
              </p>
              <p className="mt-1 text-sm text-gray-600">
                {notification.message}
              </p>
              
              {/* Indicador de clicável */}
              {isClickable && (
                <div className="mt-2 flex items-center text-xs text-primary-600 font-medium">
                  <span>Clique para visualizar</span>
                  <svg className="w-3 h-3 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                  </svg>
                </div>
              )}
              
              <p className="mt-2 text-xs text-gray-400">
                {new Date(notification.timestamp).toLocaleTimeString('pt-BR')}
              </p>
            </div>

            {/* Botão de fechar */}
            <button
              onClick={(e) => {
                e.stopPropagation();
                handleClose();
              }}
              className="ml-4 flex-shrink-0 text-gray-400 hover:text-gray-600 transition-colors"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default NotificationToast;
