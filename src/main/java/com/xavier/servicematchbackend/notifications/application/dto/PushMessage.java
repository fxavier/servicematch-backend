package com.xavier.servicematchbackend.notifications.application.dto;

import java.util.UUID;

public record PushMessage(UUID recipientId,
                          String title,
                          String body,
                          String payload) {
}
