package com.xavier.servicematchbackend.geomatching.infra.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRequestMatchRepository extends JpaRepository<ProviderRequestMatch, UUID> {

    Page<ProviderRequestMatch> findByProviderId(UUID providerId, Pageable pageable);

    List<ProviderRequestMatch> findByProviderIdOrderByMatchedAtDesc(UUID providerId);

    boolean existsByProviderIdAndRequestId(UUID providerId, UUID requestId);
}
