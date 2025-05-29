package com.kajtekh.jirabackend.controller.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = RegisterValidator.class)
public @interface ValidRegisterRequest {
    String message() default "Invalid registration data";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
