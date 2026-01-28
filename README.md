# 🎵 Sistema de Gerenciamento de Artistas e Álbuns

## 📋 Dados do Candidato

- **Nome**: Gabriel Acassio Correia
- **Vaga**: Desenvolvedor Full Stack Sênior - Java + Angular/React
- **Data**: Janeiro/2026

---

## 🎯 Sobre o Projeto

Sistema full stack para gerenciamento de artistas musicais e seus álbuns.

### Stack Tecnológico

**Backend:**
- Java 17 + Spring Boot 3.2
- PostgreSQL 15
- MinIO (S3)
- JWT Authentication
- WebSocket (STOMP)
- Flyway Migrations

**Frontend:**
- React 18 + TypeScript / Angular 17
- Tailwind CSS
- RxJS (BehaviorSubject)
- WebSocket Client

**DevOps:**
- Docker + Docker Compose

---

## 🚀 Como Executar

### Pré-requisitos
- Docker 20.10+
- Docker Compose 2.0+

### Executar
```bash
sudo docker-compose up --build
```

### Acessar
- Frontend: http://localhost:3000
- Backend: http://localhost:8080/swagger-ui.html
- MinIO: http://localhost:9001

### Credenciais
- **App**: admin / admin123
- **MinIO**: minioadmin / minioadmin

---
# 🏗️ Arquitetura do Sistema

### Visão Geral

```
┌─────────────────┐         ┌──────────────────┐
│                 │         │                  │
│   Frontend      │◄───────►│   Backend API    │
│ (React/Angular) │  HTTPS  │  (Spring Boot)   │
│                 │ WebSocket│                  │
└─────────────────┘         └──────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
                    ▼               ▼               ▼
            ┌──────────────┐ ┌──────────┐ ┌────────────────┐
            │              │ │          │ │                │
            │  PostgreSQL  │ │  MinIO   │ │  External API  │
            │   Database   │ │  (S3)    │ │  (Regionais)   │
            │              │ │          │ │                │
            └──────────────┘ └──────────┘ └────────────────┘
```

---

##  🪪 Arquitetura da Autenticação 

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ 1. POST /auth/login {username, password}
       ▼
┌─────────────────────┐
│  AuthController     │
└──────┬──────────────┘
       │ 2. Valida credenciais
       ▼
┌─────────────────────┐
│  AuthService        │
│  - UserDetailsService│
│  - BCrypt           │
└──────┬──────────────┘
       │ 3. Gera JWT
       ▼
┌─────────────────────┐
│  JwtTokenProvider   │
│  - Secret key       │
│  - Expiration: 5min │
└──────┬──────────────┘
       │ 4. Retorna token
       ▼
┌─────────────┐
│   Client    │ Armazena token
└──────┬──────┘
       │ 5. GET /artists (Authorization: Bearer <token>)
       ▼
┌─────────────────────┐
│ JwtAuthFilter       │ Valida token
└──────┬──────────────┘
       │ 6. Token válido?
       ▼
┌─────────────────────┐
│ ArtistController    │ Processa requisição
└─────────────────────┘
```

# 🗄️ Modelagem de Dados - Atualizada (N:N)

## Diagrama ER - Versão Atualizada

```
┌─────────────────────┐
│       USER          │
├─────────────────────┤
│ id (PK)             │
│ username (UNIQUE)   │
│ password            │
│ email (UNIQUE)      │
│ created_at          │
│ updated_at          │
└─────────────────────┘

┌─────────────────────┐              ┌─────────────────────┐
│      ARTIST         │              │   ARTIST_ALBUM      │
├─────────────────────┤              ├─────────────────────┤
│ id (PK)             │◄────────────┤│ artist_id (FK, PK)  │
│ name (NOT NULL)     │      N      ││ album_id (FK, PK)   │
│ bio                 │              │└─────────────────────┘
│ created_at          │                        │
│ updated_at          │                        │ N
└─────────────────────┘                        │
                                                ▼
                                    ┌─────────────────────┐
                                    │       ALBUM         │
                                    ├─────────────────────┤
                                    │ id (PK)             │
                                    │ title (NOT NULL)    │
                                    │ release_year        │
                                    │ cover_urls (JSONB)  │
                                    │ created_at          │
                                    │ updated_at          │
                                    └─────────────────────┘

┌─────────────────────┐
│     REGIONAL        │
├─────────────────────┤
│ id (PK)             │
│ nome (NOT NULL)     │
│ ativo (DEFAULT true)│
│ created_at          │
│ updated_at          │
└─────────────────────┘
```

---

## 📊 Estrutura Detalhada das Tabelas

### 1. Tabela `users`

| Coluna | Tipo | Constraints | Descrição |
|--------|------|------------|-----------|
| id | BIGSERIAL | PRIMARY KEY | Identificador único |
| username | VARCHAR(50) | NOT NULL, UNIQUE | Nome de usuário para login |
| email | VARCHAR(100) | NOT NULL, UNIQUE | Email do usuário |
| password | VARCHAR(255) | NOT NULL | Hash BCrypt da senha |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Data de criação |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Data de atualização |

**Índices:**
- `idx_users_username` em `username`
- `idx_users_email` em `email`

---

### 2. Tabela `artists`

| Coluna | Tipo | Constraints | Descrição |
|--------|------|------------|-----------|
| id | BIGSERIAL | PRIMARY KEY | Identificador único |
| name | VARCHAR(200) | NOT NULL | Nome do artista ou banda |
| bio | TEXT | NULL | Biografia/descrição (opcional) |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Data de cadastro |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Data de atualização |

**Índices:**
- `idx_artists_name` em `name`
- `idx_artists_created_at` em `created_at`

---

### 3. Tabela `albums`

| Coluna | Tipo | Constraints | Descrição |
|--------|------|------------|-----------|
| id | BIGSERIAL | PRIMARY KEY | Identificador único |
| title | VARCHAR(200) | NOT NULL | Título do álbum |
| release_year | INTEGER | NULL | Ano de lançamento (opcional) |
| cover_urls | JSONB | DEFAULT '[]'::jsonb | Array de URLs das capas (MinIO) |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Data de cadastro |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Data de atualização |

**Índices:**
- `idx_albums_title` em `title`
- `idx_albums_cover_urls` em `cover_urls` (GIN index)

**⚠️ Nota:** A coluna `artist_id` foi **REMOVIDA** na Migration V7

---

### 4. Tabela `artist_album` (Junction Table) 🆕

| Coluna | Tipo | Constraints | Descrição |
|--------|------|------------|-----------|
| artist_id | BIGINT | NOT NULL, FK → artists(id) | ID do artista |
| album_id | BIGINT | NOT NULL, FK → albums(id) | ID do álbum |
| - | - | PRIMARY KEY (artist_id, album_id) | Chave composta |

**Foreign Keys:**
- `fk_artist_album_artist`: `artist_id` → `artists(id)` ON DELETE CASCADE
- `fk_artist_album_album`: `album_id` → `albums(id)` ON DELETE CASCADE

**Índices:**
- `idx_artist_album_artist_id` em `artist_id`
- `idx_artist_album_album_id` em `album_id`

**Exemplo de dados:**
```sql
-- Serj Tankian - álbuns solo
INSERT INTO artist_album VALUES (1, 1);  -- Harakiri
INSERT INTO artist_album VALUES (1, 2);  -- Black Blooms

-- Fort Minor (Mike Shinoda + colaboradores)
INSERT INTO artist_album VALUES (2, 4);  -- The Rising Tied
INSERT INTO artist_album VALUES (5, 4);  -- Artista convidado X
```

---

### 5. Tabela `regional`

| Coluna | Tipo | Constraints | Descrição |
|--------|------|------------|-----------|
| id | INTEGER | PRIMARY KEY | ID da regional (vem da API externa) |
| nome | VARCHAR(200) | NOT NULL | Nome da regional |
| ativo | BOOLEAN | NOT NULL, DEFAULT true | Status ativo/inativo (soft delete) |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Data de criação |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Data de sincronização |

**Índices:**
- `idx_regional_ativo` em `ativo`
- `idx_regional_nome` em `nome`

---

## 🎯 Decisões de Modelagem

### 1. **Tabela USER**
- **Propósito:** Autenticação JWT
- **Username e Email únicos:** Permite login por ambos
- **Password BCrypt:** Hash com salt (mínimo 10 rounds)
- **Timestamps:** Auditoria de criação e modificação
- **Sem roles por enquanto:** Sistema simples, todos têm mesmas permissões

---

### 2. **Tabela ARTIST**
- **Propósito:** Armazenar artistas/bandas individuais
- **Campo `bio`:** Opcional, permite descrições longas (TEXT)
- **Índice em `name`:** Otimiza buscas alfabéticas e filtros
- **Relacionamento N:N:** Permite colaborações entre artistas

---

### 3. **Tabela ALBUM**
- **Propósito:** Armazenar álbuns musicais
- **Relacionamento N:N com Artist:**
  - ✅ Permite colaborações (feat., bandas temporárias)
  - ✅ Exemplo: "The Rising Tied" - Fort Minor (Mike Shinoda + convidados)
  - ✅ Álbuns ao vivo com múltiplos artistas
- **`cover_urls` JSONB:**
  - Armazena array de URLs: `["url1.jpg", "url2.jpg"]`
  - Permite múltiplas capas (versões diferentes)
  - Índice GIN permite buscar dentro do JSON
- **`release_year` opcional:**
  - Nem todos os álbuns têm ano definido
  - Facilita ordenação cronológica
- **Sem `artist_id`:**
  - Foi **removido** na Migration V7
  - Relacionamento agora é via tabela `artist_album`

---

### 4. **Tabela ARTIST_ALBUM (Junction Table)**
- **Propósito:** Implementar relacionamento N:N
- **Chave composta:** `(artist_id, album_id)` garante unicidade
- **ON DELETE CASCADE:**
  - Deletar artista → remove relacionamentos
  - Deletar álbum → remove relacionamentos
  - Não deixa registros órfãos
- **Índices bidirecionais:**
  - Buscar álbuns de um artista: rápido
  - Buscar artistas de um álbum: rápido
- **Sem campos extras:**
  - Tabela pura de relacionamento
  - Futuramente pode adicionar: `order`, `role` (ex: "vocalista", "produtor")

---

### 5. **Tabela REGIONAL**
- **Propósito:** Sincronização com API externa
- **ID não auto-increment:**
  - Vem da API externa
  - Tipo INTEGER (conforme especificação)
- **Campo `ativo` (Soft Delete):**
  - ✅ Mantém histórico
  - ✅ Permite auditoria
  - ✅ Facilita rollback
  - ❌ Não usa DELETE físico
- **Sincronização:**
  - Novo na API → INSERT
  - Removido da API → UPDATE ativo=false
  - Nome alterado → UPDATE ativo=false (antigo) + INSERT (novo)

---

## 🔍 Queries Úteis

### Buscar álbuns de um artista:
```sql
SELECT a.* 
FROM albums a
JOIN artist_album aa ON a.id = aa.album_id
WHERE aa.artist_id = 1;
```

### Buscar artistas de um álbum:
```sql
SELECT ar.* 
FROM artists ar
JOIN artist_album aa ON ar.id = aa.artist_id
WHERE aa.album_id = 1;
```

### Buscar álbuns com múltiplos artistas (colaborações):
```sql
SELECT a.id, a.title, COUNT(aa.artist_id) as num_artists
FROM albums a
JOIN artist_album aa ON a.id = aa.album_id
GROUP BY a.id, a.title
HAVING COUNT(aa.artist_id) > 1;
```

### Buscar artistas mais produtivos:
```sql
SELECT ar.name, COUNT(aa.album_id) as num_albums
FROM artists ar
JOIN artist_album aa ON ar.id = aa.artist_id
GROUP BY ar.id, ar.name
ORDER BY num_albums DESC;
```

---

## 📝 Migrations Aplicadas

| Versão | Arquivo | Descrição |
|--------|---------|-----------|
| V1 | `create_user_table.sql` | Tabela de usuários |
| V2 | `create_artist_table.sql` | Tabela de artistas |
| V3 | `create_album_table.sql` | Tabela de álbuns (com `artist_id`) |
| V4 | `create_regional_table.sql` | Tabela de regionais |
| V5 | `insert_default_user.sql` | Usuário padrão (admin/admin123) |
| V6 | `insert_sample_data.sql` | Dados de exemplo |
| **V7** | `change_album_artist_to_many_to_many.sql` | **N:N entre Album-Artist** 🆕 |

---

## 🎨 Mapeamento JPA (Java)

### Artist.java
```java
@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String bio;
    
    @ManyToMany(mappedBy = "artists")
    @JsonIgnore
    private Set<Album> albums = new HashSet<>();
}
```

### Album.java
```java
@Entity
@Table(name = "albums")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @ManyToMany
    @JoinTable(
        name = "artist_album",
        joinColumns = @JoinColumn(name = "album_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private Set<Artist> artists = new HashSet<>();
}
```

---

## ✅ Status do Projeto

### Sprint 0 - Setup ✅
- [x] Criar estrutura de diretórios
- [x] Configurar .gitignore
- [x] Criar README.md
- [x] Criar docker-compose.yml
- [x] Criar .env.example
- [x] Preencher dados no README.md
- [x] Criar documentação 
- [x] Testar docker-compose
- [x] Fazer primeiro commit

### Sprint 1 - Backend ✅

**Docker e Banco de Dados**
- [x] Criar docker-compose.yml com serviços: PostgreSQL, MinIO, API, Frontend
- [x] Configurar variáveis de ambiente
- [x] Configurar PostgreSQL (porta, credenciais, volume)
- [x] Configurar MinIO (porta, credenciais, buckets)

**Setup Backend Spring Boot**
- [x] Inicializar projeto Spring Boot (Spring Initializr)
- [x] Adicionar dependências: Web, JPA, PostgreSQL, Flyway, Security, JWT, MinIO/S3, WebSocket, Validation, OpenAPI
- [x] Configurar application.yml (datasource, MinIO, JWT)
- [x] Criar Dockerfile para API
- [x] Configurar CORS restrito ao domínio do frontend

**Modelagem e Migrations**
- [x] Criar modelo de dados (Artist, Album, User, Regional)
- [x] Criar migration V1 - Tabelas User e Artist
- [x] Criar migration V2 - Tabela Album com FK para Artist
- [x] Criar migration V3 - Tabela Regional (id, nome, ativo)
- [x] Criar migration V4 - Popular dados de exemplo


### Sprint 2 - Autenticação e Segurança ✅

**Sistema de Autenticação**

- [x] Entidade User 
- [x] Implementar UserDetailsService
- [x] Configurar Spring Security
- [x] Implementar geração de JWT (expiração 5 min)
- [x] Implementar renovação de token
- [x] Criar endpoint POST /api/v1/auth/login
- [x] Criar endpoint POST /api/v1/auth/refresh
- [x] Documentar autenticação no Swagger

**Rate Limiting**

- [x] Implementar interceptor/filter para rate limit
- [x] Configurar limite: 10 requisições/minuto por usuário
- [x] Retornar HTTP 429 quando exceder limite
- [x] Adicionar headers de rate limit na resposta