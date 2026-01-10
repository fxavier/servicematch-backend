package com.xavier.servicematchbackend.geomatching.application.event;

import com.xavier.servicematchbackend.geomatching.application.usecase.GeoMatchingService;
import com.xavier.servicematchbackend.servicerequests.domain.event.RequestPublished;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RequestPublishedEventHandler {

    private final GeoMatchingService geoMatchingService;

    public RequestPublishedEventHandler(GeoMatchingService geoMatchingService) {
        this.geoMatchingService = geoMatchingService;
    }

    @EventListener
    public void on(RequestPublished event) {
        geoMatchingService.handleRequestPublished(event);
    }
}
