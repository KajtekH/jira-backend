package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.exception.RequestNotFoundException;
import com.kajtekh.jirabackend.exception.RequestTypeNotFoundException;
import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.product.Product;
import com.kajtekh.jirabackend.model.request.Request;
import com.kajtekh.jirabackend.model.request.RequestType;
import com.kajtekh.jirabackend.model.request.dto.RequestRequest;
import com.kajtekh.jirabackend.model.request.dto.RequestResponse;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.kajtekh.jirabackend.model.Status.ABANDONED;
import static com.kajtekh.jirabackend.model.Status.CLOSED;
import static com.kajtekh.jirabackend.model.Status.IN_PROGRESS;
import static com.kajtekh.jirabackend.model.Status.OPEN;
import static java.time.temporal.ChronoUnit.MINUTES;


@Service
public class RequestService {
    private static final Logger LOG = LoggerFactory.getLogger(RequestService.class);

    private final RequestRepository requestRepository;

    public RequestService(final RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Transactional(readOnly = true)
    public List<RequestResponse> getAllRequests(final Long productId) {
        return requestRepository.findAllByProductId(productId).stream()
                .sorted((request1, request2) -> {
                    final List<Status> order = List.of(OPEN, IN_PROGRESS, CLOSED, ABANDONED);
                    return Integer.compare(order.indexOf(request1.getStatus()), order.indexOf(request2.getStatus()));
                })
                .map(RequestResponse::fromRequest)
                .toList();
    }

    @Transactional(readOnly = true)
    public Request getRequestById(final Long id) {
        return requestRepository.findById(id).orElseThrow(() -> {
            LOG.warn("Request with ID '{}' not found", id);
            return new RequestNotFoundException("Request not found");
        });
    }

    @Transactional
    public Request addRequest(final RequestRequest requestRequest, final User accountManager, final Product product) {
        final var request = new Request();
        request.setName(requestRequest.name());
        request.setDescription(requestRequest.description());
        request.setAccountManager(accountManager);
        request.setStatus(OPEN);
        request.setProduct(product);
        request.setOpenDate(LocalDateTime.now().truncatedTo(MINUTES));
        try {
            request.setRequestType(RequestType.valueOf(requestRequest.requestType().toUpperCase()));
        } catch (final IllegalArgumentException e) {
            LOG.warn("Invalid request type: '{}'. Defaulting to 'OTHER'", requestRequest.requestType());
           throw new RequestTypeNotFoundException("Invalid request type: " + requestRequest.requestType());
        }
        requestRepository.save(request);
        LOG.info("Request added successfully: '{}'", request);
        return request;
    }

    @Transactional
    public Request updateStatus(final Long id, final Status status, final String result) {
        final var request = requestRepository.findById(id).orElseThrow(() -> {
            LOG.warn("Request with ID: '{}' not found ", id);
            return new RequestNotFoundException("Request not found");
        });
        final var oldStatus = request.getStatus();
        request.setStatus(status);
        request.setResult(result);
        requestRepository.save(request);
        LOG.info("Status for request with ID  '{}' updated from '{}' to '{}'", id, oldStatus, status);
        LOG.debug("Request details: {}", request);
        return request;
    }
}

