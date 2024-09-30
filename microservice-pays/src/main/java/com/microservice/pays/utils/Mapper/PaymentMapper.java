package com.microservice.pays.utils.Mapper;

import com.mercadopago.resources.payment.Payment;
import com.microservice.pays.presentation.dto.PaymentResponse;

public class PaymentMapper {

    public PaymentResponse mapPaymentToDTO(Payment payment) {
        return new PaymentResponse(
                String.valueOf(payment.getId()), // Convierte el ID a String
                payment.getStatus(),              // Estado del pago
                payment.getTransactionAmount().doubleValue(), // Monto total de la transacción
                payment.getCurrencyId(),            // Moneda del pago
                payment.getPaymentMethodId(),     // ID del método de pago utilizado
                payment.getPayer().getEmail(),    // Correo electrónico del pagador
                payment.getDescription(),          // Descripción de la transacción
                payment.getDateCreated().toString(), // Fecha y hora de creación
                payment.getDateApproved() != null ? payment.getDateApproved().toString() : null, // Fecha y hora de aprobación, manejar null
                payment.getResponse().getStatusCode().toString()          // Mensaje de error, si corresponde
        );
    }
}
