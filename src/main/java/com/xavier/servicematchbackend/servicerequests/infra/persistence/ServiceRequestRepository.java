package com.xavier.servicematchbackend.servicerequests.infra.persistence;

import com.xavier.servicematchbackend.servicerequests.domain.entity.ServiceRequest;
import java.util.UUID;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sr from ServiceRequest sr where sr.id = :id")
    Optional<ServiceRequest> findByIdForUpdate(@Param("id") UUID id);
}
