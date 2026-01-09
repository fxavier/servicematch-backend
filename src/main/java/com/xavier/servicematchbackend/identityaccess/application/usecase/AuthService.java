package com.xavier.servicematchbackend.identityaccess.application.usecase;

import com.xavier.servicematchbackend.identityaccess.application.dto.AuthLoginRequest;
import com.xavier.servicematchbackend.identityaccess.application.dto.AuthRegisterRequest;
import com.xavier.servicematchbackend.identityaccess.application.dto.AuthResponse;
import com.xavier.servicematchbackend.identityaccess.application.dto.SessionToken;
import com.xavier.servicematchbackend.identityaccess.application.exception.InvalidCredentialsException;
import com.xavier.servicematchbackend.identityaccess.application.exception.InvalidRefreshTokenException;
import com.xavier.servicematchbackend.identityaccess.domain.entity.User;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Email;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.PasswordHash;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Role;
import com.xavier.servicematchbackend.identityaccess.infra.persistence.UserRepository;
import com.xavier.servicematchbackend.identityaccess.infra.security.JwtService;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRegistrationService userRegistrationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SessionService sessionService;

    public AuthService(UserRegistrationService userRegistrationService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       SessionService sessionService) {
        this.userRegistrationService = userRegistrationService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
    }

    public AuthResponse register(AuthRegisterRequest request) {
        Email email = Email.of(requireValue(request.email(), "email"));
        String rawPassword = requireValue(request.password(), "password");
        PasswordHash passwordHash = PasswordHash.of(passwordEncoder.encode(rawPassword));

        User user = userRegistrationService.register(email, passwordHash, Set.of(Role.CLIENT));
        SessionToken sessionToken = sessionService.create(user.id());
        return new AuthResponse(jwtService.issueToken(user), sessionToken.refreshToken());
    }

    public AuthResponse login(AuthLoginRequest request) {
        Email email = Email.of(requireValue(request.email(), "email"));
        String rawPassword = requireValue(request.password(), "password");

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(rawPassword, user.passwordHash().value())) {
            throw new InvalidCredentialsException();
        }

        SessionToken sessionToken = sessionService.create(user.id());
        return new AuthResponse(jwtService.issueToken(user), sessionToken.refreshToken());
    }

    public AuthResponse refresh(String refreshToken) {
        SessionToken sessionToken = sessionService.rotate(requireValue(refreshToken, "refreshToken"));
        User user = userRepository.findById(sessionToken.session().userId())
                .orElseThrow(InvalidRefreshTokenException::new);
        return new AuthResponse(jwtService.issueToken(user), sessionToken.refreshToken());
    }

    public void logout(String refreshToken) {
        sessionService.revoke(requireValue(refreshToken, "refreshToken"));
    }

    private String requireValue(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }
}
