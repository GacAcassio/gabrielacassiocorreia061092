import React from 'react';

interface LoadingProps {
  message?: string;
  size?: 'small' | 'medium' | 'large';
}

/**
 * Componente de loading/spinner
 */
const Loading: React.FC<LoadingProps> = ({ message = 'Carregando...', size = 'medium' }) => {
  const sizeClasses = {
    small: 'w-6 h-6',
    medium: 'w-12 h-12',
    large: 'w-16 h-16',
  };

  return (
    <div className="flex flex-col items-center justify-center p-8">
      <div
        className={`${sizeClasses[size]} border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin`}
      />
      {message && <p className="mt-4 text-gray-600">{message}</p>}
    </div>
  );
};

export default Loading;
