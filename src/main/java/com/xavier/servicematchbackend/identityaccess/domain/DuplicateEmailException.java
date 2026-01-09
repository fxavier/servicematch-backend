package com.xavier.servicematchbackend.identityaccess.domain;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(Email email) {
        super("email already in use: " + (email == null ? "null" : email.value()));
    }
}
