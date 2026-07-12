package com.commercecore.api.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger configuration.
 *
 * <p>Accessible at:
 * <ul>
 *     <li>Swagger UI: {@code /api/v1/swagger-ui.html}</li>
 *     <li>OpenAPI JSON: {@code /api/v1/api-docs}</li>
 * </ul>
 *
 * <p>Includes a Bearer JWT security scheme so that authenticated endpoints
 * can be tested directly from the Swagger UI (after the Auth module is built).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CommerceCore API")
                        .description("Headless Commerce Platform — Backend API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CommerceCore Team"))
                        .license(new License()
                                .name("Private")
                                .url("https://commercecore.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api/v1")
                                .description("Local Development"),
                        new Server()
                                .url("https://api.commercecore.com/api/v1")
                                .description("Production")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT access token")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"));
    }

}
