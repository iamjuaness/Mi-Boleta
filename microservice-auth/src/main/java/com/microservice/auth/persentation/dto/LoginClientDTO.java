package com.microservice.auth.persentation.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginClientDTO(
        @NotBlank(message = "emailAddress is required")
        String emailAddress,
        @NotBlank(message = "password is required")
        String password
) {
}
