package com.xavier.servicematchbackend.identityaccess.infra.persistence;

import com.xavier.servicematchbackend.identityaccess.domain.entity.User;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.Email;
import com.xavier.servicematchbackend.identityaccess.domain.valueobject.UserId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UserId> {

    boolean existsByEmail(Email email);

    Optional<User> findByEmail(Email email);
}
