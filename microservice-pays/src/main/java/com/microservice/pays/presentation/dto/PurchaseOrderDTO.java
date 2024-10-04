package com.microservice.pays.presentation.dto;

import com.microservice.pays.persistence.enums.State;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderDTO(
        @NotNull(message = "El ID de la orden no puede ser nulo.")
        String idOrder,

        @NotNull(message = "El ID del usuario no puede ser nulo.")
        String idUser,

        @Email(message = "El correo electrónico debe ser válido.")
        @NotNull(message = "El correo electrónico del usuario no puede ser nulo.")
        String emailUser,

        @NotNull(message = "El estado de la orden no puede ser nulo.")
        State stateOrder,

        @NotEmpty(message = "La lista de eventos no puede estar vacía.")
        List<EventDTO> eventDTOList,

        @DecimalMin(value = "0.0", inclusive = false, message = "El monto de la transacción debe ser mayor que cero.")
        @NotNull(message = "El monto de la transacción no puede ser nulo.")
        BigDecimal transactionAmount,

        @NotNull(message = "La fecha de creación no puede ser nula.")
        LocalDateTime creationDate
) {
}
