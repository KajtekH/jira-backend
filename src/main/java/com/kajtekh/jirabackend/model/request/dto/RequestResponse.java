package com.kajtekh.jirabackend.model.request.dto;

public record RequestResponse(Long id, String name, String description, String status, String requestType, String accountManager) {
    public static RequestResponse fromRequest(com.kajtekh.jirabackend.model.request.Request request) {
        return new RequestResponse(
                request.getId(),
                request.getName(),
                request.getDescription(),
                request.getStatus().name(),
                request.getRequestType().name(),
                request.getAccountManager().getUsername()
        );
    }
}
