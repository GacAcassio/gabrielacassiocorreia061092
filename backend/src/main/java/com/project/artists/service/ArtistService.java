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
    
    /**
     * Cria novo artista
     */
    ArtistResponseDTO create(ArtistRequestDTO request);
    
    /**
     * Busca artista por ID
     */
    ArtistResponseDTO findById(Long id);
    
    /**
     * Lista todos os artistas com paginação
     */
    PageResponseDTO<ArtistSummaryDTO> findAll(Pageable pageable);
    
    /**
     * Busca artistas por nome com paginação
     */
    PageResponseDTO<ArtistSummaryDTO> searchByName(String name, Pageable pageable);
    
    /**
     * Atualiza artista existente
     */
    ArtistResponseDTO update(Long id, ArtistRequestDTO request);
    
    /**
     * Deleta artista por ID
     */
    void delete(Long id);
}