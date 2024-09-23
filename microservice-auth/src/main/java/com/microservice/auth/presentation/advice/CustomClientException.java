package com.microservice.auth.presentation.advice;

public class CustomClientException extends RuntimeException {
    public CustomClientException(String message) {
        super(message);
    }
}
