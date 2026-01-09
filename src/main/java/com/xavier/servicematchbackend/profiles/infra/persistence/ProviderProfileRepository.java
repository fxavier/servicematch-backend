package com.xavier.servicematchbackend.profiles.infra.persistence;

import com.xavier.servicematchbackend.profiles.domain.entity.ProviderProfile;
import com.xavier.servicematchbackend.profiles.domain.valueobject.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, UserId> {
}
