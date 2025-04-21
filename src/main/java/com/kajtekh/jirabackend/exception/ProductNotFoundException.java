package com.kajtekh.jirabackend.exception;

public class ProductNotFoundException extends TabException {
    public ProductNotFoundException(final String message) {
        super(message);
    }
}
