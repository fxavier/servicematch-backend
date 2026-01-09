package com.xavier.servicematchbackend.identityaccess;

import com.xavier.servicematchbackend.identityaccess.domain.DuplicateEmailException;
import com.xavier.servicematchbackend.identityaccess.domain.Email;
import com.xavier.servicematchbackend.identityaccess.domain.PasswordHash;
import com.xavier.servicematchbackend.identityaccess.domain.Role;
import com.xavier.servicematchbackend.identityaccess.domain.User;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;

    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(Email email, PasswordHash passwordHash, Set<Role> roles) {
        User user = User.register(email, passwordHash, roles);
        if (userRepository.existsByEmail(user.email())) {
            throw new DuplicateEmailException(user.email());
        }
        return userRepository.save(user);
    }
}
