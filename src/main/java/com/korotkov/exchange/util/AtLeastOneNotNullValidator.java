package com.korotkov.exchange.util;


import com.korotkov.exchange.dto.request.AuthenticationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneNotNullValidator implements ConstraintValidator<AtLeastOneNotNull, AuthenticationRequest> {

    @Override
    public boolean isValid(AuthenticationRequest value, ConstraintValidatorContext context) {
        return value.getEmail() != null || value.getLogin() != null;
    }


}
