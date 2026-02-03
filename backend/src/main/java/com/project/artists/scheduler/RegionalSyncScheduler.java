package com.project.artists.scheduler;

import com.project.artists.dto.response.SyncResponseDTO;
import com.project.artists.service.RegionalSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled Job para sincronização automática de regionais
 * 
 */
@Component
@ConditionalOnProperty(
    prefix = "app.regional-sync",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = false  // Desabilitado por padrão
)
public class RegionalSyncScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(RegionalSyncScheduler.class);
    
    @Autowired
    private RegionalSyncService regionalSyncService;
    
    @Value("${app.regional-sync.cron:0 0 2 * * ?}")
    private String cronExpression;
    
    /**
     * Job executado automaticamente conforme configuração cron
     * 
     */
    @Scheduled(cron = "${app.regional-sync.cron:0 0 2 * * ?}")
    public void executarSincronizacaoAutomatica() {
        logger.info("========================================");
        logger.info("INICIANDO SINCRONIZAÇÃO AUTOMÁTICA");
        logger.info("Expressão CRON: {}", cronExpression);
        logger.info("========================================");
        
        try {
            SyncResponseDTO result = regionalSyncService.sincronizar();
            
            if (result.isSuccess()) {
                logger.info("Sincronização automática concluída com sucesso!");
                logger.info("Estatísticas: {}", result.getMessage());
            } else {
                logger.error("Sincronização automática falhou: {}", result.getMessage());
                if (result.getErrors() != null) {
                    result.getErrors().forEach(error -> 
                        logger.error("  - {}", error)
                    );
                }
            }
            
        } catch (Exception e) {
            logger.error("Erro durante sincronização automática", e);
        }
        
        logger.info("========================================");
    }
    
    /**
     * Job de teste que executa a cada 5 minutos
     * 
     */
    // @Scheduled(cron = "0 */5 * * * ?")
    // public void sincronizacaoTeste() {
    //     logger.info("Executando sincronização de teste (a cada 5 minutos)");
    //     executarSincronizacaoAutomatica();
    // }
}
