package com.project.artists.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de resposta detalhada de um álbum
 */
public class AlbumResponseDTO {
    
    private Long id;
    private String title;
    private Integer releaseYear;
    private List<ArtistSummaryDTO> artists = new ArrayList<>();
    private List<String> coverUrls = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Construtores
    public AlbumResponseDTO() {}
    
    public AlbumResponseDTO(Long id, String title, Integer releaseYear) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    
    public List<ArtistSummaryDTO> getArtists() { return artists; }
    public void setArtists(List<ArtistSummaryDTO> artists) { this.artists = artists; }
    
    public List<String> getCoverUrls() { return coverUrls; }
    public void setCoverUrls(List<String> coverUrls) { this.coverUrls = coverUrls; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // DTO interno para artistas do álbum
    public static class ArtistSummaryDTO {
        private Long id;
        private String name;
        
        public ArtistSummaryDTO() {}
        
        public ArtistSummaryDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}