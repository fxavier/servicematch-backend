package com.xavier.servicematchbackend.ratingsreviews.infra.persistence;

import com.xavier.servicematchbackend.ratingsreviews.domain.entity.ProviderReview;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProviderReviewRepository extends JpaRepository<ProviderReview, UUID> {

    boolean existsByRequestId(UUID requestId);

    @Query("select avg(r.rating) from ProviderReview r where r.providerId = :providerId")
    Double averageRatingForProvider(@Param("providerId") UUID providerId);
}
