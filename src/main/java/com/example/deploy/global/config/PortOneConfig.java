package com.example.deploy.global.config;


import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortOneConfig {

    @Value("${portOne.apiKey}") private String apiKey;

    @Value("${portOne.apiSecretKey}") private String apiSecretKey;

    @Bean
    public IamportClient getIamportClient() {
        return new IamportClient(apiKey, apiSecretKey);
    }
}
