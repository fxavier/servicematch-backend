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
import com.xavier.servicematchbackend.proposals.domain.entity.Proposal;
import com.xavier.servicematchbackend.proposals.domain.event.ProposalAccepted;
import com.xavier.servicematchbackend.proposals.domain.valueobject.ProposalStatus;
import com.xavier.servicematchbackend.proposals.infra.persistence.ProposalRepository;
import com.xavier.servicematchbackend.servicecatalog.domain.entity.Category;
import com.xavier.servicematchbackend.servicecatalog.infra.persistence.CategoryRepository;
import com.xavier.servicematchbackend.servicerequests.application.dto.ServiceRequestCreateRequest;
import com.xavier.servicematchbackend.servicerequests.application.usecase.ServiceRequestService;
import com.xavier.servicematchbackend.servicerequests.domain.entity.ServiceRequest;
import com.xavier.servicematchbackend.servicerequests.domain.valueobject.ServiceRequestStatus;
import com.xavier.servicematchbackend.servicerequests.infra.persistence.ServiceRequestRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
class ProposalAcceptIntegrationTest {

    @Autowired
    private ProposalService proposalService;

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Test
    void acceptIsIdempotent() {
        UUID requesterId = seedRequester();
        UUID requestId = seedServiceRequest(requesterId);
        UUID providerId = seedProvider();

        Proposal proposal = submitProposal(providerId, requestId);

        proposalService.accept(requesterId, proposal.id());
        proposalService.accept(requesterId, proposal.id());

        Proposal persisted = proposalRepository.findById(proposal.id()).orElseThrow();
        assertThat(persisted.status()).isEqualTo(ProposalStatus.ACCEPTED);
        assertThat(applicationEvents.stream(ProposalAccepted.class).count()).isEqualTo(1);
    }

    @Test
    void concurrentAcceptClosesOtherProposal() throws Exception {
        UUID requesterId = seedRequester();
        UUID requestId = seedServiceRequest(requesterId);
        UUID providerA = seedProvider();
        UUID providerB = seedProvider();

        Proposal proposalA = submitProposal(providerA, requestId);
        Proposal proposalB = submitProposal(providerB, requestId);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Callable<Void> acceptA = () -> {
            proposalService.accept(requesterId, proposalA.id());
            return null;
        };
        Callable<Void> acceptB = () -> {
            proposalService.accept(requesterId, proposalB.id());
            return null;
        };

        List<Future<Void>> futures = executor.invokeAll(List.of(acceptA, acceptB));
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        int successCount = 0;
        int failureCount = 0;
        for (Future<Void> future : futures) {
            try {
                future.get();
                successCount++;
            } catch (ExecutionException ex) {
                failureCount++;
            }
        }

        assertThat(successCount).isEqualTo(1);
        assertThat(failureCount).isEqualTo(1);

        Proposal refreshedA = proposalRepository.findById(proposalA.id()).orElseThrow();
        Proposal refreshedB = proposalRepository.findById(proposalB.id()).orElseThrow();
        assertThat(List.of(refreshedA.status(), refreshedB.status()))
                .containsExactlyInAnyOrder(ProposalStatus.ACCEPTED, ProposalStatus.REJECTED);

        ServiceRequest request = serviceRequestRepository.findById(requestId).orElseThrow();
        assertThat(request.status()).isEqualTo(ServiceRequestStatus.BOOKED);
    }

    @Test
    void cannotAcceptRejectedProposal() {
        UUID requesterId = seedRequester();
        UUID requestId = seedServiceRequest(requesterId);
        UUID providerId = seedProvider();

        Proposal proposal = submitProposal(providerId, requestId);
        proposal.updateStatus(ProposalStatus.REJECTED, Instant.now());
        proposalRepository.save(proposal);

        assertThatThrownBy(() -> proposalService.accept(requesterId, proposal.id()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be accepted");
    }

    private Proposal submitProposal(UUID providerId, UUID requestId) {
        proposalService.submit(providerId, new ProposalCreateRequest(requestId.toString(), "Posso ajudar"));
        return proposalRepository.findAll().stream()
                .filter(p -> p.requestId().equals(requestId) && p.providerId().equals(providerId))
                .findFirst()
                .orElseThrow();
    }

    private UUID seedServiceRequest(UUID requesterId) {
        Category category = Category.create("Limpeza", null, "/limpeza", Instant.now());
        categoryRepository.save(category);

        ServiceRequestCreateRequest request = new ServiceRequestCreateRequest(
                category.id().toString(),
                "Preciso de limpeza basica",
                -25.965,
                32.589,
                "PUBLISHED"
        );
        return UUID.fromString(serviceRequestService.create(requesterId, request).id());
    }

    private UUID seedProvider() {
        String email = "provider-accept+" + UUID.randomUUID() + "@email.com";
        User providerUser = User.register(
                Email.of(email),
                PasswordHash.of("hash"),
                java.util.Set.of(Role.PROVIDER)
        );
        userRepository.save(providerUser);
        return providerUser.id().value();
    }

    private UUID seedRequester() {
        String email = "requester-accept+" + UUID.randomUUID() + "@email.com";
        User requesterUser = User.register(
                Email.of(email),
                PasswordHash.of("hash"),
                java.util.Set.of(Role.CLIENT)
        );
        userRepository.save(requesterUser);
        return requesterUser.id().value();
    }
}
