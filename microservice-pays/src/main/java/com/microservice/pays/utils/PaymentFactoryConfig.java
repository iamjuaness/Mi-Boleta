package com.microservice.pays.utils;

import com.microservice.pays.service.implementation.MercadoPagoStrategy;
import com.microservice.pays.service.implementation.StripeStrategy;
import com.microservice.pays.service.interfaces.PaymentStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaymentFactoryConfig {

    @Bean
    public PaymentStrategyFactory paymentStrategyFactory() {
        Map<String, PaymentStrategy> strategies = new HashMap<>();
        strategies.put("mercadopago", new MercadoPagoStrategy());
        strategies.put("stripe", new StripeStrategy());
        // Agregar más proveedores según sea necesario
        return new PaymentStrategyFactory(strategies);
    }
}
