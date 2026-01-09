package com.xavier.servicematchbackend.identityaccess.domain;

public record UserRegistered(UserId userId, Email email) {

    public UserRegistered {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (email == null) {
            throw new IllegalArgumentException("email must not be null");
        }
    }
}
