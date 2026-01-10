package com.xavier.servicematchbackend.profiles.application.usecase;

import com.xavier.servicematchbackend.profiles.domain.entity.CustomerProfile;
import com.xavier.servicematchbackend.profiles.domain.entity.ProviderProfile;
import com.xavier.servicematchbackend.profiles.domain.valueobject.UserId;
import com.xavier.servicematchbackend.profiles.infra.persistence.CustomerProfileRepository;
import com.xavier.servicematchbackend.profiles.infra.persistence.ProviderProfileRepository;
import java.time.Instant;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfilesBootstrapService {

    private final CustomerProfileRepository customerProfileRepository;
    private final ProviderProfileRepository providerProfileRepository;

    public ProfilesBootstrapService(CustomerProfileRepository customerProfileRepository,
                                    ProviderProfileRepository providerProfileRepository) {
        this.customerProfileRepository = customerProfileRepository;
        this.providerProfileRepository = providerProfileRepository;
    }

    @Transactional
    public void bootstrap(UserId userId, Set<String> roles) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (roles == null || roles.isEmpty()) {
            return;
        }

        Instant now = Instant.now();
        if (roles.contains("CLIENT") && !customerProfileRepository.existsById(userId)) {
            customerProfileRepository.save(CustomerProfile.create(userId, now));
        }
        if (roles.contains("PROVIDER") && !providerProfileRepository.existsById(userId)) {
            providerProfileRepository.save(ProviderProfile.create(userId, now));
        }
    }
}
