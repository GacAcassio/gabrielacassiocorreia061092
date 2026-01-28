package com.project.artists.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para criar/atualizar artistas
 */
public class ArtistRequestDTO {
    
    @NotBlank(message = "Nome do artista é obrigatório")
    @Size(min = 2, max = 200, message = "Nome deve ter entre 2 e 200 caracteres")
    private String name;
    
    @Size(max = 5000, message = "Biografia deve ter no máximo 5000 caracteres")
    private String bio;
    
    // Construtores
    public ArtistRequestDTO() {}
    
    public ArtistRequestDTO(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }
    
    // Getters e Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
}