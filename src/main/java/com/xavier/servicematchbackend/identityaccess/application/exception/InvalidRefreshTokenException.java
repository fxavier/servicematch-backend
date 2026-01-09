package com.xavier.servicematchbackend.identityaccess.application.exception;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException() {
        super("invalid refresh token");
    }
}
