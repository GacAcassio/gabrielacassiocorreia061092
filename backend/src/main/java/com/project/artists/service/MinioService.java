package com.project.artists.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface do serviço MinIO
 */
public interface MinioService {
    
    /**
     * Faz upload de um arquivo e retorna o nome do objeto
     */
    String uploadFile(MultipartFile file, String folder);
    
    /**
     * Faz upload de múltiplos arquivos
     */
    List<String> uploadFiles(List<MultipartFile> files, String folder);
    
    /**
     * Gera URL pré-assinada (válida por 30 minutos)
     */
    String generatePresignedUrl(String objectName);
    
    /**
     * Gera URLs pré-assinadas para múltiplos objetos
     */
    List<String> generatePresignedUrls(List<String> objectNames);
    
    /**
     * Deleta um arquivo
     */
    void deleteFile(String objectName);
    
    /**
     * Verifica se bucket existe
     */
    boolean bucketExists();
}