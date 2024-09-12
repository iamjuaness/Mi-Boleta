package com.microservice.auth.persentation.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenDTO(
        @NotBlank String token
) {
}
