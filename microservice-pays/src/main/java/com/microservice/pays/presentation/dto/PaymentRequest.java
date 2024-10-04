package com.microservice.pays.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record PaymentRequest(
        @NotNull(message = "Purchase order cannot be null")
        PurchaseOrderDTO purchaseOrderDTO,

        @NotBlank(message = "Strategy ID cannot be blank")
        String strategyId
) {
}
