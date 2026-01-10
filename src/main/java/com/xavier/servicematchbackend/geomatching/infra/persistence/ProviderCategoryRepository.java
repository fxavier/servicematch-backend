package com.xavier.servicematchbackend.geomatching.infra.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProviderCategoryRepository extends JpaRepository<ProviderCategory, ProviderCategoryId> {

    @Query("select pc.id.providerId from ProviderCategory pc where pc.id.categoryId = :categoryId")
    List<UUID> findProviderIdsByCategory(@Param("categoryId") UUID categoryId);
}
