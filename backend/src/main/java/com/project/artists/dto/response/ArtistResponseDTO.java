package com.project.artists.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de resposta detalhada de um artista
 */
public class ArtistResponseDTO {
    
    private Long id;
    private String name;
    private String bio;
    private Integer albumCount;
    private List<AlbumSummaryDTO> albums = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Construtores
    public ArtistResponseDTO() {}
    
    public ArtistResponseDTO(Long id, String name, String bio, Integer albumCount) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.albumCount = albumCount;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public Integer getAlbumCount() { return albumCount; }
    public void setAlbumCount(Integer albumCount) { this.albumCount = albumCount; }
    
    public List<AlbumSummaryDTO> getAlbums() { return albums; }
    public void setAlbums(List<AlbumSummaryDTO> albums) { this.albums = albums; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // DTO interno para resumo de álbuns
    public static class AlbumSummaryDTO {
        private Long id;
        private String title;
        private Integer releaseYear;
        
        public AlbumSummaryDTO() {}
        
        public AlbumSummaryDTO(Long id, String title, Integer releaseYear) {
            this.id = id;
            this.title = title;
            this.releaseYear = releaseYear;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Integer getReleaseYear() { return releaseYear; }
        public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    }
}