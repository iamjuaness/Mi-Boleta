package com.microservice.pays.service.implementation;

import com.microservice.pays.persistence.enums.State;
import com.microservice.pays.persistence.vo.EventVO;
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


        try {
            //verificar si la respuesta es null
            if (request == null) {
                throw new NullPointerException("request is null");
            }

//        Extraer el precio unitario de compra en Bigdecimal
            BigDecimal unitValue = request.purchaseOrderDTO().cart().get(0).getUnitValue();

//        convertir el valor totalAmount a long para poder operarlo
            long unitValueLong = unitValue.multiply(BigDecimal.valueOf(100)).longValue();

            //crear los parámetros de la sessión
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

            //crear la sessión con los parámetros
            Session session = Session.create(params.build());

            //verificar que se haya creado la session
            if (session == null) {
                throw new NullPointerException("session is null");
            }

            return new PaymentResponse(session.getUrl(), false);
        } catch (NullPointerException e) {
            return new PaymentResponse(e.getMessage(), true);
        }
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
        try {
            // Recupera la sesión de pago
            Session session = Session.retrieve(paymentId);

            // Verifica si el pago fue exitoso
            if ("paid".equals(session.getPaymentStatus())) {
                System.out.println(session.getPaymentStatus());
                return State.SUCCESS.toString();
            } else {
                return State.PENDING.toString();
            }
        } catch (Exception e) {
            return State.ERROR.toString();
        }
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
