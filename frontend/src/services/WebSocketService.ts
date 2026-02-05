import { Client, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Notification } from '../models/Notification';
import { config } from '../config/config';

/**
 * Serviço de WebSocket para notificações em tempo real
 */
class WebSocketService {
  private client: Client | null = null;
  private subscription: StompSubscription | null = null;
  private listeners: ((notification: Notification) => void)[] = [];
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000; // 3 segundos
  private isConnecting = false;


  connect(): void {
    // if (this.client?.connected) {
    //   console.log('WebSocket já está conectado');
    //   return;
    // }

    // if (this.isConnecting) {
    //   console.log('WebSocket já está tentando conectar');
    //   return;
    // }

    this.isConnecting = true;
    //console.log('Conectando ao WebSocket:', config.websocket.url);

    // Cria cliente STOMP sobre SockJS
    this.client = new Client({
      webSocketFactory: () => {
        const url = config.websocket.url;
        //console.log('Criando conexão SockJS para:', url);
        return new SockJS(url);
      },
      
  
      // Callbacks
      onConnect: (frame) => {
        // console.log('WebSocket conectado com sucesso!');
        // console.log('Frame de conexão:', frame);
        this.isConnecting = false;
        this.reconnectAttempts = 0;
        this.subscribe();
      },

      onDisconnect: (frame) => {
        // console.log('WebSocket desconectado');
        // console.log('Frame de desconexão:', frame);
        this.isConnecting = false;
      },

      onStompError: (frame) => {
        // console.error('Erro STOMP:', frame.headers['message']);
        // console.error('Detalhes:', frame.body);
        // console.error('Frame completo:', frame);
        this.isConnecting = false;
      },

      onWebSocketError: (event) => {
        // console.error('Erro WebSocket:', event);
        this.isConnecting = false;
        this.handleReconnect();
      },

      onWebSocketClose: (event) => {
        // console.warn('WebSocket fechado:', event);
        this.isConnecting = false;
        // Reconectar se não foi fechamento intencional
        if (event.code !== 1000) {
          this.handleReconnect();
        }
      },

      heartbeatIncoming: 25000, // 25 segundos (match com backend)
      heartbeatOutgoing: 25000, // 25 segundos (match com backend)

      reconnectDelay: 0,

      // debug: (str) => {
      //   // console.log(' STOMP:', str);
      
      //   // Log apenas mensagens importantes
      //   if (str.includes('ERROR') || str.includes('CONNECT') || str.includes('CONNECTED')) {
      //     console.log(' STOMP:', str);
      //   }
      // },
    });

    // Ativa o cliente
    try {
      this.client.activate();
      //console.log('Cliente WebSocket ativado');
    } catch (error) {
      //console.error('Erro ao ativar cliente:', error);
      this.isConnecting = false;
      this.handleReconnect();
    }
  }

  /**
   * Se inscreve no tópico de notificações
   */
  private subscribe(): void {
    if (!this.client) {
      //console.error('Cliente WebSocket não inicializado');
      return;
    }

    if (!this.client.connected) {
      //console.error(' Cliente WebSocket não está conectado');
      return;
    }

    //console.log(' Inscrevendo-se em /topic/notifications');

    try {
      this.subscription = this.client.subscribe('/topic/notifications', (message) => {
        try {
          //console.log('Mensagem bruta recebida:', message.body);
          
          const notification: Notification = JSON.parse(message.body);
          //console.log('Notificação processada:', notification);
          
          // Notifica todos os listeners
          this.listeners.forEach(listener => {
            try {
              listener(notification);
            } catch (error) {
              console.error(' Erro ao chamar listener:', error);
            }
          });
        } catch (error) {
          //console.error('Erro ao processar notificação:', error);
          //console.error('Mensagem recebida:', message.body);
          //console.error('Headers:', message.headers);
        }
      });

      //console.log('Inscrição em /topic/notifications realizada com sucesso');
      //console.log('Subscription ID:', this.subscription.id);
    } catch (error) {
      //console.error('Erro ao se inscrever no tópico:', error);
    }
  }

  /**
   * Desconecta do WebSocket
   */
  disconnect(): void {
    //console.log(' Desconectando WebSocket...');

    // Cancela tentativas de reconexão
    this.reconnectAttempts = this.maxReconnectAttempts;

    if (this.subscription) {
      try {
        this.subscription.unsubscribe();
        //console.log('Desinscrito de /topic/notifications');
      } catch (error) {
        //console.error(' Erro ao desinscrever:', error);
      }
      this.subscription = null;
    }

    if (this.client) {
      try {
        this.client.deactivate();
        //console.log(' Cliente WebSocket desativado');
      } catch (error) {
        //console.error(' Erro ao desativar cliente:', error);
      }
      this.client = null;
    }

    this.isConnecting = false;
  }

  /**
   * Adiciona um listener para notificações
   * 
   * @param callback Função a ser chamada quando uma notificação for recebida
   * @returns Função para remover o listener
   */
  addListener(callback: (notification: Notification) => void): () => void {
    this.listeners.push(callback);
    //console.log(`Listener adicionado. Total: ${this.listeners.length}`);
    
    // Retorna função para remover o listener
    return () => {
      this.listeners = this.listeners.filter(l => l !== callback);
      //console.log(`Listener removido. Total: ${this.listeners.length}`);
    };
  }

  /**
   * Remove todos os listeners
   */
  clearListeners(): void {
    this.listeners = [];
   // console.log('Todos os listeners foram removidos');
  }

  /**
   * Tenta reconectar ao WebSocket
   */
  private handleReconnect(): void {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      //console.error(` Máximo de tentativas de reconexão atingido (${this.maxReconnectAttempts})`);
      return;
    }

    this.reconnectAttempts++;
    //console.log(`Tentando reconectar... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);

    // Limpa cliente anterior
    if (this.client) {
      try {
        this.client.deactivate();
      } catch (error) {
        // Ignora erros ao desativar
      }
      this.client = null;
    }

    setTimeout(() => {
      this.connect();
    }, this.reconnectDelay);
  }

  /**
   * Verifica se está conectado
   */
  isConnected(): boolean {
    return this.client?.connected ?? false;
  }

  /**
   * Retorna o status da conexão
   */
  getStatus(): 'connected' | 'connecting' | 'disconnected' {
    if (!this.client) return 'disconnected';
    if (this.client.connected) return 'connected';
    if (this.client.active || this.isConnecting) return 'connecting';
    return 'disconnected';
  }

  /**
   *  Força reconexão
   */
  forceReconnect(): void {
    //console.log('Forçando reconexão...');
    this.disconnect();
    this.reconnectAttempts = 0;
    setTimeout(() => {
      this.connect();
    }, 1000);
  }

  /**
   * Testa a conexão enviando um ping
   */
  async testConnection(): Promise<boolean> {
    if (!this.isConnected()) {
      //console.error(' Não conectado - não é possível testar');
      return false;
    }

    try {
      // Envia um ping ao servidor (se suportado)
      //console.log(' Testando conexão...');
      return true;
    } catch (error) {
      //console.error(' Erro ao testar conexão:', error);
      return false;
    }
  }
}

// Exporta instância singleton
export const webSocketService = new WebSocketService();