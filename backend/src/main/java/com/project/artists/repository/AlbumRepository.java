package com.project.artists.repository;

import com.project.artists.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    Page<Album> findByArtists_Id(Long artistId, Pageable pageable);
    Page<Album> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Album> findDistinctByArtists_NameContainingIgnoreCase(String name, Pageable pageable);
    
}
