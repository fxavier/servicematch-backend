package com.xavier.servicematchbackend.geomatching.infra.persistence;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderZoneRepository extends JpaRepository<ProviderZone, ProviderZoneId> {

    List<ProviderZone> findByIdProviderIdIn(Collection<UUID> providerIds);
}
