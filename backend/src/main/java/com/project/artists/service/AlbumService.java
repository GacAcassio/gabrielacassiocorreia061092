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
    
  
    AlbumResponseDTO create(AlbumRequestDTO request);
    

    AlbumResponseDTO findById(Long id);
    
    PageResponseDTO<AlbumSummaryDTO> findAll(Pageable pageable);

    PageResponseDTO<AlbumSummaryDTO> findByArtistId(Long artistId, Pageable pageable);
    
    AlbumResponseDTO update(Long id, AlbumRequestDTO request);

    void delete(Long id);
    
    AlbumResponseDTO uploadCovers(Long id, List<MultipartFile> files);
    
    AlbumResponseDTO removeCover(Long id, String coverUrl);
}