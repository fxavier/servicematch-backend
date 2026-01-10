package com.xavier.servicematchbackend.geomatching.application.usecase;

import com.xavier.servicematchbackend.geomatching.application.dto.ProviderFeedItemResponse;
import com.xavier.servicematchbackend.geomatching.application.dto.ProviderFeedResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GeoMatchingService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

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

    @Transactional(readOnly = true)
    public ProviderFeedResponse feedForProvider(UUID providerId, int page, int size, String sort) {
        if (providerId == null) {
            throw new IllegalArgumentException("providerId must not be null");
        }
        int safePage = Math.max(page, 0);
        int safeSize = normalizeSize(size);
        String sortMode = normalizeSort(sort);

        List<ProviderZone> zones = providerZoneRepository.findByIdProviderId(providerId);

        if ("proximity".equals(sortMode)) {
            List<ProviderRequestMatch> matches = providerRequestMatchRepository
                    .findByProviderIdOrderByMatchedAtDesc(providerId);
            List<MatchWithDistance> withDistance = matches.stream()
                    .map(match -> new MatchWithDistance(match, distanceToNearestZone(match, zones)))
                    .sorted((left, right) -> {
                        int distanceCompare = Double.compare(left.distanceKm(), right.distanceKm());
                        if (distanceCompare != 0) {
                            return distanceCompare;
                        }
                        return right.match().matchedAt().compareTo(left.match().matchedAt());
                    })
                    .toList();

            List<ProviderFeedItemResponse> items = paginate(withDistance, safePage, safeSize).stream()
                    .map(item -> toFeedItem(item.match(), item.distanceKm()))
                    .toList();
            return new ProviderFeedResponse(items, safePage, safeSize, withDistance.size(), sortMode);
        }

        PageRequest pageRequest = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Order.desc("matchedAt"))
        );
        Page<ProviderRequestMatch> pageResult =
                providerRequestMatchRepository.findByProviderId(providerId, pageRequest);
        List<ProviderFeedItemResponse> items = pageResult.getContent().stream()
                .map(match -> toFeedItem(match, distanceToNearestZone(match, zones)))
                .toList();
        return new ProviderFeedResponse(items, safePage, safeSize, pageResult.getTotalElements(), sortMode);
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

    private double distanceToNearestZone(ProviderRequestMatch match, List<ProviderZone> zones) {
        if (zones == null || zones.isEmpty()) {
            return Double.POSITIVE_INFINITY;
        }
        double min = Double.POSITIVE_INFINITY;
        for (ProviderZone zone : zones) {
            double distance = distanceKm(
                    match.locationLat(),
                    match.locationLng(),
                    zone.centerLat(),
                    zone.centerLng()
            );
            if (distance < min) {
                min = distance;
            }
        }
        return min;
    }

    private ProviderFeedItemResponse toFeedItem(ProviderRequestMatch match, double distanceKm) {
        Double distance = Double.isInfinite(distanceKm) ? null : distanceKm;
        return new ProviderFeedItemResponse(
                match.requestId().toString(),
                match.categoryId().toString(),
                match.description(),
                match.locationLat(),
                match.locationLng(),
                match.matchedAt(),
                distance
        );
    }

    private List<MatchWithDistance> paginate(List<MatchWithDistance> items, int page, int size) {
        if (items.isEmpty()) {
            return List.of();
        }
        int fromIndex = page * size;
        if (fromIndex >= items.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + size, items.size());
        return items.subList(fromIndex, toIndex);
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    private String normalizeSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return "recency";
        }
        String normalized = sort.trim().toLowerCase();
        if ("proximity".equals(normalized)) {
            return "proximity";
        }
        return "recency";
    }

    private record MatchWithDistance(ProviderRequestMatch match, double distanceKm) {
    }
}
