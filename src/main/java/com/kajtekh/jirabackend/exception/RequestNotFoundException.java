package com.kajtekh.jirabackend.exception;

public class RequestNotFoundException extends TabException {
    public RequestNotFoundException(final String message) {
        super(message);
    }
}
