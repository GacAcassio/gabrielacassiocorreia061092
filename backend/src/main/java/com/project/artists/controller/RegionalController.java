package com.project.artists.controller;

import com.project.artists.dto.response.SyncResponseDTO;
import com.project.artists.entity.Regional;
import com.project.artists.service.RegionalSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para operações com regionais
 * 
 * Endpoints:
 * - POST /api/v1/regionais/sync - Trigger manual de sincronização
 * - GET /api/v1/regionais - Listar regionais ativos
 * - GET /api/v1/regionais/all - Listar todos (ativos e inativos)
 */
@RestController
@RequestMapping("/api/v1/regionais")
@Tag(name = "Regionais", description = "Sincronização e consulta de regionais")
public class RegionalController {
    
    @Autowired
    private RegionalSyncService regionalSyncService;
    
    /**
     * Endpoint para trigger manual de sincronização
     * 
     * Complexidade: O(n + m) onde:
     * - n = regionais na API externa
     * - m = regionais no banco local
     * 
     * @return Resultado da sincronização com estatísticas
     */
    @PostMapping("/sync")
    @Operation(
        summary = "Sincronizar regionais",
        description = "Executa sincronização manual com a API externa. " +
                     "Identifica novos, removidos e alterados de forma eficiente."
    )
    public ResponseEntity<SyncResponseDTO> sincronizar() {
        SyncResponseDTO result = regionalSyncService.sincronizar();
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * Lista apenas regionais ativos
     * 
     * @return Lista de regionais ativos
     */
    @GetMapping
    @Operation(
        summary = "Listar regionais ativos",
        description = "Retorna apenas regionais com status ativo=true"
    )
    public ResponseEntity<List<Regional>> listarAtivos() {
        List<Regional> regionais = regionalSyncService.listarAtivos();
        return ResponseEntity.ok(regionais);
    }
    
    /**
     * Lista todos os regionais (ativos e inativos)
     * 
     * @return Lista de todos os regionais
     */
    @GetMapping("/all")
    @Operation(
        summary = "Listar todos os regionais",
        description = "Retorna todos os regionais, incluindo inativos"
    )
    public ResponseEntity<List<Regional>> listarTodos() {
        List<Regional> regionais = regionalSyncService.listarTodos();
        return ResponseEntity.ok(regionais);
    }
}
