package com.microservice.pays.utils;

import com.microservice.pays.service.interfaces.PaymentStrategy;

import java.util.Map;

public class PaymentStrategyFactory {

    private final Map<String, PaymentStrategy> strategies;

    public PaymentStrategyFactory(Map<String, PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    public PaymentStrategy getStrategy(String paymentProvider) {
        PaymentStrategy strategy = strategies.get(paymentProvider.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Proveedor de pago no soportado: " + paymentProvider);
        }
        return strategy;
    }
}
