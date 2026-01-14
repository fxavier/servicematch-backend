package com.xavier.servicematchbackend.notifications.infra.persistence;

import com.xavier.servicematchbackend.notifications.domain.entity.NotificationLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {
}
