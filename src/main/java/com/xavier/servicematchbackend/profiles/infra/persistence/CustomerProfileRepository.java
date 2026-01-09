package com.xavier.servicematchbackend.profiles.infra.persistence;

import com.xavier.servicematchbackend.profiles.domain.entity.CustomerProfile;
import com.xavier.servicematchbackend.profiles.domain.valueobject.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UserId> {
}
