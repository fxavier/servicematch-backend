package com.xavier.servicematchbackend.geomatching.application.usecase;

import com.xavier.servicematchbackend.servicerequests.domain.event.RequestPublished;
import org.springframework.stereotype.Service;

@Service
public class GeoMatchingService {

    public void handleRequestPublished(RequestPublished event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        // Placeholder for geo-matching workflow.
    }
}
