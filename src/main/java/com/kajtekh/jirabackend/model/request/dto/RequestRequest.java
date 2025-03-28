package com.kajtekh.jirabackend.model.request.dto;

import com.kajtekh.jirabackend.model.request.RequestType;

public record RequestRequest(String name, String description, RequestType requestType, String accountManager) {
}
