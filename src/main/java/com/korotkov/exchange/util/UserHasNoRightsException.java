package com.korotkov.exchange.util;

public class UserHasNoRightsException extends RuntimeException {
    public UserHasNoRightsException(String message) {
        super(message);
    }
}
