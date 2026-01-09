package com.xavier.servicematchbackend.identityaccess;

import com.xavier.servicematchbackend.identityaccess.domain.Email;
import com.xavier.servicematchbackend.identityaccess.domain.User;
import com.xavier.servicematchbackend.identityaccess.domain.UserId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UserId> {

    boolean existsByEmail(Email email);

    Optional<User> findByEmail(Email email);
}
