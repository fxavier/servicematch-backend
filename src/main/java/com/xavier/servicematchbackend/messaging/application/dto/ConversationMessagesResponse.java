package com.xavier.servicematchbackend.messaging.application.dto;

import java.util.List;

public record ConversationMessagesResponse(List<ConversationMessageResponse> items,
                                           int page,
                                           int size,
                                           long total) {
}
