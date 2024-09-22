package com.microservice.auth.presentation.dto;

import com.microservice.auth.persistence.model.enums.State;
import jakarta.validation.constraints.NotBlank;

public record ClientDTO(
        @NotBlank(message = "Id user is required")
        String idUser,
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "role is required")
        String role,
        @NotBlank(message = "email is required")
        String emailAddress,
        @NotBlank(message = "state is required")
        State state
) {
}
