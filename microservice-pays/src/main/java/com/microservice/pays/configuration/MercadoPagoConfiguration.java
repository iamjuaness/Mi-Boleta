package com.microservice.pays.configuration;

import com.mercadopago.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfiguration {


    @Value("${security.token.acces-token}")
    private String accesToken;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accesToken);
    }
}
