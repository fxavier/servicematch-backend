package com.xavier.servicematchbackend.identityaccess.domain.event;

import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Email;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Role;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.UserId;
import java.util.Set;

public record UserRegistered(UserId userId, Email email, Set<Role> roles) {

    public UserRegistered {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (email == null) {
            throw new IllegalArgumentException("email must not be null");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("roles must not be empty");
        }
        if (roles.contains(null)) {
            throw new IllegalArgumentException("roles must not contain nulls");
        }
    }
}
