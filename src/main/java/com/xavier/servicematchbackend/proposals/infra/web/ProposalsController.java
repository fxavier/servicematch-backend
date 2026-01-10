package com.xavier.servicematchbackend.proposals.infra.web;

import com.xavier.servicematchbackend.proposals.application.dto.ProposalCreateRequest;
import com.xavier.servicematchbackend.proposals.application.dto.ProposalResponse;
import com.xavier.servicematchbackend.proposals.application.usecase.ProposalService;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/proposals")
public class ProposalsController {

    private final ProposalService proposalService;

    public ProposalsController(ProposalService proposalService) {
        this.proposalService = proposalService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ProposalResponse> submit(@AuthenticationPrincipal Jwt jwt,
                                                   @RequestBody ProposalCreateRequest request) {
        UUID providerId = parseUserId(jwt);
        ProposalResponse response = proposalService.submit(providerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{proposalId}/accept")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ProposalResponse> accept(@AuthenticationPrincipal Jwt jwt,
                                                   @PathVariable String proposalId) {
        UUID requesterId = parseUserId(jwt);
        ProposalResponse response = proposalService.accept(requesterId, parseUuid(proposalId));
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

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("proposalId must be a valid UUID");
        }
    }
}
