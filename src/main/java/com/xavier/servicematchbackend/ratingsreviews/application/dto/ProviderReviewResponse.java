package com.xavier.servicematchbackend.ratingsreviews.application.dto;

import com.xavier.servicematchbackend.ratingsreviews.domain.entity.ProviderReview;
import java.time.Instant;

public record ProviderReviewResponse(String id,
                                     String requestId,
                                     String providerId,
                                     String requesterId,
                                     int rating,
                                     String comment,
                                     Instant createdAt) {

    public static ProviderReviewResponse from(ProviderReview review) {
        return new ProviderReviewResponse(
                review.id().toString(),
                review.requestId().toString(),
                review.providerId().toString(),
                review.requesterId().toString(),
                review.rating(),
                review.comment(),
                review.createdAt()
        );
    }
}
