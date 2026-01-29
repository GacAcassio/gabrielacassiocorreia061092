package com.project.artists.service;

import com.project.artists.dto.request.AlbumRequestDTO;
import com.project.artists.dto.response.AlbumResponseDTO;
import com.project.artists.dto.response.AlbumSummaryDTO;
import com.project.artists.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface do serviço de álbuns
 */
public interface AlbumService {
    
    /**
     * Cria novo álbum
     */
    AlbumResponseDTO create(AlbumRequestDTO request);
    
    /**
     * Busca álbum por ID
     */
    AlbumResponseDTO findById(Long id);
    
    /**
     * Lista todos os álbuns com paginação
     */
    PageResponseDTO<AlbumSummaryDTO> findAll(Pageable pageable);
    
    /**
     * Busca álbuns de um artista específico
     */
    PageResponseDTO<AlbumSummaryDTO> findByArtistId(Long artistId, Pageable pageable);
    
    /**
     * Atualiza álbum existente
     */
    AlbumResponseDTO update(Long id, AlbumRequestDTO request);
    
    /**
     * Deleta álbum por ID
     */
    void delete(Long id);
    
    /**
     * Faz upload de capas para um álbum
     */
    AlbumResponseDTO uploadCovers(Long id, List<MultipartFile> files);
    
    /**
     * Remove uma capa específica
     */
    AlbumResponseDTO removeCover(Long id, String coverUrl);
}