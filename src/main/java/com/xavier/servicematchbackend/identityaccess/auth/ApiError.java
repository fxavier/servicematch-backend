package com.xavier.servicematchbackend.identityaccess.auth;

import java.time.Instant;

public record ApiError(String code, String message, Instant timestamp, String path) {
}
