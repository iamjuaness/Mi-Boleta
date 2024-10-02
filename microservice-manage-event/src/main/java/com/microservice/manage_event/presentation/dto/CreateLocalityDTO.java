package com.microservice.manage_event.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

public record CreateLocalityDTO(
        @NotEmpty(message = "idLocality is required") String idLocality,
        @NotEmpty(message = "nameLocality is required") String nameLocality,
        @NotEmpty(message = "capacityLocality is required") int capacityLocality,
        @NotEmpty(message = "priceLocality is required") double priceLocality
) {
}
