package com.kajtekh.jirabackend.exception;

public class InsufficientRoleException extends TabException {
    public InsufficientRoleException(final String message) {
        super(message);
    }
}
