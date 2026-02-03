package com.project.artists.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para o processo de sincronização de regionais
 * 
 */
public class SyncResponseDTO {
    
    private LocalDateTime timestamp;
    private boolean success;
    private String message;
    private SyncStats stats;
    private List<String> errors;
    
    // Construtores
    public SyncResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }
    
    public SyncResponseDTO(boolean success, String message, SyncStats stats) {
        this.timestamp = LocalDateTime.now();
        this.success = success;
        this.message = message;
        this.stats = stats;
    }
    
    // Classe interna para estatísticas
    public static class SyncStats {
        private int totalExterno;      // Total recebido da API externa
        private int totalLocal;        // Total no banco local
        private int novos;             // Novos inseridos
        private int inativos;          // Marcados como inativos
        private int alterados;         // Atualizados (nome mudou)
        private int semMudancas;       // Sem alterações
        private long tempoExecucaoMs;  // Tempo de execução em ms
        
        public SyncStats() {}
        
        public SyncStats(int totalExterno, int totalLocal, int novos, int inativos, 
                        int alterados, int semMudancas, long tempoExecucaoMs) {
            this.totalExterno = totalExterno;
            this.totalLocal = totalLocal;
            this.novos = novos;
            this.inativos = inativos;
            this.alterados = alterados;
            this.semMudancas = semMudancas;
            this.tempoExecucaoMs = tempoExecucaoMs;
        }
        
        // Getters e Setters
        public int getTotalExterno() { return totalExterno; }
        public void setTotalExterno(int totalExterno) { this.totalExterno = totalExterno; }
        
        public int getTotalLocal() { return totalLocal; }
        public void setTotalLocal(int totalLocal) { this.totalLocal = totalLocal; }
        
        public int getNovos() { return novos; }
        public void setNovos(int novos) { this.novos = novos; }
        
        public int getInativos() { return inativos; }
        public void setInativos(int inativos) { this.inativos = inativos; }
        
        public int getAlterados() { return alterados; }
        public void setAlterados(int alterados) { this.alterados = alterados; }
        
        public int getSemMudancas() { return semMudancas; }
        public void setSemMudancas(int semMudancas) { this.semMudancas = semMudancas; }
        
        public long getTempoExecucaoMs() { return tempoExecucaoMs; }
        public void setTempoExecucaoMs(long tempoExecucaoMs) { this.tempoExecucaoMs = tempoExecucaoMs; }
    }
    
    // Getters e Setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public SyncStats getStats() { return stats; }
    public void setStats(SyncStats stats) { this.stats = stats; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
