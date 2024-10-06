package com.microservice.cart.presentation.dto;

import com.microservice.cart.persistence.model.enums.State;
import com.microservice.cart.persistence.model.vo.EventVO;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderDTO(
        @NotNull(message = "idOrder cannot to be null")
        String idOrder,
        @NotNull(message = "idUser cannot to be null")
        String idUser,
        @Email(message = "Email Addres must be valid")
        @NotNull(message = "emailUser cannot be null")
        String emailUser,
        @NotNull(message = "stateOrder cannot be null")
        State stateOrder,
        @NotEmpty(message = "Event's list cannot be empty")
        List<EventVO> cart,
        @DecimalMin(value = "0.0", inclusive = false, message = "the amount of the transaction cannot be 0.0")
        @NotNull(message = "transactionAmount cannot to be null")
        BigDecimal transactionAmount,
        @NotNull(message = "The date of creation cannot be null")
        LocalDateTime creationDate
) {
}
