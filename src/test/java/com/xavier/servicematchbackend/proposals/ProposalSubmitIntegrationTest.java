package com.xavier.servicematchbackend.proposals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.xavier.servicematchbackend.identityaccess.domain.entity.User;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Email;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.PasswordHash;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Role;
import com.xavier.servicematchbackend.identityaccess.infra.persistence.UserRepository;
import com.xavier.servicematchbackend.proposals.application.dto.ProposalCreateRequest;
import com.xavier.servicematchbackend.proposals.application.usecase.ProposalService;
import com.xavier.servicematchbackend.proposals.domain.event.ProposalSubmitted;
import com.xavier.servicematchbackend.servicecatalog.domain.entity.Category;
import com.xavier.servicematchbackend.servicecatalog.infra.persistence.CategoryRepository;
import com.xavier.servicematchbackend.servicerequests.application.dto.ServiceRequestCreateRequest;
import com.xavier.servicematchbackend.servicerequests.application.usecase.ServiceRequestService;
import com.xavier.servicematchbackend.servicerequests.domain.valueobject.ServiceRequestStatus;
import com.xavier.servicematchbackend.servicerequests.infra.persistence.ServiceRequestRepository;
import com.xavier.servicematchbackend.support.PostgresTestContainer;
import java.time.Instant;
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
class ProposalSubmitIntegrationTest extends PostgresTestContainer {

    @Autowired
    private ProposalService proposalService;

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Test
    void submittingProposalEmitsEvent() {
        UUID providerId = seedProvider();
        UUID requestId = seedServiceRequest("PUBLISHED");

        proposalService.submit(providerId, new ProposalCreateRequest(requestId.toString(), "Posso ajudar"));

        assertThat(applicationEvents.stream(ProposalSubmitted.class).count()).isEqualTo(1);
    }

    @Test
    void cannotSubmitProposalForCancelledRequest() {
        UUID providerId = seedProvider();
        UUID requestId = seedServiceRequest("CANCELLED");

        assertThatThrownBy(() -> proposalService.submit(
                providerId,
                new ProposalCreateRequest(requestId.toString(), "Sem disponibilidade")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cancelled");
    }

    private UUID seedServiceRequest(String status) {
        Category category = Category.create("Limpeza", null, "/limpeza", Instant.now());
        categoryRepository.save(category);

        UUID requesterId = seedRequester();
        String createStatus = "CANCELLED".equals(status) ? "PUBLISHED" : status;
        ServiceRequestCreateRequest request = new ServiceRequestCreateRequest(
                category.id().toString(),
                "Preciso de limpeza basica",
                -25.965,
                32.589,
                createStatus
        );
        UUID requestId = UUID.fromString(serviceRequestService.create(requesterId, request).id());
        if ("CANCELLED".equals(status)) {
            var serviceRequest = serviceRequestRepository.findById(requestId)
                    .orElseThrow();
            serviceRequest.updateStatus(ServiceRequestStatus.CANCELLED, Instant.now());
            serviceRequestRepository.save(serviceRequest);
        }
        return requestId;
    }

    private UUID seedProvider() {
        String email = "provider-proposal+" + UUID.randomUUID() + "@email.com";
        User providerUser = User.register(
                Email.of(email),
                PasswordHash.of("hash"),
                java.util.Set.of(Role.PROVIDER)
        );
        userRepository.save(providerUser);
        return providerUser.id().value();
    }

    private UUID seedRequester() {
        String email = "requester-proposal+" + UUID.randomUUID() + "@email.com";
        User requesterUser = User.register(
                Email.of(email),
                PasswordHash.of("hash"),
                java.util.Set.of(Role.CLIENT)
        );
        userRepository.save(requesterUser);
        return requesterUser.id().value();
    }
}
