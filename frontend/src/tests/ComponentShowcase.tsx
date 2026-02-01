import React, { useState } from 'react';
import { Loading, ErrorMessage } from '../components';

/**
 * Componente para visualizar e testar todos os componentes criados
 */
const ComponentShowcase: React.FC = () => {
  const [showError, setShowError] = useState(false);
  const [retryCount, setRetryCount] = useState(0);

  const handleRetry = () => {
    setRetryCount(prev => prev + 1);
    alert(`Retry clicado ${retryCount + 1} vez(es)`);
  };

  return (
    <div className="max-w-6xl mx-auto p-8">
      <h1 className="text-4xl font-bold mb-8 text-gray-800">
        Showcase de Componentes
      </h1>

      {/* Loading Component */}
      <section className="bg-white shadow-lg rounded-lg p-6 mb-8">
        <h2 className="text-2xl font-bold mb-4 text-gray-700">
          Loading Component
        </h2>
        
        <div className="space-y-6">
          <div className="border-l-4 border-blue-500 pl-4">
            <p className="font-semibold mb-2">Tamanho: Small</p>
            <Loading size="small" message="Carregando dados..." />
          </div>

          <div className="border-l-4 border-green-500 pl-4">
            <p className="font-semibold mb-2">Tamanho: Medium (padrão)</p>
            <Loading size="medium" message="Processando requisição..." />
          </div>

          <div className="border-l-4 border-purple-500 pl-4">
            <p className="font-semibold mb-2">Tamanho: Large</p>
            <Loading size="large" message="Aguarde um momento..." />
          </div>

          <div className="border-l-4 border-gray-500 pl-4">
            <p className="font-semibold mb-2">Sem mensagem</p>
            <Loading size="medium" message="" />
          </div>
        </div>
      </section>

      {/* ErrorMessage Component */}
      <section className="bg-white shadow-lg rounded-lg p-6 mb-8">
        <h2 className="text-2xl font-bold mb-4 text-gray-700">
          ErrorMessage Component
        </h2>
        
        <button
          onClick={() => setShowError(!showError)}
          className="mb-4 bg-blue-500 hover:bg-blue-600 text-white font-semibold px-6 py-2 rounded-lg transition-colors"
        >
          {showError ? 'Ocultar Erro' : 'Mostrar Erro'}
        </button>

        {showError && (
          <div className="space-y-4">
            <ErrorMessage
              message="Erro ao carregar dados. Tente novamente."
              onRetry={handleRetry}
            />
            
            <ErrorMessage
              message="Sem conexão com o servidor. Verifique sua internet e tente novamente."
            />

            <ErrorMessage
              message="Erro 429: Limite de requisições excedido. Aguarde alguns instantes."
              onRetry={() => alert('Tentando novamente...')}
            />
          </div>
        )}
      </section>

      {/* Tailwind Colors */}
      <section className="bg-white shadow-lg rounded-lg p-6 mb-8">
        <h2 className="text-2xl font-bold mb-4 text-gray-700">
           Paleta de Cores Customizada (Primary)
        </h2>
        
        <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
          {[
            { shade: 50, name: 'Lightest' },
            { shade: 100, name: 'Lighter' },
            { shade: 200, name: 'Light' },
            { shade: 300, name: 'Light-Medium' },
            { shade: 400, name: 'Medium' },
            { shade: 500, name: 'Base' },
            { shade: 600, name: 'Medium-Dark' },
            { shade: 700, name: 'Dark' },
            { shade: 800, name: 'Darker' },
            { shade: 900, name: 'Darkest' },
          ].map(({ shade, name }) => (
            <div key={shade} className="text-center">
              <div 
                className={`bg-primary-${shade} h-20 rounded-lg shadow-md border border-gray-200`}
              />
              <p className="text-sm font-semibold mt-2 text-gray-700">{shade}</p>
              <p className="text-xs text-gray-500">{name}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Botões */}
      <section className="bg-white shadow-lg rounded-lg p-6 mb-8">
        <h2 className="text-2xl font-bold mb-4 text-gray-700">
           Botões com Tailwind
        </h2>
        
        <div className="space-y-4">
          <div>
            <p className="font-semibold mb-2">Cores:</p>
            <div className="flex flex-wrap gap-2">
              <button className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded">
                Blue
              </button>
              <button className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded">
                Green
              </button>
              <button className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded">
                Red
              </button>
              <button className="bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded">
                Yellow
              </button>
              <button className="bg-purple-500 hover:bg-purple-600 text-white px-4 py-2 rounded">
                Purple
              </button>
              <button className="bg-primary-500 hover:bg-primary-600 text-white px-4 py-2 rounded">
                Primary
              </button>
            </div>
          </div>

          <div>
            <p className="font-semibold mb-2">Tamanhos:</p>
            <div className="flex flex-wrap items-center gap-2">
              <button className="bg-blue-500 text-white px-2 py-1 rounded text-sm">
                Small
              </button>
              <button className="bg-blue-500 text-white px-4 py-2 rounded">
                Medium
              </button>
              <button className="bg-blue-500 text-white px-6 py-3 rounded text-lg">
                Large
              </button>
            </div>
          </div>

          <div>
            <p className="font-semibold mb-2">Variantes:</p>
            <div className="flex flex-wrap gap-2">
              <button className="bg-blue-500 text-white px-4 py-2 rounded shadow-md">
                Solid
              </button>
              <button className="border-2 border-blue-500 text-blue-500 px-4 py-2 rounded hover:bg-blue-50">
                Outline
              </button>
              <button className="text-blue-500 px-4 py-2 rounded hover:bg-blue-50">
                Ghost
              </button>
              <button className="bg-blue-500 text-white px-4 py-2 rounded opacity-50 cursor-not-allowed">
                Disabled
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* Cards */}
      <section className="bg-white shadow-lg rounded-lg p-6 mb-8">
        <h2 className="text-2xl font-bold mb-4 text-gray-700">
           Cards de Exemplo
        </h2>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="border rounded-lg p-4 hover:shadow-lg transition-shadow">
            <h3 className="font-bold text-lg mb-2">Card Simples</h3>
            <p className="text-gray-600">Conteúdo do card com borda e hover.</p>
          </div>

          <div className="bg-gradient-to-br from-blue-500 to-purple-600 text-white rounded-lg p-4 shadow-lg">
            <h3 className="font-bold text-lg mb-2">Card Gradiente</h3>
            <p>Card com gradiente e texto branco.</p>
          </div>

          <div className="border-2 border-primary-500 bg-primary-50 rounded-lg p-4">
            <h3 className="font-bold text-lg mb-2 text-primary-700">Card Primary</h3>
            <p className="text-primary-600">Card usando cores customizadas.</p>
          </div>
        </div>
      </section>

      {/* Grid Responsivo */}
      <section className="bg-white shadow-lg rounded-lg p-6 mb-8">
        <h2 className="text-2xl font-bold mb-4 text-gray-700">
          📱 Grid Responsivo
        </h2>
        
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          {[1, 2, 3, 4, 5, 6, 7, 8].map(num => (
            <div 
              key={num} 
              className="bg-primary-100 border border-primary-300 rounded-lg p-4 text-center"
            >
              <p className="font-semibold text-primary-700">Item {num}</p>
            </div>
          ))}
        </div>
        <p className="text-sm text-gray-500 mt-4">
          * Redimensione a janela para ver a responsividade
        </p>
      </section>

      {/* Formulário de Exemplo */}
      <section className="bg-white shadow-lg rounded-lg p-6 mb-8">
        <h2 className="text-2xl font-bold mb-4 text-gray-700">
          Elementos de Formulário
        </h2>
        
        <form className="space-y-4">
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-1">
              Input Text
            </label>
            <input
              type="text"
              placeholder="Digite algo..."
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-1">
              Textarea
            </label>
            <textarea
              placeholder="Escreva uma mensagem..."
              rows={4}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-1">
              Select
            </label>
            <select className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500">
              <option>Opção 1</option>
              <option>Opção 2</option>
              <option>Opção 3</option>
            </select>
          </div>

          <div className="flex items-center">
            <input
              type="checkbox"
              id="checkbox"
              className="w-4 h-4 text-primary-600 border-gray-300 rounded focus:ring-primary-500"
            />
            <label htmlFor="checkbox" className="ml-2 text-sm text-gray-700">
              Checkbox de exemplo
            </label>
          </div>
        </form>
      </section>
    </div>
  );
};

export default ComponentShowcase;
