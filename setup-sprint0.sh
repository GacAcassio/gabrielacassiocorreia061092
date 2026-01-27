#!/bin/bash

# Script de Setup - Sprint 0
# Autor: Gabriel Acassio
# Data: Janeiro 2026

set -e  # Parar em caso de erro

echo "======================================"
echo "🚀 INICIANDO SETUP
echo "======================================"
echo ""

# Cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar se está no diretório correto
if [ ! -d ".git" ]; then
    echo -e "${YELLOW}⚠️  Diretório Git não encontrado. Inicializando...${NC}"
    git init
    git checkout -b main
    echo -e "${GREEN}✅ Git inicializado${NC}"
else
    echo -e "${GREEN}✅ Repositório Git já existe${NC}"
fi

echo ""
echo -e "${BLUE}📁 Criando estrutura de diretórios...${NC}"

# Criar estrutura backend
mkdir -p backend/src/main/java/com/project/artists/{config,controller,dto/{request,response},entity,repository,service/{impl},security/{jwt,ratelimit},exception,util}
mkdir -p backend/src/main/resources/db/migration
mkdir -p backend/src/test/java/com/project/artists/{controller,service,integration}

# Criar estrutura frontend
mkdir -p frontend/{src,public}
mkdir -p frontend/src/{components,services,pages,models,store,facade,utils}

echo -e "${GREEN}✅ Estrutura de diretórios criada${NC}"

echo ""
echo -e "${BLUE}📝 Criando arquivos de documentação...${NC}"

# Criar .gitignore
cat > .gitignore << 'EOF'
# Java / Maven
backend/target/
backend/.mvn/
backend/mvnw
backend/mvnw.cmd
*.class
*.jar
*.war
*.ear

# Node.js / NPM
frontend/node_modules/
frontend/dist/
frontend/build/
frontend/.next/
frontend/coverage/
package-lock.json
yarn.lock

# Docker
docker-compose.override.yml
postgres-data/
minio-data/

# IDEs
.idea/
.vscode/
*.iml

# Environment
.env
.env.local
*.env

# OS
.DS_Store
Thumbs.db

# Logs
*.log
EOF

echo -e "${GREEN}✅ .gitignore criado${NC}"

# Criar .env.example
cat > .env.example << 'EOF'
# DATABASE
POSTGRES_DB=artists_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# MINIO
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=minioadmin

# JWT
JWT_SECRET=your-256-bit-secret-key-change-in-production
JWT_EXPIRATION=300000

# API
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200
RATE_LIMIT_REQUESTS=10
EOF


echo ""
echo -e "${BLUE}🧪 Testando Docker...${NC}"

# Verificar se Docker está rodando
if docker ps > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Docker está funcionando${NC}"
    
    echo ""
    echo -e "${YELLOW} Testar a infraestrutura agora? (postgres + minio)${NC}"
    read -p "Digite 'y' para testar: " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${BLUE}Subindo PostgreSQL e MinIO...${NC}"
        docker-compose up -d postgres minio
        echo ""
        echo -e "${GREEN}✅ Serviços iniciados!${NC}"
        echo ""
        echo "Verificando status..."
        sleep 3
        docker-compose ps
    fi
else
    echo -e "${YELLOW}⚠️  Docker não está rodando ou não está instalado${NC}"
    echo "Instale Docker e tente novamente"
fi

echo ""
echo "======================================"
echo -e "${GREEN} SETUP CONFIGURADA COM SUCESSO!${NC}"
echo "======================================"
echo ""
