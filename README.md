#  Sistema de Gerenciamento de Artistas e Álbuns - Album repo

## Dados do Candidato

- **Nome**: Gabriel Acassio Correia
- **Insc**: 16574
- **Vaga**: ANALISTA DE TI - PERFIL PROFISSIONAL/ESPECIALIDADE - Engenheiro da Computação - Sênior
- **Projeto**: Full Stack Sênior - Java + Angular/React (Anexo II - c)
- **Data**: Janeiro/2026
- **Contato**: Acassiocorreia03@gmail.com

---

> Nota à comissão de avaliação:
> 1. O ratelimiting de 10 requisições por minuto é um valor severo e pode prejudicar a usabilidade da interface gráfica, portanto é necessário atentar-se para que muitas requisições não sejam feitas. Ao utilizar a interface web e notar erros, retorne às páginas principais (artistas ou albuns) e aguarde.
> 2. Para adicionar um álbum a um artista é necessário utilizar os formulários de álbum. Embora sejam entidades independentes, semânticamente a entidade artista existe sem um álbum, todavia para um álbum é condição neccessária a existência de um artista.
> 3. O álbum RR, presente nos dados de inicialização, possui dois artistas para exemplificar o  que foi requisitado. É possível visualizá-lo na interface web. O álbum Carrie & Lowell,presente nos dados de inicialização, possui duas capas para exemplificar o  que foi requisitado. Também é possível visualizá-lo na interface web.
> 4. Este projeto foi desenvolvido em 13 sprints, seguindo os princípios da metodologia extreme programming, que podem ser consultadas no final deste documento.
> 5. Todos os requisitos previstos em edital foram atendidos.

##  Sobre o Projeto

Sistema full stack para gerenciamento de artistas musicais e seus álbuns.

###  Faça um clone deste projeto

```bash
 git clone https://github.com/GacAcassio/gabrielacassiocorreia061092.git
```
---

##  Como Executar

### Pré-requisitos
- Docker 20.10+
- Docker Compose 2.0+

### Executar

1. Certirfique-se de que nenhum outro serviço, volume, imagem ou rede está em conflito com este projeto. Caso seja necessário, execute o comando abaixo para removê-los:

```bash
sudo docker compose down -v
sudo docker system prune -a --volumes --force
```

2. Este projeto utiliza as portas locais 3000, 8085, 9002 e 9003. Certifique-se de que elas estejam disponíveis.

```bash
sudo lsof -i :PORT
```

3. Certifique-se de que sua máquina possui conexão com a internet e que possui todos os pré-requisitos (funcionando). 

4. No diretóriod este projeto (/gabrielacassiocorreia061092), execute o comando abaixo para contruir e levantar todos os serviços necessários

```bash
sudo docker compose up -d --build
```

### Acessar
- Frontend: http://localhost:3000
- Backend: http://localhost:8085/swagger-ui.html
- MinIO: http://localhost:9001

### Credenciais
- **App**: admin / admin123
- **MinIO**: minioadmin / minioadmin

> Após finalizar execute para parar os serviços:
> ```bash sudo docker compose down -v```
---

##  Testes

O projeto possui **107 testes unitários** divididos em 3 categorias:
- **Service Tests** - Testa a lógica de negócio
- **Controller Tests** - Testa os endpoints da API
- **DTO Tests** - Testa validações e estrutura dos DTOs

**Tecnologias:**
- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc (para controllers)

---

## Como Executar

### **Pré-requisitos**
```bash
# Verificar Java
java -version  # Deve ser Java 17+

#Caso não seja, instale-o e excute:
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Verificar Maven Wrapper
./mvnw --version

#Caso não tenha, instale o mvn:
sudo apt update
sudo apt install maven -y

#E  no direótio /backend execute:
mvn wrapper:wrapper

```
## Executar 

1. **Executar TODOS os testes**

```bash
cd backend
./mvnw clean test
```

**Resultado esperado:**
```
Tests run: 107, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```


2. **Executar testes por categoria**

#### Service Tests (23 testes)
```bash
./mvnw test -Dtest=*ServiceTest
```

#### Controller Tests (16 testes)
```bash
./mvnw test -Dtest=*ControllerTest
```

#### DTO Tests (64 testes)
```bash
./mvnw test -Dtest=*DTOTest
```

3. **Executar teste específico**

#### Uma classe completa
```bash
./mvnw test -Dtest=AlbumServiceTest
```
```


#### Um método específico
```bash
./mvnw test -Dtest=AlbumServiceTest#deveCriarAlbumComSucesso
```

#### Múltiplas classes
```bash
./mvnw test -Dtest=AlbumServiceTest,ArtistServiceTest
```

4. **Executar com mais informações**

#### Com stack trace
```bash
./mvnw test -Dtest=AlbumServiceTest -e
```

#### Com debug completo
```bash
./mvnw test -Dtest=AlbumServiceTest -X
```
##  Relatórios de Teste

### **Gerar relatório HTML**
```bash
./mvnw surefire-report:report
```

Relatório gerado em:
```
backend/target/site/surefire-report.html
```

### **Ver resultados detalhados**
```bash
# Logs de cada teste
ls backend/target/surefire-reports/

# Ver arquivo específico
cat backend/target/surefire-reports/com.project.artists.service.AlbumServiceTest.txt
```
---

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

# Arquitetura do Sistema

### Diagrama de Arquitetura:

```
┌─────────────────────────────────────────────────────────┐
│                     FRONTEND                            │
│              (React / Angular / Vue)                    │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ HTTP/REST + WebSocket
                     │
┌────────────────────▼────────────────────────────────────┐
│                  ARTISTS API                            │
│                 (Spring Boot)                           │
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │           CONTROLLERS                           │   │
│  │  - AuthController                               │   │
│  │  - ArtistController                             │   │
│  │  - AlbumController                              │   │
│  │  - RegionalController (Sprint 6)                │   │
│  └────────────┬────────────────────────────────────┘   │
│               │                                         │
│  ┌────────────▼────────────────────────────────────┐   │
│  │            SERVICES                             │   │
│  │  - AuthService                                  │   │
│  │  - ArtistService                                │   │
│  │  - AlbumService                                 │   │
│  │  - RegionalSyncService                          │   │
│  │  - MinioService (Storage)                       │   │
│  │  - NotificationService (WebSocket)              │   │
│  │  - RateLimitService                             │   │
│  └────────────┬────────────────────────────────────┘   │
│               │                                         │
│  ┌────────────▼────────────────────────────────────┐   │
│  │         REPOSITORIES                            │   │
│  │  - UserRepository                               │   │
│  │  - ArtistRepository                             │   │
│  │  - AlbumRepository                              │   │
│  │  - RegionalRepository                           │   │
│  └────────────┬────────────────────────────────────┘   │
│               │                                         │
│  ┌────────────▼────────────────────────────────────┐   │
│  │           SECURITY                              │   │
│  │  - JwtAuthenticationFilter                      │   │
│  │  - RateLimitFilter                              │   │
│  │  - JwtTokenProvider                             │   │
│  │  - SecurityConfig                               │   │
│  └─────────────────────────────────────────────────┘   │
└────┬────────────────────────────────┬─────────────┬────┘
     │                                │             │
     │                                │             │
┌────▼────────┐             ┌─────────▼──────┐  ┌──▼─────────┐
│ PostgreSQL  │             │     MinIO      │  │ External   │
│  Database   │             │  (S3 Storage)  │  │ Regional   │
│             │             │                │  │    API     │
└─────────────┘             └────────────────┘  └────────────┘
```

---

##   Arquitetura da Autenticação 

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
### Padrões Arquiteturais:

1. **Layered Architecture** (Camadas)
   - Controller → Service → Repository → Entity
   
2. **Dependency Injection** (Spring IoC)
   - `@Autowired`, `@Service`, `@Repository`
   
3. **DTO Pattern**
   - Request DTOs (validação)
   - Response DTOs (exposição controlada)
   
4. **Repository Pattern**
   - Spring Data JPA
   
5. **Filter Chain** (Segurança)
   - JWT Filter → Rate Limit Filter

# Modelagem de Dados 

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

##  Estrutura Detalhada das Tabelas

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

---

### 4. Tabela `artist_album` (Junction Table)

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

##  Decisões de Modelagem

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

## Queries Úteis

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

## Migrations Aplicadas

| Versão | Arquivo | Descrição |
|--------|---------|-----------|
| V1 | `create_user_table.sql` | Tabela de usuários |
| V2 | `create_artist_table.sql` | Tabela de artistas |
| V3 | `create_album_table.sql` | Tabela de álbuns (com `artist_id`) |
| V4 | `create_regional_table.sql` | Tabela de regionais |
| V5 | `create_album_artist_table.sql` |Junction table|
| V6 | `insert_sample_data.sql` | Dados de exemplo |

---

## Mapeamento JPA (Java)

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
## Módulos e Funcionalidades

### 1. Autenticação (Auth)

**Arquivos:**
- `AuthController.java`
- `AuthService.java` / `AuthServiceImpl.java`
- `JwtTokenProvider.java`
- `JwtAuthenticationFilter.java`

**Funcionalidades:**
- ✅ Login com username/password
- ✅ Geração de JWT (access + refresh token)
- ✅ Renovação de token (refresh)
- ✅ Validação de token em cada requisição
- ✅ Hash de senhas com BCrypt

**Endpoints:**
```
POST /api/v1/auth/login       - Fazer login
POST /api/v1/auth/refresh     - Renovar token
GET  /api/v1/auth/test        - Testar API
```

**Fluxo:**
```
1. Cliente envia username + password
2. AuthService valida credenciais
3. JwtTokenProvider gera tokens
4. Retorna accessToken (5min) + refreshToken (7 dias)
5. Cliente usa accessToken em Authorization: Bearer <token>
6. JwtAuthenticationFilter valida token em cada request
```

---

### 2. Artistas (Artists)

**Arquivos:**
- `ArtistController.java`
- `ArtistService.java` / `ArtistServiceImpl.java`
- `ArtistRepository.java`
- `Artist.java` (entity)

**Funcionalidades:**
- ✅ CRUD completo de artistas
- ✅ Busca por nome (case insensitive)
- ✅ Paginação e ordenação
- ✅ Relacionamento com álbuns (OneToMany)

**Endpoints:**
```
POST   /api/v1/artists              - Criar artista
GET    /api/v1/artists              - Listar (paginado)
GET    /api/v1/artists/{id}         - Buscar por ID
GET    /api/v1/artists/search       - Buscar por nome
PUT    /api/v1/artists/{id}         - Atualizar
DELETE /api/v1/artists/{id}         - Deletar
```

**Modelo:**
```java
Artist {
  id: Long
  name: String (unique, indexed)
  genre: String
  country: String
  foundedYear: Integer
  biography: String (text)
  albums: List<Album>
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
}
```

---

### 3. Álbuns (Albums)

**Arquivos:**
- `AlbumController.java`
- `AlbumService.java` / `AlbumServiceImpl.java`
- `AlbumRepository.java`
- `Album.java` (entity)
- `MinioService.java` (storage)

**Funcionalidades:**
- ✅ CRUD completo de álbuns
- ✅ Upload múltiplo de capas (MinIO/S3)
- ✅ Relacionamento ManyToOne com artistas
- ✅ Remoção de capas específicas
- ✅ Paginação e ordenação

**Endpoints:**
```
POST   /api/v1/albums                    - Criar álbum
GET    /api/v1/albums                    - Listar (paginado)
GET    /api/v1/albums/{id}               - Buscar por ID
GET    /api/v1/albums/artist/{artistId}  - Álbuns de um artista
PUT    /api/v1/albums/{id}               - Atualizar
DELETE /api/v1/albums/{id}               - Deletar
POST   /api/v1/albums/{id}/covers        - Upload capas
DELETE /api/v1/albums/{id}/covers        - Remover capa
```

**Modelo:**
```java
Album {
  id: Long
  title: String
  releaseYear: Integer
  genre: String
  trackCount: Integer
  artist: Artist (ManyToOne)
  coverUrls: List<String> (JSON)
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
}
```

---

### 4. Regionais

**Arquivos:**
- `RegionalController.java`
- `RegionalSyncService.java` / `RegionalSyncServiceImpl.java`
- `RegionalRepository.java`
- `Regional.java` (entity)
- `RegionalApiClient.java`
- `RegionalSyncScheduler.java`

**Funcionalidades:**
- ✅ Sincronização com API externa
- ✅ Detecção de: novos, removidos, alterados
- ✅ Algoritmo O(n+m) otimizado
- ✅ Job agendado (CRON configurável)
- ✅ Estatísticas detalhadas
- ✅ Retry automático com backoff

**Endpoints:**
```
POST /api/v1/regionais/sync   - Sincronizar (manual)
GET  /api/v1/regionais        - Listar ativos
GET  /api/v1/regionais/all    - Listar todos
```

**Modelo:**
```java
Regional {
  id: Long
  nome: String (unique, indexed)
  ativo: Boolean
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
}
```

**Algoritmo de Sincronização:**
```
Complexidade: O(n + m)
- n = regionais API externa
- m = regionais banco local

1. Fetch API externa → List<RegionalExternoDTO>
2. Fetch banco local → List<Regional> (ativos)
3. Criar HashMap local (nome → Regional) → O(m)
4. Para cada regional externo → O(n)
   - Se não existe → INSERT (novo)
   - Se existe e nome mudou → UPDATE (alterado)
   - Remover do HashMap
5. Regionais restantes no HashMap → UPDATE ativo=false (removidos)
6. Retornar estatísticas
```

**Job Agendado:**
```yaml
app:
  regional-sync:
    enabled: true
    cron: "0 0 2 * * ?"  # 2h da manhã diariamente
```

---

### 5. Upload de Arquivos (MinIO)

**Arquivos:**
- `MinioService.java` / `MinioServiceImpl.java`
- `MinioConfig.java`

**Funcionalidades:**
- ✅ Upload de múltiplas imagens
- ✅ Geração de URLs pré-assinadas
- ✅ Remoção de arquivos
- ✅ Validação de tipo (jpg, png, webp)
- ✅ Armazenamento S3-compatible

**Configuração:**
```yaml
minio:
  url: http://minio:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket-name: artists-covers
```

**Fluxo:**
```
1. Cliente envia multipart/form-data
2. AlbumService valida arquivos
3. MinioService faz upload para MinIO
4. Retorna URL pública
5. Salva URLs no banco (Album.coverUrls)
```

---

### 6. WebSocket (Notificações)

**Arquivos:**
- `NotificationService.java` / `NotificationServiceImpl.java`
- `WebSocketConfig.java`
- `NotificationDTO.java`

**Funcionalidades:**
- ✅ Notificações em tempo real
- ✅ Broadcast para todos os clientes
- ✅ Tipos: INFO, SUCCESS, WARNING, ERROR

**Endpoint:**
```
WS /ws
Topic: /topic/notifications
```

**Uso:**
```javascript
// Frontend conecta
const socket = new SockJS('http://localhost:8085/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, () => {
  stompClient.subscribe('/topic/notifications', (message) => {
    const notification = JSON.parse(message.body);
    console.log(notification);
  });
});
```

**Eventos enviados:**
- Artista criado/atualizado/deletado
- Álbum criado/atualizado/deletado
- Sincronização de regionais concluída

---

### 7. Segurança

**Componentes:**

1. **JWT Authentication**
   - `JwtTokenProvider` - Gera e valida tokens
   - `JwtAuthenticationFilter` - Valida token em cada request
   - `JwtAuthenticationEntryPoint` - Handler de erros 401

2. **Rate Limiting**
   - `RateLimitService` - Controla taxa de requisições
   - `RateLimitFilter` - Aplica limites
   - Configurável por IP e endpoint

3. **Password Security**
   - BCrypt para hash de senhas
   - Validação de força (opcional)

4. **CORS**
   - `CorsConfig` - Configuração de origens permitidas
   - Headers permitidos
   - Métodos permitidos

**Configuração de Segurança:**
```java
// Endpoints públicos
/api/v1/auth/**
/swagger-ui/**
/v3/api-docs/**
/ws/**
/actuator/**

// Endpoints protegidos (requerem JWT)
/api/v1/artists/**
/api/v1/albums/**
/api/v1/regionais/**
```

---

### 8. Paginação

**Implementação:**
- Spring Data Pageable
- Parâmetros: `page`, `size`, `sortBy`, `direction`

**Exemplo:**
```
GET /api/v1/artists?page=0&size=10&sortBy=name&direction=asc
```

**Resposta:**
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 50,
  "totalPages": 5,
  "first": true,
  "last": false
}
```
---

## Endpoints da API

### **Resumo:**

| Grupo | Endpoints | Autenticação |
|-------|-----------|--------------|
| **Auth** | 3 | Não |
| **Artists** | 6 | Sim |
| **Albums** | 8 | Sim |
| **Regionais** | 3 | Sim |
| **TOTAL** | **20** | - |

### **Detalhamento:**

Ver documentação completa em:
```
http://localhost:8085/swagger-ui.html
```

---

## Arquitetura do Frontend

### Padrão Facade
Centraliza as operações e simplifica a interface para os componentes:
- **AuthFacade**: Gerencia autenticação
- **ArtistFacade**: Operações com artistas
- **AlbumFacade**: Operações com álbuns

### Gerenciamento de Estado (BehaviorSubject)
Utiliza RxJS BehaviorSubject para estado reativo:
- **AuthStore**: Estado do usuário autenticado
- **ArtistStore**: Lista e artista selecionado
- **AlbumStore**: Lista e álbum selecionado

### HttpClient
Cliente HTTP centralizado com:
- Interceptor para adicionar token JWT automaticamente
- Renovação automática de token expirado
- Tratamento de erros e rate limiting
- Suporte a upload de arquivos

##  Autenticação

### Fluxo de Autenticação
1. Login retorna `accessToken` (5 min) e `refreshToken`
2. Tokens salvos no `localStorage`
3. Interceptor adiciona token em todas as requisições
4. Token renovado automaticamente quando próximo de expirar
5. Logout ao falhar renovação

### AuthService
```typescript
// Login
await authService.login({ username, password });

// Logout
authService.logout();

// Verificar autenticação
const isAuth = authService.isAuthenticated();

// Obter usuário atual
const user = authService.getCurrentUser();
```

## Services e Facades

### Uso dos Facades
```typescript
import { artistFacade, albumFacade, authFacade } from 'services/facades';

// Login
await authFacade.login({ username: 'admin', password: 'admin123' });

// Listar artistas
await artistFacade.list({ page: 0, size: 10, name: 'Beatles' });

// Criar artista
await artistFacade.create({ name: 'Pink Floyd', bio: '...' });

// Upload de capas
await albumFacade.uploadCovers(albumId, files);
```

### Observando o Estado
```typescript
import { artistStore } from 'stores';

// Inscrever-se no estado
const subscription = artistStore.state$.subscribe(state => {
  console.log('Artists:', state.artists);
  console.log('Loading:', state.loading);
  console.log('Error:', state.error);
});

// Cancelar inscrição
subscription.unsubscribe();
```

### Response Interceptor
- Trata erros 401 renovando o token
- Trata erro 429 (rate limit)
- Desloga usuário se renovação falhar

##  Convenções de Código

- **Nomenclatura**: camelCase para variáveis/funções, PascalCase para componentes/classes
- **Tipagem**: Sempre usar TypeScript com tipos explícitos
- **Imports**: Usar imports absolutos configurados no `tsconfig.json`
- **Comentários**: JSDoc para funções e classes públicas
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

---

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

---

### Sprint 2 - Autenticação e Segurança ✅

**Sistema de Autenticação**
- [x] Entidade User 
- [x] Implementar UserDetailsService
- [x] Configurar Spring Security
- [x] Implementar geração de JWT (expiração 5 min)
- [x] Implementar renovação de token
- [x] Criar endpoint POST /api/v1/auth/login
- [x] Criar endpoint POST /api/v1/auth/refresh
- [ ] Documentar autenticação no Swagger

**Rate Limiting**
- [x] Implementar interceptor/filter para rate limit
- [x] Configurar limite: 10 requisições/minuto por usuário
- [x] Retornar HTTP 429 quando exceder limite
- [x] Adicionar headers de rate limit na resposta

---

### Sprint 3 - CRUD de Artistas  ✅

**Entidades e Repositórios**
- [x] Criar entidade Artist  
- [x] Criar ArtistRepository com métodos customizados  
- [x] Implementar paginação e ordenação  

**Serviços e Controllers**
- [x] Criar ArtistService  
- [x] Implementar POST /api/v1/artists - criar artista  
- [x] Implementar GET /api/v1/artists - listar com paginação e ordenação  
- [x] Implementar GET /api/v1/artists/{id} - buscar por ID  
- [x] Implementar PUT /api/v1/artists/{id} - atualizar artista  
- [x] Implementar busca por nome com ordenação (asc/desc)  
- [x] Adicionar validações (Bean Validation)  

**Documentação**
- [x] Documentar endpoints no Swagger/OpenAPI  
- [x] Adicionar exemplos de request/response  

---

### Sprint 4 - CRUD de Álbuns e Upload de Imagens  ✅

**Entidades e Repositórios**
- [x] Criar entidade Album (relacionamento ManyToMany com Artist)
- [x] Criar AlbumRepository com paginação

**Integração com MinIO**
- [x] Configurar cliente MinIO/S3
- [x] Criar serviço para upload de imagens
- [x] Implementar geração de presigned URLs (30 min expiração)
- [x] Criar bucket

**Endpoints de Álbuns**
- [x] Implementar POST /api/v1/albums - criar álbum
- [x] Implementar POST /api/v1/albums/{id}/covers - upload múltiplas capas
- [x] Implementar GET /api/v1/albums - listar com paginação
- [x] Implementar GET /api/v1/albums/{id} - buscar por ID
- [x] Implementar PUT /api/v1/albums/{id} - atualizar álbum
- [x] Implementar GET /api/v1/artists/{id}/albums - álbuns de um artista
- [x] Retornar presigned URLs nas respostas

---

### Sprint 5 - WebSocket e Notificações   ✅

**Configuração WebSocket**
- [x] Adicionar dependência spring-boot-starter-websocket
- [x] Configurar WebSocket broker (STOMP)
- [x] Criar endpoint WebSocket /ws

**Notificações**
- [x] Criar serviço de notificações
- [x] Emitir evento ao cadastrar novo álbum
- [x] Enviar notificação via WebSocket para clientes conectados
- [x] Documentar protocolo WebSocket no README

---

### Sprint 6 - Sincronização de Regionais (Requisito Sênior)  ✅

**Implementação da Sincronização**
- [x] Criar entidade Regional (id, nome, ativo)
- [x] Criar RegionalRepository
- [x] Implementar client HTTP para consumir API externa
- [x] Criar serviço de sincronização com algoritmo eficiente:
  - [x] Buscar regionais da API externa
  - [x] Buscar regionais locais ativas
  - [x] Comparar e identificar: novos, removidos, alterados
  - [x] Inserir novos, inativar removidos, inativar e criar novos para alterados
- [x] Criar endpoint GET /api/v1/regionais/sync para trigger manual
- [x] Implementar scheduled job (opcional: sync automático)
- [x] Documentar complexidade algorítmica no README

---

### Sprint 7 - Health Checks e Testes Backend   ✅

**Health Checks**
- [x] Implementar endpoint /actuator/health
- [x] Configurar liveness probe
- [x] Configurar readiness probe
- [x] Incluir health checks do PostgreSQL e MinIO

**Testes Unitários**
- [x] Testes unitários para ArtistService
- [x] Testes unitários para AlbumService
- [x] Testes unitários para AuthService
- [x] Testes unitários para RegionalSyncService
- [x] Testes de controllers (MockMvc)
- [x] Cobertura mínima de 70%

---

### Sprint 8 - Frontend Base (React/Angular)  ✅

**Setup Frontend**
- [x] Inicializar projeto (Create React App + TypeScript ou Angular CLI)
- [x] Configurar Tailwind CSS
- [x] Configurar estrutura de pastas (components, services, pages, models)
- [x] Criar Dockerfile para frontend
- [x] Configurar variáveis de ambiente

**Serviços Base**
- [x] Criar HttpClient service com interceptors
- [x] Criar AuthService com gestão de token JWT
- [x] Implementar renovação automática de token
- [x] Criar interceptor para adicionar token 

### Sprint 9 - Telas de Autenticação  ✅

**Login**
- [x] Criar componente de Login
- [x] Formulário com validação (email/username, senha)
- [x] Integrar com AuthService
- [x] Redirecionar após login bem-sucedido
- [x] Exibir mensagens de erro
- [x] Layout responsivo

**Guards e Rotas**
- [x] Criar AuthGuard para proteger rotas
- [x] Configurar rotas públicas e privadas
- [x] Implementar Lazy Loading para módulos

---

### Sprint 10 - Listagem de Artistas  ✅

**Componentes**
- [x] Criar página de listagem de artistas
- [x] Criar componente de card/tabela de artista
- [x] Exibir nome e número de álbuns
- [x] Layout responsivo (grid/flexbox)

**Funcionalidades**
- [x] Implementar campo de busca por nome
- [x] Implementar ordenação (asc/desc)
- [x] Implementar paginação ou scroll infinito
- [x] Integrar com ArtistService
- [x] Loading states e error handling

---

### Sprint 11 - Detalhamento e Cadastro  ✅

**Tela de Detalhamento**
- [x] Criar página de detalhes do artista
- [x] Exibir informações completas do artista
- [x] Listar álbuns associados com capas
- [x] Exibir presigned URLs das imagens
- [x] Mensagem quando não houver álbuns

**Formulários**
- [x] Criar formulário de cadastro/edição de artista
- [x] Validações de formulário (ReactHookForm ou Angular Forms)
- [x] Criar formulário de cadastro/edição de álbum
- [x] Implementar upload de múltiplas imagens
- [x] Preview de imagens antes do upload
- [x] Feedback de sucesso/erro

---

### Sprint 12 - WebSocket Frontend e Notificações  ✅

**Integração WebSocket**
- [x] Adicionar biblioteca WebSocket (SockJS, Stomp)
- [x] Criar serviço WebSocket
- [x] Conectar ao endpoint /ws
- [x] Escutar eventos de novos álbuns

**Sistema de Notificações**
- [x] Criar componente de notificação (toast/snackbar)
- [x] Exibir notificação ao receber evento de novo álbum
- [x] Auto-dismiss após alguns segundos
- [x] Permitir navegação ao álbum notificado

---

### Sprint 13 - Documentação e Finalização  ✅

**README.md Completo**
- [x] Dados de inscrição e vaga
- [x] Descrição da arquitetura
- [x] Diagrama ER do banco de dados
- [x] Decisões técnicas e justificativas
- [x] Complexidade algorítmica da sincronização (Documentado no código)
- [x] Instruções de execução (docker-compose up)
- [x] Instruções para executar testes
- [x] Credenciais padrão para acesso
- [x] Lista de endpoints da API (Disponivei no swagger)
- [x] Tecnologias utilizadas
- [x] O que foi implementado
- [x] O que não foi implementado (se houver) e por quê

**Refinamentos Finais**  ✅
- [x] Revisar código (Clean Code)
- [ ] Remover código comentado e console.logs
- [x] Verificar todos os requisitos atendidos
- [x] Testar docker-compose completo
- [x] Verificar logs de saúde dos containers
- [x] Testar fluxo completo end-to-end
- [x] Revisar histórico de commits (mensagens claras e commits pequenos)

**Checklist Final**  ✅
- [x] Docker-compose funcional (BD + MinIO + API + Frontend)
- [x] Autenticação JWT (5 min + renovação)
- [x] CORS configurado
- [x] Endpoints versionados
- [x] Swagger/OpenAPI documentado
- [x] Flyway migrations
- [x] Upload no MinIO com presigned URLs
- [x] Paginação
- [x] WebSocket funcionando
- [x] Rate limiting (10 req/min)
- [x] Health checks
- [x] Testes unitários
- [x] Sincronização de regionais
- [x] Frontend com TypeScript
- [x] Tailwind CSS
- [x] Lazy Loading
- [x] Padrão Facade
- [x] BehaviorSubject
