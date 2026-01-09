package com.xavier.servicematchbackend.identityaccess.application.usecase;

import com.xavier.servicematchbackend.identityaccess.application.dto.SessionToken;
import com.xavier.servicematchbackend.identityaccess.application.exception.InvalidRefreshTokenException;
import com.xavier.servicematchbackend.identityaccess.domain.entity.Session;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.UserId;
import com.xavier.servicematchbackend.identityaccess.infra.persistence.SessionRepository;
import com.xavier.servicematchbackend.identityaccess.infra.security.SessionProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {

    private static final int TOKEN_BYTES = 64;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final SessionRepository sessionRepository;
    private final SessionProperties sessionProperties;

    public SessionService(SessionRepository sessionRepository, SessionProperties sessionProperties) {
        this.sessionRepository = sessionRepository;
        this.sessionProperties = sessionProperties;
    }

    @Transactional
    public SessionToken create(UserId userId) {
        return createSession(userId, Instant.now());
    }

    @Transactional
    public SessionToken rotate(String refreshToken) {
        String refreshTokenHash = hashToken(refreshToken);
        Session session = sessionRepository.findByRefreshTokenHash(refreshTokenHash)
                .orElseThrow(InvalidRefreshTokenException::new);

        Instant now = Instant.now();
        if (!session.isActive(now)) {
            throw new InvalidRefreshTokenException();
        }

        SessionToken newToken = createSession(session.userId(), now);
        session.revoke(now, newToken.session().id());
        sessionRepository.save(session);
        return newToken;
    }

    @Transactional
    public void revoke(String refreshToken) {
        String refreshTokenHash = hashToken(refreshToken);
        Session session = sessionRepository.findByRefreshTokenHash(refreshTokenHash)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (session.isRevoked()) {
            return;
        }

        session.revoke(Instant.now(), null);
        sessionRepository.save(session);
    }

    private SessionToken createSession(UserId userId, Instant now) {
        long ttlSeconds = sessionProperties.getRefreshTtlSeconds();
        if (ttlSeconds <= 0) {
            throw new IllegalStateException("refreshTtlSeconds must be positive");
        }

        String refreshToken = generateToken();
        String refreshTokenHash = hashToken(refreshToken);
        Instant expiresAt = now.plusSeconds(ttlSeconds);

        Session session = Session.create(userId, refreshTokenHash, now, expiresAt);
        Session saved = sessionRepository.save(session);
        return new SessionToken(saved, refreshToken);
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("refreshToken must not be blank");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            return toHex(hashed);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }

    private String toHex(byte[] bytes) {
        char[] out = new char[bytes.length * 2];
        int i = 0;
        for (byte b : bytes) {
            int v = b & 0xFF;
            out[i++] = toHexChar(v >>> 4);
            out[i++] = toHexChar(v & 0x0F);
        }
        return new String(out);
    }

    private char toHexChar(int value) {
        return (char) (value < 10 ? '0' + value : 'a' + (value - 10));
    }
}
