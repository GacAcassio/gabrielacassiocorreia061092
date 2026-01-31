CREATE TABLE artist_album (
    artist_id BIGINT NOT NULL,
    album_id BIGINT NOT NULL,
    
    PRIMARY KEY (artist_id, album_id),
    
    CONSTRAINT fk_artist_album_artist 
        FOREIGN KEY (artist_id) 
        REFERENCES artists(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_artist_album_album 
        FOREIGN KEY (album_id) 
        REFERENCES albums(id) 
        ON DELETE CASCADE
);

CREATE INDEX idx_artist_album_artist_id ON artist_album(artist_id);
CREATE INDEX idx_artist_album_album_id ON artist_album(album_id);

COMMENT ON TABLE artist_album IS 'Tabela de relacionamento N:N entre artistas e álbuns';
COMMENT ON COLUMN artist_album.artist_id IS 'ID do artista';
COMMENT ON COLUMN artist_album.album_id IS 'ID do álbum';
