package com.xavier.servicematchbackend.identityaccess;

import com.xavier.servicematchbackend.identityaccess.domain.Email;
import com.xavier.servicematchbackend.identityaccess.domain.User;
import com.xavier.servicematchbackend.identityaccess.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UserId> {

    boolean existsByEmail(Email email);
}
