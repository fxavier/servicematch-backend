package com.xavier.servicematchbackend.messaging.infra.persistence;

import com.xavier.servicematchbackend.messaging.domain.entity.Conversation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
}
