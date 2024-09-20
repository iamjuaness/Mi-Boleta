package com.microservice.manage_user.presentation.advice;

public class CustomClientException extends RuntimeException {
    public CustomClientException(String message) {
        super(message);
    }
}
