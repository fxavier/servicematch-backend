package com.xavier.servicematchbackend.identityaccess.domain.event;

import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Email;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.UserId;

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
