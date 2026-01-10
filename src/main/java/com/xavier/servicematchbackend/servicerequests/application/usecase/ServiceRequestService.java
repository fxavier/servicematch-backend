package com.xavier.servicematchbackend.servicerequests.application.usecase;

import com.xavier.servicematchbackend.servicecatalog.application.usecase.CategoryService;
import com.xavier.servicematchbackend.servicerequests.application.dto.ServiceRequestCreateRequest;
import com.xavier.servicematchbackend.servicerequests.application.dto.ServiceRequestResponse;
import com.xavier.servicematchbackend.servicerequests.domain.event.RequestPublished;
import com.xavier.servicematchbackend.servicerequests.domain.entity.ServiceRequest;
import com.xavier.servicematchbackend.servicerequests.domain.valueobject.ServiceRequestStatus;
import com.xavier.servicematchbackend.servicerequests.infra.persistence.ServiceRequestRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CategoryService categoryService;
    private final ApplicationEventPublisher eventPublisher;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
                                 CategoryService categoryService,
                                 ApplicationEventPublisher eventPublisher) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.categoryService = categoryService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ServiceRequestResponse create(UUID requesterId, ServiceRequestCreateRequest request) {
        ServiceRequestCreateRequest input = requireRequest(request);
        UUID categoryId = parseUuid(input.categoryId(), "categoryId");
        validateCategory(categoryId);
        String description = requireDescription(input.description());
        double lat = requireLatitude(input.locationLat());
        double lng = requireLongitude(input.locationLng());
        ServiceRequestStatus status = parseStatus(input.status());

        ServiceRequest serviceRequest = ServiceRequest.create(
                requireRequester(requesterId),
                categoryId,
                description,
                status,
                lat,
                lng,
                Instant.now()
        );
        serviceRequestRepository.save(serviceRequest);
        if (status == ServiceRequestStatus.PUBLISHED) {
            eventPublisher.publishEvent(new RequestPublished(
                    serviceRequest.id(),
                    serviceRequest.requesterId(),
                    serviceRequest.categoryId(),
                    serviceRequest.description(),
                    serviceRequest.locationLat(),
                    serviceRequest.locationLng(),
                    Instant.now()
            ));
        }
        return ServiceRequestResponse.from(serviceRequest);
    }

    private ServiceRequestCreateRequest requireRequest(ServiceRequestCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        return request;
    }

    private UUID requireRequester(UUID requesterId) {
        if (requesterId == null) {
            throw new IllegalArgumentException("requesterId must not be null");
        }
        return requesterId;
    }

    private void validateCategory(UUID categoryId) {
        if (!categoryService.existsCategory(categoryId)) {
            throw new IllegalArgumentException("category not found");
        }
    }

    private String requireDescription(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        return value.trim();
    }

    private double requireLatitude(Double value) {
        if (value == null) {
            throw new IllegalArgumentException("locationLat must not be null");
        }
        if (value < -90 || value > 90) {
            throw new IllegalArgumentException("locationLat must be between -90 and 90");
        }
        return value;
    }

    private double requireLongitude(Double value) {
        if (value == null) {
            throw new IllegalArgumentException("locationLng must not be null");
        }
        if (value < -180 || value > 180) {
            throw new IllegalArgumentException("locationLng must be between -180 and 180");
        }
        return value;
    }

    private ServiceRequestStatus parseStatus(String value) {
        if (value == null || value.isBlank()) {
            return ServiceRequestStatus.DRAFT;
        }
        ServiceRequestStatus status;
        try {
            status = ServiceRequestStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("status must be a valid ServiceRequestStatus");
        }
        if (status != ServiceRequestStatus.DRAFT && status != ServiceRequestStatus.PUBLISHED) {
            throw new IllegalArgumentException("status must be DRAFT or PUBLISHED");
        }
        return status;
    }

    private UUID parseUuid(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(field + " must be a valid UUID");
        }
    }
}
