package com.xavier.servicematchbackend.profiles.application.dto;

import com.xavier.servicematchbackend.profiles.domain.entity.CustomerProfile;
import com.xavier.servicematchbackend.profiles.domain.entity.ProviderProfile;

public record ProfilesResponse(CustomerProfileResponse customer,
                               ProviderProfileResponse provider) {

    public static ProfilesResponse from(CustomerProfile customerProfile, ProviderProfile providerProfile) {
        CustomerProfileResponse customer = null;
        ProviderProfileResponse provider = null;
        if (customerProfile != null) {
            customer = new CustomerProfileResponse(customerProfile.displayName(), customerProfile.phone());
        }
        if (providerProfile != null) {
            provider = new ProviderProfileResponse(
                    providerProfile.displayName(),
                    providerProfile.bio(),
                    providerProfile.reputation()
            );
        }
        return new ProfilesResponse(customer, provider);
    }
}
