package com.xavier.servicematchbackend.profiles.application.dto;

public record ProfilesPatchRequest(CustomerProfilePatch customer,
                                   ProviderProfilePatch provider) {
}
