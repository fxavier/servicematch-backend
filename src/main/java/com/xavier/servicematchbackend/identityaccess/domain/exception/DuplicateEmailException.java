package com.xavier.servicematchbackend.identityaccess.domain.exception;

import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Email;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(Email email) {
        super("email already in use: " + (email == null ? "null" : email.value()));
    }
}
