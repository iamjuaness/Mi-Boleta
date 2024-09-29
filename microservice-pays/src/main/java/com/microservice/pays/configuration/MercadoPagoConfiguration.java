package com.microservice.pays.configuration;

import com.mercadopago.*;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfiguration {


    private String accesToken;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accesToken);
    }
}
