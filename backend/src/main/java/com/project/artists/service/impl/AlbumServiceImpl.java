package com.project.artists.service.impl;

import com.project.artists.dto.request.AlbumRequestDTO;
import com.project.artists.dto.response.AlbumResponseDTO;
import com.project.artists.dto.response.AlbumSummaryDTO;
import com.project.artists.dto.response.PageResponseDTO;
import com.project.artists.entity.Album;
import com.project.artists.entity.Artist;
import com.project.artists.exception.BadRequestException;
import com.project.artists.exception.ResourceNotFoundException;
import com.project.artists.repository.AlbumRepository;
import com.project.artists.repository.ArtistRepository;
import com.project.artists.service.AlbumService;
import com.project.artists.service.MinioService;
import com.project.artists.service.NotificationService;
import com.project.artists.dto.notification.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de álbuns
 */
@Service
@Transactional
public class AlbumServiceImpl implements AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private MinioService minioService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public AlbumResponseDTO create(AlbumRequestDTO request) {
        // Validar e buscar artistas
        Set<Artist> artists = findArtistsByIds(request.getArtistIds());

        // Criar álbum
        Album album = new Album();
        album.setTitle(request.getTitle());
        album.setReleaseYear(request.getReleaseYear());
        album.setArtists(artists);

        Album saved = albumRepository.save(album);

        // Enviar notificação em tempo real
        AlbumResponseDTO response = toResponseDTO(saved);
        String artistNames = artists.stream()
                .map(Artist::getName)
                .collect(Collectors.joining(", "));

        notificationService.sendNotification(
                NotificationType.ALBUM_CREATED,
                "Novo Álbum Cadastrado",
                String.format("O álbum '%s' de %s foi adicionado ao sistema.",
                        saved.getTitle(), artistNames),
                response
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumResponseDTO findById(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album", "id", id));

        return toResponseDTO(album);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<AlbumSummaryDTO> findAll(Pageable pageable) {
        Page<Album> page = albumRepository.findAll(pageable);

        List<AlbumSummaryDTO> content = page.getContent().stream()
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
    public PageResponseDTO<AlbumSummaryDTO> findByArtistId(Long artistId, Pageable pageable) {
        // Verificar se artista existe
        if (!artistRepository.existsById(artistId)) {
            throw new ResourceNotFoundException("Artist", "id", artistId);
        }

        Page<Album> page = albumRepository.findByArtists_Id(artistId, pageable);

        List<AlbumSummaryDTO> content = page.getContent().stream()
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
    public AlbumResponseDTO update(Long id, AlbumRequestDTO request) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album", "id", id));

        // Atualizar dados básicos
        album.setTitle(request.getTitle());
        album.setReleaseYear(request.getReleaseYear());

        // Atualizar artistas
        Set<Artist> artists = findArtistsByIds(request.getArtistIds());
        album.setArtists(artists);

        Album updated = albumRepository.save(album);

        // Enviar notificação de atualização
        AlbumResponseDTO response = toResponseDTO(updated);
        notificationService.sendNotification(
                NotificationType.ALBUM_UPDATED,
                "Álbum Atualizado",
                String.format("O álbum '%s' foi atualizado.", updated.getTitle()),
                response
        );

        return response;
    }

    @Override
    public void delete(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album", "id", id));

        String albumTitle = album.getTitle();

        // Deletar capas do MinIO
        for (String objectName : album.getCoverUrls()) {
            try {
                minioService.deleteFile(objectName);
            } catch (Exception e) {
                System.err.println("Erro ao deletar capa: " + objectName);
            }
        }

        albumRepository.delete(album);

        // Enviar notificação de deleção
        notificationService.sendNotification(
                NotificationType.ALBUM_DELETED,
                "Álbum Removido",
                String.format("O álbum '%s' foi removido do sistema.", albumTitle)
        );
    }

    @Override
    public AlbumResponseDTO uploadCovers(Long id, List<MultipartFile> files) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album", "id", id));

        if (files == null || files.isEmpty()) {
            throw new BadRequestException("Nenhum arquivo foi enviado");
        }

        // Upload dos arquivos
        List<String> objectNames = minioService.uploadFiles(files, "album-covers");

        // Adicionar aos cover_urls existentes
        List<String> currentUrls = album.getCoverUrls();
        if (currentUrls == null) {
            currentUrls = new ArrayList<>();
        }
        currentUrls.addAll(objectNames);
        album.setCoverUrls(currentUrls);

        Album updated = albumRepository.save(album);

        // Enviar notificação de upload de capa
        AlbumResponseDTO response = toResponseDTO(updated);
        notificationService.sendNotification(
                NotificationType.ALBUM_COVER_UPLOADED,
                "Nova Capa de Álbum",
                String.format("%d nova(s) capa(s) adicionada(s) ao álbum '%s'.",
                        files.size(), updated.getTitle()),
                response
        );

        return response;
    }

    @Override
    public AlbumResponseDTO removeCover(Long id, String coverUrl) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album", "id", id));

        List<String> coverUrls = album.getCoverUrls();

        if (coverUrls == null || !coverUrls.contains(coverUrl)) {
            throw new BadRequestException("Capa não encontrada neste álbum");
        }

        // Remover do MinIO
        minioService.deleteFile(coverUrl);

        // Remover da lista
        coverUrls.remove(coverUrl);
        album.setCoverUrls(coverUrls);

        Album updated = albumRepository.save(album);

        return toResponseDTO(updated);
    }

    // =====================================================
    // PESQUISAS (SEM BUILDER, SEM MAPPER)
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<AlbumSummaryDTO> searchByTitle(String title, Pageable pageable) {
        Page<Album> page = albumRepository.findByTitleContainingIgnoreCase(title, pageable);

        List<AlbumSummaryDTO> content = page.getContent().stream()
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
    public PageResponseDTO<AlbumSummaryDTO> searchByArtistName(String name, Pageable pageable) {
        Page<Album> page = albumRepository.findDistinctByArtists_NameContainingIgnoreCase(name, pageable);

        List<AlbumSummaryDTO> content = page.getContent().stream()
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

    // =====================================================
    // MÉTODOS AUXILIARES
    // =====================================================

    private Set<Artist> findArtistsByIds(List<Long> artistIds) {
        if (artistIds == null || artistIds.isEmpty()) {
            throw new BadRequestException("Álbum deve ter pelo menos um artista");
        }

        Set<Artist> artists = new HashSet<>();

        for (Long artistId : artistIds) {
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", artistId));
            artists.add(artist);
        }

        return artists;
    }

    private AlbumResponseDTO toResponseDTO(Album album) {
        AlbumResponseDTO dto = new AlbumResponseDTO();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setReleaseYear(album.getReleaseYear());
        dto.setCreatedAt(album.getCreatedAt());
        dto.setUpdatedAt(album.getUpdatedAt());

        // Converter artistas
        List<AlbumResponseDTO.ArtistSummaryDTO> artists = album.getArtists().stream()
                .map(artist -> new AlbumResponseDTO.ArtistSummaryDTO(
                        artist.getId(),
                        artist.getName()
                ))
                .collect(Collectors.toList());
        dto.setArtists(artists);

        // Gerar presigned URLs para as capas
        List<String> coverUrls = album.getCoverUrls();
        if (coverUrls != null && !coverUrls.isEmpty()) {
            List<String> presignedUrls = minioService.generatePresignedUrls(coverUrls);
            dto.setCoverUrls(presignedUrls);
        }

        return dto;
    }

    private AlbumSummaryDTO toSummaryDTO(Album album) {
        AlbumSummaryDTO dto = new AlbumSummaryDTO();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setReleaseYear(album.getReleaseYear());

        // Nomes dos artistas
        List<String> artistNames = album.getArtists().stream()
                .map(Artist::getName)
                .collect(Collectors.toList());
        dto.setArtistNames(artistNames);

        // Primeira capa (presigned URL)
        List<String> coverUrls = album.getCoverUrls();
        if (coverUrls != null && !coverUrls.isEmpty()) {
            String firstCover = minioService.generatePresignedUrl(coverUrls.get(0));
            dto.setCoverUrl(firstCover);
        }

        return dto;
    }
}