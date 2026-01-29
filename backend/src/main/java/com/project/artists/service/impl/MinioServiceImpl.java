package com.project.artists.service.impl;

import com.project.artists.exception.BadRequestException;
import com.project.artists.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Implementação do serviço MinIO
 */
@Service
public class MinioServiceImpl implements MinioService {
    
    @Autowired
    private MinioClient minioClient;
    
    @Value("${app.minio.bucket-name}")
    private String bucketName;
    
    @Value("${app.minio.presigned-url-expiration:1800}")
    private int presignedUrlExpiration; // 30 minutos em segundos
    
    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // Validar arquivo
            if (file.isEmpty()) {
                throw new BadRequestException("Arquivo está vazio");
            }
            
            // Validar tipo de arquivo (apenas imagens)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BadRequestException("Apenas imagens são permitidas");
            }
            
            // Gerar nome único para o arquivo
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String objectName = folder + "/" + UUID.randomUUID().toString() + extension;
            
            // Criar bucket se não existir
            ensureBucketExists();
            
            // Fazer upload
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(contentType)
                        .build()
                );
            }
            
            return objectName;
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload do arquivo: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<String> uploadFiles(List<MultipartFile> files, String folder) {
        List<String> objectNames = new ArrayList<>();
        
        for (MultipartFile file : files) {
            String objectName = uploadFile(file, folder);
            objectNames.add(objectName);
        }
        
        return objectNames;
    }
    
    @Override
    public String generatePresignedUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(presignedUrlExpiration, TimeUnit.SECONDS)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar URL pré-assinada: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<String> generatePresignedUrls(List<String> objectNames) {
        List<String> urls = new ArrayList<>();
        
        for (String objectName : objectNames) {
            String url = generatePresignedUrl(objectName);
            urls.add(url);
        }
        
        return urls;
    }
    
    @Override
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar arquivo: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean bucketExists() {
        try {
            return minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build()
            );
        } catch (Exception e) {
            return false;
        }
    }
    
    private void ensureBucketExists() {
        try {
            boolean exists = bucketExists();
            
            if (!exists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar bucket: " + e.getMessage(), e);
        }
    }
}