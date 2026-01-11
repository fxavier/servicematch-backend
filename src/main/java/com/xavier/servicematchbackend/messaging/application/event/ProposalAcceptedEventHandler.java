package com.xavier.servicematchbackend.messaging.application.event;

import com.xavier.servicematchbackend.messaging.application.usecase.ConversationService;
import com.xavier.servicematchbackend.proposals.domain.event.ProposalAccepted;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProposalAcceptedEventHandler {

    private final ConversationService conversationService;

    public ProposalAcceptedEventHandler(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @EventListener
    public void on(ProposalAccepted event) {
        conversationService.handleProposalAccepted(event);
    }
}
