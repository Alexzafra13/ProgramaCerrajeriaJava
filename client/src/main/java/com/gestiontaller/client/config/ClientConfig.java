package com.gestiontaller.client.config;

import com.gestiontaller.client.api.AuthApiClient;
import com.gestiontaller.client.api.ConfiguracionSerieApiClient;
import com.gestiontaller.client.api.SerieApiClient;
import com.gestiontaller.client.util.FXMLLoaderUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class ClientConfig {

    @Value("${api.base-url:http://localhost:8080}")
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
        System.out.println("Creando AuthApiClient con URL base: " + apiBaseUrl);
        return new AuthApiClient(apiBaseUrl);
    }

    @Bean
    public SerieApiClient serieApiClient() {
        System.out.println("Creando SerieApiClient con URL base: " + apiBaseUrl);
        return new SerieApiClient(apiBaseUrl);
    }

    @Bean
    public ConfiguracionSerieApiClient configuracionSerieApiClient() {
        System.out.println("Creando ConfiguracionSerieApiClient con URL base: " + apiBaseUrl);
        return new ConfiguracionSerieApiClient(apiBaseUrl);
    }
}