package com.xavier.servicematchbackend.geomatching.infra.web;

import com.xavier.servicematchbackend.geomatching.application.dto.ProviderFeedResponse;
import com.xavier.servicematchbackend.geomatching.application.usecase.GeoMatchingService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderFeedController {

    private final GeoMatchingService geoMatchingService;

    public ProviderFeedController(GeoMatchingService geoMatchingService) {
        this.geoMatchingService = geoMatchingService;
    }

    @GetMapping({"/provider/feed", "/matching/feed"})
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ProviderFeedResponse> feed(@AuthenticationPrincipal Jwt jwt,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size,
                                                     @RequestParam(defaultValue = "recency") String sort) {
        UUID providerId = parseUserId(jwt);
        ProviderFeedResponse response = geoMatchingService.feedForProvider(providerId, page, size, sort);
        return ResponseEntity.ok(response);
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
