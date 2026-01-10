package com.xavier.servicematchbackend.profiles.infra.web;

import com.xavier.servicematchbackend.profiles.application.dto.ProfilesPatchRequest;
import com.xavier.servicematchbackend.profiles.application.dto.ProfilesResponse;
import com.xavier.servicematchbackend.profiles.application.dto.ProviderZoneRequest;
import com.xavier.servicematchbackend.profiles.application.dto.ProviderZoneResponse;
import com.xavier.servicematchbackend.profiles.application.usecase.ProfilesService;
import com.xavier.servicematchbackend.profiles.domain.valueobject.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.UUID;

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

    @PostMapping("/me/zones")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    public ResponseEntity<ProviderZoneResponse> createZone(@AuthenticationPrincipal Jwt jwt,
                                                           @RequestBody ProviderZoneRequest request) {
        UserId userId = UserId.fromString(jwt.getSubject());
        return ResponseEntity.ok(profilesService.addProviderZone(userId, request));
    }

    @PatchMapping("/me/zones/{zoneId}")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    public ResponseEntity<ProviderZoneResponse> updateZone(@AuthenticationPrincipal Jwt jwt,
                                                           @PathVariable String zoneId,
                                                           @RequestBody ProviderZoneRequest request) {
        UserId userId = UserId.fromString(jwt.getSubject());
        return ResponseEntity.ok(
                profilesService.updateProviderZone(userId, UUID.fromString(zoneId), request)
        );
    }

    @DeleteMapping("/me/zones/{zoneId}")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    public ResponseEntity<Void> deleteZone(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable String zoneId) {
        UserId userId = UserId.fromString(jwt.getSubject());
        profilesService.removeProviderZone(userId, UUID.fromString(zoneId));
        return ResponseEntity.noContent().build();
    }
}
