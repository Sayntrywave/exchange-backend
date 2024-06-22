package com.korotkov.exchange.util;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}