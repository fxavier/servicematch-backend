package com.xavier.servicematchbackend.notifications.application.event;

import com.xavier.servicematchbackend.notifications.application.usecase.NotificationsService;
import com.xavier.servicematchbackend.proposals.domain.event.ProposalAccepted;
import com.xavier.servicematchbackend.proposals.domain.event.ProposalSubmitted;
import com.xavier.servicematchbackend.servicerequests.application.usecase.ServiceRequestService;
import com.xavier.servicematchbackend.servicerequests.domain.event.RequestPublished;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    private final NotificationsService notificationsService;
    private final ServiceRequestService serviceRequestService;

    public NotificationEventListener(NotificationsService notificationsService,
                                     ServiceRequestService serviceRequestService) {
        this.notificationsService = notificationsService;
        this.serviceRequestService = serviceRequestService;
    }

    @EventListener
    public void on(RequestPublished event) {
        notificationsService.notify(
                event.requesterId(),
                "REQUEST_PUBLISHED",
                "Request published",
                "Your request is now published.",
                Map.of(
                        "requestId", event.requestId().toString(),
                        "categoryId", event.categoryId().toString()
                ),
                event.publishedAt()
        );
    }

    @EventListener
    public void on(ProposalSubmitted event) {
        UUID requesterId = serviceRequestService.findRequesterId(event.requestId());
        notificationsService.notify(
                requesterId,
                "PROPOSAL_SUBMITTED",
                "New proposal",
                "You received a new proposal.",
                Map.of(
                        "proposalId", event.proposalId().toString(),
                        "requestId", event.requestId().toString(),
                        "providerId", event.providerId().toString()
                ),
                event.submittedAt()
        );
    }

    @EventListener
    public void on(ProposalAccepted event) {
        notifyProviderAccepted(event);
        notifyRequesterAccepted(event);
    }

    private void notifyProviderAccepted(ProposalAccepted event) {
        notificationsService.notify(
                event.providerId(),
                "PROPOSAL_ACCEPTED",
                "Proposal accepted",
                "Your proposal was accepted.",
                Map.of(
                        "proposalId", event.proposalId().toString(),
                        "requestId", event.requestId().toString(),
                        "requesterId", event.requesterId().toString()
                ),
                event.acceptedAt()
        );
    }

    private void notifyRequesterAccepted(ProposalAccepted event) {
        notificationsService.notify(
                event.requesterId(),
                "PROPOSAL_ACCEPTED",
                "Proposal accepted",
                "You accepted a proposal.",
                Map.of(
                        "proposalId", event.proposalId().toString(),
                        "requestId", event.requestId().toString(),
                        "providerId", event.providerId().toString()
                ),
                event.acceptedAt()
        );
    }
}
