-- Usuário padrão: admin / admin123
-- Senha hash gerada com BCrypt
INSERT INTO users (username, email, password, created_at, updated_at)
VALUES (
    'admin', 
    'admin@artists.com', 
    '$2a$10$rL3qKjmQ8xP8vN5hX9Xo1eBqY0hJ3Y5jX6KlMnP7oQ8vZ9wR1sT2u',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;
