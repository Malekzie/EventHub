package com.eventhub.eventhub_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eventHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EventHub API")
                        .description("Event Management Platform API - Discover events, manage registrations, and more.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EventHub Team")));
    }
}
