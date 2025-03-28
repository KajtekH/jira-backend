package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.request.Request;
import com.kajtekh.jirabackend.model.request.dto.RequestRequest;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.RequestRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.kajtekh.jirabackend.model.Status.ABANDONED;
import static com.kajtekh.jirabackend.model.Status.CLOSED;
import static com.kajtekh.jirabackend.model.Status.OPEN;
import static java.util.stream.Collectors.toList;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }


    public List<Request> getAllRequests() {
        return requestRepository.findAll().stream()
                .sorted((request1, request2) -> {
            List<Status> order = List.of(OPEN, CLOSED, ABANDONED);
            return Integer.compare(order.indexOf(request1.getStatus()), order.indexOf(request2.getStatus()));
        }).collect(toList());
    }

    public Request getRequestById(Long id) {
        return requestRepository.findById(id).orElse(null);
    }

    public Request addRequest(RequestRequest requestRequest, User accountManager) {
        final var request = new Request();
        request.setName(requestRequest.name());
        request.setDescription(requestRequest.description());
        request.setRequestType(requestRequest.requestType());
        request.setAccountManager(accountManager);
        request.setStatus(OPEN);
        return requestRepository.save(request);
    }

    public Request updateStatus(Long id, Status status) {
        final var request = requestRepository.findById(id).orElseThrow();
        request.setStatus(status);
        return requestRepository.save(request);
    }
}

