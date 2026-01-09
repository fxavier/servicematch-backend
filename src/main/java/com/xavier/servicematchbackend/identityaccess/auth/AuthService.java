package com.xavier.servicematchbackend.identityaccess.auth;

import com.xavier.servicematchbackend.identityaccess.UserRegistrationService;
import com.xavier.servicematchbackend.identityaccess.UserRepository;
import com.xavier.servicematchbackend.identityaccess.auth.dto.AuthLoginRequest;
import com.xavier.servicematchbackend.identityaccess.auth.dto.AuthRegisterRequest;
import com.xavier.servicematchbackend.identityaccess.auth.dto.AuthResponse;
import com.xavier.servicematchbackend.identityaccess.domain.Email;
import com.xavier.servicematchbackend.identityaccess.domain.PasswordHash;
import com.xavier.servicematchbackend.identityaccess.domain.Role;
import com.xavier.servicematchbackend.identityaccess.domain.User;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRegistrationService userRegistrationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRegistrationService userRegistrationService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRegistrationService = userRegistrationService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(AuthRegisterRequest request) {
        Email email = Email.of(requireValue(request.email(), "email"));
        String rawPassword = requireValue(request.password(), "password");
        PasswordHash passwordHash = PasswordHash.of(passwordEncoder.encode(rawPassword));

        User user = userRegistrationService.register(email, passwordHash, Set.of(Role.USER));
        return new AuthResponse(jwtService.issueToken(user));
    }

    public AuthResponse login(AuthLoginRequest request) {
        Email email = Email.of(requireValue(request.email(), "email"));
        String rawPassword = requireValue(request.password(), "password");

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(rawPassword, user.passwordHash().value())) {
            throw new InvalidCredentialsException();
        }

        return new AuthResponse(jwtService.issueToken(user));
    }

    private String requireValue(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }
}
