INSERT INTO artists (name, bio) VALUES
    ('Serj Tankian', 'Vocalista do System of a Down e artista solo'),
    ('Mike Shinoda', 'Rapper, produtor e vocalista do Linkin Park'),
    ('Michel Teló', 'Cantor e compositor sertanejo brasileiro'),
    ('Guns N'' Roses', 'Banda de hard rock norte-americana')
ON CONFLICT DO NOTHING;

INSERT INTO albums (title, release_year) VALUES
    ('Harakiri', 2012),
    ('Black Blooms', 2024),
    ('The Rough Dog', 2023),
    ('The Rising Tied', 2005),
    ('Post Traumatic', 2018),
    ('Post Traumatic EP', 2018),
    ('Where''d You Go', 2006),
    ('Bem Sertanejo', 2013),
    ('Bem Sertanejo - O Show (Ao Vivo)', 2014),
    ('Bem Sertanejo - (1ª Temporada) - EP', 2013),
    ('Use Your Illusion I', 1991),
    ('Use Your Illusion II', 1991),
    ('Greatest Hits', 2004);


