package com.xavier.servicematchbackend.identityaccess.infra.web;

import com.xavier.servicematchbackend.identityaccess.application.dto.AuthLoginRequest;
import com.xavier.servicematchbackend.identityaccess.application.dto.AuthLogoutRequest;
import com.xavier.servicematchbackend.identityaccess.application.dto.AuthRefreshRequest;
import com.xavier.servicematchbackend.identityaccess.application.dto.AuthRegisterRequest;
import com.xavier.servicematchbackend.identityaccess.application.dto.AuthResponse;
import com.xavier.servicematchbackend.identityaccess.application.usecase.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody AuthRefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody AuthLogoutRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
