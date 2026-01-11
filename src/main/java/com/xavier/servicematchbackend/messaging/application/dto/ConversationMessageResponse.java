package com.xavier.servicematchbackend.messaging.application.dto;

import com.xavier.servicematchbackend.messaging.domain.entity.ConversationMessage;
import java.time.Instant;

public record ConversationMessageResponse(String id,
                                          String conversationId,
                                          String senderId,
                                          String body,
                                          Instant sentAt) {

    public static ConversationMessageResponse from(ConversationMessage message) {
        return new ConversationMessageResponse(
                message.id().toString(),
                message.conversationId().toString(),
                message.senderId().toString(),
                message.body(),
                message.sentAt()
        );
    }
}
