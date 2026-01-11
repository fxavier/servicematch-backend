package com.xavier.servicematchbackend.ratingsreviews.infra.web;

import com.xavier.servicematchbackend.ratingsreviews.application.dto.ProviderReviewCreateRequest;
import com.xavier.servicematchbackend.ratingsreviews.application.dto.ProviderReviewResponse;
import com.xavier.servicematchbackend.ratingsreviews.application.usecase.RatingsReviewsService;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service-requests")
public class ProviderReviewsController {

    private final RatingsReviewsService ratingsReviewsService;

    public ProviderReviewsController(RatingsReviewsService ratingsReviewsService) {
        this.ratingsReviewsService = ratingsReviewsService;
    }

    @PostMapping("/{requestId}/reviews")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ProviderReviewResponse> create(@AuthenticationPrincipal Jwt jwt,
                                                         @PathVariable String requestId,
                                                         @RequestBody ProviderReviewCreateRequest request) {
        UUID requesterId = parseUserId(jwt);
        ProviderReviewResponse response = ratingsReviewsService.submit(
                requesterId,
                parseUuid(requestId, "requestId"),
                request
        );
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
