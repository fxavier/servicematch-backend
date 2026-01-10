package com.xavier.servicematchbackend.geomatching.application.dto;

import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderRequestMatch;
import java.time.Instant;

public record ProviderMatchResponse(String requestId,
                                    String categoryId,
                                    String description,
                                    double locationLat,
                                    double locationLng,
                                    Instant matchedAt) {

    public static ProviderMatchResponse from(ProviderRequestMatch match) {
        return new ProviderMatchResponse(
                match.requestId().toString(),
                match.categoryId().toString(),
                match.description(),
                match.locationLat(),
                match.locationLng(),
                match.matchedAt()
        );
    }
}
