package com.xavier.servicematchbackend.servicerequests.infra.persistence;

import com.xavier.servicematchbackend.servicerequests.domain.entity.ServiceRequest;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, UUID> {
}
