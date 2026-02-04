package com.project.artists.controller;

import com.project.artists.dto.request.ArtistRequestDTO;
import com.project.artists.dto.response.ArtistResponseDTO;
import com.project.artists.dto.response.ArtistSummaryDTO;
import com.project.artists.dto.response.PageResponseDTO;
import com.project.artists.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para endpoints de artistas
 */
@RestController
@RequestMapping("/api/v1/artists")
@Tag(name = "Artistas", description = "2. Gerenciamento de artistas e bandas musicais")
public class ArtistController {
    
    @Autowired
    private ArtistService artistService;
    
    /**
     * Criar novo artista
     */
    @PostMapping
    @Operation(summary = "Criar artista", description = "Cria um novo artista ou banda")
    public ResponseEntity<ArtistResponseDTO> create(@Valid @RequestBody ArtistRequestDTO request) {
        ArtistResponseDTO response = artistService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Buscar artista por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar artista por ID", description = "Retorna detalhes completos de um artista")
    public ResponseEntity<ArtistResponseDTO> findById(
            @Parameter(description = "ID do artista") @PathVariable Long id
    ) {
        ArtistResponseDTO response = artistService.findById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Listar todos os artistas com paginação
     */
    @GetMapping
    @Operation(summary = "Listar artistas", description = "Lista todos os artistas com paginação e ordenação")
    public ResponseEntity<PageResponseDTO<ArtistSummaryDTO>> findAll(
            @Parameter(description = "Número da página (começa em 0)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "name") String sortBy,
            
            @Parameter(description = "Direção da ordenação (asc/desc)") 
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        PageResponseDTO<ArtistSummaryDTO> response = artistService.findAll(pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Buscar artistas por nome
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar por nome", description = "Busca artistas por nome (case insensitive)")
    public ResponseEntity<PageResponseDTO<ArtistSummaryDTO>> searchByName(
            @Parameter(description = "Nome ou parte do nome") 
            @RequestParam String name,
            
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        PageResponseDTO<ArtistSummaryDTO> response = artistService.searchByName(name, pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Atualizar artista
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar artista", description = "Atualiza informações de um artista existente")
    public ResponseEntity<ArtistResponseDTO> update(
            @Parameter(description = "ID do artista") @PathVariable Long id,
            @Valid @RequestBody ArtistRequestDTO request
    ) {
        ArtistResponseDTO response = artistService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Deletar artista
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar artista", description = "Remove um artista e todos os seus álbuns")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do artista") @PathVariable Long id
    ) {
        artistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}