package com.microservice.pays.service.implementation;

import com.microservice.pays.presentation.dto.PaymentRequest;
import com.microservice.pays.presentation.dto.PaymentResponse;
import com.microservice.pays.service.interfaces.PaymentStrategy;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.strategy.Strategy;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class StripeStrategy implements PaymentStrategy {

    @Value("${security.tokenStripe.access-token}")
    private String apiKey;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) throws StripeException {

//      Configurar la api key de stripe
        Stripe.apiKey= apiKey;

        SessionCreateParams params = SessionCreateParams.builder().addPaymentMethodType(
                SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http//:localhost:4200/payment/success")
                .setCancelUrl("http//:localhost:4200/payment/fail")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L).setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(request.transactionAmount()* 100)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Mi Boleta")
                                         .build())
                                .build()
                        )
                        .build()
                )
                .build();

        Session session = Session.create(params);
        System.out.println("Session created: " + session);

        return new PaymentResponse(session.getUrl() );
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
