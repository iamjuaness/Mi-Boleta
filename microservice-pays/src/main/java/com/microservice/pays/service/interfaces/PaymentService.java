package com.microservice.pays.service.interfaces;

import com.microservice.pays.presentation.dto.PaymentRequest;
import com.microservice.pays.presentation.dto.PaymentResponse;

public interface PaymentService {

    public PaymentResponse createPayment(PaymentRequest paymentRequest);
}
