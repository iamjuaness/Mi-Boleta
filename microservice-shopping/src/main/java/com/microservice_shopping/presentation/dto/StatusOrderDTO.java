package com.microservice_shopping.presentation.dto;

import com.microservice_shopping.persistence.model.enums.State;
import jakarta.validation.constraints.NotBlank;

public record StatusOrderDTO(
        @NotBlank(message = "id order no puede ser nulo")
        String idPurchaseOrder,
        @NotBlank(message = "stateOrder no puede ser vacío ni nulo")
        State stateOrder
) {
}
