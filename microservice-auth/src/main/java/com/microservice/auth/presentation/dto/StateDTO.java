package com.microservice.auth.presentation.dto;

import com.microservice.auth.persistence.model.enums.State;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record StateDTO(
        @NotEmpty(message = "state register is required")
        State stateRegister,
        State stateUser,
        String idUser
) {
}
