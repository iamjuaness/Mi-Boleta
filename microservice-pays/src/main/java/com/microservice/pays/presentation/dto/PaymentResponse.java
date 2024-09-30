package com.microservice.pays.presentation.dto;

public record PaymentResponse(

        String id,                 // ID del pago en Mercado Pago
        String status,             // Estado del pago (ej. approved, pending, rejected)
        Double transactionAmount,   // Monto total de la transacción
        String currency,           // Moneda en la que se realizó el pago
        String paymentMethodId,    // ID del método de pago utilizado
        String payerEmail,         // Correo electrónico del pagador
        String description,        // Descripción de la transacción
        String dateCreated,        // Fecha y hora de creación del pago
        String dateApproved,       // Fecha y hora de aprobación del pago (si corresponde)
        String errorMessage        // Mensaje de error, si hay algún problema
) {
}
