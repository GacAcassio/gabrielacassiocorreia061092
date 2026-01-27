#!/bin/bash

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

clear
echo "======================================"
echo "SPRINT 1 - SETUP"
echo "======================================"

# Salvar diretório raiz
PROJECT_ROOT=$(pwd)

# Verificar se está no diretório raiz do projeto
if [ ! -f "docker-compose.yml" ]; then
    echo -e "${RED}❌ Execute este script no diretório raiz do projeto${NC}"
    exit 1
fi

# Verificar dependências
for cmd in curl unzip java; do
    if ! command -v $cmd &> /dev/null; then
        echo -e "${RED}❌ $cmd não encontrado!${NC}"
        echo "Instale: sudo apt install $cmd"
        exit 1
    fi
done

echo -e "${BLUE}📦 Baixando projeto do Spring Initializr...${NC}"

# Criar projeto via Spring Initializr API
curl -G https://start.spring.io/starter.zip \
  --data-urlencode "type=maven-project" \
  --data-urlencode "language=java" \
  --data-urlencode "bootVersion=3.2.2" \
  --data-urlencode "baseDir=backend" \
  --data-urlencode "groupId=com.project" \
  --data-urlencode "artifactId=artists" \
  --data-urlencode "name=artists" \
  --data-urlencode "description=Sistema de Gerenciamento de Artistas e Álbuns" \
  --data-urlencode "packageName=com.project.artists" \
  --data-urlencode "packaging=jar" \
  --data-urlencode "javaVersion=17" \
  --data-urlencode "dependencies=web,data-jpa,postgresql,validation,security,actuator,flyway,websocket" \
  -o backend.zip 2>/dev/null

if [ ! -f "docker-compose.yml" ]; then
    echo -e "${RED}❌ Execute no diretório raiz do projeto (onde está o docker-compose.yml)${NC}"
    exit 1
fi

echo ""
echo -e "${BLUE}━━━ CRIANDO ESTRUTURA DE DIRETÓRIOS ━━━${NC}"

# Criar TODA a estrutura de uma vez
mkdir -p backend/src/main/java/com/project/artists/{config,controller,dto/request,dto/response,entity,repository,service/impl,security/jwt,security/ratelimit,exception,util}
mkdir -p backend/src/main/resources/db/migration
mkdir -p backend/src/main/resources/static
mkdir -p backend/src/main/resources/templates
mkdir -p backend/src/test/java/com/project/artists/{controller,service,repository}

echo -e "${GREEN}✓ Estrutura de diretórios criada${NC}"
echo ""
echo "Estrutura criada:"
tree -L 4 backend/src/ 2>/dev/null || find backend/src -type d | head -20

# ===========================================
# POM.XML
# ===========================================

echo ""
echo -e "${BLUE}━━━ CRIANDO POM.XML ━━━${NC}"

cat > backend/pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.2</version>
        <relativePath/>
    </parent>
    
    <groupId>com.project</groupId>
    <artifactId>artists</artifactId>
    <version>1.0.0</version>
    <name>artists</name>
    <description>Sistema de Gerenciamento de Artistas e Álbuns</description>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
        
        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
        </dependency>
        
        <!-- MinIO -->
        <dependency>
            <groupId>io.minio</groupId>
            <artifactId>minio</artifactId>
            <version>8.5.7</version>
        </dependency>
        
        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.3</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
EOF

echo -e "${GREEN}✓ pom.xml criado${NC}"

# ===========================================
# APPLICATION.YML
# ===========================================

echo -e "${BLUE}━━━ CRIANDO APPLICATION.YML ━━━${NC}"

cat > backend/src/main/resources/application.yml << 'EOF'
spring:
  application:
    name: artists-api
  
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/artists_db}
    username: ${SPRING_DATASOURCE_USERNAME:postgres}
    password: ${SPRING_DATASOURCE_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

app:
  minio:
    endpoint: ${MINIO_ENDPOINT:http://localhost:9000}
    access-key: ${MINIO_ACCESS_KEY:minioadmin}
    secret-key: ${MINIO_SECRET_KEY:minioadmin}
    bucket-name: album-covers
    presigned-url-expiration: 1800
  
  jwt:
    secret: ${JWT_SECRET:change-this-secret-key-in-production-use-256-bits}
    expiration: 300000
    refresh-expiration: 86400000
  
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:4200}
  
  rate-limit:
    requests: 10
    duration: 60
  
  regional-api:
    url: https://integrador-argus-api.geia.vip/v1/regionais

logging:
  level:
    root: INFO
    com.project.artists: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
EOF

echo -e "${GREEN}✓ application.yml criado${NC}"

# ===========================================
# CLASSE PRINCIPAL
# ===========================================

echo -e "${BLUE}━━━ CRIANDO CLASSE PRINCIPAL ━━━${NC}"

cat > backend/src/main/java/com/project/artists/ArtistsApplication.java << 'EOF'
package com.project.artists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ArtistsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ArtistsApplication.class, args);
    }
}
EOF

echo -e "${GREEN}✓ ArtistsApplication.java criado${NC}"

# ===========================================
# TESTE
# ===========================================

cat > backend/src/test/java/com/project/artists/ArtistsApplicationTests.java << 'EOF'
package com.project.artists;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ArtistsApplicationTests {
    
    @Test
    void contextLoads() {
    }
}
EOF

echo -e "${GREEN}✓ Teste criado${NC}"

# ===========================================
# MIGRATIONS
# ===========================================

echo ""
echo -e "${BLUE}━━━ CRIANDO MIGRATIONS FLYWAY ━━━${NC}"

cat > backend/src/main/resources/db/migration/V1__create_user_table.sql << 'EOF'
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
EOF

echo -e "${GREEN}  ✓ V1__create_user_table.sql${NC}"

cat > backend/src/main/resources/db/migration/V2__create_artist_table.sql << 'EOF'
CREATE TABLE artists (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    bio TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_artists_name ON artists(name);
CREATE INDEX idx_artists_created_at ON artists(created_at);
EOF

echo -e "${GREEN}  ✓ V2__create_artist_table.sql${NC}"

cat > backend/src/main/resources/db/migration/V3__create_album_table.sql << 'EOF'
CREATE TABLE albums (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    artist_id BIGINT NOT NULL,
    release_year INTEGER,
    cover_urls JSONB DEFAULT '[]'::jsonb,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_albums_artist 
        FOREIGN KEY (artist_id) 
        REFERENCES artists(id) 
        ON DELETE CASCADE
);

CREATE INDEX idx_albums_artist_id ON albums(artist_id);
CREATE INDEX idx_albums_title ON albums(title);
CREATE INDEX idx_albums_cover_urls ON albums USING GIN (cover_urls);
EOF

echo -e "${GREEN}  ✓ V3__create_album_table.sql${NC}"

cat > backend/src/main/resources/db/migration/V4__create_regional_table.sql << 'EOF'
CREATE TABLE regional (
    id INTEGER PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_regional_ativo ON regional(ativo);
CREATE INDEX idx_regional_nome ON regional(nome);
EOF

echo -e "${GREEN}  ✓ V4__create_regional_table.sql${NC}"

cat > backend/src/main/resources/db/migration/V5__insert_default_user.sql << 'EOF'
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
EOF

echo -e "${GREEN}  ✓ V5__insert_default_user.sql${NC}"

cat > backend/src/main/resources/db/migration/V6__insert_sample_data.sql << 'EOF'
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
EOF

echo -e "${GREEN}  ✓ V6__insert_sample_data.sql${NC}"

# ===========================================
# ENTIDADES
# ===========================================

echo ""
echo -e "${BLUE}━━━ CRIANDO ENTIDADES JPA ━━━${NC}"

cat > backend/src/main/java/com/project/artists/entity/User.java << 'EOF'
package com.project.artists.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @NotBlank
    @Column(nullable = false)
    private String password;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Construtores
    public User() {}
    
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
EOF

echo -e "${GREEN}  ✓ User.java${NC}"

cat > backend/src/main/java/com/project/artists/entity/Artist.java << 'EOF'
package com.project.artists.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artists")
public class Artist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Album> albums = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Construtores
    public Artist() {}
    
    public Artist(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public List<Album> getAlbums() { return albums; }
    public void setAlbums(List<Album> albums) { this.albums = albums; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
EOF

echo -e "${GREEN}  ✓ Artist.java${NC}"

cat > backend/src/main/java/com/project/artists/entity/Album.java << 'EOF'
package com.project.artists.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "albums")
public class Album {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;
    
    @Column(name = "release_year")
    private Integer releaseYear;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cover_urls", columnDefinition = "jsonb")
    private List<String> coverUrls = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Construtores
    public Album() {}
    
    public Album(String title, Artist artist, Integer releaseYear) {
        this.title = title;
        this.artist = artist;
        this.releaseYear = releaseYear;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Artist getArtist() { return artist; }
    public void setArtist(Artist artist) { this.artist = artist; }
    
    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    
    public List<String> getCoverUrls() { return coverUrls; }
    public void setCoverUrls(List<String> coverUrls) { this.coverUrls = coverUrls; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
EOF

echo -e "${GREEN}  ✓ Album.java${NC}"

cat > backend/src/main/java/com/project/artists/entity/Regional.java << 'EOF'
package com.project.artists.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "regional")
public class Regional {
    
    @Id
    private Integer id;
    
    @NotBlank
    @Column(nullable = false, length = 200)
    private String nome;
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Construtores
    public Regional() {}
    
    public Regional(Integer id, String nome, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.ativo = ativo;
    }
    
    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
EOF

echo -e "${GREEN}  ✓ Regional.java${NC}"

# ===========================================
# REPOSITORIES
# ===========================================

echo ""
echo -e "${BLUE}━━━ CRIANDO REPOSITORIES ━━━${NC}"

cat > backend/src/main/java/com/project/artists/repository/UserRepository.java << 'EOF'
package com.project.artists.repository;

import com.project.artists.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
EOF

echo -e "${GREEN}  ✓ UserRepository.java${NC}"

cat > backend/src/main/java/com/project/artists/repository/ArtistRepository.java << 'EOF'
package com.project.artists.repository;

import com.project.artists.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Page<Artist> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
EOF

echo -e "${GREEN}  ✓ ArtistRepository.java${NC}"

cat > backend/src/main/java/com/project/artists/repository/AlbumRepository.java << 'EOF'
package com.project.artists.repository;

import com.project.artists.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    Page<Album> findByArtistId(Long artistId, Pageable pageable);
}
EOF

echo -e "${GREEN}  ✓ AlbumRepository.java${NC}"

cat > backend/src/main/java/com/project/artists/repository/RegionalRepository.java << 'EOF'
package com.project.artists.repository;

import com.project.artists.entity.Regional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionalRepository extends JpaRepository<Regional, Integer> {
    List<Regional> findByAtivoTrue();
}
EOF

echo -e "${GREEN}  ✓ RegionalRepository.java${NC}"

# ===========================================
# DOCKERFILE
# ===========================================

echo ""
echo -e "${BLUE}━━━ CRIANDO DOCKERFILE ━━━${NC}"

cat > backend/Dockerfile << 'EOF'
# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copiar pom.xml e baixar dependências (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fonte e compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Criar usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expor porta da aplicação
EXPOSE 8080

# Health check para Kubernetes/Docker
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

echo -e "${GREEN}✓ Dockerfile criado${NC}"

cat > backend/.dockerignore << 'EOF'
target/
.mvn/
mvnw
mvnw.cmd
.git/
.gitignore
*.md
.idea/
*.iml
.vscode/
EOF

echo -e "${GREEN}✓ .dockerignore criado${NC}"

# ===========================================
# GITIGNORE BACKEND
# ===========================================

cat > backend/.gitignore << 'EOF'
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

.mvn/
mvnw
mvnw.cmd

.idea/
*.iws
*.iml
*.ipr

.vscode/
EOF

echo -e "${GREEN}✓ .gitignore criado${NC}"

echo ""
echo "╔════════════════════════════════════════╗"
echo -e "║  ${GREEN}✅ SPRINT 1 CONCLUÍDA!${NC}                ║"
echo "╚════════════════════════════════════════╝"
echo ""