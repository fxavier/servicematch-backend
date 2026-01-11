package com.xavier.servicematchbackend.messaging.infra.web;

import com.xavier.servicematchbackend.messaging.application.dto.ConversationMessageCreateRequest;
import com.xavier.servicematchbackend.messaging.application.dto.ConversationMessageResponse;
import com.xavier.servicematchbackend.messaging.application.dto.ConversationMessagesResponse;
import com.xavier.servicematchbackend.messaging.application.usecase.ConversationService;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conversations")
public class ConversationsController {

    private final ConversationService conversationService;

    public ConversationsController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping("/{conversationId}/messages")
    @PreAuthorize("hasAnyRole('CLIENT', 'PROVIDER')")
    public ResponseEntity<ConversationMessageResponse> postMessage(@AuthenticationPrincipal Jwt jwt,
                                                                   @PathVariable String conversationId,
                                                                   @RequestBody ConversationMessageCreateRequest request) {
        UUID senderId = parseUserId(jwt);
        ConversationMessageResponse response = conversationService.postMessage(
                parseUuid(conversationId, "conversationId"),
                senderId,
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{conversationId}/messages")
    @PreAuthorize("hasAnyRole('CLIENT', 'PROVIDER')")
    public ResponseEntity<ConversationMessagesResponse> listMessages(@AuthenticationPrincipal Jwt jwt,
                                                                     @PathVariable String conversationId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "20") int size) {
        UUID userId = parseUserId(jwt);
        ConversationMessagesResponse response = conversationService.listMessages(
                parseUuid(conversationId, "conversationId"),
                userId,
                page,
                size
        );
        return ResponseEntity.ok(response);
    }

    private UUID parseUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            throw new IllegalArgumentException("user id not found in token");
        }
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("user id must be a valid UUID");
        }
    }

    private UUID parseUuid(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(field + " must be a valid UUID");
        }
    }
}
