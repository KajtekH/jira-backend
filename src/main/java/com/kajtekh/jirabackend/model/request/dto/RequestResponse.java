package com.kajtekh.jirabackend.model.request.dto;

import com.kajtekh.jirabackend.model.request.Request;

import java.io.Serializable;
import java.util.Optional;

public record RequestResponse(Long id, String name, String description, String status, String requestType, String accountManager, String openDate, String result) implements Serializable {
    public static RequestResponse fromRequest(final Request request) {
        return new RequestResponse(
                request.getId(),
                request.getName(),
                request.getDescription(),
                request.getStatus().name(),
                request.getRequestType().name(),
                request.getAccountManager().getUsername(),
                request.getOpenDate().toString(),
                Optional.ofNullable(request.getResult()).orElse("")
        );
    }
}
