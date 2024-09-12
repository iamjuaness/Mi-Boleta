package com.microservice.auth.presentation.dto.HTTP;

public record MessageAuthDTO <T> (
        boolean error,
        T response
) {
}
