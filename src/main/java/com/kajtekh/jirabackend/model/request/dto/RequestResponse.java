package com.kajtekh.jirabackend.model.request.dto;

import com.kajtekh.jirabackend.model.request.Request;

public record RequestResponse(Long id, String name, String description, String status, String requestType, String accountManager, String openDate) {
    public static RequestResponse fromRequest(Request request) {
        return new RequestResponse(
                request.getId(),
                request.getName(),
                request.getDescription(),
                request.getStatus().name(),
                request.getRequestType().name(),
                request.getAccountManager().getUsername(),
                request.getOpenDate().toString()
        );
    }
}
