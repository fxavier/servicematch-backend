package com.xavier.servicematchbackend.geomatching.application.usecase;

import com.xavier.servicematchbackend.geomatching.domain.event.ProvidersMatched;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderCategoryRepository;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderRequestMatch;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderRequestMatchRepository;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderZone;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderZoneRepository;
import com.xavier.servicematchbackend.servicerequests.domain.event.RequestPublished;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GeoMatchingService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private final ProviderCategoryRepository providerCategoryRepository;
    private final ProviderZoneRepository providerZoneRepository;
    private final ProviderRequestMatchRepository providerRequestMatchRepository;
    private final ApplicationEventPublisher eventPublisher;

    public GeoMatchingService(ProviderCategoryRepository providerCategoryRepository,
                              ProviderZoneRepository providerZoneRepository,
                              ProviderRequestMatchRepository providerRequestMatchRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.providerCategoryRepository = providerCategoryRepository;
        this.providerZoneRepository = providerZoneRepository;
        this.providerRequestMatchRepository = providerRequestMatchRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void handleRequestPublished(RequestPublished event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }

        List<UUID> candidateProviders = providerCategoryRepository.findProviderIdsByCategory(event.categoryId());
        Set<UUID> matchedProviders = matchProviders(candidateProviders, event.locationLat(), event.locationLng());
        Instant now = Instant.now();

        if (!matchedProviders.isEmpty()) {
            persistMatches(matchedProviders, event, now);
        }

        eventPublisher.publishEvent(new ProvidersMatched(
                event.requestId(),
                event.categoryId(),
                List.copyOf(matchedProviders),
                now
        ));
    }

    @Transactional(readOnly = true)
    public List<ProviderRequestMatch> matchesForProvider(UUID providerId) {
        if (providerId == null) {
            throw new IllegalArgumentException("providerId must not be null");
        }
        return providerRequestMatchRepository.findByProviderIdOrderByMatchedAtDesc(providerId);
    }

    private Set<UUID> matchProviders(List<UUID> providerIds, double requestLat, double requestLng) {
        if (providerIds == null || providerIds.isEmpty()) {
            return Set.of();
        }
        List<ProviderZone> zones = providerZoneRepository.findByIdProviderIdIn(providerIds);
        Set<UUID> matches = new HashSet<>();
        for (ProviderZone zone : zones) {
            if (zone.providerId() == null) {
                continue;
            }
            double distanceKm = distanceKm(requestLat, requestLng, zone.centerLat(), zone.centerLng());
            if (distanceKm <= zone.radiusKm()) {
                matches.add(zone.providerId());
            }
        }
        return matches;
    }

    private void persistMatches(Set<UUID> providerIds, RequestPublished event, Instant now) {
        for (UUID providerId : providerIds) {
            if (providerRequestMatchRepository.existsByProviderIdAndRequestId(providerId, event.requestId())) {
                continue;
            }
            ProviderRequestMatch match = ProviderRequestMatch.create(
                    providerId,
                    event.requestId(),
                    event.categoryId(),
                    event.description(),
                    event.locationLat(),
                    event.locationLng(),
                    now
            );
            providerRequestMatchRepository.save(match);
        }
    }

    private double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS_KM * c;
    }
}
