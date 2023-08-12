package com.glofox.classesbooker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${miguel.glofox.url}")
    private String projectUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(projectUrl);
        devServer.setDescription("Server URL in Development environment");

        Contact contact = new Contact();
        contact.setEmail("miguelfferreira93@gmail.com");
        contact.setName("Miguel Ferreira");

        Info info = new Info()
                .title("Gymnasium Classes Booker Management API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage classes and their bookings.");

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}
