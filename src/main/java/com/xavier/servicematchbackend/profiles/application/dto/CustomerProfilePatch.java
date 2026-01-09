package com.xavier.servicematchbackend.profiles.application.dto;

/**
 * DTO for patching a customer profile.
 *
 * @param displayName the new display name of the customer
 * @param phone       the new phone number of the customer
 */
public record CustomerProfilePatch(String displayName, String phone) {
}
