package com.xavier.servicematchbackend.servicerequests.application.dto;

import com.xavier.servicematchbackend.servicerequests.domain.entity.ServiceRequest;
import com.xavier.servicematchbackend.servicerequests.domain.valueobject.ServiceRequestStatus;

public record ServiceRequestResponse(String id,
                                     String requesterId,
                                     String categoryId,
                                     String description,
                                     double locationLat,
                                     double locationLng,
                                     ServiceRequestStatus status) {

    public static ServiceRequestResponse from(ServiceRequest serviceRequest) {
        return new ServiceRequestResponse(
                serviceRequest.id().toString(),
                serviceRequest.requesterId().toString(),
                serviceRequest.categoryId().toString(),
                serviceRequest.description(),
                serviceRequest.locationLat(),
                serviceRequest.locationLng(),
                serviceRequest.status()
        );
    }
}
