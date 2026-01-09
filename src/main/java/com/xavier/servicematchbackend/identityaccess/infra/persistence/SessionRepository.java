package com.xavier.servicematchbackend.identityaccess.infra.persistence;

import com.xavier.servicematchbackend.identityaccess.domain.entity.Session;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, UUID> {

    Optional<Session> findByRefreshTokenHash(String refreshTokenHash);
}
