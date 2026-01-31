CREATE TABLE albums (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    release_year INTEGER,
    cover_urls JSONB DEFAULT '[]'::jsonb,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_albums_title ON albums(title);
CREATE INDEX idx_albums_cover_urls ON albums USING GIN (cover_urls);
