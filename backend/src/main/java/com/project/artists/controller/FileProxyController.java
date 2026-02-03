package com.project.artists.controller;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/files")
public class FileProxyController {
    
    @Autowired
    private MinioClient minioClient;
    
    @Value("${app.minio.bucket-name}")
    private String bucketName;
    
    @GetMapping("/**")
    public ResponseEntity<byte[]> getFile(HttpServletRequest request) {
        try {
            // Extract object name from URL
            String objectName = request.getRequestURI().replace("/api/v1/files/", "");
            
            InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
            
            byte[] bytes = stream.readAllBytes();
            stream.close();
            
            HttpHeaders headers = new HttpHeaders();
            
            // Determine content type based on file extension
            if (objectName.endsWith(".jpg") || objectName.endsWith(".jpeg")) {
                headers.setContentType(MediaType.IMAGE_JPEG);
            } else if (objectName.endsWith(".png")) {
                headers.setContentType(MediaType.IMAGE_PNG);
            } else {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            }
            
            headers.setCacheControl("public, max-age=31536000");
            
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}