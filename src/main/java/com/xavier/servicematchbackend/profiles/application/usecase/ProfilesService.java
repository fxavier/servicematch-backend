package com.xavier.servicematchbackend.profiles.application.usecase;

import com.xavier.servicematchbackend.profiles.application.dto.CustomerProfilePatch;
import com.xavier.servicematchbackend.profiles.application.dto.ProfilesPatchRequest;
import com.xavier.servicematchbackend.profiles.application.dto.ProfilesResponse;
import com.xavier.servicematchbackend.profiles.application.dto.ProviderProfilePatch;
import com.xavier.servicematchbackend.profiles.domain.entity.CustomerProfile;
import com.xavier.servicematchbackend.profiles.domain.entity.ProviderProfile;
import com.xavier.servicematchbackend.profiles.domain.valueobject.UserId;
import com.xavier.servicematchbackend.profiles.infra.persistence.CustomerProfileRepository;
import com.xavier.servicematchbackend.profiles.infra.persistence.ProviderProfileRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfilesService {

    private final CustomerProfileRepository customerProfileRepository;
    private final ProviderProfileRepository providerProfileRepository;

    public ProfilesService(CustomerProfileRepository customerProfileRepository,
                           ProviderProfileRepository providerProfileRepository) {
        this.customerProfileRepository = customerProfileRepository;
        this.providerProfileRepository = providerProfileRepository;
    }

    @Transactional(readOnly = true)
    public ProfilesResponse getProfiles(UserId userId) {
        CustomerProfile customerProfile = customerProfileRepository.findById(userId).orElse(null);
        ProviderProfile providerProfile = providerProfileRepository.findById(userId).orElse(null);
        return ProfilesResponse.from(customerProfile, providerProfile);
    }

    @Transactional
    public ProfilesResponse patchProfiles(UserId userId, ProfilesPatchRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }

        Instant now = Instant.now();
        CustomerProfilePatch customerPatch = request.customer();
        if (customerPatch != null) {
            CustomerProfile customerProfile = customerProfileRepository.findById(userId)
                    .orElseGet(() -> CustomerProfile.create(userId, now));
            customerProfile.update(customerPatch.displayName(), customerPatch.phone(), now);
            customerProfileRepository.save(customerProfile);
        }

        ProviderProfilePatch providerPatch = request.provider();
        if (providerPatch != null) {
            ProviderProfile providerProfile = providerProfileRepository.findById(userId)
                    .orElseGet(() -> ProviderProfile.create(userId, now));
            providerProfile.update(providerPatch.displayName(), providerPatch.bio(), now);
            providerProfileRepository.save(providerProfile);
        }

        return getProfiles(userId);
    }
}
