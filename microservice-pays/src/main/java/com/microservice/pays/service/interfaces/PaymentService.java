package com.microservice.pays.service.interfaces;

import com.microservice.pays.presentation.dto.PaymentRequest;
import com.microservice.pays.presentation.dto.PaymentResponse;
import com.stripe.exception.StripeException;

public interface PaymentService {


    public PaymentResponse createPayment(PaymentRequest paymentRequest , String strategyId) throws StripeException;
}
