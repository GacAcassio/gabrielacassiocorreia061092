package com.project.artists.client;

import com.project.artists.dto.external.RegionalExternoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Client HTTP para consumir a API externa de regionais
 *
 * Endpoint: https://integrador-argus-api.geia.vip/v1/regionais
 */
@Component
public class RegionalApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(RegionalApiClient.class);
    
    @Value("${app.regional-api.url}")
    private String apiUrl;
    
    @Value("${app.regional-api.connect-timeout:10000}")
    private int connectTimeout; // Default: 10 segundos
    
    @Value("${app.regional-api.read-timeout:60000}")
    private int readTimeout; // Default: 60 segundos
    
    @Value("${app.regional-api.max-retries:3}")
    private int maxRetries; // Default: 3 tentativas
    
    private final RestTemplate restTemplate;
    
    public RegionalApiClient(RestTemplateBuilder restTemplateBuilder) {
        // Configurar timeouts via RestTemplateBuilder
        this.restTemplate = restTemplateBuilder
            .setConnectTimeout(Duration.ofMillis(10000))  // 10s conexão
            .setReadTimeout(Duration.ofMillis(60000))      // 60s leitura
            .build();
    }
    
    /**
     * Busca todas as regionais da API externa
     * 
     * Implementa retry automático e logs de performance
     * 
     * @return Lista de regionais da API externa
     * @throws RestClientException se houver erro na comunicação
     */
    public List<RegionalExternoDTO> buscarRegionais() {
        logger.info("Iniciando busca de regionais da API externa: {}", apiUrl);
        logger.debug("Timeouts configurados - Conexão: {}ms, Leitura: {}ms", 
                    connectTimeout, readTimeout);
        
        int tentativa = 1;
        Exception ultimoErro = null;
        
        while (tentativa <= maxRetries) {
            try {
                long startTime = System.currentTimeMillis();
                
                logger.info("Tentativa {}/{} - Fazendo requisição GET para API externa...", 
                           tentativa, maxRetries);
                
                // Fazer requisição GET
                ResponseEntity<List<RegionalExternoDTO>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<RegionalExternoDTO>>() {}
                );
                
                long duration = System.currentTimeMillis() - startTime;
                
                List<RegionalExternoDTO> regionais = response.getBody();
                
                if (regionais == null) {
                    logger.warn("API externa retornou resposta vazia");
                    return new ArrayList<>();
                }
                
                // Logs de performance
                logger.info("Sucesso ao buscar {} regionais da API externa", regionais.size());
                logger.info("Tempo de resposta: {}ms ({}s)", duration, duration / 1000.0);
                
                // Alertar se API está lenta
                if (duration > 30000) {
                    logger.warn("API EXTERNA MUITO LENTA! Tempo: {}s (> 30s)", duration / 1000.0);
                } else if (duration > 10000) {
                    logger.warn("API externa lenta. Tempo: {}s (> 10s)", duration / 1000.0);
                } else if (duration > 5000) {
                    logger.info("ℹAPI externa com performance moderada: {}s", duration / 1000.0);
                } else {
                    logger.info("✓ API externa com boa performance: {}s", duration / 1000.0);
                }
                
                return regionais;
                
            } catch (RestClientException e) {
                ultimoErro = e;
                
                String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                
                // Identificar tipo de erro
                if (errorMsg.contains("timeout") || errorMsg.contains("timed out")) {
                    logger.error("TIMEOUT na tentativa {}/{}: API externa não respondeu em tempo hábil", 
                               tentativa, maxRetries);
                    logger.error(" Timeout de conexão: {}s, Timeout de leitura: {}s", 
                               connectTimeout / 1000, readTimeout / 1000);
                } else if (errorMsg.contains("Connection refused")) {
                    logger.error("CONEXÃO RECUSADA na tentativa {}/{}: API externa não está acessível", 
                               tentativa, maxRetries);
                } else if (errorMsg.contains("UnknownHost")) {
                    logger.error("HOST DESCONHECIDO na tentativa {}/{}: Verifique a URL da API", 
                               tentativa, maxRetries);
                } else {
                    logger.error("ERRO na tentativa {}/{}: {}", tentativa, maxRetries, errorMsg);
                }
                
                // Se não for última tentativa, aguardar antes de retry
                if (tentativa < maxRetries) {
                    int waitTime = tentativa * 2; // Backoff exponencial: 2s, 4s, 6s...
                    logger.info("Aguardando {}s antes de tentar novamente...", waitTime);
                    try {
                        Thread.sleep(waitTime * 1000L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.error("Retry interrompido");
                        break;
                    }
                }
                
                tentativa++;
            }
        }
        
        // Se chegou aqui, todas as tentativas falharam
        logger.error("FALHA TOTAL: Todas as {} tentativas falharam ao buscar regionais da API externa", 
                    maxRetries);
        throw new RuntimeException(
            "Falha ao comunicar com API externa após " + maxRetries + " tentativas: " + 
            (ultimoErro != null ? ultimoErro.getMessage() : "Erro desconhecido"), 
            ultimoErro
        );
    }
    
    /**
     * Testa conectividade com a API externa
     * 
     * @return true se API está acessível, false caso contrário
     */
    public boolean testarConectividade() {
        try {
            logger.debug("Testando conectividade com API externa...");
            long start = System.currentTimeMillis();
            buscarRegionais();
            long duration = System.currentTimeMillis() - start;
            logger.info("✓ API externa acessível (tempo: {}ms)", duration);
            return true;
        } catch (Exception e) {
            logger.error("✗ API externa NÃO está acessível: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Busca regionais com timeout customizado (útil para testes)
     * 
     * @param customTimeoutMs 
     * @return Lista de regionais
     */
    public List<RegionalExternoDTO> buscarRegionaisComTimeout(int customTimeoutMs) {
        logger.info("Buscando regionais com timeout customizado: {}ms", customTimeoutMs);
        
        // Criar RestTemplate temporário com timeout customizado
        RestTemplate tempRestTemplate = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(customTimeoutMs))
            .setReadTimeout(Duration.ofMillis(customTimeoutMs))
            .build();
        
        long startTime = System.currentTimeMillis();
        
        ResponseEntity<List<RegionalExternoDTO>> response = tempRestTemplate.exchange(
            apiUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<RegionalExternoDTO>>() {}
        );
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Busca concluída em {}ms com timeout de {}ms", duration, customTimeoutMs);
        
        return response.getBody() != null ? response.getBody() : new ArrayList<>();
    }
}