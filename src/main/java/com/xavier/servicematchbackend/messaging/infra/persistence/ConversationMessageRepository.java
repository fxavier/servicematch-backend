package com.xavier.servicematchbackend.messaging.infra.persistence;

import com.xavier.servicematchbackend.messaging.domain.entity.ConversationMessage;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, UUID> {

    Page<ConversationMessage> findByConversationId(UUID conversationId, Pageable pageable);
}
