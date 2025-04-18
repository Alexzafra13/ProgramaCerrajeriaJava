package com.gestiontaller.client.config;

import com.gestiontaller.client.api.AuthApiClient;
import com.gestiontaller.client.api.SerieApiClient;
import com.gestiontaller.client.util.FXMLLoaderUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Value("${api.base-url}")
    private String apiBaseUrl;

    private final ApplicationContext applicationContext;

    public ClientConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public FXMLLoaderUtil fxmlLoaderUtil() {
        return new FXMLLoaderUtil(applicationContext);
    }

    @Bean
    public AuthApiClient authApiClient() {
        return new AuthApiClient(apiBaseUrl);
    }

    @Bean
    public SerieApiClient serieApiClient() {
        return new SerieApiClient(apiBaseUrl);
    }
}