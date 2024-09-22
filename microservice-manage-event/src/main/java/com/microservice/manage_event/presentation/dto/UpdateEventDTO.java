package com.microservice.manage_event.presentation.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record UpdateEventDTO(
        @NotBlank(message = "nameEvent is required") String nameEvent,
        @NotBlank(message = "startDate is required") LocalDateTime startDate,
        @NotBlank(message = "endDate is required") LocalDateTime endDate,
        @NotBlank(message = "address is required") String address
) {
}
