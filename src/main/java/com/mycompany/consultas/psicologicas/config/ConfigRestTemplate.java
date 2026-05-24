package com.mycompany.consultas.psicologicas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigRestTemplate {

    @Bean
    public RestTemplate restTemplate() {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        // 10 segundos para conectar
        factory.setConnectTimeout(10000);

        // 5 minutos para esperar respuesta IA
        factory.setReadTimeout(300000);

        return new RestTemplate(factory);
    }
}