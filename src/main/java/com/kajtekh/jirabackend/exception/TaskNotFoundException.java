package com.kajtekh.jirabackend.exception;

public class TaskNotFoundException extends TabException {
    public TaskNotFoundException(final String message) {
        super(message);
    }
}
