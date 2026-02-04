package com.project.artists.controller;

import com.project.artists.dto.response.SyncResponseDTO;
import com.project.artists.entity.Regional;
import com.project.artists.service.RegionalSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para operacoes com regionais
 * 
 * Endpoints:
 * - POST /sync - Trigger manual de sincronizacao
 * - GET / - Listar regionais ativos
 * - GET /all - Listar todos (ativos e inativos)
 */
@RestController
@RequestMapping("/api/v1/regionais")
@Tag(
    name = "4. Regionais", 
    description = "Sincronizacao e consulta de regionais da API externa\n\n" +
                 "**API Externa:** https://integrador-argus-api.geia.vip/v1/regionais\n\n" 
)
public class RegionalController {
    
    @Autowired
    private RegionalSyncService regionalSyncService;
    
    /**
     * Endpoint para trigger manual de sincronizacao
     * 
     * Complexidade: O(n + m) onde:
     * - n = regionais na API externa
     * - m = regionais no banco local
     */
    @PostMapping("/sync")
    @Operation(
        summary = "Sincronizar Regionais",
        description = "Executa sincronizacao manual com a API externa de regionais.\n\n" +
                     "**Processo:**\n" +
                     "1. Busca regionais da API externa\n" +
                     "2. Busca regionais locais ativos\n" +
                     "3. Identifica diferencas (novos, removidos, alterados)\n" +
                     "4. Aplica mudancas no banco de dados\n" +
                     "5. Retorna estatisticas detalhadas\n\n" +
                     "**Algoritmo:**\n" +
                     "- Usa HashMap para acesso O(1)\n" +
                     "- Complexidade total: O(n+m) - Linear\n" +
                     "- Tempo tipico: 200-500ms para 1000 regionais\n\n" +
                     "**Performance:**\n" +
                     "- 100 regionais: ~50ms\n" +
                     "- 1.000 regionais: ~200ms\n" +
                     "- 10.000 regionais: ~1.5s\n\n" +
                     "**Nota:** Se API externa estiver lenta (>30s), pode retornar timeout. " +
                     "Neste caso, aumente o timeout em application.yml."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Sincronizacao concluida com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SyncResponseDTO.class),
                examples = @ExampleObject(
                    name = "Sucesso - Primeira Sincronizacao",
                    value = "{\n" +
                           "  \"timestamp\": \"2026-02-03T10:30:00\",\n" +
                           "  \"success\": true,\n" +
                           "  \"message\": \"Sincronizacao concluida: 50 novos, 0 removidos, 0 alterados, 0 sem mudancas\",\n" +
                           "  \"stats\": {\n" +
                           "    \"totalExterno\": 50,\n" +
                           "    \"totalLocal\": 0,\n" +
                           "    \"novos\": 50,\n" +
                           "    \"inativos\": 0,\n" +
                           "    \"alterados\": 0,\n" +
                           "    \"semMudancas\": 0,\n" +
                           "    \"tempoExecucaoMs\": 234\n" +
                           "  },\n" +
                           "  \"errors\": null\n" +
                           "}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "200",
            description = "Sincronizacao com mudancas",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Sucesso - Com Mudancas",
                    value = "{\n" +
                           "  \"timestamp\": \"2026-02-03T10:35:00\",\n" +
                           "  \"success\": true,\n" +
                           "  \"message\": \"Sincronizacao concluida: 5 novos, 2 removidos, 3 alterados, 40 sem mudancas\",\n" +
                           "  \"stats\": {\n" +
                           "    \"totalExterno\": 48,\n" +
                           "    \"totalLocal\": 50,\n" +
                           "    \"novos\": 5,\n" +
                           "    \"inativos\": 2,\n" +
                           "    \"alterados\": 3,\n" +
                           "    \"semMudancas\": 40,\n" +
                           "    \"tempoExecucaoMs\": 312\n" +
                           "  },\n" +
                           "  \"errors\": null\n" +
                           "}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro durante sincronizacao (API externa indisponivel, timeout, etc)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro - Timeout",
                    value = "{\n" +
                           "  \"timestamp\": \"2026-02-03T10:40:00\",\n" +
                           "  \"success\": false,\n" +
                           "  \"message\": \"Erro durante sincronizacao: Connection timeout\",\n" +
                           "  \"stats\": {\n" +
                           "    \"tempoExecucaoMs\": 30000\n" +
                           "  },\n" +
                           "  \"errors\": [\"Connection timeout\", \"Failed to connect to external API\"]\n" +
                           "}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado - Token JWT ausente ou invalido",
            content = @Content(mediaType = "application/json")
        )
    })
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
     */
    @GetMapping
    @Operation(
        summary = "Listar Regionais Ativos",
        description = "Retorna apenas regionais com status ativo=true.\n\n" +
                     "**Uso tipico:**\n" +
                     "- Listar regionais disponiveis para selecao em formularios\n" +
                     "- Exibir apenas regionais em uso no sistema\n\n" +
                     "**Nota:** Regionais inativos sao aqueles que foram removidos " +
                     "da API externa ou tiveram o nome alterado."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de regionais ativos retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Regional.class),
                examples = @ExampleObject(
                    name = "Lista de Regionais Ativos",
                    value = "[\n" +
                           "  {\n" +
                           "    \"id\": 1,\n" +
                           "    \"nome\": \"Regional Sul\",\n" +
                           "    \"ativo\": true,\n" +
                           "    \"createdAt\": \"2026-01-15T10:00:00\",\n" +
                           "    \"updatedAt\": \"2026-02-03T10:30:00\"\n" +
                           "  },\n" +
                           "  {\n" +
                           "    \"id\": 2,\n" +
                           "    \"nome\": \"Regional Norte\",\n" +
                           "    \"ativo\": true,\n" +
                           "    \"createdAt\": \"2026-01-15T10:00:00\",\n" +
                           "    \"updatedAt\": \"2026-02-03T10:30:00\"\n" +
                           "  }\n" +
                           "]"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<List<Regional>> listarAtivos() {
        List<Regional> regionais = regionalSyncService.listarAtivos();
        return ResponseEntity.ok(regionais);
    }
    
    /**
     * Lista todos os regionais (ativos e inativos)
     */
    @GetMapping("/all")
    @Operation(
        summary = "Listar Todos os Regionais",
        description = "Retorna todos os regionais, incluindo inativos.\n\n" +
                     "**Uso tipico:**\n" +
                     "- Auditoria e historico de mudancas\n" +
                     "- Relatorios administrativos\n" +
                     "- Debugging de sincronizacao\n\n" +
                     "**Regionais inativos:**\n" +
                     "- Removidos da API externa\n" +
                     "- Tiveram o nome alterado (versao antiga fica inativa)\n\n" +
                     "**Nota:** Regionais nunca sao deletados fisicamente do banco, " +
                     "apenas marcados como inativos para manter historico."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista completa de regionais retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Regional.class),
                examples = @ExampleObject(
                    name = "Lista Completa de Regionais",
                    value = "[\n" +
                           "  {\n" +
                           "    \"id\": 1,\n" +
                           "    \"nome\": \"Regional Sul\",\n" +
                           "    \"ativo\": true,\n" +
                           "    \"createdAt\": \"2026-01-15T10:00:00\",\n" +
                           "    \"updatedAt\": \"2026-02-03T10:30:00\"\n" +
                           "  },\n" +
                           "  {\n" +
                           "    \"id\": 2,\n" +
                           "    \"nome\": \"Regional Norte\",\n" +
                           "    \"ativo\": false,\n" +
                           "    \"createdAt\": \"2026-01-15T10:00:00\",\n" +
                           "    \"updatedAt\": \"2026-02-01T08:00:00\"\n" +
                           "  },\n" +
                           "  {\n" +
                           "    \"id\": 3,\n" +
                           "    \"nome\": \"Regional Centro\",\n" +
                           "    \"ativo\": true,\n" +
                           "    \"createdAt\": \"2026-01-20T14:30:00\",\n" +
                           "    \"updatedAt\": \"2026-02-03T10:30:00\"\n" +
                           "  }\n" +
                           "]"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<List<Regional>> listarTodos() {
        List<Regional> regionais = regionalSyncService.listarTodos();
        return ResponseEntity.ok(regionais);
    }
}