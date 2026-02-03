package com.project.artists.service;

import com.project.artists.dto.response.SyncResponseDTO;
import com.project.artists.entity.Regional;

import java.util.List;

/**
 * Interface do serviço de sincronização de regionais
 */
public interface RegionalSyncService {
    
    /**
     * Sincroniza regionais com a API externa
     * 
     * @return Resultado da sincronização com estatísticas
     */
    SyncResponseDTO sincronizar();
    
    /**
     * Lista todos os regionais (ativos e inativos)
     * 
     * @return Lista de todos os regionais
     */
    List<Regional> listarTodos();
    
    /**
     * Lista apenas regionais ativos
     * 
     * @return Lista de regionais ativos
     */
    List<Regional> listarAtivos();
}
