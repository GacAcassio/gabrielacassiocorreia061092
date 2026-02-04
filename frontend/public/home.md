
# Requisitos do Projeto (Full Stack Sênior - Java + React/Angular)
## Dados do Candidato

- **Nome**: Gabriel Acassio Correia
-**Vaga**: ANALISTA DE TI - PERFIL PROFISSIONAL/ESPECIALIDADE - Engenheiro da Computação - Sênio
- **Projeto**: Full Stack Sênior - Java + Angular/React (Anexo II - c)
- **Data**: Janeiro/2026

---


## Documentação / Arquitetura
- Propor a **estrutura de dados das tabelas** de forma coerente ✅ 
- Documentar decisões e arquitetura no **README.md** ✅ 

---

## Pré-requisitos Gerais
- Backend em **Java** usando **Spring Boot ou Quarkus** ✅ 
- Frontend em **React ou Angular** ✅ 
- Entrega via **docker-compose** com:
  - API ✅ 
  - MinIO ✅ 
  - Banco de Dados ✅ 
  - Frontend ✅ 

---

# Requisitos do Back End

## Segurança e Autenticação
- Bloquear acesso aos endpoints a partir de **domínios distintos do domínio do serviço** (restrição de origem / CORS) ✅ 
- Autenticação **JWT**
  - Token com expiração de **5 minutos** ✅ 
  - Possibilidade de **renovação** do token ✅ 

## API e Funcionalidades
- Implementar pelo menos os verbos:
  - `POST` ✅ 
  - `PUT`  ✅ 
  - `GET`  ✅ 
- Paginação na consulta dos **álbuns** ✅ 
- Expor relação de **artistas ↔ álbuns**, permitindo **consultas parametrizadas** ✅ 
- Consultas por **nome do artista** ✅ 
  - Ordenação alfabética **ASC** e **DESC** ✅ 

## Upload e Imagens
- Upload de **uma ou mais imagens** da capa do álbum
- Armazenar imagens no **MinIO (API S3)** ✅ 
- Recuperar imagens via **links pré-assinados (presigned URL)** 
  - Expiração do link: **30 minutos** ✅ 

## Boas práticas e Infra
- Versionar endpoints (ex: `/v1/...`) ✅ 
- Usar **Flyway Migrations** 
  - Criar tabelas ✅ 
  - Popular tabelas ✅ 
- Documentar endpoints com **OpenAPI/Swagger** ✅ 

---

# Requisitos do Front End

## Geral
- Consumir a API e prover uma **interface intuitiva** ✅ 
- Utilizar **TypeScript** ✅ 
- Layout **responsivo** ✅ 
- Se usar framework CSS, priorizar **Tailwind** ✅ 
- Boas práticas:
  - Modularização ✅ 
  - Componentização ✅ 
  - Services ✅ 
- **Lazy Loading Routes** para módulos distintos ✅ 
- Implementar **paginação** ou **scroll infinito** ✅ 

## Tela Inicial — Listagem de Artistas
- Consultar e exibir lista de artistas ✅ 
- Exibir em **cards** ou **tabela responsiva** ✅
    - Mostrar: **nome** e **nº de álbuns** ✅ 
- Campo de busca por nome ✅ 
- Ordenação **ASC/DESC** ✅ 
- Paginação ✅ 

## Tela de Detalhamento do Artista
- Ao clicar em um artista, exibir os **álbuns associados** ✅ 
- Exibir informações completas, incluindo **capas** ✅ 
- Se não houver álbuns, exibir **mensagem informativa** ✅ 

## Tela de Cadastro/Edição ✅ 
- Formulário para inserir artistas ✅ 
- Formulário para adicionar álbuns a um artista ✅ 
- Edição de registros ✅ 
- Upload de capas usando endpoints integrados com **MinIO** ✅ 

## Autenticação no Front
- Acesso ao front exige **login** ✅ 
- Implementar autenticação JWT consumindo endpoint da API ✅ 
- Gerenciar:
  - Expiração do token ✅ 
  - Renovação do token ✅ 

---

# Requisitos Extras (Apenas para Sênior)

## API
- Health Checks ✅ 
- Liveness / Readiness ✅ 
- Testes unitários ✅ 
- WebSocket  
  - Enviar notificação a cada **novo álbum cadastrado** ✅ 
- Rate limit
  - Máximo **10 requisições por minuto por usuário** 

## Front End
- Padrão **Facade** ✅ 
- Gestão de estado com **BehaviorSubject** ✅ 
- Exibir notificações recebidas via **WebSocket** ✅ 

---

# Integração Externa — Regionais da Polícia Civil (API)

## Endpoint
- Consumir: `https://integrador-argus-api.geia.vip/v1/regionais` ✅ 

## Requisitos de Persistência
- Importar estrutura para uma tabela interna ✅ 
- Criar tabela: `regional` ✅ 
  - `id integer` ✅ 
  - `nome varchar(200)` 
  - `ativo boolean` ✅ 

## Requisitos de Sincronização (menor complexidade algorítmica possível)
1. Se existir no endpoint e não existir local → **inserir** ✅ 
2. Se existir local e não existir no endpoint → **inativar**  ✅ 
3. Se algum atributo mudar → **inativar o registro anterior** e **criar um novo** com a nova denominação ✅ 
