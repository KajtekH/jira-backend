package com.kajtekh.jirabackend.exception;

public class UserNotFoundException extends TabException {
    public UserNotFoundException(final String message) {
        super(message);
    }
}
