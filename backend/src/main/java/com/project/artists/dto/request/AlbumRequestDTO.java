package com.project.artists.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para criar/atualizar álbuns
 */
public class AlbumRequestDTO {
    
    @NotBlank(message = "Título do álbum é obrigatório")
    @Size(min = 1, max = 200, message = "Título deve ter entre 1 e 200 caracteres")
    private String title;
    
    @NotEmpty(message = "Álbum deve ter pelo menos um artista")
    private List<Long> artistIds = new ArrayList<>();
    
    private Integer releaseYear;
    
    // Construtores
    public AlbumRequestDTO() {}
    
    public AlbumRequestDTO(String title, List<Long> artistIds, Integer releaseYear) {
        this.title = title;
        this.artistIds = artistIds;
        this.releaseYear = releaseYear;
    }
    
    // Getters e Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public List<Long> getArtistIds() {
        return artistIds;
    }
    
    public void setArtistIds(List<Long> artistIds) {
        this.artistIds = artistIds;
    }
    
    public Integer getReleaseYear() {
        return releaseYear;
    }
    
    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }
}