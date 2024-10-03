package com.microservice.pays.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(
        @NotNull(message = "Transaction amount cannot be null")
        @Positive(message = "Transaction amount must be positive")
        Long transactionAmount,

        @NotBlank(message = "Description cannot be blank")
        String description,


        @NotBlank(message = "Payer email cannot be blank")
        String payerEmail
) {
}
