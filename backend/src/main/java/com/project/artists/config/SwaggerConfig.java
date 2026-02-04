package com.project.artists.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuracao completa do Swagger/OpenAPI 3.0
 */
@Configuration
public class SwaggerConfig {
    
    @Value("${server.port:8085}")
    private String serverPort;
    
    @Value("${app.name:Artists API}")
    private String appName;
    
    @Value("${app.version:1.0.0}")
    private String appVersion;
    
    @Value("${app.description:API para gerenciamento de artistas, albuns e sincronizacao de regionais}")
    private String appDescription;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(apiInfo())
            .servers(apiServers())
            .components(securityComponents())
            .addSecurityItem(securityRequirement());
    }
    
    /**
     * Informacoes da API
     */
    private Info apiInfo() {
        return new Info()
            .title(appName)
            .version(appVersion)
            .description(appDescription + "\n\n" + 
                "### Autenticacao\n" +
                "Esta API utiliza JWT (JSON Web Token) para autenticacao.\n\n" +
                "**Como usar:**\n" +
                "1. Faca login em `/api/v1/auth/login` com suas credenciais\n" +
                "2. Copie o `accessToken` da resposta\n" +
                "3. Clique no botao **Authorize** acima\n" +
                "4. Cole o token no campo `Value` (sem 'Bearer ')\n" +
                "5. Clique em **Authorize** e depois **Close**\n\n" +
                "**Credenciais padrao:**\n" +
                "- Username: `admin`\n" +
                "- Password: `admin123`\n\n" +
                "**Expiracao:**\n" +
                "- Access Token: 5 minutos\n" +
                "- Refresh Token: 7 dias\n\n" +
                "Use `/api/v1/auth/refresh` para renovar o access token.")
            .contact(apiContact())
            .license(apiLicense());
    }
    }
    
    /**
     * Servidores disponiveis
     */
    private List<Server> apiServers() {
        return List.of(
            new Server()
                .url("http://localhost:" + serverPort)
                .description("Servidor de Desenvolvimento (Local)"),
            
            new Server()
                .url("http://localhost:8080")
                .description("Servidor Alternativo (Porta 8080)"),
            
            new Server()
                .url("https://api.artists.com")
                .description("Servidor de Producao (HTTPS)")
        );
    }
    
    /**
     * Componentes de seguranca (JWT)
     */
    private Components securityComponents() {
        return new Components()
            .addSecuritySchemes("bearerAuth", 
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token obtido atraves do endpoint /api/v1/auth/login\n\n" +
                               "**Exemplo de uso:**\n" +
                               "```\n" +
                               "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\n" +
                               "```\n\n" +
                               "**Nota:** Nao e necessario incluir a palavra 'Bearer' ao usar o botao Authorize acima.")
            );
    }
    
    /**
     * Requisito de seguranca global
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("bearerAuth");
    }
}