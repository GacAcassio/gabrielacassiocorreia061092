-- Dados de exemplo conforme especificação do projeto
INSERT INTO artists (name, bio) VALUES
    ('Serj Tankian', 'Vocalista do System of a Down e artista solo'),
    ('Mike Shinoda', 'Rapper, produtor e vocalista do Linkin Park'),
    ('Michel Teló', 'Cantor e compositor sertanejo brasileiro'),
    ('Guns N'' Roses', 'Banda de hard rock norte-americana')
ON CONFLICT DO NOTHING;

INSERT INTO albums (title, artist_id, release_year) VALUES
    ('Harakiri', 1, 2012),
    ('Black Blooms', 1, 2024),
    ('The Rough Dog', 1, 2023),
    ('The Rising Tied', 2, 2005),
    ('Post Traumatic', 2, 2018),
    ('Post Traumatic EP', 2, 2018),
    ('Where''d You Go', 2, 2006),
    ('Bem Sertanejo', 3, 2013),
    ('Bem Sertanejo - O Show (Ao Vivo)', 3, 2014),
    ('Bem Sertanejo - (1ª Temporada) - EP', 3, 2013),
    ('Use Your Illusion I', 4, 1991),
    ('Use Your Illusion II', 4, 1991),
    ('Greatest Hits', 4, 2004);

INSERT INTO users (username, email, password, created_at, updated_at)
VALUES (
    'admin',
    'admin@artists.com',
    '$2a$10$LAUcZ.dgpNbfXyl1/OKKpen4wYPSmhxmRzbkdchgDkx6gZziwqdnG', -- senha: admin123
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;


