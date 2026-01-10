package com.xavier.servicematchbackend.geomatching.application.dto;

import java.time.Instant;

public record ProviderFeedItemResponse(String requestId,
                                       String categoryId,
                                       String description,
                                       double locationLat,
                                       double locationLng,
                                       Instant matchedAt,
                                       Double distanceKm) {
}
