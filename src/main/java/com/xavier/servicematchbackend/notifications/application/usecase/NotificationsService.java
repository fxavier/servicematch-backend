package com.xavier.servicematchbackend.notifications.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavier.servicematchbackend.notifications.application.dto.PushMessage;
import com.xavier.servicematchbackend.notifications.application.gateway.PushGateway;
import com.xavier.servicematchbackend.notifications.domain.entity.NotificationLog;
import com.xavier.servicematchbackend.notifications.infra.persistence.NotificationLogRepository;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationsService {

    private final NotificationLogRepository notificationLogRepository;
    private final PushGateway pushGateway;
    private final ObjectMapper objectMapper;

    public NotificationsService(NotificationLogRepository notificationLogRepository,
                                PushGateway pushGateway,
                                ObjectMapper objectMapper) {
        this.notificationLogRepository = notificationLogRepository;
        this.pushGateway = pushGateway;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public NotificationLog notify(UUID recipientId,
                                  String eventType,
                                  String title,
                                  String body,
                                  Map<String, Object> payload,
                                  Instant occurredAt) {
        UUID recipientUuid = requireRecipient(recipientId);
        String payloadJson = toJson(payload);
        Instant now = Instant.now();
        Instant createdAt = occurredAt != null ? occurredAt : now;

        NotificationLog log = NotificationLog.create(
                recipientUuid,
                eventType,
                title,
                body,
                payloadJson,
                createdAt
        );
        notificationLogRepository.save(log);

        try {
            pushGateway.send(new PushMessage(recipientUuid, title, body, payloadJson));
            log.markSent(now);
        } catch (Exception ex) {
            log.markFailed(normalizeFailure(ex), now);
        }
        return notificationLogRepository.save(log);
    }

    private UUID requireRecipient(UUID recipientId) {
        if (recipientId == null) {
            throw new IllegalArgumentException("recipientId must not be null");
        }
        return recipientId;
    }

    private String toJson(Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    private String normalizeFailure(Exception ex) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            return ex.getClass().getSimpleName();
        }
        return message;
    }
}
