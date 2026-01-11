package com.xavier.servicematchbackend.messaging.application.usecase;

import com.xavier.servicematchbackend.messaging.application.dto.ConversationMessageCreateRequest;
import com.xavier.servicematchbackend.messaging.application.dto.ConversationMessageResponse;
import com.xavier.servicematchbackend.messaging.application.dto.ConversationMessagesResponse;
import com.xavier.servicematchbackend.messaging.domain.entity.Conversation;
import com.xavier.servicematchbackend.messaging.domain.entity.ConversationMessage;
import com.xavier.servicematchbackend.messaging.infra.persistence.ConversationMessageRepository;
import com.xavier.servicematchbackend.messaging.infra.persistence.ConversationRepository;
import com.xavier.servicematchbackend.proposals.domain.event.ProposalAccepted;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConversationService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository messageRepository;

    public ConversationService(ConversationRepository conversationRepository,
                               ConversationMessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public void handleProposalAccepted(ProposalAccepted event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        if (conversationRepository.existsById(event.requestId())) {
            return;
        }
        Conversation conversation = Conversation.create(
                event.requestId(),
                event.proposalId(),
                event.requesterId(),
                event.providerId(),
                event.acceptedAt()
        );
        try {
            conversationRepository.save(conversation);
        } catch (DataIntegrityViolationException ex) {
            if (!conversationRepository.existsById(event.requestId())) {
                throw ex;
            }
        }
    }

    @Transactional
    public ConversationMessageResponse postMessage(UUID conversationId,
                                                   UUID senderId,
                                                   ConversationMessageCreateRequest request) {
        UUID conversationUuid = requireConversationId(conversationId);
        UUID senderUuid = requireUser(senderId, "senderId");
        ConversationMessageCreateRequest input = requireRequest(request);
        Conversation conversation = requireConversation(conversationUuid);
        if (!conversation.isParticipant(senderUuid)) {
            throw new IllegalArgumentException("user does not belong to conversation");
        }

        Instant now = Instant.now();
        ConversationMessage message = ConversationMessage.create(
                conversationUuid,
                senderUuid,
                input.body(),
                now
        );
        messageRepository.save(message);
        conversation.touch(now);
        conversationRepository.save(conversation);
        return ConversationMessageResponse.from(message);
    }

    @Transactional(readOnly = true)
    public ConversationMessagesResponse listMessages(UUID conversationId,
                                                     UUID userId,
                                                     int page,
                                                     int size) {
        UUID conversationUuid = requireConversationId(conversationId);
        UUID viewerId = requireUser(userId, "userId");
        Conversation conversation = requireConversation(conversationUuid);
        if (!conversation.isParticipant(viewerId)) {
            throw new IllegalArgumentException("user does not belong to conversation");
        }

        int safePage = Math.max(page, 0);
        int safeSize = normalizeSize(size);
        PageRequest pageRequest = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Order.desc("sentAt"))
        );
        Page<ConversationMessage> pageResult = messageRepository
                .findByConversationId(conversationUuid, pageRequest);
        List<ConversationMessageResponse> items = pageResult.getContent().stream()
                .map(ConversationMessageResponse::from)
                .toList();
        return new ConversationMessagesResponse(
                items,
                safePage,
                safeSize,
                pageResult.getTotalElements()
        );
    }

    private ConversationMessageCreateRequest requireRequest(ConversationMessageCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        return request;
    }

    private Conversation requireConversation(UUID conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("conversation not found"));
    }

    private UUID requireConversationId(UUID conversationId) {
        if (conversationId == null) {
            throw new IllegalArgumentException("conversationId must not be null");
        }
        return conversationId;
    }

    private UUID requireUser(UUID userId, String field) {
        if (userId == null) {
            throw new IllegalArgumentException(field + " must not be null");
        }
        return userId;
    }

    private int normalizeSize(int size) {
        int safeSize = size <= 0 ? DEFAULT_PAGE_SIZE : size;
        return Math.min(safeSize, MAX_PAGE_SIZE);
    }
}
