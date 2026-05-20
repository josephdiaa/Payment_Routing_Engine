package com.example.payment_routing_engine.common.exception;

public class BusinessValidationException extends RuntimeException{
    public BusinessValidationException(String message) {
        super(message);
    }
}
