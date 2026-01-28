package com.project.artists.dto.response;

/**
 * DTO resumido de artista (para listagens)
 */
public class ArtistSummaryDTO {
    
    private Long id;
    private String name;
    private Integer albumCount;
    
    // Construtores
    public ArtistSummaryDTO() {}
    
    public ArtistSummaryDTO(Long id, String name, Integer albumCount) {
        this.id = id;
        this.name = name;
        this.albumCount = albumCount;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getAlbumCount() { return albumCount; }
    public void setAlbumCount(Integer albumCount) { this.albumCount = albumCount; }
}