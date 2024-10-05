package com.microservice.pays.service.implementation;

import com.microservice.pays.persistence.enums.State;
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
    public PaymentResponse createPayment(PaymentRequest paymentRequest) throws StripeException {

        if(paymentRequest == null ) {
            throw new IllegalArgumentException("PaymentRequest cannot be null or empty");
        }

        // Seleccionar el proveedor basado en el paymentProvider (no más paymentMethodId interno)
        PaymentStrategy paymentStrategy = paymentStrategyFactory.getStrategy(paymentRequest.strategyId());

        if(paymentStrategy != null) {
            return paymentStrategy.processPayment(paymentRequest);

        }else {
            throw new NullPointerException("PaymentStrategy is null");
        }
    }

    @Override
    public void setSateOrder(String idSession, String strategyId) {

        //verificar que no sea vacio
        if(!StringUtils.hasText(idSession) || !StringUtils.hasText(strategyId)) {
            throw new IllegalArgumentException("IdSession and strategyId cannot be null");
        }
        //traer la estrategia
        PaymentStrategy paymentStrategy = paymentStrategyFactory.getStrategy(strategyId);
        if(paymentStrategy != null) {
            throw new NullPointerException("PaymentStrategy is null");
        }

        String statePayment = paymentStrategy.checkPaymentStatus(idSession);
        if (statePayment.equals(State.SUCCESS.toString())){
//            actualizar  por medio de un endpoint en el microservicio shopping
        }
//


    }


}
