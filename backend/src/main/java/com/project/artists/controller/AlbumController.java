package com.project.artists.controller;

import com.project.artists.dto.request.AlbumRequestDTO;
import com.project.artists.dto.response.AlbumResponseDTO;
import com.project.artists.dto.response.AlbumSummaryDTO;
import com.project.artists.dto.response.PageResponseDTO;
import com.project.artists.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller para endpoints de álbuns
 */
@RestController
@RequestMapping("/api/v1/albums")
@Tag(name = "Álbuns", description = "Gerenciamento de álbuns musicais")
public class AlbumController {
    
    @Autowired
    private AlbumService albumService;
    
    /**
     * Criar novo álbum
     */
    @PostMapping
    @Operation(summary = "Criar álbum", description = "Cria um novo álbum associado a um ou mais artistas")
    public ResponseEntity<AlbumResponseDTO> create(@Valid @RequestBody AlbumRequestDTO request) {
        AlbumResponseDTO response = albumService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Buscar álbum por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar álbum por ID", description = "Retorna detalhes completos de um álbum")
    public ResponseEntity<AlbumResponseDTO> findById(
            @Parameter(description = "ID do álbum") @PathVariable Long id
    ) {
        AlbumResponseDTO response = albumService.findById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Listar todos os álbuns com paginação
     */
    @GetMapping
    @Operation(summary = "Listar álbuns", description = "Lista todos os álbuns com paginação e ordenação")
    public ResponseEntity<PageResponseDTO<AlbumSummaryDTO>> findAll(
            @Parameter(description = "Número da página (começa em 0)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "title") String sortBy,
            
            @Parameter(description = "Direção da ordenação (asc/desc)") 
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        PageResponseDTO<AlbumSummaryDTO> response = albumService.findAll(pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Buscar álbuns de um artista específico
     */
    @GetMapping("/artist/{artistId}")
    @Operation(summary = "Álbuns por artista", description = "Lista todos os álbuns de um artista específico")
    public ResponseEntity<PageResponseDTO<AlbumSummaryDTO>> findByArtistId(
            @Parameter(description = "ID do artista") @PathVariable Long artistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "releaseYear") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        PageResponseDTO<AlbumSummaryDTO> response = albumService.findByArtistId(artistId, pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Atualizar álbum
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar álbum", description = "Atualiza informações de um álbum existente")
    public ResponseEntity<AlbumResponseDTO> update(
            @Parameter(description = "ID do álbum") @PathVariable Long id,
            @Valid @RequestBody AlbumRequestDTO request
    ) {
        AlbumResponseDTO response = albumService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Deletar álbum
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar álbum", description = "Remove um álbum e suas capas do armazenamento")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do álbum") @PathVariable Long id
    ) {
        albumService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Upload de capas de álbum
     */
    @PostMapping(value = "/{id}/covers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de capas", description = "Faz upload de uma ou mais imagens de capa para o álbum")
    public ResponseEntity<AlbumResponseDTO> uploadCovers(
            @Parameter(description = "ID do álbum") @PathVariable Long id,
            @Parameter(description = "Arquivos de imagem") @RequestParam("files") List<MultipartFile> files
    ) {
        AlbumResponseDTO response = albumService.uploadCovers(id, files);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Remover uma capa específica
     */
    @DeleteMapping("/{id}/covers")
    @Operation(summary = "Remover capa", description = "Remove uma capa específica do álbum")
    public ResponseEntity<AlbumResponseDTO> removeCover(
            @Parameter(description = "ID do álbum") @PathVariable Long id,
            @Parameter(description = "URL da capa a ser removida") @RequestParam String coverUrl
    ) {
        AlbumResponseDTO response = albumService.removeCover(id, coverUrl);
        return ResponseEntity.ok(response);
    }
}