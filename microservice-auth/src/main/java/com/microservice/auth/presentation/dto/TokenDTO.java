package com.microservice.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenDTO(
        @NotBlank String token
) {
}
