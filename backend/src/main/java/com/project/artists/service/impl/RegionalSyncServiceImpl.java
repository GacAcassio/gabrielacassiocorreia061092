package com.project.artists.service.impl;

import com.project.artists.client.RegionalApiClient;
import com.project.artists.dto.external.RegionalExternoDTO;
import com.project.artists.dto.response.SyncResponseDTO;
import com.project.artists.entity.Regional;
import com.project.artists.repository.RegionalRepository;
import com.project.artists.service.RegionalSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço de sincronização de regionais
 * 
 * ALGORITMO DE SINCRONIZAÇÃO EFICIENTE
 * 
 * Complexidade: O(n + m) onde:
 * - n = número de regionais na API externa
 * - m = número de regionais no banco local
 * 
 * Passos do algoritmo:
 * 
 * 1. BUSCAR DADOS (O(n) + O(m))
 *    - Buscar regionais da API externa: O(n)
 *    - Buscar regionais locais ativas: O(m)
 * 
 * 2. CRIAR MAPAS (O(n) + O(m))
 *    - Criar Map<ID, Regional> para acesso O(1)
 *    - Externo: Map<Integer, RegionalExternoDTO>
 *    - Local: Map<Integer, Regional>
 * 
 * 3. IDENTIFICAR MUDANÇAS (O(n))
 *    Para cada regional externo:
 *    - Se NÃO existe no local → NOVO (inserir)
 *    - Se existe e nome diferente → ALTERADO (inativar antigo + criar novo)
 *    - Se existe e nome igual → SEM MUDANÇAS (manter)
 * 
 * 4. IDENTIFICAR REMOVIDOS (O(m))
 *    Para cada regional local:
 *    - Se NÃO existe no externo → REMOVIDO (inativar)
 * 
 * 5. EXECUTAR OPERAÇÕES (O(k))
 *    Onde k = número total de mudanças
 *    - Inserir novos
 *    - Inativar removidos
 *    - Inativar alterados + inserir novos
 * 
 * COMPLEXIDADE TOTAL: O(n + m + k) ≈ O(n + m)
 * 
 * OTIMIZAÇÕES:
 * - Uso de HashMap para acesso O(1)
 * - Batch operations para inserções
 * - Single query para buscar locais
 * - Transação única para todas as operações
 */
@Service
@Transactional
public class RegionalSyncServiceImpl implements RegionalSyncService {
    
    private static final Logger logger = LoggerFactory.getLogger(RegionalSyncServiceImpl.class);
    
    @Autowired
    private RegionalApiClient regionalApiClient;
    
    @Autowired
    private RegionalRepository regionalRepository;
    
    @Override
    public SyncResponseDTO sincronizar() {
        logger.info("=== INICIANDO SINCRONIZAÇÃO DE REGIONAIS ===");
        
        long startTime = System.currentTimeMillis();
        
        try {
            // PASSO 1: BUSCAR DADOS
            // Complexidade: O(n) + O(m)
            logger.debug("Passo 1: Buscando dados...");
            
            // Buscar regionais da API externa - O(n)
            List<RegionalExternoDTO> regionaisExternos = regionalApiClient.buscarRegionais();
            logger.info("Regionais recebidos da API externa: {}", regionaisExternos.size());
            
            // Buscar regionais locais ativos - O(m)
            List<Regional> regionaisLocais = regionalRepository.findByAtivoTrue();
            logger.info("Regionais ativos no banco local: {}", regionaisLocais.size());
            
            
           
            // PASSO 2: CRIAR MAPAS PARA ACESSO O(1)
            // Complexidade: O(n) + O(m)
            logger.debug("Passo 2: Criando índices (Maps)...");
            
            // Map de regionais externos - O(n)
            Map<Integer, RegionalExternoDTO> mapaExternos = regionaisExternos.stream()
                .collect(Collectors.toMap(
                    RegionalExternoDTO::getId,
                    regional -> regional
                ));
            
            // Map de regionais locais - O(m)
            Map<Integer, Regional> mapaLocais = regionaisLocais.stream()
                .collect(Collectors.toMap(
                    Regional::getId,
                    regional -> regional
                ));
            
            
            // PASSO 3: IDENTIFICAR MUDANÇAS
            // Complexidade: O(n)
            logger.debug("Passo 3: Identificando mudanças...");
            
            List<Regional> novos = new ArrayList<>();
            List<Regional> alterados = new ArrayList<>();
            List<Regional> inativarAlterados = new ArrayList<>();
            int semMudancas = 0;
            
            // Para cada regional externo - O(n)
            for (RegionalExternoDTO externo : regionaisExternos) {
                Regional local = mapaLocais.get(externo.getId()); // O(1) lookup
                
                if (local == null) {
                    // NÃO EXISTE NO LOCAL → NOVO
                    logger.debug("NOVO: Regional {} - {}", externo.getId(), externo.getNome());
                    Regional novoRegional = new Regional(
                        externo.getId(),
                        externo.getNome(),
                        true
                    );
                    novos.add(novoRegional);
                    
                } else if (!local.getNome().equals(externo.getNome())) {
                    // EXISTE MAS NOME DIFERENTE → ALTERADO
                    logger.debug("ALTERADO: Regional {} - '{}' → '{}'", 
                               externo.getId(), local.getNome(), externo.getNome());
                    
                    // Inativar o antigo
                    local.setAtivo(false);
                    inativarAlterados.add(local);
                    
                    // Criar novo com nome atualizado
                    Regional novoRegional = new Regional(
                        externo.getId(),
                        externo.getNome(),
                        true
                    );
                    alterados.add(novoRegional);
                    
                } else {
                    // EXISTE E NOME IGUAL → SEM MUDANÇAS
                    logger.trace("SEM MUDANÇAS: Regional {} - {}", local.getId(), local.getNome());
                    semMudancas++;
                }
            }
            
            // PASSO 4: IDENTIFICAR REMOVIDOS
            // Complexidade: O(m)
            logger.debug("Passo 4: Identificando removidos...");
            
            List<Regional> removidos = new ArrayList<>();
            
            // Para cada regional local - O(m)
            for (Regional local : regionaisLocais) {
                if (!mapaExternos.containsKey(local.getId())) { // O(1) lookup
                    // NÃO EXISTE NO EXTERNO → REMOVIDO
                    logger.debug("REMOVIDO: Regional {} - {}", local.getId(), local.getNome());
                    local.setAtivo(false);
                    removidos.add(local);
                }
            }
            
            
            // PASSO 5: EXECUTAR OPERAÇÕES NO BANCO
            // Complexidade: O(k) onde k = total de mudanças
            logger.debug("Passo 5: Persistindo mudanças...");
            
            // Inativar alterados
            if (!inativarAlterados.isEmpty()) {
                regionalRepository.saveAll(inativarAlterados);
                logger.info("Inativos (alterados): {}", inativarAlterados.size());
            }
            
            // Inativar removidos
            if (!removidos.isEmpty()) {
                regionalRepository.saveAll(removidos);
                logger.info("Inativos (removidos): {}", removidos.size());
            }
            
            // Inserir novos
            if (!novos.isEmpty()) {
                regionalRepository.saveAll(novos);
                logger.info("Inseridos (novos): {}", novos.size());
            }
            
            // Inserir alterados (versões novas)
            if (!alterados.isEmpty()) {
                regionalRepository.saveAll(alterados);
                logger.info("Inseridos (alterados): {}", alterados.size());
            }
            
       
            long duration = System.currentTimeMillis() - startTime;
            
            SyncResponseDTO.SyncStats stats = new SyncResponseDTO.SyncStats(
                regionaisExternos.size(),
                regionaisLocais.size(),
                novos.size(),
                removidos.size(),
                alterados.size(),
                semMudancas,
                duration
            );
            
            String message = String.format(
                "Sincronização concluída: %d novos, %d removidos, %d alterados, %d sem mudanças",
                novos.size(), removidos.size(), alterados.size(), semMudancas
            );
            
            logger.info("=== SINCRONIZAÇÃO CONCLUÍDA EM {}ms ===", duration);
            logger.info(message);
            
            return new SyncResponseDTO(true, message, stats);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Erro durante sincronização: {}", e.getMessage(), e);
            
            SyncResponseDTO response = new SyncResponseDTO();
            response.setSuccess(false);
            response.setMessage("Erro durante sincronização: " + e.getMessage());
            response.setErrors(Arrays.asList(e.getMessage()));
            
            SyncResponseDTO.SyncStats stats = new SyncResponseDTO.SyncStats();
            stats.setTempoExecucaoMs(duration);
            response.setStats(stats);
            
            return response;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Regional> listarTodos() {
        return regionalRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Regional> listarAtivos() {
        return regionalRepository.findByAtivoTrue();
    }
}
