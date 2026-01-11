package com.xavier.servicematchbackend.messaging.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conversation_messages")
public class ConversationMessage {

    public static final int MAX_BODY_LENGTH = 2000;

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "body", nullable = false, length = MAX_BODY_LENGTH)
    private String body;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    protected ConversationMessage() {
    }

    private ConversationMessage(UUID id,
                                UUID conversationId,
                                UUID senderId,
                                String body,
                                Instant sentAt) {
        this.id = requireNonNull(id, "id must not be null");
        this.conversationId = requireNonNull(conversationId, "conversationId must not be null");
        this.senderId = requireNonNull(senderId, "senderId must not be null");
        this.body = normalizeBody(body);
        this.sentAt = requireNonNull(sentAt, "sentAt must not be null");
    }

    public static ConversationMessage create(UUID conversationId,
                                             UUID senderId,
                                             String body,
                                             Instant sentAt) {
        return new ConversationMessage(UUID.randomUUID(), conversationId, senderId, body, sentAt);
    }

    public UUID id() {
        return id;
    }

    public UUID conversationId() {
        return conversationId;
    }

    public UUID senderId() {
        return senderId;
    }

    public String body() {
        return body;
    }

    public Instant sentAt() {
        return sentAt;
    }

    private static String normalizeBody(String body) {
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("body must not be blank");
        }
        String trimmed = body.trim();
        if (trimmed.length() > MAX_BODY_LENGTH) {
            throw new IllegalArgumentException("body must be at most " + MAX_BODY_LENGTH + " characters");
        }
        return trimmed;
    }

    private static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConversationMessage that = (ConversationMessage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
