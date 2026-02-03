package com.project.artists.dto.response;
import com.project.artists.entity.Album;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO resumido de álbum (para listagens)
 */
public class AlbumSummaryDTO {
    
    private Long id;
    private String title;
    private Integer releaseYear;
    private List<String> artistNames = new ArrayList<>();
    private String coverUrl; // Primeira capa apenas
    
    // Construtores
    public AlbumSummaryDTO() {}
    
    public AlbumSummaryDTO(Long id, String title, Integer releaseYear) {
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
    
    public List<String> getArtistNames() { return artistNames; }
    public void setArtistNames(List<String> artistNames) { this.artistNames = artistNames; }
    
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}