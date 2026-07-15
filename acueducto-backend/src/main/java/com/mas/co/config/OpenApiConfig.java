package com.mas.co.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para la documentación de la API.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Acueducto Backend API")
                        .description("API REST para el sistema de gestión de acueducto")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("MAS Development Team")
                                .email("desarrollo@mas.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + "/api")
                                .description("Servidor de desarrollo"),
                        new Server()
                                .url("https://api.acueducto.com/api")
                                .description("Servidor de producción")
                ));
    }
}