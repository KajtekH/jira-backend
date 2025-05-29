package com.kajtekh.jirabackend.controller.validator;

import com.kajtekh.jirabackend.model.auth.AuthenticationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LoginValidator implements ConstraintValidator<ValidLoginRequest, AuthenticationRequest> {
    public static final String ERROR_MESSAGE = "Invalid login credentials";

    @Override
    public boolean isValid(final AuthenticationRequest authenticationRequest, final ConstraintValidatorContext context) {
        if (authenticationRequest == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ERROR_MESSAGE).addConstraintViolation();
            return false;
        }

        final var email = authenticationRequest.email();
        final var password = authenticationRequest.password();

        final var isValid = email != null && !email.isBlank() && password != null && !password.isBlank();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ERROR_MESSAGE).addConstraintViolation();
        }

        return isValid;
    }
}
