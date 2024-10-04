package com.microservice.pays.service.implementation;

import com.microservice.pays.persistence.vo.EventVO;
import com.microservice.pays.presentation.dto.EventDTO;
import com.microservice.pays.presentation.dto.PaymentRequest;
import com.microservice.pays.presentation.dto.PaymentResponse;
import com.microservice.pays.service.interfaces.PaymentStrategy;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class StripeStrategy implements PaymentStrategy {

    @Override
    public PaymentResponse processPayment(PaymentRequest request) throws StripeException {
//        Extraer el total de la orden de compra
        System.out.println(request);
        if (request == null) System.out.println("request is null");
        if (request.purchaseOrderDTO() == null) System.out.println("purchase is null");

        BigDecimal unitValue = request.purchaseOrderDTO().cart().get(0).getUnitValue();

//        convertir el valor totalAmount a long para poder operarlo

        long unitValueLong = unitValue.multiply(BigDecimal.valueOf(100)).longValue();

        SessionCreateParams.Builder params = SessionCreateParams.builder().addPaymentMethodType(
                        SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/payment/success")
                .setCancelUrl("http://localhost:4200/payment/fail");

        // Agregar cada producto al SessionCreateParams
        for (EventVO event : request.purchaseOrderDTO().cart()) {
            params.addLineItem(SessionCreateParams.LineItem.builder()
                    .setQuantity((long) event.getQuantity())
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("usd")
                            .setUnitAmount(unitValueLong)
                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(event.getNameLocality())
                                    .build())
                            .build())
                    .build());
        }

        Session session = Session.create(params.build());
        System.out.println("Session created: " + session);

        return new PaymentResponse(session.getUrl());
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
