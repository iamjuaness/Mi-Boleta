package com.microservice.manage_user.presentation.dto;

import java.math.BigDecimal;

public record AddToCartDTO(
        String idEventVO,
        String idEvent,
        String idLocality,
        BigDecimal unitValue,
        Integer quantity
) {
}
