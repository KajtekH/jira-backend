package com.kajtekh.jirabackend.exception;

public class IssueTypeNotFoundException extends TabException {
    public IssueTypeNotFoundException(final String message) {
        super(message);
    }
}
