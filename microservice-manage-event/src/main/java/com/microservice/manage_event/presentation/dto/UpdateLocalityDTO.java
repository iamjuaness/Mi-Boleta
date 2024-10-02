package com.microservice.manage_event.presentation.dto;


public record UpdateLocalityDTO(
        String nameLocality,
        int capacityLocality,
        double priceLocality
) {
}
