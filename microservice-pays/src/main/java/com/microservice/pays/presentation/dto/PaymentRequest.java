package com.microservice.pays.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(
        @NotNull(message = "Transaction amount cannot be null")
        @Positive(message = "Transaction amount must be positive")
        Double transactionAmount,

        @NotBlank(message = "Card token cannot be blank")
        String cardToken,

        @NotBlank(message = "Description cannot be blank")
        String description,

        @NotNull(message = "Installments cannot be null")
        @Positive(message = "Installments must be positive")
        Integer installments,

        @NotBlank(message = "Payment method ID cannot be blank")
        String paymentMethodId,

        @NotBlank(message = "Payer email cannot be blank")
        String payerEmail
) {
}
