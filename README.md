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
docker-compose up --build
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

## 🗄️ Modelagem de Dados

### Diagrama ER

```
┌─────────────────────┐
│       USER          │
├─────────────────────┤
│ id (PK)             │
│ username (UNIQUE)   │
│ password            │
│ email (UNIQUE)      │
│ created_at          │
└─────────────────────┘

┌─────────────────────┐
│      ARTIST         │
├─────────────────────┤
│ id (PK)             │
│ name (NOT NULL)     │
│ bio                 │
│ created_at          │
│ updated_at          │
└─────────────────────┘
         │
         │ 1
         │
         │ N
         ▼
┌─────────────────────┐
│       ALBUM         │
├─────────────────────┤
│ id (PK)             │
│ title (NOT NULL)    │
│ artist_id (FK)      │
│ release_year        │
│ cover_urls (JSON)   │
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

##  Arquitetura da Autenticação 

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

---

### Decisões de Modelagem

#### 1. **Tabela USER**
- Armazena credenciais de autenticação
- Username e email únicos para login
- Password com hash BCrypt
- Timestamp de criação para auditoria

#### 2. **Tabela ARTIST**
- Armazena informações dos artistas/bandas
- Campo `bio` opcional para descrição
- Timestamps para rastreamento de mudanças
- Índice em `name` para otimizar buscas

#### 3. **Tabela ALBUM**
- Relacionamento N:1 com ARTIST (um artista pode ter vários álbuns)
- `cover_urls` armazena array JSON com URLs das capas (presigned URLs do MinIO)
- `release_year` opcional (pode ser adicionado posteriormente)
- Índice em `artist_id` para consultas eficientes

#### 4. **Tabela REGIONAL**
- Estrutura simples conforme especificação
- Campo `ativo` para soft delete (mantém histórico)
- Timestamps para rastreamento de sincronizações

---

## ✅ Status do Projeto

### Sprint 0 - Setup ✅
- [x] Criar estrutura de diretórios
- [x] Configurar .gitignore
- [x] Criar README.md
- [x] Criar docker-compose.yml
- [x] Criar .env.example
- [x] Preencher dados no README.md
- [ ] Criar documentação 
- [x] Testar docker-compose
- [x] Fazer primeiro commit

### Sprint 1 - Backend 

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


### Sprint 2 - Autenticação e Segurança

**Sistema de Autenticação**

- [x] Entidade User 
- [x] Implementar UserDetailsService
- [x] Configurar Spring Security
- [x] Implementar geração de JWT (expiração 5 min)
- [x] Implementar renovação de token
- [x] Criar endpoint POST /api/v1/auth/login
- [x] Criar endpoint POST /api/v1/auth/refresh
- [] Documentar autenticação no Swagger

**Rate Limiting**

- [x] Implementar interceptor/filter para rate limit
- [x] Configurar limite: 10 requisições/minuto por usuário
- [x] Retornar HTTP 429 quando exceder limite
- [x] Adicionar headers de rate limit na resposta