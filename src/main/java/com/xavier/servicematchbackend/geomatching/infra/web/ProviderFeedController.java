package com.xavier.servicematchbackend.geomatching.infra.web;

import com.xavier.servicematchbackend.geomatching.application.dto.ProviderMatchResponse;
import com.xavier.servicematchbackend.geomatching.application.usecase.GeoMatchingService;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderRequestMatch;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matching")
public class ProviderFeedController {

    private final GeoMatchingService geoMatchingService;

    public ProviderFeedController(GeoMatchingService geoMatchingService) {
        this.geoMatchingService = geoMatchingService;
    }

    @GetMapping("/feed")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    public ResponseEntity<List<ProviderMatchResponse>> feed(@AuthenticationPrincipal Jwt jwt) {
        UUID providerId = parseUserId(jwt);
        List<ProviderRequestMatch> matches = geoMatchingService.matchesForProvider(providerId);
        List<ProviderMatchResponse> response = matches.stream()
                .map(ProviderMatchResponse::from)
                .toList();
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
