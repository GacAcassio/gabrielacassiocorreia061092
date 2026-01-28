-- V7__change_album_artist_to_many_to_many.sql
-- Altera relacionamento Album-Artist de 1:N para N:N

-- 1. Criar tabela intermediária (junction table)
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

-- 2. Criar índices para performance
CREATE INDEX idx_artist_album_artist_id ON artist_album(artist_id);
CREATE INDEX idx_artist_album_album_id ON artist_album(album_id);

-- 3. Migrar dados existentes (de albums.artist_id para artist_album)
INSERT INTO artist_album (artist_id, album_id)
SELECT artist_id, id 
FROM albums
WHERE artist_id IS NOT NULL;

-- 4. Remover coluna artist_id da tabela albums
-- IMPORTANTE: Isso quebra a FK antiga
ALTER TABLE albums DROP CONSTRAINT IF EXISTS fk_albums_artist;
ALTER TABLE albums DROP COLUMN IF EXISTS artist_id;

-- Comentários
COMMENT ON TABLE artist_album IS 'Tabela de relacionamento N:N entre artistas e álbuns';
COMMENT ON COLUMN artist_album.artist_id IS 'ID do artista';
COMMENT ON COLUMN artist_album.album_id IS 'ID do álbum';
