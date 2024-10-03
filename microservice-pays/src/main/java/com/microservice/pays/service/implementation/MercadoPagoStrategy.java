package com.microservice.pays.service.implementation;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.microservice.pays.presentation.dto.PaymentRequest;
import com.microservice.pays.presentation.dto.PaymentResponse;
import com.microservice.pays.service.interfaces.PaymentStrategy;
import com.microservice.pays.utils.Mapper.PaymentMapper;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;

@Component
public class MercadoPagoStrategy implements PaymentStrategy {
    PaymentMapper mapper = new PaymentMapper();


    public PaymentResponse processPayment(PaymentRequest request) {
//        // Crear el cliente de Mercado Pago
//        PaymentClient client = new PaymentClient();
//
//        // Crear la solicitud de pago para Mercado Pago
//        PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
//                .transactionAmount(new BigDecimal(request.transactionAmount()))
//                .token(request.cardToken())
//                .description(request.description())
//                .installments(request.installments())
//                .paymentMethodId(request.paymentMethodId())
//                .payer(PaymentPayerRequest.builder().email(request.payerEmail()).build())
//                .build();
//
//        try {
//            // Realizar el pago y obtener el objeto Payment
//            Payment payment = client.create(createRequest);
//            System.out.println(payment);
//
//            // Verificar el estado del pago
//            String status = payment.getStatus();
//            switch (status.toLowerCase()) {
//                case "approved":
//                    // El pago fue aprobado
//                    return mapper.mapPaymentToDTO(payment);
//                case "pending":
//                    // El pago está pendiente
//                    return new PaymentResponse(
//                            payment.getId().toString(),
//                            "pending",
//                            payment.getTransactionAmount().doubleValue(),
//                            payment.getCurrencyId(),
//                            payment.getPaymentMethodId(),
//                            payment.getPayer().getEmail(),
//                            payment.getDescription(),
//                            payment.getDateCreated().toString(),
//                            null,
//                            "El pago está pendiente de confirmación"
//                    );
//                case "rejected":
//                    // El pago fue rechazado
//                    return new PaymentResponse(
//                            payment.getId().toString(),
//                            "rejected",
//                            payment.getTransactionAmount().doubleValue(),
//                            payment.getCurrencyId(),
//                            payment.getPaymentMethodId(),
//                            payment.getPayer().getEmail(),
//                            payment.getDescription(),
//                            payment.getDateCreated().toString(),
//                            null,
//                            "El pago fue rechazado: " + payment.getStatusDetail()
//                    );
//                default:
//                    // Estado no reconocido
//                    return new PaymentResponse(
//                            payment.getId().toString(),
//                            "unknown",
//                            payment.getTransactionAmount().doubleValue(),
//                            payment.getCurrencyId(),
//                            payment.getPaymentMethodId(),
//                            payment.getPayer().getEmail(),
//                            payment.getDescription(),
//                            payment.getDateCreated().toString(),
//                            null,
//                            "Estado del pago no reconocido: " + status
//                    );
//            }
//
//        } catch (MPApiException ex) {
//            System.out.printf(
//                    "MercadoPago Error. Status: %s, Content: %s%n",
//                    ex.getApiResponse().getStatusCode(), ex.getApiResponse().getContent());
//            return new PaymentResponse(null, "failed", null, null, null, null, null, null, null, ex.getMessage());
//        } catch (MPException ex) {
//            ex.printStackTrace();
//            return new PaymentResponse(null, "error", null, null, null, null, null, null, null, ex.getMessage());
//        }
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
