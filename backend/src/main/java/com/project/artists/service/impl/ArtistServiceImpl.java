package com.project.artists.service.impl;

import com.project.artists.dto.request.ArtistRequestDTO;
import com.project.artists.dto.response.ArtistResponseDTO;
import com.project.artists.dto.response.ArtistSummaryDTO;
import com.project.artists.dto.response.PageResponseDTO;
import com.project.artists.entity.Artist;
import com.project.artists.exception.ResourceNotFoundException;
import com.project.artists.repository.ArtistRepository;
import com.project.artists.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de artistas
 */
@Service
@Transactional
public class ArtistServiceImpl implements ArtistService {
    
    @Autowired
    private ArtistRepository artistRepository;
    
    @Override
    public ArtistResponseDTO create(ArtistRequestDTO request) {
        Artist artist = new Artist();
        artist.setName(request.getName());
        artist.setBio(request.getBio());
        
        Artist saved = artistRepository.save(artist);
        
        return toResponseDTO(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ArtistResponseDTO findById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", id));
        
        return toDetailedResponseDTO(artist);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ArtistSummaryDTO> findAll(Pageable pageable) {
        Page<Artist> page = artistRepository.findAll(pageable);
        
        List<ArtistSummaryDTO> content = page.getContent().stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        
        return new PageResponseDTO<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ArtistSummaryDTO> searchByName(String name, Pageable pageable) {
        Page<Artist> page = artistRepository.findByNameContainingIgnoreCase(name, pageable);
        
        List<ArtistSummaryDTO> content = page.getContent().stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        
        return new PageResponseDTO<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
    
    @Override
    public ArtistResponseDTO update(Long id, ArtistRequestDTO request) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", id));
        
        artist.setName(request.getName());
        artist.setBio(request.getBio());
        
        Artist updated = artistRepository.save(artist);
        
        return toResponseDTO(updated);
    }
    
    @Override
    public void delete(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", id));
        
        artistRepository.delete(artist);
    }
    
    // Métodos auxiliares de conversão
    
    private ArtistResponseDTO toResponseDTO(Artist artist) {
        ArtistResponseDTO dto = new ArtistResponseDTO();
        dto.setId(artist.getId());
        dto.setName(artist.getName());
        dto.setBio(artist.getBio());
        dto.setAlbumCount(artist.getAlbums().size());
        dto.setCreatedAt(artist.getCreatedAt());
        dto.setUpdatedAt(artist.getUpdatedAt());
        return dto;
    }
    
    private ArtistResponseDTO toDetailedResponseDTO(Artist artist) {
        ArtistResponseDTO dto = toResponseDTO(artist);
        
        // Adicionar lista de álbuns
        List<ArtistResponseDTO.AlbumSummaryDTO> albums = artist.getAlbums().stream()
                .map(album -> new ArtistResponseDTO.AlbumSummaryDTO(
                        album.getId(),
                        album.getTitle(),
                        album.getReleaseYear()
                ))
                .collect(Collectors.toList());
        
        dto.setAlbums(albums);
        
        return dto;
    }
    
    private ArtistSummaryDTO toSummaryDTO(Artist artist) {
        return new ArtistSummaryDTO(
                artist.getId(),
                artist.getName(),
                artist.getAlbums().size()
        );
    }
}