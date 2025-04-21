package com.kajtekh.jirabackend.exception;

public class IssueNotFoundException extends TabException {
    public IssueNotFoundException(final String message) {
        super(message);
    }
}
