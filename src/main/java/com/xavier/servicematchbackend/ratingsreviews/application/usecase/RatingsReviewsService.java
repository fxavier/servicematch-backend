package com.xavier.servicematchbackend.ratingsreviews.application.usecase;

import com.xavier.servicematchbackend.profiles.application.usecase.ProfilesService;
import com.xavier.servicematchbackend.proposals.application.usecase.ProposalService;
import com.xavier.servicematchbackend.ratingsreviews.application.dto.ProviderReviewCreateRequest;
import com.xavier.servicematchbackend.ratingsreviews.application.dto.ProviderReviewResponse;
import com.xavier.servicematchbackend.ratingsreviews.domain.entity.ProviderReview;
import com.xavier.servicematchbackend.ratingsreviews.infra.persistence.ProviderReviewRepository;
import com.xavier.servicematchbackend.servicerequests.application.usecase.ServiceRequestService;
import java.time.Instant;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingsReviewsService {

    private final ProviderReviewRepository reviewRepository;
    private final ServiceRequestService serviceRequestService;
    private final ProposalService proposalService;
    private final ProfilesService profilesService;

    public RatingsReviewsService(ProviderReviewRepository reviewRepository,
                                 ServiceRequestService serviceRequestService,
                                 ProposalService proposalService,
                                 ProfilesService profilesService) {
        this.reviewRepository = reviewRepository;
        this.serviceRequestService = serviceRequestService;
        this.proposalService = proposalService;
        this.profilesService = profilesService;
    }

    @Transactional
    public ProviderReviewResponse submit(UUID requesterId,
                                         UUID requestId,
                                         ProviderReviewCreateRequest request) {
        UUID requesterUuid = requireUser(requesterId, "requesterId");
        UUID requestUuid = requireRequestId(requestId);
        ProviderReviewCreateRequest input = requireRequest(request);

        serviceRequestService.assertRequestCompleted(requestUuid, requesterUuid);

        if (reviewRepository.existsByRequestId(requestUuid)) {
            throw new IllegalArgumentException("review already submitted");
        }

        UUID providerId = proposalService.findAcceptedProviderId(requestUuid);
        int rating = requireRating(input.rating());

        Instant now = Instant.now();
        ProviderReview review = ProviderReview.create(
                requestUuid,
                providerId,
                requesterUuid,
                rating,
                input.comment(),
                now
        );
        try {
            reviewRepository.save(review);
        } catch (DataIntegrityViolationException ex) {
            if (reviewRepository.existsByRequestId(requestUuid)) {
                throw new IllegalArgumentException("review already submitted");
            }
            throw ex;
        }

        Double reputation = reviewRepository.averageRatingForProvider(providerId);
        profilesService.updateProviderReputation(providerId, reputation, now);

        return ProviderReviewResponse.from(review);
    }

    private ProviderReviewCreateRequest requireRequest(ProviderReviewCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        return request;
    }

    private UUID requireRequestId(UUID requestId) {
        if (requestId == null) {
            throw new IllegalArgumentException("requestId must not be null");
        }
        return requestId;
    }

    private UUID requireUser(UUID userId, String field) {
        if (userId == null) {
            throw new IllegalArgumentException(field + " must not be null");
        }
        return userId;
    }

    private int requireRating(Integer rating) {
        if (rating == null) {
            throw new IllegalArgumentException("rating must not be null");
        }
        return rating;
    }
}
