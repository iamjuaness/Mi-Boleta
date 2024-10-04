package com.microservice.pays.service.implementation;

import com.microservice.pays.presentation.dto.PaymentRequest;
import com.microservice.pays.presentation.dto.PaymentResponse;
import com.microservice.pays.service.interfaces.PaymentService;
import com.microservice.pays.service.interfaces.PaymentStrategy;
import com.microservice.pays.utils.PaymentStrategyFactory;
import com.stripe.exception.StripeException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PaymentServiceImpl implements PaymentService {


    PaymentStrategyFactory paymentStrategyFactory;

    public PaymentServiceImpl( PaymentStrategyFactory paymentStrategyFactory) {

        this.paymentStrategyFactory = paymentStrategyFactory;
    }


    @Override
    public PaymentResponse createPayment(PaymentRequest paymentRequest, String strategyId) throws StripeException {

        if(paymentRequest == null && !StringUtils.hasText(strategyId)) {
            throw new IllegalArgumentException("PaymentRequest cannot be null or empty");
        }

        // Seleccionar el proveedor basado en el paymentProvider (no más paymentMethodId interno)
        PaymentStrategy paymentStrategy = paymentStrategyFactory.getStrategy(strategyId);

        if(paymentStrategy != null) {
            return paymentStrategy.processPayment(paymentRequest);

        }else {
            throw new NullPointerException("PaymentStrategy is null");
        }
    }


}
