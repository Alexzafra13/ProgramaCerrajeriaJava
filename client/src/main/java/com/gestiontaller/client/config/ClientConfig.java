package com.gestiontaller.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gestiontaller.client.api.*;
import com.gestiontaller.client.util.FXMLLoaderUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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

    @Bean
    public ComponenteSerieApiClient componenteSerieApiClient() {
        System.out.println("Creando ComponenteSerieApiClient con URL base: " + apiBaseUrl);
        return new ComponenteSerieApiClient(apiBaseUrl);
    }

    @Bean
    public ProductoApiClient productoApiClient() {
        System.out.println("Creando ProductoApiClient con URL base: " + apiBaseUrl);
        return new ProductoApiClient(apiBaseUrl);
    }

    @Bean
    public ClienteApiClient clienteApiClient() {
        System.out.println("Creando ClienteApiClient con URL base: " + apiBaseUrl);
        return new ClienteApiClient(apiBaseUrl);
    }

    @Bean
    public PresupuestoApiClient presupuestoApiClient() {
        System.out.println("Creando PresupuestoApiClient con URL base: " + apiBaseUrl);
        return new PresupuestoApiClient(apiBaseUrl);
    }

    @Bean
    public TrabajoApiClient trabajoApiClient() {
        System.out.println("Creando TrabajoApiClient con URL base: " + apiBaseUrl);
        return new TrabajoApiClient(apiBaseUrl);
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Configurar el objeto mapper para manejar fechas de Java 8
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Configurar el mensaje converter
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        // Reemplazar el converter predeterminado
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(converter);
        converters.addAll(restTemplate.getMessageConverters());
        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }
}