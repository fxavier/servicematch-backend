package com.xavier.servicematchbackend.geomatching;

import static org.assertj.core.api.Assertions.assertThat;

import com.xavier.servicematchbackend.geomatching.domain.event.ProvidersMatched;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderCategory;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderCategoryId;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderCategoryRepository;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderRequestMatch;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderRequestMatchRepository;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderZone;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderZoneId;
import com.xavier.servicematchbackend.geomatching.infra.persistence.ProviderZoneRepository;
import com.xavier.servicematchbackend.identityaccess.domain.entity.User;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Email;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.PasswordHash;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Role;
import com.xavier.servicematchbackend.identityaccess.infra.persistence.UserRepository;
import com.xavier.servicematchbackend.profiles.domain.entity.ProviderProfile;
import com.xavier.servicematchbackend.profiles.domain.valueobject.UserId;
import com.xavier.servicematchbackend.profiles.infra.persistence.ProviderProfileRepository;
import com.xavier.servicematchbackend.servicecatalog.domain.entity.Category;
import com.xavier.servicematchbackend.servicecatalog.infra.persistence.CategoryRepository;
import com.xavier.servicematchbackend.servicerequests.application.dto.ServiceRequestCreateRequest;
import com.xavier.servicematchbackend.servicerequests.application.usecase.ServiceRequestService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
class GeoMatchingIntegrationTest {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProviderCategoryRepository providerCategoryRepository;

    @Autowired
    private ProviderZoneRepository providerZoneRepository;

    @Autowired
    private ProviderRequestMatchRepository providerRequestMatchRepository;

    @Autowired
    private ProviderProfileRepository providerProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Test
    void publishedRequestMatchesProvidersByCategoryAndRadius() {
        Instant now = Instant.now();
        Category category = Category.create("Limpeza", null, "/limpeza", now);
        categoryRepository.save(category);

        User providerUser = User.register(
                Email.of("provider@email.com"),
                PasswordHash.of("hash"),
                java.util.Set.of(Role.PROVIDER)
        );
        userRepository.save(providerUser);
        UUID providerId = providerUser.id().value();

        ProviderProfile providerProfile = ProviderProfile.create(UserId.of(providerId), now);
        providerProfileRepository.save(providerProfile);

        providerCategoryRepository.save(new ProviderCategory(
                new ProviderCategoryId(providerId, category.id())
        ));

        providerZoneRepository.save(new ProviderZone(
                new ProviderZoneId(providerId, UUID.randomUUID()),
                -25.965,
                32.589,
                5.0
        ));

        ServiceRequestCreateRequest request = new ServiceRequestCreateRequest(
                category.id().toString(),
                "Preciso de limpeza basica",
                -25.965,
                32.589,
                "PUBLISHED"
        );

        serviceRequestService.create(UUID.randomUUID(), request);

        List<ProvidersMatched> events = applicationEvents.stream(ProvidersMatched.class).toList();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).providerIds()).contains(providerId);

        List<ProviderRequestMatch> matches =
                providerRequestMatchRepository.findByProviderIdOrderByMatchedAtDesc(providerId);
        assertThat(matches).isNotEmpty();
        assertThat(matches.get(0).categoryId()).isEqualTo(category.id());
    }
}
