package com.microservice.cart.presentation.advice;

public class CustomClientException extends RuntimeException {
    public CustomClientException(String message) {
        super(message);
    }
}
