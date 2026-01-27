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

## 📚 Documentação

- [Arquitetura](./ARCHITECTURE_DECISIONS.md)
- [Guia Rápido](./QUICK_START.md)

---

## ✅ Status do Projeto

### Sprint 0 - Setup ✅
- [Sprint 0](./SPRINT_0.md)

