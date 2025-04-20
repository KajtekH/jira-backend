package com.kajtekh.jirabackend.facade;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.request.dto.RequestRequest;
import com.kajtekh.jirabackend.model.request.dto.RequestResponse;
import com.kajtekh.jirabackend.service.ProductService;
import com.kajtekh.jirabackend.service.RequestService;
import com.kajtekh.jirabackend.service.UpdateNotificationService;
import com.kajtekh.jirabackend.service.UserService;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kajtekh.jirabackend.model.Status.CLOSED;

@Service
public class RequestFacade {

    private final RequestService requestService;
    private final UserService userService;
    private final ProductService productService;
    private final UpdateNotificationService updateNotificationService;
    private final Cache cache;

    public RequestFacade(final RequestService requestService, final UserService userService, final ProductService productService,
                         final UpdateNotificationService updateNotificationService, final Cache cache) {
        this.requestService = requestService;
        this.userService = userService;
        this.productService = productService;
        this.updateNotificationService = updateNotificationService;
        this.cache = cache;
    }

    @Cacheable(value = "data", key = "'requests' + #productId")
    public List<RequestResponse> getAllRequests(final Long productId) {
        return requestService.getAllRequests(productId);
    }

    @Cacheable(value = "data", key = "'request' + #id")
    public RequestResponse getRequestById(final Long id) {
        return RequestResponse.fromRequest(requestService.getRequestById(id));
    }

    public RequestResponse addRequest(final RequestRequest requestRequest, final Long productId) {
        final var product = productService.getProductById(productId);
        final var accountManager = userService.getUserByUsername(requestRequest.accountManager());
        final var request = requestService.addRequest(requestRequest, accountManager, product);
        cache.evictIfPresent("requests" + productId);
        cache.put("request" + request.getId(), RequestResponse.fromRequest(request));
        updateNotificationService.notifyRequestListUpdate(productId);
        return RequestResponse.fromRequest(request);
    }

  public RequestResponse updateStatus(final Long id, final Status status) {
        final var request = requestService.updateStatus(id, status);
      if (status == CLOSED) {
          productService.bumpVersion(request.getProduct(), request.getRequestType());
          cache.evictIfPresent("products");
      }
        cache.evictIfPresent("requests" + request.getProduct().getId());
        cache.put("request" + request.getId(), RequestResponse.fromRequest(request));
        updateNotificationService.notifyRequestListUpdate(request.getProduct().getId());
        return RequestResponse.fromRequest(request);
    }
}
