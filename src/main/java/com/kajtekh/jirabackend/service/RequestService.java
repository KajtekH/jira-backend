package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.product.Product;
import com.kajtekh.jirabackend.model.request.Request;
import com.kajtekh.jirabackend.model.request.dto.RequestRequest;
import com.kajtekh.jirabackend.model.request.dto.RequestResponse;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.RequestRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.kajtekh.jirabackend.model.Status.ABANDONED;
import static com.kajtekh.jirabackend.model.Status.CLOSED;
import static com.kajtekh.jirabackend.model.Status.IN_PROGRESS;
import static com.kajtekh.jirabackend.model.Status.OPEN;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.stream.Collectors.toList;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    public RequestService(final RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "requests", key = "#productId")
    public List<RequestResponse> getAllRequests(final Long productId) {
        return requestRepository.findAllByProductId(productId).stream()
                .sorted((request1, request2) -> {
            final List<Status> order = List.of(OPEN,IN_PROGRESS, CLOSED, ABANDONED);
            return Integer.compare(order.indexOf(request1.getStatus()), order.indexOf(request2.getStatus()));
        })
                .map(RequestResponse::fromRequest)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public Request getRequestById(final Long id) {
        return requestRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public RequestResponse getRequestResponseById(final Long id) {
        return requestRepository.findById(id)
                .map(RequestResponse::fromRequest)
                .orElse(null);
    }

    @Transactional
    public Request addRequest(final RequestRequest requestRequest, final User accountManager, final Product product) {
        final var request = new Request();
        request.setName(requestRequest.name());
        request.setDescription(requestRequest.description());
        request.setRequestType(requestRequest.requestType());
        request.setAccountManager(accountManager);
        request.setStatus(OPEN);
        request.setProduct(product);
        request.setOpenDate(LocalDateTime.now().truncatedTo(MINUTES));
        return requestRepository.save(request);
    }

    @Transactional
    public Request updateStatus(final Long id, final Status status) {
        final var request = requestRepository.findById(id).orElseThrow();
        request.setStatus(status);
        return requestRepository.save(request);
    }
}

