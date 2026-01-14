package com.xavier.servicematchbackend.notifications.domain.entity;

import com.xavier.servicematchbackend.notifications.domain.valueobject.NotificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "notifications_log")
public class NotificationLog {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    @Column(name = "title", nullable = false, length = 160)
    private String title;

    @Column(name = "body", nullable = false, length = 1000)
    private String body;

    @Column(name = "payload")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "dispatched_at")
    private Instant dispatchedAt;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    protected NotificationLog() {
    }

    private NotificationLog(UUID id,
                            UUID recipientId,
                            String eventType,
                            String title,
                            String body,
                            String payload,
                            NotificationStatus status,
                            Instant createdAt) {
        this.id = requireNonNull(id, "id must not be null");
        this.recipientId = requireNonNull(recipientId, "recipientId must not be null");
        this.eventType = requireNonBlank(eventType, "eventType must not be blank");
        this.title = requireNonBlank(title, "title must not be blank");
        this.body = requireNonBlank(body, "body must not be blank");
        this.payload = payload;
        this.status = requireNonNull(status, "status must not be null");
        this.createdAt = requireNonNull(createdAt, "createdAt must not be null");
    }

    public static NotificationLog create(UUID recipientId,
                                         String eventType,
                                         String title,
                                         String body,
                                         String payload,
                                         Instant createdAt) {
        return new NotificationLog(
                UUID.randomUUID(),
                recipientId,
                eventType,
                title,
                body,
                payload,
                NotificationStatus.PENDING,
                createdAt
        );
    }

    public UUID id() {
        return id;
    }

    public UUID recipientId() {
        return recipientId;
    }

    public String eventType() {
        return eventType;
    }

    public String title() {
        return title;
    }

    public String body() {
        return body;
    }

    public String payload() {
        return payload;
    }

    public NotificationStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant dispatchedAt() {
        return dispatchedAt;
    }

    public String failureReason() {
        return failureReason;
    }

    public void markSent(Instant dispatchedAt) {
        this.status = NotificationStatus.SENT;
        this.dispatchedAt = requireNonNull(dispatchedAt, "dispatchedAt must not be null");
        this.failureReason = null;
    }

    public void markFailed(String reason, Instant dispatchedAt) {
        this.status = NotificationStatus.FAILED;
        this.dispatchedAt = requireNonNull(dispatchedAt, "dispatchedAt must not be null");
        this.failureReason = requireNonBlank(reason, "failureReason must not be blank");
    }

    private static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotificationLog that = (NotificationLog) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
