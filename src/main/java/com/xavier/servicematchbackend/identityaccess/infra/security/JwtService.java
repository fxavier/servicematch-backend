package com.xavier.servicematchbackend.identityaccess.infra.security;

import com.xavier.servicematchbackend.identityaccess.domain.entity.User;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Role;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;

    public JwtService(JwtEncoder jwtEncoder, JwtProperties properties) {
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
    }

    public String issueToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.getTtlSeconds());
        String roles = user.roles().stream()
                .map(Role::name)
                .collect(Collectors.joining(","));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.getIssuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.id().value().toString())
                .claim("email", user.email().value())
                .claim("roles", roles)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
