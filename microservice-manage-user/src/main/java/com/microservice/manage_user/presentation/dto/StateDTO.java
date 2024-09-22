package com.microservice.manage_user.presentation.dto;

import com.microservice.manage_user.persistence.model.enums.State;
import jakarta.validation.constraints.NotEmpty;


public record StateDTO(
        @NotEmpty(message = "stateRegister is required")
        State stateRegister,
        State stateUser,
        String idUser

) {
}
