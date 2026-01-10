package com.xavier.servicematchbackend.servicerequests.infra.web;

import com.xavier.servicematchbackend.servicerequests.application.dto.ServiceRequestCreateRequest;
import com.xavier.servicematchbackend.servicerequests.application.dto.ServiceRequestResponse;
import com.xavier.servicematchbackend.servicerequests.application.usecase.ServiceRequestService;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service-requests")
public class ServiceRequestsController {

    private final ServiceRequestService serviceRequestService;

    public ServiceRequestsController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<ServiceRequestResponse> create(@AuthenticationPrincipal Jwt jwt,
                                                         @RequestBody ServiceRequestCreateRequest request) {
        UUID requesterId = parseUserId(jwt);
        ServiceRequestResponse response = serviceRequestService.create(requesterId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private UUID parseUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            throw new IllegalArgumentException("user id not found in token");
        }
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("user id must be a valid UUID");
        }
    }
}
