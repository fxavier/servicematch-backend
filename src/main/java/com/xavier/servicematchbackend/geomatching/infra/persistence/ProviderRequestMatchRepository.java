package com.xavier.servicematchbackend.geomatching.infra.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRequestMatchRepository extends JpaRepository<ProviderRequestMatch, UUID> {

    List<ProviderRequestMatch> findByProviderIdOrderByMatchedAtDesc(UUID providerId);

    boolean existsByProviderIdAndRequestId(UUID providerId, UUID requestId);
}
