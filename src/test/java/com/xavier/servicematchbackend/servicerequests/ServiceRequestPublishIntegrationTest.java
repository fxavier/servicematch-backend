package com.xavier.servicematchbackend.servicerequests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.xavier.servicematchbackend.geomatching.application.usecase.GeoMatchingService;
import com.xavier.servicematchbackend.servicecatalog.domain.entity.Category;
import com.xavier.servicematchbackend.servicecatalog.infra.persistence.CategoryRepository;
import com.xavier.servicematchbackend.servicerequests.application.dto.ServiceRequestCreateRequest;
import com.xavier.servicematchbackend.servicerequests.application.usecase.ServiceRequestService;
import com.xavier.servicematchbackend.servicerequests.domain.event.RequestPublished;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
class ServiceRequestPublishIntegrationTest {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    @SpyBean
    private GeoMatchingService geoMatchingService;

    @Test
    void publishRequestEmitsEventAndNotifiesGeoMatching() {
        Category category = Category.create("Limpeza", null, "/limpeza", Instant.now());
        categoryRepository.save(category);

        ServiceRequestCreateRequest request = new ServiceRequestCreateRequest(
                category.id().toString(),
                "Preciso de limpeza basica",
                -25.965,
                32.589,
                "PUBLISHED"
        );

        serviceRequestService.create(UUID.randomUUID(), request);

        long publishedEvents = applicationEvents.stream(RequestPublished.class).count();
        assertThat(publishedEvents).isEqualTo(1);

        verify(geoMatchingService, timeout(1000)).handleRequestPublished(any(RequestPublished.class));
    }
}
