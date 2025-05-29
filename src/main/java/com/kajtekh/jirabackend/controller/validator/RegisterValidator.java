package com.kajtekh.jirabackend.controller.validator;

import com.kajtekh.jirabackend.model.auth.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RegisterValidator implements ConstraintValidator<ValidRegisterRequest, RegisterRequest> {
    public static final String ERROR_MESSAGE = "Invalid registration details";

    @Override
    public boolean isValid(final RegisterRequest registerRequest, final ConstraintValidatorContext context) {
        if (registerRequest == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ERROR_MESSAGE).addConstraintViolation();
            return false;
        }

        final var username = registerRequest.username();
        final var password = registerRequest.password();
        final var email = registerRequest.email();
        final var firstName = registerRequest.firstName();
        final var lastName = registerRequest.lastName();

        final var isValid = username != null && !username.isBlank() &&
                password != null && !password.isBlank() &&
                email != null && !email.isBlank() &&
                firstName != null && !firstName.isBlank() &&
                lastName != null && !lastName.isBlank();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ERROR_MESSAGE).addConstraintViolation();
        }

        return isValid;
    }
}
