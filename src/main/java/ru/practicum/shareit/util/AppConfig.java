package ru.practicum.shareit.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UriBuilderUtil uriBuilderUtil() {
        return new UriBuilderUtil("localhost", "8080", "http");
    }
}