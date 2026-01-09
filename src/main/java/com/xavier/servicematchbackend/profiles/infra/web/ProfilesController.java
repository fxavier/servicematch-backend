package com.xavier.servicematchbackend.profiles.infra.web;

import com.xavier.servicematchbackend.profiles.application.dto.ProfilesPatchRequest;
import com.xavier.servicematchbackend.profiles.application.dto.ProfilesResponse;
import com.xavier.servicematchbackend.profiles.application.usecase.ProfilesService;
import com.xavier.servicematchbackend.profiles.domain.valueobject.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
public class ProfilesController {

    private final ProfilesService profilesService;

    public ProfilesController(ProfilesService profilesService) {
        this.profilesService = profilesService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENT', 'PROVIDER', 'ADMIN')")
    public ResponseEntity<ProfilesResponse> me(@AuthenticationPrincipal Jwt jwt) {
        UserId userId = UserId.fromString(jwt.getSubject());
        return ResponseEntity.ok(profilesService.getProfiles(userId));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENT', 'PROVIDER', 'ADMIN')")
    public ResponseEntity<ProfilesResponse> patch(@AuthenticationPrincipal Jwt jwt,
                                                  @RequestBody ProfilesPatchRequest request) {
        UserId userId = UserId.fromString(jwt.getSubject());
        return ResponseEntity.ok(profilesService.patchProfiles(userId, request));
    }
}
