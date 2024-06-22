package com.korotkov.exchange.util;

import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

public class InvalidRequestException extends  RuntimeException {
    public InvalidRequestException(List<ObjectError> errors) {
        super(formatMessage(errors));
    }

    private static String formatMessage(List<ObjectError> errors) {
        return errors.stream()
                .map(error -> String.format("error '%s': %s ", error.getCode(), error.getDefaultMessage()))
                .collect(Collectors.joining(" \n")   );
    }
}
