package com.xavier.servicematchbackend.identityaccess.application.dto;

import com.xavier.servicematchbackend.identityaccess.domain.entity.Session;

public record SessionToken(Session session, String refreshToken) {

    public SessionToken {
        if (session == null) {
            throw new IllegalArgumentException("session must not be null");
        }
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("refreshToken must not be blank");
        }
    }
}
