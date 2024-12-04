package com.example.shelfsensebe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BcryptConfig {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(14);
    }
}
