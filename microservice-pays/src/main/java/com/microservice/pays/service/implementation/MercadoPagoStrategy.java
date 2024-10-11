package com.microservice.pays.service.implementation;

import com.microservice.pays.presentation.dto.PaymentRequest;
import com.microservice.pays.presentation.dto.PaymentResponse;
import com.microservice.pays.service.interfaces.PaymentStrategy;
import com.microservice.pays.utils.Mapper.PaymentMapper;
import org.springframework.stereotype.Component;



@Component
public class MercadoPagoStrategy implements PaymentStrategy {
    PaymentMapper mapper = new PaymentMapper();


    public PaymentResponse processPayment(PaymentRequest request) {
//
        return null;
    }

    @Override
    public boolean validatePaymentDetails(String paymentDetails) {
        return false;
    }

    @Override
    public boolean refundPayment(String paymentId, double amount) {
        return false;
    }

    @Override
    public String checkPaymentStatus(String paymentId) {
        return "";
    }

    @Override
    public String getPaymentReceipt(String paymentId) {
        return "";
    }

    @Override
    public boolean cancelPayment(String paymentId) {
        return false;
    }
}
