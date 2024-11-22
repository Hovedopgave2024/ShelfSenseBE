package com.example.shelfsensebe.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebApiConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl("https://api.mouser.com/api/v2").build();
    }

}
