package com.project.artists.service;

import com.project.artists.dto.request.ArtistRequestDTO;
import com.project.artists.dto.response.ArtistResponseDTO;
import com.project.artists.dto.response.ArtistSummaryDTO;
import com.project.artists.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

/**
 * Interface do serviço de artistas
 */
public interface ArtistService {
    
    ArtistResponseDTO create(ArtistRequestDTO request);
    
    ArtistResponseDTO findById(Long id);
    
    PageResponseDTO<ArtistSummaryDTO> findAll(Pageable pageable);
    
    PageResponseDTO<ArtistSummaryDTO> searchByName(String name, Pageable pageable);
    
    ArtistResponseDTO update(Long id, ArtistRequestDTO request);
    
    void delete(Long id);
}