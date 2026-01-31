package com.project.artists.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade Artist
 * Relacionamento N:N com Album (um artista pode ter vários álbuns)
 */
@Entity
@Table(name = "artists")
public class Artist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome do artista é obrigatório")
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    /**
     * RELACIONAMENTO N:N COM ALBUMS
     */
    @ManyToMany(mappedBy = "artists", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Album> albums = new HashSet<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Construtores
    public Artist() {}
    
    public Artist(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }
    
    // Métodos auxiliares
    public void addAlbum(Album album) {
        this.albums.add(album);
        album.getArtists().add(this);
    }
    
    public void removeAlbum(Album album) {
        this.albums.remove(album);
        album.getArtists().remove(this);
    }
    
    // Getters e Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
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
    
    public Set<Album> getAlbums() { 
        return albums; 
    }
    
    public void setAlbums(Set<Album> albums) { 
        this.albums = albums; 
    }
    
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artist)) return false;
        Artist artist = (Artist) o;
        return id != null && id.equals(artist.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
