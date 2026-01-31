package com.project.artists.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface do serviço MinIO
 */
public interface MinioService {
    
    String uploadFile(MultipartFile file, String folder);
    
    List<String> uploadFiles(List<MultipartFile> files, String folder);
    
    String generatePresignedUrl(String objectName);
    
    List<String> generatePresignedUrls(List<String> objectNames);
    
    void deleteFile(String objectName);
    
    boolean bucketExists();
}