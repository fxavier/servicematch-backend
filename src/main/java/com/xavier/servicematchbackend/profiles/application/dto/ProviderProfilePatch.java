package com.xavier.servicematchbackend.profiles.application.dto;

/**
 * DTO for patching a provider profile.
 *
 * @param displayName the new display name of the provider
 * @param bio         the new bio of the provider
 */
public record ProviderProfilePatch(String displayName, String bio) {
}
